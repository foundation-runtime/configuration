package com.cisco.vss.foundation.configuration.xml.jaxb.adapters;

import javax.xml.bind.DatatypeConverter;

public class DataConverter {
	
	public static Integer parseInteger(String value) {
		return DatatypeConverter.parseInt(value);
	}

	public static String printInteger(Integer value) {
		if (null != value){
			return DatatypeConverter.printInt(value);
		}
		return null;
	}
}
