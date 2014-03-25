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

/**
 * 
 */
package com.cisco.oss.foundation.test.util;

import com.cisco.oss.foundation.configuration.CommonConfigurationsLoader;
import com.cisco.oss.foundation.configuration.ConfigurationFactory;

import java.lang.reflect.Field;


/**
 * This class is used just as flag for using the configuration test support.
 * The intention is that users that wish to use configuration in a more flexible way - just for Unit testing or QC, can add this class to the classpath.
 * When added by maven with scope of test, the cabConfiguration library will look into a System library called: "testConfigFileSuffix" and read a file called: QCTEST_{testConfigFileSuffix}.properties.<br>
 * THIS OPTION SHOULD NEVER BE USED OTHER THAN FOR JUNIT OF QC.
 * @author Yair Ogen
 *
 */
public final class ConfigurationForTest {
	
	private ConfigurationForTest(){
		//prevent instantiation
	}
	
	public static final String TEST_CONFIG_FILE = "testConfigFile";
	
	public static void  setTestConfigFile(final String testConfigFile){
		System.setProperty(TEST_CONFIG_FILE, testConfigFile)	;
		clearConfigurtionInConfigurationFactory();
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

}
