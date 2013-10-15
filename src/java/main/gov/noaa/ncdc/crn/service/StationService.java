package gov.noaa.ncdc.crn.service;

import gov.noaa.ncdc.crn.dao.PorDao;
import gov.noaa.ncdc.crn.dao.StationDao;
import gov.noaa.ncdc.crn.domain.POR;
import gov.noaa.ncdc.crn.domain.Station;
import gov.noaa.ncdc.crn.domain.sort.StationComparator;
import gov.noaa.ncdc.crn.domain.sort.StationComparator.SORTS;
import gov.noaa.ncdc.crn.util.TimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;

@Service
public class StationService {
    @Autowired
    private StationDao stationDao;
    @Autowired
    private PorDao porDao;
    private static final int GOES_OVERLAP_RANGE = 24 * 7; /* one week */
    private static Log LOG = LogFactory.getLog(StationService.class);

    /**
     * Retrieves a Map{@code <Integer,Station>} of all visible (non-test and non-abandoned) stations which are now
     * closed
     * @return Map{@code <Integer,Station>} of all closed visible stations in the database mapped by stationId
     * @throws DataAccessException (unchecked)
     */
    public Map<Integer, Station> getClosedStations() throws DataAccessException {
        Map<String, Object> params = new HashMap<>();
        params.put("closed", Boolean.TRUE);
        params.put("visible", Boolean.TRUE);
        return stationDao.getStationsFromParams(params);
    }

    /**
     * Retrieves a Map{@code <Integer,Station>} of all stations which are commissioned according to their network's
     * requirements
     * @return Map{@code <Integer,Station>} of all commissioned stations in the database mapped by stationId
     * @throws DataAccessException (unchecked)
     */
    public Map<Integer, Station> getCommissionedStations() throws DataAccessException {
        Map<String, Object> params = new HashMap<>();
        params.put("commissioned", true);
        return stationDao.getStationsFromParams(params);
    }

    /**
     * Retrieves a Map{@code <Integer,Station>} of stations with PORs in the parameter state(s) mapped by stationId
     * @param states 2-letter USPS uppercase abbreviation
     * @return Map{@code <Integer,Station>} of stations in these states
     * @throws DataAccessException (unchecked)
     */
    public Map<Integer, Station> getStationsFromStates(String... states) throws DataAccessException {
        for (String state : states) {
            Preconditions.checkNotNull(state);
            Preconditions.checkArgument(state.length() == 2);
        }
        Map<String, Object> params = new HashMap<>();
        params.put("states", states);
        return stationDao.getStationsFromParams(params);
    }

    /**
     * Retrieves a Map{@code <Integer,Station>} of stations with PORs for a list of networks
     * @param networkIds int[] of networkIds
     * @return Map{@code <Integer,Station>} of stations for these networks
     * @throws DataAccessException (unchecked)
     */
    public Map<Integer, Station> getStationsForNetworks(int... networkIds) throws DataAccessException {
        Map<String, Object> params = new HashMap<>();
        params.put("networkIds", networkIds);
        return stationDao.getStationsFromParams(params);
    }

    /**
     * Retrieves a Map{@code <Integer,Station>} of all stations with PORs mapped by stationId
     * @return Map{@code <Integer,Station>} of all stations in the database mapped by stationId
     * @throws DataAccessException (unchecked)
     */
    public Map<Integer, Station> getStations() throws DataAccessException {
        return stationDao.getStations();
    }

    /**
     * Retrieves a Map{@code <Integer,Station>} of stations with PORs in the list mapped by stationId
     * @param stationIds List{@code <Integer>} of stationIds to include
     * @return Map{@code <Integer,Station>} of stations with these stationIds
     * @throws DataAccessException (unchecked)
     */
    public Map<Integer, Station> getStations(Collection<Integer> stationIds) throws DataAccessException {
        Map<Integer, Station> all = getStations();
        return Maps.filterKeys(all, Predicates.in(stationIds));
    }

    /**
     * Retrieves a Map{@code <String,Station>} of all stations with PORs mapped by wban number
     * @return Map{@code <String,Station>} of all stations mapped by wban number
     * @throws DataAccessException (unchecked)
     */
    public Map<String, Station> getWbanStationMap() throws DataAccessException {
        return stationDao.getWbanStationMap();
    }

    /**
     * Retrieves a Map{@code <Integer,Station>} of all visible (non-test and non-abandoned) stations with PORs mapped by
     * stationId
     * @param stationIds List{@code <Integer>} of stationIds to include
     * @return Map{@code <Integer,Station>} visible stations in the database with the requested stationIds mapped by
     * stationId
     * @throws DataAccessException (unchecked)
     */
    public Map<Integer, Station> getVisibleStations(Collection<Integer> stationIds) throws DataAccessException {
        Map<Integer, Station> all = getVisibleStations();
        return Maps.filterKeys(all, Predicates.in(stationIds));
    }

    /**
     * Retrieves a Map{@code <Integer,Station>} of all visible (non-test and non-abandoned) stations with PORs mapped by
     * stationId
     * @return Map{@code <Integer,Station>} of all visible stations in the database mapped by stationId
     * @throws DataAccessException (unchecked)
     */
    public Map<Integer, Station> getVisibleStations() throws DataAccessException {
        Map<String, Object> params = new HashMap<>();
        params.put("visible", Boolean.TRUE);
        // don't include stations unless they have a POR
        params.put("includeSilent", Boolean.FALSE);
        return stationDao.getStationsFromParams(params);
    }

    /**
     * Retrieves a Map{@code <Integer,Station>} of all visible (non-test and non-abandoned) stations with PORs for the
     * networks requested mapped by stationId
     * @return Map{@code <Integer,Station>} of all visible stations in the database in the networks requested mapped by
     * stationId
     * @throws DataAccessException (unchecked)
     */
    public Map<Integer, Station> getVisibleStationsForNetworks(int... networkIds) throws DataAccessException {
        Map<String, Object> params = new HashMap<>();
        params.put("visible", Boolean.TRUE);
        params.put("includeSilent", Boolean.FALSE);
        if (networkIds.length > 1) {
            params.put("networkIds", networkIds);
        } else if (networkIds.length == 1) {
            params.put("networkId", networkIds[0]);
        }
        return stationDao.getStationsFromParams(params);
    }

    /**
     * Retrieves the Station with this stationId, or null if none is found. Permits the retrieval of a station with no
     * POR.
     * @param stationId The stationId of a station in the database
     * @return Station with this stationId
     * @throws DataAccessException (unchecked)
     */
    public Station getStation(int stationId) throws DataAccessException {
        return stationDao.getStation(stationId);
    }

    /**
     * Retrieves a station from a id or WBAN number. Permits the retrieval of a station with no POR.
     * @param id The identifier of the station
     * @return a Station fitting the identifier
     * @throws DataAccessException (unchecked)
     */
    public Station stationFromIdentifier(String id) throws DataAccessException {
        if (id.matches("\\d{4}")) {
            return getStation(Integer.valueOf(id));
        } else {
            Map<String, Object> params = new HashMap<>();
            params.put("id", id);
            return stationDao.getStationFromParams(params);
        }
    }

    /**
     * Retrieves a station from ATDD number. Permits the retrieval of a station with no POR. NOTE: Can't uniquely add atddno
     * to stationFromIdentifier method, because there is overlap between stationId and atddno.
     * @param atddno The atddno of the station
     * @return a Station with this atddno
     * @throws DataAccessException (unchecked)
     */
    public Station stationFromAtddno(String atddno) throws DataAccessException {
        Preconditions.checkNotNull(atddno);
        Preconditions.checkArgument(Integer.valueOf(atddno) > 0);

        Map<String, Object> params = new HashMap<>();
        params.put("atddno", atddno);
        return stationDao.getStationFromParams(params);
    }

    /**
     * Retrieves a station from goesId. If only one station uses a given goesId (as is usually the case) return that
     * station. If more than one station share the same goesid, use the datetimeId to choose the station based on the
     * closed date; choose the last station in the list which was not closed prior to the the datetimeId (plus a week's
     * slop.)
     * <p>
     * For clarity:
     * <ul>
     * <li>The station's closed date, if it exists, indicates the date a station's operational status changed to closed
     * or abandoned according to its ISIS event date.
     * <li>If the datetimeId precedes the closed date of the first station, or even the beginning of its POR, choose the
     * first station.
     * <li>If the first station closed April 6, 2008, and the datetimeId falls on April 12, 2008, choose the first
     * station because the datetimeId falls within the slop range.
     * <li>If the next station opened July 2008, and the requested datetimeId falls in June 2008, choose it anyway.
     * <li>Whether or not the last station in the list is closed or abandoned, if the datetimeId is later than the
     * preceding station's closed/abandoned date (plus slop), choose the last station.
     * </ul>
     * 
     * <pre>
     * Station to select
     * a a a a a a a a a a a b b b b b b b b b b c c c c c c c c c c c c c c c c c c c c c c c c c c
     *        |    a     |            |  b   |              |                 c               |  now
     * </pre>
     * 
     * POR for each station sharing same GOES_ID Note that the POR can't be used to determine a station's operational
     * status for a certain time because records can be processed out of order. This is particularly true during
     * reprocessing, because later stations will not yet have accurate periods of record. Additionally, if two stations
     * are in the database with the same goesId but neither is closed, the order CAN NOT BE GUARANTEED.
     * @param goesId The goesId of the station
     * @param datetimeId The day under consideration.
     * @return a Station with this goesId which transmitted data on this day
     * @throws DataAccessException (unchecked)
     */
    public Station stationFromGoesId(String goesId, int datetimeId) throws DataAccessException {
        Preconditions.checkNotNull(goesId);

        ListMultimap<String, Station> goesMap = getGoesStationMap();
        List<Station> stations = goesMap.get(goesId);
        // remove this after syncfromisis stops stripping prefixes
        stations.addAll(goesMap.get("CD" + goesId));
        stations.addAll(goesMap.get("DA" + goesId));
        if (stations.size() == 0) {
            return null;
        } else if (stations.size() == 1) {
            return stations.get(0);
        } else {
            /*
             * sort stations with this goes id based on their closed date with null closed dates last
             */
            Collections.sort(stations, new StationComparator<Station>(SORTS.CLOSED_DATE));
            Iterator<Station> stationIt = stations.iterator();
            // already know more than 1 exists in list
            Station station = stationIt.next();
            // the requested calendar time plus slop for comparison
            Calendar requestDate = TimeUtils.computeCalendarDate(datetimeId);
            do {
                try {
                    Calendar closedDatePlus = TimeUtils.addHours(TimeUtils.createUTCCalendar(station.getClosedDate()),
                            GOES_OVERLAP_RANGE);
                    /*
                     * check whether date requested is in the current station's range
                     */
                    if (requestDate.before(closedDatePlus)) {
                        return station;
                    }
                } catch (NullPointerException npe) {
                    throw new RuntimeException("Multiple stations have the same" + " goesId (" + goesId
                            + ") but no closed date.");
                }
                station = stationIt.next();
            } while (stationIt.hasNext());
            // if datetimeId after last station's POR, return it
            return station;
        }
    }

    /**
     * Retrieves a ListMultimap{@code <String,Station>} mapping goesId to Station. Supports multiple Stations mapped to
     * the same goesId and stations which have not yet transmitted data.
     * @return ListMultimap{@code <String,Station>} mapping goesId to Station
     */
    public ListMultimap<String, Station> getGoesStationMap() {
        Map<Integer, Station> stationMap = stationDao.getStations(true);
        return transformStationMap(stationMap);
    }

    /**
     * Transforms a Map{@code <Integer,Station>} mapping stationId to station to a ListMultimap{@code <String,Station>}
     * mapping goesId to List{@code <Station>}
     * @param stationMap The map for the transformation
     * @return The transformed map
     */
    private ListMultimap<String, Station> transformStationMap(Map<Integer, Station> stationMap) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("station count before transform: " + stationMap.size());
            LOG.trace("1788: " + stationMap.get(1788));
            LOG.trace("1778: " + stationMap.get(1778));
            LOG.trace("1782: " + stationMap.get(1782));
            LOG.trace("7779: " + stationMap.get(7779));
        }
        ListMultimap<String, Station> mm = ArrayListMultimap.create(stationMap.size(), 1);
        for (Station station : stationMap.values()) {
            mm.put(station.getGoesId(), station);
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("station count after transform: " + mm.size());
        }
        return mm;
    }

    /**
     * Retrieves a List{@code <Station>} with PORs from an identifier list: ID, wbanno, goesid
     * @param ids The identifiers of the Stations
     * @throws DataAccessException (unchecked)
     */
    public List<Station> stationsFromIdentifiers(List<String> ids) throws DataAccessException {
        Preconditions.checkNotNull(ids);
        Preconditions.checkArgument(ids.size() > 0);

        List<Integer> stationIds = new ArrayList<>();
        List<String> otherIds = new ArrayList<>();

        Collection<String> filtered = Collections2.filter(ids, Predicates.notNull());
        for (String id : filtered) {
            if (id.matches("\\d{4}")) {
                stationIds.add(Integer.valueOf(id));
            } else {
                otherIds.add(id);
            }
        }
        Map<Integer, Station> stations = new HashMap<>();
        if (stationIds.size() > 0) {
            stations.putAll(getStations(stationIds));
        }
        if (otherIds.size() > 0) {
            Map<String, Object> params = new HashMap<>();
            params.put("ids", otherIds);
            stations.putAll(stationDao.getStationsFromParams(params));
        }
        List<Station> stationList = new ArrayList<>();
        stationList.addAll(stations.values());
        return stationList;
    }

    /**
     * Retrieves a station from its unique state-location-vector (eg "AL", "Gadsden", "19 N"). Permits the retrieval of
     * a station with no POR.
     * @param state eg "AL"
     * @param location eg "Gadsden"
     * @param vector eg "19 N"
     * @return the Station at state+location+vector
     * @throws DataAccessException (unchecked)
     */
    /*
     * TODO handle cases where more than one station have same slv (e.g. dual transmitters
     */
    public Station stationFromName(String state, String location, String vector) throws DataAccessException {
        Preconditions.checkNotNull(state);
        Preconditions.checkArgument(state.length() == 2);
        Preconditions.checkNotNull(location);
        Preconditions.checkNotNull(vector);
        Preconditions.checkArgument(isValidVector(vector));

        Map<String, Object> params = new HashMap<>();
        params.put("state", state);
        params.put("location", location);
        params.put("vector", vector);
        return stationDao.getStationFromParams(params);
    }

    /**
     * Checks that a vector can be valid, i.e. in the form at of 19 NNE
     * @param vector the vector to check
     * @return true if it passes validation
     */
    private static boolean isValidVector(String vector) {
        String[] components = vector.split(" ");
        Preconditions.checkArgument(components.length == 2);
        try {
            int miles = Integer.valueOf(components[0]);
            Preconditions.checkArgument(miles > 0 && miles < 100);
        } catch (NumberFormatException nfe) {
            return false;
        }
        Preconditions.checkArgument(components[1].matches("^(N|S|W|E){1,3}$"));
        return true;
    }

    /**
     * Retrieves the last time an observation was modified for a station between two datetimes.
     * @param stationId The id of the station
     * @param begin The earliest datetime to consider
     * @param end The last datetime to consider
     * @return The last modification time as a YYYYMMDDHH24MI String
     * @throws DataAccessException (unchecked)
     */
    public String getLastModForStation(int stationId, int begin, int end) throws DataAccessException {
        return stationDao.getLastModForStation(stationId, begin, end);
    }

    /**
     * Retrieves POR information for all stations in the database mapped by stationId
     * @return Map{@code <Integer,POR>} mapping each stationId to its POR
     * @throws DataAccessException (unchecked)
     */
    public Map<Integer, POR> getPor() {
        return porDao.getPor();
    }

    /**
     * Retrieves POR information for a single station in the database
     * @return POR for a single station
     * @throws DataAccessException (unchecked)
     */
    public POR getPor(int stationId) {
        return porDao.getPor(stationId);
    }

    /**
     * Retrieves the rain gauge depth for a station on a certain datetime
     * @param stationId of the station
     * @param datetimeId of the datetime
     * @return the depth in mm of the station's rain gauge at that time
     * @throws DataAccessException (unchecked)
     */
    public Integer getStationRainGaugeDepth(int stationId, int datetimeId) {
        return stationDao.getStationRainGaugeDepth(stationId, datetimeId);
    }

}
