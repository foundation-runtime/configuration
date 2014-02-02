package com.cisco.vss.foundation.configuration;

import com.cisco.vss.foundation.environment.utils.EnvUtils;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.HierarchicalConfiguration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Simple utility class useful for handling the common configuration framework.
 * 
 * @author Joel Gurfinkel
 * 
 */
public final class ConfigUtil {

	private ConfigUtil() {
		// prevent instantiation.
	}

	/**
	 * returns the hierarchical part of a configuration
	 * 
	 * @param configuration
	 *            the given configuration
	 * @return the hierarchical configuration or null if not found in the given
	 *         configuration object.
	 */
	public static HierarchicalConfiguration getHierarchicalConfiguration(final Configuration configuration) {
		if (configuration instanceof CompositeConfiguration) {
			final CompositeConfiguration compositeConfig = (CompositeConfiguration) configuration;
			for (int i = 0; i < compositeConfig.getNumberOfConfigurations(); i++) {
				if (compositeConfig.getConfiguration(i) instanceof HierarchicalConfiguration) {
					return (HierarchicalConfiguration) compositeConfig.getConfiguration(i);
				}
			}
		}
		// maybe I need to send a runtime exception ??
		// throw new
		// ConfigurationRuntimeException("no hierarchical configuration was defined");
		return null;
	}

	/**
	 * create a composite configuration that wraps the configuration sent by the
	 * user. this util will also load the "defaultConfig.properties" file loaded
	 * relative to the given "clazz" parameter
	 * 
	 * @param clazz
	 *            - the class that acts as referenced location of
	 *            "defaultConfig.properties".
	 * @param configuration
	 *            - the configuration supplied by the user that may override
	 *            values in "defaultConfig.properties". If null is ignored.
	 * @return the created compsite configuration.
	 */
	public static Configuration createCompositeConfiguration(final Class<?> clazz, final Configuration configuration) {
		final CompositeConfiguration compositeConfiguration = new CompositeConfiguration();
		final Configuration defaultConfiguration = ConfigurationFactory.getDefaultConfiguration();
		if (configuration != null) {
			compositeConfiguration.addConfiguration(configuration);
		}
		compositeConfiguration.addConfiguration(defaultConfiguration);
		return compositeConfiguration;
	}

	public static void enableCcp(String componentName, String componentVersion, String installPath, String compoentHostName, String ccpServerHostName) {
		enableCcp(componentName, componentVersion, installPath, compoentHostName, ccpServerHostName, 5670);
	}

	public static void enableCcp(String componentName, String componentVersion, String installPath, String compoentHostName, String ccpServerHostName, int ccpServerPort) {
		EnvUtils.updateEnv(CcpConstants.CCP_ENABLED, "true");
		EnvUtils.updateEnv(CcpConstants.FQDN, compoentHostName);
		EnvUtils.updateEnv(CcpConstants.RPM_SOFTWARE_NAME, compoentHostName);
		EnvUtils.updateEnv(CcpConstants.ARTIFACT_VERSION, componentVersion);
		EnvUtils.updateEnv(CcpConstants.INSTALL_DIR, installPath);
		EnvUtils.updateEnv(CcpConstants.CCP_SERVER, ccpServerHostName + ":" + ccpServerPort);
	}

	public static void disableCcp() {
		EnvUtils.updateEnv(CcpConstants.CCP_ENABLED, "false");
	}

	/**
	 * Parse a complex array structure and return it as a map containing maps as
	 * values for further internal structures <br>
	 * Example of configuration we support:
	 * smartcardAdaptor.cardFeatures.1.cardFamily=VGE
	 * smartcardAdaptor.cardFeatures.1.supportSbm=true
	 * smartcardAdaptor.cardFeatures.1.maxSbmBlocks=16
	 * smartcardAdaptor.cardFeatures.1.sbmMSB.0=0x00
	 * smartcardAdaptor.cardFeatures.1.sbmMSB.1=0x01
	 * smartcardAdaptor.cardFeatures.1.maxRegions=4
	 * smartcardAdaptor.cardFeatures.1.initialCounter=1000
	 * smartcardAdaptor.cardFeatures.1.maxNumOfOPPVs=25
	 * 
	 * smartcardAdaptor.cardFeatures.23.cardFamily=VGE
	 * smartcardAdaptor.cardFeatures.23.supportSbm=true
	 * smartcardAdaptor.cardFeatures.23.maxSbmBlocks=16
	 * smartcardAdaptor.cardFeatures.23.sbmMSB.0=0x00
	 * smartcardAdaptor.cardFeatures.23.sbmMSB.1=0x01
	 * smartcardAdaptor.cardFeatures.23.maxRegions=4
	 * smartcardAdaptor.cardFeatures.23.initialCounter=1000
	 * smartcardAdaptor.cardFeatures.23.maxNumOfOPPVs=25
	 * 
	 * <br>
	 * Will return a map where key is 1, 23 and values are the key/value pairs
	 * the follow the key. <br>
	 * E.g Map - key = 1, value= Map(cardFamily -> VGE, supportSbm ->true) etc.
	 */
	public static Map<String, Map<String, String>> parseComplexArrayStructure(String configPrefix) {

		Configuration configuration = ConfigurationFactory.getConfiguration();

		Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();

		// get a subset of the configuration based on the config prefix.
		final Configuration subset = configuration.subset(configPrefix);

		@SuppressWarnings("unchecked")
		final Iterator<String> keys = subset.getKeys();

		while (keys.hasNext()) {

			final String key = keys.next();
			final String mapKey = stripKey(key);

			// only if the map key is not null and contains the above saved
			// criteria key do we want to handle this.
			if (mapKey != null) {

				// first time only - create the inner map instance
				if (!result.containsKey(mapKey)) {
					result.put(mapKey, new HashMap<String, String>());
				}

				// get the inner map key value.
				final String innerMapKey = getInnerMapKey(key);

				// update the reply map format.
				result.get(mapKey).put(innerMapKey, subset.getString(key));

			}
		}

		return result;
	}
	
	/**
	 * parse an simple array and return it as a Map. Can be used in conjunction with {@code ConfigUtil#parseComplexArrayStructure(String)} 
	 * @param configPrefix the prefix that is the basis of the array. E.g.
	 * 
	 * given:
	 * 
	 * smartcardAdaptor.cardFeatures.23.sbmMSB.0=0x00
	 * smartcardAdaptor.cardFeatures.23.sbmMSB.1=0x01
	 * 
	 * the config prefix will be:
	 * smartcardAdaptor.cardFeatures.23.sbmMSB
	 * 
	 * @return the Map where the index is the key and the value is the value.
	 */
	public static Map<String, String> parseSimpleArrayAsMap(String configPrefix) {

		Configuration configuration = ConfigurationFactory.getConfiguration();

		Map<String, String> result = new HashMap<String, String>();

		// get a subset of the configuration based on the config prefix.
		final Configuration subset = configuration.subset(configPrefix);

		@SuppressWarnings("unchecked")		
		final Iterator<String> keys = subset.getKeys();

		while (keys.hasNext()) {

			final String key = keys.next();
			result.put(key, subset.getString(key));			
		}

		return result;
	}

	/**
	 * create inner key map by taking what ever is after the first dot.
	 * 
	 * @param key
	 * @return
	 */
	private static String getInnerMapKey(final String key) {
		final int index = key.indexOf(".");
		return key.substring(index + 1);
	}

	/**
	 * strip the original key by taking every thing from the start of the
	 * original key to the first dot.
	 * 
	 * @param key
	 * @return
	 */
	private static String stripKey(final String key) {
		int index = key.indexOf(".");
		if (index > 0) {
			return key.substring(0, index);
		}
		return null;
	}
}
