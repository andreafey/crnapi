package gov.noaa.ncdc.crn.dao.mybatis.typehandler;

import gov.noaa.ncdc.crn.util.TimeUtils;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * An iBATIS type handler callback for java.sql.Dates in UTC that are mapped to strings in yyyymmdd format. If a value
 * is <code>null</code>, the resulting String will be null
 */
public class NullNADatestringTypeHandler extends BaseTypeHandler<Date> {

    static final String DEFAULT = "NA";

    private Date dbToJava(String s) {
        if (s == null || DEFAULT.equals(s))
            return null;
        try {
            Calendar cal = TimeUtils.createUTCCalendar(s.trim());
            return new Date(cal.getTimeInMillis());
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * From DB to Java.
     */
    @Override
    public Date getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return dbToJava(rs.getString(columnName));
    }

    /**
     * From DB to Java.
     */
    @Override
    public Date getNullableResult(ResultSet rs, int index) throws SQLException {
        return dbToJava(rs.getString(index));
    }

    /**
     * From DB to Java.
     */
    @Override
    public Date getNullableResult(CallableStatement cs, int index) throws SQLException {
        return dbToJava(cs.getString(index));
    }

    /**
     * From Java to DB.
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, final int i, final Date dateparam, final JdbcType type)
            throws SQLException {
        if (dateparam != null) {
            Calendar cal = TimeUtils.createUTCCalendar(dateparam);
            String yyyymmdd = TimeUtils.getYYYYMMDD(cal);
            ps.setString(i, yyyymmdd);
        } else {
            ps.setString(i, DEFAULT);
        }
    }

}