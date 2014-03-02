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

package factorybeantest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationTestContext.xml" })
public class FactoryBeanTest {

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
