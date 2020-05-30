package com.propertydekho.fetcherservice.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AreaIndexer
{
    @JsonProperty("area")
    private String area;

    @JsonProperty("prop_detail")
    private PropFilterableSortableData propDetail;
}
