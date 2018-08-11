package com.nerd4me.easylog.event;

import com.google.common.collect.Lists;
import com.nerd4me.easylog.model.BizLog;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author yondy
 * @version 2018/08/11.
 */
public class EasyLogEventSource {
    private final List<EasyLogEventListener> listeners = Lists.newArrayList();

    public void register(EasyLogEventListener eventListener) {
        checkNotNull(eventListener);
        listeners.add(eventListener);
    }

    public void registerAll(Collection<? extends EasyLogEventListener> eventListeners) {
        checkNotNull(eventListeners);
        listeners.addAll(eventListeners);
    }

    public void newEvent(BizLog bizLog) {
        listeners.forEach(listener -> listener.onNewEvent(bizLog));
    }

    public void complete() {
        listeners.forEach(EasyLogEventListener::onComplete);
    }
}
