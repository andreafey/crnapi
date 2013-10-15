package gov.noaa.ncdc.crn.domain;

import gov.noaa.ncdc.crn.util.MathUtils;

import java.io.Serializable;
import java.math.BigDecimal;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

/**
 * This domain class contains all information about a single observed or calculated value, including information about
 * the observing station, the time of observation, the element and its observed or calculated value, any QC flags that
 * are set, and the number of decimal places stored and publishable.
 * @author Andrea Fey
 */
@SuppressWarnings("serial")
public class ElementValue implements DatetimeAware, StationAware, ElementAware, Comparable<Object>, Serializable {
    /** Contains the station identifier, the observation datetime, and the element id */
    private final StationDateElement staDateElement;
    /** The observed or calculated value; nullable if none available */
    private final BigDecimal value;
    /**
     * The number of stored decimal places for this element; nullable (meaning all transmitted decimal places are
     * stored)
     */
    private final Integer decimalPlaces;
    /** The number of decimal places to be publicly displayed; nullable (meaning all stored decimal places are displayed */
    private final Integer publishedDecimalPlaces;
    /** The QC flags that have been set */
    private final Flags flags;

    /**
     * Constructs a new {@link gov.noaa.ncdc.crn.domain.ElementValue ElementValue} from the parameters and sets all QC
     * flags to false.
     * 
     * NOTE: Only the numerical value of the {@code BigDecimal} "value" parameter is maintained. No information within
     * the {@code BigDecimal} object about scaling, precision, or rounding is preserved.
     * 
     * @param stationId The station identifier
     * @param datetimeId The datetime id of the observation time
     * @param elementId The id of the element
     * @param value The observed or calculated value of the element
     * @param decimalPlaces The number of decimal places retained in the database for this element
     * @param publishedDecimalPlaces The number of publishable decimal places for this element
     */
    public ElementValue(int stationId, int datetimeId, int elementId, BigDecimal value, Integer decimalPlaces,
            Integer publishedDecimalPlaces) {
        this(stationId, datetimeId, elementId, value, 0, decimalPlaces, publishedDecimalPlaces);
    }

    /**
     * Constructs a new {@link gov.noaa.ncdc.crn.domain.ElementValue ElementValue} from the parameters, including an
     * @{code Integer} representation of the QC flags.
     * 
     * NOTE: Only the numerical value of the {@code BigDecimal} "value" parameter is maintained. No information within
     * the {@code BigDecimal} object about scaling, precision, or rounding is preserved.
     * 
     * @param stationId The station identifier
     * @param datetimeId The datetime id of the observation time
     * @param elementId The id of the element
     * @param value The observed or calculated value of the element
     * @param flagInt {@code Integer} representation of the QC flags
     * @param decimalPlaces The number of decimal places retained in the database for this element
     * @param publishedDecimalPlaces The number of publishable decimal places for this element
     */
    public ElementValue(int stationId, int datetimeId, int elementId, BigDecimal value, Integer flagInt,
            Integer decimalPlaces, Integer publishedDecimalPlaces) {
        this.staDateElement = new StationDateElement(stationId, datetimeId, elementId);
        this.value = value==null ? null : new BigDecimal(value.toPlainString());
        this.decimalPlaces = decimalPlaces;
        this.publishedDecimalPlaces = publishedDecimalPlaces;
        this.flags = new Flags(flagInt);
    }

    /**
     * Returns the observed or calculated value of the element.
     * 
     * NOTE: scaling, precision, and rounding are not specified in the return object as these should always be handled
     * by {@link gov.noaa.ncdc.crn.util.MathUtils#round MathUtils.round}.
     * 
     * @return the observed or calculated value of the element
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Returns the observed or calculated value of the element rounded via
     * {@link gov.noaa.ncdc.crn.util.MathUtils#round(java.math.BigDecimal,int) MathUtils.round} to the number of
     * places appropriate for publication
     * @return the observed or calculated value of the element
     */
    public BigDecimal getPublishedValue() {
        // don't round if the number of places is unspecified
        if (getPublishedDecimalPlaces() == null) {
            return getValue();
        }
        return getValue() == null ? null : MathUtils.round(getValue(), getPublishedDecimalPlaces());
    }

    /**
     * Returns a {@code Flags} object containing QC flag information
     * @return {@code Flags} object containing QC flag information
     */
    public Flags getFlags() {
        return flags;
    }

    /**
     * Returns the station id of the observing Station
     * @return the station id of the observing Station
     */
    @Override
    public int getStationId() {
        return staDateElement.getStationId();
    }

    /**
     * Returns the datetime id of the observation time
     * @return the datetime id of the observation time
     */
    @Override
    public int getDatetimeId() {
        return staDateElement.getDatetimeId();
    }

    /**
     * Returns the element id of the observed/calculated element
     * @return the element id of the observed/calculated element
     */
    @Override
    public int getElementId() {
        return staDateElement.getElementId();
    }

    /**
     * Returns the number of decimal places retained in the database for this element
     * @return the number of decimal places retained in the database for this element
     */
    public Integer getDecimalPlaces() {
        return decimalPlaces;
    }

    /**
     * Returns the number of decimal places appropriate for public display for this element
     * @return the number of decimal places appropriate for public display for this element
     */
    public Integer getPublishedDecimalPlaces() {
        return publishedDecimalPlaces;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(getStationId()).addValue(getDatetimeId()).addValue(getElementId())
                .addValue(value).addValue(flags).toString();
    }

    @Override
    public int compareTo(Object o) {
        if (o != null && o instanceof ElementValue) {
            ElementValue ev = (ElementValue) o;
            return ComparisonChain.start().compare(staDateElement, ev.staDateElement, Ordering.natural().nullsLast())
                    .compare(value, ev.value, Ordering.natural().nullsLast()).compare(this.flags, ev.flags).result();
        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (this.compareTo(o) == 0) {
            ElementValue ev = (ElementValue) o;
            // decimal places, publishedDecimalPlaces are equal or both null
            return Objects.equal(decimalPlaces, ev.decimalPlaces)
                    && Objects.equal(publishedDecimalPlaces, ev.publishedDecimalPlaces);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(staDateElement, value, flags);
    }

}
