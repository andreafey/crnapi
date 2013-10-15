package gov.noaa.ncdc.crn.aop;

import java.sql.SQLException;

import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

class PointcutExceptionThrower {

    /**
     * This method throws a runtime exception. It is intended to be used for testing by being inserted as a pointcut at
     * a point in the code where you want to monitor what happens when a runtime exception occurs.
     */
    void throwRuntimeException() {
        throw new RuntimeException();
    }

    /**
     * This method throws an SQLException. It is intended to be used for testing by being inserted as a pointcut at a
     * point in the code where you want to monitor what happens when an SQLException occurs, such as a query timeout or
     * a unique constraint violation.
     */
    void throwSQLException() throws SQLException {
        throw new SQLException();
    }

    /**
     * This method throws a DataAccessException (unchecked). It is intended to be used for testing by being inserted as
     * a pointcut at a point in the code where you want to monitor what happens when a DataAccessException occurs, such
     * as a wrapped SQLException.
     */
    void throwDataAccessException() {
        SQLExceptionTranslator translator = new SQLErrorCodeSQLExceptionTranslator();
        throw translator.translate("I'm a wrapper", null, new SQLException("I'm a dummy exception"));
    }
}
