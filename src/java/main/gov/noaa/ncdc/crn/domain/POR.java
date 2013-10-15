package gov.noaa.ncdc.crn.domain;

import java.io.Serializable;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

/**
 * Information about the period of record a station has transmitted data in the CRN network.
 * @author Andrea Fey
 */
@SuppressWarnings("serial")
public class POR implements StationAware, Comparable<Object>, Serializable {

    /** Unique station identifier */
    private final int stationId;
    /** Datetime id of the beginning of the period of record. */
    private final int startDatetime;
    /** Datetime id of the end of the period of record. */
    private final int endDatetime;

    public POR(final int stationId, final int startDatetime, final int endDatetime) {
        this.stationId = stationId;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
    }

    @Override
    public int getStationId() {
        return stationId;
    }

    public int getStartDatetime() {
        return startDatetime;
    }

    public int getEndDatetime() {
        return endDatetime;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(stationId).addValue(startDatetime).addValue(endDatetime)
                .toString();
    }

    @Override
    public int compareTo(Object o) {
        if (o != null && o instanceof POR) {
            POR p = (POR) o;
            return ComparisonChain.start().compare(stationId, p.stationId).compare(startDatetime, p.startDatetime)
                    .compare(endDatetime, p.endDatetime).result();
        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return this.compareTo(o) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(stationId, startDatetime, endDatetime);
    }

}
