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

import com.cisco.oss.foundation.configuration.xml.jaxb.NamespaceDefinition;
import com.cisco.oss.foundation.configuration.xml.jaxb.NamespaceDefinitions;
import com.cisco.oss.foundation.configuration.xml.jaxb.ObjectFactory;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

public class NamespaceDefinitionMessageTest extends TestCase {
    public void testUnmarshall_Simple() throws Exception {
        String xml = XmlTestUtil.getXml(XmlTestUtil.XML_FILES.NAMESPACE_DEFINITIONS);

        new NamespaceDefinitionMessage(xml);
    }
    public void testUnmarshall_StructureArray() throws Exception {
        String xml = XmlTestUtil.getXml(XmlTestUtil.XML_FILES.NAMESPACE_DEF_STRUCT_ARR);

        new NamespaceDefinitionMessage(xml);
    }
    public void testUnmarshall_NoParameters() throws Exception {
        String xml = XmlTestUtil.getXml(XmlTestUtil.XML_FILES.NAMESPACE_DEF_NO_PARAMS);

        new NamespaceDefinitionMessage(xml);
    }
    public void testUnmarshall_1() throws Exception {
        String xml = XmlTestUtil.getXml(XmlTestUtil.XML_FILES.NAMESPACE_DEFINITIONS_1);

        new NamespaceDefinitionMessage(xml);
    }
    public void testUnmarshall_2() throws Exception {
        String xml = XmlTestUtil.getXml(XmlTestUtil.XML_FILES.NAMESPACE_DEFINITIONS_2);

        new NamespaceDefinitionMessage(xml);
    }
    public void testUnmarshall_ErrorHandling_WrongMessageType() throws Exception {
        String xml = XmlTestUtil.getXml(XmlTestUtil.XML_FILES.CONFIGURATION_RESPONSE);

        try {
            new NamespaceDefinitionMessage(xml);
            fail("XmlException expected");
        } catch(XmlException e) {
            // good.  We expect a XmlException since this message doesn't correspond to a NamespaceDefinitions Message.
        }
    }
    public void testMarshall_Simple() throws Exception {
        NamespaceDefinitions jaxb = (NamespaceDefinitions)XmlTestUtil.getJaxb(XmlTestUtil.XML_FILES.NAMESPACE_DEFINITIONS);

        new NamespaceDefinitionMessage(jaxb);
    }

    public void testCreateNamespaceDefinitionList_ForNathan() throws Exception {
        String xml = XmlTestUtil.getXml(XmlTestUtil.XML_FILES.NAMESPACE_DEFINITIONS);

        NamespaceDefinitionMessage message = new NamespaceDefinitionMessage(xml);
        
        NamespaceDefinitions definitions = message.jaxb();

        List<NamespaceDefinition> namespaceList = definitions.getNamespaceDefinitions();

        assertEquals("Expected 3 Namespaces", namespaceList.size(), 3);
    }

    public void testSortByDependencies() throws Exception {
        String xml = XmlTestUtil.getXml(XmlTestUtil.XML_FILES.NAMESPACE_LOTS_DEPS);
        NamespaceDefinitionMessage msg = new NamespaceDefinitionMessage(xml);

        List<NamespaceDefinition> sorted = msg.sortByDependencies();

        ObjectFactory jaxbFactory = new ObjectFactory();
        NamespaceDefinitions namespaceDefs = jaxbFactory.createNamespaceDefinitions();
        namespaceDefs.getNamespaceDefinitions().addAll(sorted);
        msg = new NamespaceDefinitionMessage(namespaceDefs);

        assertTrue(isOneOf(sorted.get(0).getNamespaceIdentifier().getName(), "C D F G"));
        assertTrue(isOneOf(sorted.get(1).getNamespaceIdentifier().getName(), "C D F G"));
        assertTrue(isOneOf(sorted.get(2).getNamespaceIdentifier().getName(), "C D F G"));
        assertTrue(isOneOf(sorted.get(3).getNamespaceIdentifier().getName(), "C D F G"));
        assertTrue(isOneOf(sorted.get(4).getNamespaceIdentifier().getName(), "B E"));
        assertTrue(isOneOf(sorted.get(5).getNamespaceIdentifier().getName(), "B E"));
        assertTrue(isOneOf(sorted.get(6).getNamespaceIdentifier().getName(), "A"));
        assertTrue(isOneOf(sorted.get(7).getNamespaceIdentifier().getName(), "H"));
    }
    public void testSortByDependencies_circularDependency() throws Exception {
        String xml = XmlTestUtil.getXml(XmlTestUtil.XML_FILES.NAMESPACE_CIRCULAR_DEP);
        NamespaceDefinitionMessage msg = new NamespaceDefinitionMessage(xml);

        try {
            msg.sortByDependencies();
            fail("Expected XmlException");
        } catch(XmlException e) {}
    }

    private boolean isOneOf(String val, String range) throws Exception {
        StringTokenizer strTok = new StringTokenizer(range);

        while(strTok.hasMoreElements()) {
            String token = strTok.nextToken();
            if(val.equals(token)) {
                return(true);
            }
        }
        throw new Exception(val + " is not a member of " + range);
    }

    public void testExtractNamespaceWithLeastDependencies_errorCondition() throws Exception {
        String xml = XmlTestUtil.getXml(XmlTestUtil.XML_FILES.NAMESPACE_LOTS_DEPS);
        NamespaceDefinitionMessage msg = new NamespaceDefinitionMessage(xml);

        HashMap<Namespace, List<String>> namespaceMap = null;
        
        assertNull(msg.extractNamespaceWithLeastDependencies(namespaceMap));
        
        namespaceMap = new HashMap<Namespace, List<String>>();
        
        assertNull(msg.extractNamespaceWithLeastDependencies(namespaceMap));
    }
}