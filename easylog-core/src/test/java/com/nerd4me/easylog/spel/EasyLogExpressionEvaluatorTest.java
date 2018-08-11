package com.nerd4me.easylog.spel;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author yondy
 * @version 2018/08/11.
 */
@ContextConfiguration(classes = {
        EasyLogExpressionEvaluatorTest.EleeConfiguration.class
})
@RunWith(SpringJUnit4ClassRunner.class)
public class EasyLogExpressionEvaluatorTest {
    private static final Logger log = LoggerFactory.getLogger("easylog.evaluator.test");

    @Autowired
    private TestService testService;

    @Autowired
    private Flux<Object> eventStream;

    @Autowired
    private EasyLogTestEventSource eventSource;

    @Test
    public void testCreate() {
        List<Object> events = Lists.newArrayList();
        eventStream.log().subscribe(events::add);
        testService.create(ImmutablePair.of("赤离", "小面包1*4"));
        testService.create(ImmutablePair.of("小明", "小面包1*4"));
        StepVerifier.create(Flux.fromIterable(events))
                .expectNext("赤离", "小明")
                .verifyComplete();
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface EleeAnno {
        String value();
    }

    @Component
    @Aspect
    static class EleeTestAspect {
        @Autowired
        private EasyLogExpressionEvaluator easyLogExpressionEvaluator;

        @Autowired
        private EasyLogTestEventSource eventSource;

        @Before("@annotation(eleeAnno)")
        public void eleeAspect(JoinPoint joinPoint, EleeAnno eleeAnno) {
            Pair<Method, Class> methodClassPair = methodClassPair(joinPoint);
            String expression = eleeAnno.value();
            EvaluationContext evaluationContext = easyLogExpressionEvaluator.createEvaluationContext(
                    joinPoint.getThis(), methodClassPair.getLeft(), joinPoint.getArgs(), null
            );
            Object logInfo = easyLogExpressionEvaluator.eval(
                    new AnnotatedElementKey(methodClassPair.getLeft(), methodClassPair.getRight()),
                    expression, evaluationContext, Object.class
            );
            eventSource.sendEvent(logInfo);
        }

        private Pair<Method, Class> methodClassPair(JoinPoint joinPoint) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            return ImmutablePair.of(
                    signature.getMethod(),
                    signature.getDeclaringType()
            );
        }
    }

    @Configuration
    @ComponentScan
    @EnableAspectJAutoProxy
    static class EleeConfiguration {
        @Bean
        public EasyLogExpressionEvaluator easyLogExpressionEvaluator() {
            return new EasyLogExpressionEvaluator();
        }

        @Bean
        public EasyLogTestEventSource eventSource() {
            return new EasyLogTestEventSource();
        }

        @Bean
        public Flux<Object> eventStream() {
            return Flux.create(sink -> eventSource().register(new EventListener() {
                @Override
                public void onSendEvent(Object object) {
                    sink.next(object);
                }

                @Override
                public void onComplete() {
                    sink.complete();
                }
            })).subscribeOn(Schedulers.single());
        }
    }

    static class EasyLogTestEventSource {
        List<EventListener> listeners = Lists.newArrayList();

        public void sendEvent(Object object) {
            listeners.forEach(lsn -> lsn.onSendEvent(object));
        }

        public void complete() {
            listeners.forEach(EventListener::onComplete);
        }

        public void register(EventListener eventListener) {
            listeners.add(eventListener);
        }
    }

    interface EventListener {
        void onSendEvent(Object object);

        void onComplete();
    }

    @Service
    static class TestService {
        @EleeAnno("#order.left")
        public void create(Pair<String, String> order) {
            log.info("order: {} created.", order);
        }
    }
}
