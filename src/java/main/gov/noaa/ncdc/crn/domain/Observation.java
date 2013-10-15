package gov.noaa.ncdc.crn.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

/**
 * Represents an hourly {@code Observation} transmitted by a CRN station. Includes information about the station,
 * {@code Observation} time, stream, data source type, file name, line number, initial load time, last modified time,
 * and time exported to ISD.
 * @author Andrea Fey
 */
@SuppressWarnings("serial")
public class Observation implements StationAware, DatetimeAware, Comparable<Observation>, Serializable {
    /** The station identifier. */
    private final int stationId;
    /** The id of the {@code Observation} time. */
    private final int datetimeId;
    /** The id of the transmission stream. */
    private final int streamId;
    /** The id of the type of data used to load the {@code Observation} into the database. */
    private final int dataSourceId;
    /** The initial time in UTC the data was loaded into the database */
    private Timestamp initialLoad;
    /** The time in UTC the data was last modified */
    private Timestamp lastModified;
    /** The time in UTC the data was last exported to ISD format */
    private Timestamp timeExportedToIsd;
    /** The name of the file the {@code Observation} came from */
    private final String fileName;
    /** The line number of the file the {@code Observation} came from */
    private final Integer lineNumber;
    /** The time in UTC the file was loaded into the database */
    private Timestamp timeLoaded;

    /**
     * Constructor which includes all information which should be available at ingest time
     * @param stationId The id of the observing station
     * @param datetimeId The id of the hour of {@code Observation}
     * @param streamId The id of the observing stream
     * @param dataSourceId The id of the type of data source
     * @param fileName The fileName containing the {@code Observation}
     * @param lineNumber The line number of the {@code Observation} within the containing file
     */
    public Observation(int stationId, int datetimeId, int streamId, int dataSourceId, String fileName, int lineNumber) {
        this.stationId = stationId;
        this.datetimeId = datetimeId;
        this.streamId = streamId;
        this.dataSourceId = dataSourceId;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
    }

    /**
     * Returns the id of the type of data used to load the {@code Observation} into the database
     * @return the id of the type of data used to load the {@code Observation} into the database
     */
    public int getDataSourceId() {
        return dataSourceId;
    }

    /**
     * Returns the datetime id
     * @return the datetime id
     */
    @Override
    public int getDatetimeId() {
        return datetimeId;
    }

    /**
     * Returns the time the {@code Observation} was originally loaded into the database
     * @return the time the {@code Observation} was originally loaded into the database
     */
    public Timestamp getInitialLoad() {
        return initialLoad;
    }

    /**
     * Sets the time the {@code Observation} was originally loaded into the database
     * @param initialLoad the time the {@code Observation} was originally loaded into the database
     */
    public void setInitialLoad(Timestamp initialLoad) {
        this.initialLoad = initialLoad;
    }

    /**
     * Returns the time the {@code Observation} was last modified
     * @return the time the {@code Observation} was last modified
     */
    public Timestamp getLastModified() {
        return lastModified;
    }

    /**
     * Sets the time the {@code Observation} was last modified
     * @param lastModified the time the {@code Observation} was last modified
     */
    public void setLastModified(Timestamp lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * Returns the station id
     * @return the station id
     */
    @Override
    public int getStationId() {
        return stationId;
    }

    /**
     * Returns the stream id
     * @return the stream id
     */
    public int getStreamId() {
        return streamId;
    }

    /**
     * Returns the time the {@code Observation} was last exported to ISD
     * @return the time the {@code Observation} was last exported to ISD
     */
    public Timestamp getTimeExportedToIsd() {
        return timeExportedToIsd;
    }

    /**
     * Sets the time the {@code Observation} was last exported to ISD
     * @param timeExportedToIsd the time the {@code Observation} was last exported to ISD
     */
    public void setTimeExportedToIsd(Timestamp timeExportedToIsd) {
        this.timeExportedToIsd = timeExportedToIsd;
    }

    /**
     * Returns the name of the file this {@code Observation} was generated from
     * @return the name of the file this {@code Observation} was generated from
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Returns the line number in the file that this {@code Observation} was generated from
     * @return the line number in the file that this {@code Observation} was generated from
     */
    public Integer getLineNumber() {
        return lineNumber;
    }

    /**
     * Returns the {@code Timestamp} of the time the {@code Observation} in this specific file/line number was inserted
     * into the database
     * @return the {@code Timestamp} of the time the {@code Observation} in this specific file/line number was inserted
     * into the database
     */
    public Timestamp getTimeLoaded() {
        return timeLoaded;
    }

    /**
     * Sets the {@code Timestamp} of the time the {@code Observation} in this specific file/line number was inserted
     * into the database.
     * This {@code Timestamp} should be generated by the database rather than the application.
     * @param timeLoaded
     */
    public void setTimeLoaded(Timestamp timeLoaded) {
        this.timeLoaded = timeLoaded;
    }

    /**
     * Retrieves a {@code StationDate} associated with this {@code Observation}
     * @return {@code StationDate} associated with this {@code Observation}
     */
    public StationDate getStationDate() {
        return new StationDate(stationId, datetimeId);
    }

    // orders by datetimeId, stationId, streamId, dataSourceId, lastModified
    @Override
    public int compareTo(Observation ob) {
        return ComparisonChain.start().compare(datetimeId, ob.datetimeId).compare(stationId, ob.stationId)
                .compare(streamId, ob.streamId).compare(dataSourceId, ob.dataSourceId)
                .compare(lastModified, ob.lastModified, Ordering.natural().nullsLast()).result();
    }

    // tests all fields for equality
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Observation) {
            Observation ob = (Observation) o;
            if (this.compareTo(ob) == 0) {
                return Objects.equal(this.initialLoad, ob.initialLoad)
                        && Objects.equal(this.timeExportedToIsd, ob.timeExportedToIsd)
                        && Objects.equal(this.getLineNumber(), ob.getLineNumber())
                        && Objects.equal(this.getFileName(), ob.getFileName())
                        && Objects.equal(this.timeLoaded, ob.timeLoaded);
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(stationId, datetimeId, streamId, dataSourceId, initialLoad, lastModified,
                timeExportedToIsd, fileName, lineNumber, timeLoaded);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(stationId).addValue(datetimeId).addValue(streamId)
                .add("source", dataSourceId).add("line", lineNumber).add("file", fileName).toString();
    }

}
