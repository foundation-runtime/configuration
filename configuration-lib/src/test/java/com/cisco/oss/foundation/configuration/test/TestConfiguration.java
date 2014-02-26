package com.cisco.oss.foundation.configuration.test;

import com.cisco.oss.foundation.configuration.CommonConfigurationsLoader;
import com.cisco.oss.foundation.configuration.ConfigUtil;
import com.cisco.oss.foundation.configuration.ConfigurationFactory;
import com.cisco.oss.foundation.configuration.FoundationCompositeConfiguration;
import org.apache.commons.configuration.*;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class TestConfiguration {

	private static final Logger LOGGER = Logger.getLogger(TestConfiguration.class);

	private static ApplicationContext context;

	@BeforeClass
	public static void init() throws Exception {
		System.setProperty("testConfigFile", "QCTEST_cabConfig.properties");
		// System.setProperty("commonsConfig.defaultListDelimiter", "~");
		System.setProperty("commonsConfig.delimiterParsingDisabled", "true");
		clearConfigurtionInConfigurationFactory();
		context = new ClassPathXmlApplicationContext(new String[] { "applicationTestContext.xml" });
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
			
			FoundationCompositeConfiguration configuration = (FoundationCompositeConfiguration)ConfigurationFactory.getConfiguration();
			configuration.clearCache();
		} catch (Exception e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		}
	}

	@Test
	// @Ignore
	public void testConfiguration() {
		Configuration configuration = (Configuration) context.getBean("configuration");

		@SuppressWarnings("unchecked")
		Iterator<String> keys = configuration.getKeys();

		while (keys.hasNext()) {
			String key = keys.next();
			LOGGER.debug("key = " + key + ", value = " + configuration.getString(key));
		}

		// assertTrue(configuration.getString("database.url").equals("jdbc:oracle:thin:@columbia:67890:EMMG"));

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

		ConfigUtil.createCompositeConfiguration(TestConfiguration.class, new PropertiesConfiguration());
	}

	@Test
	public void testConfigRef() {
		Configuration configuration = (Configuration) context.getBean("configuration");
		assertEquals(configuration.getString("dummyValueOneCopy"), configuration.getString("dummyKey"));
	}

	@Ignore
	@Test
	public void testHierarchicalConfiguration() {
		Configuration configuration = (Configuration) context.getBean("configuration");
		HierarchicalConfiguration hierarchicalConfiguration = ConfigurationUtils.convertToHierarchical(configuration);

		@SuppressWarnings("unchecked")
		List<Object> list = hierarchicalConfiguration.getList("person.name");
		assertEquals(1, list.size());

		for (int i = 0; i < list.size(); i++) {
			System.out.println(hierarchicalConfiguration.getString("person.name(" + i + ")"));
			assertEquals("1", hierarchicalConfiguration.getString("person.name(0)"));
			assertEquals("4", hierarchicalConfiguration.getString("person.name(1)"));
			assertEquals("7", hierarchicalConfiguration.getString("person.name(2)"));
		}

		assertEquals(1, hierarchicalConfiguration.getInt("person.name(0)"));
		assertEquals(4, hierarchicalConfiguration.getInt("person.name(1)"));
		assertEquals(7, hierarchicalConfiguration.getInt("person.name(2)"));
	}

	// @Test
	// public void testHierarchicalConfiguration2(){
	// Configuration configuration = (Configuration)
	// context.getBean("configuration");
	// HierarchicalConfiguration hierarchicalConfiguration =
	// ConfigurationUtils.convertToHierarchical(configuration);
	//
	// hierarchicalConfiguration.getRoot()
	//
	// // for (int i = 0; i < iterator.size(); i++) {
	// // Configuration subset =
	// hierarchicalConfiguration.subset("person("+i+")");
	// // String name = subset.getString("name");
	// // String surName = subset.getString("surName");
	// // String phoneNum = subset.getString("phoneNum");
	// //
	// // System.out.println("name = " + name + ", surName=" + surName +
	// ", phoneNum=" + phoneNum);
	// //
	// // }
	//
	//
	//
	//
	// }

	@Test
	public void testDefault() {

		Configuration defaultConfiguration = ConfigurationFactory.getDefaultConfiguration();
		assertTrue(true);

	}

	@Test
	public void readArray() {
		Configuration configuration = ConfigurationFactory.getConfiguration();
		Configuration domainGroups = configuration.subset("domain.group");

		List<String> handledKeys = new ArrayList<String>();

		@SuppressWarnings("unchecked")
		Iterator<String> keys = domainGroups.getKeys();
		while (keys.hasNext()) {
			String key = keys.next();
			if (handledKeys.contains(key)) {
				continue;
			} else {
				if (key.contains("groupId")) {
					String groupId = domainGroups.getString(key);
					String groupType = domainGroups.getString(key.replace("groupId", "groupType"));
				}

			}

		}
	}

	@Test
	public void resetConfiguration() throws Exception {
		ConfigurationFactory.getConfiguration();
		Field contextFiled = ConfigurationFactory.class.getDeclaredField("context");
		contextFiled.setAccessible(true);
		ApplicationContext context = (ApplicationContext) contextFiled.get(ConfigurationFactory.class);
		Assert.assertNotNull(context);
		contextFiled.set(ConfigurationFactory.class, null);
		contextFiled = ConfigurationFactory.class.getDeclaredField("context");
		contextFiled.setAccessible(true);
		context = (ApplicationContext) contextFiled.get(ConfigurationFactory.class);
		Assert.assertNull(context);
	}

	@Test
	public void delimiterDisabledTest() {
		String string = ConfigurationFactory.getConfiguration().getString("ecmserver.emergencyAC");
		Assert.assertEquals("{\"tiers\":[{\"id\":\"111\",\"name\":\"Tier_1\"}],\"usageRules\":\"AC_0\"}", string);
	}

	@Test
	public void testConcurrentInit() throws Exception {

		int numOfIter = 50;

		List<Thread> threads = new ArrayList<Thread>(numOfIter);

		for (int i = 0; i < numOfIter; i++) {
			Thread thread = new Thread(new GetConfigRunnable());
			threads.add(thread);
		}

		for (Thread thread : threads) {
			thread.start();
		}

		for (Thread thread : threads) {
			thread.join();
		}
	}

	public static void main(String[] args)  {
		for (int i = 0; i < 10; i++) {
			new Thread(new GetConfigRunnable()).start();
		}
	}

	public static class GetConfigRunnable implements Runnable {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			ConfigurationFactory.getConfiguration();
		}

	}
	
	@Test public void testParseComplexStrucutreArray(){
		Map<String, Map<String, String>> parseComplexArrayStructure = ConfigUtil.parseComplexArrayStructure("smartcardAdaptor.cardFeatures");
		assertTrue(parseComplexArrayStructure.size() > 0);
		assertEquals("{1={maxRegions=4, cardFamily=VGE, maxNumOfOPPVs=25, sbmMSB.1=0x01, initialCounter=1000, sbmMSB.0=0x00, supportSbm=true, maxSbmBlocks=16}, 23={maxRegions=4, cardFamily=VGE, maxNumOfOPPVs=25, sbmMSB.1=0x01, initialCounter=1000, sbmMSB.0=0x00, supportSbm=true, maxSbmBlocks=16}}", parseComplexArrayStructure.toString());
		
		Map<String, String> parseComplexArrayStructure2 = ConfigUtil.parseSimpleArrayAsMap("smartcardAdaptor.cardFeatures.1.sbmMSB");
		assertTrue(parseComplexArrayStructure2.size() > 0);
		assertEquals("{1=0x01, 0=0x00}", parseComplexArrayStructure2.toString());
		
	}

}
