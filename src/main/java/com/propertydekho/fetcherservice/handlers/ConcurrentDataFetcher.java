package com.propertydekho.fetcherservice.handlers;

import com.propertydekho.fetcherservice.datasource.PropertiesDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ConcurrentDataFetcher implements DataFetcher {

    private static ExecutorService executorService = Executors.newFixedThreadPool(10);
    private DefaultDataFetcher dataFetcher;

    @Autowired
    public ConcurrentDataFetcher(DefaultDataFetcher dataFetcher) {
        this.dataFetcher = dataFetcher;
    }

    @Override
    public void fetchData(List<PropertiesDataSource> propertiesDataSourceList) {
        Map<PropertiesDataSource, Future<DataFetcherTask>> dataSourceFutureMap = propertiesDataSourceList.stream()
                .collect(Collectors.toMap(Function.identity(),
                        propertiesDataSource -> executorService.submit(new DataFetcherTask(propertiesDataSource))));

        dataSourceFutureMap.values()
                .forEach(task -> {
                    try {
                        task.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                });

    }

    private class DataFetcherTask implements Callable<DataFetcherTask> {

        private PropertiesDataSource propertiesDataSource;

        private DataFetcherTask(PropertiesDataSource propertiesDataSource) {
            this.propertiesDataSource = propertiesDataSource;
        }

        @Override
        public DataFetcherTask call() {
            dataFetcher.fetchData(Collections.singletonList(propertiesDataSource));
            return this;
        }
    }
}
