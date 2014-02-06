/**
 *
 */
package com.cisco.vss.foundation.configuration;

import org.apache.commons.configuration.CompositeConfiguration;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * this is an extension of the commons configuration CompositeConfiguration to
 * enable sorting the keys before returning them to the users.
 * 
 * @author Yair Ogen
 */
public class FoundationCompositeConfiguration extends CompositeConfiguration {

	private final Map<String, Object> cache = new ConcurrentHashMap<String, Object>();
	private static final boolean DISABLE_CACHE = Boolean.getBoolean("configuration.disableCache");

	/**
	 * each call to this method method will first sort the keys and then return
	 * the iterator.
	 */
	@Override
	@SuppressWarnings("unchecked")
	/**
	 * override to support ordering of returned keys.
	 */
	public Iterator<String> getKeys() {

		final Iterator tempKeysIter = super.getKeys();
		final List<String> keys = sortKeys(tempKeysIter);

		return keys.iterator();
	}

	private List<String> sortKeys(final Iterator<String> tempKeysIter) {
		final List<String> keys = new ArrayList<String>();
		while (tempKeysIter.hasNext()) {
			final String key = (String) tempKeysIter.next();
			keys.add(key);
		}

		Collections.sort(keys);
		return keys;
	}

	@Override
	public Object getProperty(String key) {
		if (DISABLE_CACHE) {
			return super.getProperty(key);			
		} else {
			if (cache.containsKey(key)) {
				return cache.get(key);
			} else {
				Object value = super.getProperty(key);
				if (value != null)
					cache.put(key, value);
				return value;
			}
		}
	}

	public void clearCache() {
		cache.clear();
	}

	public void updateCache(String key, Object value) {
		cache.put(key, value);
	}

    @Override
    public void setProperty(String key, Object value) {
        super.setProperty(key, value);
        updateCache(key, value);
    }

}
