package com.propertydekho.fetcherservice.handlers;

import com.propertydekho.fetcherservice.PropDaoMapper;
import com.propertydekho.fetcherservice.datasource.IndexedDataSource;
import com.propertydekho.fetcherservice.datasource.KafkaDataSource;
import com.propertydekho.fetcherservice.models.AreaPropertiesList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class PropertiesDataFetcher {

    private IndexedDataSource indexedDataSource;
    private KafkaDataSource kafkaDataSource;

    private ConcurrentDataFetcher dataFetcher;

    private JdbcTemplate jdbcTemplate;
    private PropDaoMapper propDaoMapper;

    @Autowired
    public PropertiesDataFetcher(ConcurrentDataFetcher dataFetcher, JdbcTemplate jdbcTemplate, PropDaoMapper propDaoMapper){
        this.dataFetcher = dataFetcher;
        this.jdbcTemplate = jdbcTemplate;
        this.propDaoMapper = propDaoMapper;
    }

    public void fetchData(List<String> areas) {
        indexedDataSource = new IndexedDataSource(areas, jdbcTemplate, propDaoMapper);
        kafkaDataSource = new KafkaDataSource(areas);
        dataFetcher.fetchData(Arrays.asList(indexedDataSource, kafkaDataSource));
    }

    public AreaPropertiesList getPropertiesList() {
        return AreaPropertiesList.builder()
                .indexedProperties(indexedDataSource.getProperties())
                .nonIndexedProperties(kafkaDataSource.getProperties())
                .build();
    }


}
