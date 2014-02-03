package com.cisco.vss.foundation.configuration;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Assert;

/**
 * Factory to get a Configuration object that gets all the configuration files
 * in the system and makes them a single object.
 * 
 * This class can be used to work with the
 * <code>org.apache.commons.configuration.Configuration</code> without the need
 * to use Spring. It has 2 public API's to get all the configuration layers, or
 * just the default layer.
 * 
 * @see org.apache.commons.configuration.Configuration
 * @author Joel Gurfinkel
 * @author Yair Ogen
 * 
 */
public final class ConfigurationFactory {

	private static final Logger LOGGER = Logger.getLogger(ConfigurationFactory.class);

	private ConfigurationFactory() {
		// prevent instantiation.
	}

	private static volatile ApplicationContext context;

	/**
	 * get the full configuration object that contains all the configuration
	 * layers: customer, deployment and factory.
	 * 
	 * @return the configuration object
	 */
	public static Configuration getConfiguration() {
		return (Configuration) getContext().getBean("configuration");
	}

	/**
	 * get only a configuration that contains all the factory layer files.
	 * 
	 * @return a configuration that contains all the factory layer files.
	 */
	public static Configuration getDefaultConfiguration() {
		return (Configuration) getContext().getBean("defaultConfiguration");
	}

	private static ApplicationContext getContext() {
		if (context == null) {
			synchronized (ConfigurationFactory.class) {
				if (context == null) {
					context = new ClassPathXmlApplicationContext(new String[] {"META-INF/configurationContext.xml"});
				}
			}
		}
		Assert.notNull(context);
		return context;
	}

}
