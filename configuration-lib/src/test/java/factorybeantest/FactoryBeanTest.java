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

package factorybeantest;

import com.cisco.oss.foundation.configuration.CommonConfigurationsLoader;
import com.cisco.oss.foundation.configuration.ConfigurationFactory;
import com.cisco.oss.foundation.configuration.FoundationCompositeConfiguration;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.lang.reflect.Field;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationTestContext.xml" })
@Ignore
public class FactoryBeanTest {

    @BeforeClass
    public static void init(){
//        clearConfigurtionInConfigurationFactory();
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

	public FactoryBeanTest() {
		// try {
		// Settings settings = new Settings();
		// settings.setChildLoggersEnabled(true);
		// CABLogging.setup(SubSystem.headend, "InfraConfiguration",
		// "InfraConfiguration", "InfraConfiguration", "log.properties",
		// settings);
		// } catch (CABConfigurationException e) {
		// e.printStackTrace();
		// }
		super();
	}

	// protected String[] getConfigPaths() {
	// return new String[] {"/applicationTestContext.xml"};
	// }

	@Resource
	private Bean1 bean1;
	@Resource
	private Bean1 bean2;
	@Resource
	private Bean1 bean3;
	@Resource
	private SPELSampleBean spelSampleBean;

	public void setBean1(final Bean1 bean1) {
		this.bean1 = bean1;
	}

	public void setBean2(final Bean1 bean2) {
		this.bean2 = bean2;
	}

	public void setBean3(final Bean1 bean3) {
		this.bean3 = bean3;
	}

	@Test
	public void testBean1() {
		Assert.assertNotNull(bean1);
		Assert.assertEquals(1, bean1.getParam1());
		Assert.assertEquals("factory1", bean1.getFactoryName());

	}

	@Test
	public void testBean2() {
		Assert.assertNotNull(bean2);
		Assert.assertEquals(2, bean2.getParam1());
		Assert.assertEquals("factory1", bean1.getFactoryName());
	}

	@Test
	public void testBean3() {
		Assert.assertNotNull(bean3);
		Assert.assertEquals(3, bean3.getParam1());
		Assert.assertEquals("factory1", bean1.getFactoryName());
	}
	
	@Test
	public void testSPEL(){
		Assert.assertEquals(1230, spelSampleBean.getNumber());
	}

}
