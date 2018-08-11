package com.nerd4me.easylog.support;

import com.google.common.collect.Maps;
import com.nerd4me.easylog.model.EasyLogAttribute;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Optional;

/**
 * @author yondy
 * @version 2018/08/11.
 */
public abstract class AbstractEasyLogAttributeSupport implements EasyLogAttributeSupport {
    /**
     * empty log attribute, just for cache
     */
    private final static EasyLogAttribute EMPTY_ATTRIBUTE = EasyLogAttribute.empty();

    /**
     * cache for log attribute
     */
    private final Map<Pair<Method, Class<?>>, EasyLogAttribute> attributeCache = Maps.newConcurrentMap();

    @Override
    public EasyLogAttribute getEasyLogAttribute(Method method, Class<?> targetClass) {
        Pair<Method, Class<?>> cacheKey = getCacheKey(method, targetClass);
        return Optional.of(attributeCache.computeIfAbsent(cacheKey, this::computeCacheFriendlyEasyLogAttribute))
                .filter(t -> t != EMPTY_ATTRIBUTE)
                .orElse(null);
    }

    private EasyLogAttribute computeCacheFriendlyEasyLogAttribute(Pair<Method, Class<?>> methodClassPair) {
        return Optional.ofNullable(computeEasyLogAttribute(methodClassPair.getLeft(), methodClassPair.getRight()))
                .orElse(EMPTY_ATTRIBUTE);
    }

    private EasyLogAttribute computeEasyLogAttribute(Method method, Class<?> targetClass) {
        if(allowPublicMethodsOnly() && !Modifier.isPublic(method.getModifiers())) {
            return null;
        }
        Class<?> userClass = ClassUtils.getUserClass(targetClass);
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, userClass);
        Method bridgedSpecificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
        return Optional.ofNullable(findEasyLogAttribute(bridgedSpecificMethod))
                .orElseGet(() -> bridgedSpecificMethod != method ? findEasyLogAttribute(method) : null);
    }

    protected abstract EasyLogAttribute findEasyLogAttribute(Method method);

    private Pair<Method, Class<?>> getCacheKey(Method method, Class<?> targetClass) {
        return ImmutablePair.of(method, targetClass);
    }

    private boolean allowPublicMethodsOnly() {
        return false;
    }
}
