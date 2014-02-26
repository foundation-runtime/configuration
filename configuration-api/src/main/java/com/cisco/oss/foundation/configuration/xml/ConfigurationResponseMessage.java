package com.cisco.oss.foundation.configuration.xml;

import com.cisco.oss.foundation.configuration.xml.jaxb.ConfigurationResponse;

/**
 * This class is responsible for marshalling and unmarshalling ConfigurationResponse messages.
 * @author dventura
 *
 */
public class ConfigurationResponseMessage {

    private String xml = null;
    private ConfigurationResponse jaxb = null;
    private XmlParser parser = null;

    public ConfigurationResponseMessage(String xml) throws XmlException {
        parser = new XmlParser();
        jaxb = unmarshall(xml);
        this.xml = xml;
    }
    public ConfigurationResponseMessage(ConfigurationResponse jaxb) throws XmlException {
        parser = new XmlParser();
        xml = marshall(jaxb);
        this.jaxb = jaxb;
    }

    public String toXml() {
        return(xml);
    }

    public ConfigurationResponse jaxb() {
        return(jaxb);
    }

    protected String marshall(ConfigurationResponse jaxb) throws XmlException {
        return(parser.marshall(jaxb));
    }

    protected ConfigurationResponse unmarshall(String xml) throws XmlException {
        ConfigurationResponse jaxb = null;
        try {
            jaxb = (ConfigurationResponse)parser.unmarshall(xml);
        } catch(ClassCastException e) {
            throw new XmlException("The given message was not a ConfigurationResponse Message - ClassCastException: " + e.getMessage(), e);
        }
        return(jaxb);
    }
}
