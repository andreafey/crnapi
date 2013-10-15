package gov.noaa.ncdc.crn.service;

import gov.noaa.ncdc.crn.dao.ElementDao;
import gov.noaa.ncdc.crn.dao.ExceptionDao;
import gov.noaa.ncdc.crn.dao.ObservationDao;
import gov.noaa.ncdc.crn.domain.CrnException;
import gov.noaa.ncdc.crn.domain.CrnExceptionFact;
import gov.noaa.ncdc.crn.domain.ElementValue;
import gov.noaa.ncdc.crn.domain.ExceptionReapply;
import gov.noaa.ncdc.crn.domain.ExceptionResolution;
import gov.noaa.ncdc.crn.domain.ExceptionResolutionFact;
import gov.noaa.ncdc.crn.domain.ExceptionTicketSystem;
import gov.noaa.ncdc.crn.domain.ResolutionWithFacts;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;

@Service
public class ExceptionService {
    @Autowired
    ElementDao elementDao;
    @Autowired
    ExceptionDao exceptionDao;
    @Autowired
    ObservationDao observationDao;

    /**
     * Updates only the flags of these ElementValues. Inserts flags where they don't exist already, updates the flag's
     * value if the new value differs from the old, and deletes a flag if the new value is zero.
     * <p>
     * Use with caution, for example when you are adding or removing an exception. And now an even stronger emphasis on
     * using caution: this method is in ExceptionService because there is no currently no business case for modifying
     * data flags except for adding and removing exceptions.
     * @param values The ElementValues to update
     * @throws DataAccessException
     */
    @Transactional
    public void updateFlags(Collection<ElementValue> values) throws DataAccessException {
        elementDao.updateFlags(values);
    }

    /**
     * Retrieve exception from id.
     * @param The id of the exception
     * @throws DataAccessException
     */
    public CrnException getException(int exceptionId) throws DataAccessException {
        return exceptionDao.getException(exceptionId);
    }

    /**
     * Retrieve exceptions associated with a particular trouble ticket.
     * @param system The ticket system
     * @param ticketId The internal id to the ticket system
     * @return Collection{@code <CrnException} associated with a trouble ticket
     * @throws DataAccessException
     */
    public Collection<CrnException> getExceptionsFromTicket(ExceptionTicketSystem system, String ticketId)
            throws DataAccessException {
        return exceptionDao.getExceptionsFromTicket(system, ticketId);
    }

    /**
     * Retrieve List{@code <CrnExceptionFact>} from an exceptionId.
     * @param The id of the exception
     * @return List{@code <CrnExceptionFact>} associated with the exception
     * @throws DataAccessException
     */
    public List<CrnExceptionFact> getExceptionFacts(int exceptionId) throws DataAccessException {
        Map<String, Object> params = new HashMap<>();
        params.put("exceptionId", Integer.valueOf(exceptionId));
        return exceptionDao.getExceptionFacts(params);
    }

    /**
     * Retrieve List{@code <CrnExceptionFact>} which are unresolved.
     * @param stationIds The stations to retrieve data for; when null retrieve all stations
     * @param beginDatetimeId The beginning of the datetime range to retrieve data for
     * @param endDatetimeId The end of the datetime range to retrieve data for
     * @param elementIds The elements to retrieve data for; when null retrieve all elements
     * @return List{@code <CrnExceptionFact>} which are unresolved
     * @throws DataAccessException
     */
    public List<CrnExceptionFact> getUnresolvedExceptionFacts(Collection<Integer> stationIds, int beginDatetimeId,
            int endDatetimeId, int... elementIds) throws DataAccessException {
        return exceptionDao.getUnresolvedExceptionFacts(stationIds, beginDatetimeId, endDatetimeId, elementIds);
    }

    /**
     * Retrieve List{@code <CrnExceptionFact>} for a CrnException which are unresolved.
     * @param exceptionId The CrnException to retrieve unresolved CrnExceptionFacts for
     * @throws DataAccessException
     */
    public List<CrnExceptionFact> getUnresolvedExceptionFacts(int exceptionId) throws DataAccessException {
        return exceptionDao.getUnresolvedExceptionFacts(exceptionId);
    }

    public List<ResolutionWithFacts> getResolutions(int stationId, int datetimeId, ExceptionReapply reapply) {
        return exceptionDao.getResolutionWithFacts(stationId, datetimeId, reapply);
    }

    public List<ResolutionWithFacts> getResolutions(int stationId, int beginDt, int endDt, ExceptionReapply reapply) {
        return exceptionDao.getResolutionWithFacts(stationId, beginDt, endDt, reapply);
    }

    /**
     * Inserts a new CrnException and its associated CrnExceptionFacts into the database.
     * @param exception The CrnException to insert
     * @param facts The associated CrnExceptionFacts to insert
     * @throws DataAccessException
     */
    @Transactional
    public void insertException(CrnException exception, Collection<CrnExceptionFact> facts) throws DataAccessException {
        exceptionDao.insertException(exception);
        insertExceptionFacts(facts);
    }

    /**
     * Inserts facts for an existing CrnException
     * @param facts The associated CrnExceptionFacts to insert
     * @throws DataAccessException
     */
    @Transactional
    public void insertExceptionFacts(Collection<CrnExceptionFact> facts) throws DataAccessException {
        Preconditions.checkNotNull(facts, "facts collection not nullable");
        for (CrnExceptionFact fact : facts) {
            exceptionDao.insertExceptionFact(fact);
        }
    }

    /**
     * Updates a CrnException's status.
     * @param exception The CrnException to update
     * @throws DataAccessException
     */
    @Transactional
    public void updateExceptionStatus(CrnException exception) throws DataAccessException {
        Preconditions.checkNotNull(exception, "exception not nullable");
        exceptionDao.updateExceptionStatus(exception);
    }

    /**
     * Inserts resolved changes (ExceptionResolutionFacts) associated with a CrnException. NOTE: It is assumed these
     * changes are applied at the time the resolutions are inserted. The database is modified to indicate the
     * resolutions have been applied at the time of insert.
     * @param facts The facts to insert
     * @throws DataAccessException
     */
    @Transactional
    public void insertExceptionResolutionFacts(Collection<ExceptionResolutionFact> facts) throws DataAccessException {
        Preconditions.checkNotNull(facts, "facts collection not nullable");
        exceptionDao.insertExceptionResolutionFacts(facts);
    }

    /**
     * Inserts an ExceptionResolution into the database.
     * @param resolution
     * @throws DataAccessException
     */
    @Transactional
    public void insertExceptionResolution(ExceptionResolution resolution) throws DataAccessException {
        Preconditions.checkNotNull(resolution, "resolution not nullable");
        exceptionDao.insertExceptionResolution(resolution);
    }

    /**
     * Updates facts and flags with new data and updates timestamps in fact, flag, and observation tables. Calculated
     * values can be deleted, but observed values can only be updated or inserted. TODO see if can use merge instead of
     * update/delete
     * @param elementValues The values to be updated. If a calculated fact should be deleted, ElementValue.value should
     * be null.
     */
    @Transactional
    public void updateFacts(Collection<ElementValue> elementValues) {
        Preconditions.checkNotNull(elementValues, "elementValues not nullable");
        observationDao.updateElementValues(elementValues);
    }

}
