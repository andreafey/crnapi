package gov.noaa.ncdc.crn.dao.mybatis;

import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Collections2.filter;
import gov.noaa.ncdc.crn.dao.ElementDao;
import gov.noaa.ncdc.crn.dao.ObservationDao;
import gov.noaa.ncdc.crn.domain.ElementValue;
import gov.noaa.ncdc.crn.domain.ElementValues;
import gov.noaa.ncdc.crn.domain.Observation;
import gov.noaa.ncdc.crn.domain.ObservationWithData;
import gov.noaa.ncdc.crn.domain.StationDate;
import gov.noaa.ncdc.crn.persistence.ElementMapper;
import gov.noaa.ncdc.crn.persistence.ObservationMapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Repository
public class ObservationDaoImpl implements ObservationDao {
    @Autowired
    private ObservationMapper mapper;
    @Autowired
    private ElementDao elementDao;
    @Autowired
    private ElementMapper elementMapper;
    @Autowired
    private SqlSession sqlSession;
    private static int THROTTLE = 100;

    @Override
    public Observation getObservation(final int datetimeId, final int stationId) throws DataAccessException {
        return mapper.selectObservation(datetimeId, stationId);
    }

    @Override
    public Map<Integer, Observation> getObservations(final int datetimeId, final Collection<Integer> stationIds)
            throws DataAccessException {
        return mapper.selectObservations(datetimeId, stationIds);
    }

    @Override
    public Map<StationDate, Observation> getObservations(final int startId, final int endId,
            final Collection<Integer> stationIds) throws DataAccessException {
        return mapper.selectObservations(startId, endId, stationIds);
    }

    @Override
    public Map<Integer, Observation> getObservations(final int datetimeId) {
        return mapper.selectObservations(datetimeId);
    }

    @Override
    public Map<Integer, Observation> getObservations(final int startDatetimeId, final int endDatetimeId,
            final int stationId) throws DataAccessException {
        return mapper.selectObservations(startDatetimeId, endDatetimeId, stationId);
    }

    @Override
    public Map<Integer, Observation> getCurrentObservations() throws DataAccessException {
        return mapper.selectCurrentObservations();
    }

    @Override
    public Map<Integer, Observation> getCurrentObservations(final Collection<Integer> stationIds)
            throws DataAccessException {
        return Maps.filterKeys(getCurrentObservations(), Predicates.in(stationIds));
    }

    @Override
    public Observation getCurrentObservation(final int stationId) throws DataAccessException {
        return getCurrentObservations().get(stationId);
    }

    @Override
    public Map<Integer, Observation> getCurrentObservationsForNetworks(final int... networkIds)
            throws DataAccessException {
        return mapper.selectCurrentObservations(networkIds);
    }

    @Override
    public Integer getLastNonmissingCalculatedDatetimeIdBefore(final int datetimeId, final int stationId)
            throws DataAccessException {
        return mapper.selectLastNonmissingCalculatedDatetimeIdBefore(datetimeId, stationId);
    }

    @Override
    public Integer getLastNonmissingDatetimeIdBefore(final int datetimeId, final Integer stationId)
            throws DataAccessException {
        return mapper.selectLastNonmissingDatetimeIdBefore(datetimeId, stationId);
    }

    @Override
    public Integer getStepsPerHour(final int datetimeId, final int stationId) throws DataAccessException {
        return mapper.selectStepsPerHour(datetimeId, stationId);
    }

    @Override
    public int getDateOfFirstSoilMoistureObservation(final int stationId) throws DataAccessException {
        return mapper.selectDateOfFirstSoilMoistureObservation(stationId);
    }

    @Override
    public void updateTimeExportedToIsd(final int datetimeId, final int stationId) throws DataAccessException {
        mapper.updateTimeExportedToIsd(datetimeId, stationId);
    }

    @Override
    public void deleteObservation(final int datetimeId, final int stationId) throws DataAccessException {
        mapper.deleteObLoadlog(stationId, datetimeId);
        elementMapper.deleteFlags(stationId, datetimeId);
        elementMapper.deleteFacts(stationId, datetimeId);
        mapper.deleteObservation(stationId, datetimeId);
    }

    @Override
    public void deleteObservations(final Collection<Observation> observations) {
        /* break into partitions to avoid max cursors when collection is large */
        Iterable<List<Observation>> partitions = Iterables.partition(observations, THROTTLE);
        for (List<Observation> partition : partitions) {
            /*
             * separating delete statements to prevent new cursor from being retrieved during batch processing
             */
            for (Observation ob : partition) {
                mapper.deleteObLoadlog(ob.getStationId(), ob.getDatetimeId());
            }
            for (Observation ob : partition) {
                elementMapper.deleteFlags(ob.getStationId(), ob.getDatetimeId());
            }
            for (Observation ob : partition) {
                elementMapper.deleteFacts(ob.getStationId(), ob.getDatetimeId());
            }
            for (Observation ob : partition) {
                mapper.deleteObservation(ob.getStationId(), ob.getDatetimeId());
            }
        }
    }

    @Override
    public void deleteObservations(final int stationId, final int beginDatetimeId, final int endDatetimeId)
            throws DataAccessException {
        /*
         * very slow when try to delete a large range; call recursively with a subset
         */
        if (endDatetimeId - beginDatetimeId > THROTTLE) {
            deleteObservations(stationId, beginDatetimeId, beginDatetimeId + THROTTLE - 1);
            deleteObservations(stationId, beginDatetimeId + THROTTLE, endDatetimeId);
        } else {
            mapper.deleteObLoadlogs(stationId, beginDatetimeId, endDatetimeId);
            elementMapper.deleteFlags(stationId, beginDatetimeId, endDatetimeId);
            elementMapper.deleteFacts(stationId, beginDatetimeId, endDatetimeId);
            mapper.deleteObservations(stationId, beginDatetimeId, endDatetimeId);
        }
    }

    // Note that the return value is nonsensical when mybatis in BATCH mode
    @Override
    public int deleteCalculatedValues(final Integer stationId, final int beginDatetimeId, final int endDatetimeId,
            final int... elementIds) throws DataAccessException {
        int factCount = 0;
        if (endDatetimeId - beginDatetimeId > THROTTLE) {
            factCount += deleteCalculatedValues(stationId, beginDatetimeId, beginDatetimeId + THROTTLE - 1, elementIds);
            factCount += deleteCalculatedValues(stationId, beginDatetimeId + THROTTLE, endDatetimeId, elementIds);
        } else {
            elementMapper.deleteCalculatedFlags(stationId, beginDatetimeId, endDatetimeId, elementIds);
            factCount += elementMapper.deleteCalculatedFacts(stationId, beginDatetimeId, endDatetimeId, elementIds);
        }
        return factCount;
    }

    @Override
    public void deleteCalculatedValues(final Collection<ElementValue> values) throws DataAccessException {
        /* break into partitions to avoid max cursors when collection is large */
        Iterable<List<ElementValue>> partitions = Iterables.partition(values, THROTTLE);
        for (List<ElementValue> partition : partitions) {
            for (ElementValue value : partition) {
                elementMapper.deleteCalculatedFlag(value.getStationId(), value.getDatetimeId(), value.getElementId());
            }
            for (ElementValue value : partition) {
                elementMapper.deleteCalculatedFact(value.getStationId(), value.getDatetimeId(), value.getElementId());
            }
            sqlSession.flushStatements();
        }
    }

    @Override
    public void updateElementValues(final Collection<ElementValue> values) {
        Collection<ElementValue> deletes = filterDeletes(values);
        Collection<ElementValue> updates = filterUpdates(values);
        /*
         * NOTE: if any ElementValues in deletes are not calculated values, they will not be deleted.
         */
        deleteCalculatedValues(deletes);

        /* break into partitions to avoid max cursors when collection is large */
        Iterable<List<ElementValue>> partitions = Iterables.partition(updates, THROTTLE);
        List<ElementValue> flagValues = Lists.newArrayList();
        for (List<ElementValue> partition : partitions) {
            // update facts
            for (ElementValue value : partition) {
                elementMapper.updateFact(value);
            }
            // update flags
            for (ElementValue value : partition) {
                elementMapper.updateFlag(value);
            }
            // trigger modification time trigger; updates TABLE1 timestamp
            sqlSession.flushStatements();
            flagValues.clear();
        }
    }

    /**
     * Returns a subcollection of ElementValues whose members are calculated and their value() method returns null.
     * @param unfiltered The Collection to filter
     * @return members of the Collection whose value() method returns null
     */
    private Collection<ElementValue> filterDeletes(final Collection<ElementValue> unfiltered) {
        return filter(unfiltered, Predicates.and(ElementValues.NULL_VALUE, IS_CALCULATED));
    }

    /**
     * Returns a subcollection of ElementValues whose members' value() method returns a non-null value.
     * @param unfiltered The Collection to filter
     * @return members of the Collection whose value() method returns a non-null value
     */
    private Collection<ElementValue> filterUpdates(final Collection<ElementValue> unfiltered) {
        return filter(unfiltered, not(ElementValues.NULL_VALUE));
    }

    /**
     * Predicate which returns true when ElementValue is a calculated Element NOTE for now this depends on having access
     * to the elementDao, which is why it's inconveniently located here instead of elsewhere; this is also duplicated in
     * ObservationService
     */
    private Predicate<ElementValue> IS_CALCULATED = new Predicate<ElementValue>() {
        @Override
        public boolean apply(ElementValue value) {
            return elementDao.getElements().get(value.getElementId()).isCalculated();
        }
    };

    @Override
    public void insertObservations(Collection<ObservationWithData> observations) throws DataAccessException {
        /* break into partitions to avoid max cursors when collection is large */
        Iterable<List<ObservationWithData>> partitions = Iterables.partition(observations, THROTTLE);
        for (List<ObservationWithData> partition : partitions) {
            /*
             * separating table inserts to prevent new cursor from being retrieved during batch processing
             */
            for (ObservationWithData ob : partition) {
                mapper.insertObservation(ob.getObservation());
            }
            for (ObservationWithData ob : partition) {
                mapper.insertObLoadlog(ob.getObservation());
            }
            for (ObservationWithData ob : partition) {
                elementDao.insertElementValues(ob.getElementValues().values());
            }
            sqlSession.flushStatements();
        }
    }

    @Override
    public void insertObservation(ObservationWithData ob) throws DataAccessException {
        mapper.insertObservation(ob.getObservation());
        mapper.insertObLoadlog(ob.getObservation());
        elementDao.insertElementValues(ob.getElementValues().values());
    }

    @Override
    public void updateTimeExportedToIsd(Collection<Observation> observations) throws DataAccessException {
        /* break into partitions to avoid max cursors when collection is large */
        Iterable<List<Observation>> partitions = Iterables.partition(observations, THROTTLE);
        for (List<Observation> partition : partitions) {
            for (Observation observation : partition) {
                mapper.updateTimeExportedToIsd(observation.getDatetimeId(), observation.getStationId());
            }
            sqlSession.flushStatements();
        }
    }

}
