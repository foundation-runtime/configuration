package com.cisco.vss.foundation.configuration.test;

import com.cisco.vss.foundation.configuration.FoundationConfigurationListener;
import com.cisco.vss.foundation.configuration.FoundationConfigurationListenerRegistry;
import com.cisco.vss.foundation.configuration.ConfigUtil;
import com.cisco.vss.foundation.configuration.ConfigurationFactory;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;

import static org.junit.Assert.*;

public class TestConfiguration2 {

	private static final Logger LOGGER = Logger.getLogger(TestConfiguration2.class);

	private static ApplicationContext context;

	private boolean reloaded = false;;

	@BeforeClass
	public static void init() throws Exception {
		System.setProperty("testConfigFile", "QCTEST_cabConfig2.properties");
		context = new ClassPathXmlApplicationContext(new String[] {"/META-INF/configurationContext.xml"});
	}

	@Test
	// @Ignore
	public void testConfiguration() {
		Configuration configuration = (Configuration) context.getBean("configuration");

		Assert.assertEquals("dummy key", configuration.getString("dummyKey"));

		@SuppressWarnings("unchecked")
		Iterator<String> keys = configuration.getKeys();

		while (keys.hasNext()) {
			String key = keys.next();
			LOGGER.debug("key = " + key);
		}

	}

	@Test
	public void testConfigUtil() {
		Configuration configuration = (Configuration) context.getBean("configuration");
		HierarchicalConfiguration hierarchicalConfiguration = ConfigUtil.getHierarchicalConfiguration(configuration);
		assertNull(hierarchicalConfiguration);

		hierarchicalConfiguration = new HierarchicalConfiguration();
		((CompositeConfiguration) configuration).addConfiguration(hierarchicalConfiguration);
		HierarchicalConfiguration hierarchicalConfiguration2 = ConfigUtil.getHierarchicalConfiguration(configuration);
		assertSame(hierarchicalConfiguration, hierarchicalConfiguration2);
		((CompositeConfiguration) configuration).removeConfiguration(hierarchicalConfiguration);

		ConfigUtil.createCompositeConfiguration(TestConfiguration2.class, new PropertiesConfiguration());
	}

	@Test
	public void testDefault() {

		Configuration defaultConfiguration = ConfigurationFactory.getDefaultConfiguration();
		assertTrue(true);

	}

//	@Ignore
	@Test
	public void testConfigurationReload() {

		System.setProperty("testConfigFile", "config.properties");
		context = new ClassPathXmlApplicationContext(new String[] { "/META-INF/configurationContext.xml" });
		Configuration configuration = (Configuration) context.getBean("configuration");

		try {
			FoundationConfigurationListenerRegistry.addFoundationConfigurationListener(new FoundationConfigurationListener() {

                @Override
                public void configurationChanged() {
                    reloaded = true;
                    LOGGER.info("configuration has changed");
                }
            });

			FileOutputStream fileOutputStream = new FileOutputStream(new File(this.getClass().getResource("/config.properties").toURI()), true);
			String entry = "\nnew data key= new value\n";
			byte[] bytes = entry.getBytes();

			fileOutputStream.getChannel().write(ByteBuffer.wrap(bytes));
			fileOutputStream.flush();
			fileOutputStream.close();

			// give time for reload strategy to work.
			Thread.sleep(10000);

			configuration.getString("new data key");

			Assert.assertTrue(reloaded);
		} catch (Exception e) {
			Assert.fail(e.toString());
		}

	}

}
