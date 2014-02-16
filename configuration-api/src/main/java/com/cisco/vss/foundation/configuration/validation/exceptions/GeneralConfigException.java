package com.cisco.vss.foundation.configuration.validation.exceptions;

import com.cisco.vss.foundation.application.exception.ErrorCode;
import com.cisco.vss.foundation.application.exception.RuntimeApplicationException;

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
