package com.cisco.oss.foundation.configuration.xml;

import com.cisco.oss.foundation.configuration.xml.jaxb.ConfigurationOperations;
import junit.framework.TestCase;

public class ConfigurationOperationsMessageTest extends TestCase {

    public void testUnmarshall_Simple() throws Exception {
        String xml = XmlTestUtil.getXml(XmlTestUtil.XML_FILES.CONFIGURATION_OPERATIONS);

        new ConfigurationOperationsMessage(xml);
    }
    public void testUnmarshall_ErrorHandling_WrongMessageType() throws Exception {
        String xml = XmlTestUtil.getXml(XmlTestUtil.XML_FILES.NAMESPACE_DEFINITIONS);

        try {
            new ConfigurationOperationsMessage(xml);
            fail("XmlException expected");
        } catch(XmlException e) {
            // good.  We expect a XmlException since this message doesn't correspond to a ConfigurationOperations Message.
        }
    }

    public void testMarshall_Simple() throws Exception {
        ConfigurationOperations jaxb = (ConfigurationOperations)XmlTestUtil.getJaxb(XmlTestUtil.XML_FILES.CONFIGURATION_OPERATIONS);

        new ConfigurationOperationsMessage(jaxb);
    }
}
