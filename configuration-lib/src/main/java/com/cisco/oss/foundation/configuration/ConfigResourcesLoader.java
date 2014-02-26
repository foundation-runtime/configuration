package com.cisco.oss.foundation.configuration;

import com.cisco.oss.foundation.logging.ApplicationState;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * returns a list of resources that could be used:
 * <p/>
 * By Spring - PropertyPlaceholderConfigurer to replace the ${param} information
 * inside the spring xmls.
 * <p/>
 * By the Common configuration Loader in order to create a common configuration
 * object. This is a Dynamic Proxy that exposes itself as a List. it is an
 * external class and should not be constructed outside of CABConfiguration.
 * <p/>
 * The implementation of this class represents the 3 basic configuration layers:
 * <br>
 * 1. customer layer - represented by the file: config.proeprties. <br>
 * 2. deployment layer - represented by the file: deploymentConfig.properties. <br>
 * 3. factory layer - represented by the files: defaultConfig.properties. <br>
 * the precedence of the configuration files is from 1 to 3. customer is the
 * strongest and factory is the weakest.
 *
 * @author Joel Gurfinkel
 * @author Yair Ogen
 */
public class ConfigResourcesLoader implements FactoryBean<List<Resource>>, ApplicationContextAware, InitializingBean {

    /**
     *
     */
    private static final String CUSTOMER_CONFIG = "config.properties";
    /**
     *
     */
    private static final String CONFIGURATION_TEST_CLASS = "com.nds.cab.infra.test.ConfigurationForTest";
    /**
     *
     */
    private static final String TEST_CONFIG_FILE = "testConfigFile";
    private static final Logger LOGGER = Logger.getLogger(ConfigResourcesLoader.class);
    private static boolean LOAD_CONFIG_FROM_WORKING_DIR = Boolean.getBoolean("configuration.loadConfigFromWorkingDir");
    private static boolean printedToLog = false;
    private final List<Resource> resourcesList = new ArrayList<Resource>();
    private ApplicationContext context;
    private String internalPropertyConfig;
    private String internalXmlConfig;
    private List<String> customerPropertyConfig;
    private List<String> deploymentConfig;

    /**
     * set by injection the factory layer config file name.
     *
     * @param internalPropertyConfig the factory layer config file name.
     */
    public void setInternalPropertyConfig(final String internalPropertyConfig) {
        this.internalPropertyConfig = internalPropertyConfig;
    }

    /**
     * set by injection the factory layer config file name.
     *
     * @param internalXmlConfig the factory layer xml config file name.
     */
    public void setInternalXmlConfig(final String internalXmlConfig) {
        this.internalXmlConfig = internalXmlConfig;
    }

    /**
     * set by injection the list of all possible customer layer config file
     * names.
     *
     * @param customerConfig list of all possible customer layer config file names.
     */
    public void setCustomerConfig(final List<String> customerConfig) {
        this.customerPropertyConfig = customerConfig;
    }

    /**
     * set by injection the list of all possible deployment layer config file
     * names.
     *
     * @param deploymentConfig list of all possible deployment layer config file names.
     */
    public void setDeploymentConfig(final List<String> deploymentConfig) {
        this.deploymentConfig = deploymentConfig;
    }

    /**
     * return the list created by this dynamic proxy class. this is the list of
     * all the created resources of all configuration files from all
     * configuration layers.
     *
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    @Override
    public List<Resource> getObject() throws Exception { // NOPMD
        return resourcesList;
    }

    /**
     * return the exposed interface of this Dynamic Proxy.
     *
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    @Override
    @SuppressWarnings({"rawtypes"})
    public Class<List> getObjectType() {
        return List.class;
    }

    /**
     * this is a singleton.
     *
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * get by injection an application context. this is a callback method
     * invoked by spring.
     *
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(final ApplicationContext context) throws BeansException {
        this.context = context;
    }

    /**
     * fill the resourcesList only once. return it in the getObject method.
     */
    @Override
    public void afterPropertiesSet() throws Exception { // NOPMD

        final boolean isQCEnabld = isQCEnabled();

        // iterator over the list of possible external resources
        // only one of the resources should exist
        // this is to support special locations on special modules such as: Web
        // etc.
        Resource customerPropertyResource = null;

        // Resource customerXmlResource = null;

        Resource deploymentResource = null;

        Resource qcResource = null;

        if (isQCEnabld) {
            LOGGER.info("Found the ConfigurationTest class indicating that QC config is to be used instead of deployment and customer config files!");
            qcResource = validateQCResource();
        } else {
            customerPropertyResource = validateCustomerPropertyConfig();
            deploymentResource = validateDeploymentConfig();
            if (!printedToLog) {
                StringBuffer logMessageBuffer = new StringBuffer("The customer resources loaded are:");
                printResourcesLoaded(logMessageBuffer, customerPropertyResource);
                ApplicationState.setState(Level.INFO, logMessageBuffer.toString());

                if (deploymentResource != null) {
                    logMessageBuffer = new StringBuffer("The deployment resources loaded are:");
                    printResourcesLoaded(logMessageBuffer, deploymentResource);
                    ApplicationState.setState(Level.INFO, logMessageBuffer.toString());
                }

            }
        }
        // get all the resources of the internal property config files.
        final Resource[] internalPropertyResources = context.getResources(internalPropertyConfig);
        final List<Resource> internalPropertyResourcesList = Arrays.asList(internalPropertyResources);

        // get all the resources of the internal xml config files.
        // xml resources take precedence over the properties loaded.
        final Resource[] internalXmlResources = context.getResources(internalXmlConfig);
        final List<Resource> internalXmlResourcesList = Arrays.asList(internalXmlResources);

        if (!printedToLog) {
            final StringBuffer logMessageBuffer = new StringBuffer("The default resources loaded are:");
            printResourcesLoaded(logMessageBuffer, internalXmlResourcesList);
            printResourcesLoaded(logMessageBuffer, internalPropertyResourcesList);
            ApplicationState.setState(Level.INFO, logMessageBuffer.toString());
            printedToLog = true;
        }

        // order of the resources is important to maintain properly the
        // hierarchy of the configuration.

        resourcesList.addAll(internalPropertyResourcesList);

        // xml resources take precedence over the properties loaded.
        resourcesList.addAll(internalXmlResourcesList);

        if (deploymentResource != null) {
            resourcesList.add(deploymentResource);
        }

        if (customerPropertyResource != null) {
            resourcesList.add(customerPropertyResource);
        }

        // xml customer resource take precedence over the properties loaded.
        // if (customerXmlResource != null) {
        // resourcesList.add(customerXmlResource);
        // }

        if (qcResource != null) {
            resourcesList.add(qcResource);
        }

    }

    /**
     * @return
     */
    private Resource validateQCResource() {
        Resource qcResource = null;
        String qcFile = System.getProperty(TEST_CONFIG_FILE);

        if (StringUtils.isBlank(qcFile)) {
            // throw new
            // IllegalArgumentException("Application was started with the " +
            // CONFIGURATION_TEST_CLASS +
            // " in the class path, but the system property: " +
            // TEST_CONFIG_FILE + " was not found!");

            // in order to support tests that do not wish to set the test
            // property while other do, we fallback to the default
            // "config.proeprties" file in case the system property is empty.
            qcFile = CUSTOMER_CONFIG;

        }

        // validate the file name. it must contain a case less "test" word in
        // it.
        if (!qcFile.equals(CUSTOMER_CONFIG) && !qcFile.matches(".*(?i)test(?-i).*")) {
            throw new IllegalArgumentException("The test config file name is not valid. it must containt the word 'test' in it, but it does not. the file names processed is: " + qcFile);
        }

        LOGGER.debug("searching for the file name: " + qcFile);
        qcResource = new ClassPathResource(qcFile);
        return qcResource;
    }

    /**
     * @return
     */
    private boolean isQCEnabled() {
        boolean isQCEnabled = true;
        try {
            Class.forName(CONFIGURATION_TEST_CLASS);
        } catch (ClassNotFoundException e) {
            isQCEnabled = false;
        }
        return isQCEnabled;
    }

    private Resource validateCustomerPropertyConfig() {
        Resource customerResource = null;
        boolean isValid = false;

        for (String resource : customerPropertyConfig) {

            customerResource = context.getResource(resource);
            isValid = validateResourceIsValid(customerResource);
            if (isValid) {
                // the first valid resource will be used.
                break;
            }

        }

        if (Boolean.valueOf(System.getenv(CcpConstants.CCP_ENABLED))) {
            customerResource = null;
        } else if (!isValid && !customerResource.getFilename().contains("dummyConfig")) {

            if (LOAD_CONFIG_FROM_WORKING_DIR) {
                FileSystemResource fileSystemResource = new FileSystemResource("config.properties");
                isValid = validateResourceIsValid(fileSystemResource);
                if (!isValid) {
                    LOGGER.error("could not find the file: " + fileSystemResource.getFilename());
                    return null;
                } else {
                    customerResource = fileSystemResource;
                }
            } else {

                if (!"config.properties".equals(customerResource.getFilename())) {

                    customerResource = context.getResource("config.properties");

                    isValid = validateResourceIsValid(customerResource);
                    if (!isValid) {
                        LOGGER.error("could not find the file: " + customerResource.getFilename());
                        return null;
                    }

                } else {
                    LOGGER.error("could not find the file: " + customerResource.getFilename());
                    // for backward compatibility do not mandate this file.
                    return null;
                }
            }
        }
        return customerResource;
    }

    private Resource validateDeploymentConfig() {

        if (deploymentConfig == null) {
            return null;
        } else {
            Resource deploymentResource = null;
            boolean isValid = false;
            for (String resource : deploymentConfig) {

                deploymentResource = context.getResource(resource);
                isValid = validateResourceIsValid(deploymentResource);
                if (isValid) {
                    // the first valid resource will be used.
                    break;
                }

            }
            if (!isValid) {
                LOGGER.trace("Could not find the file: deploymentConfig.properties in the class path");
                return null;
            }
            return deploymentResource;
        }

    }

    /**
     * Make sure the URL is correct and exists. used to support different
     * locations for a resources (E.g. under WEB-INF or not etc.)
     *
     * @param externalResource the resource to check it's validity.
     * @return true if exists, false if not.
     */
    private boolean validateResourceIsValid(final Resource externalResource) {
        boolean isValid = true;

        try {
            URL url = externalResource.getURL();
            if (url.getPath() != null && url.getPath().contains(".jar") && !url.getPath().contains("dummyConfig")) {
                throw new IllegalArgumentException("The customer config file: \"config.properties\" was found in a jar file. This is not legal. Offending jar is: " + url.getPath());
            }
        } catch (IOException e) {
            isValid = false;
        }

        return isValid;
    }

    private void printResourcesLoaded(final StringBuffer logMessageBuffer, final Resource resource) throws IOException {
        if (resource == null) {
            LOGGER.debug("Loaded a null resource in the resources list");
        } else {
            logMessageBuffer.append('\n');
            logMessageBuffer.append(resource.getURL()).append(" config file loaded");
        }
    }

    private void printResourcesLoaded(final StringBuffer logMessageBuffer, final List<Resource> resourcesList) throws IOException {

        for (Resource resource : resourcesList) {
            printResourcesLoaded(logMessageBuffer, resource);
        }
    }

}
