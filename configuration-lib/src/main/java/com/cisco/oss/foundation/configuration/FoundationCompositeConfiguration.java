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
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * this is an extension of the commons configuration CompositeConfiguration to
 * enable sorting the keys before returning them to the users.
 *
 * @author Yair Ogen
 */
public class FoundationCompositeConfiguration extends CompositeConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(FoundationCompositeConfiguration.class);

    private final Map<String, Object> cache = new ConcurrentHashMap<String, Object>();
    private static Boolean DISABLE_CACHE = Boolean.getBoolean("configuration.disableCache");

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

    public boolean containsKeyFromCommonsConfig(String key) {
        return super.containsKey(key);
    }

    @Override
    public boolean containsKey(String key) {
        if (DISABLE_CACHE) {
            return super.containsKey(key);
        } else {
            return cache.containsKey(key);
        }
    }

    @Override
    public Object getProperty(String key) {
        if (DISABLE_CACHE) {
            LOGGER.trace("Reloading from disk.");
            return super.getProperty(key);
        } else {
            if (cache.containsKey(key)) {
                final Object value = cache.get(key);
                return (value == ObjectUtils.NULL)?null:value;
            } else {
                Object value = super.getProperty(key);
                if (value == null) {
                     cache.put(key, ObjectUtils.NULL);
                 } else {
                      cache.put(key, value);
                 }
 
                return value;
            }
        }
    }

    public void clearCache() {
        cache.clear();
    }

    public void updateCache(String key, Object value) {
        if (value == null) {
            cache.remove(key);
        } else {
            cache.put(key, value);
        }
    }

    @Override
    public void setProperty(String key, Object value) {
        super.setProperty(key, value);
        updateCache(key, value);
    }

    @Override
    public boolean getBoolean(String key) {
        Boolean b = getBoolean(key, null);
        if (b != null) {
            return b.booleanValue();
        } else {
            throw new NoSuchElementException('\'' + key + "' doesn't map to an existing object");
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return getBoolean(key, (Boolean) defaultValue);
    }

    @Override
    public Boolean getBoolean(String key, Boolean defaultValue) {
        Object property = getProperty(key);
        if (property != null) {
            return BooleanUtils.toBooleanObject(property.toString());
        } else {
            return defaultValue;
        }
    }
}
