package gov.noaa.ncdc.crn.dao;

import java.sql.SQLException;

/**
 * This Exception is thrown when a database connection can not be secured.
 * @author Andrea Fey
 */
@SuppressWarnings("serial")
public class ConnectionUnavailableException extends SQLException {
    private final String error;
    public final static String DEFAULT_ERROR = "A connection to the database is not available.";

    public ConnectionUnavailableException() {
        super();
        error = DEFAULT_ERROR;
    }

    public ConnectionUnavailableException(String error) {
        super();
        this.error = error == null ? DEFAULT_ERROR : error;
    }

    @Override
    public String getMessage() {
        return error;
    }

}
