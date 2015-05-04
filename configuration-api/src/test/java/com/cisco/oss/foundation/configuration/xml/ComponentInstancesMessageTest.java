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
