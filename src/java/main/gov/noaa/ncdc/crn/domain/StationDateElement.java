package gov.noaa.ncdc.crn.domain;

import java.io.Serializable;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;

@SuppressWarnings("serial")
public class StationDateElement implements StationAware, DatetimeAware, ElementAware, Comparable<Object>, Serializable {

    /** The station identifier. */
    private int stationId;
    /** The id of the observation time. */
    private int datetimeId;
    /** The element identifier. */
    private int elementId;

    public StationDateElement(final int stationId, final int datetimeId, final int elementId) {
        this.stationId = stationId;
        this.datetimeId = datetimeId;
        this.elementId = elementId;
    }

    public StationDateElement(StationDate stationDate, int elementId) {
        Preconditions.checkNotNull("stationDate not nullable", stationDate);
        this.stationId = stationDate.getStationId();
        this.datetimeId = stationDate.getDatetimeId();
        this.elementId = elementId;
    }

    public StationDate getStationDate() {
        return new StationDate(stationId, datetimeId);
    }

    public int getStationId() {
        return stationId;
    }

    public int getDatetimeId() {
        return datetimeId;
    }

    public int getElementId() {
        return elementId;
    }

    @Override
    public boolean equals(Object o) {
        return this.compareTo(o) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(stationId, datetimeId, elementId);
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof StationDateElement) {
            StationDateElement sde = (StationDateElement) o;
            return ComparisonChain.start().compare(getStationDate(), sde.getStationDate())
                    .compare(elementId, sde.elementId).result();
        }
        return -1;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(stationId).addValue(datetimeId).addValue(elementId).toString();
    }

}
