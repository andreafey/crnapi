package gov.noaa.ncdc.crn.dao;

import gov.noaa.ncdc.crn.domain.POR;

import java.util.Collection;
import java.util.Map;

import org.springframework.dao.DataAccessException;

public interface PorDao {

    /**
     * Retrieves POR information for all stations in the database mapped by stationId
     * @return Map<Integer,POR> mapping each stationId to its POR
     * @throws DataAccessException (unchecked)
     */
    public abstract Map<Integer, POR> getPor() throws DataAccessException;

    /**
     * Retrieves POR information for some stations in the database mapped by stationId
     * @param stationIds the stations to retrieve
     * @return Map<Integer,POR> mapping each stationId to its POR
     * @throws DataAccessException (unchecked)
     */
    public abstract Map<Integer, POR> getPor(Collection<Integer> stationIds) throws DataAccessException;

    /**
     * Retrieves POR information for a single station in the database
     * @return POR for a single station
     * @throws DataAccessException (unchecked)
     */
    public abstract POR getPor(int stationId) throws DataAccessException;

    /**
     * Updates the POR for a station if this value contains an end date later than the existing one or a start date
     * earlier than the existing one.
     * @param por The up-to-date POR
     * @return true if the row was successfully updated
     * @throws DataAccessException (unchecked)
     */
    public abstract boolean updatePor(POR por) throws DataAccessException;

    /**
     * Inserts a POR when one does not already exist for a station
     * @param por The POR to insert
     * @throws DataAccessException (unchecked)
     */
    public abstract void insertPor(POR por) throws DataAccessException;
}
