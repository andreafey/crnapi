package gov.noaa.ncdc.crn.dao;

import gov.noaa.ncdc.crn.domain.Stream;
import gov.noaa.ncdc.crn.domain.StreamElement;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

public interface StreamDao {
    /**
     * Retrieves a Stream based on the requested streamId
     * @param streamId The id of the requested stream
     * @return a Stream based on the requested streamId
     * @throws DataAccessException (unchecked)
     */
    public abstract Stream getStream(@Param("streamId") int streamId) throws DataAccessException;

    /**
     * Retrieves a Stream based on the requested crxVersion. It is known that early crxVersions may have multiple
     * streams associated with them. This method does not consider that, and this method will throw an exception if a
     * crx_version number is provided that has multiple streams.
     * @param crxVersion The crx_version number
     * @return a Stream based on the requested crxVersion
     * @throws DataAccessException (unchecked)
     */
    public abstract Stream getStream(@Param("crxVersion") String crxVersion) throws DataAccessException;

    /**
     * Retrieves a Stream based on the requested crxVersion and element count. This helps differentiate between the
     * older crx_versions which have multiple streams associated with them.
     * @param crxVersion The crx_version number
     * @param elementCount The number of expected elements in the resulting stream.
     * @return a Stream based on the requested crxVersion
     * @throws DataAccessException (unchecked)
     */
    public abstract Stream getStream(@Param("crxVersion") String crxVersion, @Param("elementCount") int elementCount)
            throws DataAccessException;

    /**
     * Retrieves a Map<Integer,Stream> of all streamIds mapped to Streams
     * @return a Map<Integer,Stream> of all streamIds mapped to Streams
     * @throws DataAccessException (unchecked)
     */
    @MapKey("stationId")
    public abstract Map<Integer, Stream> getStreams() throws DataAccessException;

    /**
     * Retrieves an ordered List<StreamElement> for a given stream
     * @param streamId The id of the requested stream
     * @return an ordered List<StreamElement>
     * @throws DataAccessException (unchecked)
     */
    public List<StreamElement> getStreamElementList(@Param("streamId") int streamId) throws DataAccessException;

    /**
     * Retrieves an ordered List<StreamElement> for a given stream with the multipliers appropriately set for
     * datalogger data
     * @param streamId The id of the requested stream
     * @return an ordered List<StreamElement> with the multipliers appropriately set for datalogger data
     * @throws DataAccessException (unchecked)
     */
    public List<StreamElement> getStreamElementListForPda(int streamId) throws DataAccessException;

}
