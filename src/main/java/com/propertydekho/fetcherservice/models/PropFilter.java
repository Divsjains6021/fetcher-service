package com.propertydekho.fetcherservice.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PropFilter
{
    @JsonProperty("filter_type")
    private String filterType;

    @JsonProperty("filter_value")
    private String filterValue;

    public String getName() {
        return filterType;
    }
}
