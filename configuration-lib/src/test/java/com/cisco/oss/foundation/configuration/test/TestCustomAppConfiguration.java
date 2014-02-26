package com.cisco.oss.foundation.configuration.test;

import com.cisco.oss.foundation.configuration.CommonConfigurationsLoader;
import com.cisco.oss.foundation.configuration.ConfigurationFactory;
import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.reflect.Field;

public class TestCustomAppConfiguration {

	private static final Logger LOGGER = Logger.getLogger(TestCustomAppConfiguration.class);

	private static ApplicationContext context;

	@BeforeClass
	public static void init() throws Exception {
		System.setProperty("app.instance.name", "mytest");
		clearConfigurtionInConfigurationFactory();
		context = new ClassPathXmlApplicationContext(new String[] { "/META-INF/configurationContext.xml" });
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

	@Test
	// @Ignore
	public void testConfiguration() {
		Configuration configuration = (Configuration) context.getBean("configuration");		
	}

	}
