package com.cisco.oss.foundation.configuration.validation.params;

/**
 * Created By: kgreen
 * Date-Time: 10/3/13 11:46 AM
 */
public interface ParamValueInterceptor<T> {

    public T onValueRead(T value) ;
}
