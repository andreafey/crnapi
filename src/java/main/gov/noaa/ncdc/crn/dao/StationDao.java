package gov.noaa.ncdc.crn.dao;

import gov.noaa.ncdc.crn.domain.Station;

import java.util.Map;

import org.springframework.dao.DataAccessException;

public interface StationDao {
    /**
     * Retrieves a Map<Integer,Station> of all stations with PORs mapped by stationId
     * @return Map<Integer,Station> of all stations with PORs mapped by stationId
     * @throws DataAccessException (unchecked)
     */
    public abstract Map<Integer, Station> getStations() throws DataAccessException;

    /**
     * Retrieves a Map<Integer,Station> of all stations, including those without PORs, mapped by stationId. Generally
     * should not be used unless retrieving stations which have not yet transmitted data is desirable.
     * @param includeSilent true if should include stations which have not yet transmitted data
     * @return Map<Integer,Station> of all stations in the database mapped by stationId
     * @throws DataAccessException (unchecked)
     */
    public abstract Map<Integer, Station> getStations(boolean includeSilent);

    /**
     * Retrieves a Map<String,Station> of all stations with PORs mapped by wban number
     * @return Map<String,Station> of all stations mapped by wban number
     * @throws DataAccessException (unchecked)
     */
    public abstract Map<String, Station> getWbanStationMap() throws DataAccessException;

    /**
     * Retrieves a Station from a parameter map. Parameters supported are stationId, id (can be wban, coop, or goes),
     * and state/location/vector. A unique station is expected to be found, and an exception will be thrown if multiple
     * stations are retrieved.
     * <ul>
     * List of possible params, note that Lists can also be provided as arrays
     * <li>"stationId" Integer stationId</li>
     * <li>"id" String coop, wban, or goes id</li>
     * <li>"state" String 2-letter state abbreviation</li>
     * <li>"location" String city name, e.g. Los Cruces</li>
     * <li>"vector" String length+directional, e.g. "8 SW"</li>
     * </ul>
     * @param params Map<String, Object> of parameters the query should meet
     * @return Station which meets these parameters or null if nothing retrieved
     * @throws DataAccessException (unchecked)
     */
    public abstract Station getStationFromParams(Map<String, Object> params) throws DataAccessException;

    /**
     * Retrieves a Map<Integer,Station> of stations with PORs from a parameter map. Parameters supported are
     * stationId(s), networkId(s), id (can be wban, coop, or goes), state(s) (must be 2-letter abbr.), visible (true). A
     * single stationId may be passed as "stationId" or a list of stationIds may be passed as "stationIds", but not
     * both; the same holds true for networkId and state. Multiple parameters may otherwise be passed (such as a list of
     * states and visible=true), and stations which meet all parameters will be returned.
     * <ul>
     * List of possible params, note that Lists can also be provided as arrays
     * <li>"stationId" Integer stationId</li>
     * <li>"stationIds" Collection<Integer> of stationIds</li>
     * <li>"id" String coop, wban, or goes id</li>
     * <li>"ids" Collection<String> of coop, wban, or goes ids</li>
     * <li>"networkId" Integer networkId</li>
     * <li>"networkIds" Collection<Integer> of networkIds</li>
     * <li>"network" String [crnonly|usrcrnonly]</li>
     * <li>"crnreps" Boolean true to include representative CRN stations in USRCRN network; must be used in conjunction
     * with network=usrcrnonly</li>
     * <li>"states" Collection<String> 2-letter state abbreviations</li>
     * <li>"state" String 2-letter state abbreviation</li>
     * <li>"location" String city name, e.g. Los Cruces</li>
     * <li>"vector" String length+directional, e.g. "8 SW"</li>
     * <li>"nontestStations" Boolean true to exclude test stations (removing from use in favor of "visible")</li>
     * <li>"visible" Boolean true to exclude test and abandoned stations</li>
     * <li>"commissioned" Boolean true to include only stations are currently commmissioned</li>
     * <li>"commdate" String yyyymmdd only stations which were commmissioned on a certain date</li>
     * <li>"closed" Boolean true to include only stations currently with a commcode of "closed"</li>
     * <li>"porstart" String yyyymmdd only stations which have PORs beginning after a certain date</li>
     * <li>"includeSilent" Boolean determines whether or not stations without a POR will be included</li>
     * </ul>
     * @param params Map<String, Object> of parameters the query should meet
     * @return Map<Integer,Station> of stations which meet these parameters
     * @throws DataAccessException (unchecked)
     */
    public Map<Integer, Station> getStationsFromParams(Map<String, Object> params) throws DataAccessException;

    /**
     * Retrieves the Station with this stationId, or null if none is found. Permits the retrieval of a station with no
     * POR.
     * @param stationId The stationId of a station in the database
     * @return Station with this stationId
     * @throws DataAccessException (unchecked)
     */
    public abstract Station getStation(int stationId) throws DataAccessException;

    /**
     * Retrieves a Map<Integer, Station> of stations mapped stationId which currently measure SM/ST. Permits the
     * retrieval of stations with no POR.
     * @return a Map of stations which currently measure SM/ST
     * @throws DataAccessException (unchecked)
     */
    public abstract Map<Integer, Station> getStationsCurrentlyWithSmSt() throws DataAccessException;

    /**
     * Retrieves the last time an observation was modified for a station between two datetimes.
     * @param stationId The id of the station
     * @param begin The earliest datetime to consider
     * @param end The last datetime to consider
     * @return A String representing the last modification time in YYYYMMDDHH24MI
     * @throws DataAccessException (unchecked)
     */
    public abstract String getLastModForStation(int stationId, int begin, int end) throws DataAccessException;

    /**
     * Retrieves the capacity of the Geonor rain gauge, in mm, for a station.
     * @param stationId The stationId of the station
     * @return the capacity of the Geonor rain gauge, in mm, for the station
     */
    public abstract Float getGeonorCapacity(int stationId) throws DataAccessException;

    /**
     * Retrieves the rain gauge depth for a station on a certain datetime
     * @param stationId of the station
     * @param datetimeId of the datetime
     * @return the depth in mm of the station's rain gauge at that time
     * @throws DataAccessException (unchecked)
     */
    public abstract Integer getStationRainGaugeDepth(int stationId, int datetimeId);
}
