package gov.noaa.ncdc.crn.domain;

import java.util.Collection;

public class ResolutionWithFacts {
    private final ExceptionResolution resolution;
    private final Collection<ExceptionResolutionFact> facts;

    public ResolutionWithFacts(final ExceptionResolution resolution, final Collection<ExceptionResolutionFact> facts) {
        this.resolution = resolution;
        this.facts = facts;
    }

    public ExceptionResolution getResolution() {
        return resolution;
    }

    public Collection<ExceptionResolutionFact> getFacts() {
        return facts;
    }

}
