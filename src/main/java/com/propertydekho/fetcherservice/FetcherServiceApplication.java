package com.propertydekho.fetcherservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertydekho.fetcherservice.handlers.DataMetaRegistryInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;
import javax.sql.DataSource;

@SpringBootApplication(exclude = KafkaAutoConfiguration.class)
public class FetcherServiceApplication
{

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    @Autowired
    private DataMetaRegistryInitializer dataMetaRegistryInitializer;

    @Bean
    public void registerDataSources(){
        dataMetaRegistryInitializer.registerDataSources("com.propertydekho.fetcherservice.datasource");
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource)
    {
        return new JdbcTemplate(dataSource);
    }

    public static void main(String[] args) {
        SpringApplication.run(FetcherServiceApplication.class, args);
    }

}
