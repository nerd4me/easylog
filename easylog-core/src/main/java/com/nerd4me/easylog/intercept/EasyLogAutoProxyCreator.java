package com.nerd4me.easylog.intercept;

import com.google.common.base.Strings;
import com.nerd4me.easylog.model.EasyLogAttribute;
import com.nerd4me.easylog.support.EasyLogAttributeSupport;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Method;
import java.util.stream.Stream;

/**
 * @author yondy
 * @version 2018/08/11.
 */
public class EasyLogAutoProxyCreator extends AbstractAutoProxyCreator {
    @Value("${easy.log.enabled:true}")
    private boolean enabled;

    @Autowired
    private EasyLogInterceptor easyLogInterceptor;

    @Autowired
    private EasyLogAttributeSupport easyLogAttributeSupport;

    @Override
    protected Object[] getAdvicesAndAdvisorsForBean(Class<?> beanClass, String beanName, TargetSource customTargetSource) throws BeansException {
        if(enabled && easyLogInterceptor != null && matches(beanClass)) {
            return new Object[]{easyLogInterceptor};
        }
        return null;
    }

    private boolean matches(Class<?> targetClass) {
        boolean matched = false;
        for (Method method : targetClass.getMethods()) {
            // not returned directly for expression warm up
            if(matches(method, targetClass)) {
                matched = true;
            }
        }
        return matched;
    }

    private boolean matches(Method method, Class<?> targetClass) {
        EasyLogAttribute easyLogAttribute = easyLogAttributeSupport.getEasyLogAttribute(method, targetClass);
        if(easyLogAttribute == null) {
            return false;
        }
        Stream.of(easyLogAttribute.getExtra(), easyLogAttribute.getResult())
                .filter(expression -> !Strings.isNullOrEmpty(expression))
                .forEach(expression -> easyLogInterceptor.warmup(method, targetClass, expression));
        return true;
    }
}
