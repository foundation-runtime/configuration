package com.cisco.vss.foundation.configuration.validation;

import com.cisco.vss.foundation.configuration.*;
import com.cisco.vss.foundation.configuration.validation.exceptions.GeneralConfigException;
import com.cisco.vss.foundation.configuration.validation.params.*;
import org.apache.commons.configuration.Configuration;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created By: kgreen
 * Date-Time: 10/3/13 9:01 AM
 */
public class BaseConfiguration implements FoundationConfigurationListener {

    /**
     * a listener to configuration changes
     */
    public static interface ConfigChangeListener {
        public void configurationChanged(HashSet<Param> changedParams) ;
    }
    //==================================================================================================================

    // indicate if to ignore static parameter and always reads parameters from the configuration (for JUnit tests usage)
    private static boolean enableStaticConfiguration = true;

    // holder of all parameters
    private final static ArrayList<Param> parameters = new ArrayList();

    // holder of configuration change listeners
    private final static CopyOnWriteArraySet<ConfigChangeListener> configChangeListeners = new CopyOnWriteArraySet<ConfigChangeListener>();

    protected Configuration configuration;
    //==================================================================================================================

    public void setConfiguration(Configuration configuration) {

        this.configuration = configuration;

        // init ParamReaders with the new <Configuration>
        ParamReaders.getInstance().setConfiguration(configuration);

        // register as listener to configuration change
        FoundationConfigurationListenerRegistry.addFoundationConfigurationListener(this);

        // verify that all configured values are correct
        verifyConfiguration();

    }
    //==================================================================================================================

    public Configuration getConfiguration() {
        return configuration;
    }
    //==================================================================================================================

    /**
     * verify that all configured values are correct
     */
    public void verifyConfiguration() {

        // get all defined parameters and read values to verify their correctness
        for (Param param : parameters) {
            readAndValidateParameter(param);
        }

    }
    //==================================================================================================================

    private void readAndValidateParameter (Param param) {

        try {
            param.readValue();
            param.validate();

        } catch (GeneralConfigException ex) {
            throw ex;

        } catch (Exception ex) {
            throw new GeneralConfigException("Configured value for '" + param.getName() + "' is invalid!", ex);
        }

    }
    //==================================================================================================================

    /**
     * add listener for the configuration change events
     * @param listener
     */
    public void addConfigurationChangeListener (ConfigChangeListener listener) {
        configChangeListeners.add(listener);
    }
    //==================================================================================================================

    /**
     * notified on reload configuration event
     */
    @Override
    public void configurationChanged() {

        HashSet<Param> changedParams = new HashSet();

        // read dynamic parameters
        for (Param param : parameters) {

            if (! param.isStatic()) {

                // preserve current value
                Object oldValue = param.getValue();

                // read new value
                readAndValidateParameter(param);

                // get if parameter value had changed
                if (! ConfigUtil.equalValues(oldValue, param.getValue())) {
                    changedParams.add(param);
                }
            }

        }

        // invoke listeners for all changed parameters
        if (! changedParams.isEmpty()) {

            for (ConfigChangeListener listener : configChangeListeners) {
                listener.configurationChanged(changedParams);
            }

        }

    }
    //==================================================================================================================

    public static boolean isEnableStaticConfiguration() {
        return enableStaticConfiguration;
    }

    public void setEnableStaticConfiguration(boolean enableStaticConfiguration) {
        BaseConfiguration.enableStaticConfiguration = enableStaticConfiguration;
    }
    //==================================================================================================================

    public static void addParameter (Param param) {
        parameters.add(param);
    }
    //==================================================================================================================

    /**
     * parameters builder helper methods
     */
    public static SimpleParamFactory staticParam() {
        return ParamFactory.staticFactory;
    }

    public static SimpleParamFactory dynamicParam() {
        return ParamFactory.dynamicFactory;
    }

    /**
     * factory for List parameters
     */
    public static ParamListFactory staticParamList() {
        return ParamFactory.listStaticFactory;
    }

    public static ParamListFactory dynamicParamList() {
        return ParamFactory.listDynamicFactory;
    }

    /**
     * factory for Set parameters
     */
    public static ParamSetFactory staticParamSet() {
        return ParamFactory.setStaticFactory;
    }

    public static ParamSetFactory dynamicParamSet() {
        return ParamFactory.setDynamicFactory;
    }
    //==================================================================================================================

}
