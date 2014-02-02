package com.cisco.vss.foundation.configuration.xml;

import com.cisco.vss.foundation.configuration.xml.jaxb.ParameterInstantiations;
import junit.framework.TestCase;

public class ParameterInstantiationsMessageTest extends TestCase {
    public void testUnmarshall_Simple() throws Exception {
        String xml = XmlTestUtil.getXml(XmlTestUtil.XML_FILES.PARAMETER_INSTANTIATIONS);

        new ParameterInstantiationsMessage(xml);
    }
    public void testUnmarshall_ErrorHandling_WrongMessageType() throws Exception {
        String xml = XmlTestUtil.getXml(XmlTestUtil.XML_FILES.NAMESPACE_DEFINITIONS);

        try {
            new ParameterInstantiationsMessage(xml);
            fail("XmlException expected");
        } catch(XmlException e) {
            // good.  We expect a XmlException since this message doesn't correspond to a ParameterInstantiations Message.
        }
    }
    public void testMarshall_Simple() throws Exception {
        ParameterInstantiations jaxb = (ParameterInstantiations)XmlTestUtil.getJaxb(XmlTestUtil.XML_FILES.PARAMETER_INSTANTIATIONS);

        new ParameterInstantiationsMessage(jaxb);
    }
}