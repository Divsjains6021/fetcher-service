package com.propertydekho.fetcherservice.handlers;

import com.propertydekho.fetcherservice.datasource.PropertiesDataSource;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataMetaRegistryInitializer {

    private DataMetaRegistry dataMetaRegistry;

    @Autowired
    public DataMetaRegistryInitializer(DataMetaRegistry dataMetaRegistry) {
        this.dataMetaRegistry = dataMetaRegistry;
    }

    public void registerDataSources(String packageName) {

        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends PropertiesDataSource>> dataSourceSet =
                reflections.getSubTypesOf(PropertiesDataSource.class);

        for (Class<? extends PropertiesDataSource> dataSource : dataSourceSet) {
            dataMetaRegistry.register(dataSource);
        }

    }
}
