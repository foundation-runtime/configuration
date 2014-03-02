/*
 * Copyright 2014 Cisco Systems, Inc.
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

/**
 * This class is responsible for marshalling and unmarshalling ComponentInstances messages.
 * @author dventura
 *
 */
public class ComponentInstancesMessage {

    private String xml = null;
    private ComponentInstances jaxb = null;
    private XmlParser parser = null;

    public ComponentInstancesMessage(String xml) throws XmlException {
        parser = new XmlParser();
        jaxb = unmarshall(xml);
        this.xml = xml;
    }
    public ComponentInstancesMessage(ComponentInstances jaxb) throws XmlException {
        parser = new XmlParser();
        xml = marshall(jaxb);
        this.jaxb = jaxb;
    }

    public String toXml() {
        return(xml);
    }

    public ComponentInstances jaxb() {
        return(jaxb);
    }

    protected String marshall(ComponentInstances jaxb) throws XmlException {
        return(parser.marshall(jaxb));
    }

    protected ComponentInstances unmarshall(String xml) throws XmlException {
        ComponentInstances jaxb = null;
        try {
            jaxb = (ComponentInstances)parser.unmarshall(xml);
        } catch(ClassCastException e) {
            throw new XmlException("The given message was not a ComponentInstances Message - ClassCastException: " + e.getMessage(), e);
        }
        return(jaxb);
    }
}
