package com.nerd4me.easylog.model;

import java.util.Map;

/**
 * @author yondy
 * @version 2018/08/11.
 */
public interface BizLog {
    /**
     * 业务日志主题
     *
     * @return
     */
    String getTopic();

    /**
     * 业务日志名称
     *
     * @return
     */
    String getName();

    /**
     * 业务日志内容
     *
     * @return
     */
    Map<String, Object> getContent();
}
