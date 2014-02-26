/**
 * 
 */
package com.cisco.oss.foundation.configuration;

import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

/**
 * @author Yair Ogen
 * 
 */
public class FoundationFileChangedReloadingStrategy extends FileChangedReloadingStrategy {

	/**
	 * @see org.apache.commons.configuration.reloading.FileChangedReloadingStrategy#reloadingPerformed()
	 */
	@Override
	public void reloadingPerformed() {
		super.reloadingPerformed();
		FoundationCompositeConfiguration configuration = (FoundationCompositeConfiguration) ConfigurationFactory.getConfiguration();
		configuration.clearCache();
		configuration.getString("configuration.dynamicConfigReload.refreshDelay");
		FoundationConfigurationListenerRegistry.fireConfigurationChangedEvent();
	}

}
