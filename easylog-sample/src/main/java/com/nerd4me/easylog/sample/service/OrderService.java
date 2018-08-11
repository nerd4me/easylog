package com.nerd4me.easylog.sample.service;

import com.google.common.base.Strings;
import com.nerd4me.easylog.annotations.EasyLog;
import com.nerd4me.easylog.sample.model.OrderInfo;
import org.springframework.stereotype.Service;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author yondy
 * @version 2018/08/11.
 */
@Service
public class OrderService {
    @EasyLog(topic = "order", name = "create", result = "#result",
            extra = "{orderId: #orderInfo.orderId, buyerNick: #orderInfo.buyerNick}")
    public boolean create(OrderInfo orderInfo) {
        checkArgument(!Strings.isNullOrEmpty(orderInfo.getBuyerNick()), "invalid buyer nick");
        return true;
    }
}