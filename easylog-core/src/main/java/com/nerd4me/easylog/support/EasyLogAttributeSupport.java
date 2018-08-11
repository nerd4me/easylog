package com.nerd4me.easylog.support;

import com.nerd4me.easylog.model.EasyLogAttribute;

import java.lang.reflect.Method;

/**
 * @author yondy
 * @version 2018/08/11.
 */
public interface EasyLogAttributeSupport {
    /**
     * get easy log attribute with method & class
     *
     * @return
     */
    EasyLogAttribute getEasyLogAttribute(Method method, Class<?> targetClass);
}
