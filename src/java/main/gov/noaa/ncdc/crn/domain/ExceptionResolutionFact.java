package gov.noaa.ncdc.crn.domain;

import java.io.Serializable;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

@SuppressWarnings("serial")
public class ExceptionResolutionFact implements StationAware, DatetimeAware, ElementAware,
        Comparable<ExceptionResolutionFact>, Serializable {
    private final int stationId;
    private final int datetimeId;
    private final int elementId;
    // may be unknown prior to insert
    private Integer resolutionId;
    // can be a number or a formula
    final private String value;

    public ExceptionResolutionFact(final int stationId, final int datetimeId, final int elementId,
            Integer resolutionId, final String value) {
        this.stationId = stationId;
        this.datetimeId = datetimeId;
        this.elementId = elementId;
        this.resolutionId = resolutionId;
        this.value = value;
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

    public int getResolutionId() {
        return resolutionId;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int compareTo(ExceptionResolutionFact erFact) {
        if (erFact != null) {
            return ComparisonChain.start().compare(stationId, erFact.stationId).compare(datetimeId, erFact.datetimeId)
                    .compare(elementId, erFact.elementId).compare(resolutionId, erFact.resolutionId)
                    .compare(value, erFact.value).result();

        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        // true if these refer to the same object
        if (this == o) {
            return true;
        }
        if (o != null && o instanceof ExceptionResolutionFact) {
            ExceptionResolutionFact e = (ExceptionResolutionFact) o;
            return this.compareTo(e) == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(stationId, datetimeId, elementId, resolutionId, value);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(stationId).addValue(datetimeId).addValue(elementId)
                .add("resolutionId", resolutionId).add("value", value).toString();
    }

}
