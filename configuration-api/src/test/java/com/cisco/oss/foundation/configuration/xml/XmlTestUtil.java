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

/**
 * utility class for XML Message Tests
 * @author dventura
 *
 */
public class XmlTestUtil {

    private static XmlParser parser = new XmlParser();
    private static XmlLoader loader = new XmlLoader();

    public interface XML_FILES {
        public static final String BASE    = "./";
        public static final String COMPONENT_INSTANCES  = BASE + "ComponentInstances_simple.xml";
        
        public static final String CONFIGURATION_OPERATIONS  = BASE + "ConfigurationOperations_simple.xml";
        public static final String CONFIGURATION_RESPONSE  = BASE + "ConfigurationResponse_simple.xml";
        public static final String CONFIGURATION_GENERATED_GUI  = BASE + "ConfigurationResponse_generatedFromTestCase_gui.xml";
        public static final String CONFIGURATION_GENERATED_CLIENT  = BASE + "ConfigurationResponse_generatedFromTestCase_client.xml";
        public static final String CONFIGURATION_STRUCTURE_GUI  = BASE + "ConfigurationResponse_StructureGUI.xml";
        public static final String CONFIGURATION_STRUCTURE_CLIENT  = BASE + "ConfigurationResponse_StructureClient.xml";

        public static final String HIERARCHY_TREE = BASE + "HierarchyTree_simple.xml";
        public static final String INVALID = BASE + "Invalid.xml";
        public static final String NAMESPACE_DEFINITIONS  = BASE + "NamespaceDefinitions_simple.xml";
        public static final String NAMESPACE_DEF_STRUCT_ARR  = BASE + "NamespaceDefinition_structureArray.xml";
        public static final String NAMESPACE_DEF_NO_PARAMS  = BASE + "NamespaceDefinition_noParameters.xml";
        public static final String NAMESPACE_LOTS_DEPS  = BASE + "NamespaceDefinition_LotsOfDependencies.xml";
        public static final String NAMESPACE_CIRCULAR_DEP  = BASE + "NamespaceDefinition_circularDependency.xml";

        public static final String PARAMETER_INSTANTIATIONS  = BASE + "ParameterInstantiations_simple.xml";

        public static final String NAMESPACE_DEFINITIONS_1  = BASE + "NamespaceDefinition_example1.xml";
        public static final String NAMESPACE_DEFINITIONS_2  = BASE + "NamespaceDefinition_example2.xml";
        
        //Inject Namespace Test XMLs
        public static final String INJECT_NAMESPACE_BASE = "injectNamespaceXmls/";
        public static final String INJECT_NAMESPACE_NAMESPACES_ONLY = BASE + INJECT_NAMESPACE_BASE + "InjectNamespace_NamespacesOnly.xml"; 
        public static final String INJECT_NAMESPACE_PARAMS_ONLY = BASE + INJECT_NAMESPACE_BASE + "InjectNamespace_ParametersOnly.xml"; 
        public static final String INJECT_NAMESPACE_DEPENDENCIES = BASE + INJECT_NAMESPACE_BASE + "InjectNamespace_Dependencies.xml"; 
        public static final String INJECT_NAMESPACE_OVERRIDES = BASE + INJECT_NAMESPACE_BASE + "InjectNamespace_Overrides.xml"; 
        public static final String INJECT_NAMESPACE_STRUCTURES_ONLY = BASE + INJECT_NAMESPACE_BASE + "InjectNamespace_StructuresOnly.xml";
        public static final String INJECT_NAMESPACE_COMPLEX_STRUCTURES_ONLY = BASE + INJECT_NAMESPACE_BASE + "InjectNamespace_ComplexStructuresOnly.xml";
        public static final String INJECT_NAMESPACE_COMPLEX_STRUCTURES_WITH_OVERRIDE = BASE + INJECT_NAMESPACE_BASE + "InjectNamespace_ComplexStructuresWithOverride.xml";
        public static final String INJECT_NAMESPACE_ARRAYS_ONLY = BASE + INJECT_NAMESPACE_BASE + "InjectNamespace_ArraysOnly.xml"; 
        public static final String INJECT_NAMESPACE_RANGES_ONLY = BASE + INJECT_NAMESPACE_BASE + "InjectNamespace_RangesOnly.xml"; 
        public static final String INJECT_NAMESPACE_ENABLE_BYS = BASE + INJECT_NAMESPACE_BASE + "InjectNamespace_EnableBys.xml"; 
        public static final String INJECT_NAMESPACE_NULL_VALUES = BASE + INJECT_NAMESPACE_BASE + "InjectNamespace_NullValues.xml";
        public static final String INJECT_NAMESPACE_WITH_LINK = BASE + INJECT_NAMESPACE_BASE + "InjectNamespace_WithLink.xml";
        public static final String INJECT_NAMESPACE_UPDATE_DB_PASSWORD = BASE + INJECT_NAMESPACE_BASE + "InjectNamespace_UpdateDBPassword.xml";
        //Invalid Inject Namespace Test XMLs 
        public static final String INJECT_NAMESPACE_INVALID_STRUCT_OF_STRUCTS = BASE + INJECT_NAMESPACE_BASE + "InjectNamespace_Invalid_StructOfStructs.xml"; 
        public static final String INJECT_NAMESPACE_INVALID_OVERRIDE_AN_OVERRIDE = BASE + INJECT_NAMESPACE_BASE + "InjectNamespace_Invalid_OverrideAnOverride.xml"; 
        public static final String INJECT_NAMESPACE_INVALID_PARAMETER_ENABLED_BY_ARRAY = BASE + INJECT_NAMESPACE_BASE + "InjectNamespace_Invalid_ParameterEnabledByArray.xml"; 
        public static final String INJECT_NAMESPACE_INVALID_PARAMETER_ENABLED_BY_STRUCT_MEMBER = BASE + INJECT_NAMESPACE_BASE + "InjectNamespace_Invalid_ParameterEnabledByStructMember.xml"; 
        public static final String INJECT_NAMESPACE_INVALID_PARAMETER_ENABLED_BY_TWICE = BASE + INJECT_NAMESPACE_BASE + "InjectNamespace_Invalid_ParameterEnabledByTwice.xml"; 
        public static final String INJECT_NAMESPACE_INVALID_PARAMETER_ENABLED_BY_DIRECT_CIRCULAR_DEP = BASE + INJECT_NAMESPACE_BASE + "InjectNamespace_Invalid_ParameterEnabledByDirectCircularDependency.xml"; 
        
    }

    public static String getXml(String path) throws XmlLoaderException {
        String xml = loader.loadXmlFromResource(path);
        return(xml);
    }
    public static Object getJaxb(String path) throws XmlLoaderException, XmlException  {
        String xml = getXml(path);
        return( parser.unmarshall(xml) );
    }
}
