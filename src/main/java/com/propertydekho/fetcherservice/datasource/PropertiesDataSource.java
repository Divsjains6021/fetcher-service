package com.propertydekho.fetcherservice.datasource;

import java.util.List;

public abstract class PropertiesDataSource {

    protected List<String> areas;

    protected PropertiesDataSource(List<String> areas) {
        this.areas = areas;
    }
}
