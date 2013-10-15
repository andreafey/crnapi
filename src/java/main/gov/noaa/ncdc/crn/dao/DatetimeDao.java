package gov.noaa.ncdc.crn.dao;

import gov.noaa.ncdc.crn.domain.Datetime;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

public interface DatetimeDao {

    /**
     * Retrieves a datetimeId from the database for a date
     * @param YYYYMMDDHH24 the UTC date of the datetimeId to retrieve
     * @return the datetimeId for the date or null if the date is not defined in the database, for example when the date
     * predates the CRN network's period of record
     * @throws DataAccessException (unchecked)
     */
    public abstract Integer getDatetimeId(String YYYYMMDDHH24) throws DataAccessException;

    /**
     * Retrieves a Datetime from the database for a date
     * @param YYYYMMDDHH24 the UTC date of the datetimeId to retrieve
     * @return the Datetime for the date
     * @throws DataAccessException (unchecked)
     */
    public abstract Datetime getDatetime(String YYYYMMDDHH24) throws DataAccessException;

    /**
     * Retrieves a List<Integer> of datetimeIds from the database for a date range
     * @param beginYYYYMMDDHH24 begin date, where HH24 must be between 0 and 23
     * @param endYYYYMMDDHH24 end date, where HH24 must be between 0 and 23
     * @return a List<Integer> of datetimeIds which fall in the date range
     * @throws DataAccessException (unchecked)
     */
    public abstract List<Integer> getDatetimeIds(String beginYYYYMMDDHH24, String endYYYYMMDDHH24)
            throws DataAccessException;

    /**
     * Retrieves a List<Datetime> from the database of hours for a date range
     * @param beginYYYYMMDDHH24 begin UTC date, where HH24 between 0 and 23
     * @param endYYYYMMDDHH24 end UTC date, where HH24 between 0 and 23
     * @return a List<Datetime> of hours which fall in the date range
     * @throws DataAccessException (unchecked)
     */
    public abstract List<Datetime> getDatetimeList(String beginYYYYMMDDHH24, String endYYYYMMDDHH24)
            throws DataAccessException;

    /**
     * Retrieves a Map<Integer,Datetime> from the database of hours for a date range
     * @param beginYYYYMMDDHH24 begin UTC date, where HH24 between 0 and 23
     * @param endYYYYMMDDHH24 end UTC date, where HH24 between 0 and 23
     * @return a Map<Integer,Datetime> mapping datetimeId to Datetime of hours which fall in the date range
     * @throws DataAccessException (unchecked)
     */
    public abstract Map<Integer, Datetime> getDatetimeMap(String beginYYYYMMDDHH24, String endYYYYMMDDHH24)
            throws DataAccessException;

    /**
     * Retrieves a Datetime from the database for a datetimeId
     * @param datetimeId of the Datetime to retrieve
     * @return the Datetime for the datetimeId
     * @throws DataAccessException (unchecked)
     */
    public abstract Datetime getDatetime(int datetimeId) throws DataAccessException;

    /**
     * Retrieves a Map<Integer,Datetime> from the database of hours for a datetime range
     * @param beginDatetimeId begin datetimeId
     * @param endDatetimeId end datetimeId
     * @return a Map<Integer,Datetime> mapping datetimeId to Datetime of hours which fall in the datetimeId range
     * @throws DataAccessException (unchecked)
     */
    public abstract Map<Integer, Datetime> getDatetimeMap(Integer beginDatetimeId, Integer endDatetimeId)
            throws DataAccessException;

    /**
     * Retrieves a Map<Integer,Datetime> from the database of hours for a Collection<Integer> of datetimeIds
     * @param datetimeIds The Collection<Integer> of datetimeIds
     * @return a Map<Integer,Datetime> mapping datetimeId to Datetime
     * @throws DataAccessException (unchecked)
     */
    public abstract Map<Integer, Datetime> getDatetimeMap(Collection<Integer> datetimeIds) throws DataAccessException;
}
