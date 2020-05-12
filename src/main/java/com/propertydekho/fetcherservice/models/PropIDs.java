package com.propertydekho.fetcherservice.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PropIDs
{
    private List<String> propIDs;
}
