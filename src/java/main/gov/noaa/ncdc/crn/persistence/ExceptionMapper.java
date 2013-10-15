package gov.noaa.ncdc.crn.persistence;

import gov.noaa.ncdc.crn.domain.CrnException;
import gov.noaa.ncdc.crn.domain.CrnExceptionFact;
import gov.noaa.ncdc.crn.domain.ExceptionReapply;
import gov.noaa.ncdc.crn.domain.ExceptionResolution;
import gov.noaa.ncdc.crn.domain.ExceptionResolutionFact;
import gov.noaa.ncdc.crn.domain.ExceptionTicketSystem;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

public interface ExceptionMapper {

    /**
     * Retrieve exception from id.
     * @param The id of the exception
     * @throws DataAccessException
     */
    public CrnException selectException(final int exceptionId) throws DataAccessException;

    /**
     * Retrieve List{@code <CrnExceptionFact>} from parameters.
     * @param Map of parameters Some combination of these:
     * 
     * <pre>
     *   exceptionId => The id of the exception
     *   stationId => The id of the station
     *   datetimeId => the time of observation
     *   beginDatetimeId & endDatetimeId => the beginning and end of the range
     *   elementId => The element id to include
     * </pre>
     * @throws DataAccessException
     */
    public List<CrnExceptionFact> selectExceptionFacts(final Map<String, Object> params) throws DataAccessException;

    /**
     * Retrieve List{@code <ExceptionResolution>} for a station/datetime ordered by time loaded
     * @param stationId The id of the station
     * @param datetimeId The time of consideration
     * @param reapply The ExceptionReapply status
     * @return List{@code <ExceptionResolution>} ordered by ExceptionResolutionFact load time
     * @throws DataAccessException
     */
    public List<ExceptionResolution> selectExceptionResolutions(@Param("stationId") int stationId,
            @Param("datetimeId") final int datetimeId, @Param("reapply") final ExceptionReapply reapply)
            throws DataAccessException;

    /**
     * Retrieve List{@code <ExceptionResolution>} for a station/time range ordered by time loaded
     * @param stationId The id of the station
     * @param beginDt The beginning of the range time (datetimeId)
     * @param endDt The end of the range time (datetimeId)
     * @param reapply The ExceptionReapply status
     * @return List{@code <ExceptionResolution>} ordered by ExceptionResolution load time
     * @throws DataAccessException
     */
    public List<ExceptionResolution> selectExceptionResolutions(@Param("stationId") final int stationId,
            @Param("beginDatetimeId") final int beginDt, @Param("endDatetimeId") final int endDt,
            @Param("reapply") final ExceptionReapply reapply) throws DataAccessException;

    /**
     * Retrieve List{@code <ExceptionResolutionFact>} for a station/datetime ordered by time loaded
     * @param stationId The id of the station
     * @param datetimeId The time of consideration
     * @param reapply The ExceptionReapply status
     * @return List{@code <ExceptionResolutionFact>} ordered by ExceptionResolutionFact load time
     * @throws DataAccessException
     */
    public List<ExceptionResolutionFact> selectExceptionResolutionFacts(@Param("stationId") final int stationId,
            @Param("datetimeId") final int datetimeId, @Param("reapply") final ExceptionReapply reapply)
            throws DataAccessException;

    /**
     * Retrieve List{@code <ExceptionResolutionFact>} for a station/time range ordered by time loaded
     * @param stationId The id of the station
     * @param beginDt The beginning of the range time (datetimeId)
     * @param endDt The end of the range time (datetimeId)
     * @param reapply The ExceptionReapply status
     * @return List{@code <ExceptionResolutionFact>} ordered by ExceptionResolutionFact load time
     * @throws DataAccessException
     */
    public List<ExceptionResolutionFact> selectExceptionResolutionFacts(@Param("stationId") final int stationId,
            @Param("beginDatetimeId") final int beginDt, @Param("endDatetimeId") final int endDt,
            @Param("reapply") final ExceptionReapply reapply) throws DataAccessException;

    /**
     * Retrieve exceptions associated with a particular trouble ticket.
     * @param system The ticket system
     * @param ticketId The internal id to the ticket system
     * @throws DataAccessException
     */
    public Collection<CrnException> selectExceptionsFromTicket(
            @Param(value = "system") final ExceptionTicketSystem system,
            @Param(value = "ticketId") final String ticketId) throws DataAccessException;

    /**
     * Retrieve List{@code <CrnExceptionFact>} which are unresolved.
     * @param stationIds The stations to retrieve data for; when null retrieve all stations
     * @param beginDatetimeId The beginning of the datetime range to retrieve data for
     * @param endDatetimeId The end of the datetime range to retrieve data for
     * @param elementIds The elements to retrieve data for; when null retrieve all elements
     * @return List{@code <CrnExceptionFact>} which are unresolved
     * @throws DataAccessException
     */
    public List<CrnExceptionFact> selectUnresolvedExceptionFacts(
            @Param(value = "stationIds") final Collection<Integer> stationIds,
            @Param(value = "beginDatetimeId") final int beginDatetimeId,
            @Param(value = "endDatetimeId") final int endDatetimeId,
            @Param(value = "elementIds") final int... elementIds);

    /**
     * Retrieve List{@code <CrnExceptionFact>} for a CrnException which are unresolved.
     * @param exceptionId The CrnException to retrieve data for
     * @throws DataAccessException
     */
    public List<CrnExceptionFact> selectUnresolvedExceptionFacts(@Param(value = "exceptionId") int exceptionId);

    /**
     * Inserts a new CrnException into the database.
     * @param exception The CrnException to insert
     * @throws DataAccessException
     */
    public void insertException(CrnException exception) throws DataAccessException;

    /**
     * Inserts a CrnExceptionFact into the database.
     * @param facts The fact to insert
     * @throws DataAccessException
     */
    public void insertExceptionFact(CrnExceptionFact fact) throws DataAccessException;

    /**
     * Updates a CrnException's status.
     * @param exception The CrnException to update
     * @throws DataAccessException
     */
    public void updateExceptionStatus(final CrnException exception) throws DataAccessException;

    /**
     * Inserts a new ExceptionResolution into the database.
     * @param exception The ExceptionResolution to insert
     * @throws DataAccessException
     */
    public void insertExceptionResolution(ExceptionResolution resolution) throws DataAccessException;

    /**
     * Inserts a new ExceptionResolutionFact into the database.
     * @param exception The ExceptionResolutionFact to insert
     * @throws DataAccessException
     */
    public void insertExceptionResolutionFact(ExceptionResolutionFact fact) throws DataAccessException;

}
