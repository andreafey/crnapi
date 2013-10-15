package gov.noaa.ncdc.crn.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

/**
 * {@code ObservationWithData} is a wrapper class which associates an {@link Observation} to a
 * {@code Map<Integer,ElementValue>} containing some or all of its observed and/or calculated values.
 * @author: Andrea Fey
 */
// TODO make this immutable. It should instead be extended to add
// setElementValues, addNewElement methods, constructor with just Observation
@SuppressWarnings("serial")
public class ObservationWithData implements StationAware, DatetimeAware, Comparable<ObservationWithData>, Serializable {
    private Map<Integer, ElementValue> elementValues;
    private final Observation observation;

    /**
     * Constructor from {@link Observation}
     * @param ob The object's {@link Observation}
     */
    public ObservationWithData(final Observation ob) {
        this.observation = ob;
    }

    /**
     * Constructor from {@link Observation} and {@code Map<Integer,ElementValue>}
     * @param ob The object's {@link Observation}
     * @param values The object's {@link ElementValue}s
     */
    public ObservationWithData(final Observation ob, Map<Integer, ElementValue> values) {
        this.observation = ob;
        setElementValues(values);
    }

    /**
     * Creates a new {@link ElementValue} with this element id and value (and no flags) and adds it to the
     * {@code Map<Integer,ElementValue>}
     * @param elementId The element it
     * @param value The observed or calculated value
     * @param flag The int value of the associated flag
     * @return the new {@code ElementValue}
     */
    public ElementValue addNewElementValue(int elementId, BigDecimal value, int flag) {
        ElementValue elVal = new ElementValue(observation.getStationId(), observation.getDatetimeId(), elementId,
                value, flag, null, null);
        if (elementValues == null) {
            elementValues = new HashMap<>();
        }
        elementValues.put(elementId, elVal);
        return elVal;
    }

    /**
     * Creates a new {@code ElementValue} with this element id and value (and no flags) and adds it to the
     * {@code Map<Integer,ElementValue>}
     * @param elementId The element it
     * @param value The observed or calculated value
     * @return the previous value associated with key, or {@code null} if there was no mapping for key. (A {@code null}
     * return can also indicate that the map previously associated {@code null} with key, if the implementation supports
     * {@code null} values.)
     */
    public ElementValue addNewElementValue(int elementId, BigDecimal value) {
        return addNewElementValue(elementId, value, 0);
    }

    /**
     * Returns the {@code ElementValue} in the {@code Map<Integer,ElementValue>} with this elementid
     * @param elementId the elementid of the {@code ElementValue} to retrieve
     * @return the {@code ElementValue} in the {@code Map<Integer,ElementValue>} with this elementid
     */
    public ElementValue getElementValue(int elementId) {
        return elementValues.get(elementId);
    }

    /**
     * Returns the {@code Map<Integer,ElementValue>} of observed and calculated values. No assumption is made as to the
     * relationship between the Map and the database, as this might contain only a limited number of elements from the
     * database, or it might be created in order to populate the database.
     * @return {@code Map<Integer,ElementValue>} of observed and calculated values
     */
    public Map<Integer, ElementValue> getElementValues() {
        return elementValues;
    }

    /**
     * Sets the existing {@code Map<Integer,ElementValue>}.
     * @param values The {@code ElementValue}s to point the map to
     */
    public void setElementValues(Map<Integer, ElementValue> values) {
        elementValues = values;
    }

    /**
     * Removes the {@code ElementValue} with this id from the Map
     * @param elementId The id of the {@code ElementValue} to remove
     */
    public void removeElementValue(int elementId) {
        if (elementValues != null) {
            elementValues.remove(elementId);
        }
    }

    /**
     * Returns the station id of the observation
     * @return the station id
     */
    @Override
    public int getStationId() {
        return observation.getStationId();
    }

    /**
     * Returns the datetime id of the observation
     * @return the datetime id
     */
    @Override
    public int getDatetimeId() {
        return observation.getDatetimeId();
    }

    /**
     * Returns the {@code Observation} in this object
     * @return the {@code Observation} in this object
     */
    public Observation getObservation() {
        return observation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ObservationWithData) {
            ObservationWithData ob = (ObservationWithData) o;
            return Objects.equal(this.observation, ob.observation) && Objects.equal(elementValues, ob.elementValues);
        }
        return false;
    }

    /* Just compare the observations for sorting purposes */
    @Override
    public int compareTo(ObservationWithData ob) {
        return ComparisonChain.start().compare(observation, ob.observation, Ordering.natural().nullsLast()).result();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(observation)
                .add("#values", elementValues == null ? "null" : elementValues.size()).toString();
    }

}
