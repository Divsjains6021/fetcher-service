package com.propertydekho.fetcherservice.handlers;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class DataSourceMeta {
    private Map<ResolutionGroup, List<Method>> dataSourceMetaInfo;

    public DataSourceMeta(Map<ResolutionGroup, List<Method>> dataSourceMetaInfo) {
        this.dataSourceMetaInfo = dataSourceMetaInfo;
    }

    public Map<ResolutionGroup, List<Method>> getDataSourceMetaInfo() {
        return dataSourceMetaInfo;
    }
}
