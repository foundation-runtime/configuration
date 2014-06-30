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

package com.cisco.oss.foundation.configuration.test.wrapper;

import com.cisco.oss.foundation.configuration.*;
import com.cisco.oss.foundation.configuration.validation.BaseConfiguration;
import com.cisco.oss.foundation.configuration.validation.exceptions.GeneralConfigException;
import com.cisco.oss.foundation.configuration.validation.params.*;
import org.apache.commons.configuration.Configuration;
import org.junit.*;

import java.lang.reflect.Field;
import java.util.*;

import static com.cisco.oss.foundation.configuration.validation.BaseConfiguration.*;
import static com.cisco.oss.foundation.configuration.validation.params.ParamReaders.*;
import static com.cisco.oss.foundation.configuration.validation.params.ParamValidators.*;
import static com.cisco.oss.foundation.configuration.validation.params.ParamValidators.getValidator;

/**
 * Created By: kgreen
 * Date-Time: 10/29/13 8:54 AM
 */
public class BaseConfigurationTest {

    private static ConfigImpl config;

    // use static initialization to make sure that environment is initialized before the parameters are
    static {
        // disable caching in the Configuration to allow changing configuration on need
        System.setProperty("configuration.disableCache", "true");

        config = new ConfigImpl();
    }

    @BeforeClass
    public static void initClass () {
        BaseClassTest.initEnvironment();
    }

    @Before
    public void initTest() {

        // indicate to disable static configuration mode
        config.setEnableStaticConfiguration(false);

        config.verifyConfig = true;
        config.setConfiguration(ConfigurationFactory.getConfiguration());
    }

    @After
    public void afterTest() {
        config.verifyConfig = false;
    }

    // instantiate a Dynamic parameter with multiple Integer values that are organized within a Set and with default value "1"
    public final Param<Set<Integer>> intSetParam = ParamFactory.setDynamicFactory.asInt("intSetParamName", 1);

    // instantiate a Static parameter with multiple String values in Hexadecimal format that are organized within a List and with default values "AABB33" + "FFFFF"
    public final Param<List<String>> hexListParam = ParamFactory.listStaticFactory.asString("hexListParamName", new String[] {"AABB33", "FFFFF"}, HEXValidator.instance(false));

    // list of all parameters
    public final static Param<String> sampleParameterString = staticParam().asString("cfgWrapper.sampleParameter.string");
    public final static Param<String> sampleParameterFile = staticParam().asString("cfgWrapper.sampleParameter.stringFile", null, getValidator(FileValidator.class, true));
    public final static Param<String> sampleParameterURL = staticParam().asString("cfgWrapper.sampleParameter.stringURL", null, getValidator(URLValidator.class, true));
    public final static Param<String> sampleParameterHEX = staticParam().asString("cfgWrapper.sampleParameter.stringHEX", null, getValidator(HEXValidator.class, true));
    public final static Param<Boolean> sampleParameterBoolean = staticParam().asBool("cfgWrapper.sampleParameter.boolean");
    public final static Param<Long> sampleParameterLong = staticParam().asLong("cfgWrapper.sampleParameter.long");
    public final static Param<Integer> sampleParameterInteger = staticParam().asInt("cfgWrapper.sampleParameter.integer");
    public final static Param<Short> sampleParameterShort = staticParam().asShort("cfgWrapper.sampleParameter.short");
    public final static Param<Byte> sampleParameterByte = staticParam().asByte("cfgWrapper.sampleParameter.byte");
    public final static Param<Float> sampleParameterFloat = staticParam().asFloat("cfgWrapper.sampleParameter.float");
    public final static Param<Double> sampleParameterDouble = staticParam().asDouble("cfgWrapper.sampleParameter.double");
    public final static Param<AnyData> sampleParameterAny = staticParam().asAny(new MethodReader(BaseConfigurationTest.class, "getAnyData", "cfgWrapper.sampleParameter.any"), "cfgWrapper.sampleParameter.any");

    public final static Param<List<String>> sampleParameterArrString = staticParamList().asString("cfgWrapper.sampleParameter.array.string");
    public final static Param<List<Boolean>> sampleParameterArrBoolean = staticParamList().asBool("cfgWrapper.sampleParameter.array.boolean");
    public final static Param<List<Long>> sampleParameterArrLong = staticParamList().asLong("cfgWrapper.sampleParameter.array.long");
    public final static Param<List<Integer>> sampleParameterArrInteger = staticParamList().asInt("cfgWrapper.sampleParameter.array.integer");
    public final static Param<List<Short>> sampleParameterArrShort = staticParamList().asShort("cfgWrapper.sampleParameter.array.short");
    public final static Param<List<Byte>> sampleParameterArrByte = staticParamList().asByte("cfgWrapper.sampleParameter.array.byte");
    public final static Param<List<Float>> sampleParameterArrFloat = staticParamList().asFloat("cfgWrapper.sampleParameter.array.float");
    public final static Param<List<Double>> sampleParameterArrDouble = staticParamList().asDouble("cfgWrapper.sampleParameter.array.double");
    public final static Param<List<AnyData>> sampleParameterArrAny = staticParam().asAny(new MethodReader(BaseConfigurationTest.class, "getAnyData", "cfgWrapper.sampleParameter.array.any"), "cfgWrapper.sampleParameter.array.any");

    public final static Param<Set<String>> sampleParameterSetString = staticParamSet().asString("cfgWrapper.sampleParameter.set.string");
    public final static Param<Set<Boolean>> sampleParameterSetBoolean = staticParamSet().asBool("cfgWrapper.sampleParameter.set.boolean");
    public final static Param<Set<Long>> sampleParameterSetLong = staticParamSet().asLong("cfgWrapper.sampleParameter.set.long");
    public final static Param<Set<Integer>> sampleParameterSetInteger = staticParamSet().asInt("cfgWrapper.sampleParameter.set.integer");
    public final static Param<Set<Short>> sampleParameterSetShort = staticParamSet().asShort("cfgWrapper.sampleParameter.set.short");
    public final static Param<Set<Byte>> sampleParameterSetByte = staticParamSet().asByte("cfgWrapper.sampleParameter.set.byte");
    public final static Param<Set<Float>> sampleParameterSetFloat = staticParamSet().asFloat("cfgWrapper.sampleParameter.set.float");
    public final static Param<Set<Double>> sampleParameterSetDouble = staticParamSet().asDouble("cfgWrapper.sampleParameter.set.double");
    public final static Param<Set<AnyData>> sampleParameterSetAny = staticParam().asAny(new MethodReader(BaseConfigurationTest.class, "getAnyData", "cfgWrapper.sampleParameter.set.any"), "cfgWrapper.sampleParameter.set.any");
    //==================================================================================================================

    @Test
    public void verifyAllOKTest() {
        // run verification for all parameters - no exception is expected
        config.verifyConfiguration();
    }
    //==================================================================================================================

    @Test
    public void verifyInvalidParamsTest() {
        
        verifyFailedParam(sampleParameterFile, "bsvbvsbvsbvdsbvdbsv"); // invalid file
        verifyFailedParam(sampleParameterURL, "httpx:"); // invalid URL
        verifyFailedParam(sampleParameterHEX, "aabbddZ00"); // invalid hex value

        verifyFailedParam(sampleParameterString, null); // missing value
        
        // wrong values
        verifyFailedParam(sampleParameterBoolean, 5);
        verifyFailedParam(sampleParameterLong, "aa");
        verifyFailedParam(sampleParameterInteger, "aa");
        verifyFailedParam(sampleParameterShort, "aa");
        verifyFailedParam(sampleParameterByte, "aa");
        verifyFailedParam(sampleParameterFloat, "aa");
        verifyFailedParam(sampleParameterDouble, "aa");

        verifyFailedParam(sampleParameterArrBoolean, 5);
        verifyFailedParam(sampleParameterArrLong, "aa");
        verifyFailedParam(sampleParameterArrInteger, "aa");
        verifyFailedParam(sampleParameterArrShort, "aa");
        verifyFailedParam(sampleParameterArrByte, "aa");
        verifyFailedParam(sampleParameterArrFloat, "aa");
        verifyFailedParam(sampleParameterArrDouble, "aa");

        verifyFailedParam(sampleParameterSetBoolean, 5);
        verifyFailedParam(sampleParameterSetLong, "aa");
        verifyFailedParam(sampleParameterSetInteger, "aa");
        verifyFailedParam(sampleParameterSetShort, "aa");
        verifyFailedParam(sampleParameterSetByte, "aa");
        verifyFailedParam(sampleParameterSetFloat, "aa");
        verifyFailedParam(sampleParameterSetDouble, "aa");

    }
    //==================================================================================================================

    private Configuration config() {
        return config.getConfiguration();
    }

    private void verifyFailedParam (Param param, Object newValue) {

        ((FoundationCompositeConfiguration)config()).clearCache();

        // preserve current value
        Object currValue = param.getValue();

        // change value in configuration
        config().clearProperty(param.getName());
//        if (newValue != null) {
            config().setProperty(param.getName(), newValue);
//        }

        boolean hadException = false;

        try {
            config.verifyConfiguration();
        } catch (GeneralConfigException ex) {
            hadException = true;

        } finally {

            // change back parameter value in configuration
            config().setProperty(param.getName(), currValue);

        }

        Assert.assertTrue("No validation exception is received though it was expected", hadException);
    }
    //==================================================================================================================

    public static AnyData getAnyData (String prefix) {

        ArrayList<String> outList = new ArrayList();

        Iterator iter = config.getConfiguration().getKeys(prefix);
        while(iter.hasNext()){
            String key = iter.next().toString();
            outList.add(config.getConfiguration().getString(key));
        }

        return new AnyData(outList);
    }
    //==================================================================================================================

    private static class AnyData {

        private ArrayList<String> list;

        private AnyData (ArrayList<String> list) {
            this.list = list;
        }

        @Override
        public String toString() {
            return list.toString();
        }
    }
    //==================================================================================================================

    private static class ConfigImpl extends BaseConfiguration {

        private boolean verifyConfig = false;

        @Override
        public void verifyConfiguration() {

            if (verifyConfig) {
                super.verifyConfiguration();
            }
        }
    }
    //==================================================================================================================

    @Test
    public void validateHexValueTest () {

        validateHexValue(true, "zzTop");
        validateHexValue(false, "abcde123");

        validateHexValue(true, "abc", 2, false, false);
        validateHexValue(true, "xb", 2, false, true);
        validateHexValue(true, "ab", 3, false, true);
        validateHexValue(true, "ab", 7, true, false);
        validateHexValue(true, "ab", 9, true, true);

        validateHexValue(false, "abc", 3, false, true);
        validateHexValue(false, "abc", 3, false, false);
        validateHexValue(false, "ab", 8, true, true);
        validateHexValue(false, "ab", 8, true, false);

        validateHexValue(true, "100000000", 4294967295d);
        validateHexValue(false, "100000000", 4294967296d);

    }

    private void validateHexValue (boolean shouldFail, String value) {
        String errMsg = HEXValidator.validateHexValue(value);

        if (shouldFail) {
            Assert.assertNotNull(errMsg);
        } else {
            Assert.assertNull(errMsg);
        }
    }

    private void validateHexValue (boolean shouldFail, String value, int maxLength, boolean lengthInBits, boolean exactLength) {

        String errMsg = HEXValidator.validateHexValue(value, maxLength, lengthInBits, exactLength);

        if (shouldFail) {
            Assert.assertNotNull(errMsg);
        } else {
            Assert.assertNull(errMsg);
        }

    }

    private void validateHexValue (boolean shouldFail, String value, double maxValue) {
        String errMsg = HEXValidator.validateHexValue(value, maxValue);

        if (shouldFail) {
            Assert.assertNotNull(errMsg);
        } else {
            Assert.assertNull(errMsg);
        }
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

    @AfterClass
    public static void wrapup(){
        clearConfigurtionInConfigurationFactory();
    }
}
