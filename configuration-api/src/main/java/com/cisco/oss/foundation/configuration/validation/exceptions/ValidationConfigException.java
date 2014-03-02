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

package com.cisco.oss.foundation.configuration.validation.exceptions;

/**
 * Input parameters validation exception
 */
public class ValidationConfigException extends GeneralConfigException {

    public ValidationConfigException(String message) {
        super(message, ValidationError);
    }

    public ValidationConfigException(String message, Throwable cause) {
        super(message, cause, ValidationError);
    }

    public ValidationConfigException(Throwable cause) {
        super(cause, ValidationError);
    }

}
