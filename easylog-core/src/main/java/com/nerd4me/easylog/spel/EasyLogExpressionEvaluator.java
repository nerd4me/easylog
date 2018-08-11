package com.nerd4me.easylog.spel;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

import java.lang.reflect.Method;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author yondy
 * @version 2018/08/11.
 */
public class EasyLogExpressionEvaluator extends CachedExpressionEvaluator {
    private static final Logger log = LoggerFactory.getLogger("easyLog.evaluator");

    /**
     * Spring expression cache map
     */
    private final Map<ExpressionKey, Expression> expressionCache = Maps.newConcurrentMap();

    public EvaluationContext createEvaluationContext(Object rootObject, Method method, Object[] args, BeanFactory beanFactory) {
        MethodBasedEvaluationContext evaluationContext =
                new MethodBasedEvaluationContext(rootObject, method, args, getParameterNameDiscoverer());
        if (beanFactory != null) {
            evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
        }
        return evaluationContext;
    }

    public Expression getExpression(AnnotatedElementKey elementKey, String expression) {
        if(Strings.isNullOrEmpty(expression)) {
            log.error("invalid expression with element key: {}", elementKey);
            return null;
        }
        return getExpression(expressionCache, elementKey, expression);
    }

    public <T> T eval(AnnotatedElementKey elementKey, String expression, EvaluationContext evaluationContext,
                      Class<? extends T> clazz) {
        checkNotNull(elementKey);
        checkArgument(!Strings.isNullOrEmpty(expression), "invalid expression !!!");
        checkNotNull(evaluationContext);
        checkNotNull(clazz);
        return getExpression(elementKey, expression).getValue(evaluationContext, clazz);
    }

    public void warmup(Method method, Class<?> targetClass, String expression) {
        getExpression(createKey(method, targetClass), expression);
    }

    private static AnnotatedElementKey createKey(Method method, Class<?> targetClass) {
        checkNotNull(method);
        checkNotNull(targetClass);
        return new AnnotatedElementKey(method, targetClass);
    }
}
