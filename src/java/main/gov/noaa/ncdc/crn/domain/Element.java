package gov.noaa.ncdc.crn.domain;

import java.io.Serializable;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

/**
 * Represents an {@code Element} either transmitted by a CRN station or calculated by the network.
 * @author Andrea Fey
 */
@SuppressWarnings("serial")
public class Element implements ElementAware, Comparable<Object>, Serializable {

    /** The internal id */
    private final int elementId;
    /** The unique element name */
    private final String name;
    /** A description of what the element measures */
    private final String description;
    /** The number of decimal places of precision to store by default */
    private final Integer defaultDecimalPlaces;
    /** The number of decimal places of precision to display by default */
    private final Integer defaultPublishedDecimalPlaces;
    /** Whether or not this is a calculated element */
    private final boolean calculated;
    /** The measurement units */
    private final String units;
    /** The id of the related netCDF variable */
    private final int netcdfId;
    /**
     * Length of time in minutes spent measuring phenomenon to produce the value this element describes. Must be between
     * 0 and 60. 0 indicates this is an instantaneous measurement. 60 indicates it is an hourly measurement. 5 or 15
     * indicates it is a subhourly measurement.
     */
    private final int duration;
    /** The minute of the hour at which a measurement interval concludes. */
    private final int endMinute;

    /**
     * Full constructor
     * @param elementId The unique internal elementId
     * @param name The unique element name
     * @param description A description of what the element measures
     * @param calculated Not nullable; Whether or not this is a calculated element
     * @param duration Length of time in minutes the measurement covers
     * @param endMinute The minute of the hour at which a measurement interval concludes.
     * @param decimalPlaces The number of decimal places of precision to store by default
     * @param publishedDecimalPlaces The number of decimal places of precision to display by default
     * @param units The measurement units
     * @param netcdfId The internal id of the related netCDF variable
     */
    public Element(final int elementId, final String name, final String description, final Boolean calculated,
            final int duration, final int endMinute, final Integer decimalPlaces, final Integer publishedDecimalPlaces,
            final String units, final int netcdfId) {
        this.elementId = elementId;
        this.name = name;
        this.description = description;
        this.calculated = calculated;
        this.duration = duration;
        this.endMinute = endMinute;
        this.defaultDecimalPlaces = decimalPlaces;
        this.defaultPublishedDecimalPlaces = publishedDecimalPlaces;
        this.units = units;
        this.netcdfId = netcdfId;
    }

    /**
     * Return the {@code Element}'s elementId
     * @return the {@code Element}'s elementId
     */
    @Override
    public int getElementId() {
        return elementId;
    }

    /**
     * Return the name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Return the description
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Return the default stored precision for the element
     * @return the default stored precision for the element
     */
    public Integer getDefaultDecimalPlaces() {
        return defaultDecimalPlaces;
    }

    /**
     * Return the default display precision for the element
     * @return the default display precision for the element
     */
    public Integer getDefaultPublishedDecimalPlaces() {
        return defaultPublishedDecimalPlaces;
    }

    /**
     * Return true if this element is calculated after the transmission is received
     * @return true if this element is calculated
     */
    public boolean isCalculated() {
        return calculated;
    }

    /**
     * Return the length of time in minutes spent measuring phenomenon to produce the value this element describes. Must
     * be between 0 and 60.
     * @return Measurement time in minutes
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Return the minute of the hour at which a measurement interval concludes.
     * @return the time of the hour the measurement interval concludes
     */
    public int getEndMinute() {
        return endMinute;
    }

    /**
     * Return the units this element measures
     * @return
     */
    public String getUnits() {
        return units;
    }

    /**
     * Return the id of the netCDF variable
     * @return the id of the netCDF variable
     */
    public int getNetcdfId() {
        return netcdfId;
    }

    @Override
    public int compareTo(Object o) {
        if (o != null && o instanceof Element) {
            return ComparisonChain.start().compare(elementId, ((Element) o).elementId).result();
        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        // true if these refer to the same object
        if (this == o) {
            return true;
        }
        // true if o is a non-null Element with the same elementId and name
        if (o != null && o instanceof Element) {
            Element e = (Element) o;
            return ComparisonChain.start().compare(elementId, e.elementId).compare(name, e.name).result() == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.elementId;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(elementId).addValue(name).addValue(description).addValue(units)
                .addValue(calculated).addValue(netcdfId).addValue(duration).add("ends", endMinute)
                .add("places", defaultDecimalPlaces).add("published", defaultPublishedDecimalPlaces).toString();
    }
}
