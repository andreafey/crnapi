package gov.noaa.ncdc.crn.dao.mybatis.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * MyBatis type handler callback for java.lang.Integer that are mapped to either 'NA' or an integer. If a value is
 * something other than an Integer in the database, including <code>null</code>, the resulting db String will be 'NA'.
 * <p>
 * DB --> Java ---------------- '600' 600 null null Java --> DB ---------------- null 'NA' 600 '600'
 */
public class NullNAIntegerTypeHandler extends BaseTypeHandler<Integer> {

    static final String DEFAULT = "NA";

    private Integer dbToJava(String s) {
        if (s == null || DEFAULT.equals(s))
            return null;
        try {
            return Integer.valueOf(s.trim());
        } catch (Exception ex) {
            return null;
        }
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
    public void setNonNullParameter(PreparedStatement ps, final int i, final Integer intparam, final JdbcType type)
            throws SQLException {
        if (intparam != null && intparam > 0) {
            ps.setString(i, intparam.toString());
        } else {
            ps.setString(i, DEFAULT);
        }
    }

}