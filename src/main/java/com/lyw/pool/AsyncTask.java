package com.lyw.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Created by Lenovo on 2018/9/28.
 */
@Component
public class AsyncTask {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Async("myTaskAsyncPool")
    public void doTask1(int i) throws InterruptedException {

        logger.info("Task"+ i + "started");
    }
    @Async("myTaskAsyncPool")
    public Future<String> doTask2(int i) throws InterruptedException {
//        logger.info("Task"+ i + "started");
        return CompletableFuture.completedFuture("Task"+ i + "started");
    }
}
