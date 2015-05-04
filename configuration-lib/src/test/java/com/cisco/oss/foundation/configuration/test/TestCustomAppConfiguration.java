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

package com.cisco.oss.foundation.configuration.test;

import com.cisco.oss.foundation.configuration.CommonConfigurationsLoader;
import com.cisco.oss.foundation.configuration.ConfigurationFactory;
import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.reflect.Field;

public class TestCustomAppConfiguration {

	private static final Logger LOGGER = Logger.getLogger(TestCustomAppConfiguration.class);

	private static ApplicationContext context;

	@BeforeClass
	public static void init() throws Exception {
		System.setProperty("app.instance.name", "mytest");
		clearConfigurtionInConfigurationFactory();
		context = new ClassPathXmlApplicationContext(new String[] { "/META-INF/configurationContext.xml" });
	}
	
	private static void clearConfigurtionInConfigurationFactory() {
		try {
			Field configField = ConfigurationFactory.class.getDeclaredField("context");
			configField.setAccessible(true);
			configField.set(ConfigurationFactory.class, null);

			configField = CommonConfigurationsLoader.class.getDeclaredField("configuration");
			configField.setAccessible(true);
			configField.set(CommonConfigurationsLoader.class, null);

			configField = CommonConfigurationsLoader.class.getDeclaredField("printedToLog");
			configField.setAccessible(true);
			configField.set(CommonConfigurationsLoader.class, Boolean.FALSE);
		} catch (Exception e) {
			e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
		}
	}

	@Test
	// @Ignore
	public void testConfiguration() {
		Configuration configuration = (Configuration) context.getBean("configuration");		
	}

	}
