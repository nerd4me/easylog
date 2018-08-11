package com.nerd4me.easylog.event;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.nerd4me.easylog.model.BizLog;
import com.nerd4me.easylog.model.RowBizLog;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.List;

/**
 * @author yondy
 * @version 2018/08/11.
 */
public class EasyLogEventSourceTest {
    private static final EasyLogEventSource eventSource = new EasyLogEventSource();

    private Flux<BizLog> bizLogFlux = Flux.<BizLog>create(sink -> {
        try {
            eventSource.register(new EasyLogEventListener() {
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
    }).subscribeOn(Schedulers.single()).log();

    @Test
    public void testNewEvent() {
        List<Object> names = Lists.newArrayList();
        bizLogFlux.map(bizLog -> bizLog.getContent().get("name"))
                .subscribe(names::add);
        BizLog bizLog = RowBizLog.builder()
                .topic("shipment")
                .name("order.create")
                .content(
                        ImmutableMap.<String, Object>builder()
                                .put("name", "赤离")
                                .put("amount", 100)
                                .build()
                ).build();
        eventSource.newEvent(bizLog);
        eventSource.complete();
        StepVerifier.create(Flux.fromIterable(names))
                .expectNext("赤离")
                .verifyComplete();
    }
}
