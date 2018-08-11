package com.nerd4me.easylog.handler;

/**
 * business exception handler
 *
 * @author yondy
 * @version 2018/08/11.
 */
public interface ErrorHandler<T> {
    T handler(Throwable throwable);
}
