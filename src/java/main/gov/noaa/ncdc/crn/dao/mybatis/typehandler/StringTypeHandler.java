package gov.noaa.ncdc.crn.dao.mybatis.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * A MyBatis abstract type handler which allows string-string mapping. Enables implementing class to constrain
 * parameters and/or provide default values, for example.
 */
public abstract class StringTypeHandler extends BaseTypeHandler<String> {
    abstract String dbToJava(String s);

    abstract String javaToDb(String s);

    /**
     * From DB to Java.
     */
    @Override
    public String getNullableResult(ResultSet rs, String columnLabel) throws SQLException {
        return dbToJava(rs.getString(columnLabel));
    }

    @Override
    public String getNullableResult(ResultSet rs, int index) throws SQLException {
        return dbToJava(rs.getString(index));
    }

    @Override
    public String getNullableResult(CallableStatement cs, int index) throws SQLException {
        return dbToJava(cs.getString(index));
    }

    /**
     * From Java to DB.
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType type) throws SQLException {
        ps.setString(i, javaToDb(parameter));
    }
}