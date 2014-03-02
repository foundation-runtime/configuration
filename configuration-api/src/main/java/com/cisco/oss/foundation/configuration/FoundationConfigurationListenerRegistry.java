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
package com.cisco.oss.foundation.configuration;

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
