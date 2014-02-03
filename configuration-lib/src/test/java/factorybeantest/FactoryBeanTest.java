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
