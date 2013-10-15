package gov.noaa.ncdc.crn.domain.sort;

import gov.noaa.ncdc.crn.domain.CrnDomains;
import gov.noaa.ncdc.crn.domain.POR;
import gov.noaa.ncdc.crn.domain.Station;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
/**
 * Comparator for sorting List<Station> and List<Integer> of stationIds
 * Sample usage where stationDao is an instance of an implementation of
 * StationDao:
 * <pre>
 * <code>
        Map{@code <Integer,Station>} stationMap = stationDao.getStations();
        List{@code <Station>} stations = new ArrayList<>();
        stations.addAll(stationMap.values());
        // sorts by station name (state,location,vector) using
        //     Station.compareTo(Station);
        Collections.sort(stations);
        // sorts all stations by goesid
        Collections.sort(stations,
            new StationComparator<Station>(StationComparator.SORTS.GOES_ID));
        List{@code <Integer>} stationIds = new ArrayList<>();
        stationIds.addAll(stationMap.keySet());
        // sorts stationIds first by networkId and then by stationId
        Collections.sort(stationIds,
            new StationComparator{@code <Integer>}(stationMap,
                StationComparator.SORTS.NETWORK_ID,
                StationComparator.SORTS.STATION_ID));
    </code>
    </pre>
 * @author Andrea Fey
 * @param <T> supports Station or Integer
 */
@SuppressWarnings("serial")
public class StationComparator<T> implements Comparator<T>, Serializable {

    /** Maps stationId to Station. Used to sort a List{@code <Integer>} of stationIds
     * in any order but stationId. */
    private final Map<Integer,Station> stationMap;
    /** Maps stationId to POR. Used to sort Stations by period of record. */
    private final Map<Integer,POR> porMap;
    /** List<SORTS> of sorts to be applied by Comparator. Sorts are applied in
     * List order, e.g. first by networkId, then by goesId. */
    private final List<SORTS> sorts;

    /** Supported sorting strategies */
    public enum SORTS {
        STATION_ID, GOES_ID, NAME, NETWORK_ID, GOES_SATELLITE, CLOSED, POR,
        CLOSED_DATE, TEST
    }
    /**
     * Constructor which uses no stationMap lookup. Can be used for
     * StationComparator<Station> construction or StationComparator<Integer>
     * when comparing stationIds
     * @param sorts - each member indicates sort order; not nullable
     */
    public StationComparator(SORTS... sorts) {
        Preconditions.checkArgument(sorts.length>0, "No sort order provided");
        this.sorts = Arrays.asList(sorts);
        this.porMap = null;
        this.stationMap = null;
    }
    /**
     * Constructor which uses stationMap lookup. Can be used for
     * StationComparator<Integer> construction where the Integer is assumed to
     * be a stationId referencing a particular Station. All SORTS except POR
     * are supported with this constructor.  When parameters are station ids,
     * compares Stations retrieved from station map.
     * @param stationMap non-null Map<Integer,Station> lookup for Stations
     * by stationId
     * @param sorts indicates sort order; sorts applied in argument order
     */
    public StationComparator(Map<Integer,Station> stationMap, SORTS... sorts) {
        this.stationMap = stationMap;
        this.porMap = null;
        Preconditions.checkArgument(sorts.length>0, "No sort order provided");
        this.sorts = Arrays.asList(sorts);
    }
    /**
     * Constructor which uses stationMap and porMap lookup. Can be used for
     * StationComparator<Integer> construction where the Integer is assumed to
     * be a stationId referencing a particular Station. All SORTS are supported
     * with this constructor.  When parameters are station ids, compares
     * Stations retrieved from station map.
     * @param stationMap non-null Map<Integer,Station> lookup for Stations
     * by stationId
     * @param sorts indicates sort order; sorts applied in argument order
     */
    public StationComparator(Map<Integer,Station> stationMap,
            Map<Integer,POR> porMap, SORTS... sorts) {
        Preconditions.checkNotNull(porMap, "porMap can not be null");
        Preconditions.checkArgument(sorts.length>0, "No sort order provided");
        this.sorts = Arrays.asList(sorts);
        this.stationMap = stationMap;
        this.porMap = porMap;
    }
    /**
     * Constructor which uses porMap lookup. Can be used for
     * StationComparator<Station> construction. All SORTS are supported
     * with this constructor.
     * @param pors non-null Collection<POR>
     * by stationId
     * @param sorts indicates sort order; sorts applied in argument order
     */
    public StationComparator(Collection<POR> pors, SORTS... sorts) {
        Preconditions.checkNotNull(pors, "pors can not be null");
        Preconditions.checkArgument(sorts.length>0, "No sort order provided");
        this.sorts = Arrays.asList(sorts);
        this.stationMap = null;
        this.porMap = Maps.uniqueIndex(pors, CrnDomains.STATION_ID);
    }

    @Override
    public int compare(T a, T b) {
        if (a==b) return 0;
        int compare = 0;
        for (SORTS sort : sorts) {
            compare = compare(a,b,sort);
            if (compare!=0) {
                return compare;
            }
        }
        return 0;
    }
    /**
     * Returns true if both Objects refer to the same Station. When parameters
     * are station ids, compares Stations retrieved from station map.
     * @param a - the first object to be compared.
     * @param b - the second object to be compared.
     * @return true if both Objects refer to the same Station
     * @throws ClassCastException - if the arguments' types prevent them from
     * being compared
     */
    public boolean equals(T a, T b) {
        return compare(a, b)==0;
    }
    /**
     * Compares its first two arguments for order based on the sorting strategy
     * indicated by its third argument. Returns a negative integer, zero, or a
     * positive integer as the first argument is less than, equal to, or
     * greater than the second based on the sorting strategy.  When parameters
     * are station ids, compares Stations retrieved from station map.
     * @param a - the first object to be compared.
     * @param b - the second object to be compared.
     * @param sort - the sorting strategy to use for comparison
     * @return a negative integer, zero, or a positive integer as the first
     * argument is less than, equal to, or greater than the second.
     * @throws ClassCastException - if the arguments' types prevent them from
     * being compared
     * @see compare(T a, T b)
     */
    private int compare(T a, T b, SORTS sort) {
        if (a.getClass().isAssignableFrom(Integer.class)) {
            if (sort==SORTS.STATION_ID) {
                // no need to assume the stationMap is available if we're just
                // sorting by stationId
                return ComparisonChain.start()
                        .compare((Integer)a, (Integer)b)
                        .result();
            } else if (sort==SORTS.POR) {
                return compareByPOR(porMap.get(a),
                        porMap.get(b));
            } else {
                return compare(stationMap.get(a), stationMap.get(b), sort);
            }
        } else if (sort==SORTS.POR) {
            return compareByPOR(porMap.get(((Station)a).getStationId()),
                    porMap.get(((Station)b).getStationId()));
        } else {
            return compare((Station)a,(Station)b, sort);
        }
    }
    /**
     * Compares its first two argument Stations for order based on the sorting
     * strategy indicated by its third argument. Returns a negative integer,
     * zero, or a positive integer as the first Station is less than, equal to,
     * or greater than the second based on the sorting strategy.
     * @param a - the first Station to be compared.
     * @param b - the second Station to be compared.
     * @param sort - the sorting strategy to use for comparison
     * @return a negative integer, zero, or a positive integer as the first
     * argument is less than, equal to, or greater than the second based on the
     * sorting strategy.
     * @throws ClassCastException - if the arguments' types prevent them from
     * being compared
     */
    public static int compare(Station a, Station b, SORTS sort) {
        if (a==null && b==null) { return 0; }
        else if (a==null) { return 1; }
        else if (b==null) { return -1; }

        switch (sort) {
        case STATION_ID:
            return compareByStationId(a, b);
        case NAME:
            return compareByName(a, b);
        case GOES_ID:
            return compareByGoesId(a, b);
        case NETWORK_ID:
            return compareByNetworkId(a, b);
        case GOES_SATELLITE:
            return compareByGoesSatellite(a, b);
        case CLOSED:
            return compareByClosed(a, b);
        case TEST:
            return compareByTest(a, b);
        case POR:
            throw new InvalidParameterException(
                    "Can't use the static compare method to compare PORs");
        case CLOSED_DATE:
            return compareByClosedDate(a, b);
            // could get here if sort==null
        default:
            return 0;
        }
    }
    /**
     * Compares its two Station arguments for order based on stationIds. Returns
     * a negative integer, zero, or a positive integer as the first Station's
     * stationId is less than, equal to, or greater than the second's.
     * @param a - the first Station to be compared.
     * @param b - the second Station to be compared.
     * @return a negative integer, zero, or a positive integer as the first
     * Station's stationId is less than, equal to, or greater than the second's.
     */
    private static int compareByStationId(Station a, Station b) {
        return a.getStationId()-b.getStationId();
    }
    /**
     * Compares its two Station arguments for order based on GOES ids. Returns
     * a negative integer, zero, or a positive integer as the first Station's
     * GOES id is less than, equal to, or greater than the second's.
     * @param a - the first Station to be compared.
     * @param b - the second Station to be compared.
     * @return a negative integer, zero, or a positive integer as the first
     * Station's GOES id is less than, equal to, or greater than the second's.
     */
    private static int compareByGoesId(Station a, Station b) {
        return ComparisonChain.start()
                .compare(a.getGoesId(), b.getGoesId())
                .compare(a.getClosedDate(), b.getClosedDate(),
                        Ordering.natural().nullsLast())
                        .result();
    }
    /**
     * Compares its two Station arguments for order based on closed date. Returns
     * a negative integer, zero, or a positive integer as the first Station's
     * closed date is less than, equal to, or greater than the second's. Null
     * closed dates (indicating a Station is still active) fall last.
     * @param a - the first Station to be compared.
     * @param b - the second Station to be compared.
     * @return a negative integer, zero, or a positive integer as the first
     * Station's closed date is less than, equal to, or greater than the
     * second's. Null closed dates (indicating a Station is still active) fall
     * last.
     */
    private static int compareByClosedDate(Station a, Station b) {
        return ComparisonChain.start()
                .compare(a.getClosedDate(), b.getClosedDate(),
                        Ordering.natural().nullsLast())
                        .result();
    }
    /**
     * Compares its two Station arguments for order based on stationIds. Returns
     * a negative integer, zero, or a positive integer as the first Station's
     * networkId is less than, equal to, or greater than the second's.
     * @param a - the first Station to be compared.
     * @param b - the second Station to be compared.
     * @return a negative integer, zero, or a positive integer as the first
     * Station's networkId is less than, equal to, or greater than the second's.
     */
    private static int compareByNetworkId(Station a, Station b) {
        return a.getNetworkId()-b.getNetworkId();
    }
    /**
     * Compares its two Station arguments for order based on name. Returns a
     * negative integer, zero, or a positive integer as the first Station's name
     * precedes, is equal to, or follows the second's alphanumerically.
     * @param a - the first Station to be compared.
     * @param b - the second Station to be compared.
     * @return a negative integer, zero, or a positive integer as the first
     * Station's name is alphanumerically less than, equal to, or greater than
     * the second's.
     */
    private static int compareByName(Station a, Station b) {
        return ComparisonChain.start()
                .compare(a.getNameString(), b.getNameString())
                .result();
    }
    /**
     * Compares its two Station arguments for order based on GOES satellite
     * assignment. Returns a negative integer, zero, or a positive integer as
     * the first Station's stationId is less than, equal to, or
     * greater than the second's.
     * @param a - the first Station to be compared.
     * @param b - the second Station to be compared.
     * @return a negative integer, zero, or a positive integer as the first
     * Station's  GOES satellite assignment is alphanumerically less than,
     * equal to, or greater than the second's.
     */
    private static int compareByGoesSatellite(Station a, Station b) {
        return ComparisonChain.start()
                .compare(a.getGoesSat(), b.getGoesSat())
                .result();
    }
    /**
     * Compares its two Station arguments for order based on whether the station
     * is closed or abandoned. Abandoned stations are filtered to the bottom of
     * the list, and closed stations are just above that.
     * @param a - the first Station to be compared.
     * @param b - the second Station to be compared.
     * @return a negative integer, zero, or a positive integer based on whether
     * the first Station's operational status is higher priority (not closed or
     * abandoned) than the second's
     */
    private static int compareByClosed(Station a, Station b) {
        char aChar = a.getOpStatus().toCharArray()[0];
        char bChar = b.getOpStatus().toCharArray()[0];
        /* Station's ordered operational statuses
         * <ul><li>Y (Operational)</li>
         * <li>N (Non-operational)</li>
         * <li>C (Closed)</li>
         * <li>A (Abandoned)</li></ul>
         * Note it is coincidental that status priority is inversely related
         * to its int value
         */
        int aInt = getStatusInt(aChar);
        int bInt = getStatusInt(bChar);
        return bInt - aInt;

    }
    /**
     * Compares its two Station arguments for order based on whether or not the
     * station is a test site. Test stations are filtered to the bottom of
     * the list.
     * @param a - the first Station to be compared.
     * @param b - the second Station to be compared.
     * @return a negative integer, zero, or a positive integer based on whether
     * the first Station's test site only status is higher priority (not a test)
     * than the second's
     */
    private static int compareByTest(Station a, Station b) {
        boolean aTest = a.getTestSiteOnly()==null?Boolean.FALSE:a.getTestSiteOnly();
        boolean bTest = b.getTestSiteOnly()==null?Boolean.FALSE:b.getTestSiteOnly();
        return ComparisonChain.start()
                .compareFalseFirst(aTest, bTest)
                .result();
    }
    /**
     * Compares its two Station arguments for order based on the beginning of
     * the stations' periods of record. May only be used when the porMap
     * constructor is used.
     * @param aPor - the POR for the first Station to be compared.
     * @param bPor - the POR for the second Station to be compared.
     * @return a negative integer, zero, or a positive integer based on the
     * stations' periods of record
     */
    private static int compareByPOR(POR aPor, POR bPor) {
        return aPor.getStartDatetime()-bPor.getStartDatetime();
    }
    private static int getStatusInt(char x) {
        switch (x) {
        case 'C':
            return -1;
        case 'A':
            return -2;
        default:
            return 0;
        }
    }
}
