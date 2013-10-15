package gov.noaa.ncdc.crn.domain;

/**
 * Interface for all CRN domain objects which relate to {@link Station}s
 * @author Andrea Fey
 */
public interface StationAware {
    public abstract int getStationId();
}
