package com.cisco.vss.foundation.configuration.xml;

import com.cisco.vss.foundation.configuration.xml.jaxb.ConfigurationResponse;
import junit.framework.TestCase;

public class ConfigurationResponseMessageTest extends TestCase {
    public void testUnmarshall_Simple() throws Exception {
        String xml = XmlTestUtil.getXml(XmlTestUtil.XML_FILES.CONFIGURATION_RESPONSE);

        new ConfigurationResponseMessage(xml);
    }
    public void testUnmarshall_GeneratedFromTestCase() throws Exception {
        String xml = XmlTestUtil.getXml(XmlTestUtil.XML_FILES.CONFIGURATION_GENERATED_GUI);

        new ConfigurationResponseMessage(xml);
    }
    public void testUnmarshall_ErrorHandling_WrongMessageType() throws Exception {
        String xml = XmlTestUtil.getXml(XmlTestUtil.XML_FILES.NAMESPACE_DEFINITIONS);

        try {
            new ConfigurationResponseMessage(xml);
            fail("XmlException expected");
        } catch(XmlException e) {
            // good.  We expect a XmlException since this message doesn't correspond to a ConfigurationResponse Message.
        }
    }
    public void testMarshall_Simple() throws Exception {
        ConfigurationResponse jaxb = (ConfigurationResponse)XmlTestUtil.getJaxb(XmlTestUtil.XML_FILES.CONFIGURATION_RESPONSE);

        new ConfigurationResponseMessage(jaxb);
    }
}
