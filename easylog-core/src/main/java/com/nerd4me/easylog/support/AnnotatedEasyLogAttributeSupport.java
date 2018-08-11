package com.nerd4me.easylog.support;

import com.nerd4me.easylog.annotations.EasyLog;
import com.nerd4me.easylog.model.EasyLogAttribute;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * @author yondy
 * @version 2018/08/11.
 */
public class AnnotatedEasyLogAttributeSupport extends AbstractEasyLogAttributeSupport {
    @Override
    protected EasyLogAttribute findEasyLogAttribute(Method method) {
        return determineEasyLogAttribute(method);
    }

    private EasyLogAttribute determineEasyLogAttribute(AnnotatedElement annotatedElement) {
        return Optional.ofNullable(annotatedElement)
                .filter(ae -> ae.getAnnotations().length > 0)
                .map(ae -> ae.getAnnotation(EasyLog.class))
                .map(easyLog -> EasyLogAttribute.builder()
                        .topic(easyLog.topic())
                        .name(easyLog.name())
                        .result(easyLog.result())
                        .extra(easyLog.extra())
                        .errorHandler(easyLog.errorHandler())
                        .build())
                .orElse(null);
    }
}
