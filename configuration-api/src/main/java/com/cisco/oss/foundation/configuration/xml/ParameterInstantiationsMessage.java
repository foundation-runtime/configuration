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

import com.cisco.oss.foundation.configuration.xml.jaxb.ParameterInstantiations;

/**
 * This class is responsible for marshalling and unmarshalling ParameterInstantiations messages.
 * @author dventura
 *
 */
public class ParameterInstantiationsMessage {

    private String xml = null;
    private ParameterInstantiations jaxb = null;
    private XmlParser parser = null;

    public ParameterInstantiationsMessage(String xml) throws XmlException {
        parser = new XmlParser();
        jaxb = unmarshall(xml);
        this.xml = xml;
    }
    public ParameterInstantiationsMessage(ParameterInstantiations jaxb) throws XmlException {
        parser = new XmlParser();
        xml = marshall(jaxb);
        this.jaxb = jaxb;
    }

    public String toXml() {
        return(xml);
    }

    public ParameterInstantiations jaxb() {
        return(jaxb);
    }

    protected String marshall(ParameterInstantiations jaxb) throws XmlException {
        return(parser.marshall(jaxb));
    }

    protected ParameterInstantiations unmarshall(String xml) throws XmlException {
        ParameterInstantiations jaxb = null;
        try {
            jaxb = (ParameterInstantiations)parser.unmarshall(xml);
        } catch(ClassCastException e) {
            throw new XmlException("The given message was not a ParameterInstantiations Message - ClassCastException: " + e.getMessage(), e);
        }
        return(jaxb);
    }
}
