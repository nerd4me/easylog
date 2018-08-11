package com.nerd4me.easylog.intercept;

import com.google.common.base.Throwables;
import com.nerd4me.easylog.common.Constants;
import com.nerd4me.easylog.event.EasyLogEventSource;
import com.nerd4me.easylog.model.EasyLogAttribute;
import com.nerd4me.easylog.model.SpelEvaluableBizLog;
import com.nerd4me.easylog.spel.EasyLogExpressionEvaluator;
import com.nerd4me.easylog.support.EasyLogAttributeSupport;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;

import java.lang.reflect.Method;

/**
 * @author yondy
 * @version 2018/08/11.
 */
public class EasyLogInterceptor implements MethodInterceptor {
    @Autowired
    private EasyLogExpressionEvaluator easyLogExpressionEvaluator;

    @Autowired
    private EasyLogAttributeSupport easyLogAttributeSupport;

    @Autowired
    private EasyLogEventSource easyLogEventSource;

    @Autowired
    private BeanFactory beanFactory;

    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        Class<?> clazz = invocation.getThis().getClass();
        EasyLogAttribute easyLogAttribute = easyLogAttributeSupport.getEasyLogAttribute(method, clazz);
        if (easyLogAttribute == null) {
            return invocation.proceed();
        }
        EvaluationContext evaluationContext = evaluationContext(invocation);
        Throwable businessException = null;
        try {
            Object result = invocation.proceed();
            evaluationContext.setVariable(Constants.RESULT_KEY, result);
            return result;
        } catch (Throwable throwable) {
            businessException = throwable;
            Throwables.throwIfUnchecked(throwable);
            throw new RuntimeException(throwable);
        } finally {
            easyLogEventSource.newEvent(
                    SpelEvaluableBizLog.builder()
                            .evaluationContext(evaluationContext)
                            .easyLogAttribute(easyLogAttribute)
                            .easyLogExpressionEvaluator(easyLogExpressionEvaluator)
                            .elementKey(new AnnotatedElementKey(method, clazz))
                            .businessException(businessException)
                            .build()
            );
        }
    }

    private EvaluationContext evaluationContext(MethodInvocation invocation) {
        return easyLogExpressionEvaluator.createEvaluationContext(
                invocation.getThis(), invocation.getMethod(), invocation.getArguments(), beanFactory);
    }

    /**
     * expression pre warm up
     *
     * @param method
     * @param targetClass
     * @param expression
     */
    public void warmup(Method method, Class<?> targetClass, String expression) {
        easyLogExpressionEvaluator.warmup(method, targetClass, expression);
    }
}
