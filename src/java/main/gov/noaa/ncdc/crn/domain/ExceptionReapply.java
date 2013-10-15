package gov.noaa.ncdc.crn.domain;

/**
 * Available options when reprocessing {@link ExceptionFact}s
 * @author Andrea.Fey
 */
public enum ExceptionReapply {
    /** Reapply this resolution automatically when fact is reprocessed */
    AUTO,
    /** Do not reapply when fact is reprocessed */
    NO,
    /** Do not reapply automatically, but alert a developer */
    MANUAL;
}
