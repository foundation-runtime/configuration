package personal.personal;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class MyBean {
	private List<Integer> integers;

	public List<Integer> getIntegersList() {
		return integers;
	}

	public void setIntegers(String integers) {
		System.out.println("Sent String from cabConf : " + integers);
		final String[] splitArr = integers.split(",");
		final List<Integer> integersArr = new ArrayList<Integer>();
		for (String intStr : splitArr) {
			integersArr.add(Integer.decode(intStr.trim()));
		}
		this.integers = integersArr;
	}

	public static void main(String[] args) {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("appContext.xml");
		MyBean bean = (MyBean) applicationContext.getBean("testBean");
		System.out.println(bean.getIntegersList());
	}
}
