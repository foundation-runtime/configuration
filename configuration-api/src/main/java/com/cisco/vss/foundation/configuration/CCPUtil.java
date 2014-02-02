package com.cisco.vss.foundation.configuration;

import com.cisco.vss.foundation.configuration.xml.jaxb.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CCPUtil {

	public static boolean equals(ParameterValue valueA, ParameterValue valueB){
		// Compare primitive values
		if (valueA.getPrimitiveValues() != null && valueA.getPrimitiveValues().size() > 0){
			if (valueB.getPrimitiveValues() == null || valueB.getPrimitiveValues().size() == 0){
				return false;
			}
			
			if (valueA.getPrimitiveValues().size() != valueB.getPrimitiveValues().size()){
				return false;
			}
			
			// Sort according index
			List<PrimitiveValue> primitiveValuesA = sortPrimitiveValues(valueA.getPrimitiveValues());
			List<PrimitiveValue> primitiveValuesB = sortPrimitiveValues(valueB.getPrimitiveValues());
			for (int i=0; i < primitiveValuesA.size() ; i++){
				PrimitiveValue primitiveA = primitiveValuesA.get(i);
				PrimitiveValue primitiveB = primitiveValuesB.get(i);				
				if (!equals(primitiveA.getIndex(), primitiveB.getIndex()) || !equals(primitiveA.getValue(), primitiveB.getValue())){
					return false;
				}
			}
			return true;
		}
		
		// Compare structure values
		return equals(valueA.getStructureValues(), valueB.getStructureValues());
	}

	private static boolean equals(List<StructureValue> structureListA, List<StructureValue> structureListB) {
		if (null == structureListA && null == structureListB){
			return true;
		}
		
		if ((null == structureListA && null != structureListB) || (null != structureListA && null == structureListB)){
			return false;
		}

		if (structureListA.size() != structureListB.size()){
			return false;
		}
		
		// Sort according index
		List<StructureValue> structureValuesA = sort(structureListA);
		List<StructureValue> structureValuesB = sort(structureListB);
		for (int i=0; i < structureValuesA.size() ; i++){
			StructureValue structureA = structureValuesA.get(i);
			StructureValue structureB = structureValuesB.get(i);				
			if (!equals(structureA, structureB)){
				return false;
			}
		}
		return true;
	}

	private static boolean equals(StructureValue structureA, StructureValue structureB) {
		if (structureA.getIndex() != structureB.getIndex()){
			return false;
		}
		List<StructureMemberValue> structureMemeberValueListA = structureA.getStructureMemberValues();
		List<StructureMemberValue> structureMemeberValueListB = structureB.getStructureMemberValues();
		
		if (null == structureMemeberValueListA && null == structureMemeberValueListB){
			return true;
		}
		
		if ((null == structureMemeberValueListA && null != structureMemeberValueListB) || (null != structureMemeberValueListA && null == structureMemeberValueListB)){
			return false;
		}
		
		if (structureMemeberValueListA.size() != structureMemeberValueListB.size()){
			return false;
		}
		
		// TODO: Sort according structure member name and index
		List<StructureMemberValue> structureMemeberValuesA = sortStructureMembers(structureMemeberValueListA);
		List<StructureMemberValue> structureMemeberValuesB = sortStructureMembers(structureMemeberValueListB);
		for (int i=0; i < structureMemeberValuesA.size(); i++){
			StructureMemberValue structureMemberValueA = structureMemeberValuesA.get(i);
			StructureMemberValue structureMemberValueB = structureMemeberValuesB.get(i);
			
			if (!equals(structureMemberValueA.getStructureValues(), structureMemberValueB.getStructureValues()) || 
				!equals(structureMemberValueA.getValue(), structureMemberValueB.getValue()) ||
				!equals(structureMemberValueA.getIndex(), structureMemberValueB.getIndex())){
				return false;
			}
		}
		return true;
	}

	private static boolean equals(String strA, String strB){
		if (null == strA && null == strB){
			return true;
		}
		
		if ((null == strA && null != strB) || (null != strA && null == strB)){
			return false;
		}
		
		return strA.equals(strB);
	}
	

	private static List<PrimitiveValue> sortPrimitiveValues(List<PrimitiveValue> primitiveValues) {
		Collections.sort(primitiveValues, new Comparator<PrimitiveValue>() {

			@Override
			public int compare(PrimitiveValue o1, PrimitiveValue o2) {
				if (null == o1){
					return -1;
				}
				if (null == o2 || null == o1.getIndex()){
					return 1;
				}
				if (null == o2.getIndex()){
					return -1;
				}
				return o1.getIndex().compareTo(o2.getIndex());
			}
		});
		return primitiveValues;
	}
	
	
	private static List<StructureValue> sort(List<StructureValue> structureValues) {
		Collections.sort(structureValues, new Comparator<StructureValue>() {

			@Override
			public int compare(StructureValue o1, StructureValue o2) {
				if (null == o1){
					return -1;
				}
				if (null == o2 || null == o1.getIndex()){
					return 1;
				}
				if (null == o2.getIndex()){
					return -1;
				}
				return o1.getIndex().compareTo(o2.getIndex());
			}
		});
		return structureValues;
	}
		
	private static List<StructureMemberValue> sortStructureMembers(List<StructureMemberValue> structureMemeberValues) {
		Collections.sort(structureMemeberValues, new Comparator<StructureMemberValue>() {

			@Override
			public int compare(StructureMemberValue o1, StructureMemberValue o2) {
				if (null == o1){
					return -1;
				}
				if (null == o2 || null == o1.getName()){
					return 1;
				}
				if (null == o2.getName()){
					return -1;
				}
				int compareameResult = o1.getName().compareTo(o2.getName());
				if (compareameResult != 0){
					return compareameResult;
				}
				
				// Compare indexes
				if (null == o1.getIndex()){
					return 1;
				}
				if (null == o2.getIndex()){
					return -1;
				}
				return o1.getIndex().compareTo(o2.getIndex());
			}
		});
		return structureMemeberValues;
	}

	public static Parameter clone(Parameter parameter){
		Parameter clonedParameter = new Parameter();
		clonedParameter.setAdvanced(parameter.isAdvanced());
		clonedParameter.setBase(parameter.getBase());
		clonedParameter.setDefaultInstantiationLevelId(parameter.getDefaultInstantiationLevelId());
		clonedParameter.setDefaultValue(parameter.getDefaultValue()); // TODO: clone
		//clonedParameter.setDiscoveredBy(clone(parameter.getDiscoveredBy()));
		//clonedParameter.setEnabledBy(clone(parameter.getEnabledBy()));
		clonedParameter.setHidden(parameter.isHidden());
		clonedParameter.setInstantiationLevel(parameter.getInstantiationLevel());
		clonedParameter.setInstantiationLevelId(parameter.getInstantiationLevelId());
		clonedParameter.setInstantiationLevelName(parameter.getInstantiationLevelName());
		clonedParameter.setIsArray(parameter.isIsArray());
		clonedParameter.setName(parameter.getName());
		clonedParameter.setParamId(parameter.getParamId());
		//clonedParameter.setRange(clone(parameter.getRange()));
		clonedParameter.setReadOnly(parameter.isReadOnly());
		clonedParameter.setRequired(parameter.isRequired());
		clonedParameter.setRequiresRestart(parameter.isRequiresRestart());
		clonedParameter.setStructureDefinition(parameter.getStructureDefinition()); // TODO: clone
		clonedParameter.setType(parameter.getType());
		clonedParameter.setUnit(parameter.getUnit());
		//clonedParameter.setValue(clone(parameter.getValue()));
		return clonedParameter;
	}

}
