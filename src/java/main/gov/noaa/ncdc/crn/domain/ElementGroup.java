package gov.noaa.ncdc.crn.domain;

import java.io.Serializable;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

/**
 * {@code ElementGroup} is a domain class which abstractly represents some collection of elements. It contains a
 * description and ordinal information so that it can be ordered for display.
 * @author Andrea.Fey
 */
@SuppressWarnings("serial")
public class ElementGroup implements Comparable<Object>, Serializable {

    private final int elementGroupId;
    private final int ordinal;
    private final String description;
    private final int representativeElement;
    private final List<Integer> memberElements;

    /**
     * Creates an {@code ElementGroup} from its the components
     * @param elementGroupId The elementGroupId
     * @param ordinal The ordinal
     * @param description The description
     * @param representativeElement The element which artificially determines if any elements in the group are available
     * @param memberElements Ordered list of elementIds in this group
     */
    public ElementGroup(int elementGroupId, int ordinal, String description, int representativeElement,
            List<Integer> memberElements) {
        this.elementGroupId = elementGroupId;
        this.ordinal = ordinal;
        this.description = description;
        this.representativeElement = representativeElement;
        this.memberElements = memberElements;
    }

    /**
     * Returns the elementGroupId
     * @return the elementGroupId
     */
    public int getElementGroupId() {
        return elementGroupId;
    }

    /**
     * Returns the ordinal
     * @return the ordinal
     */
    public int getOrdinal() {
        return ordinal;
    }

    /**
     * Return the description
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Return the id of a member Element which "represents" this group; i.e. one can determine whether a stream contains
     * the group by whether the element is present in the stream
     * @return the id of a specific member Element
     */
    public int getRepresentativeElement() {
        return representativeElement;
    }

    /**
     * Return a {@code List<Integer>} of elementIds contained within this {@code ElementGroup}
     * @return {@code List<Integer>} of elementIds contained within this {@code ElementGroup}
     */
    public List<Integer> getMemberElements() {
        return memberElements;
    }

    @Override
    public int compareTo(Object o) {
        // if o is an ElementGroup, compare based on id and ordinal
        if (o instanceof ElementGroup) {
            ElementGroup eg = (ElementGroup) o;
            return ComparisonChain.start().compare(elementGroupId, eg.elementGroupId).compare(ordinal, eg.ordinal)
                    .result();
        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        // true if these refer to the same object
        if (this == o) {
            return true;
        }
        // true if o is an ElementGroup with the same id, ordinal, and description
        if (this.compareTo(o) == 0) {
            ElementGroup eg = (ElementGroup) o;
            return this.description.equals(eg.description);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(elementGroupId, ordinal, description);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(elementGroupId).add("ordinal", ordinal).addValue(description)
                .toString();
    }

}
