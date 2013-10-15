package gov.noaa.ncdc.crn.domain;

/**
 * An ordered representation of the different CRN QC flags
 * @author Andrea Fey
 */
public enum FlagType {
    /*
     * The index of the enum is the same as the bit which is masked when the flag is set. DO NOT change the order of
     * this index; it is used to determine the Integer value of the flag for database population
     */

    /** observed value is outside permitted range */
    RANGE,
    /** observed value differs more than is allowed from values observed by other sensors at the same time */
    DELTA,
    /** datalogger door was open when value was observed */
    DOOR,
    /** there is an unresolved exception associated with the fact */
    EXCEPTION,
    /** the ground was frozen when the value was observed */
    FROZEN,
    /** the sensor observing this value was known to be malfunctioning */
    SENSOR;
}
