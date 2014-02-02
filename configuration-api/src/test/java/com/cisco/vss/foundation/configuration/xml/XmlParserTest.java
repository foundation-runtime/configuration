package com.cisco.vss.foundation.configuration.xml;

import junit.framework.TestCase;

import javax.xml.bind.JAXBContext;

public class XmlParserTest extends TestCase {

    private static String JAXB_CONTEXT = "com.cisco.vss.foundation.configuration.xml.jaxb";

    public void testUnmarshaller_ComponentInstances() throws Exception {
        UnmarshallTest(XmlTestUtil.XML_FILES.COMPONENT_INSTANCES);
    }
    public void testUnmarshaller_ConfigurationOperations() throws Exception {
        UnmarshallTest(XmlTestUtil.XML_FILES.CONFIGURATION_OPERATIONS);
    }
    public void testUnmarshaller_ConfigurationResponse() throws Exception {
        UnmarshallTest(XmlTestUtil.XML_FILES.CONFIGURATION_RESPONSE);
    }
    public void testUnmarshaller_HierarchyTree() throws Exception {
        UnmarshallTest(XmlTestUtil.XML_FILES.HIERARCHY_TREE);
    }
    public void testUnmarshaller_NamespaceDefinitions() throws Exception {
        UnmarshallTest(XmlTestUtil.XML_FILES.NAMESPACE_DEFINITIONS);
    }
    public void testUnmarshaller_ParameterInstantiations() throws Exception {
        UnmarshallTest(XmlTestUtil.XML_FILES.PARAMETER_INSTANTIATIONS);
    }

    public void testMarshaller_ComponentInstances() throws Exception {
        MarshallTest(XmlTestUtil.XML_FILES.COMPONENT_INSTANCES);
    }
    public void testMarshaller_ConfigurationOperations() throws Exception {
        MarshallTest(XmlTestUtil.XML_FILES.CONFIGURATION_OPERATIONS);
    }
    public void testMarshaller_ConfigurationResponse() throws Exception {
        MarshallTest(XmlTestUtil.XML_FILES.CONFIGURATION_RESPONSE);
    }
    public void testMarshaller_HierarchyTree() throws Exception {
        MarshallTest(XmlTestUtil.XML_FILES.HIERARCHY_TREE);
    }
    public void testMarshaller_NamespaceDefinitions() throws Exception {
        MarshallTest(XmlTestUtil.XML_FILES.NAMESPACE_DEFINITIONS);
    }
    public void testMarshaller_ParameterInstantiations() throws Exception {
        MarshallTest(XmlTestUtil.XML_FILES.PARAMETER_INSTANTIATIONS);
    }

    public void testUnmarshaller_ErrorHandlingInvalidXml() throws Exception {
        String xml = XmlTestUtil.getXml(XmlTestUtil.XML_FILES.INVALID);

        try {
            XmlParser parser = new XmlParser();
            parser.unmarshall(xml);
            fail("XmlException expected");
        } catch(XmlException e) {
            // The MockUnmarshaller will throw a JAXBException, which should be encapsulated in a XmlException
        }
    }
    public void testUnmarshaller_ErrorHandlingJaxbException() throws Exception {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(JAXB_CONTEXT);

            XmlParser parser = new XmlParser(jaxbContext, new MockUnmarshaller(), jaxbContext.createMarshaller());
            parser.unmarshall("foo");
            fail("XmlException expected");
        } catch(XmlException e) {
            // The MockUnmarshaller will throw a JAXBException, which should be encapsulated in a XmlException
        } finally {
            XmlParser.clearJaxbObjects();
        }
    }
    public void testMarshaller_ErrorHandlingJaxbException() throws Exception {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(JAXB_CONTEXT);

            XmlParser parser = new XmlParser(jaxbContext, jaxbContext.createUnmarshaller(), new MockMarshaller());

            parser.marshall(new Object());
            fail("XmlException expected");
        } catch(XmlException e) {
            // The MockMarshaller will throw a JAXBException, which should be encapsulated in a XmlException
        } finally {
            XmlParser.clearJaxbObjects();
        }
    }

    private void UnmarshallTest(String path) throws XmlLoaderException, XmlException {
        XmlParser parser = new XmlParser();
        String xml = XmlTestUtil.getXml(path);

        parser.unmarshall(xml);
    }
    private void MarshallTest(String path) throws XmlLoaderException, XmlException {
        XmlParser parser = new XmlParser();
        Object jaxb = XmlTestUtil.getJaxb(path);
        parser.marshall(jaxb);
    }
}
