package gov.noaa.ncdc.crn.domain;

import java.io.Serializable;
import java.sql.Date;

import com.google.common.collect.ComparisonChain;

@SuppressWarnings("serial")
public class StationRainGauge implements Comparable<StationRainGauge>, Serializable {
    private int depth;
    private int stationId;
    private Date eventDate;
    // ordinal is only used to break a sort tie when stationid and eventdate are equal
    private int ordinal;

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    @Override
    public int compareTo(StationRainGauge gauge) {
        return ComparisonChain.start().compare(stationId, gauge.stationId).compare(eventDate, gauge.eventDate)
                .compare(ordinal, gauge.ordinal).result();
    }

}
