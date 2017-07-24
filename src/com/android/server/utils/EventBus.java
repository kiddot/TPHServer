package com.android.server.utils;

import com.android.server.common.event.Event;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by root on 17-7-22.
 */
public class EventBus {
    private final Logger LOGGER = LoggerFactory.getLogger(EventBus.class);
    public static final EventBus I = new EventBus();
    private final com.google.common.eventbus.EventBus eventBus;

    public EventBus() {
        Executor executor = Executors.newScheduledThreadPool(5);
        eventBus = new AsyncEventBus(executor, new SubscriberExceptionHandler() {
            @Override
            public void handleException(Throwable throwable, SubscriberExceptionContext subscriberExceptionContext) {
                LOGGER.warn("EventBus failed");
            }
        });
    }

    public void post(Event event) {
        //eventBus.post(event);
    }

    public void register(Object bean) {
        eventBus.register(bean);
    }

    public void unregister(Object bean) {
        eventBus.unregister(bean);
    }

}
