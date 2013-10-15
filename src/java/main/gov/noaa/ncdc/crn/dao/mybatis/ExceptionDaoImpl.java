package gov.noaa.ncdc.crn.dao.mybatis;

import gov.noaa.ncdc.crn.dao.ExceptionDao;
import gov.noaa.ncdc.crn.domain.CrnException;
import gov.noaa.ncdc.crn.domain.CrnExceptionFact;
import gov.noaa.ncdc.crn.domain.ExceptionReapply;
import gov.noaa.ncdc.crn.domain.ExceptionResolution;
import gov.noaa.ncdc.crn.domain.ExceptionResolutionFact;
import gov.noaa.ncdc.crn.domain.ExceptionTicketSystem;
import gov.noaa.ncdc.crn.domain.Exceptions;
import gov.noaa.ncdc.crn.domain.ResolutionWithFacts;
import gov.noaa.ncdc.crn.persistence.ExceptionMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Iterables;

@Repository
public class ExceptionDaoImpl implements ExceptionDao {

    private static final int THROTTLE = 100;

    @Autowired
    private ExceptionMapper mapper;

    @Override
    public CrnException getException(final int exceptionId) throws DataAccessException {
        return mapper.selectException(exceptionId);
    }

    @Override
    public List<CrnExceptionFact> getExceptionFacts(final Map<String, Object> params) throws DataAccessException {
        return mapper.selectExceptionFacts(params);
    }

    @Override
    public void insertException(CrnException exception) throws DataAccessException {
        mapper.insertException(exception);
    }

    @Override
    public void insertExceptionFact(CrnExceptionFact fact) throws DataAccessException {
        mapper.insertExceptionFact(fact);
    }

    @Override
    public void updateExceptionStatus(final CrnException exception) throws DataAccessException {
        mapper.updateExceptionStatus(exception);
    }

    @Override
    public Collection<CrnException> getExceptionsFromTicket(final ExceptionTicketSystem system, final String ticketId)
            throws DataAccessException {
        return mapper.selectExceptionsFromTicket(system, ticketId);
    }

    @Override
    public void insertExceptionResolution(ExceptionResolution resolution) {
        mapper.insertExceptionResolution(resolution);
    }

    @Override
    public List<ResolutionWithFacts> getResolutionWithFacts(final int stationId, int datetimeId,
            ExceptionReapply reapply) throws DataAccessException {
        List<ExceptionResolution> resolutions = mapper.selectExceptionResolutions(stationId, datetimeId, reapply);
        List<ExceptionResolutionFact> facts = mapper.selectExceptionResolutionFacts(stationId, datetimeId, reapply);
        return addFactsToResolutions(resolutions, facts);
    }

    @Override
    public List<ResolutionWithFacts> getResolutionWithFacts(final int stationId, final int beginDt, final int endDt,
            final ExceptionReapply reapply) throws DataAccessException {
        List<ExceptionResolution> resolutions = mapper.selectExceptionResolutions(stationId, beginDt, endDt, reapply);
        List<ExceptionResolutionFact> facts = mapper.selectExceptionResolutionFacts(stationId, beginDt, endDt, reapply);
        return addFactsToResolutions(resolutions, facts);
    }

    @Override
    public List<CrnExceptionFact> getUnresolvedExceptionFacts(final Collection<Integer> stationIds,
            final int beginDatetimeId, final int endDatetimeId, final int... elementIds) {
        return mapper.selectUnresolvedExceptionFacts(stationIds, beginDatetimeId, endDatetimeId, elementIds);
    }

    @Override
    public List<CrnExceptionFact> getUnresolvedExceptionFacts(int exceptionId) throws DataAccessException {
        return mapper.selectUnresolvedExceptionFacts(exceptionId);
    }

    private List<ResolutionWithFacts> addFactsToResolutions(List<ExceptionResolution> resolutions,
            List<ExceptionResolutionFact> facts) {
        List<ResolutionWithFacts> resWithFacts = new ArrayList<>();
        for (ExceptionResolution res : resolutions) {
            // filter facts based on resolution id
            Collection<ExceptionResolutionFact> filteredFacts = Exceptions.filterResolutionFactsForResolution(facts,
                    res.getResolutionId());
            resWithFacts.add(new ResolutionWithFacts(res, filteredFacts));
        }
        return resWithFacts;
    }

    @Override
    public void insertExceptionResolutionFacts(Collection<ExceptionResolutionFact> facts) throws DataAccessException {
        /* break into partitions to avoid max cursors when collection is large */
        Iterable<List<ExceptionResolutionFact>> partitions = Iterables.partition(facts, THROTTLE);
        for (List<ExceptionResolutionFact> partition : partitions) {
            for (ExceptionResolutionFact fact : partition) {
                mapper.insertExceptionResolutionFact(fact);
            }
        }
    }

}
