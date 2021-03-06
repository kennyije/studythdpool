package com.lyw.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Created by Lenovo on 2018/9/28.
 */
@Component
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(AsyncExceptionHandler.class);

    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        logger.error("Unexpected asynchronous exception at : " + method.getDeclaringClass().getName() + "." + method.getName(),ex);
    }
}
