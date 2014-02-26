/**
 *
 */
package com.cisco.oss.foundation.configuration;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Properties;

/**
 * this extension to <code>org.springframework.beans.factory.config.PropertyPlaceholderConfigurer</code> is used to trim values read from the configuration file.
 *
 * @author Yair Ogen
 * @see org.springframework.beans.factory.config.PropertyPlaceholderConfigurer
 */
public class InfraPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
	
	private static final Logger LOGGER = Logger.getLogger(InfraPropertyPlaceholderConfigurer.class);

	/**
     * trim the string value before it is returned to the caller method.
     *
     * @see org.springframework.beans.factory.config.PropertyPlaceholderConfigurer#resolvePlaceholder(String, java.util.Properties)
     */
    @Override
    protected String resolvePlaceholder(String placeholder, Properties props) {
        String resolvedPlaceholder = super.resolvePlaceholder(placeholder, props);
        if (null == resolvedPlaceholder) {
			LOGGER.trace("could not resolve place holder for key: " + placeholder + ". Please check your configuration files.");
			return resolvedPlaceholder;
		} else {
			return resolvedPlaceholder.trim();
		}
		
    }
}
