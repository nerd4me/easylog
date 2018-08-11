package com.nerd4me.easylog.handler;

import org.apache.commons.lang3.tuple.Pair;

/**
 * business exception handler
 *
 * @author yondy
 * @version 2018/08/11.
 */
public interface ErrorHandler {
    Pair<String, String> handler(Throwable throwable);
}
