package com.nerd4me.easylog.event;

import com.nerd4me.easylog.model.BizLog;

/**
 * @author yondy
 * @version 2018/08/11.
 */
public interface EasyLogEventListener {
    /**
     * 新日志事件
     *
     * @param bizLog
     */
    void onNewEvent(BizLog bizLog);

    /**
     * 事件完成
     */
    void onComplete();
}
