package com.nerd4me.easylog.model;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.nerd4me.easylog.common.Constants;
import com.nerd4me.easylog.handler.ErrorHandler;
import com.nerd4me.easylog.spel.EasyLogExpressionEvaluator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;

import java.util.Map;
import java.util.Optional;

/**
 * @author yondy
 * @version 2018/08/11.
 */
@AllArgsConstructor
@Builder
public class SpelEvaluableBizLog implements BizLog {
    /**
     * error handler cache
     */
    private static final Map<Class<? extends ErrorHandler>, ErrorHandler> errorHandlerCache
            = Maps.newConcurrentMap();

    private final EvaluationContext evaluationContext;
    private final EasyLogAttribute easyLogAttribute;
    private final EasyLogExpressionEvaluator easyLogExpressionEvaluator;
    private final AnnotatedElementKey elementKey;
    private final Throwable businessException;

    /**
     * content cache
     */
    private Map<String, Object> content;

    @Override
    public String getTopic() {
        return easyLogAttribute.getTopic();
    }

    @Override
    public String getName() {
        return easyLogAttribute.getName();
    }

    @Override
    public Map<String, Object> getContent() {
        if (content != null) {
            return content;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> extraInfo = Optional.ofNullable(easyLogAttribute.getExtra())
                .filter(extra -> !Strings.isNullOrEmpty(extra))
                .map(extra -> easyLogExpressionEvaluator.eval(
                        elementKey, easyLogAttribute.getExtra(), evaluationContext, Map.class))
                .map(extra -> (Map<String, Object>) extra)
                .orElseGet(Maps::newHashMap);
        this.content = ImmutableMap.<String, Object>builder()
                .putAll(extraInfo)
                .putAll(computeResult())
                .build();
        return content;
    }

    private Map<String, Object> computeResult() {
        Map<String, Object> result = Maps.newHashMap();
        if(businessException != null) {
            ErrorHandler errorHandler = errorHandlerCache.computeIfAbsent(easyLogAttribute.getErrorHandler(), BeanUtils::instantiateClass);
            Pair<String, String> codeMsg = errorHandler.handler(businessException);
            result.put(Constants.RESULT_KEY, Constants.RESULT_FAILED);
            result.put(Constants.ERR_CODE_KEY, codeMsg.getLeft());
            result.put(Constants.ERR_MSG_KEY, codeMsg.getRight());
        } else if(!Strings.isNullOrEmpty(easyLogAttribute.getResult())) {
            result.put(
                    Constants.RESULT_KEY,
                    easyLogExpressionEvaluator.eval(elementKey, easyLogAttribute.getResult(), evaluationContext, Object.class)
            );
        }
        return result;
    }
}
