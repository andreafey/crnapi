package gov.noaa.ncdc.crn.dao.mybatis.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class YNBooleanTypeHandler extends BaseTypeHandler<Boolean> {

    @Override
    public Boolean getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return stringToBoolean(rs.getString(columnName));
    }

    @Override
    public Boolean getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return stringToBoolean(rs.getString(columnIndex));
    }

    @Override
    public Boolean getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return stringToBoolean(cs.getString(columnIndex));
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType type) throws SQLException {
        ps.setString(i, booleanToString(parameter));
    }

    private Boolean stringToBoolean(String value) throws SQLException {
        if (value == null) {
            return Boolean.FALSE;
        }
        switch (value) {
        case "Y":
            return Boolean.TRUE;
        case "N":
            return Boolean.FALSE;
        default:
            throw new IllegalArgumentException("column contains data other than Y or N");
        }
    }

    private String booleanToString(Boolean value) {
        return Boolean.TRUE ? "Y" : "N";
    }

}
