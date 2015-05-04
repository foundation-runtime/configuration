/*
 * Copyright 2015 Cisco Systems, Inc.
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

/**
 *
 */
package com.cisco.oss.foundation.configuration;

import org.slf4j.*;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Properties;

/**
 * this extension to <code>org.springframework.beans.factory.config.PropertyPlaceholderConfigurer</code> is used to trim values read from the configuration file.
 *
 * @author Yair Ogen
 * @see org.springframework.beans.factory.config.PropertyPlaceholderConfigurer
 */
public class InfraPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InfraPropertyPlaceholderConfigurer.class);

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
