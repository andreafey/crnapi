package gov.noaa.ncdc.crn.domain;

import java.io.Serializable;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

/**
 * A {@code CrnException} contains information about a problem discovered with CRN data which needs to be addressed in
 * some way and tracked. Note: This has nothing to do with a software exception, such as one which extends
 * {@code java.lang.Exception}.
 * 
 * It was only after extensive discussion that we settled on this nomenclature, which while confusing to software
 * developers, has semantic meaning to the climate community.
 * 
 * @author Andrea.Fey
 */
@SuppressWarnings("serial")
public class CrnException implements Comparable<CrnException>, Serializable {
    /** internal id of exception */
    private final Integer exceptionId;
    /** system ticket system where this exception is documented */
    private final ExceptionTicketSystem system;
    /** internal ticket system id */
    private final String ticketId;
    /** brief description of the exception */
    private final String description;
    /** current exception status */
    private ExceptionStatus status;
    /** List of resolutions associated with this exception (usually one) */
    private final List<ExceptionResolution> resolutions;

    /**
     * Constructor to use when exceptionId already assigned
     * @param exceptionId internal id of exception
     * @param system ticket system where this exception is documented
     * @param ticketId internal ticket system id
     * @param description brief description of the exception
     * @param status current exception status
     */
    public CrnException(Integer exceptionId, ExceptionTicketSystem system, String ticketId, String description,
            ExceptionStatus status) {
        this(exceptionId, system, ticketId, description, status, null);
    }

    /**
     * Constructor to create a new {@code CrnException} prior to database insert
     * @param system ticket system where this exception is documented
     * @param ticketId internal ticket system id
     * @param description brief description of the exception
     * @param status current exception status
     */
    public CrnException(ExceptionTicketSystem system, String ticketId, String description, ExceptionStatus status) {
        this(null, system, ticketId, description, status, null);
    }

    /**
     * Constructor to use when exceptionId already assigned and resolutions are available
     * @param exceptionId internal id of exception
     * @param system ticket system where this exception is documented
     * @param ticketId internal ticket system id
     * @param description brief description of the exception
     * @param status current exception status
     * @param resolutions List of resolutions associated with this exception
     */
    public CrnException(Integer exceptionId, ExceptionTicketSystem system, String ticketId, String description,
            ExceptionStatus status, List<ExceptionResolution> resolutions) {
        this.exceptionId = exceptionId;
        this.system = system;
        this.ticketId = ticketId;
        this.description = description;
        this.status = status;
        this.resolutions = resolutions;
    }

    public Integer getExceptionId() {
        return exceptionId;
    }

    public ExceptionTicketSystem getSystem() {
        return system;
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getDescription() {
        return description;
    }

    public ExceptionStatus getStatus() {
        return status;
    }

    /**
     * Change this {@code CrnException}'s status
     * @param status the status to change to
     */
    public void setStatus(ExceptionStatus status) {
        this.status = status;
    }

    // public List<ExceptionResolution> getResolutions() {
    // return resolutions;
    // }
    // public void setResolutions(List<ExceptionResolution> resolutions) {
    // this.resolutions = resolutions;
    // }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o.getClass().isAssignableFrom(this.getClass())) {
            CrnException ex = (CrnException) o;
            return Objects.equal(exceptionId, ex.exceptionId) && Objects.equal(status, ex.status);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(exceptionId, status);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(exceptionId).add(system.name(), ticketId).addValue(status.name())
                .toString();
    }

    @Override
    public int compareTo(CrnException ex) {
        if (this == ex) {
            return 0;
        }
        if (ex != null) {
            return ComparisonChain.start().compare(exceptionId, ex.exceptionId).compare(status, ex.status).result();
        }
        return -1;
    }

}
