package gov.noaa.ncdc.crn.persistence;

import gov.noaa.ncdc.crn.domain.Station;
import gov.noaa.ncdc.crn.domain.StationRainGauge;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

public interface StationMapper {

    /**
     * Retrieve a map of stations mapped by station id. includeSilent determines whether or not stations without a POR
     * will be included
     * @param includeSilent determines whether or not stations without a POR will be included
     * @return a map of stations mapped by station id
     */
    @MapKey("stationId")
    public abstract Map<Integer, Station> selectStations(@Param("includeSilent") final Boolean includeSilent);

    /**
     * Retrieve a Map{@code <String,Station>} mapped by wbanno. Includes stations without a POR
     * @return a Map{@code <String,Station>} mapped by wbanno
     */
    @MapKey("wbanno")
    public abstract Map<String, Station> selectWbanStations();

    /**
     * Retrieves a Station from a parameter map. Parameters supported are stationId, id (can be wban, coop, or goes),
     * and state/location/vector. A unique station is expected to be found, and an exception will be thrown if multiple
     * stations are retrieved. Station not required to have a POR.
     * <ul>
     * List of possible params, note that Lists can also be provided as arrays
     * <li>"stationId" Integer stationId</li>
     * <li>"id" String coop, wban, or goes id</li>
     * <li>"state" String 2-letter state abbreviation</li>
     * <li>"location" String city name, e.g. Los Cruces</li>
     * <li>"vector" String length+directional, e.g. "8 SW"</li>
     * </ul>
     * @param params Map{@code <String,Object>} of parameters the query should meet
     * @return Station which meets these parameters or null if nothing retrieved
     */
    public abstract Station selectStation(final Map<String, Object> params);

    /**
     * Retrieves a Station from a stationId. Station not required to have a POR.
     * @param params Map{@code <String,Object>} of parameters the query should meet
     * @return Station which meets these parameters or null if nothing retrieved
     */
    public abstract Station selectStation(@Param("stationId") final int stationId);

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
     * @param params Map{@code <String,Object>} of parameters the query should meet
     * @return Map{@code <Integer,Station>} of stations which meet these parameters
     */
    @MapKey("stationId")
    public abstract Map<Integer, Station> selectStations(final Map<String, Object> params);

    /**
     * Retrieves a Map{@code <Integer,Station>} mapped by stationId of Stations which currently measure SM/ST. Permits
     * the retrieval of stations with no POR.
     * @return a Map{@code <Integer,Station>} which currently measure SM/ST
     */
    @MapKey("stationId")
    public abstract Map<Integer, Station> selectStationsCurrentlyWithSmSt();

    /**
     * Retrieves the last time an observation was modified for a station between two datetimes.
     * @param stationId The id of the station
     * @param begin The earliest datetime to consider
     * @param end The last datetime to consider
     * @return A String representing the last modification time in YYYYMMDDHH24MI
     */
    public abstract String selectLastModForStation(@Param("stationId") final int stationId,
            @Param("beginDatetimeId") final int begin, @Param("endDatetimeId") final int end);

    /**
     * Retrieves the current capacity of the Geonor rain gauge, in mm, for a station.
     * @param stationId The stationId of the station
     * @return the capacity of the Geonor rain gauge, in mm, for the station
     */
    public abstract BigDecimal selectGeonorCapacity(@Param("stationId") final int stationId);

    public abstract Collection<StationRainGauge> selectRainGaugeDepths();
}
