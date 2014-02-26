package com.cisco.oss.foundation.configuration.xml;

import com.cisco.oss.foundation.configuration.xml.jaxb.ConfigurationOperations;

/**
 * This class is responsible for marshalling and unmarshalling ConfigurationOperations messages.
 * @author dventura
 *
 */
public class ConfigurationOperationsMessage {

    private String xml = null;
    private ConfigurationOperations jaxb = null;
    private XmlParser parser = null;

    public ConfigurationOperationsMessage(String xml) throws XmlException {
        parser = new XmlParser();
        jaxb = unmarshall(xml);
        this.xml = xml;
    }
    public ConfigurationOperationsMessage(ConfigurationOperations jaxb) throws XmlException {
        parser = new XmlParser();
        xml = marshall(jaxb);
        this.jaxb = jaxb;
    }

    public String toXml() {
        return(xml);
    }

    public ConfigurationOperations jaxb() {
        return(jaxb);
    }

    protected String marshall(ConfigurationOperations jaxb) throws XmlException {
        return(parser.marshall(jaxb));
    }

    protected ConfigurationOperations unmarshall(String xml) throws XmlException {
        ConfigurationOperations jaxb = null;
        try {
            jaxb = (ConfigurationOperations)parser.unmarshall(xml);
        } catch(ClassCastException e) {
            throw new XmlException("The given message was not a ConfigurationOperations Message - ClassCastException: " + e.getMessage(), e);
        }
        return(jaxb);
    }
}
