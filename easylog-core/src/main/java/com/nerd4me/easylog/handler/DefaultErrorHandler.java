package com.nerd4me.easylog.handler;

/**
 * @author yondy
 * @version 2018/08/11.
 */
public class DefaultErrorHandler implements ErrorHandler<String> {
    @Override
    public String handler(Throwable throwable) {
        return "FAILED";
    }
}
