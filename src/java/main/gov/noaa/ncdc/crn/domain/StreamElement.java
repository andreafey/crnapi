package gov.noaa.ncdc.crn.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

@SuppressWarnings("serial")
public class StreamElement implements ElementAware, Comparable<Object>, Serializable {

    private final int streamId;
    private final BigDecimal multiplier;
    private final Integer overrideDecimalPlaces;
    private final Integer overridePublishedDecimalPlaces;
    private final Element element;

    public StreamElement(Element element, int streamId, String multiplier, Integer decimalPlaces,
            Integer publishedDecimalPlaces) {
        this.element = element;
        this.streamId = streamId;
        this.multiplier = new BigDecimal(multiplier);
        this.overrideDecimalPlaces = decimalPlaces;
        this.overridePublishedDecimalPlaces = publishedDecimalPlaces;
    }

    public int getStreamId() {
        return streamId;
    }

    public BigDecimal getMultiplier() {
        return multiplier;
    }

    public Integer getDecimalPlaces() {
        return overrideDecimalPlaces != null && overrideDecimalPlaces != -1 ? overrideDecimalPlaces : element
                .getDefaultDecimalPlaces();
    }

    public Integer getPublishedDecimalPlaces() {
        return overridePublishedDecimalPlaces != null && overridePublishedDecimalPlaces != -1 ? overridePublishedDecimalPlaces
                : element.getDefaultPublishedDecimalPlaces();
    }

    /**
     * Return the {@code Element}'s elementId
     * @return the {@code Element}'s elementId
     */
    @Override
    public int getElementId() {
        return element.getElementId();
    }

    /**
     * Return the name
     * @return the name
     */
    public String getName() {
        return element.getName();
    }

    /**
     * Return the description
     * @return the description
     */
    public String getDescription() {
        return element.getDescription();
    }

    @Override
    public int compareTo(Object o) {
        if (o != null && o instanceof StreamElement) {
            StreamElement se = (StreamElement) o;
            return ComparisonChain.start().compare(this.streamId, se.streamId).compare(this.element, se.element)
                    .result();
        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && o instanceof StreamElement) {
            StreamElement se = (StreamElement) o;
            return ComparisonChain
                    .start()
                    .compare(this, se)
                    .compare(multiplier, se.multiplier)
                    .compare(overrideDecimalPlaces, se.overrideDecimalPlaces, Ordering.natural().nullsLast())
                    .compare(overridePublishedDecimalPlaces, se.overridePublishedDecimalPlaces,
                            Ordering.natural().nullsLast()).result() == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(streamId, element);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("streamId", streamId).addValue(element)
                .add("store", getDecimalPlaces()).add("publish", getPublishedDecimalPlaces())
                .add("multiplier", getMultiplier()).toString();
    }
}
