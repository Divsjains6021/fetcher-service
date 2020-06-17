package com.propertydekho.fetcherservice.handlers;

import com.propertydekho.fetcherservice.datasource.PropertiesDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DataMetaRegistry {
    private Map<Class<?>, DataSourceMeta> dataSourceMetaMap;

    @Autowired
    public DataMetaRegistry(Map<Class<?>, DataSourceMeta> dataSourceMetaMap) {
        this.dataSourceMetaMap = dataSourceMetaMap;
    }

    public void register(Class<? extends PropertiesDataSource> dataSource) {

        Method[] declaredMethods = dataSource.getDeclaredMethods();
        Map<ResolutionGroup, List<Method>> dataSourceMetaInfo = Arrays.stream(declaredMethods)
                .filter(method -> method.isAnnotationPresent(Resolvable.class))
                .collect(Collectors.groupingBy(method -> method.getAnnotation(Resolvable.class).group(), HashMap::new
                        , Collectors.toCollection(ArrayList::new)));

        DataSourceMeta dataSourceMeta = new DataSourceMeta(dataSourceMetaInfo);

        dataSourceMetaMap.put(dataSource, dataSourceMeta);
    }

    public DataSourceMeta getDataSourceMeta(Class<?> dataSourceClazz) {
        return dataSourceMetaMap.get(dataSourceClazz);
    }
}
