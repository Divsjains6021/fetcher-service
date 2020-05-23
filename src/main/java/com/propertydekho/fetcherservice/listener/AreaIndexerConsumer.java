package com.propertydekho.fetcherservice.listener;

import com.propertydekho.fetcherservice.models.AreaIndexer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class AreaIndexerConsumer extends KafkaConsumer<String, AreaIndexer>
{
    public AreaIndexerConsumer(Map<String, Object> configs, Deserializer<String> keyDeserializer,
                               Deserializer<AreaIndexer> valueDeserializer) {
        super(configs, keyDeserializer, valueDeserializer);
    }
}
