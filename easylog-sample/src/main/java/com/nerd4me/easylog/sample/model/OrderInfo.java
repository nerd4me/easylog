package com.nerd4me.easylog.sample.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author yondy
 * @version 2018/08/11.
 */
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class OrderInfo implements Serializable {
    private Long orderId;

    private String buyerNick;

    private String itemName;
}
