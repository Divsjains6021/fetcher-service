package com.propertydekho.fetcherservice.config;

import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import com.propertydekho.fetcherservice.models.AreaIndexer;
//import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.HashMap;
import java.util.Map;

//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.kafka.annotation.EnableKafka;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

//@EnableKafka
public class KafkaConsumerConfiguration
{
//    public static Map<String, Object> getPropsConsumerConfig() {
//        Map<String, Object> consumerConfig = new HashMap<>();
//        consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
//        consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, "group_id");
//        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, AreaIndexer.class);
//        consumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        return consumerConfig;
//    }

//    @Bean
//    public ConsumerFactory<String, AreaIndexer> getConsumerFactory() {
//        Map<String, Object> consumerConfig = getPropsConsumerConfig();
//        return new DefaultKafkaConsumerFactory<>(consumerConfig);
//    }

////    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, AreaIndexer> getKafkaConsumerListenerFactory(){
//        ConcurrentKafkaListenerContainerFactory<String, AreaIndexer> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(getConsumerFactory());
//        return factory;
//    }
}
