package com.propertydekho.fetcherservice.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Arrays;

@Data
@Builder
public class PropFilter
{
    @JsonProperty("filter_type")
    private String filterType;

    @JsonProperty("filter_value")
    private String filterValue;

    public PropFilter() {
    }

    public PropFilter(String filterType, String filterValue) {
        this.filterType = filterType;
        this.filterValue = filterValue;
    }

    public String getName() {
        return filterType;
    }
}
