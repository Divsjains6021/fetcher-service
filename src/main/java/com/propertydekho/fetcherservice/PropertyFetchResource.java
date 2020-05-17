package com.propertydekho.fetcherservice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertydekho.fetcherservice.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class PropertyFetchResource
{
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/fetch-propids")
    public PropIDs getPropetyIDs() {

        List<String> propIDs = Arrays.asList("Prop-ID1", "Prop-ID2");
        return PropIDs.builder().propIDs(propIDs).build();
    }

    @RequestMapping("/fetch-kafka-props")
    public PropMetaDataList getKafkaPropIDs(@RequestParam String area) {

        // Step 1: Fetch the index file of the area specified
        PropIDs propIDs = fetchIndexFile(area);

        // Step 2: Consume Kafka events for the area given
        List<PropFilterableSortableData> newlyAddedProps = getPropsFromKafka(area);

        PropMetaDataList indexedProps = getProps(propIDs);
        // Step 3: In case of no Kafka event, return the list obtained from step 1
        if (newlyAddedProps.isEmpty()) {
            return indexedProps;
        }

        // Step 4: Else, sort the kafka events based on the relevancy score(or, default sorter)
        // Step 5: Merge both the lists obtained in Step 4 and Step 5
        // Step 6: Return the merged list
        return mergeAllProps(AreaPropertiesList.builder()
                .indexedProperties(indexedProps)
                .nonIndexedProperties(
                        PropMetaDataList.builder()
                                .propFilterableSortableData(newlyAddedProps)
                                .build()
                )
                .build());
    }

    private PropMetaDataList mergeAllProps(AreaPropertiesList areaPropertiesList) {

        return restTemplate.postForObject("http://localhost:8087/merge-sort-props", areaPropertiesList,
                PropMetaDataList.class);
    }

    private PropMetaDataList getProps(PropIDs propIDs) {
        return PropMetaDataList.builder().propFilterableSortableData(
                propIDs.getPropIDs().stream()
                        .map(propId -> PropFilterableSortableData.builder()
                                .propID(propId)
                                .propName("2BHK for sale in Whitefield, Bangalore")
                                .propPrice(5500000)
                                .area("Whitefield")
                                .bedroom("2BHK")
                                .saleType("New")
                                .constructionStatus("Ready to move")
                                .build())
                        .collect(Collectors.toList())
        ).build();
    }

    private List<PropFilterableSortableData> getPropsFromKafka(String area) {
        List<AreaIndexer> kafkaMessages = new ArrayList<>();
        kafkaMessages.add(AreaIndexer.builder().area("WhiteField").propDetail(
                PropFilterableSortableData.builder().propID("3gdfgd4").propName("2BHK for sale in Whitefield, " +
                        "Bangalore")
                        .propPrice(4500000)
                        .area("Whitefield")
                        .bedroom("2BHK")
                        .saleType("Resale")
                        .constructionStatus("Ready to move").build()
        ).build());
        return kafkaMessages.stream()
                .filter(kafkaMessage -> kafkaMessage.getArea().equalsIgnoreCase(area))
                .map(AreaIndexer::getPropDetail)
                .collect(Collectors.toList());
    }

    private PropIDs fetchIndexFile(String area) {
        ObjectMapper mapper = new ObjectMapper();
        try {


            InputStream inputStream = new FileInputStream(new File(
                    "/Users/sameer.jain/prop_dekho/fetcher-service/src/main/resources/" + area + ".json"));
            TypeReference<PropIDs> typeReference = new TypeReference<PropIDs>()
            {
            };
            return mapper.readValue(inputStream, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return PropIDs.builder().propIDs(Collections.emptyList()).build();
    }
}
