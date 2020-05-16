package com.propertydekho.fetcherservice.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PropFilterableSortableData {
    @JsonProperty("prop_id")
    private String propID;
    @JsonProperty("prop_name")
    private String propName;
    @JsonProperty("prop_price")
    private double propPrice;
    @JsonProperty("budget_max")
    private double budgetMax;
    @JsonProperty("bedroom")
    private String bedroom;
    @JsonProperty("sale_type")
    private String saleType;
    @JsonProperty("constructn_status")
    private String constructionStatus;
    @JsonProperty("area")
    private String area;

    public PropFilterableSortableData() {

    }

    public PropFilterableSortableData(String propID, String propName, double propPrice, double budgetMax, String bedroom, String saleType, String constructionStatus, String area) {
        this.propID = propID;
        this.propName = propName;
        this.propPrice = propPrice;
        this.budgetMax = budgetMax;
        this.bedroom = bedroom;
        this.saleType = saleType;
        this.constructionStatus = constructionStatus;
        this.area = area;
    }
}
