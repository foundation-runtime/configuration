package com.cisco.vss.foundation.configuration.xml;

public class XmlException extends Exception {

    private static final long serialVersionUID = -7896174002995027483L;

    public XmlException() {
    }

    public XmlException(String message) {
        super(message);
    }

    public XmlException(Throwable cause) {
        super(cause);
    }

    public XmlException(String message, Throwable cause) {
        super(message, cause);
    }
}
