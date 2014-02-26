package com.cisco.oss.foundation.configuration.xml;

import com.cisco.oss.foundation.configuration.xml.jaxb.ComponentInstances;
import junit.framework.TestCase;

public class ComponentInstancesMessageTest extends TestCase {

    public void testUnmarshall_Simple() throws Exception {
        String xml = XmlTestUtil.getXml(XmlTestUtil.XML_FILES.COMPONENT_INSTANCES);

        new ComponentInstancesMessage(xml);
    }
    public void testUnmarshall_ErrorHandling_WrongMessageType() throws Exception {
        String xml = XmlTestUtil.getXml(XmlTestUtil.XML_FILES.NAMESPACE_DEFINITIONS);

        try {
            new ComponentInstancesMessage(xml);
            fail("XmlException expected");
        } catch(XmlException e) {
            // good.  We expect a XmlException since this message doesn't correspond to a ComponentInstances Message.
        }
    }

    public void testMarshall_Simple() throws Exception {
        ComponentInstances jaxb = (ComponentInstances)XmlTestUtil.getJaxb(XmlTestUtil.XML_FILES.COMPONENT_INSTANCES);

        new ComponentInstancesMessage(jaxb);
    }
}
