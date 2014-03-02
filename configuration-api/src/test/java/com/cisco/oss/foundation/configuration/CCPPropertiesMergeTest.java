/*
 * Copyright 2014 Cisco Systems, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.cisco.oss.foundation.configuration;

import com.cisco.oss.foundation.configuration.xml.XmlException;
import com.cisco.oss.foundation.configuration.xml.XmlParser;
import com.cisco.oss.foundation.configuration.xml.jaxb.*;
import junit.framework.Assert;
import org.junit.Test;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

public class CCPPropertiesMergeTest {

	@Test
	//@Ignore
	public void testSimpleParameter() throws Exception{
		Properties configProperties = loadconfigProperties("./src/test/resources/simpleParameterConfig.properties");
		NamespaceDefinitions namespaceDefinitions = loadCCPConfig("./src/test/resources/ccpConfig.xml");

		Map<NamespaceIdentifier, Map<String, Parameter>> testResult = CCPPropertiesMerge.mergerPropertiesToCCPObjects(namespaceDefinitions, configProperties);

		Assert.assertEquals(6, testResult.size());
		for (NamespaceIdentifier namespaceIdentifier : testResult.keySet()){
			if ("carm".equals(namespaceIdentifier.getName())) {
				Map<String, Parameter> newValueMap = testResult.get(namespaceIdentifier);
				Assert.assertEquals(1, newValueMap.size());
				Assert.assertEquals("30,60", newValueMap.get("database.carm.tidyupProducts").getValue().getPrimitiveValues().get(0).getValue());
			}
			else if ("cabConfiguration".equals(namespaceIdentifier.getName())){
				Map<String, Parameter> newValueMap = testResult.get(namespaceIdentifier);
				Assert.assertEquals(1, newValueMap.size());
				Assert.assertEquals("true", newValueMap.get("dynamicConfigReload.enabled").getValue().getPrimitiveValues().get(0).getValue());
			}
			else {
				Map<String, Parameter> newValueMap = testResult.get(namespaceIdentifier);
				Assert.assertEquals(0, newValueMap.size());
			}
		}
		System.out.println("end test");
	}

	@Test
	//@Ignore
	public void testSimpleArrayParameter() throws Exception{
		Properties configProperties = loadconfigProperties("./src/test/resources/simpleArrayParameterConfig.properties");
		NamespaceDefinitions namespaceDefinitions = loadCCPConfig("./src/test/resources/ccpConfig.xml");

		Map<NamespaceIdentifier, Map<String, Parameter>> testResult = CCPPropertiesMerge.mergerPropertiesToCCPObjects(namespaceDefinitions, configProperties);

		Assert.assertEquals(6, testResult.size());
		for (NamespaceIdentifier namespaceIdentifier : testResult.keySet()){
			if ("carm".equals(namespaceIdentifier.getName())) {
				Map<String, Parameter> newValueMap = testResult.get(namespaceIdentifier);
				Assert.assertEquals(1, newValueMap.size());
				ParameterValue parameterValue = newValueMap.get("emmgSync.caProductType").getValue();
				List<PrimitiveValue> primitiveValueList = parameterValue.getPrimitiveValues();
				Assert.assertEquals(3, primitiveValueList.size());
				for (PrimitiveValue primitiveValue : primitiveValueList){
					if ("3".equals(primitiveValue.getIndex())) {
						Assert.assertEquals("12", primitiveValue.getValue());
					}
					else if ("4".equals(primitiveValue.getIndex())) {
						Assert.assertEquals("90", primitiveValue.getValue());
					}
					else if ("5".equals(primitiveValue.getIndex())) {
						Assert.assertEquals("87,30", primitiveValue.getValue());
					}
					else {
						Assert.fail("Index: " + primitiveValue.getIndex() + " is not expected");
					}
				}
			}
			else {
				Map<String, Parameter> newValueMap = testResult.get(namespaceIdentifier);
				Assert.assertEquals(0, newValueMap.size());
			}
		}
	}

	@Test
	//@Ignore
	public void testSimpleStructure() throws Exception {
		Properties configProperties = loadconfigProperties("./src/test/resources/simpleStructureConfig.properties");
		NamespaceDefinitions namespaceDefinitions = loadCCPConfig("./src/test/resources/ccpConfig.xml");

		Map<NamespaceIdentifier, Map<String, Parameter>> testResult = CCPPropertiesMerge.mergerPropertiesToCCPObjects(namespaceDefinitions, configProperties);

		Assert.assertEquals(6, testResult.size());
		for (NamespaceIdentifier namespaceIdentifier : testResult.keySet()){
			if ("carm".equals(namespaceIdentifier.getName())) {
				Map<String, Parameter> newValueMap = testResult.get(namespaceIdentifier);
				Assert.assertEquals(1, newValueMap.size());
				ParameterValue parameterValue = newValueMap.get("service.acg").getValue();
				List<StructureValue> structureValueList = parameterValue.getStructureValues();
				Assert.assertEquals(1, structureValueList.size());
				for (StructureValue structureValue : structureValueList){
					for (StructureMemberValue structureMemberValue : structureValue.getStructureMemberValues())
						if ("host".equals(structureMemberValue.getName())) {
							Assert.assertEquals("myHost", structureMemberValue.getValue());
							Assert.assertEquals(null, structureMemberValue.getIndex());
						}
						else {
							Assert.assertEquals("port", structureMemberValue.getName());
							Assert.assertEquals("330", structureMemberValue.getValue());
							Assert.assertEquals(null, structureMemberValue.getIndex());
						}
				}
			}
			else {
				Map<String, Parameter> newValueMap = testResult.get(namespaceIdentifier);
				Assert.assertEquals(0, newValueMap.size());
			}
		}
	}

	@Test
	public void testArrayStructure() throws Exception {
		Properties configProperties = loadconfigProperties("./src/test/resources/simpleStructureArrayConfig.properties");
		NamespaceDefinitions namespaceDefinitions = loadCCPConfig("./src/test/resources/ccpConfig.xml");

		Map<NamespaceIdentifier, Map<String, Parameter>> testResult = CCPPropertiesMerge.mergerPropertiesToCCPObjects(namespaceDefinitions, configProperties);

		Assert.assertEquals(6, testResult.size());
		for (NamespaceIdentifier namespaceIdentifier : testResult.keySet()){
			if ("carm".equals(namespaceIdentifier.getName())) {
				Map<String, Parameter> newValueMap = testResult.get(namespaceIdentifier);
				Assert.assertEquals(1, newValueMap.size());
				ParameterValue parameterValue = newValueMap.get("service.carm").getValue();
				List<StructureValue> structureValueList = parameterValue.getStructureValues();
				Assert.assertEquals(2, structureValueList.size());
				for (StructureValue structureValue : structureValueList){
					if ("22".equals(structureValue.getIndex())){
						for (StructureMemberValue structureMemberValue : structureValue.getStructureMemberValues()){
							if ("host".equals(structureMemberValue.getName())) {
								Assert.assertEquals("myHost", structureMemberValue.getValue());
								Assert.assertEquals(null, structureMemberValue.getIndex());
							}
							else {
								Assert.assertEquals("port", structureMemberValue.getName());
								Assert.assertEquals("770", structureMemberValue.getValue());
								Assert.assertEquals(null, structureMemberValue.getIndex());
							}		
						}
					}
					else { // index = 23
						Assert.assertEquals("23", structureValue.getIndex());
						Assert.assertEquals(1, structureValue.getStructureMemberValues().size());
						for (StructureMemberValue structureMemberValue : structureValue.getStructureMemberValues()){
							if ("host".equals(structureMemberValue.getName())) {
								Assert.assertEquals("myHost1", structureMemberValue.getValue());
								Assert.assertEquals(null, structureMemberValue.getIndex());
							}
							/*else {
								Assert.assertEquals("port", structureMemberValue.getName());
								Assert.assertEquals("7701", structureMemberValue.getValue());
								Assert.assertEquals(null, structureMemberValue.getIndex());
							}	*/	
							else {
								Assert.fail("Structure member: " + structureMemberValue.getName() + " not expected");
							}
						}
					}
				}
					
			}
			else {
				Map<String, Parameter> newValueMap = testResult.get(namespaceIdentifier);
				Assert.assertEquals(0, newValueMap.size());
			}
		}
	}

	@Test
	public void testArrayStructureWithArryMemeber() throws Exception{
		Properties configProperties = loadconfigProperties("./src/test/resources/simpleStructureArrayWithArrayMemeberConfig.properties");
		NamespaceDefinitions namespaceDefinitions = loadCCPConfig("./src/test/resources/ccpConfig.xml");

		Map<NamespaceIdentifier, Map<String, Parameter>> testResult = CCPPropertiesMerge.mergerPropertiesToCCPObjects(namespaceDefinitions, configProperties);

		Assert.assertEquals(6, testResult.size());
		for (NamespaceIdentifier namespaceIdentifier : testResult.keySet()){
			if ("cabCommunication".equals(namespaceIdentifier.getName())) {
				Map<String, Parameter> newValueMap = testResult.get(namespaceIdentifier);
				Assert.assertEquals(1, newValueMap.size());
				ParameterValue parameterValue = newValueMap.get("service.http").getValue();
				List<StructureValue> structureValueList = parameterValue.getStructureValues();
				Assert.assertEquals(2, structureValueList.size());
				for (StructureValue structureValue : structureValueList){
					if ("22".equals(structureValue.getIndex())){
						for (StructureMemberValue structureMemberValue : structureValue.getStructureMemberValues()){
							if ("host".equals(structureMemberValue.getName())) {
								Assert.assertEquals("myHost", structureMemberValue.getValue());
								Assert.assertEquals(null, structureMemberValue.getIndex());
							}
							else {
								Assert.assertEquals("port", structureMemberValue.getName());
								if ("1".equals(structureMemberValue.getIndex())){
									Assert.assertEquals("2222", structureMemberValue.getValue());
								}
								else {
									Assert.assertEquals("2", structureMemberValue.getIndex());
									Assert.assertEquals("3333", structureMemberValue.getValue());									
								}
/*								Assert.assertEquals("9", structureMemberValue.getIndex());
								Assert.assertEquals("770", structureMemberValue.getValue());*/
								
							}		
						}
					}
					else { // index = 23
						Assert.assertEquals("23", structureValue.getIndex());
						for (StructureMemberValue structureMemberValue : structureValue.getStructureMemberValues()){
							if ("host".equals(structureMemberValue.getName())) {
								Assert.assertEquals("myHost1", structureMemberValue.getValue());
								Assert.assertEquals(null, structureMemberValue.getIndex());
							}
							else {
								Assert.assertEquals("port", structureMemberValue.getName());
								if ("5".equals(structureMemberValue.getIndex())){
									Assert.assertEquals("7701", structureMemberValue.getValue());
								}
								else {
									Assert.assertEquals("6", structureMemberValue.getIndex());
									Assert.assertEquals("8701", structureMemberValue.getValue());
								}
							}		
						}
					}
				}
					
			}
			else {
				Map<String, Parameter> newValueMap = testResult.get(namespaceIdentifier);
				Assert.assertEquals(0, newValueMap.size());
			}
		}
	}
	
	@Test
	public void testNestedSimpleStructure() throws Exception{
		Properties configProperties = loadconfigProperties("./src/test/resources/nestedSimpleStructureConfig.properties");
		NamespaceDefinitions namespaceDefinitions = loadCCPConfig("./src/test/resources/ccpConfig.xml");

		Map<NamespaceIdentifier, Map<String, Parameter>> testResult = CCPPropertiesMerge.mergerPropertiesToCCPObjects(namespaceDefinitions, configProperties);

		Assert.assertEquals(6, testResult.size());
		for (NamespaceIdentifier namespaceIdentifier : testResult.keySet()){
			if ("carm".equals(namespaceIdentifier.getName())) {
				Map<String, Parameter> newValueMap = testResult.get(namespaceIdentifier);
				Assert.assertEquals(1, newValueMap.size());
				ParameterValue parameterValue = newValueMap.get("service.bsm").getValue();
				List<StructureValue> structureValueList = parameterValue.getStructureValues();
				Assert.assertEquals(1, structureValueList.size());
				for (StructureValue structureValue : structureValueList){
					for (StructureMemberValue structureMemberValue : structureValue.getStructureMemberValues())
						if ("host".equals(structureMemberValue.getName())) {
							Assert.assertEquals("myHost", structureMemberValue.getValue());
							Assert.assertEquals(null, structureMemberValue.getIndex());
						}
						else if ("port".equals(structureMemberValue.getName())) {
							Assert.assertEquals("port", structureMemberValue.getName());
							Assert.assertEquals("770", structureMemberValue.getValue());
							Assert.assertEquals(null, structureMemberValue.getIndex());
						}				
						else {
							Assert.assertEquals("nested", structureMemberValue.getName());
							List<StructureValue> nestedStructureValue = structureMemberValue.getStructureValues();
							Assert.assertEquals(1, nestedStructureValue.size());
							Assert.assertEquals(2, nestedStructureValue.get(0).getStructureMemberValues().size());
							for (StructureMemberValue nestedMemberValue : nestedStructureValue.get(0).getStructureMemberValues()){
								if ("host".equals(nestedMemberValue.getName())) {
									Assert.assertEquals("vgc1a", nestedMemberValue.getValue());
									Assert.assertEquals(null, nestedMemberValue.getIndex());
								}
								else if ("port".equals(nestedMemberValue.getName())) {
									Assert.assertEquals("port", nestedMemberValue.getName());
									Assert.assertEquals("7701", nestedMemberValue.getValue());
									Assert.assertEquals(null, nestedMemberValue.getIndex());
								}	
								else {
									Assert.fail("Not expected structure member name: " + nestedMemberValue.getName());
								}
							}
						}
				}
			}
			else {
				Map<String, Parameter> newValueMap = testResult.get(namespaceIdentifier);
				Assert.assertEquals(0, newValueMap.size());
			}
		}
	}

	@Test
	public void testNestedArrayStructure() throws Exception{
		Properties configProperties = loadconfigProperties("./src/test/resources/nestedStructureArrayConfig.properties");
		NamespaceDefinitions namespaceDefinitions = loadCCPConfig("./src/test/resources/ccpConfig.xml");

		Map<NamespaceIdentifier, Map<String, Parameter>> testResult = CCPPropertiesMerge.mergerPropertiesToCCPObjects(namespaceDefinitions, configProperties);

		Assert.assertEquals(6, testResult.size());
		for (NamespaceIdentifier namespaceIdentifier : testResult.keySet()){
			if ("carm".equals(namespaceIdentifier.getName())) {
				Map<String, Parameter> newValueMap = testResult.get(namespaceIdentifier);
				Assert.assertEquals(1, newValueMap.size());
				ParameterValue parameterValue = newValueMap.get("service.bb").getValue();
				List<StructureValue> structureValueList = parameterValue.getStructureValues();
				Assert.assertEquals(1, structureValueList.size());
				for (StructureValue structureValue : structureValueList){
					Assert.assertEquals("1", structureValue.getIndex());
					for (StructureMemberValue structureMemberValue : structureValue.getStructureMemberValues())
						if ("host".equals(structureMemberValue.getName())) {
							Assert.assertEquals("myHost", structureMemberValue.getValue());
							Assert.assertEquals(null, structureMemberValue.getIndex());
						}
						else if ("port".equals(structureMemberValue.getName())) {
							Assert.assertEquals("port", structureMemberValue.getName());
							Assert.assertEquals("770", structureMemberValue.getValue());
							Assert.assertEquals(null, structureMemberValue.getIndex());
						}				
						else {
							Assert.assertEquals("nested", structureMemberValue.getName());
							Assert.assertEquals(null, structureMemberValue.getIndex());
							List<StructureValue> nestedStructureValue = structureMemberValue.getStructureValues();
							Assert.assertEquals("2", nestedStructureValue.get(0).getIndex());
							Assert.assertEquals(1, nestedStructureValue.size());
							for (StructureMemberValue nestedMemberValue : nestedStructureValue.get(0).getStructureMemberValues()){
								if ("host".equals(nestedMemberValue.getName())) {
									Assert.assertEquals("bbHost", nestedMemberValue.getValue());
									Assert.assertEquals(null, nestedMemberValue.getIndex());
								}
								else if ("port".equals(nestedMemberValue.getName())) {
									Assert.assertEquals("port", nestedMemberValue.getName());
									Assert.assertEquals("222", nestedMemberValue.getValue());
									Assert.assertEquals(null, nestedMemberValue.getIndex());
								}				
							}
						}
				}
			}
			else {
				Map<String, Parameter> newValueMap = testResult.get(namespaceIdentifier);
				Assert.assertEquals(0, newValueMap.size());
			}
		}
	}
	
	@Test
	public void testNestedArrayStructureWithIgnoreName() throws Exception{
		Properties configProperties = loadconfigProperties("./src/test/resources/nestedStructureArrayWithIgnoreNameConfig.properties");
		NamespaceDefinitions namespaceDefinitions = loadCCPConfig("./src/test/resources/ccpConfig.xml");

		Map<NamespaceIdentifier, Map<String, Parameter>> testResult = CCPPropertiesMerge.mergerPropertiesToCCPObjects(namespaceDefinitions, configProperties);

		Assert.assertEquals(6, testResult.size());
		for (NamespaceIdentifier namespaceIdentifier : testResult.keySet()){
			if ("carm".equals(namespaceIdentifier.getName())) {
				Map<String, Parameter> newValueMap = testResult.get(namespaceIdentifier);
				Assert.assertEquals(1, newValueMap.size());
				ParameterValue parameterValue = newValueMap.get("service.ignoreName").getValue();
				List<StructureValue> structureValueList = parameterValue.getStructureValues();
				Assert.assertEquals(1, structureValueList.size());
				for (StructureValue structureValue : structureValueList){
					Assert.assertEquals("1", structureValue.getIndex());
					for (StructureMemberValue structureMemberValue : structureValue.getStructureMemberValues())
						if ("host1".equals(structureMemberValue.getName())) {
							Assert.assertEquals("myHost", structureMemberValue.getValue());
							Assert.assertEquals(null, structureMemberValue.getIndex());
						}
						else if ("port1".equals(structureMemberValue.getName())) {
							Assert.assertEquals("port1", structureMemberValue.getName());
							Assert.assertEquals("770", structureMemberValue.getValue());
							Assert.assertEquals(null, structureMemberValue.getIndex());
						}				
						else {
							Assert.assertEquals("nested", structureMemberValue.getName());
							Assert.assertEquals(null, structureMemberValue.getIndex());
							List<StructureValue> nestedStructureValue = structureMemberValue.getStructureValues();
							Assert.assertEquals("2", nestedStructureValue.get(0).getIndex());
							Assert.assertEquals(1, nestedStructureValue.size());
							for (StructureMemberValue nestedMemberValue : nestedStructureValue.get(0).getStructureMemberValues()){
								if ("host2".equals(nestedMemberValue.getName())) {
									Assert.assertEquals("bbHost", nestedMemberValue.getValue());
									Assert.assertEquals(null, nestedMemberValue.getIndex());
								}
								else if ("port2".equals(nestedMemberValue.getName())) {
									Assert.assertEquals("port2", nestedMemberValue.getName());
									Assert.assertEquals("222", nestedMemberValue.getValue());
									Assert.assertEquals(null, nestedMemberValue.getIndex());
								}				
							}
						}
				}
			}
			else {
				Map<String, Parameter> newValueMap = testResult.get(namespaceIdentifier);
				Assert.assertEquals(0, newValueMap.size());
			}
		}
	}

	private Properties loadconfigProperties(String propertiesFileName) throws FileNotFoundException, IOException{
		Properties properties = new Properties();
		properties.load(new FileInputStream(propertiesFileName));
		return properties;
	}

	private NamespaceDefinitions loadCCPConfig(String ccpConfigFileName) throws FileNotFoundException, XmlException{
		InputStream is = new FileInputStream(new File(ccpConfigFileName));
		String xml = new Scanner(is).useDelimiter("\\Z").next();

		XmlParser xmlParser = new XmlParser();
		NamespaceDefinitions namespaceDefinitions = (NamespaceDefinitions) xmlParser.unmarshall(xml);
		return namespaceDefinitions;
	}
}
