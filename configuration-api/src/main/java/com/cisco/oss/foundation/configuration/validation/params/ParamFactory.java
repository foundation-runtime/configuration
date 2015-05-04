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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.cisco.oss.foundation.configuration.validation.params.ParamReaders.*;
import static com.cisco.oss.foundation.configuration.validation.params.ParamValidators.*;

/**
 * Created By: kgreen
 * Date-Time: 11/6/13 5:17 PM
 */
public class ParamFactory {

    public final static SimpleParamFactory staticFactory = new SimpleParamFactory(ParamType.Static);
    public final static SimpleParamFactory dynamicFactory = new SimpleParamFactory(ParamType.Dynamic);

    public final static ParamListFactory listStaticFactory = new ParamListFactory(ParamType.Static);
    public final static ParamListFactory listDynamicFactory = new ParamListFactory(ParamType.Dynamic);

    public final static ParamSetFactory setStaticFactory = new ParamSetFactory(ParamType.Static);
    public final static ParamSetFactory setDynamicFactory = new ParamSetFactory(ParamType.Dynamic);

    protected static enum ParamType {
        Dynamic,Static;
    }

    private ParamType paramType;
    //==================================================================================================================

    protected ParamFactory(ParamType paramType) {
        this.paramType = paramType;
    }
    //==================================================================================================================

    protected Param getParam (ParamReader reader, String name) {
        return getParam(new Param(reader, name));
    }

    protected Param getParam (ParamReader reader, String name, Object defValue) {
        return getParam(new Param(reader, name, defValue));
    }

    protected Param getParam (ParamReader reader, String name, Object defValue, ParamValidator validator) {
        return getParam(new Param(reader, name, defValue, validator));
    }

    private Param getParam (Param param) {
        param.setStatic(paramType == ParamType.Static);
        return param;
    }
    //==================================================================================================================

    protected<T> List<T> toList (T value) {

        if (value == null) {
            return null;
        }

        return Arrays.asList(value);
    }

    protected<T> List<T> toList (T ... values) {

        if (values.length == 0 || values[0] == null) {
            return null;
        }

        return Arrays.asList(values);
    }
    //==================================================================================================================

    protected<T, C extends Collection<T>> ParamListValidator<T, C> getListValidator(ParamValidator<T> validator) {

        if (validator == null) {
            return null;
        }

        return new ParamListValidator<T, C>(validator);
    }
    //==================================================================================================================

}
