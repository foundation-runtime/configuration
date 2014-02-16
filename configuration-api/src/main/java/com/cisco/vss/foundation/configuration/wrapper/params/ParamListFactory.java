package com.cisco.vss.foundation.configuration.wrapper.params;

import java.util.List;

import static com.cisco.vss.foundation.configuration.wrapper.params.ParamReaders.*;
import static com.cisco.vss.foundation.configuration.wrapper.params.ParamValidators.*;

/**
 * Created By: kgreen
 * Date-Time: 11/6/13 6:00 PM
 */
public class ParamListFactory extends ParamFactory {

    protected ParamListFactory(ParamType paramType) {
        super(paramType);
    }
    //==================================================================================================================

    public Param<List<String>> asString (String name) {
        return getParam(stringListReader, name);
    }

    public Param<List<String>> asString (String name, String defValue) {
        return getParam(stringListReader, name, toList(defValue));
    }

    public Param<List<String>> asString (String name, String[] defValue) {
        return getParam(stringListReader, name, toList(defValue));
    }

    public Param<List<String>> asString (String name, String defValue, ParamValidator<String> validator) {
        return getParam(stringListReader, name, toList(defValue), getListValidator(validator));
    }

    public Param<List<String>> asString (String name, String[] defValue, ParamValidator<String> validator) {
        return getParam(stringListReader, name, toList(defValue), getListValidator(validator));
    }
    //==================================================================================================================

    public Param<List<Integer>> asInt (String name) {
        return getParam(intListReader, name);
    }

    public Param<List<Integer>> asInt (String name, Integer defValue) {
        return getParam(intListReader, name, toList(defValue));
    }

    public Param<List<Integer>> asInt (String name, Integer[] defValue) {
        return getParam(intListReader, name, toList(defValue));
    }

    public Param<List<Integer>> asInt (String name, Integer defValue, ParamValidator<Integer> validator) {
        return getParam(intListReader, name, toList(defValue), getListValidator(validator));
    }

    public Param<List<Integer>> asInt (String name, Integer[] defValue, ParamValidator<Integer> validator) {
        return getParam(intListReader, name, toList(defValue), getListValidator(validator));
    }
    //==================================================================================================================

    public Param<List<Long>> asLong (String name) {
        return getParam(longListReader, name);
    }

    public Param<List<Long>> asLong (String name, Long defValue) {
        return getParam(longListReader, name, toList(defValue));
    }

    public Param<List<Long>> asLong (String name, Long[] defValue) {
        return getParam(longListReader, name, toList(defValue));
    }

    public Param<List<Long>> asLong (String name, Long defValue, ParamValidator<Long> validator) {
        return getParam(longListReader, name, toList(defValue), getListValidator(validator));
    }

    public Param<List<Long>> asLong (String name, Long[] defValue, ParamValidator<Long> validator) {
        return getParam(longListReader, name, toList(defValue), getListValidator(validator));
    }
    //==================================================================================================================

    public Param<List<Boolean>> asBool (String name) {
        return getParam(booleanListReader, name);
    }

    public Param<List<Boolean>> asBool (String name, Boolean defValue) {
        return getParam(booleanListReader, name, toList(defValue));
    }

    public Param<List<Boolean>> asBool (String name, Boolean[] defValue) {
        return getParam(booleanListReader, name, toList(defValue));
    }

    public Param<List<Boolean>> asBool (String name, Boolean defValue, ParamValidator<Boolean> validator) {
        return getParam(booleanListReader, name, toList(defValue), getListValidator(validator));
    }

    public Param<List<Boolean>> asBool (String name, Boolean[] defValue, ParamValidator<Boolean> validator) {
        return getParam(booleanListReader, name, toList(defValue), getListValidator(validator));
    }
    //==================================================================================================================

    public Param<List<Float>> asFloat (String name) {
        return getParam(floatListReader, name);
    }

    public Param<List<Float>> asFloat (String name, Float defValue) {
        return getParam(floatListReader, name, toList(defValue));
    }

    public Param<List<Float>> asFloat (String name, Float[] defValue) {
        return getParam(floatListReader, name, toList(defValue));
    }

    public Param<List<Float>> asFloat (String name, Float defValue, ParamValidator<Float> validator) {
        return getParam(floatListReader, name, toList(defValue), getListValidator(validator));
    }

    public Param<List<Float>> asFloat (String name, Float[] defValue, ParamValidator<Float> validator) {
        return getParam(floatListReader, name, toList(defValue), getListValidator(validator));
    }
    //==================================================================================================================

    public Param<List<Double>> asDouble (String name) {
        return getParam(doubleListReader, name);
    }

    public Param<List<Double>> asDouble (String name, Double defValue) {
        return getParam(doubleListReader, name, toList(defValue));
    }

    public Param<List<Double>> asDouble (String name, Double[] defValue) {
        return getParam(doubleListReader, name, toList(defValue));
    }

    public Param<List<Double>> asDouble (String name, Double defValue, ParamValidator<Double> validator) {
        return getParam(doubleListReader, name, toList(defValue), getListValidator(validator));
    }

    public Param<List<Double>> asDouble (String name, Double[] defValue, ParamValidator<Double> validator) {
        return getParam(doubleListReader, name, toList(defValue), getListValidator(validator));
    }
    //==================================================================================================================

    public Param<List<Short>> asShort (String name) {
        return getParam(shortListReader, name);
    }

    public Param<List<Short>> asShort (String name, Short defValue) {
        return getParam(shortListReader, name, toList(defValue));
    }

    public Param<List<Short>> asShort (String name, Short[] defValue) {
        return getParam(shortListReader, name, toList(defValue));
    }

    public Param<List<Short>> asShort (String name, Short defValue, ParamValidator<Short> validator) {
        return getParam(shortListReader, name, toList(defValue), getListValidator(validator));
    }

    public Param<List<Short>> asShort (String name, Short[] defValue, ParamValidator<Short> validator) {
        return getParam(shortListReader, name, toList(defValue), getListValidator(validator));
    }
    //==================================================================================================================

    public Param<List<Byte>> asByte (String name) {
        return getParam(byteListReader, name);
    }

    public Param<List<Byte>> asByte (String name, Byte defValue) {
        return getParam(byteListReader, name, toList(defValue));
    }

    public Param<List<Byte>> asByte (String name, Byte[] defValue) {
        return getParam(byteListReader, name, toList(defValue));
    }

    public Param<List<Byte>> asByte (String name, Byte defValue, ParamValidator<Byte> validator) {
        return getParam(byteListReader, name, toList(defValue), getListValidator(validator));
    }

    public Param<List<Byte>> asByte (String name, Byte[] defValue, ParamValidator<Byte> validator) {
        return getParam(byteListReader, name, toList(defValue), getListValidator(validator));
    }
    //==================================================================================================================

    public<T> Param<List<T>> asAny (ParamReader<List<T>> reader, String name) {
        return getParam(reader, name);
    }

    public<T> Param<List<T>> asAny (ParamReader<List<T>> reader, String name, T defValue) {
        return getParam(reader, name, toList(defValue));
    }

    public<T> Param<List<T>> asAny (ParamReader<List<T>> reader, String name, T defValue, ParamValidator<T> validator) {
        return getParam(reader, name, toList(defValue), getListValidator(validator));
    }
    //****************************************************************************************************

}
