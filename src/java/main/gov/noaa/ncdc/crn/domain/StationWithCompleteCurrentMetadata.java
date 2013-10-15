package gov.noaa.ncdc.crn.domain;

import gov.noaa.ncdc.crn.util.TimeUtils;

import java.sql.Date;
import java.util.Calendar;

/**
 * The {@code Station} represents a CRN station and provides access to its most complete current relevant metadata.
 * @author Andrea Fey
 */
@SuppressWarnings("serial")
public class StationWithCompleteCurrentMetadata extends Station {

    /** Date of the first metadata event in ISIS */
    private Date active;
    /** Date of the last metadata event in ISIS */
    private Date lastEvent;
    /**
     * correlates with offset, but provides more specific information, particularly for Indiana stations
     */
    private String timezone;
    /** country */
    private String country;
    /** serial number of the RH sensor */
    private String rhSerialNo;
    /** max depth of the rain gauge */
    private Integer raingaugeDepth;
    /** government property id */
    private String governmentPropertyId;

    // /** time in the hour the station transmits */
    // private Integer transmitTime;

    public StationWithCompleteCurrentMetadata(int stationId, String state, String location, String vector,
            int networkId, String longName, String goesId, String wbanno, String atddno, int offset, String longitude,
            String latitude, Integer elevation, String commCode, String commDate, String opStatus, int pairStationId,
            Boolean testSiteOnly, String goesSat, String closedDate, Boolean pseudoRcrn) {
        super(stationId, state, location, vector, networkId, longName, goesId, wbanno, atddno, offset, longitude,
                latitude, elevation, commCode, commDate, opStatus, pairStationId, testSiteOnly, goesSat, closedDate,
                pseudoRcrn);
    }

    public Date getActive() {
        return active;
    }

    public String getActiveString() {
        if (active == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(active);
        return TimeUtils.getYYYYMMDD(cal);
    }

    public void setActive(Date active) {
        this.active = active;
    }

    public Date getLastEvent() {
        return lastEvent;
    }

    public String getLastEventString() {
        if (lastEvent == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(lastEvent);
        return TimeUtils.getYYYYMMDD(cal);
    }

    public void setLastEvent(Date lastEvent) {
        this.lastEvent = lastEvent;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRhSerialNo() {
        return rhSerialNo;
    }

    public void setRhSerialNo(String rhSerialNo) {
        this.rhSerialNo = rhSerialNo;
    }

    public Integer getRaingaugeDepth() {
        return raingaugeDepth;
    }

    public void setRaingaugeDepth(Integer raingaugeDepth) {
        this.raingaugeDepth = raingaugeDepth;
    }

    public String getGovernmentPropertyId() {
        return governmentPropertyId;
    }

    public void setGovernmentPropertyId(String governmentPropertyId) {
        this.governmentPropertyId = governmentPropertyId;
    }

    @Override
    public String toString() {
        return "StationWithCompletCurrentMetadata [" + super.toString() + "]";
    }

}
