package gov.noaa.ncdc.crn.dao;

import org.springframework.dao.DataAccessException;

/**
 * This class provides a few simple methods to determine whether the database is available and to flush the stored data
 * cache.
 * @author Andrea Fey
 */
public interface ConnectionDao {

    /**
     * Checks to see whether or not a database connection is available
     * @return true if a connection is available, false if not
     */
    public abstract boolean testConnection();

    /**
     * Flushes all data caches.
     */
    public abstract void flushDataCache() throws DataAccessException;
}
