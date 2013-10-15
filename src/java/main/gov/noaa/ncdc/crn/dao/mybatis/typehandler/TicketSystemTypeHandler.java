package gov.noaa.ncdc.crn.dao.mybatis.typehandler;

import gov.noaa.ncdc.crn.domain.ExceptionTicketSystem;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class TicketSystemTypeHandler extends BaseTypeHandler<ExceptionTicketSystem> {

    @Override
    public ExceptionTicketSystem getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return ExceptionTicketSystem.valueOf(rs.getString(columnName));
    }

    @Override
    public ExceptionTicketSystem getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return ExceptionTicketSystem.valueOf(rs.getString(columnIndex));
    }

    @Override
    public ExceptionTicketSystem getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return ExceptionTicketSystem.valueOf(cs.getString(columnIndex));
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ExceptionTicketSystem parameter, JdbcType type)
            throws SQLException {
        ps.setString(i, parameter.name());
    }

}
