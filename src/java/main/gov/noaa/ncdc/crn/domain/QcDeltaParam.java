package gov.noaa.ncdc.crn.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

@SuppressWarnings("serial")
public class QcDeltaParam implements Comparable<Object>, Serializable {

    private final Integer streamId;
    private final Integer stationId;
    private final Integer month;
    private final int[] elementIds = new int[3];
    private final BigDecimal delta;
    // this is the maximum expected precision past the decimal point in the database
    private static final int SCALE = 1;

    public QcDeltaParam(final Integer streamId, final Integer stationId, final Integer month, final int elementId1,
            final int elementId2, final int elementId3, final BigDecimal delta) {
        this.streamId = streamId;
        this.stationId = stationId;
        this.month = month;
        this.elementIds[0] = elementId1;
        this.elementIds[1] = elementId2;
        this.elementIds[2] = elementId3;
        this.delta = delta != null ? delta.setScale(SCALE) : null;
    }

    public int[] getElementIds() {
        return elementIds;
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

    public BigDecimal getDelta() {
        return delta;
    }

    /*
     * Orders by elementId[0], streamId, stationId, month
     */
    @Override
    public int compareTo(Object o) {
        if (this == o) {
            return 0;
        }
        if (o != null && o instanceof QcDeltaParam) {
            QcDeltaParam qcd = (QcDeltaParam) o;
            return ComparisonChain.start().compare(elementIds[0], qcd.elementIds[0])
                    .compare(streamId, qcd.streamId, Ordering.natural().nullsLast())
                    .compare(stationId, qcd.stationId, Ordering.natural().nullsLast())
                    .compare(month, qcd.month, Ordering.natural().nullsLast()).result();
        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && o instanceof QcDeltaParam) {
            QcDeltaParam qcd = (QcDeltaParam) o;
            return ComparisonChain.start().compare(this, qcd).compare(elementIds[1], qcd.elementIds[1])
                    .compare(elementIds[2], qcd.elementIds[2]).compare(delta, qcd.delta).result() == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(streamId, stationId, month, elementIds, delta);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("streamId", streamId).add("stationId", stationId).add("month", month)
                .add("elementIds", elementIds).add("delta", delta).toString();
    }

}
