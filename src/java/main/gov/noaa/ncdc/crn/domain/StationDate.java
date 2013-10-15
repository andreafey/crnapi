package gov.noaa.ncdc.crn.domain;

import java.io.Serializable;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

/**
 * {@code StationDate} is a simple combination of station id with datetime id. It exists because it serves as the
 * natural key to a {@code Map<StationDate,Observation>}.
 * @author egg.davis
 */

@SuppressWarnings("serial")
public class StationDate implements StationAware, DatetimeAware, Comparable<Object>, Serializable {

    private final int stationId;
    private final int datetimeId;

    public StationDate(final int stationId, final int datetimeId) {
        this.stationId = stationId;
        this.datetimeId = datetimeId;
    }

    @Override
    public int getStationId() {
        return stationId;
    }

    @Override
    public int getDatetimeId() {
        return datetimeId;
    }

    public StationDate preceding() {
        return new StationDate(stationId, datetimeId - 1);
    }

    public StationDate succeeding() {
        return new StationDate(stationId, datetimeId + 1);
    }

    @Override
    public boolean equals(Object o) {
        return this.compareTo(o) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(stationId, datetimeId);
    }

    @Override
    public int compareTo(Object o) {
        if (o != null && o instanceof StationDate) {
            StationDate sd = (StationDate) o;
            return ComparisonChain.start().compare(stationId, sd.stationId).compare(datetimeId, sd.datetimeId).result();
        }
        return -1;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(stationId).addValue(datetimeId).toString();
    }
}
