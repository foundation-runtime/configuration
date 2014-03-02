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

package com.cisco.oss.foundation.configuration.test;

import com.cisco.oss.foundation.configuration.CcpConstants;
import com.cisco.oss.foundation.configuration.CommonConfigurationsLoader;
import com.cisco.oss.foundation.configuration.ConfigurationFactory;
import com.cisco.oss.foundation.environment.utils.EnvUtils;
import com.cisco.oss.foundation.http.HttpClient;
import com.cisco.oss.foundation.http.HttpMethod;
import com.cisco.oss.foundation.http.HttpRequest;
import com.cisco.oss.foundation.http.apache.ApacheHttpClientFactory;
import com.cisco.oss.foundation.http.apache.ApacheHttpResponse;
import org.apache.commons.configuration.Configuration;
import org.junit.*;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.reflect.Field;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "/applicationTestContext.xml", "/META-INF/ccpServerContext.xml" })
public class TestCentralConfiguration {

	// @Resource(name = "dbApiImpl")
//	private static DBAPI dbAPI;

	// @Resource(name = "CCPServer")
//	private static Server ccpServer;

	@BeforeClass
	public static void initOnce() {

		EnvUtils.updateEnv(CcpConstants.CCP_ENABLED, "true");
		EnvUtils.updateEnv("_CCP_DB_IMPL", "oracle");

		EnvUtils.updateEnv(CcpConstants.FQDN, "ch-perf-dt1.il.nds.com");
		EnvUtils.updateEnv(CcpConstants.RPM_SOFTWARE_NAME, "hep");
		EnvUtils.updateEnv(CcpConstants.ARTIFACT_VERSION, "2.49.0-0");
		EnvUtils.updateEnv(CcpConstants.INSTALL_DIR, "/opt/nds/installed/hep-2.49.0-0");

		EnvUtils.updateEnv(CcpConstants.CCP_SERVER_PORT, "7890");
		EnvUtils.updateEnv(CcpConstants.CCP_SERVER, "chvm16:5670");
		EnvUtils.updateEnv(CcpConstants.CCP_DB_URL, "jdbc:oracle:thin:@columbia:1522:EMMG");
		EnvUtils.updateEnv(CcpConstants.CCP_DB_USER, "ccp");
		EnvUtils.updateEnv(CcpConstants.CCP_DB_PASSWORD, "ccp");
		EnvUtils.updateEnv(CcpConstants.CCP_PASSWORD_ENCRYPTION, "false");
		EnvUtils.updateEnv(CcpConstants.CCP_COMPONENT_CHECK_ENABLE,"false");
		EnvUtils.updateEnv(CcpConstants.CCP_COMPONENT_CHECK_DELAY,"10000");
		EnvUtils.updateEnv(CcpConstants.CCP_SERVICE_DISCOVERY_LEASE, "10");

//		ApplicationContext context = new ClassPathXmlApplicationContext("/META-INF/ccpServerContext.xml");
//		dbAPI = context.getBean("dbApiImpl", DBAPI.class);
//		ccpServer = context.getBean("CCPServer", Server.class);

//		new Thread(new RunServer(ccpServer)).start();

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Before
	public void init() {

//		clearConfigurtionInConfigurationFactory();
//		resetParamInDB();
		// new Thread(new RunServer(ccpServer)).start();

		// try {
		// Thread.sleep(3000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }

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

	@AfterClass
	public static void post() {
		
		try {
//			 ccpServer.stop();
//			 dbAPI.deregisterClient(4);
//			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		EnvUtils.updateEnv(CcpConstants.CCP_ENABLED, "false");
		clearConfigurtionInConfigurationFactory();
	}

	@Test()
    @Ignore
	public void getWebConfig() {
		// init();
		EnvUtils.updateEnv(CcpConstants.RPM_SOFTWARE_NAME, "ndsconsole");
		try {
			Configuration configuration = ConfigurationFactory.getConfiguration();
			Assert.fail("sould not get here");
		} catch (BeanCreationException bce) {
			Throwable cause = bce.getCause().getCause().getCause().getCause();
			if (cause == null || !(cause instanceof IllegalArgumentException)) {
				Assert.fail("should have been an IllegalArgumentException but was: " + cause);
			}
		}

		EnvUtils.updateEnv(CcpConstants.RPM_SOFTWARE_NAME, "BSM");
	}

	@Test
    @Ignore
	public void getConfig() {
		// init();
//		EnvUtils.updateEnv(CcpConstants.ARTIFACT_NAME, "BSM");
		Configuration configuration = ConfigurationFactory.getConfiguration();
//		String url = configuration.getString("url");
//		Assert.assertEquals("mylocalhost", url);
	}

	@Test
    @Ignore
	public void testDynamicReload() {

		// stop();
//		EnvUtils.updateEnv(CcpConstants.ARTIFACT_NAME, "BSM");
		Configuration configuration = ConfigurationFactory.getConfiguration();
		boolean clpIsEnabled = configuration.getBoolean("clp.isEnabled");
        boolean value = true;
		Assert.assertEquals(value, clpIsEnabled);

//		PrimitiveValue newValue = new PrimitiveValue();
//		String newValueStr = "mylocalhost_123";
//		newValue.setValue(newValueStr);
//		dbAPI.updatePrimitive(12, 4, null, newValue);

        HttpClient<HttpRequest, ApacheHttpResponse> httpClient = ApacheHttpClientFactory.createHttpClient("service.configurationLib", false);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri("http://chvm16:5670/api/configuration")
                .httpMethod(HttpMethod.POST)
                .entity("<ConfigurationOperations>\n" +
                        "  <Operation index=\"0\">\n" +
                        "    <Update paramId=\"177697\" levelInstanceId=\"2746\">\n" +
                        "      <OldPrimitive value=\"true\"/>\n" +
                        "      <NewPrimitive value=\"false\"/>\n" +
                        "    </Update>\n" +
                        "  </Operation>\n" +
                        "</ConfigurationOperations>")
                .build();

        ApacheHttpResponse httpResponse = httpClient.execute(httpRequest);

        Assert.assertEquals(200, httpResponse.getStatus());

        long refreshDelay = configuration.getLong("configuration.dynamicConfigReload.refreshDelay");

		try {
			Thread.sleep(refreshDelay+1000);
		} catch (InterruptedException e) {
			// ignore
		}

        clpIsEnabled = configuration.getBoolean("clp.isEnabled");
        Assert.assertEquals(!value, clpIsEnabled);


        httpRequest = HttpRequest.newBuilder()
                .uri("http://chvm16:5670/api/configuration")
                .httpMethod(HttpMethod.POST)
                .entity("<ConfigurationOperations>\n" +
                        "  <Operation index=\"0\">\n" +
                        "    <Update paramId=\"177697\" levelInstanceId=\"2746\">\n" +
                        "      <OldPrimitive value=\"false\"/>\n" +
                        "      <NewPrimitive value=\"true\"/>\n" +
                        "    </Update>\n" +
                        "  </Operation>\n" +
                        "</ConfigurationOperations>")
                .build();

        httpResponse = httpClient.execute(httpRequest);

        Assert.assertEquals(200, httpResponse.getStatus());

        try {
            Thread.sleep(refreshDelay+1000);
        } catch (InterruptedException e) {
            // ignore
        }


        clpIsEnabled = configuration.getBoolean("clp.isEnabled");
        Assert.assertEquals(value, clpIsEnabled);

	}

	// @Test
	public void getHttpConfig() {
		init();
		EnvUtils.updateEnv(CcpConstants.ARTIFACT_NAME, "BSM");

		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// Startup.main(null);
		// }
		// }).start();
		//
		// try {
		// Thread.sleep(2000);
		// } catch (InterruptedException e) {
		// //ignore
		// }

		EnvUtils.updateEnv(CcpConstants.CCP_DB_URL, "");
		EnvUtils.updateEnv(CcpConstants.CCP_DB_USER, "");
		EnvUtils.updateEnv(CcpConstants.CCP_DB_PASSWORD, "");
		EnvUtils.updateEnv(CcpConstants.CCP_SERVER, "localhost:" + System.getenv(CcpConstants.CCP_SERVER_PORT));

		clearConfigurtionInConfigurationFactory();

		Configuration configuration = ConfigurationFactory.getConfiguration();
		String port = configuration.getString("carmClient.1.port");
		Assert.assertEquals("13333", port);
	}

//	@After
//	public void stop() throws DBAPIException {
//		resetParamInDB();
//
////		try {
////			 ccpServer.stop();
////			Thread.sleep(3000);
////		} catch (Exception e) {
////			e.printStackTrace();
////		}
//	}

//	protected void resetParamInDB() throws DBAPIException {
//		PrimitiveValue newValue = new PrimitiveValue();
//		String newValueStr = "mylocalhost";
//		newValue.setValue(newValueStr);
//		dbAPI.updatePrimitive(12, 4, null, newValue);
//	}

//	private static class RunServer implements Runnable {
//
//		private Server ccpServer;
//
//		public RunServer(Server ccpServer) {
//			this.ccpServer = ccpServer;
//		}
//
//		@Override
//		public void run() {
//			try {
//				ccpServer.start();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//		}
//
//	}

}
