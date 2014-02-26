package com.cisco.oss.foundation.configuration.xml;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.bind.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

public class MockUnmarshaller implements Unmarshaller {

	public MockUnmarshaller() {
	}

	@Override
	public Object unmarshal(InputStream is) throws JAXBException {
		throw new JAXBException("MockUnmarshaller intentially throws JAXBException");
	}


	/**
	 * UNUSED METHODS **
	 */

	@Override
	public Object unmarshal(File f) throws JAXBException {
		return null;
	}

	@Override
	public Object unmarshal(Reader reader) throws JAXBException {
		return null;
	}

	@Override
	public Object unmarshal(URL url) throws JAXBException {
		return null;
	}

	@Override
	public Object unmarshal(InputSource source) throws JAXBException {
		return null;
	}

	@Override
	public Object unmarshal(Node node) throws JAXBException {
		return null;
	}

	@Override
	public <T> JAXBElement<T> unmarshal(Node node, Class<T> declaredType) throws JAXBException {
		return null;
	}

	@Override
	public Object unmarshal(Source source) throws JAXBException {
		return null;
	}

	@Override
	public <T> JAXBElement<T> unmarshal(Source source, Class<T> declaredType) throws JAXBException {
		return null;
	}

	@Override
	public Object unmarshal(XMLStreamReader reader) throws JAXBException {
		return null;
	}

	@Override
	public <T> JAXBElement<T> unmarshal(XMLStreamReader reader, Class<T> declaredType) throws JAXBException {
		return null;
	}

	@Override
	public Object unmarshal(XMLEventReader reader) throws JAXBException {
		return null;
	}

	@Override
	public <T> JAXBElement<T> unmarshal(XMLEventReader reader, Class<T> declaredType) throws JAXBException {
		return null;
	}

	@Override
	public UnmarshallerHandler getUnmarshallerHandler() {
		return null;
	}

	@Override
	public void setEventHandler(ValidationEventHandler handler) throws JAXBException {
	}

	@Override
	public ValidationEventHandler getEventHandler() throws JAXBException {
		return null;
	}

	@Override
	public void setProperty(String name, Object value) throws PropertyException {
	}

	@Override
	public Object getProperty(String name) throws PropertyException {
		return null;
	}

	@Override
	public void setSchema(Schema schema) {
	}

	@Override
	public Schema getSchema() {
		return null;
	}

	@Override
	public void setAdapter(XmlAdapter adapter) {
	}

	@Override
	public <A extends XmlAdapter> void setAdapter(Class<A> type, A adapter) {
	}

	@Override
	public <A extends XmlAdapter> A getAdapter(Class<A> type) {
		return null;
	}

	@Override
	public void setAttachmentUnmarshaller(AttachmentUnmarshaller au) {
	}

	@Override
	public AttachmentUnmarshaller getAttachmentUnmarshaller() {
		return null;
	}

	@Override
	public void setListener(Listener listener) {
	}

	@Override
	public Listener getListener() {
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setValidating(boolean validating) throws JAXBException {
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isValidating() throws JAXBException {
		return false;
	}

}
