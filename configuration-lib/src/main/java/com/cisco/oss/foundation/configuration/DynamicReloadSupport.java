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

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class reads the configuration to determine if dynamic reload of the configuration in case of config file changes should be enabled or not.<br>
 * When enabled the configuration in memory map will be updated upon file changes within a configuration refresh delay period.<br>
 * Client interested in getting notifications of configuration reload should register via {@link FoundationConfigurationListenerRegistry#addFoundationConfigurationListener(FoundationConfigurationListener)} API.
 * 
 * @author Yair Ogen
 */
public class DynamicReloadSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(DynamicReloadSupport.class);

	private final CompositeConfiguration configuration;

	private DynamicReloadSupport(CompositeConfiguration configuration) {
		super();
		this.configuration = configuration;
	}

	public void init() {

		LOGGER.debug("in DynamicReloadSupport init method");

		boolean isDynamicReLoadEnabled = configuration.getBoolean("configuration.dynamicConfigReload.enabled", false);
		boolean isDynamicReloadAutoUpdateEnabled = true;//configuration.getBoolean("configuration.dynamicConfigReload.autoUpdateEnabled");
		long refreshDelay = configuration.getLong("configuration.dynamicConfigReload.refreshDelay", 30000);

		if (isDynamicReLoadEnabled) {

			LOGGER.info("configuration dynamic reload is enabled!");

			int numberOfConfigurations = configuration.getNumberOfConfigurations();

			for (int index = 0; index < numberOfConfigurations; index++) {

				Configuration config = configuration.getConfiguration(index);

				// file reload only supported on file based configurations.
				// cab configuration only supports properties configuration.
				if (config instanceof PropertiesConfiguration) {

					PropertiesConfiguration propertiesConfiguration = (PropertiesConfiguration) config;

					// TODO: ignore default config files
					String fileName = propertiesConfiguration.getFileName();
					if (fileName == null) {
						fileName = propertiesConfiguration.getBasePath();
					}
					if (fileName.startsWith("default")) {
						// do not support reload for default config files.
						continue;
					}

					LOGGER.debug("Setting reload strategy on: " + propertiesConfiguration.getPath());

					FoundationFileChangedReloadingStrategy strategy = new FoundationFileChangedReloadingStrategy();
					strategy.setRefreshDelay(refreshDelay);

					propertiesConfiguration.setReloadingStrategy(strategy);

				}

			}

			if (isDynamicReloadAutoUpdateEnabled) {
				startDynamicReloadAutoUpdateDeamon(configuration, refreshDelay);
			}

		}

	}

	/**
	 * @param refreshDelay
	 */
	private void startDynamicReloadAutoUpdateDeamon(final Configuration configuration, final long refreshDelay) {

		// run as daemon
		Timer timer = new Timer("DynamicReloadAutoUpdate", true);

		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				
				try {
					Field configCacheField = FoundationCompositeConfiguration.class.getDeclaredField("DISABLE_CACHE");
					configCacheField.setAccessible(true);
					Field modifiersField = Field.class.getDeclaredField("modifiers");
					modifiersField.setAccessible(true);
					modifiersField.setInt(configCacheField, configCacheField.getModifiers() & ~Modifier.FINAL);
					boolean disableCahce = (Boolean) configCacheField.get(configuration);
					if(!disableCahce){
						configCacheField.set(configuration, Boolean.TRUE);
						// just for triggering reload mechanism

//						configuration.getProperty("configuration.dynamicConfigReload.enabled");
						boolean loaded = false;
						Iterator<String> configIterator = ConfigResourcesLoader.customerPropertyNames.iterator();
						while (configIterator.hasNext() && !loaded) {
							String propName =  configIterator.next();
							try {
								configuration.getProperty(propName);
								loaded = true;
							} catch (Exception e) {
								loaded = false;
							}

						}

						configCacheField.set(configuration, Boolean.FALSE);
					}else{
						// just for triggering reload mechanism
						configuration.getProperty("configuration.dynamicConfigReload.enabled");
					}
				} catch (Exception e) {					
					LOGGER.error("problem reloading the configuration", e);
				}
				

			}
		}, refreshDelay, refreshDelay);
	}

}
