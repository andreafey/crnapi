package gov.noaa.ncdc.crn.domain;

import java.io.Serializable;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

@SuppressWarnings("serial")
public class ExceptionResolution implements Comparable<ExceptionResolution>, Serializable {
    // nullable (id created on insert)
    private Integer resolutionId;
    private final int exceptionId;
    private final String filename;
    private final ExceptionReapply reapply;

    public ExceptionResolution(Integer resolutionId, final int exceptionId, final String filename,
            final ExceptionReapply reapply) {
        this.resolutionId = resolutionId;
        this.exceptionId = exceptionId;
        this.filename = filename;
        this.reapply = reapply;
    }

    public int getResolutionId() {
        return resolutionId;
    }

    public int getExceptionId() {
        return exceptionId;
    }

    public String getFilename() {
        return filename;
    }

    public ExceptionReapply getReapply() {
        return reapply;
    }

    @Override
    public int compareTo(ExceptionResolution er) {
        if (er != null) {
            return ComparisonChain.start().compare(resolutionId, er.resolutionId).compare(exceptionId, er.exceptionId)
                    .compare(filename, er.filename).compare(reapply, er.reapply).result();
        }
        return -1;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj instanceof ExceptionResolution) {
            ExceptionResolution er = (ExceptionResolution) obj;
            return this.compareTo(er) == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(resolutionId, exceptionId, filename, reapply);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("resolutionId", resolutionId).add("exceptionId", exceptionId)
                .add("reapply", reapply).addValue(filename).toString();
    }
}
