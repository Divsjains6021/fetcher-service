package com.propertydekho.fetcherservice.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Builder
public class PropFilterableSortableData
{
    @Id
    @JsonProperty("prop_id")
    private String propID;

    @JsonProperty("prop_name")
    private String propName;

    @JsonProperty("prop_price")
    private Double propPrice;

    @JsonProperty("sqft")
    private Integer sqft;

    @JsonProperty("bedroom")
    private String bedroom;

    @JsonProperty("sale_type")
    private String saleType;

    @JsonProperty("constructn_status")
    private String constructionStatus;

    @JsonProperty("area")
    private String area;
}
