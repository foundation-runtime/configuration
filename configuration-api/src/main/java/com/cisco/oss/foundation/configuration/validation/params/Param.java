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

package com.cisco.oss.foundation.configuration.validation.params;

import com.cisco.oss.foundation.configuration.validation.BaseConfiguration;
import com.cisco.oss.foundation.configuration.validation.params.ParamValidators.*;

/**
* Created By: kgreen
* Date-Time: 10/3/13 8:52 AM
*/
public class Param<T> {

    private String name;
    private T value;
    private T defValue;
    private boolean staticParam;
    private ParamReaders.ParamReader<T> reader;
    private ParamValidator<T> validator;
    private ParamValueInterceptor<T> valueInterceptor = null;
    //==================================================================================================================

    public Param(ParamReaders.ParamReader<T> reader, String name) {
        this(reader, name, null);
    }

    public Param(ParamReaders.ParamReader<T> reader, String name, T defValue) {
        this(reader, name, defValue, null);
    }

    public Param(ParamReaders.ParamReader<T> reader, String name, T defValue, ParamValidator<T> validator) {
        this.name = name;
        this.defValue = defValue;
        this.reader = reader;
        this.validator = validator;

        // if no default value and no validator - add a "required" validator to make sure that parameter is configured
        if (defValue == null && validator == null) {
            this.validator = ParamValidator.instance(true);
        }

        // add to list of parameters
        BaseConfiguration.addParameter(this);
    }
    //==================================================================================================================

    public String getName() {
        return name;
    }
    //==================================================================================================================

    public T getValue() {

        // if always read parameters from the configuration and to ignore static configuration - re-read parameter's value.
        // Note: values of dynamic parameters are re-read only when configurationChanged() event is invoked.
        if (! BaseConfiguration.isEnableStaticConfiguration()) {
            readValue();
        }
        return value;
    }
    //==================================================================================================================

    public void readValue() {

        if (defValue == null) {
            value = reader.readValue(name);
        } else {
            value = reader.readValue(name, defValue);
        }

        if (valueInterceptor != null) {
            value = valueInterceptor.onValueRead(value);
        }
    }
    //==================================================================================================================

    public Param<T> setValueInterceptor(ParamValueInterceptor<T> valueInterceptor) {
        this.valueInterceptor = valueInterceptor;
        return this;
    }
    //==================================================================================================================

    public boolean isStatic () {
        return staticParam;
    }

    public void setStatic (boolean staticParam) {
        this.staticParam = staticParam;
    }
    //==================================================================================================================

    public void validate() {

        if (validator != null) {
            validator.validate(name, value);
        }
    }
    //==================================================================================================================

}
