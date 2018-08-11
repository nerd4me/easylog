package com.nerd4me.easylog.sample.service;

import com.nerd4me.easylog.model.BizLog;
import com.nerd4me.easylog.sample.model.OrderInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;

import java.util.concurrent.TimeUnit;

/**
 * @author yondy
 * @version 2018/08/11.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j(topic = "order.test")
public class OrderServiceTest {
    @Autowired
    private OrderService orderService;

    @Autowired
    private Flux<BizLog> bizLogStream;

    @Test
    public void testCreate() throws InterruptedException {
        try {
            bizLogStream.log()
                    .subscribe(bizLog -> log.info("topic: {}, name: {}, content: {}",
                            bizLog.getTopic(), bizLog.getName(), bizLog.getContent()));
            orderService.create(
                    OrderInfo.builder()
                            .orderId(1234030030L)
                            .buyerNick("")
                            .itemName("《世界大战》")
                            .build()
            );
        } catch (Exception e) {
            log.error("create order failed.", e);
        }
        TimeUnit.SECONDS.sleep(30);
    }
}
