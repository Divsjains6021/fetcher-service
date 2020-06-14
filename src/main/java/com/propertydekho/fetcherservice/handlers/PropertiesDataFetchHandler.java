package com.propertydekho.fetcherservice.handlers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertydekho.fetcherservice.PropDaoMapper;
import com.propertydekho.fetcherservice.config.KafkaConsumerConfiguration;
import com.propertydekho.fetcherservice.listener.AreaIndexerConsumer;
import com.propertydekho.fetcherservice.models.AreaIndexer;
import com.propertydekho.fetcherservice.models.AreaPropertiesList;
import com.propertydekho.fetcherservice.models.PropFilterableSortableData;
import com.propertydekho.fetcherservice.models.PropIDs;
import com.propertydekho.fetcherservice.models.PropMetaDataList;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class PropertiesDataFetchHandler {

    public static final String PROPS_TABLE_NAME = "`property`";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private PropDaoMapper propDaoMapper;

    private Map<String, PropMetaDataList> areaWiseIndexedProperties;

    private PropMetaDataList allNonIndexedProperties;
    private PropertiesDataFetcher propertiesDataFetcher;

    @Autowired
    public PropertiesDataFetchHandler(PropDaoMapper propDaoMapper, PropertiesDataFetcher propertiesDataFetcher) {
        this.propDaoMapper = propDaoMapper;
        this.propertiesDataFetcher = propertiesDataFetcher;
    }


    public AreaPropertiesList getAreaProperties(List<String> areas) {


//        // Step 1: Get all indexed properties(area-wise)
//        areaWiseIndexedProperties = getAreaWiseIndexedProperties(areas);

//        // Step 2: Get all newly added properties(from Kafka)
        propertiesDataFetcher.fetchData(areas);

        return propertiesDataFetcher.getPropertiesList();

    }



    public List<PropFilterableSortableData> getPropsFromKafka(List<String> areas) {
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

    public PropIDs fetchIndexFile(String area) {
        ObjectMapper mapper = new ObjectMapper();
        try {


            InputStream inputStream = new FileInputStream(new File(
                    "/Users/sameer.jain/prop_dekho/fetcher-service/src/main/resources/" + area + ".json"));
            TypeReference<PropIDs> typeReference = new TypeReference<PropIDs>() {
            };
            return mapper.readValue(inputStream, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return PropIDs.builder().propIDs(Collections.emptyList()).build();
    }

    public PropMetaDataList getProps(PropIDs propIDsWrapper) {
        List<String> propIDList = propIDsWrapper.getPropIDs();
        if (propIDList.isEmpty()) {
            return new PropMetaDataList();
        }

        String propIDs = '(' +
                StringUtils.join(propIDList.stream()
                                .map(propID -> "'" + propID + "'")
                                .collect(Collectors.toList()),
                        ','
                ) +
                ')';


        String sqlQuery = "select * from " + PROPS_TABLE_NAME + "where `prop_id` in " + propIDs + ";";
        List<PropFilterableSortableData> props = jdbcTemplate.query(sqlQuery, propDaoMapper);
        return PropMetaDataList.builder()
                .propFilterableSortableData(props)
                .build();
    }

    private List<PropFilterableSortableData> deleteIndexedProperties(List<PropFilterableSortableData> props,
                                                                     Set<String> deletedPropIDs) {
        return props.stream()
                .filter(prop -> !deletedPropIDs.contains(prop.getPropID()))
                .collect(Collectors.toList());
    }

    private Predicate<AreaIndexer> getAreaIndexerPredicate(List<String> areas) {
        return areaIndexer -> areas.stream()
                .anyMatch(area -> area.equalsIgnoreCase(areaIndexer.getArea()));
    }
}
