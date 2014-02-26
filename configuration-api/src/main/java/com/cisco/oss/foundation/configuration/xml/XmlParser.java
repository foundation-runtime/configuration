package com.cisco.oss.foundation.configuration.xml;

import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class XmlParser {

	private static final ThreadLocal<Marshaller> marshallerThreadLocal = new ThreadLocal<Marshaller>();
	private static final ThreadLocal<Unmarshaller> unmarshallerThreadLocal = new ThreadLocal<Unmarshaller>();
	private static final ThreadLocal<JaxbValidator> validatorThreadLocal = new ThreadLocal<JaxbValidator>();
	
	private static String JAXB_CONTEXT = "com.cisco.oss.foundation.configuration.xml.jaxb";

	private static boolean jaxbInit = false;
	private static JAXBContext jaxbContext = null;

	public XmlParser() {
	}

	// Used for Dependency Injection Mock-testing
	protected XmlParser(JAXBContext jaxbContext, Unmarshaller jaxbUnmarshaller, Marshaller jaxbMarshaller) {
		synchronized (XmlParser.class) {
			XmlParser.jaxbContext = jaxbContext;
			XmlParser.jaxbInit = true;
		}
	}

	// Used for testing only
	protected static void clearJaxbObjects() {
		synchronized (XmlParser.class) {
			jaxbContext = null;
			jaxbInit = false;
		}
	}

	private void init() throws JAXBException, SAXException {
		synchronized (XmlParser.class) {
			if (!jaxbInit) {
				jaxbContext = JAXBContext.newInstance(JAXB_CONTEXT);
				jaxbInit = true;
			}
		}		
	}

	public String marshall(Object jaxb) throws XmlException {
		StringWriter writer;

		try {
			init();
			Marshaller jaxbMarshaller = marshallerThreadLocal.get();
			if (null == jaxbMarshaller){
				jaxbMarshaller = jaxbContext.createMarshaller();
				jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				jaxbMarshaller.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, "http://ch-infra.il.nds.com/cabResources/CCP_XML.xsd");
			}
			writer = new StringWriter();

			synchronized (XmlParser.class) {
				jaxbMarshaller.marshal(jaxb, writer);
			}

			String marshellString = writer.toString();
			return (marshellString);
		} catch (JAXBException e) {
			throw new XmlException("Error marshalling given JAXB Object - JAXBException: " + e, e);
		} catch (SAXException e) {
			throw new XmlException("Error marshalling given JAXB Object - SAXException: " + e, e);
		}
	}

	public Object unmarshall(String xml) throws XmlException {
		try {
			init();
			Unmarshaller jaxbUnmarshaller = unmarshallerThreadLocal.get();
			if (null == jaxbUnmarshaller) {
				jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			}
			
			JaxbValidator validator = validatorThreadLocal.get();
			if (null == validator) {				
				validator = new JaxbValidator();
				validator.setValidating(jaxbUnmarshaller);
			}
			
			InputStream inStream = new ByteArrayInputStream(xml.getBytes("UTF-8"));

			Object jaxb;
			synchronized (XmlParser.class) {
				jaxb = jaxbUnmarshaller.unmarshal(inStream);
			}

			inStream.close();

			return (jaxb);
		} catch (JAXBException e) {
			throw new XmlException("The given XML Message could not be parsed - JAXBException: " + e, e);
		} catch (SAXException e) {
			throw new XmlException("The given XML Message could not be parsed - SAXException: " + e, e);
		} catch (IOException e) {
			throw new XmlException("The given XML Message could not be parsed - IOException: " + e, e);
		}
	}
}