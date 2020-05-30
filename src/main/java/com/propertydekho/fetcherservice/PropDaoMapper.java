package com.propertydekho.fetcherservice;

import com.propertydekho.fetcherservice.models.PropFilterableSortableData;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;


@Component
public class PropDaoMapper implements RowMapper<PropFilterableSortableData>
{

    public PropDaoMapper() {
    }

    public static final String PROP_ID_COL = "prop_id";
    public static final String PROP_NAME_COL = "prop_name";
    public static final String CONSTRUCTN_STATUS_COL = "constructn_status";
    public static final String SALE_TYPE_COL = "sale_type";
    public static final String BEDROOM_COL = "bedroom";
    public static final String AREA_COL = "area";
    public static final String PROP_PRICE_COL = "prop_price";
    public static final String SQ_FT_COL = "sq_ft";

    @Override
    public PropFilterableSortableData mapRow(ResultSet resultSet, int i) throws SQLException {
        return PropFilterableSortableData.builder()
                .propID(resultSet.getString(PROP_ID_COL))
                .propName(resultSet.getString(PROP_NAME_COL))
                .constructionStatus(resultSet.getString(CONSTRUCTN_STATUS_COL))
                .saleType(resultSet.getString(SALE_TYPE_COL))
                .bedroom(resultSet.getString(BEDROOM_COL))
                .area(resultSet.getString(AREA_COL))
                .propPrice(resultSet.getDouble(PROP_PRICE_COL))
                .sqft(resultSet.getInt(SQ_FT_COL))
                .build();
    }
}
