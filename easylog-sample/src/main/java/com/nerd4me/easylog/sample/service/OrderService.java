package com.nerd4me.easylog.sample.service;

import com.google.common.base.Strings;
import com.nerd4me.easylog.annotations.EasyLog;
import com.nerd4me.easylog.handler.CustomizedErrorHandler;
import com.nerd4me.easylog.sample.model.OrderInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author yondy
 * @version 2018/08/11.
 */
@Service
@Slf4j(topic = "order")
public class OrderService {
    @EasyLog(topic = "order", name = "create", result = "#result",
            extra = "{orderId: #orderInfo.orderId, buyerNick: #orderInfo.buyerNick}",
            errorHandler = CustomizedErrorHandler.class)
    public boolean create(OrderInfo orderInfo) {
        checkArgument(!Strings.isNullOrEmpty(orderInfo.getBuyerNick()), "invalid buyer nick");
        log.info("order: {} created.", orderInfo);
        return true;
    }
}
