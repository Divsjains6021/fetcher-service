package com.propertydekho.fetcherservice.datasource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertydekho.fetcherservice.PropDaoMapper;
import com.propertydekho.fetcherservice.handlers.ResolutionGroup;
import com.propertydekho.fetcherservice.handlers.Resolvable;
import com.propertydekho.fetcherservice.models.PropFilterableSortableData;
import com.propertydekho.fetcherservice.models.PropIDs;
import com.propertydekho.fetcherservice.models.PropMetaDataList;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class IndexedDataSource extends PropertiesDataSource {

    public static final String PROPS_TABLE_NAME = "`property`";
    private JdbcTemplate jdbcTemplate;
    private PropDaoMapper propDaoMapper;
    private Map<String, PropMetaDataList> areaWiseIndexedProperties;


    public IndexedDataSource(List<String> areas, JdbcTemplate jdbcTemplate, PropDaoMapper propDaoMapper) {
        super(areas);
        this.jdbcTemplate = jdbcTemplate;
        this.propDaoMapper = propDaoMapper;
    }

    @Resolvable(group = ResolutionGroup.ONE)
    public void fetchIndexedPropertiesData() {
        areaWiseIndexedProperties = getAreaWiseIndexedProperties(areas);
    }

    public Map<String, PropMetaDataList> getAreaWiseIndexedProperties(List<String> areas) {
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

    private PropMetaDataList getIndexedProperties(String area) {
        // Step 1: Fetch the index file of the area specified
        PropIDs propIDs = fetchIndexFile(area);

        // Step 2: Connect to DB and fetch the details
        return getProps(propIDs);
    }

    private Map<String, Set<String>> getDeletedProperties(List<String> areas) {
        // Step 1: Fetch all deleted props
        return areas.stream()
                .collect(Collectors.toMap(area -> area, area -> Collections.emptySet()));
    }

    private List<PropFilterableSortableData> deleteIndexedProperties(List<PropFilterableSortableData> props,
                                                                     Set<String> deletedPropIDs) {
        return props.stream()
                .filter(prop -> !deletedPropIDs.contains(prop.getPropID()))
                .collect(Collectors.toList());
    }

    private void deleteIndexedProperties(Map<String, PropMetaDataList> areaWiseIndexedProperties, String area,
                                         Set<String> deletedPropIDs) {

        PropMetaDataList propMetaDataList = areaWiseIndexedProperties.get(area);
        List<PropFilterableSortableData> props = propMetaDataList.getPropFilterableSortableData();
        props = deleteIndexedProperties(props, deletedPropIDs);
        propMetaDataList.setPropFilterableSortableData(props);
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

    public Map<String, PropMetaDataList> getProperties() {
        return areaWiseIndexedProperties;
    }
}
