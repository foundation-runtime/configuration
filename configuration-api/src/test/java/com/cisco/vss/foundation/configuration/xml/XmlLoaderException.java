package com.cisco.vss.foundation.configuration.xml;

public class XmlLoaderException extends Exception {

    private static final long serialVersionUID = -2553813401121282691L;

    public XmlLoaderException() {
    }
    public XmlLoaderException(String message) {
        super(message);
    }
    public XmlLoaderException(Throwable cause) {
        super(cause);
    }
    public XmlLoaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
