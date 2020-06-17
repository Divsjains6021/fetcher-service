package com.propertydekho.fetcherservice.handlers;

import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
public class ConcurrentMethodExecutor {
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public <T> Future<T> execute(T dataSource, Method method) {
        return executorService.submit(new MethodExecutionTask<>(dataSource, method));
    }

    private static class MethodExecutionTask<T> implements Callable<T> {

        private T dataSource;
        private Method method;

        MethodExecutionTask(T dataSource, Method method) {
            this.dataSource = dataSource;
            this.method = method;
        }

        @Override
        public T call() throws Exception {
            method.invoke(dataSource);
            return dataSource;
        }
    }
}
