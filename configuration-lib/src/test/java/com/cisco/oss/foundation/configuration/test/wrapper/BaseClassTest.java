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

package com.cisco.oss.foundation.configuration.test.wrapper;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created By: kgreen
 * Date-Time: 10/29/13 8:31 AM
 */
public class BaseClassTest {


    private static ApplicationContext _factory = null;
    //==================================================================================================================

    synchronized public static void initEnvironment() {

        if (_factory != null) {
            return;
        }

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
            }

        });

        _factory = new ClassPathXmlApplicationContext(new String[] {"META-INF/configurationContext.xml"});
    }
    //==================================================================================================================

    public static<T> T getBean(String beanName) {
        return (T)_factory.getBean(beanName);
    }

    @Test
    public void emptyTest() {
        // empty runnable test
    }
    //==================================================================================================================

}
