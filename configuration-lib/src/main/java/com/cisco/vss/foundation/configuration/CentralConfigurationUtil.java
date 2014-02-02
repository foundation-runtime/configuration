/**
 *
 */
package com.cisco.vss.foundation.configuration;

import com.cisco.vss.foundation.configuration.xml.ConfigurationResponseMessage;
import com.cisco.vss.foundation.configuration.xml.NamespaceDefinitionMessage;
import com.cisco.vss.foundation.configuration.xml.XmlException;
import com.cisco.vss.foundation.configuration.xml.jaxb.*;
import com.cisco.vss.foundation.http.*;
import com.cisco.vss.foundation.http.apache.ApacheHttpClientFactory;
import org.apache.commons.configuration.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;

/**
 * This class is a utility that handled CCP related API's. It serves in a dual
 * mdoe: <br>
 * 1. support developer and non CCP enabled mode <br>
 * 2. support loading from DB or HTTP when CCP is enabled.
 *
 * @author Yair Ogen
 */
public enum CentralConfigurationUtil {

    // singleton implemetnation.
    INSTANCE;
    public static final String CCP_BACKUP_FILE_NAME = System.getProperty("app.instance.name") == null ? "/ccp.backup.properties" : "/ccp." + System.getProperty("app.instance.name") + ".backup.properties";
    public static final String CCP_BACKUP_DIR = "_CCP_BACKUP_DIR";
    public static final Map<String, Parameter> parameterMap = new HashMap<String, Parameter>();
    // compile the message format only once as instantiation of this is
    // expensive.
    private static final MessageFormat GET_CONFIG_URL = new MessageFormat("http://{0}:{1}/api/configuration?process-name={2}");
    private static final MessageFormat REGISTER_URL = new MessageFormat("http://{0}:{1}/api/register?uniqueProcessName={2}");
    private static final MessageFormat RENEW_LEASE_URL = new MessageFormat("http://{0}:{1}/api/renewLease?levelInstanceId={2,number,#}");
    private static final Logger LOGGER = LoggerFactory.getLogger(CentralConfigurationUtil.class);
    private static String uniqueProcName = null;
    private HttpClient httpClient = ApacheHttpClientFactory.createHttpClient("ccp-lib");

    /**
     * return the unique process name that is composed of the following parts: <br>
     * 1. FQDN (Fully qualified domain name) <br>
     * 2. name <br>
     * 3. version <br>
     * 4. install path
     *
     * @return eturn the unique process name
     */
    public static String getUniqueProcessName() {

        String rpmSoftwareName = System.getenv(CcpConstants.RPM_SOFTWARE_NAME);

        if (rpmSoftwareName == null) {
            rpmSoftwareName = System.getenv(CcpConstants.ARTIFACT_NAME);
        }

        if (rpmSoftwareName == null) {
            throw new IllegalArgumentException(CcpConstants.RPM_SOFTWARE_NAME + " environment variable is mandatory when CCP is enabled");
        }

        if (CcpConstants.VCS_CONSOLE.equals(rpmSoftwareName)) {
            return getProcNameforVCSConsoleModule();
        }

        return getProcNameforNonVCSConsoleModule(rpmSoftwareName);

    }

    /**
     * special support for nds console ui modules. the methods looks for the
     * ccpConfig.xml in the classes folder of the ui module. from there it
     * extracts the real name for the unique process name computation. without
     * the above all the ui modules will have "ndsconsole' as their name.
     *
     * @return
     */
    private static String getProcNameforVCSConsoleModule() {
        String webUniqueProcName = null;

        String artifactName = null;
        String artifactVersion = null;
        BufferedReader reader = null;

        URL ccpConfigFileResource = CentralConfigurationUtil.class.getResource(CcpConstants.CCP_CONFIG_FILE);

        if (ccpConfigFileResource == null) {
            throw new IllegalArgumentException("for ndsconsole or one of its modules ccpConfig.xml must be in the module classpath");
        }

        LOGGER.debug("processing the ccp file: " + ccpConfigFileResource.getFile());

        try {

            InputStream ccpConfigFileStream = ccpConfigFileResource.openStream();
            reader = new BufferedReader(new InputStreamReader(ccpConfigFileStream));

            String line = reader.readLine();
            boolean stop = false;

            while (line != null && !stop) {
                if (line.contains("NamespaceIdentifier")) {
                    int nameIndex = line.indexOf("name=\"") + 6;
                    int versionIndex = line.indexOf("version=\"") + 9;

                    artifactName = line.substring(nameIndex, line.indexOf("\"", nameIndex));
                    artifactVersion = line.substring(versionIndex, line.indexOf("\"", versionIndex));

                    LOGGER.debug("Setting the artifact name and version of {} and {}", artifactName, artifactVersion);

                    stop = true;
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("CCP is enabled, but can't load the system as unique process name is unknown. Error is: " + e, e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                // ignore
            }
        }

        File ccpConfigFile = new File(ccpConfigFileResource.getFile());
        File currentFile = ccpConfigFile;
        File parentFile = ccpConfigFile.getParentFile();
        String artifactInstallDir = null;

        boolean stop = false;

        // walk up the tree till you find the correct install path
        while (parentFile != null && parentFile.exists() && !stop) {
            if (CcpConstants.BASE_INSTALL_PATH.equals(parentFile.getPath())) {
                artifactInstallDir = currentFile.getPath();
                stop = true;
            }
            currentFile = parentFile;
            parentFile = parentFile.getParentFile();
        }

        if (artifactInstallDir == null) {
            throw new IllegalArgumentException("Can't calculate the correct install path based on the ccpConfigFile location: " + ccpConfigFile.getPath());
        }

        webUniqueProcName = System.getenv(CcpConstants.FQDN);
        webUniqueProcName += "-" + artifactName;
        webUniqueProcName += "-" + artifactVersion;
        webUniqueProcName += "-" + artifactInstallDir.replaceAll("/", "_");

        LOGGER.debug("web module unique proc name is: " + webUniqueProcName);

        return webUniqueProcName;
    }

    private static String getProcNameforNonVCSConsoleModule(String rpmSoftwareName) {
        if (uniqueProcName == null) {
            uniqueProcName = System.getenv(CcpConstants.FQDN);
            uniqueProcName += "-" + rpmSoftwareName;
            uniqueProcName += "-" + System.getenv(CcpConstants.ARTIFACT_VERSION);
            uniqueProcName += "-" + System.getenv(CcpConstants.INSTALL_DIR).replaceAll("/", "_");
        }

        return uniqueProcName;
    }

    public static String getCcpBackupFileName() {
        String backupDir = System.getenv(CCP_BACKUP_DIR);

        if (StringUtils.isBlank(backupDir)) {
            backupDir = System.getProperty("user.dir");
        }


        if (backupDir.endsWith("etc")) {
            backupDir = backupDir + "/../docs/ccp";
        } else {
            backupDir = backupDir + "/docs/ccp";
        }

        return backupDir;
    }

    /**
     * for non CCP ENabled mode load the flat properties from the
     * configSchema.xml file.
     *
     * @param props         - the properties to load into.
     * @param is            - the input stream of the currently processed config file.
     * @param baseMap       - a map holding all the "template parameters" (i.e.
     *                      ParameterType in the xml file)
     * @param childMap      - a map holding all the parameters that point to a template
     *                      using the "base" attribute.
     * @param configuration the composite configuration
     */
    void loadPropertiesFromXml(Map<String, String> props, InputStream is, Map<String, ParameterType> baseMap, Map<String, Parameter> childMap, CompositeConfiguration configuration) {

        String xml = new Scanner(is).useDelimiter("\\Z").next();

        try {

            NamespaceDefinitionMessage namespaceDefinitionMessage = new NamespaceDefinitionMessage(xml);

            List<NamespaceDefinition> namespaceDefinitions = namespaceDefinitionMessage.jaxb().getNamespaceDefinitions();

            loadProps(props, namespaceDefinitions, baseMap, childMap, configuration);

        } catch (XmlException e) {
            throw new IllegalArgumentException("cannot parse default config xml file.", e);
        }
    }

    private void loadProps(Map<String, String> props, List<NamespaceDefinition> namespaceDefinitions, Map<String, ParameterType> baseMap, Map<String, Parameter> childMap, CompositeConfiguration configuration) {

        if (namespaceDefinitions.isEmpty()) {
            throw new UnsupportedOperationException("the configSchema.xml file must contain one name space defiinition");
        } else {
            // we only need to read the first namespace (should be only onw any
            // way)
            NamespaceDefinition namespaceDefinition = namespaceDefinitions.get(0);

            loadPropsFromNamespaceDefinition(props, baseMap, childMap, namespaceDefinition, configuration);
        }

    }

    /**
     * load the flat properties from a single namespace definition
     *
     * @param props               - the properties to load into.
     * @param baseMap             - a map holding all the "template parameters" (i.e.
     *                            ParameterType in the xml file)
     * @param childMap            - a map holding all the parameters that point to a template
     *                            using the "base" attribute.
     * @param namespaceDefinition - the root namespace - the source for the parameters to be
     *                            parsed.
     * @param configuration       the composite configuration
     */
    private void loadPropsFromNamespaceDefinition(Map<String, String> props, Map<String, ParameterType> baseMap, Map<String, Parameter> childMap, NamespaceDefinition namespaceDefinition, CompositeConfiguration configuration) {

        List<Parameter> parameters = namespaceDefinition.getParameters();

        List<ParameterType> parameterTypes = namespaceDefinition.getParameterTypes();

        // first populate the base map
        for (ParameterType parameterType : parameterTypes) {
            baseMap.put(parameterType.getName(), parameterType);
        }

        // now load the actual parameters from the "regular" parameter elements
        for (Parameter parameter : parameters) {

            parameterMap.put(parameter.getName(), parameter);
            loadPropsFromParameter(props, childMap, parameter, configuration);

        }
    }

    /**
     * update the properties object based on a single parameter. we need to
     * support both structure and primitive parameters.
     *
     * @param props         - the properties to load into.
     * @param childMap      - a map holding all the parameters that point to a template
     *                      using the "base" attribute.
     * @param configuration a set that contains a set of all the flat members of any given
     *                      structure array
     */
    public void loadPropsFromParameter(Map<String, String> props, Map<String, Parameter> childMap, Parameter parameter, CompositeConfiguration configuration) {

        String key = parameter.getName();

        // first time we add the child parameter to the map and return. we will
        // later on return back here for actually adding the relevant properties
        // into the map.
        if (StringUtils.isNotEmpty(parameter.getBase())) {
            childMap.put(key, parameter);
            return;
        }

        // special support for structures that have default valued in their
        // structure definition
        if (ParameterKind.STRUCTURE.equals(parameter.getType())) {
            updatePropFromStructureDefaults(props, parameter);
        }

        ParameterValue defaultValue = parameter.getDefaultValue();

        // there may not be a default value - if it is required and must be
        // override
        if (defaultValue != null) {

            if (ParameterKind.STRUCTURE.equals(parameter.getType())) {

                List<StructureValue> structureValues = defaultValue.getStructureValues();
                fillFromStructure(props, parameter, structureValues, configuration);

            } else {
                List<PrimitiveValue> primitiveValues = defaultValue.getPrimitiveValues();
                updateProps(props, parameter, key, primitiveValues, configuration);

            }
        }
    }

    // update properties where we have default values defined in the structure
    // member definitions themselves
    private void updatePropFromStructureDefaults(Map<String, String> props, Parameter parameter) {

        StructureDefinition structureDefinition = parameter.getStructureDefinition();

        if (structureDefinition != null && !parameter.isIsArray()) {

            List<StructureMemberDefinition> structureMemberDefinitions = structureDefinition.getStructureMemberDefinitions();

            for (StructureMemberDefinition structureMemberDefinition : structureMemberDefinitions) {

                // if the structure member is not of STRUCTURE type
                if (!ParameterKind.STRUCTURE.equals(structureMemberDefinition.getType())) {

                    // assuming there is a default value - if not - nothing to
                    // do here
                    if (structureMemberDefinition.getDefaultValue() != null) {

                        String key = parameter.getName();

                        // only if users do not want to ignore the structure
                        // name will we append it to the key
                        if (!structureMemberDefinition.isIgnoreName()) {
                            key += "." + structureMemberDefinition.getName();
                        }

                        ParameterValue defaultValue = structureMemberDefinition.getDefaultValue();

                        if (defaultValue.getPrimitiveValues() != null) {

                            // handle a case where we only have one entry to
                            // update
                            if (defaultValue.getPrimitiveValues().size() == 1) {
                                props.put(key, defaultValue.getPrimitiveValues().get(0).getValue());
                            } else {
                                for (PrimitiveValue primitiveValue : defaultValue.getPrimitiveValues()) {
                                    String index = primitiveValue.getIndex();
                                    props.put(key + "." + index, primitiveValue.getValue());
                                }
                            }
                        }

                    }
                } else {
                    // the structure member in itself is also of STRUCTURE type
                    StructureDefinition innerStructureDefinition = structureMemberDefinition.getStructureDefinition();

                    if (innerStructureDefinition != null) {

                        List<StructureMemberDefinition> innerStructureMemberDefinitions = innerStructureDefinition.getStructureMemberDefinitions();

                        // loop over all the internal structure definition and
                        // allocate and possibly define default values
                        for (StructureMemberDefinition innerStructureMemberDefinition : innerStructureMemberDefinitions) {

                            // assuming there is a default value assigned
                            if (innerStructureMemberDefinition.getDefaultValue() != null) {

                                String key = parameter.getName();

                                // only if users do not want ot ignore the
                                // structure name will we append it to the key
                                if (!structureMemberDefinition.isIgnoreName()) {
                                    key += "." + structureMemberDefinition.getName();
                                }

                                ParameterValue defaultValue = structureMemberDefinition.getDefaultValue();

                                // we only support internal structures here as
                                // the type is STRUCTURE
                                if (defaultValue != null && defaultValue.getStructureValues() != null) {

                                    // only single entry
                                    if (defaultValue.getStructureValues().size() == 1) {

                                        StructureValue structureValue = defaultValue.getStructureValues().get(0);
                                        updatePropsFromStructureMemberValue(props, key, structureValue);

                                    } else {
                                        for (StructureValue structureValue : defaultValue.getStructureValues()) {
                                            updatePropsFromStructureMemberValue(props, key + "." + structureValue.getIndex(), structureValue);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * update the properties object based on a single structure value
     *
     * @param props          - the properties object ot update
     * @param key            - the base key
     * @param structureValue - the actual root structure value
     */
    public void updatePropsFromStructureMemberValue(Map<String, String> props, String key, StructureValue structureValue) {

        List<StructureMemberValue> structureMemberValues = structureValue.getStructureMemberValues();

        for (StructureMemberValue structureMemberValue : structureMemberValues) {

            String newKey = key + structureMemberValue.getName();
            props.put(newKey, structureMemberValue.getValue());
        }
    }

    private void fillFromStructure(Map<String, String> props, Parameter parameter, List<StructureValue> structureValues, CompositeConfiguration configuration) {

        Map<String, String> tempProps = new HashMap<String, String>();

        // save all the parameters that wish to ignore the structure name in
        // resolved key.
        // this is useful so we do not need to loop over all the parameters
        // later on
        // this is relevant here as the ignoreName is set on the member
        // definition but we loop over the structure values here and not the
        // definition
        Map<String, Boolean> ignoreNamesMap = createIgnoreNamesMap(parameter);

        String key = parameter.getName();

        // loop over all structure values

        for (StructureValue structureValue : structureValues) {
            List<StructureMemberValue> structureMemberValues = structureValue.getStructureMemberValues();

            // loop over all the structure member values
            for (StructureMemberValue structureMemberValue : structureMemberValues) {

                String structKey = key;

                // if this an array - append the index to the key
                if (structureValue.getIndex() != null) {
                    structKey = key + "." + structureValue.getIndex();
                }

                // if users do not want the structure name as part of the
                // resolved key - then we do not append it
                Boolean ignoreName = ignoreNamesMap.get(structureMemberValue.getName());
                if (ignoreName == null || (ignoreName != null && Boolean.FALSE.equals(ignoreName))) {

                    structKey = structKey + "." + structureMemberValue.getName();
                }

                if (structureMemberValue.getIndex() != null) {
                    structKey = structKey + "." + structureMemberValue.getIndex();
                }

                String value = structureMemberValue.getValue();

                // handle nested structure
                if (value == null) {

                    List<StructureValue> innerStructureValues = structureMemberValue.getStructureValues();

                    // loop over internal structures. this is where we support
                    // on level of internal structures and arrays
                    for (StructureValue innerStructureValue : innerStructureValues) {

                        String index = innerStructureValue.getIndex();

                        List<StructureMemberValue> innerStructureMemberValues = innerStructureValue.getStructureMemberValues();

                        for (StructureMemberValue innerStructureMemberValue : innerStructureMemberValues) {
                            String innerStructName = innerStructureMemberValue.getName();
                            String tempKey = structKey;
                            if (index != null) {
                                tempKey = structKey + "." + index;
                            }
                            ignoreName = ignoreNamesMap.get(innerStructName);
                            if (ignoreName == null || (ignoreName != null && Boolean.FALSE.equals(ignoreName))) {
                                tempKey += "." + innerStructName;
                            }
                            String innerStructValue = innerStructureMemberValue.getValue();

                            if (innerStructValue == null) {
                                List<StructureValue> structureValues2 = innerStructureMemberValue.getStructureValues();
                                for (StructureValue structureValue2 : structureValues2) {
                                    String index2 = structureValue2.getIndex();
                                    String tempKey2 = tempKey;
                                    if (index2 != null) {
                                        tempKey2 += "." + index2;
                                    }
                                    List<StructureMemberValue> structureMemberValues2 = structureValue2.getStructureMemberValues();
                                    for (StructureMemberValue structureMemberValue2 : structureMemberValues2) {
                                        String tempKey3 = structureMemberValue2.getName();
                                        tempKey3 = tempKey2 + "." + tempKey3;
                                        tempProps.put(tempKey3, structureMemberValue2.getValue());
                                    }
                                }
                            } else {
                                tempProps.put(tempKey, innerStructValue);
                            }

                        }

                    }
                } else {
                    // the value was not null - we have a regular structure
                    // value - not nested structure arrays
                    tempProps.put(structKey, value);
                }

            }
        }

        boolean override = true;
        Set<String> keys = tempProps.keySet();
        for (String tempKey : keys) {
            if (configuration.containsKey(tempKey.toString())) {
                override = false;
                break;
            }
        }

        if (override) {
            props.putAll(tempProps);
        }

    }

    private Map<String, Boolean> createIgnoreNamesMap(Parameter parameter) {

        Map<String, Boolean> ignoreNamesMap = new HashMap<String, Boolean>();

        StructureDefinition structureDefinition = parameter.getStructureDefinition();

        if (structureDefinition != null) {

            List<StructureMemberDefinition> structureMemberDefinitions = structureDefinition.getStructureMemberDefinitions();

            for (StructureMemberDefinition structureMemberDefinition : structureMemberDefinitions) {

                boolean ignoreName = structureMemberDefinition.isIgnoreName();
                ignoreNamesMap.put(structureMemberDefinition.getName(), ignoreName);

                if (structureMemberDefinition.getStructureDefinition() != null) {

                    List<StructureMemberDefinition> innerStructureMemberDefinitions = structureMemberDefinition.getStructureDefinition().getStructureMemberDefinitions();

                    for (StructureMemberDefinition innerStructureMemberDefinition : innerStructureMemberDefinitions) {
                        ignoreName = innerStructureMemberDefinition.isIgnoreName();
                        ignoreNamesMap.put(innerStructureMemberDefinition.getName(), ignoreName);
                    }
                }
            }
        }

        return ignoreNamesMap;
    }

    private void updateProps(Map<String, String> props, Parameter parameter, String key, List<PrimitiveValue> primitiveValues, Configuration configuration) {

        if (primitiveValues.size() == 1 && !parameter.isIsArray()) {
            // only one value - no array
            props.put(key, primitiveValues.get(0).getValue());

        } else {

            for (PrimitiveValue primitiveValue : primitiveValues) {
                String arrayKey = key + "." + primitiveValue.getIndex();
                if (configuration.containsKey(arrayKey)) {
                    // if we have overrides for this array we do use the array
                    // from the confgiSchema.xml file.
                    break;
                }
                props.put(arrayKey, primitiveValue.getValue());
            }

        }
    }

    /**
     * used by commons config loader to load configuration from CCP data source
     *
     * @param configuration            - the configuration object to update
     * @param enablesDynamicSupportSet
     */
    public void loadCentralConfiguration(CompositeConfiguration configuration, Map<String, String> descriptionMap, Set<String> enablesDynamicSupportSet) {

        String uniqeProcessName = getUniqueProcessName();
        if (StringUtils.isBlank(uniqeProcessName)) {
            throw new IllegalArgumentException("Central configuraiotn is enabled but the properties for CCP were not filled. Please check if you are using the latest GIS.");
        }

        try {
            if (StringUtils.isNotBlank(System.getenv(CcpConstants.CCP_SERVER))) {
                loadFromHTTP(configuration, descriptionMap, enablesDynamicSupportSet, uniqeProcessName);
            }
        } catch (Exception e) {
            LOGGER.error("Error reading central configuration from DB or Server. Reading from fallback file instead. Error is: " + e.toString(), e);
            loadFromFallbackPropertyFile(configuration);
        }

    }

    private void loadFromHTTP(CompositeConfiguration configuration, Map<String, String> descriptionMap, Set<String> enablesDynamicSupportSet, String uniqeProcessName) {

        ConfigurationResponse configurationResponse = getConfigurationResponseViaHttp(uniqeProcessName);

        List<NamespaceParameters> namespaceParameters = configurationResponse.getNamespaceParameters();
        updateCentralConfigFromNamespaceParameters(configuration, namespaceParameters, descriptionMap, enablesDynamicSupportSet);

    }

    /**
     * get the configuration response VIA Http including HA support. This API
     * relies on the availability of hot and port pairs in the _CCP_SERVER
     * environment variable.
     *
     * @return configuration response
     */
    public ConfigurationResponse getConfigurationResponseViaHttp() {
        return getConfigurationResponseViaHttp(getUniqueProcessName());
    }

    /**
     * get the configuration response VIA Http including HA support. This API
     * relies on the availability of hot and port pairs in the _CCP_SERVER
     * environment variable.
     *
     * @param uniqeProcessName - the unique process name for this process
     * @return configuration response
     */
    public ConfigurationResponse getConfigurationResponseViaHttp(String uniqeProcessName) {

        loadHttpMetadata();

        ConfigurationResponse configurationResponse = null;

        HttpRequest httpRequest = HttpRequest.newBuilder().httpMethod(HttpMethod.GET).uri("/api/configuration?process-name=" + uniqeProcessName).build();
        HttpResponse httpResponse = httpClient.executeWithLoadBalancer(httpRequest);

        int statusCode = httpResponse.getStatus();

        // read the response
        String xml = httpResponse.getResponseAsString();

        // if all is well
        if (statusCode == 200) {
            ConfigurationResponseMessage message;
            try {
                message = new ConfigurationResponseMessage(xml);
                configurationResponse = message.jaxb();
            } catch (XmlException e) {
                throw new IllegalArgumentException("cannot process http get message.", e);
            }
        } else {
            // if we don't get a good response should fall back to the
            // property file.
            throw new IllegalArgumentException("received an invalid response. error code is: " + statusCode + ". response is: " + xml);
        }


        // if we couldn't get a response from any server, throw an error
        if (configurationResponse == null) {
            throw new IllegalArgumentException("couldn't find any available ccp server to get the configuration.");
        }

        return configurationResponse;
    }

//    public Pair<HttpResponse, Pair<String, Integer>> executeHttpRequest(String methodType, String url, String body) throws NoHttpResponseException {
//
//        loadHttpMetadata();
//
//        HttpResponse response = null;
//
//        HttpRequestBase httpRequestBase = null;
//
//        Pair<String, Integer> currentHostPort = null;
//
//        // loop over all available host and port pairs. is the first pair
//        // succeeds we do not progress to the next pair.
//        for (Pair<String, Integer> hostPort : httpclients) {
//
//            currentHostPort = hostPort;
//
//            if (HttpGet.METHOD_NAME.equalsIgnoreCase(methodType)) {
//                httpRequestBase = new HttpGet(String.format(url, hostPort.key, hostPort.value.toString()));
//            } else if (HttpPost.METHOD_NAME.equalsIgnoreCase(methodType)) {
//                httpRequestBase = new HttpPost(String.format(url, hostPort.key, hostPort.value.toString()));
//                HttpPost httpPost = (HttpPost) httpRequestBase;
//                if (StringUtils.isNotEmpty(body)) {
//                    try {
//                        StringEntity entity = new StringEntity(body, HTTP.UTF_8);
//                        entity.setContentType("text/xml");
//                        httpPost.setEntity(entity);
//                    } catch (UnsupportedEncodingException e) {
//                        throw new IllegalArgumentException("cannot execute http post method as the content is not un UTF-8 format");
//                    }
//                }
//            } else {
//                throw new IllegalArgumentException("invoked with illegal method type. method typed requested is" + methodType);
//            }
//
//            try {
//
//                response = httpClient.execute(httpRequestBase);
//                break;
//            } catch (ClientProtocolException e) {
//                throw new IllegalArgumentException("cannot process http get message.", e);
//            } catch (IOException e) {
//                // if we get an IOException, log the error and continue to the
//                // next pair in the loop
//                LOGGER.warn("cannot process http method. probably server is down. moving to the next server if exists.", e);
//            }
//
//        }
//
//        if (response == null) {
//            throw new NoHttpResponseException("could not access any available server.");
//        }
//
//        return new Pair<HttpResponse, Pair<String, Integer>>(response, currentHostPort);
//    }

    public void loadHttpMetadata() {
        // first time init the host and port pairs after parsing from the
        // environemnt variable
//        if (httpclients.isEmpty()) {

            // parse the host & port pairs
            String ccpServerData = System.getenv(CcpConstants.CCP_SERVER);

            // validate the string matches the expected pattern:
            // "host:port;host:port..."
            if (StringUtils.isEmpty(ccpServerData)) {
                throw new IllegalArgumentException("the CCP_SERVER environment variable does not have a legal host port pair. The value read is: " + ccpServerData);
            }

            if (!ccpServerData.matches(CcpConstants.CCP_SERVER__HOST_REG_EX) && !ccpServerData.matches(CcpConstants.CCP_SERVER__IPV6_REG_EX)) {
                throw new IllegalArgumentException("the CCP_SERVER environment variable does not have a legal host port pair. The value read is: " + ccpServerData);
            }

            String[] hostPortPairs = ccpServerData.split(";");

        int numOfServers = 0;
            for (String hostPortString : hostPortPairs) {
                numOfServers++;
                int index = hostPortString.lastIndexOf(":");
                String host = hostPortString.substring(0, index);
                String port = hostPortString.substring(index + 1);
                ConfigurationFactory.getConfiguration().setProperty("ccp-lib." + numOfServers + ".host", host);
                ConfigurationFactory.getConfiguration().setProperty("ccp-lib." + numOfServers + ".port", port);
//                Pair<String, Integer> pair = new Pair<String, Integer>(host, Integer.parseInt(port));
//                httpclients.add(pair);
            }

//        }
    }

    /**
     * @param configuration
     * @param namespaceParameters
     * @param descriptionMap
     * @param enablesDynamicSupportSet
     * @param enablesDynamicSupportSet
     */
    private void updateCentralConfigFromNamespaceParameters(CompositeConfiguration configuration, List<NamespaceParameters> namespaceParameters, Map<String, String> descriptionMap, Set<String> enablesDynamicSupportSet) {

        Map<String, String> configMap = new HashMap<String, String>();

        for (NamespaceParameters namespaceParameter : namespaceParameters) {
            List<Parameter> parameters = namespaceParameter.getParameters();
            for (Parameter parameter : parameters) {
                String key = parameter.getName();
                String description = parameter.getDescription();

                ParameterValue value = parameter.getValue();
                if (value != null) {
                    List<PrimitiveValue> valueList = value.getPrimitiveValues();
                    boolean enablesDynamicSupport = false;
                    if (!parameter.isRequiresRestart()) {
                        enablesDynamicSupport = true;
                    }
                    if (valueList.size() == 1 && !parameter.isIsArray()) {
                        // only one value - no array
                        configMap.put(key, StringEscapeUtils.unescapeXml(valueList.get(0).getValue()));
                        descriptionMap.put(key, description);
                        if (enablesDynamicSupport) {
                            enablesDynamicSupportSet.add(key);
                        }

                    } else {

                        // handle primitive arrays
                        for (PrimitiveValue primitiveValue : valueList) {

                            key = parameter.getName() + "." + primitiveValue.getIndex();
                            descriptionMap.put(key, description);
                            configMap.put(key, StringEscapeUtils.unescapeXml(primitiveValue.getValue()));
                            if (enablesDynamicSupport) {
                                enablesDynamicSupportSet.add(key);
                            }

                        }

                    }
                    List<StructureValue> strucutValueList = value.getStructureValues();
                    // handle structure arrays
                    updateCentralConfigWithStructure(configMap, descriptionMap, parameter.getName(), strucutValueList, description, enablesDynamicSupport, enablesDynamicSupportSet);

                }
            }

        }
        MapConfiguration mapConfiguration = new MapConfiguration(configMap);
        configuration.addConfiguration(mapConfiguration);
    }

    private void updateCentralConfigWithStructure(Map<String, String> configMap, Map<String, String> descriptionMap, String keyPrefix, List<StructureValue> strucutValueList, String description, boolean enablesDynamicSupport, Set<String> enablesDynamicSupportSet) {


        for (StructureValue structureValue : strucutValueList) {

            List<StructureMemberValue> structureMemberValues = structureValue.getStructureMemberValues();

            for (StructureMemberValue structureMemberValue : structureMemberValues) {
                String key = keyPrefix + "." + (structureValue.getIndex() != null ? (structureValue.getIndex() + ".") : "") + structureMemberValue.getName();
                if (StringUtils.isNotEmpty(structureMemberValue.getIndex())) {
                    key += "." + structureMemberValue.getIndex();
                }
                descriptionMap.put(key, description);

                if (null != structureMemberValue.getStructureValues() && structureMemberValue.getStructureValues().size() > 0) {
                    updateCentralConfigWithStructure(configMap, descriptionMap, key, structureMemberValue.getStructureValues(), description, enablesDynamicSupport, enablesDynamicSupportSet);
                } else {
                    configMap.put(key, StringEscapeUtils.unescapeXml(structureMemberValue.getValue()));
                    if (enablesDynamicSupport) {
                        enablesDynamicSupportSet.add(key);
                    }
                }
            }

        }

    }

    /**
     * @param configuration
     */
    private void loadFromFallbackPropertyFile(CompositeConfiguration configuration) {
        String backupDir = System.getProperty("user.dir");

        if (backupDir.endsWith("etc")) {
            backupDir = backupDir + "/../docs/ccp";
        } else {
            backupDir = backupDir + "/docs/ccp";
        }

        File fallbackFile = new File(backupDir + CCP_BACKUP_FILE_NAME);

        if (fallbackFile.exists()) {
            LOGGER.info("Problems in DB load, using fallback file: \"" + fallbackFile.getPath() + "\" instead.");
            PropertiesConfiguration propertiesConfiguration = null;
            try {
                propertiesConfiguration = new PropertiesConfiguration(fallbackFile);
            } catch (ConfigurationException e) {
                throw new IllegalArgumentException("cannot fallback to proeprty file!", e);
            }
            configuration.addConfiguration(propertiesConfiguration);
        } else {
            throw new IllegalArgumentException("cannot fallback to proeprty file, as the file: \"" + fallbackFile.getPath() + "\" does not exist.");
        }
    }

//    public String registerServerWithCCP() {
//
//        String registerResponse = null;
//
//        loadHttpMetadata();
//
//        // loop over all available host and port pairs. is the first pair
//        // succeeds we do not progress to the next pair.
//        for (Pair<String, Integer> hostPort : httpclients) {
//            HttpGet httpGet = new HttpGet(REGISTER_URL.format(new Object[]{hostPort.key, hostPort.value.toString(), getUniqueProcessName()}));
//            try {
//
//                HttpResponse response = httpClient.execute(httpGet);
//
//                // read the status code
//                StatusLine statusLine = response.getStatusLine();
//                int statusCode = statusLine.getStatusCode();
//
//                HttpEntity entity = response.getEntity();
//
//                InputStream content = entity.getContent();
//
//                // read the response
//                registerResponse = new Scanner(content).useDelimiter("\\Z").next();
//
//                // release connection
//                EntityUtils.consume(entity);
//
//                // if all is well
//                if (statusCode == 200) {
//                    break;
//                } else {
//                    // if we don't get a good response should fall back to the
//                    // property file.
//                    throw new IllegalArgumentException("received an invalid response. error code is: " + statusCode + ". response is: " + registerResponse);
//                }
//
//            } catch (ClientProtocolException e) {
//                throw new IllegalArgumentException("cannot process http get message.", e);
//            } catch (IOException e) {
//                // if we get an IOException, log the error and continue to the
//                // next pair in the loop
//                LOGGER.warn("cannot process http get method. probalby server is down. moving to the next server if exists.", e);
//            }
//
//        }
//        return registerResponse;
//    }

//    public void renewLeaseWithCCP(int levelInstanceId) {
//
//        loadHttpMetadata();
//
//        // loop over all available host and port pairs. is the first pair
//        // succeeds we do not progress to the next pair.
//        for (Pair<String, Integer> hostPort : httpclients) {
//            HttpGet httpGet = new HttpGet(RENEW_LEASE_URL.format(new Object[]{hostPort.key, hostPort.value.toString(), levelInstanceId}));
//            try {
//
//                HttpResponse response = httpClient.execute(httpGet);
//
//                // release connection
//                EntityUtils.consume(response.getEntity());
//
//                // read the status code
//                StatusLine statusLine = response.getStatusLine();
//                int statusCode = statusLine.getStatusCode();
//
//                // if all is well
//                if (statusCode == 200) {
//                    break;
//                } else {
//                    // if we don't get a good response should fall back to the
//                    // property file.
//                    throw new IllegalArgumentException("received an invalid response. error code is: " + statusCode);
//                }
//
//            } catch (ClientProtocolException e) {
//                throw new IllegalArgumentException("cannot process http get message.", e);
//            } catch (IOException e) {
//                // if we get an IOException, log the error and continue to the
//                // next pair in the loop
//                LOGGER.warn("cannot process http get method. probalby server is down. moving to the next server if exists.", e);
//            }
//
//        }
//    }

}