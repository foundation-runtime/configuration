package com.cisco.vss.foundation.configuration.wrapper.exceptions;

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
