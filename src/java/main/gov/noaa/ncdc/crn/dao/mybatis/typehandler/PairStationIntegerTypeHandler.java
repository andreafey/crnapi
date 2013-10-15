package gov.noaa.ncdc.crn.dao.mybatis.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * An iBATIS type handler callback for java.lang.Integer that are mapped to either '-1' or a valid stationId in the
 * database. If a value is something other than a positive Integer in the database, including <code>null</code>, the
 * resulting Integer will be -1.
 * 
 * <pre>
 * DB   -->  Java
 * ----------------
 * '0'       -1
 * null      -1
 * 'NA'      -1
 * '1026'    1026
 * 
 * Java  -->  DB
 * ----------------
 * -1       '-1'
 * 1026     '1026'
 * 0        '-1'
 * </pre>
 */
public class PairStationIntegerTypeHandler extends BaseTypeHandler<Integer> {

    /** Indicates no pair station present. */
    static final Integer NO_PAIR = -1;

    public Integer dbToJava(String s) {
        if (s != null) {
            try {
                final Integer intValue = Integer.valueOf(s);
                if (intValue > 0) {
                    return intValue;
                }
            } catch (NumberFormatException nfe) {
                // handles cases where value in DB is non-numerical, e.g. 'NA'
            }
        }
        return NO_PAIR;
    }

    /**
     * From DB to Java.
     */
    @Override
    public Integer getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return dbToJava(rs.getString(columnName));
    }

    /**
     * From DB to Java.
     */
    @Override
    public Integer getNullableResult(ResultSet rs, int index) throws SQLException {
        return dbToJava(rs.getString(index));
    }

    /**
     * From DB to Java.
     */
    @Override
    public Integer getNullableResult(CallableStatement cs, int index) throws SQLException {
        return dbToJava(cs.getString(index));
    }

    /**
     * From Java to DB.
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Integer intparam, JdbcType type) throws SQLException {
        if (intparam != null && intparam > 0) {
            ps.setInt(i, intparam);
        } else {
            ps.setInt(i, NO_PAIR);
        }
    }

}