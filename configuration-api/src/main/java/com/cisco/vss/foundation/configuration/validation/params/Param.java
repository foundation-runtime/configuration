package com.cisco.vss.foundation.configuration.validation.params;

import com.cisco.vss.foundation.configuration.validation.BaseConfiguration;
import com.cisco.vss.foundation.configuration.validation.params.ParamValidators.*;

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
