package com.cisco.vss.foundation.configuration.test.wrapper;

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
