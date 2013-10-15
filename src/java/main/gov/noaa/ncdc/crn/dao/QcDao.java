package gov.noaa.ncdc.crn.dao;

import gov.noaa.ncdc.crn.domain.QcDeltaParam;
import gov.noaa.ncdc.crn.domain.QcRangeParam;

import java.util.Map;

import org.springframework.dao.DataAccessException;

public interface QcDao {

    /**
     * Retrieves the best QcRangeParam choice from the provided parameters. If multiple QC range parameters which match
     * an elementId are available, selects the one which matches the station (and the stream first and then the month if
     * these are specified in the database for the stream), then the one which matches that month, then the one which
     * matches the particular stream.
     * @param elementId The elementId to retrieve
     * @param streamId The streamId to retrieve
     * @param stationId The stationId to retrieve
     * @param month The month to retrieve
     * @return best QcRangeParam for the given parameters
     * @throws DataAccessException (unchecked)
     */
    public abstract QcRangeParam getQcRangeParam(int elementId, int streamId, int stationId, int month)
            throws DataAccessException;

    /**
     * Retrieves a Map<Integer,QcRangeParam> mapping elementId to the best QcRangeParam choice from the provided
     * parameters. If multiple QC range parameters which match an elementId are available, selects the one which matches
     * the station (and the stream first and then the month if these are specified in the database for the stream), then
     * the one which matches that month, then the one which matches the particular stream.
     * @param streamId The streamId to retrieve
     * @param stationId The stationId to retrieve
     * @param month The month to retrieve
     * @return Map<Integer,QcRangeParam> mapping elementId to the best QcRangeParam for the given parameters
     * @throws DataAccessException (unchecked)
     */
    public abstract Map<Integer, QcRangeParam> getQcRangeParams(int streamId, int stationId, int month)
            throws DataAccessException;

    /**
     * Retrieves the best QcDeltaParam choice from the provided parameters. If multiple QC delta parameters which match
     * an elementId are available, selects the one which matches the station (and the stream first and then the month if
     * these are specified in the database for the stream), then the one which matches that month, then the one which
     * matches the particular stream.
     * @param elementId The elementId to retrieve
     * @param streamId The streamId to retrieve
     * @param stationId The stationId to retrieve
     * @param month The month to retrieve
     * @return best QcDeltaParam for the given parameters
     * @throws DataAccessException (unchecked)
     */
    public abstract QcDeltaParam getQcDeltaParam(int elementId, int streamId, int stationId, int month)
            throws DataAccessException;

    /**
     * Retrieves a Map<Integer,QcDeltaParam> mapping elementId to the best QcDeltaParam choice from the provided
     * parameters. If multiple QC delta parameters which match an elementId are available, selects the one which matches
     * the station (and the stream first and then the month if these are specified in the database for the stream), then
     * the one which matches that month, then the one which matches the particular stream.
     * @param streamId The streamId to retrieve
     * @param stationId The stationId to retrieve
     * @param month The month to retrieve
     * @return Map<Integer,QcDeltaParam> mapping elementId to the best QcDeltaParam for the given parameters
     * @throws DataAccessException (unchecked)
     */
    public abstract Map<Integer, QcDeltaParam> getQcDeltaParams(int streamId, int stationId, int month)
            throws DataAccessException;

}
