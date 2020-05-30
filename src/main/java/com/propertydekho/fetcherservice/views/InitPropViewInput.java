package com.propertydekho.fetcherservice.views;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.propertydekho.fetcherservice.models.PropFilter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class InitPropViewInput
{

    @JsonProperty("filters")
    private List<PropFilter> filters;
}
