/**
 * 
 */
package com.cisco.vss.foundation.configuration;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Yair Ogen
 * 
 */
public class CabConfigurationListenerRegistry {

	private static CopyOnWriteArraySet<CabConfigurationListener> listeners = new CopyOnWriteArraySet<CabConfigurationListener>();

	public static void fireConfigurationChangedEvent() {

		for (CabConfigurationListener listener : listeners) {
			listener.configurationChanged();
		}

	}

	public static void addCabConfigurationListener(CabConfigurationListener listener) {
		listeners.add(listener);
	}
	
	public static void removeCabConfigurationListener(CabConfigurationListener listener) {
		listeners.remove(listener);
	}

}
