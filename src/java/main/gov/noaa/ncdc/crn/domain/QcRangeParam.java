package gov.noaa.ncdc.crn.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

@SuppressWarnings("serial")
public class QcRangeParam implements Comparable<QcRangeParam>, Serializable {
    private final int elementId;
    private final Integer streamId;
    private final Integer stationId;
    private final Integer month;
    private final BigDecimal lo;
    private final BigDecimal hi;
    // this is the maximum expected precision past the decimal point in the database
    private static final int SCALE = 1;

    public QcRangeParam(final int elementId, final Integer streamId, final Integer stationId, final Integer month,
            final BigDecimal lo, final BigDecimal hi) {
        this.elementId = elementId;
        this.streamId = streamId;
        this.stationId = stationId;
        this.month = month;
        this.lo = lo != null ? lo.setScale(SCALE) : null;
        this.hi = hi != null ? hi.setScale(SCALE) : null;
    }

    public int getElementId() {
        return elementId;
    }

    public Integer getStreamId() {
        return streamId;
    }

    public Integer getStationId() {
        return stationId;
    }

    public Integer getMonth() {
        return month;
    }

    public BigDecimal getLo() {
        return lo;
    }

    public BigDecimal getHi() {
        return hi;
    }

    /* Orders by elementId (required), streamId, stationId, month */
    @Override
    public int compareTo(QcRangeParam qcr) {
        if (this == qcr) {
            return 0;
        }
        return ComparisonChain.start().compare(elementId, qcr.elementId)
                .compare(streamId, qcr.streamId, Ordering.natural().nullsLast())
                .compare(stationId, qcr.stationId, Ordering.natural().nullsLast())
                .compare(month, qcr.month, Ordering.natural().nullsLast()).result();
    }

    /* Checks all fields for equality */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof QcRangeParam) {
            QcRangeParam qcr = (QcRangeParam) o;
            return ComparisonChain.start().compare(this, qcr, Ordering.natural().nullsLast()).compare(lo, qcr.lo)
                    .compare(hi, qcr.hi).result() == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(elementId, streamId, stationId, month, lo, hi);
    }

    @Override
    public String toString() {
        String range = String.format("%+04.2f:%+04.2f", lo == null ? new Float(-9999.99) : lo, hi == null ? new Float(
                -9999.99) : hi);
        return Objects.toStringHelper(this).addValue(elementId).addValue(streamId).addValue(stationId)
                .add("range", range).toString();
    }

}
