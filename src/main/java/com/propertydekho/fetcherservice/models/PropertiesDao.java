package com.propertydekho.fetcherservice.models;

import com.propertydekho.fetcherservice.entity.PropFilterableSortableData;

import java.util.List;

public interface PropertiesDao {

    List<PropFilterableSortableData> findAll();

    void insertProperty(PropFilterableSortableData prop);

    void updateProperty(PropFilterableSortableData prop);

    void executeUpdateProperty(PropFilterableSortableData prop);

    public void deleteProperty(PropFilterableSortableData prop);
}
