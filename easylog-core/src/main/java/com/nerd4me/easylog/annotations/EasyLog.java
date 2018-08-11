package com.nerd4me.easylog.annotations;

import com.nerd4me.easylog.handler.DefaultErrorHandler;
import com.nerd4me.easylog.handler.ErrorHandler;

import java.lang.annotation.*;

/**
 * @author yondy
 * @version 2018/08/11.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EasyLog {
    /**
     * log topic
     *
     * @return
     */
    String topic();

    /**
     * log name
     *
     * @return
     */
    String name();

    /**
     * business operation result
     *
     * @return
     */
    String result();

    /**
     * log content, support SPEL
     * <code>
     * { orderType: #order.type, result: #result.code }
     * </code>
     *
     * @return
     */
    String extra();

    /**
     * error handler's class
     *
     * @return
     */
    Class<? extends ErrorHandler> errorHandler() default DefaultErrorHandler.class;
}
