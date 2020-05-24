package com.propertydekho.fetcherservice.models;

import com.propertydekho.fetcherservice.entity.PropFilterableSortableData;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PropertiesDaoImpl implements PropertiesDao {

    NamedParameterJdbcTemplate template;

    public PropertiesDaoImpl(NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    @Override
    public List<PropFilterableSortableData> findAll() {
        return template.query("select * from property", new PropertyRowMapper());
    }

    @Override
    public void insertProperty(PropFilterableSortableData prop) {

        final String sql = "insert into property(prop_id, prop_name , prop_price, sq_ft, bedroom, constructn_status, sale_type, area) values(:prop_id, :prop_name , :prop_price, :sq_ft, :bedroom, :constructn_status, :sale_type, :area)";
        KeyHolder holder = new GeneratedKeyHolder();
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("prop_id", prop.getPropID())
                .addValue("prop_name", prop.getPropName())
                .addValue("prop_price", prop.getPropPrice())
                .addValue("sq_ft", prop.getSqft())
                .addValue("bedroom", prop.getBedroom())
                .addValue("constructn_status", prop.getConstructionStatus())
                .addValue("sale_type", prop.getSaleType())
                .addValue("area", prop.getArea());
        template.update(sql, param, holder);
    }

    @Override
    public void updateProperty(PropFilterableSortableData prop) {
        final String sql = "update property set prop_name=:prop_name, prop_price=:prop_price, sq_ft=:sq_ft, " +
                "bedroom=:bedroom, constructn_status=:constructn_status, sale_type=:sale_type" +
                "where prop_id=:prop_id";
        KeyHolder holder = new GeneratedKeyHolder();
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("prop_id", prop.getPropID())
                .addValue("prop_name", prop.getPropName())
                .addValue("prop_price", prop.getPropPrice())
                .addValue("sq_ft", prop.getSqft())
                .addValue("bedroom", prop.getBedroom())
                .addValue("constructn_status", prop.getConstructionStatus())
                .addValue("sale_type", prop.getSaleType());

        template.update(sql, param, holder);
    }

    @Override
    public void executeUpdateProperty(PropFilterableSortableData prop) {
        final String sql = "update property set prop_name=:prop_name, prop_price=:prop_price, sq_ft=:sq_ft, " +
                "bedroom=:bedroom, constructn_status=:constructn_status, sale_type=:sale_type" +
                "where prop_id=:prop_id";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("prop_id", prop.getPropID());
        map.put("prop_name", prop.getPropName());
        map.put("prop_price", prop.getPropPrice());
        map.put("sq_ft", prop.getSqft());
        map.put("bedroom", prop.getBedroom());
        map.put("constructn_status", prop.getConstructionStatus());
        map.put("sale_type", prop.getSaleType());
        template.execute(sql, map, new PreparedStatementCallback<Object>() {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps)
                    throws SQLException, DataAccessException {
                return ps.executeUpdate();
            }
        });
    }

    @Override
    public void deleteProperty(PropFilterableSortableData prop) {
        final String sql = "delete from property where prop_id=:prop_id";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("prop_id", prop.getPropID());
        template.execute(sql, map, new PreparedStatementCallback<Object>() {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps)
                    throws SQLException, DataAccessException {
                return ps.executeUpdate();
            }
        });
    }
}



