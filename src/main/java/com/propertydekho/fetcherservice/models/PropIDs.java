package com.propertydekho.fetcherservice.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PropIDs
{
    @JsonProperty("prop_ids")
    private List<String> propIDs;

    public PropIDs() {
    }

    public PropIDs(List<String> propIDs) {
        this.propIDs = propIDs;
    }
}
