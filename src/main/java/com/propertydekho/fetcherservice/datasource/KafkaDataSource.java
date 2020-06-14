package com.propertydekho.fetcherservice.datasource;

import com.propertydekho.fetcherservice.config.KafkaConsumerConfiguration;
import com.propertydekho.fetcherservice.handlers.ResolutionGroup;
import com.propertydekho.fetcherservice.handlers.Resolvable;
import com.propertydekho.fetcherservice.listener.AreaIndexerConsumer;
import com.propertydekho.fetcherservice.models.AreaIndexer;
import com.propertydekho.fetcherservice.models.PropFilterableSortableData;
import com.propertydekho.fetcherservice.models.PropMetaDataList;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class KafkaDataSource extends PropertiesDataSource {
    private PropMetaDataList allNonIndexedProperties;

    public KafkaDataSource(List<String> areas) {
        super(areas);
    }

    @Resolvable(group = ResolutionGroup.ONE)
    public void fetchKafkaPropertiesData() {
        allNonIndexedProperties = getNewlyAddedProperties(areas);
    }

    public PropMetaDataList getNewlyAddedProperties(List<String> areas) {

        // Step 1: Get all new properties from Kafka
        List<PropFilterableSortableData> newlyAddedProps = getPropsFromKafka(areas);
//        List<PropFilterableSortableData> newlyAddedProps = PropFilterableSortableData.createDummyProperties();
        return PropMetaDataList.builder()
                .propFilterableSortableData(newlyAddedProps)
                .build();
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

    private Predicate<AreaIndexer> getAreaIndexerPredicate(List<String> areas) {
        return areaIndexer -> areas.stream()
                .anyMatch(area -> area.equalsIgnoreCase(areaIndexer.getArea()));
    }

    public PropMetaDataList getProperties() {
        return allNonIndexedProperties;
    }
}
