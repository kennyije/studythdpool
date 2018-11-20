package com.lyw.configuration;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Created by Lenovo on 2018/9/28.
 */
@Configuration
@EnableAsync
public class AsyncConfig extends AsyncConfigurerSupport {
    @Autowired
    private AsyncExceptionHandler asyncExceptionHandler;

    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return asyncExceptionHandler;
    }
}
