package com.nerd4me.easylog.config;

import com.nerd4me.easylog.event.EasyLogEventListener;
import com.nerd4me.easylog.event.EasyLogEventSource;
import com.nerd4me.easylog.intercept.EasyLogAutoProxyCreator;
import com.nerd4me.easylog.intercept.EasyLogInterceptor;
import com.nerd4me.easylog.model.BizLog;
import com.nerd4me.easylog.spel.EasyLogExpressionEvaluator;
import com.nerd4me.easylog.support.AnnotatedEasyLogAttributeSupport;
import com.nerd4me.easylog.support.EasyLogAttributeSupport;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * @author yondy
 * @version 2018/08/11.
 */
public abstract class EasyLogConfiguration {
    @Bean
    public EasyLogInterceptor easyLogInterceptor() {
        return new EasyLogInterceptor();
    }

    @Bean
    public EasyLogExpressionEvaluator easyLogExpressionEvaluator() {
        return new EasyLogExpressionEvaluator();
    }

    @Bean
    public EasyLogAttributeSupport annotatedEasyLogAttributeSupport() {
        return new AnnotatedEasyLogAttributeSupport();
    }

    @Bean
    public EasyLogEventSource easyLogEventSource() {
        return new EasyLogEventSource();
    }

    @Bean
    public EasyLogAutoProxyCreator easyLogAutoProxyCreator() {
        return new EasyLogAutoProxyCreator();
    }

    @Bean
    public Flux<BizLog> easyLogStream() {
        return Flux.<BizLog>create(sink -> {
            try {
                easyLogEventSource().register(new EasyLogEventListener() {
                    @Override
                    public void onNewEvent(BizLog bizLog) {
                        sink.next(bizLog);
                    }

                    @Override
                    public void onComplete() {
                        sink.complete();
                    }
                });
            } catch (Throwable throwable) {
                sink.error(throwable);
            }
        }).publishOn(Schedulers.single());
    }
}
