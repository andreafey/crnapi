package gov.noaa.ncdc.crn.domain;

import java.io.Serializable;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

/**
 * {@code CrnExceptionFact}s represent data which have {@code CrnException}s associated with them.
 * @author Andrea.Fey
 */
@SuppressWarnings("serial")
public class CrnExceptionFact implements StationAware, DatetimeAware, ElementAware, Comparable<CrnExceptionFact>,
Serializable {
    /** The station identifier. */
    private final int stationId;
    /** The id of the observation time. */
    private final int datetimeId;
    /** The element identifier. */
    private final int elementId;
    /** internal id of exception */
    private final Integer exceptionId;

    // Collection<ExceptionResolutionFact> resolutionFacts;
    public CrnExceptionFact(int stationId, int datetimeId, int elementId, Integer exceptionId) {
        this.stationId = stationId;
        this.datetimeId = datetimeId;
        this.elementId = elementId;
        this.exceptionId = exceptionId;
    }

    public CrnExceptionFact(int stationId, int datetimeId, int elementId) {
        this(stationId, datetimeId, elementId, null);
    }

    @Override
    public int getStationId() {
        return stationId;
    }

    @Override
    public int getDatetimeId() {
        return datetimeId;
    }

    @Override
    public int getElementId() {
        return elementId;
    }

    public Integer getExceptionId() {
        return exceptionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o.getClass().isAssignableFrom(this.getClass())) {
            CrnExceptionFact exf = (CrnExceptionFact) o;
            return Objects.equal(exceptionId, exf.exceptionId) && Objects.equal(stationId, exf.stationId)
                    && Objects.equal(datetimeId, exf.datetimeId) && Objects.equal(elementId, exf.elementId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(exceptionId, stationId, datetimeId, elementId);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(exceptionId).addValue(stationId).addValue(datetimeId)
                .addValue(elementId).toString();
    }

    @Override
    public int compareTo(CrnExceptionFact ef) {
        if (this == ef) {
            return 0;
        }
        if (ef != null) {
            return ComparisonChain.start().compare(stationId, ef.stationId).compare(datetimeId, ef.datetimeId)
                    .compare(elementId, ef.elementId).compare(exceptionId, ef.exceptionId).result();
        }
        return -1;
    }
}
