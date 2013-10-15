package gov.noaa.ncdc.crn.service;

import gov.noaa.ncdc.crn.dao.PorDao;
import gov.noaa.ncdc.crn.domain.POR;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class PorService {
    @Autowired
    PorDao porDao;

    /**
     * Retrieves POR information for all stations in the database mapped by stationId
     * @return Map{@code <Integer,POR>} mapping each stationId to its POR
     * @throws DataAccessException (unchecked)
     */
    public Map<Integer, POR> getPor() throws DataAccessException {
        return porDao.getPor();
    }

    /**
     * Retrieves POR information for some stations in the database mapped by stationId
     * @param stationIds the stations to retrieve
     * @return Map{@code <Integer,POR>} mapping each stationId to its POR
     * @throws DataAccessException (unchecked)
     */
    public Map<Integer, POR> getPor(Collection<Integer> stationIds) throws DataAccessException {
        return porDao.getPor(stationIds);
    }

    /**
     * Retrieves POR information for a single station in the database
     * @return POR for a single station
     * @throws DataAccessException (unchecked)
     */
    public POR getPor(int stationId) throws DataAccessException {
        return porDao.getPor(stationId);
    }

    /**
     * Updates the POR for a station if this value contains an end date later than the existing one or a start date
     * earlier than the existing one.
     * @param por The up-to-date POR
     * @throws DataAccessException (unchecked)
     */
    public void updatePor(POR por) throws DataAccessException {
        porDao.updatePor(por);
    }

    /**
     * Inserts a POR when one does not already exist for a station
     * @param por The POR to insert
     * @throws DataAccessException (unchecked)
     */
    public void insertPor(POR por) throws DataAccessException {
        porDao.insertPor(por);
    }

}
