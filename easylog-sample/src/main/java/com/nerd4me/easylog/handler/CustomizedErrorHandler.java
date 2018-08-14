package com.nerd4me.easylog.handler;

import com.nerd4me.easylog.common.Constants;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @author yondy
 * @version 2018/08/11.
 */
public class CustomizedErrorHandler implements ErrorHandler {
    @Override
    public Pair<String, String> handler(Throwable throwable) {
        if(throwable instanceof IllegalArgumentException) {
            return ImmutablePair.of("ILLEGAL_ARGUMENT", throwable.getMessage());
        }
        return ImmutablePair.of(Constants.INTERNAL_ERROR_CODE, throwable.getMessage());
    }
}
