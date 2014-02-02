package com.cisco.vss.foundation.configuration;

import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * User: Yair Ogen Date: 4/18/13 Time: 4:53 PM
 */
public class CabCommonsConfigurationFactoryBean implements FactoryBean<Properties> {

	private Configuration configuration;

	public CabCommonsConfigurationFactoryBean(Configuration configuration) {
		Assert.notNull(configuration);
		this.configuration = configuration;
	}

	/**
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	public Properties getObject() throws Exception {
		return (configuration != null) ? convertToProperties(configuration) : null;
	}

	/**
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	public Class<Properties> getObjectType() {
		return Properties.class;
	}

	/**
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	public boolean isSingleton() {
		return true;
	}

	private Properties convertToProperties(Configuration configuration) {
		Properties props = new Properties();

		Iterator<String> keys = configuration.getKeys();

		while (keys.hasNext()) {
			String key = keys.next();
			if (configuration.containsKey(key)) {
				Object result = configuration.getProperty(key);
				String value = result.toString();
				if (result instanceof List<?>) {
					value = value.substring(1);
					value = value.substring(0,value.length()-1);
				}

				props.setProperty(key, value);
			}

		}

		return props;
	}
}
