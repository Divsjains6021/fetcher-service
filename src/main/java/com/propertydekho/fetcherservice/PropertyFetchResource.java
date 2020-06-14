package com.propertydekho.fetcherservice;

import com.propertydekho.fetcherservice.handlers.PropertiesDataFetchHandler;
import com.propertydekho.fetcherservice.models.AreaPropertiesList;
import com.propertydekho.fetcherservice.models.PropFilter;
import com.propertydekho.fetcherservice.models.PropFilterableSortableData;
import com.propertydekho.fetcherservice.models.PropIDs;
import com.propertydekho.fetcherservice.models.PropMetaDataList;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertydekho.fetcherservice.config.KafkaConsumerConfiguration;
import com.propertydekho.fetcherservice.entity.PropFilterableSortableData;
import com.propertydekho.fetcherservice.listener.AreaIndexerConsumer;
import com.propertydekho.fetcherservice.models.*;
import com.propertydekho.fetcherservice.service.PropertyService;
import com.propertydekho.fetcherservice.views.FilterableAreaPropsViewInput;
import com.propertydekho.fetcherservice.views.InitPropViewInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import utilities.Utilities;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class PropertyFetchResource {
    @Resource
    PropertyService propertyService;
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private RestTemplate restTemplate;

    private PropertiesDataFetchHandler propertiesDataFetchHandler;

    @Autowired
    public PropertyFetchResource(PropertiesDataFetchHandler propertiesDataFetchHandler) {
        this.propertiesDataFetchHandler = propertiesDataFetchHandler;
    }

    @RequestMapping("/fetch-propids")
    public PropIDs getPropetyIDs() {

        List<String> propIDs = Arrays.asList("Prop-ID1", "Prop-ID2");
        return PropIDs.builder().propIDs(propIDs).build();
    }

    @RequestMapping("/fetch-init-data")
    public PropMetaDataList fetchInitData(@RequestBody InitPropViewInput viewInput) {
        List<PropFilter> filters = viewInput.getFilters();
        Optional<PropFilter> optAreaFilter = filters.stream()
                .filter(filter -> "area".equalsIgnoreCase(filter.getName()))
                .findAny();

        if (!optAreaFilter.isPresent()) {
            return PropMetaDataList.builder().propFilterableSortableData(Collections.emptyList()).build();
        }

        PropFilter areaPropFilter = optAreaFilter.get();
        String areaFilter = areaPropFilter.getFilterValue();
        String[] areaArr = areaFilter.split("&");
        List<String> areas = Arrays.stream(areaArr)
                .map(String::trim)
                .filter(area -> !area.isEmpty())
                .collect(Collectors.toList());

        AreaPropertiesList allPropertiesAreaWise = propertiesDataFetchHandler.getAreaProperties(areas);

        // Step 3: Remove Area Filter, as this filter is already applied
        filters.remove(areaPropFilter);

        // Step 4: Filter all properties list(both indexed or non-indexed)
        AreaPropertiesList filteredPropertiesList = filterAllProperties(filters, allPropertiesAreaWise);

        // Step 5: Sort all properties based on the default sorter
        return sortAllProps(filteredPropertiesList);
    }

    private AreaPropertiesList filterAllProperties(List<PropFilter> filters, AreaPropertiesList allPropertiesAreaWise) {
        if (filters.isEmpty()) {
            return allPropertiesAreaWise;
        }

        FilterableAreaPropsViewInput view = FilterableAreaPropsViewInput.builder()
                .propFilters(filters)
                .propertiesList(allPropertiesAreaWise)
                .build();
        return filterAllProps(view);
    }

    @RequestMapping("/fetch-kafka-props")
    public PropMetaDataList getKafkaPropIDs(@RequestParam String area) {

        // Step 1: Fetch the index file of the area specified
        PropIDs propIDs = propertiesDataFetchHandler.fetchIndexFile(area);
        PropMetaDataList indexedProps = propertiesDataFetchHandler.getProps(propIDs);

        // Step 2: Consume Kafka events for the area given
        List<PropFilterableSortableData> newlyAddedProps =
                propertiesDataFetchHandler.getPropsFromKafka(Collections.singletonList(area));

        // Step 3: In case of no Kafka event, return the list obtained from step 1
        if (newlyAddedProps.isEmpty()) {
            return indexedProps;
        }

        Map<String, PropMetaDataList> areaWiseIndexedProps = new HashMap<>();
        areaWiseIndexedProps.put(area, indexedProps);

        // Step 4: Else, sort the kafka events based on the relevancy score(or, default sorter)
        // Step 5: Merge both the lists obtained in Step 4 and Step 5
        // Step 6: Return the merged list
        return sortAllProps(AreaPropertiesList.builder()
                .indexedProperties(areaWiseIndexedProps)
                .nonIndexedProperties(
                        PropMetaDataList.builder()
                                .propFilterableSortableData(newlyAddedProps)
                                .build()
                )
                .build());
    }

    private AreaPropertiesList filterAllProps(FilterableAreaPropsViewInput view) {
        return restTemplate.postForObject("http://localhost:8086/filter-all-props", view, AreaPropertiesList.class);
    }


    private PropMetaDataList sortAllProps(AreaPropertiesList areaPropertiesList) {

        return restTemplate.postForObject("http://localhost:8087/merge-sort-props", areaPropertiesList,
                PropMetaDataList.class);
    }

    @RequestMapping("/addDataToDB")
    public String addDataToDB() {
        Utilities utilities = new Utilities();
        List<PropFilterableSortableData> properties = utilities.getProperties();
        for (PropFilterableSortableData property : properties) {
            propertyService.insertProperty(property);
        }
        return "Data added successfully";
    }
}
