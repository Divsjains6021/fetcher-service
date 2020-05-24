package com.propertydekho.fetcherservice.views;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.propertydekho.fetcherservice.models.AreaPropertiesList;
import com.propertydekho.fetcherservice.models.PropFilter;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FilterableAreaPropsViewInput
{
    @JsonProperty("prop_list")
    private AreaPropertiesList propertiesList;

    @JsonProperty("prop_filters")
    private List<PropFilter> propFilters;
}
