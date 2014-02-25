/**
 * 
 */
package com.cisco.vss.foundation.configuration;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Yair Ogen
 * 
 */
public class FoundationConfigurationListenerRegistry {

	private static CopyOnWriteArraySet<FoundationConfigurationListener> listeners = new CopyOnWriteArraySet<FoundationConfigurationListener>();

	public static void fireConfigurationChangedEvent() {

		for (FoundationConfigurationListener listener : listeners) {
			listener.configurationChanged();
		}

	}

	public static void addFoundationConfigurationListener(FoundationConfigurationListener listener) {
		listeners.add(listener);
	}
	
	public static void removeFoundationConfigurationListener(FoundationConfigurationListener listener) {
		listeners.remove(listener);
	}

}
