package com.propertydekho.fetcherservice.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AreaPropertiesList
{
    @JsonProperty("index_props")
    private PropMetaDataList indexedProperties;
    @JsonProperty("non_index_props")
    private PropMetaDataList nonIndexedProperties;

    public AreaPropertiesList() {
    }

    public AreaPropertiesList(PropMetaDataList indexedProperties, PropMetaDataList nonIndexedProperties) {
        this.indexedProperties = indexedProperties;
        this.nonIndexedProperties = nonIndexedProperties;
    }
}
