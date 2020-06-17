package com.propertydekho.fetcherservice.handlers;

import com.propertydekho.fetcherservice.datasource.PropertiesDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DefaultDataFetcher implements DataFetcher {

    private static List<ResolutionGroup> resolutionGroupPriority;
    private DataMetaRegistry dataMetaRegistry;
    private ConcurrentMethodExecutor methodExecutor;

    @Autowired
    public DefaultDataFetcher(DataMetaRegistry dataMetaRegistry, ConcurrentMethodExecutor methodExecutor) {
        resolutionGroupPriority = Arrays.asList(ResolutionGroup.ONE, ResolutionGroup.TWO);
        this.dataMetaRegistry = dataMetaRegistry;
        this.methodExecutor = methodExecutor;
    }

    @Override
    public void fetchData(List<PropertiesDataSource> propertiesDataSourceList) {
        if (propertiesDataSourceList.isEmpty()) {
            throw new IllegalArgumentException("Empty List");
        }

        propertiesDataSourceList
                .forEach(propertiesDataSource -> {
                    DataSourceMeta dataSourceMeta = dataMetaRegistry.getDataSourceMeta(propertiesDataSource.getClass());
                    Map<ResolutionGroup, List<Method>> dataSourceMetaInfo = dataSourceMeta.getDataSourceMetaInfo();
                    resolutionGroupPriority
                            .forEach(resolutionGroup -> {
                                List<Method> methods = dataSourceMetaInfo.get(resolutionGroup);
                                if (methods != null) {
                                    resolve(propertiesDataSource, methods);
                                }
                            });
                });
    }

    private void resolve(PropertiesDataSource propertiesDataSource, List<Method> methods) {
        Map<Method, Future<PropertiesDataSource>> methodFutureMap = methods.stream()
                .collect(Collectors.toMap(Function.identity(), method -> methodExecutor.execute(propertiesDataSource,
                        method)));
        for (Future<PropertiesDataSource> propertiesDataSourceFuture : methodFutureMap.values()) {
            try {
                propertiesDataSourceFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

    }
}
