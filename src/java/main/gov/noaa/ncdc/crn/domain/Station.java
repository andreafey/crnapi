package gov.noaa.ncdc.crn.domain;

import java.io.Serializable;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

/**
 * The {@code Station} represents a CRN station and provides access to its most common metadata.
 * @author Andrea Fey
 */
@SuppressWarnings("serial")
public class Station implements StationAware, Comparable<Station>, Serializable {

    /** Unique station name - state location vector, e.g NC Asheville 13 S */
    private final StationName name;
    /** Unique station identifier */
    private final int stationId;
    /** GOES DCS platform identifier */
    private String goesId;
    /** WBAN number identifier */
    private String wbanno;
    /** Station's longitude from metadata */
    private String longitude;
    /** Station's latitude from metadata */
    private String latitude;
    /** Station's elevation in feet; nullable */
    private Integer elevation;
    /**
     * Station's commission status; one of
     * <ul>
     * <li>Y (Commissioned)</li>
     * <li>N (Non-commissioned, but intended to be commissioned eventually)</li>
     * <li>E (Experimental)</li>
     * <li>T (Test Site)</li>
     * </ul>
     */
    private String commCode;
    /** Station's commission date in YYYYMMDD format; UN if station uncommissioned */
    private String commDate;
    /** Hours offset from UTC time */
    private int offset;
    /**
     * Station's operational status; one of
     * <ul>
     * <li>Y (Operational)</li>
     * <li>N (Non-operational)</li>
     * <li>C (Closed)</li>
     * <li>A (Abandoned)</li>
     */
    private String opStatus;
    /** stationId of closely-located pair station, if available; otherwise -1 */
    private int pairStationId;
    /** Satellite station transmits to, either E or W */
    private String goesSat;
    /** networkId of station's primary network */
    private int networkId;
    /** Station's official designated name */
    private String longName;
    /** true if is a test site and the data should be considered unreliable; otherwise false */
    private Boolean testSiteOnly;
    /** ATDD identifier */
    private String atddno;
    /** Date a station was closed or abandoned; null if never */
    private String closedDate;
    /** true if CRN station is a grid point for RCRN network */
    private Boolean pseudoRcrn;

    public Station(int stationId, String state, String location, String vector) {
        this.stationId = stationId;
        name = new StationName(state, location, vector);
    }

    public Station(int stationId, String state, String location, String vector, int networkId, String longName,
            String goesId, String wbanno, String atddno, int offset, String longitude, String latitude,
            Integer elevation, String commCode, String commDate, String opStatus, int pairStationId,
            Boolean testSiteOnly, String goesSat, String closedDate, Boolean pseudoRcrn) {
        this.stationId = stationId;
        name = new StationName(state, location, vector);
        this.networkId = networkId;
        this.longName = longName;
        this.goesId = goesId;
        this.wbanno = wbanno;
        this.atddno = atddno;
        this.offset = offset;
        this.longitude = longitude;
        this.latitude = latitude;
        this.elevation = elevation;
        this.commCode = commCode;
        this.commDate = commDate;
        this.opStatus = opStatus;
        this.pairStationId = pairStationId;
        this.testSiteOnly = testSiteOnly;
        this.goesSat = goesSat;
        this.closedDate = closedDate;
        this.pseudoRcrn = pseudoRcrn;
    }

    /**
     * returns true if station is closed or abandoned
     * @return true if station is closed or abandoned
     */
    public boolean isClosed() {
        return "C".equals(opStatus) || "A".equals(opStatus);
    }

    @Override
    public int getStationId() {
        return stationId;
    }

    public String getGoesId() {
        return goesId;
    }

    public String getWbanno() {
        return wbanno;
    }

    public StationName getName() {
        return name;
    }

    public String getNameString() {
        return name.toString();
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public Integer getElevation() {
        return elevation;
    }

    /**
     * Station's commission status; one of
     * <ul>
     * <li>Y (Commissioned)</li>
     * <li>N (Non-commissioned, but intended to be commissioned eventually)</li>
     * <li>E (Experimental)</li>
     * <li>T (Test Site)</li>
     * </ul>
     */
    public String getCommCode() {
        return commCode;
    }

    public String getCommDate() {
        return commDate;
    }

    public int getOffset() {
        return offset;
    }

    /**
     * Station's operational status; one of
     * <ul>
     * <li>Y (Operational)</li>
     * <li>N (Non-operational)</li>
     * <li>C (Closed)</li>
     * <li>A (Abandoned)</li>
     * </ul>
     */
    public String getOpStatus() {
        return opStatus;
    }

    public int getPairStationId() {
        return pairStationId;
    }

    public String getGoesSat() {
        return goesSat;
    }

    public int getNetworkId() {
        return networkId;
    }

    public String getLongName() {
        return longName;
    }

    public Boolean getTestSiteOnly() {
        return testSiteOnly;
    }

    public String getAtddno() {
        return atddno;
    }

    public String getClosedDate() {
        return closedDate;
    }

    public Boolean isPseudoRcrn() {
        return pseudoRcrn;
    }

    @Override
    public int compareTo(Station station) {
        if (station != null) {
            String thisname = null;
            String stationname = null;
            try {
                thisname = this.getNameString();
            } catch (NullPointerException npe) {
                // thisname is null if getNameString() problematic
            }
            try {
                stationname = station.getNameString();
            } catch (NullPointerException npe) {
                // stationname stays null if getNameString() problematic
            }
            return ComparisonChain.start().compare(thisname, stationname, Ordering.natural().nullsLast()).result();
        } else {
            // null station follows this station (not null)
            return -1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && o instanceof Station) {
            Station s = (Station) o;
            return ComparisonChain.start().compare(stationId, s.stationId).compare(this, s)
                    .compare(goesId, s.goesId, Ordering.natural().nullsLast())
                    .compare(wbanno, s.wbanno, Ordering.natural().nullsLast())
                    .compare(longitude, s.longitude, Ordering.natural().nullsLast())
                    .compare(latitude, s.latitude, Ordering.natural().nullsLast())
                    .compare(elevation, s.elevation, Ordering.natural().nullsLast())
                    .compare(commCode, s.commCode, Ordering.natural().nullsLast())
                    .compare(commDate, s.commDate, Ordering.natural().nullsLast()).compare(offset, s.offset)
                    .compare(opStatus, s.opStatus, Ordering.natural().nullsLast())
                    .compare(pairStationId, s.pairStationId)
                    .compare(goesSat, s.goesSat, Ordering.natural().nullsLast()).compare(networkId, s.networkId)
                    .compare(longName, s.longName, Ordering.natural().nullsLast())
                    .compare(testSiteOnly, s.testSiteOnly, Ordering.natural().nullsLast())
                    .compare(atddno, s.atddno, Ordering.natural().nullsLast())
                    .compare(closedDate, s.closedDate, Ordering.natural().nullsLast())
                    // .compare(pseudoRcrn,s.isPseudoRcrn())
                    .result() == 0;
        }
        return false;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("stationId", stationId).add("name", getNameString())
                .add("goesId", goesId).add("wbanno", wbanno).add("commcode", getCommCode()).add("opstat", opStatus)
                .addValue(testSiteOnly != null && testSiteOnly ? ",TEST SITE" : "").toString();
    }

    @Override
    public int hashCode() {
        return stationId;
    }

}
