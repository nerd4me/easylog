package com.nerd4me.easylog.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * @author yondy
 * @version 2018/08/11.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RowBizLog implements BizLog, Serializable {
    private String topic;
    private String name;
    private Map<String, Object> content;
}
