package com.propertydekho.fetcherservice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertydekho.fetcherservice.config.KafkaConsumerConfiguration;
import com.propertydekho.fetcherservice.listener.AreaIndexerConsumer;
import com.propertydekho.fetcherservice.models.*;
import com.propertydekho.fetcherservice.views.FilterableAreaPropsViewInput;
import com.propertydekho.fetcherservice.views.InitPropViewInput;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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

        // Step 1: Get all indexed properties(area-wise)
        Map<String, PropMetaDataList> areaWiseIndexedProperties = getAreaWiseIndexedProperties(areas);

        // Step 2: Get all newly added properties(from Kafka)
        PropMetaDataList allNonIndexedProperties = getNewlyAddedProperties(areas);

        // Step 3: Remove Area Filter, as this filter is already applied
        filters.remove(areaPropFilter);

        AreaPropertiesList allPropertiesAreaWise = AreaPropertiesList.builder()
                .indexedProperties(areaWiseIndexedProperties)
                .nonIndexedProperties(allNonIndexedProperties)
                .build();

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

    private Map<String, PropMetaDataList> getAreaWiseIndexedProperties(List<String> areas) {
        Map<String, PropMetaDataList> areaWiseIndexedProperties =
                areas.stream()
                        .collect(Collectors.toMap((area) -> area, this::getIndexedProperties));

        Map<String, Set<String>> areaWiseDeletedProperties = getDeletedProperties(areas);
        areaWiseDeletedProperties.forEach(
                (key, value) -> deleteIndexedProperties(areaWiseIndexedProperties, key, value)
        );

        List<String> areasWithNoProperties = areaWiseIndexedProperties.keySet().stream()
                .filter(area -> areaWiseIndexedProperties.get(area).getPropFilterableSortableData().isEmpty())
                .collect(Collectors.toList());

        areasWithNoProperties
                .forEach(areaWiseIndexedProperties::remove);
        return areaWiseIndexedProperties;
    }

    private AreaPropertiesList filterAllProps(FilterableAreaPropsViewInput view) {
        return restTemplate.postForObject("localhost:8086/filter-all-props",
                view, AreaPropertiesList.class);
    }

    private void deleteIndexedProperties(Map<String, PropMetaDataList> areaWiseIndexedProperties, String area,
                                         Set<String> deletedPropIDs) {

        PropMetaDataList propMetaDataList = areaWiseIndexedProperties.get(area);
        List<PropFilterableSortableData> props = propMetaDataList.getPropFilterableSortableData();
        props = deleteIndexedProperties(props, deletedPropIDs);
        propMetaDataList.setPropFilterableSortableData(props);
    }

    private List<PropFilterableSortableData> deleteIndexedProperties(List<PropFilterableSortableData> props,
                                                                     Set<String> deletedPropIDs) {
        return props.stream()
                .filter(prop -> !deletedPropIDs.contains(prop.getPropID()))
                .collect(Collectors.toList());
    }

    private Map<String, Set<String>> getDeletedProperties(List<String> areas) {
        // Step 1: Fetch all deleted props
        return areas.stream()
                .collect(Collectors.toMap(area -> area, area -> Collections.emptySet()));
    }

    private PropMetaDataList getNewlyAddedProperties(List<String> areas) {

        // Step 1: Get all new properties from Kafka
        List<PropFilterableSortableData> newlyAddedProps = getPropsFromKafka(areas);

        return PropMetaDataList.builder()
                .propFilterableSortableData(newlyAddedProps)
                .build();
    }

    private PropMetaDataList getIndexedProperties(String area) {
        // Step 1: Fetch the index file of the area specified
        PropIDs propIDs = fetchIndexFile(area);

        // Step 2: Connect to DB and fetch the details
        return getProps(propIDs);
    }

    private Predicate<AreaIndexer> getAreaIndexerPredicate(List<String> areas) {
        return areaIndexer -> areas.stream()
                .anyMatch(area -> area.equalsIgnoreCase(areaIndexer.getArea()));
    }

    @RequestMapping("/fetch-kafka-props")
    public PropMetaDataList getKafkaPropIDs(@RequestParam String area) {

        // Step 1: Fetch the index file of the area specified
        PropIDs propIDs = fetchIndexFile(area);

        // Step 2: Consume Kafka events for the area given
        List<PropFilterableSortableData> newlyAddedProps = getPropsFromKafka(Collections.singletonList(area));

        PropMetaDataList indexedProps = getProps(propIDs);
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

    private PropMetaDataList sortAllProps(AreaPropertiesList areaPropertiesList) {

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

    private List<PropFilterableSortableData> getPropsFromKafka(List<String> areas) {
        JsonDeserializer<AreaIndexer> valueDeserializer = new JsonDeserializer<>(AreaIndexer.class, false);
        valueDeserializer.addTrustedPackages("com.propertydekho.createservice.models");
        AreaIndexerConsumer areaIndexerConsumer =
                new AreaIndexerConsumer(KafkaConsumerConfiguration.getPropsConsumerConfig(), new StringDeserializer()
                        , valueDeserializer);
        areaIndexerConsumer.subscribe(Collections.singletonList("area-indexer"));
        areaIndexerConsumer.poll(Duration.ZERO);
        areaIndexerConsumer.seekToBeginning(areaIndexerConsumer.assignment());
        ConsumerRecords<String, AreaIndexer> records = areaIndexerConsumer.poll(Duration.ofDays(1));
        List<ConsumerRecord<String, AreaIndexer>> consumerRecords = records.records(new TopicPartition("area-indexer"
                , 0));
        return consumerRecords.stream()
                .map(ConsumerRecord::value)
                .filter(getAreaIndexerPredicate(areas))
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
