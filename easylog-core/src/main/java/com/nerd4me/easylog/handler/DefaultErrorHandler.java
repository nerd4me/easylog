package com.nerd4me.easylog.handler;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import static com.nerd4me.easylog.common.Constants.INTERNAL_ERROR_CODE;

/**
 * @author yondy
 * @version 2018/08/11.
 */
public class DefaultErrorHandler implements ErrorHandler {
    @Override
    public Pair<String, String> handler(Throwable throwable) {
        return ImmutablePair.of(INTERNAL_ERROR_CODE, throwable.getMessage());
    }
}
