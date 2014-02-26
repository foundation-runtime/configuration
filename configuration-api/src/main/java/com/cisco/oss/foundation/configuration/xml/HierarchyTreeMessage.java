package com.cisco.oss.foundation.configuration.xml;

import com.cisco.oss.foundation.configuration.xml.jaxb.HierarchyTree;

/**
 * This class is responsible for marshalling and unmarshalling HierarchyTree messages.
 * @author dventura
 *
 */
public class HierarchyTreeMessage {

    private String xml = null;
    private HierarchyTree jaxb = null;
    private XmlParser parser = null;

    public HierarchyTreeMessage(String xml) throws XmlException {
        parser = new XmlParser();
        jaxb = unmarshall(xml);
        this.xml = xml;
    }
    public HierarchyTreeMessage(HierarchyTree jaxb) throws XmlException {
        parser = new XmlParser();
        xml = marshall(jaxb);
        this.jaxb = jaxb;
    }

    public String toXml() {
        return(xml);
    }

    public HierarchyTree jaxb() {
        return(jaxb);
    }

    protected String marshall(HierarchyTree jaxb) throws XmlException {
        return(parser.marshall(jaxb));
    }

    protected HierarchyTree unmarshall(String xml) throws XmlException {
        HierarchyTree jaxb = null;
        try {
            jaxb = (HierarchyTree)parser.unmarshall(xml);
        } catch(ClassCastException e) {
            throw new XmlException("The given message was not a HierarchyTree Message - ClassCastException: " + e.getMessage(), e);
        }
        return(jaxb);
    }
}
