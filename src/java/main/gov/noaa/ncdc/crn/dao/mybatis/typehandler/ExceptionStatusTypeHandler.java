package gov.noaa.ncdc.crn.dao.mybatis.typehandler;

import gov.noaa.ncdc.crn.domain.ExceptionStatus;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

public class ExceptionStatusTypeHandler extends BaseTypeHandler<ExceptionStatus> {

    @Override
    public ExceptionStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return getStatus(rs.getString(columnName));
    }

    @Override
    public ExceptionStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return getStatus(rs.getString(columnIndex));
    }

    @Override
    public ExceptionStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return getStatus(cs.getString(columnIndex));
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ExceptionStatus parameter, JdbcType type)
            throws SQLException {
        ps.setString(i, getString(parameter));
    }

    private ExceptionStatus getStatus(String status) {
        return ExceptionStatus.valueOf(status.toUpperCase().replace('-', '_'));
    }

    private String getString(ExceptionStatus status) {
        String name = status.name();
        Iterable<String> parts = Splitter.on('_').split(name);
        return Joiner.on('-').join(Iterables.transform(parts, titleCaseTransform));
    }

    private Function<String, String> titleCaseTransform = new Function<String, String>() {
        @Override
        public String apply(final String input) {
            return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
        }
    };

}
