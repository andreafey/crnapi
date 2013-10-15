package gov.noaa.ncdc.crn.persistence;

import gov.noaa.ncdc.crn.domain.Observation;
import gov.noaa.ncdc.crn.domain.StationDate;

import java.util.Collection;
import java.util.Map;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

public interface ObservationMapper {
    /**
     * Returns an Observation for a station for a single datetime
     * @param datetimeId The time of observation
     * @param stationId The station
     * @return The Observation for the station for that datetime
     * @throws DataAccessException (unchecked)
     */
    public abstract Observation selectObservation(@Param("datetimeId") final int datetimeId,
            @Param("stationId") final int stationId) throws DataAccessException;

    /**
     * Returns a Map{@code <Integer,Observation>} mapped by stationId for a single datetime for a list of stations.
     * @param datetimeId The time of observation
     * @param stationIds The stations to retrieve data for
     * @return A Map{@code <Integer,Observation>} for the datetime for the list of stations
     * @throws DataAccessException (unchecked)
     */
    @MapKey("stationId")
    public abstract Map<Integer, Observation> selectObservations(@Param("datetimeId") final int datetimeId,
            @Param("stationIds") final Collection<Integer> stationIds) throws DataAccessException;

    /**
     * Returns a Map{@code <StationDate,Observation>} for a datetime range for a list of stations.
     * @param startId The time of the beginning of the range
     * @param endId The time of the end of the range
     * @param stationIds The stations to retrieve data for
     * @return A Map{@code <StationDate,Observation>} for the datetime for the list of stations
     * @throws DataAccessException (unchecked)
     */
    @MapKey("stationDate")
    public abstract Map<StationDate, Observation> selectObservations(@Param("beginDatetimeId") final int startId,
            @Param("endDatetimeId") final int endId, @Param("stationIds") final Collection<Integer> stationIds)
            throws DataAccessException;

    /**
     * Returns a Map{@code <Integer,Observation>} mapped by stationId for a single datetime for all stations; maps
     * stationId to Observation.
     * @param datetimeId The time of observation
     * @return A Map{@code <Integer,Observation>} for the datetime for all stations
     * @throws DataAccessException (unchecked)
     */
    @MapKey("stationId")
    public abstract Map<Integer, Observation> selectObservations(@Param("datetimeId") final int datetimeId);

    /**
     * Returns a Map{@code <Integer,Observation>} mapped by datetimeId for a datetime range for a single station. The
     * implementation class may throttle the amount of data available for retrieval.
     * @param startDatetimeId The beginning observation time
     * @param endDatetimeId The end observation time
     * @return A Map{@code <Integer,Observation>} for the datetime range for this station
     * @throws DataAccessException (unchecked)
     */
    @MapKey("datetimeId")
    public abstract Map<Integer, Observation> selectObservations(@Param("beginDatetimeId") final int startDatetimeId,
            @Param("endDatetimeId") final int endDatetimeId, @Param("stationId") final int stationId)
            throws DataAccessException;

    /**
     * Returns a Map{@code <Integer,Observation>} of most recent Observations for all stations; maps stationId to
     * Observation.
     * @return A Map{@code <Integer,Observation>} of the most recent Observations for all stations
     * @throws DataAccessException (unchecked)
     */
    @MapKey("stationId")
    public abstract Map<Integer, Observation> selectCurrentObservations() throws DataAccessException;

    /**
     * Returns a Map{@code <Integer,Observation>} of most recent Observations for a network or networks; maps stationId
     * to Observation.
     * @return A Map{@code <Integer,Observation>} of the most recent Observations for a network or networks
     * @throws DataAccessException (unchecked)
     */
    @MapKey("stationId")
    public abstract Map<Integer, Observation> selectCurrentObservations(@Param("networkIds") final int... networkIds)
            throws DataAccessException;

    /**
     * Returns a datetimeId prior to the datetimeId argument where calculated precipitation or temperature is available
     * or null if one can not be found
     * @param datetimeId Only datetimes prior to this one will be considered
     * @param stationId The station to consider
     * @return datetimeId prior to the datetimeId argument where calculated precipitation or temperature is available or
     * null if one can not be found
     * @throws DataAccessException (unchecked)
     */
    /*
     * NOTE the difference between this method and getLastNonmissingDatetimeIdBefore(int datetimeId, Integer stationId)
     * is a guarantee of the presence of calcuated precip or temp in the observation
     */
    public abstract Integer selectLastNonmissingCalculatedDatetimeIdBefore(@Param("datetimeId") final int datetimeId,
            @Param("stationId") final int stationId) throws DataAccessException;

    /**
     * Returns a datetimeId prior to the datetimeId argument where an observation is available or null if one can not be
     * found
     * @param datetimeId Only datetimes prior to this one will be considered
     * @param stationId The station to consider
     * @return datetimeId prior to the datetimeId argument where an observation is available or null if one can not be
     * found
     * @throws DataAccessException (unchecked)
     */
    public abstract Integer selectLastNonmissingDatetimeIdBefore(@Param("datetimeId") final int datetimeId,
            @Param("stationId") final Integer stationId) throws DataAccessException;

    /**
     * Returns the subhourly frequency (4 or 12) associated with a particular observation
     * @param datetimeId The time of the observation
     * @param stationId The station transmitting the observation
     * @return subhourly frequency
     * @throws DataAccessException (unchecked)
     */
    public abstract Integer selectStepsPerHour(@Param("datetimeId") final int datetimeId,
            @Param("stationId") final int stationId) throws DataAccessException;

    /**
     * Retrieves the datetimeId of the first observation which is of a stream containing soil moisture/soil temp data
     * for a station
     * @param stationId
     * @return the datetimeId of the first observation which is of a stream containing soil moisture/soil temp data for
     * a station
     * @throws DataAccessException (unchecked)
     */
    public abstract int selectDateOfFirstSoilMoistureObservation(@Param("stationId") final int stationId)
            throws DataAccessException;

    /**
     * Updates an Observation's time exported to isd to the current UTC time
     * @param datetimeId
     * @param stationId
     * @throws DataAccessException
     */
    public abstract void updateTimeExportedToIsd(@Param("datetimeId") final int datetimeId,
            @Param("stationId") final int stationId) throws DataAccessException;

    /**
     * Deletes an observation from the database. Note this returns void because it is expected to be executed as a batch
     * statement and the number of rows affected by the insert is unavailable.
     * @param datetimeId the datetime of the observation to be deleted
     * @param stationId the id of the station whose observation will be deleted
     * @throws DataAccessException (unchecked)
     */
    public abstract void deleteObservation(@Param("stationId") final int stationId,
            @Param("datetimeId") final int datetimeId) throws DataAccessException;

    /**
     * Deletes a range of Observations for a station from the database, including all facts, flags, and obloadlogs.
     * @param stationId The station id of the facts to be deleted
     * @param beginDatetimeId The beginning of the range of datetimeIds to be deleted
     * @param endDatetimeId The end of the range of datetimeIds to be deleted
     * @throws DataAccessException (unchecked)
     */
    public abstract void deleteObservations(@Param("stationId") final int stationId,
            @Param("beginDatetimeId") final int beginDatetimeId, @Param("endDatetimeId") int endDatetimeId)
            throws DataAccessException;

    /**
     * Deletes an ob_loadlog from the database. Note this returns void because the number of rows affected by the insert
     * is unavailable.
     * @param datetimeId the datetime of the ob_loadlog to be deleted
     * @param stationId the id of the station whose ob_loadlog will be deleted
     * @throws DataAccessException (unchecked)
     */
    public abstract void deleteObLoadlog(@Param("stationId") final int stationId,
            @Param("datetimeId") final int datetimeId) throws DataAccessException;

    /**
     * Deletes a range for a station from the ob_loadlog table
     * @param stationId The station id of the ob_loadlogs to be deleted
     * @param beginDatetimeId The beginning of the range of datetimeIds to be deleted
     * @param endDatetimeId The end of the range of datetimeIds to be deleted
     * @throws DataAccessException (unchecked)
     */
    public abstract void deleteObLoadlogs(@Param("stationId") final int stationId,
            @Param("beginDatetimeId") final int beginDatetimeId, @Param("endDatetimeId") int endDatetimeId)
            throws DataAccessException;

    /**
     * Inserts an Observation, without values, flags, or ob_loadlog into the database. Updates relevant database load
     * times with current UTC timestamp. Expects that there are no constraint violations present. Throws a
     * DataAccessException if anything already exists in the database. Note this returns void because it is expected to
     * be executed as a batch statement and the number of rows affected by the insert is unavailable.
     * @param observation the observation to insert
     * @throws DataAccessException (unchecked) if any constraint violations or if some other data access exception is
     * thrown
     */
    public abstract void insertObservation(final Observation observation) throws DataAccessException;

    /**
     * Inserts an ob_loadlog, without the observation, values, or flags, into the database. Updates relevant database
     * load times with current UTC timestamp. Expects that there are no constraint violations present. Throws a
     * DataAccessException if anything already exists in the database. Note this returns void because it is expected to
     * be executed as a batch statement and the number of rows affected by the insert is unavailable.
     * @param observation the observation to insert
     * @throws DataAccessException (unchecked) if any constraint violations or if some other data access exception is
     * thrown
     */
    public abstract void insertObLoadlog(final Observation observation) throws DataAccessException;
}
