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

package com.cisco.oss.foundation.configuration;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory to get a Configuration object that gets all the configuration files
 * in the system and makes them a single object.
 * 
 * This class can be used to work with the
 * <code>org.apache.commons.configuration.Configuration</code> without the need
 * to use Spring. It has 2 public API's to get all the configuration layers, or
 * just the default layer.
 * 
 * @see org.apache.commons.configuration.Configuration
 * @author Joel Gurfinkel
 * @author Yair Ogen
 * 
 */

@Service
@Order(1)
public class ConfigurationFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationFactory.class);

	@Autowired
	private ConfigurableEnvironment environment;

	private static Configuration configuration;

	@PostConstruct
    public void init(){
        init(environment);
    }

    public void init(ConfigurableEnvironment environment) {
        try {
            configuration = (Configuration) getContext().getBean("configuration");
        } catch (BeansException e) {
            if(environment != null){
                Map<String, Object> allProperties = getAllProperties();
                configuration = new MapConfiguration(allProperties);
            }else{
                throw new RuntimeException("can't load commons config. error: " + e);
            }
        }
    }

    public Map<String,Object> getAllProperties()
    {
        Map<String,Object> result = new HashMap<>();
        environment.getPropertySources().forEach( ps -> addAll( result, getAllProperties( ps ) ) );
        return result;
    }

    public Map<String,Object> getAllProperties( PropertySource<?> aPropSource )
    {
        Map<String,Object> result = new HashMap<>();

        if ( aPropSource instanceof CompositePropertySource)
        {
            CompositePropertySource cps = (CompositePropertySource) aPropSource;
            cps.getPropertySources().forEach( ps -> addAll( result, getAllProperties( ps ) ) );
            return result;
        }

        if ( aPropSource instanceof EnumerablePropertySource<?> )
        {
            EnumerablePropertySource<?> ps = (EnumerablePropertySource<?>) aPropSource;
            Arrays.asList( ps.getPropertyNames() ).forEach(key -> result.put( key, ps.getProperty( key ) ) );
            return result;
        }


        return result;

    }

    private static void addAll( Map<String, Object> aBase, Map<String, Object> aToBeAdded )
    {
        for (Map.Entry<String, Object> entry : aToBeAdded.entrySet())
        {
            if ( aBase.containsKey( entry.getKey() ) )
            {
                continue;
            }

            aBase.put( entry.getKey(), entry.getValue() );
        }
    }

    private static volatile ApplicationContext context;

	/**
	 * get the full configuration object that contains all the configuration
	 * layers: customer, deployment and factory.
	 * 
	 * @return the configuration object
	 */
	public static Configuration getConfiguration() {
	    if(configuration == null){
            try {
                configuration = (Configuration) getContext().getBean("configuration");
            } catch (BeansException e) {
                LOGGER.error("Can't load config. Error: {}",e ,e);
            }
        }
	    return configuration;
	}

	/**
	 * get only a configuration that contains all the factory layer files.
	 * 
	 * @return a configuration that contains all the factory layer files.
	 */
	public static Configuration getDefaultConfiguration() {
		return (Configuration) getContext().getBean("defaultConfiguration");
	}

	private static ApplicationContext getContext() {
		if (context == null) {
			synchronized (ConfigurationFactory.class) {
				if (context == null) {
					context = new ClassPathXmlApplicationContext(new String[] {"META-INF/configurationContext.xml"});
				}
			}
		}
		Assert.notNull(context);
		return context;
	}

}
