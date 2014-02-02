package com.cisco.vss.foundation.configuration.xml;

import com.cisco.vss.foundation.configuration.xml.jaxb.NamespaceDefinition;
import com.cisco.vss.foundation.configuration.xml.jaxb.NamespaceDefinitions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class is responsible for marshalling and unmarshalling NamespaceDefinition messages.
 * @author dventura
 *
 */
public class NamespaceDefinitionMessage {

    private String xml = null;
    private NamespaceDefinitions jaxb = null;
    private XmlParser parser = null;

    public NamespaceDefinitionMessage(String xml) throws XmlException {
        parser = new XmlParser();
        jaxb = unmarshall(xml);
        this.xml = xml;
    }
    public NamespaceDefinitionMessage(NamespaceDefinitions jaxb) throws XmlException {
        parser = new XmlParser();
        xml = marshall(jaxb);
        this.jaxb = jaxb;
    }

    public String toXml() {
        return(xml);
    }

    public NamespaceDefinitions jaxb() {
        return(jaxb);
    }

    protected String marshall(NamespaceDefinitions jaxb) throws XmlException {
        return(parser.marshall(jaxb));
    }

    protected NamespaceDefinitions unmarshall(String xml) throws XmlException {
        NamespaceDefinitions jaxb = null;
        try {
            jaxb = (NamespaceDefinitions)parser.unmarshall(xml);
        } catch(ClassCastException e) {
            throw new XmlException("The given message was not a NamespaceDefinitions Message - ClassCastException: " + e.getMessage(), e);
        }
        return(jaxb);
    }
    
    /**
     * A NamespaceDefinition cannot be injected into the DB unless it's dependencies are injected first.
     * This method will sort the given list of NamespaceDefinitions by dependency.
     * 
     * If dependencies are missing, or circular dependencies exist such that there aren't any NamespaceDefinitions without dependencies
     * a XmlException will be thrown.
     *
     * @return The list of NamespaceDefinition Object, sorted by dependencies
     * 
     * @throws XmlException if dependencies are missing or circular dependencies exist such that there aren't any NamespaceDefinitions without dependencies
     */
    public List<NamespaceDefinition> sortByDependencies() throws XmlException {

        ArrayList<NamespaceDefinition> sorted = new ArrayList<NamespaceDefinition>();

        Map<Namespace, List<String>> namespaceMap = createNamespaceMap(jaxb.getNamespaceDefinitions());

        NamespaceDefinition def = null;
        do {
            def = extractNamespaceWithLeastDependencies(namespaceMap);
            if(def != null) {
                sorted.add(def);
            }
        } while(def != null);

        return(sorted);
    }

    protected NamespaceDefinition extractNamespaceWithLeastDependencies(Map<Namespace, List<String>> namespaceMap) {

        if( (namespaceMap == null) || (namespaceMap.keySet().size() == 0) ) {
            return(null);
        }

        Namespace lowest = null;
        int lowestSize = Integer.MAX_VALUE;

        for(Namespace current: namespaceMap.keySet()) {
            int currentSize = namespaceMap.get(current).size();

            if( (lowest == null) || (currentSize < lowestSize) ){
                lowest = current;
                lowestSize = currentSize;
            }
        }

        namespaceMap.remove(lowest);

        return(lowest.getNamespaceDefinition());
    }

    /**
     * Checks that there aren't namespaces that depend on namespaces that don't exist in the list,
     * and creates a list of all Namespace Objects in the list.
     * 
     * @throws XmlException if there are any dependencies missing.
     */
    protected Map<Namespace, List<String>> createNamespaceMap(List<NamespaceDefinition> namespaceDefs) throws XmlException {
        ArrayList<Namespace> namespaces = new ArrayList<Namespace>();

        // add all root namespaces
        for(NamespaceDefinition namespaceDef: namespaceDefs) {
            Namespace node = new Namespace(namespaceDef);
            if(namespaces.contains(node)) {
                throw new XmlException("duplicate NamespaceDefinitions found: " + node.getId());
            }
            namespaces.add(node);
        }

        // go through each namespace's dependencies to see if any are missing
        for(Namespace namespace: namespaces) {
            List<String> dependencyIds = namespace.getDirectDependencyIds();
            for(String id: dependencyIds) {
                if(find(namespaces, id) == null) {
                    throw new XmlException("Found missing dependency: " + id);
                }
            }
        }

        HashMap<Namespace, List<String>> namespaceFullDependencyMap = new HashMap<Namespace, List<String>>();
        for(Namespace namespace: namespaces) {
            ArrayList<String> fullDependencies = new ArrayList<String>();

            for(String directDependencyId: namespace.getDirectDependencyIds()) {
                addDependenciesRecursively(namespace, namespaces, directDependencyId, fullDependencies);
            }

            namespaceFullDependencyMap.put(namespace, fullDependencies);
        }

        return(namespaceFullDependencyMap);
    }

    /**
     * Adds dependencyId, and all of it's depdencies recursively using namespaceList, to extendedDependencies for namespace.
     * @throws XmlException 
     */
    protected void addDependenciesRecursively(Namespace namespace, List<Namespace> namespaceList, String dependencyId, List<String> extendedDependencies) throws XmlException {
        if(extendedDependencies.contains(dependencyId)) {
            return;
        }
        if(namespace.getId().equals(dependencyId)) {
            throw new XmlException("Circular dependency found in " + namespace.getId());
        }

        Namespace dependency = find(namespaceList, dependencyId);
        extendedDependencies.add(dependency.getId());
        for(String indirectDependencyId: dependency.getDirectDependencyIds()) {
            addDependenciesRecursively(namespace, namespaceList, indirectDependencyId, extendedDependencies);
        }
    }

    private Namespace find(List<Namespace> namespaces, String id) throws XmlException {
        for(Namespace namespace: namespaces) {
            if(namespace.getId().equals(id)) {
                return(namespace);
            }
        }
        return(null);
    }

}
