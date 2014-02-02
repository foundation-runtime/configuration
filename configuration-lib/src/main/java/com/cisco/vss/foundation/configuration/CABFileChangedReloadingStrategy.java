/**
 * 
 */
package com.cisco.vss.foundation.configuration;

import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

/**
 * @author Yair Ogen
 * 
 */
public class CABFileChangedReloadingStrategy extends FileChangedReloadingStrategy {

	/**
	 * @see org.apache.commons.configuration.reloading.FileChangedReloadingStrategy#reloadingPerformed()
	 */
	@Override
	public void reloadingPerformed() {
		super.reloadingPerformed();
		FoundationCompositeConfiguration configuration = (FoundationCompositeConfiguration) ConfigurationFactory.getConfiguration();
		configuration.clearCache();
		configuration.getString("configuration.dynamicConfigReload.refreshDelay");
		CabConfigurationListenerRegistry.fireConfigurationChangedEvent();
	}

}
