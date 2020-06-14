package com.propertydekho.fetcherservice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import utilities.Utilities;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    public static List<PropFilterableSortableData> createDummyProperties() {
        PropFilterableSortableData dummyProp = PropFilterableSortableData.builder()
                .sqft(1234)
                .propPrice(234343.0)
                .bedroom("1 BHK")
                .area("WhiteField")
                .saleType("New")
                .constructionStatus("Ready to move")
                .propName("Dummy Property")
                .propID("dummy-ID")
                .build();
        return Collections.singletonList(dummyProp);
    }
}
