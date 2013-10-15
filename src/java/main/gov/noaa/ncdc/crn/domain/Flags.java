package gov.noaa.ncdc.crn.domain;

import gov.noaa.ncdc.crn.util.MathUtils;

import java.io.Serializable;
import java.util.BitSet;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

@SuppressWarnings("serial")
public class Flags implements Comparable<Flags>, Serializable {

    /** A collection which represents ordered QC flag values */
    private BitSet flagbits = new BitSet();

    /**
     * Default constructor which sets all QC flags to false
     */
    public Flags() {
        flagbits = new BitSet();
    }

    /**
     * Constructor which uses the combined {@code Integer} representation of QC flags for construction
     * @param intvalue
     */
    public Flags(Integer intvalue) {
        setFlagsFromInt(intvalue);
    }

    /**
     * Returns true if any QC flag is true.
     * @return true if any QC flag is true
     */
    public boolean isFlagged() {
        return getIntValue() != 0;
    }

    /**
     * Returns true if the QC flag of {@link FlagType} is true.
     * @return true if the QC flag of {@link FlagType} is true
     */
    public boolean isFlagged(FlagType type) {
        return flagbits.get(type.ordinal());
    }

    /**
     * Sets the QC flag of type {@link FlagType} to the value of the flagged argument
     * @param type The {@link FlagType} to set the flag on
     * @param flagged The value to set the flag to
     */
    public void setFlagged(FlagType type, boolean flagged) {
        flagbits.set(type.ordinal(), flagged);
    }

    /**
     * Sets the QC flag of type {@link FlagType} to true
     * @param type The {@link FlagType} to set the flag on
     */
    public void setFlagged(FlagType type) {
        flagbits.set(type.ordinal());
    }

    /**
     * Retrieves the combined int value of all the QC flags
     * @return the combined {@code int} value of all the QC flags
     * @deprecated use getIntValue() instead
     */
    @Deprecated
    public int intValue() {
        return getIntValue();
    }

    /**
     * Retrieves the combined {@code int} value of all the QC flags
     * @return the combined {@code int} value of all the QC flags
     */
    public int getIntValue() {
        return MathUtils.bitSetToUnsignedInt(flagbits);
    }

    /**
     * Sets the QC flags by considering intvalue to be the combined {@code Integer} value of all the QC flags. If
     * intvalue is {@code null}, sets all QC flags to false.
     * @param intvalue the combined {@code Integer} value of all the QC flags
     */
    public void setFlagsFromInt(Integer intvalue) {
        if (intvalue == null) {
            intvalue = Integer.valueOf(0);
        }
        flagbits = MathUtils.unsignedIntToBitSet(intvalue);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("flagged", isFlagged()).addValue(getIntValue()).toString();
    }

    @Override
    public int compareTo(Flags flags) {
        return ComparisonChain.start().compare(this.getIntValue(), flags.getIntValue()).result();
    }

    @Override
    public int hashCode() {
        return getIntValue();
    }
}
