package com.propertydekho.fetcherservice.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AreaPropertiesList
{
    @JsonProperty("index_props")
    private Map<String, PropMetaDataList> indexedProperties;

    @JsonProperty("non_index_props")
    private PropMetaDataList nonIndexedProperties;
}
