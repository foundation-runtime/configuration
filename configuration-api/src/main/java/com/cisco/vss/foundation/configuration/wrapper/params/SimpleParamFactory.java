package com.cisco.vss.foundation.configuration.wrapper.params;

import static com.cisco.vss.foundation.configuration.wrapper.params.ParamReaders.*;
import static com.cisco.vss.foundation.configuration.wrapper.params.ParamValidators.*;

/**
 * Created By: kgreen
 * Date-Time: 10/3/13 9:48 AM
 */
public class SimpleParamFactory extends ParamFactory {

    protected SimpleParamFactory(ParamType paramType) {
        super(paramType);
    }

    public Param<String> asString (String name) {
        return getParam(stringReader, name);
    }

    public Param<String> asString (String name, String defValue) {
        return getParam(stringReader, name, defValue);
    }

    public Param<String> asString (String name, String defValue, ParamValidator<String> validator) {
        return getParam(stringReader, name, defValue, validator);
    }
    //****************************************************************************************************

    public Param<Integer> asInt (String name) {
        return getParam(intReader, name);
    }

    public Param<Integer> asInt (String name, Integer defValue) {
        return getParam(intReader, name, defValue);
    }

    public Param<Integer> asInt (String name, Integer defValue, ParamValidator<Integer> validator) {
        return getParam(intReader, name, defValue, validator);
    }
    //****************************************************************************************************

    public Param<Long> asLong (String name) {
        return getParam(longReader, name);
    }

    public Param<Long> asLong (String name, Long defValue) {
        return getParam(longReader, name, defValue);
    }

    public Param<Long> asLong (String name, Long defValue, ParamValidator<Long> validator) {
        return getParam(longReader, name, defValue, validator);
    }
    //****************************************************************************************************

    public Param<Boolean> asBool (String name) {
        return getParam(booleanReader, name);
    }

    public Param<Boolean> asBool (String name, Boolean defValue) {
        return getParam(booleanReader, name, defValue);
    }

    public Param<Boolean> asBool (String name, Boolean defValue, ParamValidator<Boolean> validator) {
        return getParam(booleanReader, name, defValue, validator);
    }
    //****************************************************************************************************

    public Param<Float> asFloat (String name) {
        return getParam(floatReader, name);
    }

    public Param<Float> asFloat (String name, Float defValue) {
        return getParam(floatReader, name, defValue);
    }

    public Param<Float> asFloat (String name, Float defValue, ParamValidator<Float> validator) {
        return getParam(floatReader, name, defValue, validator);
    }
    //****************************************************************************************************

    public Param<Double> asDouble (String name) {
        return getParam(doubleReader, name);
    }

    public Param<Double> asDouble (String name, Double defValue) {
        return getParam(doubleReader, name, defValue);
    }

    public Param<Double> asDouble (String name, Double defValue, ParamValidator<Double> validator) {
        return getParam(doubleReader, name, defValue, validator);
    }
    //****************************************************************************************************

    public Param<Short> asShort (String name) {
        return getParam(shortReader, name);
    }

    public Param<Short> asShort (String name, Short defValue) {
        return getParam(shortReader, name, defValue);
    }

    public Param<Short> asShort (String name, Short defValue, ParamValidator<Short> validator) {
        return getParam(shortReader, name, defValue, validator);
    }
    //****************************************************************************************************

    public Param<Byte> asByte (String name) {
        return getParam(byteReader, name);
    }

    public Param<Byte> asByte (String name, Byte defValue) {
        return getParam(byteReader, name, defValue);
    }

    public Param<Byte> asByte (String name, Byte defValue, ParamValidator<Byte> validator) {
        return getParam(byteReader, name, defValue, validator);
    }
    //****************************************************************************************************

    public<T> Param<T> asAny (ParamReader<T> reader, String name) {
        return getParam(reader, name);
    }

    public<T> Param<T> asAny (ParamReader<T> reader, String name, T defValue) {
        return getParam(reader, name, defValue);
    }

    public<T> Param<T> asAny (ParamReader<T> reader, String name, T defValue, ParamValidator<T> validator) {
        return getParam(reader, name, defValue, validator);
    }
    //****************************************************************************************************

}
