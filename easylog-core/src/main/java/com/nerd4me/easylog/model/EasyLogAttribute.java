package com.nerd4me.easylog.model;

import com.nerd4me.easylog.handler.ErrorHandler;
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
@NoArgsConstructor
@Builder
@Data
public class EasyLogAttribute implements Serializable {
    private String topic;

    private String name;

    private String result;

    private String extra;

    private Class<? extends ErrorHandler> errorHandler;

    public static EasyLogAttribute empty() {
        return EasyLogAttribute.builder().build();
    }
}
