package gov.noaa.ncdc.crn.dao;

import gov.noaa.ncdc.crn.domain.CrnException;
import gov.noaa.ncdc.crn.domain.CrnExceptionFact;
import gov.noaa.ncdc.crn.domain.ExceptionReapply;
import gov.noaa.ncdc.crn.domain.ExceptionResolution;
import gov.noaa.ncdc.crn.domain.ExceptionResolutionFact;
import gov.noaa.ncdc.crn.domain.ExceptionTicketSystem;
import gov.noaa.ncdc.crn.domain.ResolutionWithFacts;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

public interface ExceptionDao {

    /**
     * Retrieve exception from id.
     * @param exceptionId The id of the exception
     * @throws DataAccessException
     */
    public CrnException getException(int exceptionId)
            throws DataAccessException;

    /**
     * Retrieve exceptions associated with a particular trouble ticket.
     * @param system The ticket system
     * @param ticketId The internal id to the ticket system
     * @throws DataAccessException
     */
    public Collection<CrnException>
    getExceptionsFromTicket(ExceptionTicketSystem system, String ticketId)
            throws DataAccessException;

    /**
     * Retrieve List<CrnExceptionFact> from parameters.
     * @param params Map of parameters
     * Some combination of these:
     *   exceptionId => The id of the exception
     *   stationId => The id of the station
     *   datetimeId => the time of observation
     *   beginDatetimeId & endDatetimeId => the beginning and end of the range
     *   elementId => The element id to include
     * @throws DataAccessException
     */
    public List<CrnExceptionFact> getExceptionFacts(Map<String,Object> params)
            throws DataAccessException;

    /**
     * Retrieve a List<ResolutionWithFacts> for a station/datetime and a 
     * particular ExceptionReapply status 
     * @param stationId The station to consider
     * @param datetimeId The datetime to consider
     * @param reapply The ExceptionReapply status to consider
     * @return List<ResolutionWithFacts> for a station/datetime and a 
     * particular ExceptionReapply status
     * @throws DataAccessException
     */
    public List<ResolutionWithFacts> getResolutionWithFacts(int stationId,
            int datetimeId, ExceptionReapply reapply) throws DataAccessException;
    /**
     * Retrieve a List<ResolutionWithFacts> for a station/time range and a 
     * particular ExceptionReapply status 
     * @param stationId The station to consider
     * @param beginDt The beginning of the range (datetimeId) to consider
     * @param endDt The end of the range (datetimeId) to consider
     * @param reapply The ExceptionReapply status to consider
     * @return List<ResolutionWithFacts> for a station/datetime and a 
     * particular ExceptionReapply status
     * @throws DataAccessException
     */
    public List<ResolutionWithFacts> getResolutionWithFacts(int stationId,
            int beginDt, int endDt, ExceptionReapply reapply) 
                    throws DataAccessException;

    /**
     * Retrieve List<CrnExceptionFact> which are unresolved.
     * @param stationIds The stations to retrieve data for; when null retrieve all stations
     * @param beginDatetimeId The beginning of the datetime range to retrieve data for
     * @param endDatetimeId The end of the datetime range to retrieve data for
     * @param elementIds The elements to retrieve data for; when null retrieve all elements
     * @throws DataAccessException
     */
    public List<CrnExceptionFact> getUnresolvedExceptionFacts(
            Collection<Integer> stationIds, int beginDatetimeId,
            int endDatetimeId, int... elementIds) throws DataAccessException;

    /**
     * Retrieve List<CrnExceptionFact> for a CrnException which are unresolved.
     * @param exceptionId The CrnException to retrieve unresolved CrnExceptionFacts for
     * @throws DataAccessException
     */
    public List<CrnExceptionFact> getUnresolvedExceptionFacts(int exceptionId) throws DataAccessException;

    /**
     * Inserts a new CrnException into the database.
     * @param exception The CrnException to insert
     * @throws DataAccessException
     */
    public void insertException(CrnException exception)
            throws DataAccessException;


    /**
     * Inserts a CrnExceptionFact into the database.
     * @param facts The fact to insert
     * @throws DataAccessException
     */
    public void insertExceptionFact(CrnExceptionFact fact)
            throws DataAccessException;

    /**
     * Insert an ExceptionResolution into the database
     * @param resolution The ExceptionResolution to insert
     */
    public void insertExceptionResolution(ExceptionResolution resolution) throws DataAccessException;

    /**
     * Insert a Collection<ExceptionResolutionFact> into the database
     * @param facts The facts to insert
     */
    public void insertExceptionResolutionFacts(
            Collection<ExceptionResolutionFact> facts) throws DataAccessException;

    /**
     * Updates a CrnException's status.
     * @param exception The CrnException to update
     * @throws DataAccessException
     */
    public void updateExceptionStatus(CrnException exception) throws DataAccessException;

}
