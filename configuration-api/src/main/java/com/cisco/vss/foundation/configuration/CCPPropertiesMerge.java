package com.cisco.vss.foundation.configuration;

import com.cisco.vss.foundation.configuration.xml.jaxb.*;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CCPPropertiesMerge {

	private final static Logger logger = LoggerFactory.getLogger(CCPPropertiesMerge.class);

	private static final String paramNameSeperator = ".";

	public static Map<NamespaceIdentifier, Map<String, Parameter>> mergerPropertiesToCCPObjects(NamespaceDefinitions namespaceDefinitions, Properties properties) {
		
		AbstractConfiguration.setDefaultListDelimiter('<');
		
		logger.trace("Merging properties object with CCP Namespace Parameters");

		Map<NamespaceIdentifier, Map<String, Parameter>> meregedConfigurationMap = new HashMap<NamespaceIdentifier, Map<String,Parameter>>();
		Configuration configProperties = new CompositeConfiguration();
		((CompositeConfiguration)configProperties).setDelimiterParsingDisabled(true);		
		
		
		for (Object property : properties.keySet()){
			String propertyValue = properties.getProperty(property.toString());
			if (StringUtils.isEmpty(propertyValue) ||
					(StringUtils.startsWith(propertyValue, "<") && StringUtils.endsWith(propertyValue, ">"))){
				logger.trace("Ignoring " + property + " parameter from properties object as it has no valid value: " + propertyValue);
				continue;
			}

			configProperties.addProperty(property.toString(), StringEscapeUtils.escapeXml(propertyValue));
			logger.trace("Adding " + property + " parameter from properties object with value " + propertyValue + " to configuration object");
		}

		for (NamespaceDefinition namespaceDefinition : namespaceDefinitions.getNamespaceDefinitions()){
			logger.trace("Looking for new parameters value for parameters from namspace: " + namespaceDefinition.getNamespaceIdentifier().getName() + " - " + namespaceDefinition.getNamespaceIdentifier().getVersion());

			Map<String, Parameter> mergedParameterValues = new HashMap<String, Parameter>();

			Map<String, Parameter> parameterMap = new HashMap<String, Parameter>();
			List<String> primitiveParameterList =  new ArrayList<String>();
			List<String> primitiveArrayParameterList =  new ArrayList<String>();
			Map<String, Parameter> structureDefinitionMap = new HashMap<String, Parameter>();
			Map<String, List<String>> structureMemberMap = new HashMap<String, List<String>>();
			generatePrimitiveParameterList(parameterMap, namespaceDefinition, primitiveParameterList, primitiveArrayParameterList, structureDefinitionMap, structureMemberMap);

			Set<String> structureNameSet = structureDefinitionMap.keySet();

			// Loop over all properties
			Iterator<String> propertyKeyIterator = configProperties.getKeys();
			while(propertyKeyIterator.hasNext()){
				String propertyKey = propertyKeyIterator.next();

				// Check the property is primitive
				if (primitiveParameterList.contains(propertyKey)){
					ParameterValue parameterValue = generatePrimitiveParameterValue(configProperties, propertyKey);
					Parameter parameter = parameterMap.get(propertyKey);
					parameter.setValue(parameterValue);
					mergedParameterValues.put(propertyKey, parameter);					
					continue;
				}

				// Check the property (without last word) is not primitive ARRAY
				if (primitiveArrayParameterList.contains(StringUtils.substringBeforeLast(propertyKey, paramNameSeperator))){
					String primitivePropertyName = StringUtils.substringBeforeLast(propertyKey, paramNameSeperator);
					if (mergedParameterValues.containsKey(primitivePropertyName)){
						continue;
					}
					ParameterValue parameterValue = generatePrimitiveArrayParameterValue(configProperties, primitivePropertyName);
					Parameter parameter = parameterMap.get(primitivePropertyName);
					parameter.setValue(parameterValue);
					mergedParameterValues.put(primitivePropertyName, parameter);					
					continue;
				}

				// Traverse over property parts until matching structureName
				String candidateStructName = propertyKey;

				while (StringUtils.isNotEmpty(candidateStructName) && !structureNameSet.contains(candidateStructName)){
					if (candidateStructName.contains(paramNameSeperator)){
						candidateStructName = StringUtils.substringBeforeLast(candidateStructName, paramNameSeperator);
					} 
					else {
						candidateStructName = StringUtils.EMPTY;
					}
				}

				if (StringUtils.isEmpty(candidateStructName)){
					continue;
				}

				// Validate the flat property is structure member
				Parameter structureParameter = structureDefinitionMap.get(candidateStructName);	
				/*List<String> structureMemeberList = structureMemberMap.get(candidateStructName);
				String candidateMemberName;
				 
				if (structureParameter.isIsArray()){
					String index = StringUtils.substringBetween(propertyKey, candidateStructName + paramNameSeperator, paramNameSeperator);
					candidateMemberName = StringUtils.substringAfter(propertyKey, index + paramNameSeperator);
				}
				else {
					candidateMemberName = StringUtils.substringAfter(propertyKey, candidateStructName + paramNameSeperator);
				}
				if (candidateMemberName.contains(paramNameSeperator)){
					candidateMemberName = StringUtils.substringBefore(candidateMemberName, paramNameSeperator);
				}
				
				// TODO - deal with ignoreName !!!
				if (!structureMemeberList.contains(candidateMemberName)){
					continue;
				}*/

				if (mergedParameterValues.containsKey(candidateStructName)){
					continue;
				}

				// Group structure properties parameters and work against structure definition
				ParameterValue parameterValue = generateStructureParameterValue(candidateStructName, structureParameter, configProperties);
				if (null == parameterValue){
					continue;
				}
				Parameter parameter = parameterMap.get(candidateStructName);
				parameter.setValue(parameterValue);
				mergedParameterValues.put(candidateStructName, parameter);				
			}

			meregedConfigurationMap.put(namespaceDefinition.getNamespaceIdentifier(), mergedParameterValues);
		}
		return meregedConfigurationMap;
	}

	private static List<String> generatePrimitiveParameterList(Map<String, Parameter> parameterMap, NamespaceDefinition namespaceDefinition, List<String> primitiveParameterList, List<String> primitiveArrayParameterList, Map<String, Parameter> structureDefinitionMap, Map<String, List<String>> structureMemberMap) {
		for (Parameter parameter : namespaceDefinition.getParameters()){
			parameterMap.put(parameter.getName(), CCPUtil.clone(parameter));
			if (null == parameter.getStructureDefinition()){
				if (!parameter.isIsArray()){
					primitiveParameterList.add(parameter.getName());
				}
				else {
					primitiveArrayParameterList.add(parameter.getName());
				}
			}
			else {
				structureDefinitionMap.put(parameter.getName(), CCPUtil.clone(parameter));
				List<String> structureMemeberList = new ArrayList<String>();
				for (StructureMemberDefinition structureMemberDefinition : parameter.getStructureDefinition().getStructureMemberDefinitions()){
					structureMemeberList.add(structureMemberDefinition.getName());
				}
				structureMemberMap.put(parameter.getName(), structureMemeberList);
			}
		}
		return primitiveParameterList;
	}

	private static ParameterValue generatePrimitiveParameterValue(Configuration configuration, String propertyName){
		PrimitiveValue primitiveValue = new PrimitiveValue();
		primitiveValue.setValue(getValueFromConfiguration(propertyName, configuration));

		List<PrimitiveValue> primitiveValueList = new ArrayList<PrimitiveValue>();
		primitiveValueList.add(primitiveValue);

		ParameterValue parameterValue = new ParameterValue();
		parameterValue.setPrimitiveValues(primitiveValueList);

		configuration.clearProperty(propertyName);
		return parameterValue;
	}

	private static ParameterValue generatePrimitiveArrayParameterValue(Configuration configuration, String propertyName){
		List<PrimitiveValue> primitiveValueList = new ArrayList<PrimitiveValue>();

		Iterator<String> arrayProperties = configuration.getKeys(propertyName);
		while (arrayProperties.hasNext()){
			String arrayElementProperty = arrayProperties.next();
			PrimitiveValue primitiveValue = new PrimitiveValue();
			primitiveValue.setValue(getValueFromConfiguration(arrayElementProperty, configuration));
			primitiveValue.setIndex(StringUtils.substringAfterLast(arrayElementProperty, paramNameSeperator));

			primitiveValueList.add(primitiveValue);						
			configuration.clearProperty(arrayElementProperty);
		}

		ParameterValue parameterValue = new ParameterValue();
		parameterValue.setPrimitiveValues(primitiveValueList);		
		return parameterValue;
	}

	private static ParameterValue generateStructureParameterValue(String structureName, Parameter structureParameter, Configuration configuration) {
		List<StructureValue> structureValueList = generateListOfStructureValue(structureName, structureParameter.getStructureDefinition(), structureParameter.isIsArray(), configuration);
		if (null == structureValueList || structureValueList.size() == 0){
			return null;
		}
		ParameterValue parameterValue = new ParameterValue();
		parameterValue.setStructureValues(structureValueList);
		return parameterValue;
	}

	private static List<StructureValue> generateListOfStructureValue (String structureName, StructureDefinition structureDefinition, boolean isArray, Configuration configuration) {
		String flatNamePrefix = structureName;
		Map<String, Configuration> structureIndexValueMap = new HashMap<String, Configuration>();
		if (isArray) {
			// Group by index
			Iterator<String> structureConfigurationIter = configuration.getKeys(flatNamePrefix);
			while (structureConfigurationIter.hasNext()){
				String structureConfiguration  = structureConfigurationIter.next();
				String index = StringUtils.substringBetween(structureConfiguration, flatNamePrefix + paramNameSeperator, paramNameSeperator);
				if (StringUtils.isEmpty(index)){
					continue;
				}
				if (!structureIndexValueMap.containsKey(index)){
					structureIndexValueMap.put(index, new CompositeConfiguration());
				}
				structureIndexValueMap.get(index).addProperty(structureConfiguration, getValueFromConfiguration(structureConfiguration, configuration));
			}	
		}
		else {
			structureIndexValueMap.put(null, new CompositeConfiguration());
			Iterator<String> structureConfigurationIter = configuration.getKeys(flatNamePrefix);
			while (structureConfigurationIter.hasNext()){
				String structureConfiguration  = structureConfigurationIter.next();
				structureIndexValueMap.get(null).addProperty(structureConfiguration, getValueFromConfiguration(structureConfiguration, configuration));
			}
		}

		// for each group create structureValue
		List<StructureValue> structureValueList = new ArrayList<StructureValue>();

		for (String structureIndex : structureIndexValueMap.keySet()){
			String index = null;
			if (StringUtils.isNotEmpty(structureIndex)){

				index = structureIndex;
			}
			StructureValue structureValue = generateStructureValue(structureName, structureDefinition, index, configuration);
			if (null != structureValue){
				structureValueList.add(structureValue);
			}
		}

		return structureValueList;
	}

	private static List<StructureValue> generateListOfStructureValueFromDefault(StructureMemberDefinition structureMemberDefinition) {
		// No default value
		if (null == structureMemberDefinition.getDefaultValue()
				|| null == structureMemberDefinition.getDefaultValue().getStructureValues() 
				|| structureMemberDefinition.getDefaultValue().getStructureValues().size() <= 0){
			return null;
		}		
		return structureMemberDefinition.getDefaultValue().getStructureValues();
	}
	
	private static StructureValue generateStructureValue(String structureName, StructureDefinition structureDefinition, String index, Configuration configuration) {
		List<StructureMemberValue> structureMemeberValueList = new ArrayList<StructureMemberValue>();
		String flatConfigPrefix = structureName + paramNameSeperator;
		if (null != index) {
			flatConfigPrefix += index + paramNameSeperator;
		}

		for (StructureMemberDefinition structureMemberDefinition : structureDefinition.getStructureMemberDefinitions()){
			StructureMemberValue structureMemberValue = new StructureMemberValue();
			structureMemberValue.setName(structureMemberDefinition.getName());

			/***
			 * Calculate the property name on the configuration object;
			 * The property name shouldn't include the structure member name, in case of 'ignoreName=true'
			 * The property name can't end with dot '.'
			 */
			String flatConfig =  flatConfigPrefix;
			if (!structureMemberDefinition.isIgnoreName()){
				flatConfig  += structureMemberDefinition.getName();
			}	
			else if (flatConfigPrefix.endsWith(".")){
				flatConfig = flatConfigPrefix.substring(0, flatConfigPrefix.length() - 1);
			}

			// Nested Structure Member
			if (null != structureMemberDefinition.getStructureDefinition()){
				List<StructureValue> nestedStructureValue = generateListOfStructureValue(flatConfig, structureMemberDefinition.getStructureDefinition(), structureMemberDefinition.isIsArray(), configuration);
				// Look for Default Value
				if (structureMemberDefinition.isRequired() && (null == nestedStructureValue || nestedStructureValue.size() == 0)){
					nestedStructureValue = generateListOfStructureValueFromDefault(structureMemberDefinition);
				}
				if (structureMemberDefinition.isRequired() && (null == nestedStructureValue || nestedStructureValue.size() == 0)){
					logger.warn("Neither config.proeprties file nor ccpConfig.xml file contains value for: " + flatConfig);
					continue;
				}
				structureMemberValue.setStructureValues(nestedStructureValue);
				structureMemeberValueList.add(structureMemberValue);
			}

			// Primitive array member
			else if (structureMemberDefinition.isIsArray()){
				List<StructureMemberValue> structureValueList = generateListOfPrimitiveStructureMemeberValue(flatConfig, structureMemberDefinition.getName(), configuration);
				// Look for default value
				if (structureMemberDefinition.isRequired() && (null == structureValueList || structureValueList.size() == 0)){
					structureValueList = generateListOfPrimitiveStructureMemeberValueFromDefaultValue(structureMemberDefinition);
				}
				if (structureMemberDefinition.isRequired() && (null == structureValueList || structureValueList.size() == 0)){
					logger.warn("Neither config.proeprties file nor ccpConfig.xml file contains value for: " + flatConfig);
					continue;
				}
				structureMemeberValueList.addAll(structureValueList);
			}
			// Primitive Member
			else {				
				String memberValue = getValueFromConfiguration(flatConfig, configuration);
				// Get default value if exists and if there is no override value
				if(StringUtils.isEmpty(memberValue)){
					if (null != structureMemberDefinition.getDefaultValue() 
							&& null != structureMemberDefinition.getDefaultValue().getPrimitiveValues()
							&& structureMemberDefinition.getDefaultValue().getPrimitiveValues().size() == 1){
						memberValue = structureMemberDefinition.getDefaultValue().getPrimitiveValues().get(0).getValue();
					}
				}

				if(StringUtils.isEmpty(memberValue)){
					if (structureMemberDefinition.isRequired()){
						logger.warn("Neither config.proeprties file nor ccpConfig.xml file contains value for: " + flatConfig);
					}
					continue;
				}
				structureMemberValue.setValue(memberValue);
				structureMemeberValueList.add(structureMemberValue);
				configuration.clearProperty(flatConfig);
			}		
		}

		StructureValue structureValue = new StructureValue();
		structureValue.setIndex(index);		
		structureValue.setStructureMemberValues(structureMemeberValueList);
		return structureValue;
	}

	private static List<StructureMemberValue> generateListOfPrimitiveStructureMemeberValue(String flatConfigPrefix, String memberName, Configuration configuration){
		List<StructureMemberValue> structureMemeberValueList = new ArrayList<StructureMemberValue>();
		Iterator<String> structureMemeberArrayKeys = configuration.getKeys(flatConfigPrefix);
		while (structureMemeberArrayKeys.hasNext()){
			String arrayKey = structureMemeberArrayKeys.next();
			String index = StringUtils.substringAfter(arrayKey, flatConfigPrefix + paramNameSeperator);

			StructureMemberValue structureMemberValue = new StructureMemberValue();
			structureMemberValue.setIndex(index);
			structureMemberValue.setName(memberName);
			structureMemberValue.setValue(getValueFromConfiguration(flatConfigPrefix + paramNameSeperator + index, configuration));

			structureMemeberValueList.add(structureMemberValue);

			configuration.clearProperty(flatConfigPrefix + paramNameSeperator + index);
		}
		return structureMemeberValueList;
	}

	private static List<StructureMemberValue> generateListOfPrimitiveStructureMemeberValueFromDefaultValue(StructureMemberDefinition structureMemberDefinition){

		// No Default Value
		if (null == structureMemberDefinition.getDefaultValue()
				|| null == structureMemberDefinition.getDefaultValue().getPrimitiveValues()
				|| structureMemberDefinition.getDefaultValue().getPrimitiveValues().size() <= 0){
			return null;
		}
		
		List<StructureMemberValue> structureMemeberValueList = new ArrayList<StructureMemberValue>();
		for (PrimitiveValue primitiveValue : structureMemberDefinition.getDefaultValue().getPrimitiveValues()){
			StructureMemberValue structureMemberValue = new StructureMemberValue();
			structureMemberValue.setIndex(primitiveValue.getIndex());
			structureMemberValue.setName(structureMemberDefinition.getName());
			structureMemberValue.setValue(primitiveValue.getValue());

			structureMemeberValueList.add(structureMemberValue);

		}
		return structureMemeberValueList;
	}

	private static String getValueFromConfiguration(String popetyName, Configuration config){
		String value = config.getString(popetyName);
		return value;
			
	}
}
