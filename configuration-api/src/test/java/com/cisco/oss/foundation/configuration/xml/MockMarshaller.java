/*
 * Copyright 2015 Cisco Systems, Inc.
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

package com.cisco.oss.foundation.configuration.xml;

import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.validation.Schema;
import java.io.File;
import java.io.OutputStream;
import java.io.Writer;

public class MockMarshaller implements Marshaller {

	public MockMarshaller() {
	}

	@Override
	public void marshal(Object jaxbElement, Writer writer) throws JAXBException {
		throw new JAXBException("MockMarshaller intentially throws JAXBException");
	}


	/**
	 * UNUSED METHODS **
	 */

	@Override
	public void marshal(Object jaxbElement, Result result) throws JAXBException {
	}

	@Override
	public void marshal(Object jaxbElement, OutputStream os) throws JAXBException {
	}

	@Override
	public void marshal(Object jaxbElement, File output) throws JAXBException {
	}

	@Override
	public void marshal(Object jaxbElement, ContentHandler handler) throws JAXBException {
	}

	@Override
	public void marshal(Object jaxbElement, Node node) throws JAXBException {
	}

	@Override
	public void marshal(Object jaxbElement, XMLStreamWriter writer) throws JAXBException {
	}

	@Override
	public void marshal(Object jaxbElement, XMLEventWriter writer) throws JAXBException {
	}

	@Override
	public Node getNode(Object contentTree) throws JAXBException {
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
	public void setEventHandler(ValidationEventHandler handler) throws JAXBException {
	}

	@Override
	public ValidationEventHandler getEventHandler() throws JAXBException {
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
	public void setAttachmentMarshaller(AttachmentMarshaller am) {
	}

	@Override
	public AttachmentMarshaller getAttachmentMarshaller() {
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
	public void setListener(Listener listener) {
	}

	@Override
	public Listener getListener() {
		return null;
	}
}
