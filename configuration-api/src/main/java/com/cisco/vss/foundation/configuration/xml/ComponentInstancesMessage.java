package com.cisco.vss.foundation.configuration.xml;

import com.cisco.vss.foundation.configuration.xml.jaxb.ComponentInstances;

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
