package gov.noaa.ncdc.crn.domain;

/**
 * An ordered representation of the different CRN QC flags
 * @author Andrea Fey
 */
public enum ExceptionStatus {
    /** No resolutions have been applied to the exception */
    OPEN(1),
    /** Some resolutions have been applied but some associated exception flags still need to be addressed */
    OPEN_PARTIAL(2),
    /** Resolutions have been applied and all associated exception flags have been removed */
    CLOSED_RESOLVED(3),
    /** No resolutions are possible for this exception; exception flags will remain indefinitely */
    CLOSED_UNRESOLVABLE(4),
    /** Some resolutions have been applied but no others are possible; some exception flags will remain indefinitely */
    CLOSED_PARTIAL(5);
    private int id;

    private ExceptionStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
