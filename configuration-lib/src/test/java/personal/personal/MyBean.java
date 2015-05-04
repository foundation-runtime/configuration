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
