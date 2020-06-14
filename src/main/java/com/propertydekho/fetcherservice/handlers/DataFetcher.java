package com.propertydekho.fetcherservice.handlers;

import com.propertydekho.fetcherservice.datasource.PropertiesDataSource;

import java.util.List;

public interface DataFetcher {

    void fetchData(List<PropertiesDataSource> propertiesDataSourceList);
}
