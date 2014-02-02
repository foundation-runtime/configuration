package com.cisco.vss.foundation.configuration.xml;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

public class JaxbValidator {

	public void setValidating(Unmarshaller unmarshaller) throws JAXBException, SAXException {
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = sf.newSchema(this.getClass().getResource("/CCP_XML.xsd"));
		unmarshaller.setSchema(schema);
	}
}
