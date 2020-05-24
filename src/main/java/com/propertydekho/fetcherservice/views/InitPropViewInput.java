package com.propertydekho.fetcherservice.views;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.propertydekho.fetcherservice.models.PropFilter;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class InitPropViewInput
{

    @JsonProperty("filters")
    private List<PropFilter> filters;

    public InitPropViewInput() {
    }

    public InitPropViewInput(List<PropFilter> filters) {
        this.filters = filters;
    }
}
