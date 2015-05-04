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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.cisco.oss.foundation.configuration.validation.params.ParamReaders.*;
import static com.cisco.oss.foundation.configuration.validation.params.ParamValidators.*;

/**
 * Created By: kgreen
 * Date-Time: 11/6/13 6:00 PM
 */
public class ParamSetFactory extends ParamFactory {

    protected ParamSetFactory(ParamType paramType) {
        super(paramType);
    }
    //==================================================================================================================

    private<T> Set<T> toSet (T value) {
        return toSet(toList(value));
    }

    private<T> Set<T> toSet (T ... values) {
        return toSet(toList(values));
    }

    private Set toSet (List list) {

        if (list == null) {
            return null;
        }

        return new LinkedHashSet(list);
    }
    //==================================================================================================================

    public Param<Set<String>> asString (String name) {
        return getParam(stringSetReader, name);
    }

    public Param<Set<String>> asString (String name, String defValue) {
        return getParam(stringSetReader, name, toSet(defValue));
    }

    public Param<Set<String>> asString (String name, String[] defValue) {
        return getParam(stringSetReader, name, toSet(defValue));
    }

    public Param<Set<String>> asString (String name, String defValue, ParamValidator<String> validator) {
        return getParam(stringSetReader, name, toSet(defValue), getListValidator(validator));
    }

    public Param<Set<String>> asString (String name, String[] defValue, ParamValidator<String> validator) {
        return getParam(stringSetReader, name, toSet(defValue), getListValidator(validator));
    }
    //==================================================================================================================

    public Param<Set<Integer>> asInt (String name) {
        return getParam(intSetReader, name);
    }

    public Param<Set<Integer>> asInt (String name, Integer defValue) {
        return getParam(intSetReader, name, toSet(defValue));
    }

    public Param<Set<Integer>> asInt (String name, Integer[] defValue) {
        return getParam(intSetReader, name, toSet(defValue));
    }

    public Param<Set<Integer>> asInt (String name, Integer defValue, ParamValidator<Integer> validator) {
        return getParam(intSetReader, name, toSet(defValue), getListValidator(validator));
    }

    public Param<Set<Integer>> asInt (String name, Integer[] defValue, ParamValidator<Integer> validator) {
        return getParam(intSetReader, name, toSet(defValue), getListValidator(validator));
    }
    //==================================================================================================================

    public Param<Set<Long>> asLong (String name) {
        return getParam(longSetReader, name);
    }

    public Param<Set<Long>> asLong (String name, Long defValue) {
        return getParam(longSetReader, name, toSet(defValue));
    }

    public Param<Set<Long>> asLong (String name, Long[] defValue) {
        return getParam(longSetReader, name, toSet(defValue));
    }

    public Param<Set<Long>> asLong (String name, Long defValue, ParamValidator<Long> validator) {
        return getParam(longSetReader, name, toSet(defValue), getListValidator(validator));
    }

    public Param<Set<Long>> asLong (String name, Long[] defValue, ParamValidator<Long> validator) {
        return getParam(longSetReader, name, toSet(defValue), getListValidator(validator));
    }
    //==================================================================================================================

    public Param<Set<Boolean>> asBool (String name) {
        return getParam(booleanSetReader, name);
    }

    public Param<Set<Boolean>> asBool (String name, Boolean defValue) {
        return getParam(booleanSetReader, name, toSet(defValue));
    }

    public Param<Set<Boolean>> asBool (String name, Boolean[] defValue) {
        return getParam(booleanSetReader, name, toSet(defValue));
    }

    public Param<Set<Boolean>> asBool (String name, Boolean defValue, ParamValidator<Boolean> validator) {
        return getParam(booleanSetReader, name, toSet(defValue), getListValidator(validator));
    }

    public Param<Set<Boolean>> asBool (String name, Boolean[] defValue, ParamValidator<Boolean> validator) {
        return getParam(booleanSetReader, name, toSet(defValue), getListValidator(validator));
    }
    //==================================================================================================================

    public Param<Set<Float>> asFloat (String name) {
        return getParam(floatSetReader, name);
    }

    public Param<Set<Float>> asFloat (String name, Float defValue) {
        return getParam(floatSetReader, name, toSet(defValue));
    }

    public Param<Set<Float>> asFloat (String name, Float[] defValue) {
        return getParam(floatSetReader, name, toSet(defValue));
    }

    public Param<Set<Float>> asFloat (String name, Float defValue, ParamValidator<Float> validator) {
        return getParam(floatSetReader, name, toSet(defValue), getListValidator(validator));
    }

    public Param<Set<Float>> asFloat (String name, Float[] defValue, ParamValidator<Float> validator) {
        return getParam(floatSetReader, name, toSet(defValue), getListValidator(validator));
    }
    //==================================================================================================================

    public Param<Set<Double>> asDouble (String name) {
        return getParam(doubleSetReader, name);
    }

    public Param<Set<Double>> asDouble (String name, Double defValue) {
        return getParam(doubleSetReader, name, toSet(defValue));
    }

    public Param<Set<Double>> asDouble (String name, Double[] defValue) {
        return getParam(doubleSetReader, name, toSet(defValue));
    }

    public Param<Set<Double>> asDouble (String name, Double defValue, ParamValidator<Double> validator) {
        return getParam(doubleSetReader, name, toSet(defValue), getListValidator(validator));
    }

    public Param<Set<Double>> asDouble (String name, Double[] defValue, ParamValidator<Double> validator) {
        return getParam(doubleSetReader, name, toSet(defValue), getListValidator(validator));
    }
    //==================================================================================================================

    public Param<Set<Short>> asShort (String name) {
        return getParam(shortSetReader, name);
    }

    public Param<Set<Short>> asShort (String name, Short defValue) {
        return getParam(shortSetReader, name, toSet(defValue));
    }

    public Param<Set<Short>> asShort (String name, Short[] defValue) {
        return getParam(shortSetReader, name, toSet(defValue));
    }

    public Param<Set<Short>> asShort (String name, Short defValue, ParamValidator<Short> validator) {
        return getParam(shortSetReader, name, toSet(defValue), getListValidator(validator));
    }

    public Param<Set<Short>> asShort (String name, Short[] defValue, ParamValidator<Short> validator) {
        return getParam(shortSetReader, name, toSet(defValue), getListValidator(validator));
    }
    //==================================================================================================================

    public Param<Set<Byte>> asByte (String name) {
        return getParam(byteSetReader, name);
    }

    public Param<Set<Byte>> asByte (String name, Byte defValue) {
        return getParam(byteSetReader, name, toSet(defValue));
    }

    public Param<Set<Byte>> asByte (String name, Byte[] defValue) {
        return getParam(byteSetReader, name, toSet(defValue));
    }

    public Param<Set<Byte>> asByte (String name, Byte defValue, ParamValidator<Byte> validator) {
        return getParam(byteSetReader, name, toSet(defValue), getListValidator(validator));
    }

    public Param<Set<Byte>> asByte (String name, Byte[] defValue, ParamValidator<Byte> validator) {
        return getParam(byteSetReader, name, toSet(defValue), getListValidator(validator));
    }
    //==================================================================================================================

    public<T> Param<Set<T>> asAny (ParamReader<Set<T>> reader, String name) {
        return getParam(reader, name);
    }

    public<T> Param<Set<T>> asAny (ParamReader<Set<T>> reader, String name, T defValue) {
        return getParam(reader, name, toSet(defValue));
    }

    public<T> Param<Set<T>> asAny (ParamReader<Set<T>> reader, String name, T defValue, ParamValidator<T> validator) {
        return getParam(reader, name, toSet(defValue), getListValidator(validator));
    }
    //****************************************************************************************************

}
