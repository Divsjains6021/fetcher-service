package com.propertydekho.fetcherservice.models;

import com.propertydekho.fetcherservice.entity.PropFilterableSortableData;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PropertyRowMapper implements RowMapper<PropFilterableSortableData> {
    @Override
    public PropFilterableSortableData mapRow(ResultSet rs, int arg1) throws SQLException {
        PropFilterableSortableData propFSData = new PropFilterableSortableData();
        propFSData.setPropID(rs.getString("prop_id"));
        propFSData.setPropName(rs.getString("prop_name"));
        propFSData.setPropPrice(rs.getDouble("prop_price"));
        propFSData.setSqft(rs.getInt("sq_ft"));
        propFSData.setArea(rs.getString("area"));
        propFSData.setBedroom(rs.getString("bedroom"));
        propFSData.setConstructionStatus(rs.getString("constructn_status"));
        propFSData.setSaleType(rs.getString("sale_type"));
        return propFSData;
    }

}
