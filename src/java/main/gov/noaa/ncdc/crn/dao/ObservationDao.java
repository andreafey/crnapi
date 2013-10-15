package gov.noaa.ncdc.crn.dao;

import gov.noaa.ncdc.crn.domain.ElementValue;
import gov.noaa.ncdc.crn.domain.Observation;
import gov.noaa.ncdc.crn.domain.ObservationWithData;
import gov.noaa.ncdc.crn.domain.StationDate;

import java.util.Collection;
import java.util.Map;

import org.springframework.dao.DataAccessException;

public interface ObservationDao {

    /**
     * Returns an Observation for a station for a single datetime
     * @param datetimeId The time of observation
     * @param stationId The station
     * @return The Observation for the station for that datetime
     * @throws DataAccessException (unchecked)
     */
    public abstract Observation getObservation(int datetimeId, int stationId) throws DataAccessException;

    /**
     * Returns a Map<Integer,Observation> mapped by stationId for a single datetime for a list of stations.
     * @param datetimeId The time of observation
     * @param stationIds The stations to retrieve data for
     * @return A Map<Integer,Observation> for the datetime for the list of stations
     * @throws DataAccessException (unchecked)
     */
    public abstract Map<Integer, Observation> getObservations(int datetimeId, Collection<Integer> stationIds)
            throws DataAccessException;

    /**
     * Returns a Map<StationDate,Observation> for a datetime range for a list of stations.
     * @param startId The time of the beginning of the range
     * @param endId The time of the end of the range
     * @param stationIds The stations to retrieve data for
     * @return A Map<StationDate,Observation> for the datetime for the list of stations
     * @throws DataAccessException (unchecked)
     */
    public abstract Map<StationDate, Observation> getObservations(int startId, int endId, Collection<Integer> stationIds)
            throws DataAccessException;

    /**
     * Returns a Map<Integer,Observation> mapped by stationId for a single datetime for all stations; maps stationId to
     * Observation.
     * @param datetimeId The time of observation
     * @return A Map<Integer,Observation> for the datetime for all stations
     * @throws DataAccessException (unchecked)
     */
    public abstract Map<Integer, Observation> getObservations(int datetimeId);

    /**
     * Returns a Map<Integer,Observation> mapped by datetimeId for a datetime range for a single stations; maps
     * stationId to Observation. The implementation class may throttle the amount of data available for retrieval.
     * @param startDatetimeId The beginning observation time
     * @param endDatetimeId The end observation time
     * @return A Map<Integer,Observation> for the datetime range for this station
     * @throws DataAccessException (unchecked)
     */
    public abstract Map<Integer, Observation> getObservations(int startDatetimeId, int endDatetimeId, int stationId)
            throws DataAccessException;

    /**
     * Returns a Map<Integer,Observation> of most recent Observations for all stations; maps stationId to Observation.
     * @return A Map<Integer,Observation> of the most recent Observations for all stations
     * @throws DataAccessException (unchecked)
     */
    public abstract Map<Integer, Observation> getCurrentObservations() throws DataAccessException;

    /**
     * Returns a Map<Integer,Observation> for current for certain stations; maps stationId to Observation.
     * @param stationIds the stations to retrieve
     * @return A Map<Integer,Observation> for the current observation for certain stations
     * @throws DataAccessException (unchecked)
     */
    public abstract Map<Integer, Observation> getCurrentObservations(Collection<Integer> stationIds)
            throws DataAccessException;

    /**
     * Returns the most recent Observation for a station
     * @param stationId the station to retrieve
     * @return the most recent Observation for a station
     * @throws DataAccessException (unchecked)
     */
    public abstract Observation getCurrentObservation(int stationId) throws DataAccessException;

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
    public abstract Integer getLastNonmissingCalculatedDatetimeIdBefore(int datetimeId, int stationId)
            throws DataAccessException;

    /**
     * Returns a datetimeId prior to the datetimeId argument where an observation is available or null if one can not be
     * found
     * @param datetimeId Only datetimes prior to this one will be considered
     * @param stationId The station to consider
     * @return datetimeId prior to the datetimeId argument where an observation is available or null if one can not be
     * found
     * @throws DataAccessException (unchecked)
     */
    public abstract Integer getLastNonmissingDatetimeIdBefore(int datetimeId, Integer stationId)
            throws DataAccessException;

    /**
     * Returns a Map<Integer,Observation> of most recent Observations for a network or networks; maps stationId to
     * Observation.
     * @return A Map<Integer,Observation> of the most recent Observations for a network or networks
     * @throws DataAccessException (unchecked)
     */
    public abstract Map<Integer, Observation> getCurrentObservationsForNetworks(int... networkIds)
            throws DataAccessException;

    /**
     * Returns the subhourly frequency (4 or 12) associated with a particular observation
     * @param stationId The station transmitting the observation
     * @param datetimeId The time of the observation
     * @return subhourly frequency
     * @throws DataAccessException (unchecked)
     */
    public abstract Integer getStepsPerHour(int datetimeId, int stationId) throws DataAccessException;

    /**
     * Retrieves the datetimeId of the first observation which is of a stream containing soil moisture/soil temp data
     * for a station
     * @param stationId
     * @return the datetimeId of the first observation which is of a stream containing soil moisture/soil temp data for
     * a station
     * @throws DataAccessException (unchecked)
     */
    public abstract int getDateOfFirstSoilMoistureObservation(int stationId) throws DataAccessException;

    /**
     * Updates an Observation's time exported to isd to the current UTC time
     * @param datetimeId
     * @param stationId
     * @throws DataAccessException
     */
    public abstract void updateTimeExportedToIsd(int datetimeId, int stationId) throws DataAccessException;

    /**
     * Deletes an observation from the database. Note this returns void because it is expected to be executed as a batch
     * statement and the number of rows affected by the insert is unavailable.
     * @param datetimeId the datetime of the observation to be deleted
     * @param stationId the id of the station whose observation will be deleted
     * @throws DataAccessException (unchecked)
     */
    public abstract void deleteObservation(int datetimeId, int stationId) throws DataAccessException;

    /**
     * Deletes a Collection<Observation> from the database, including all facts, flags, and obloadlogs.
     * @param observations The Observations to be deleted
     * @throws DataAccessException (unchecked)
     */
    public abstract void deleteObservations(Collection<Observation> observations);

    /**
     * Deletes a range of Observations for a station from the database, including all facts, flags, and obloadlogs.
     * @param stationId The station id of the facts to be deleted
     * @param beginDatetimeId The beginning of the range of datetimeIds to be deleted
     * @param endDatetimeId The end of the range of datetimeIds to be deleted
     * @throws DataAccessException (unchecked)
     */
    public abstract void deleteObservations(int stationId, int beginDatetimeId, int endDatetimeId)
            throws DataAccessException;

    /**
     * Deletes facts from an Observation only if they are calculated values.
     * @param stationId the id of the station whose facts and flags will be deleted if null, all stations' values will
     * be deleted based on the other params
     * @param beginDatetimeId the beginning datetime of the range of facts and flags to be deleted
     * @param endDatetimeId the end datetime of the range of facts and flags to be deleted
     * @param elementIds the ids of the calculated elements to be deleted; if the ids are not associated with calculated
     * values, they will be ignored
     * @return the number of facts affected by the deletion
     * @throws DataAccessException
     */
    public abstract int deleteCalculatedValues(Integer stationId, int beginDatetimeId, int endDatetimeId,
            int... elementIds) throws DataAccessException;

    /**
     * Deletes facts from an Observation only if they are calculated values.
     * @param values the facts to delete
     * @return the number of facts affected by the deletion
     * @throws DataAccessException
     */
    public abstract void deleteCalculatedValues(Collection<ElementValue> values) throws DataAccessException;

    /**
     * Inserts a Collection<Observation>, their values and flags, and their ob_loadlogs into the database. Updates
     * relevant database load times with current UTC timestamp. Expects that there are no constraint violations present.
     * Throws a DataAccessException if anything already exists in the database. Note this implementation returns void
     * because it is executed as a batch statement and the number of rows affected by the insert is unavailable.
     * @param observations the observations to insert
     * @throws DataAccessException (unchecked) if any constraint violations or if some other data access exception is
     * thrown
     */
    public abstract void insertObservations(Collection<ObservationWithData> observations) throws DataAccessException;

    /**
     * Inserts an Observation, its values and flags, and the ob_loadlog into the database. Updates relevant database
     * load times with current UTC timestamp. Expects that there are no constraint violations present. Throws a
     * DataAccessException if anything already exists in the database. Note this returns void because it is expected to
     * be executed as a batch statement and the number of rows affected by the insert is unavailable.
     * @param observations the observations to insert
     * @throws DataAccessException (unchecked) if any constraint violations or if some other data access exception is
     * thrown
     */
    public abstract void insertObservation(ObservationWithData observation) throws DataAccessException;

    /**
     * In a Collection<Observation>, updates each member's TimeExportedToIsd
     * @param observations The Observations to update
     * @throws DataAccessException
     */
    public abstract void updateTimeExportedToIsd(Collection<Observation> observations) throws DataAccessException;

    /**
     * In a Collection<ElementValue>, updates each member's values and flags, and updates the Observation's timestamp.
     * Values are deleted when ElementValue.value is null and the Element is calculated.
     * @param values The ElementValues to alter
     * @throws IllegalArgumentException when ElementValue.value is null and the Element is observed not calculated
     */
    public abstract void updateElementValues(Collection<ElementValue> values);
}
