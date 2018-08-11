package com.nerd4me.easylog.handler;

/**
 * @author yondy
 * @version 2018/08/11.
 */
public class ErrorHandlers {
    private ErrorHandlers() {
        throw new UnsupportedOperationException();
    }

    public static final ErrorHandler<String> DEFAULT_STRING_HANDLER = e -> "FAILED";
}
