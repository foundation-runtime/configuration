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

package com.cisco.oss.foundation.configuration.validation.exceptions;

import com.cisco.oss.foundation.application.exception.ErrorCode;
import com.cisco.oss.foundation.application.exception.RuntimeApplicationException;

/**
 * general exception for Key Store operations
 */
public class GeneralConfigException extends RuntimeApplicationException {

    public final static ErrorCode GeneralConfigError = new ErrorCode("CommonConfigurationWrapper", 1701);
    public final static ErrorCode ValidationError = new ErrorCode("CommonConfigurationWrapper", 1705);
    //==================================================================================================================

    public GeneralConfigException(String message) {
        this(message, GeneralConfigError);
    }

    public GeneralConfigException(String message, Throwable cause) {
        this(message, cause, GeneralConfigError);
    }

    public GeneralConfigException(Throwable cause) {
        this(cause, GeneralConfigError);
    }

    protected GeneralConfigException(Throwable e, ErrorCode errorCode) {
        this(e.toString(), e, errorCode);
    }

    protected GeneralConfigException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    protected GeneralConfigException(String message, Throwable e, ErrorCode errorCode) {
        super(message, e, errorCode);
    }
    //==================================================================================================================

    public static void convertToRuntimeException(Throwable t) {
        GeneralConfigException.<RuntimeException>convertException(t);
    }

    private static<T extends Throwable> void convertException (Throwable t) throws T {
        throw (T) t;
    }
    //==================================================================================================================

}
