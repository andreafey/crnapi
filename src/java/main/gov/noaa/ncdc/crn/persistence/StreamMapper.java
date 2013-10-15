package gov.noaa.ncdc.crn.persistence;

import gov.noaa.ncdc.crn.domain.Stream;
import gov.noaa.ncdc.crn.domain.StreamElement;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

public interface StreamMapper {
    /**
     * Retrieves a Stream based on the requested streamId
     * @param streamId The id of the requested stream
     * @return a Stream based on the requested streamId
     * @throws DataAccessException (unchecked)
     */
    public abstract Stream selectStream(@Param("streamId") final int streamId) throws DataAccessException;

    /**
     * Retrieves a Stream based on the requested crxVersion. It is known that early crxVersions may have multiple
     * streams associated with them. This method does not consider that, and this method will throw an exception if a
     * crx_version number is provided that has multiple streams.
     * @param crxVersion The crx_version number
     * @return a Stream based on the requested crxVersion
     * @throws DataAccessException (unchecked)
     */
    public abstract Stream selectStream(@Param("crxVersion") final String crxVersion) throws DataAccessException;

    /**
     * Retrieves a Stream based on the requested crxVersion and element count. This helps differentiate between the
     * older crx_versions which have multiple streams associated with them.
     * @param crxVersion The crx_version number
     * @param elementCount The number of expected elements in the resulting stream.
     * @return a Stream based on the requested crxVersion
     * @throws DataAccessException (unchecked)
     */
    public abstract Stream selectStream(@Param("crxVersion") String crxVersion,
            @Param("elementCount") final int elementCount) throws DataAccessException;

    /**
     * Retrieves a Map{@code <Integer,Stream>} of all streamIds mapped to Streams
     * @return a Map{@code <Integer,Stream>} of all streamIds mapped to Streams
     * @throws DataAccessException (unchecked)
     */
    @MapKey("streamId")
    public abstract Map<Integer, Stream> selectStreams() throws DataAccessException;

    /**
     * Retrieves an ordered List{@code <StreamElement>} for a given stream
     * @param streamId The id of the requested stream
     * @return an ordered List{@code <StreamElement>}
     * @throws DataAccessException (unchecked)
     */
    public List<StreamElement> selectStreamElementList(@Param("streamId") final int streamId) throws DataAccessException;

    /**
     * Retrieves an ordered List{@code <StreamElement>} for a given stream with the multipliers appropriately set for
     * datalogger data
     * @param streamId The id of the requested stream
     * @return an ordered List{@code <StreamElement>} with the multipliers appropriately set for datalogger data
     * @throws DataAccessException (unchecked)
     */
    public List<StreamElement> selectStreamElementListForDatalogger(@Param("streamId") final int streamId)
            throws DataAccessException;

}
