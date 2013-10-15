package gov.noaa.ncdc.crn.service;

import gov.noaa.ncdc.crn.dao.ElementDao;
import gov.noaa.ncdc.crn.dao.ObservationDao;
import gov.noaa.ncdc.crn.domain.ElementValue;
import gov.noaa.ncdc.crn.domain.Observation;
import gov.noaa.ncdc.crn.domain.ObservationWithData;
import gov.noaa.ncdc.crn.domain.StationDateElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

@Service
public class ObservationService {
    @Autowired
    private ObservationDao observationDao;
    @Autowired
    private ElementDao elementDao;

    private static Log LOG = LogFactory.getLog(ObservationService.class);
    // maximum number of observation hours to allow
    private static final int MAX_OB_HOURS = 368 * 24;

    /**
     * Inserts ObservationWithData into the database, including all ElementValues (facts and flags). Updates the
     * observation with the timestamp of the load time. Inserts an entry into the ob_loadlog table.
     * <p>
     * Calculated values may be included with the observation or added later. There must not be an observation in the
     * database for the station/datetime already.
     * <p>
     * Expects that no observation for this station/datetime is currently in the database. Throws a DataAccessException
     * if the observation already exists in the database.
     * @param observation the observation to insert
     * @throws DataAccessException (unchecked) if observation not unique or if some other data access exception is
     * thrown
     */
    @Transactional
    public void insertObservation(ObservationWithData observation) throws DataAccessException {
        observationDao.insertObservation(observation);
    }

    /**
     * Inserts Collection{@code <ObservationWithData>} into the database, including all ElementValues (facts and flags).
     * Updates observations with the timestamp of the load time. Inserts an entry into the ob_loadlog table for each
     * Observation.
     * <p>
     * Calculated values may be included with the Observation or added later. There must not be an Observation in the
     * database for the station/datetime already.
     * <p>
     * Expects that no observation for this station/datetime is currently in the database. Throws a DataAccessException
     * if the observation already exists in the database.
     * @param observation the ObservationWithData to insert
     * @throws DataAccessException (unchecked) if observation not unique or if some other data access exception is
     * thrown
     */
    @Transactional
    public void insertObservations(Collection<ObservationWithData> observations) throws DataAccessException {
        observationDao.insertObservations(observations);
    }

    /**
     * Returns an Observation for a station for a single datetime
     * @param datetimeId The time of observation
     * @param stationId The station
     * @return The Observation for the station for that datetime
     * @throws DataAccessException (unchecked)
     */
    public Observation getObservation(int datetimeId, int stationId) throws DataAccessException {
        return observationDao.getObservation(datetimeId, stationId);
    }

    /**
     * Deletes an observation from the database, including all facts, flags, and obloadlogs.
     * @param datetimeId the datetime of the observation to be deleted
     * @param stationId the id of the station whose observation will be deleted
     * @throws DataAccessException (unchecked)
     */
    @Transactional
    public void deleteObservation(int datetimeId, int stationId) throws DataAccessException {
        observationDao.deleteObservation(datetimeId, stationId);
    }

    /**
     * Deletes a range of Observations for a station from the database, including all facts, flags, and obloadlogs.
     * @param stationId The station id of the facts to be deleted
     * @param beginDatetimeId The beginning of the range of datetimeIds to be deleted
     * @param endDatetimeId The end of the range of datetimeIds to be deleted
     * @throws DataAccessException (unchecked)
     */
    @Transactional
    public void deleteObservations(int stationId, int beginDatetimeId, int endDatetimeId) throws DataAccessException {
        observationDao.deleteObservations(stationId, beginDatetimeId, endDatetimeId);

    }

    /**
     * Deletes a Collection{@code <Observation>} from the database, including all facts, flags, and obloadlogs.
     * @param observations The Observations to be deleted
     * @throws DataAccessException (unchecked)
     */
    @Transactional
    public void deleteObservations(Collection<Observation> observations) throws DataAccessException {
        observationDao.deleteObservations(observations);
    }

    /**
     * Returns an ObservationWithData for a station for a single datetime; includes all the observed and calculated
     * values currently in the database
     * @param datetimeId The time of observation
     * @param stationId The station
     * @return The ObservationWithData for the station for that datetime
     * @throws DataAccessException (unchecked)
     */
    @Transactional
    public ObservationWithData getObservationWithData(int datetimeId, int stationId) throws DataAccessException {
        Observation ob = observationDao.getObservation(datetimeId, stationId);
        if (ob != null) {
            Map<Integer, ElementValue> values = elementDao.getElementValues(datetimeId, stationId);
            return new ObservationWithData(ob, values);
        }
        return null;
    }

    /*
     * Note that this method requires two database calls, one to get the Observations and the other to get the
     * ElementValues. This is an effort to avoid the iBATIS n+1 problem. TODO see if the n+1 problem still exists in
     * mybatis
     */
    /**
     * Returns a List{@code <ObservationWithData>} for a station for a time period; includes all the observed and
     * calculated values currently in the database
     * @param beginDatetimeId The beginning time of observation
     * @param endDatetimeId The end time of observation
     * @param stationId The station
     * @return The List{@code <ObservationWithData>} for the station for that time range
     * @throws DataAccessException (unchecked)
     */
    @Transactional
    public List<ObservationWithData> getObservationsWithData(int beginDatetimeId, int endDatetimeId, int stationId)
            throws DataAccessException {
        int diffs = endDatetimeId - beginDatetimeId;
        Preconditions.checkArgument(diffs > 0, "end datetime before begin datetime");
        Preconditions.checkArgument(diffs <= MAX_OB_HOURS, "too many hours requested (max " + MAX_OB_HOURS + " ["
                + MAX_OB_HOURS / 24 + " days])");
        // first get the Observations
        Map<Integer, Observation> obs = observationDao.getObservations(beginDatetimeId, endDatetimeId, stationId);
        // then get the ElementValues
        Map<String, Object> params = new HashMap<>();
        params.put("begin", beginDatetimeId);
        params.put("end", endDatetimeId);
        params.put("stationId", stationId);
        Map<StationDateElement, ElementValue> values = elementDao.getElementValues(params);
        List<Integer> datetimeIds = new ArrayList<>(obs.keySet());
        Collections.sort(datetimeIds);
        // lastly merge the Observations and their associated ElementValues
        // into ObservationWithData objects
        Map<Integer, Map<Integer, ElementValue>> convertedValues = convertValues(values);
        List<ObservationWithData> obsList = new ArrayList<>();
        for (Integer dt : datetimeIds) {
            ObservationWithData owd = new ObservationWithData(obs.get(dt), convertedValues.get(dt));
            obsList.add(owd);
        }
        return obsList;
    }

    /**
     * Provides a Map{@code <Integer, Map<Integer, ElementValue>>} for a single station (assumed). The outer map uses
     * datetimeId as the key, and the inner map uses elementId as the key.
     * @return Map{@code <Integer, Map<Integer,ElementValue>>} mapping datetimeId to elementId-mapped ElementValues so
     * you can retrieve all the element values for an hour
     */
    private Map<Integer, Map<Integer, ElementValue>> convertValues(Map<StationDateElement, ElementValue> values) {
        Map<Integer, Map<Integer, ElementValue>> converted = new HashMap<>();
        for (ElementValue value : values.values()) {
            Map<Integer, ElementValue> map = converted.get(value.getDatetimeId());
            if (map == null) {
                map = new HashMap<>();
                converted.put(value.getDatetimeId(), map);
            }
            map.put(value.getElementId(), value);
        }
        return converted;
    }

    /**
     * Returns a Map{@code <Integer,Observation>} mapped by stationId for a single datetime for a list of stations; maps
     * stationId to Observation.
     * @param datetimeId The time of observation
     * @param stationIds The stations to retrieve data for
     * @return A Map{@code <Integer,Observation>} for the datetime for the list of stations
     * @throws DataAccessException (unchecked)
     */
    public Map<Integer, Observation> getObservations(int datetimeId, Collection<Integer> stationIds)
            throws DataAccessException {
        return observationDao.getObservations(datetimeId, stationIds);
    }

    /**
     * Returns a Map{@code <Integer,Observation>} mapped by stationId for a single datetime for all stations; maps
     * stationId to Observation.
     * @param datetimeId The time of observation
     * @return A Map{@code <Integer,Observation>} for the datetime for all stations
     * @throws DataAccessException (unchecked)
     */
    public Map<Integer, Observation> getObservations(int datetimeId) throws DataAccessException {
        return observationDao.getObservations(datetimeId);
    }

    /**
     * Returns a Map{@code <Integer,Observation>} mapped by datetimeId for a datetime range for a single stations; maps
     * stationId to Observation. The implementation class may throttle the amount of data available for retrieval.
     * @param startDatetimeId The beginning observation time
     * @param endDatetimeId The end observation time
     * @return A Map{@code <Integer,Observation>} for the datetime range for this station
     * @throws DataAccessException (unchecked)
     */
    public Map<Integer, Observation> getObservations(int startDatetimeId, int endDatetimeId, int stationId)
            throws DataAccessException {
        int diffs = endDatetimeId - startDatetimeId;
        Preconditions.checkArgument(diffs >= 0, "end datetime before begin datetime");
        Preconditions.checkArgument(diffs <= MAX_OB_HOURS, "too many hours requested (max " + MAX_OB_HOURS + " ["
                + MAX_OB_HOURS / 24 + " days])");
        return observationDao.getObservations(startDatetimeId, endDatetimeId, stationId);
    }

    /**
     * Returns a Map{@code <Integer,Observation>} of most recent Observations for all stations; maps stationId to
     * Observation.
     * @return A Map{@code <Integer,Observation>} of the most recent Observations for all stations
     * @throws DataAccessException (unchecked)
     */
    public Map<Integer, Observation> getCurrentObservations() throws DataAccessException {
        return observationDao.getCurrentObservations();
    }

    /**
     * Returns a Map{@code <Integer,Observation>} for current for certain stations; maps stationId to Observation.
     * @param stationIds the stations to retrieve
     * @return A Map{@code <Integer,Observation>} for the current observation for certain stations
     * @throws DataAccessException (unchecked)
     */
    public Map<Integer, Observation> getCurrentObservations(Collection<Integer> stationIds) throws DataAccessException {
        return observationDao.getCurrentObservations(stationIds);
    }

    /**
     * Returns the most recent Observation for a station
     * @param stationId the station to retrieve
     * @return the most recent Observation for a station
     * @throws DataAccessException (unchecked)
     */
    public Observation getCurrentObservation(int stationId) throws DataAccessException {
        return observationDao.getCurrentObservation(stationId);
    }

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
     * is a guarantee of the presence of calcluated precip or temp in the observation
     */
    public Integer getLastNonmissingCalculatedDatetimeIdBefore(int datetimeId, int stationId)
            throws DataAccessException {
        return observationDao.getLastNonmissingCalculatedDatetimeIdBefore(datetimeId, stationId);
    }

    /**
     * Returns a datetimeId prior to the datetimeId argument where an observation is available or null if one can not be
     * found
     * @param datetimeId Only datetimes prior to this one will be considered
     * @param stationId The station to consider
     * @return datetimeId prior to the datetimeId argument where an observation is available or null if one can not be
     * found
     * @throws DataAccessException (unchecked)
     */
    public Integer getLastNonmissingDatetimeIdBefore(int datetimeId, Integer stationId) throws DataAccessException {
        return observationDao.getLastNonmissingDatetimeIdBefore(datetimeId, stationId);
    }

    /**
     * Returns a Map{@code <Integer,Observation>} of most recent Observations for a network or networks; maps stationId
     * to Observation.
     * @return A Map{@code <Integer,Observation>} of the most recent Observations for a network or networks
     * @throws DataAccessException (unchecked)
     */
    public Map<Integer, Observation> getCurrentObservationsForNetworks(int... networkIds) throws DataAccessException {
        return observationDao.getCurrentObservationsForNetworks(networkIds);
    }

    /**
     * Returns the subhourly frequency (4 or 12) associated with a particular observation
     * @param stationId The station transmitting the observation
     * @param datetimeId The time of the observation
     * @return subhourly frequency
     * @throws DataAccessException (unchecked)
     */
    public Integer getStepsPerHour(int datetimeId, int stationId) throws DataAccessException {
        return observationDao.getStepsPerHour(datetimeId, stationId);
    }

    /**
     * Add calculated facts to the database for existing Observations and update the Observations' last mod dates.
     * Throws an exception if the insert is an observed value (not calculated). TODO consider deprecation in favor of
     * addCalculatedValuesToObs(Collection<ElementValue>); Observation no longer needed for streamId
     * @param updates maps each observation slated for update with its set of calculated element additions
     */
    @Transactional
    public void addCalculatedValuesToObs(Map<Observation, Collection<ElementValue>> updates) {
        for (Map.Entry<Observation, Collection<ElementValue>> entry : updates.entrySet()) {
            Observation ob = entry.getKey();
            LOG.debug("Adding calculated values to " + ob);
            Collection<ElementValue> values = entry.getValue();
            // if any elementvalue is in elIds, it is not a calculated value;
            // throw exception
            boolean notvalid = containsObservedElements(values);
            if (notvalid) {
                throw new IllegalArgumentException("Inserts must be calculated values.");
            }
            elementDao.insertElementValues(values);
        }
    }

    /**
     * Add calculated facts to the database for existing Observations and update the Observations' last mod dates.
     * Throws an exception if any fact is an observed value (not calculated).
     * @param values calculated facts slated to be added to existing Observations
     */
    @Transactional
    public void addCalculatedValuesToObs(Collection<ElementValue> values) {
        Preconditions.checkArgument(!containsObservedElements(values), "Inserts must be calculated values.");
        elementDao.insertElementValues(values);
    }

    /**
     * Returns true if any ElementValue is of an observed (i.e. not calculated) Element
     * @param values The ElementValues to check
     * @return true if any Element is not calculated
     */
    private boolean containsObservedElements(Collection<ElementValue> values) {
        // return ! Iterables.all(values,IS_CALCULATED);
        return Iterables.any(values, Predicates.not(IS_CALCULATED));
    }

    /**
     * Predicate which returns true when ElementValue is a calculated Element NOTE This is duplicated in
     * ObservationDaoImpl; need to figure out where this lives
     */
    public Predicate<ElementValue> IS_CALCULATED = new Predicate<ElementValue>() {
        @Override
        public boolean apply(ElementValue value) {
            return elementDao.getElements().get(value.getElementId()).isCalculated();
        }
    };

    /**
     * Delete specified calculated values from existing observations for a single station and update the observations'
     * last mod dates. Does nothing if the requested element(s) to delete are observed values (members of the stream)
     * @param stationId the id of the station whose facts and flags will be deleted if null, all stations' values will
     * be deleted based on the other params
     * @param beginDatetimeId the beginning datetime of the range of facts and flags to be deleted
     * @param endDatetimeId the end datetime of the range of facts and flags to be deleted
     * @param elementIds the ids of the calculated elements to be deleted; if the ids are not associated with calculated
     * values, they will be ignored
     * @throws DataAccessException
     */
    @Transactional
    public int deleteCalculatedValuesFromObs(Integer stationId, int beginDatetimeId, int endDatetimeId,
            int... elementIds) throws DataAccessException {
        return observationDao.deleteCalculatedValues(stationId, beginDatetimeId, endDatetimeId, elementIds);
    }

    /**
     * Retrieves from the database a Map{@code <Integer,ElementValue>} of values for a single station/date mapped by
     * elementId.
     * @param datetimeId the datetimeId to retrieve the values for
     * @param stationId the station to retrieve the values for
     * @return a Map{@code <Integer,ElementValue>} of values mapped by elementId
     * @throws DataAccessException (unchecked)
     */
    public Map<Integer, ElementValue> getElementValues(int datetimeId, int stationId) throws DataAccessException {
        return elementDao.getElementValues(datetimeId, stationId);
    }

    /**
     * Get the ElementValue stored for the given station, datetime, and element.
     * @param stationId The stationId.
     * @param datetimeId The internal datetimeId.
     * @param elementId The elementId.
     * @return The element value corresponding to the given inputs.
     */
    public ElementValue getElementValue(int stationId, int datetimeId, int elementId) throws DataAccessException {
        return elementDao.getElementValue(new StationDateElement(stationId, datetimeId, elementId));
    }

    /**
     * Retrieves ElementValues for a station/date if the ids are in elementIds.
     * @param datetimeId the datetimeId to retrieve the values for
     * @param stationId the station to retrieve the values for
     * @param elementIds the elements to retrieve values for
     * @return a Collection{@code <ElementValue>} of values
     * @throws DataAccessException (unchecked)
     */
    public Collection<ElementValue> getElementValues(int datetimeId, int stationId, Collection<Integer> elementIds)
            throws DataAccessException {
        Map<String, Object> params = new HashMap<>(3);
        params.put("stationId", stationId);
        params.put("datetimeId", datetimeId);
        params.put("elementIds", elementIds);
        return elementDao.getElementValues(params).values();
    }

    /**
     * Retrieves ElementValues for a station/date range if the ids are in elementIds.
     * @param datetimeId the datetimeId to retrieve the values for
     * @param stationId the station to retrieve the values for
     * @param elementIds the elements to retrieve values for
     * @return a Map{@code <Integer,ElementValue>} of values mapped by elementId
     * @throws DataAccessException (unchecked)
     */
    public Map<StationDateElement, ElementValue> getElementValues(int beginDatetimeId, int endDatetimeId,
            int stationId, Collection<Integer> elementIds) throws DataAccessException {
        Map<String, Object> params = new HashMap<>(4);
        params.put("stationId", stationId);
        params.put("begin", beginDatetimeId);
        params.put("end", endDatetimeId);
        params.put("elementIds", elementIds);
        return elementDao.getElementValues(params);
    }
}
