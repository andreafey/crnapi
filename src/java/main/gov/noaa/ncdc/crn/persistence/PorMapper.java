package gov.noaa.ncdc.crn.persistence;

import gov.noaa.ncdc.crn.domain.POR;

import java.util.Map;

import org.apache.ibatis.annotations.MapKey;
import org.springframework.dao.DataAccessException;

public interface PorMapper {

    /**
     * Retrieves POR information for all stations in the database mapped by stationId
     * @return Map{@code <String,POR>} mapping each stationId to its POR
     * @throws DataAccessException (unchecked)
     */
    @MapKey("stationId")
    public abstract Map<Integer, POR> getPor() throws DataAccessException;

    /**
     * Updates the POR for a station if this value contains an end date later than the existing one.
     * @param por The up-to-date POR
     * @throws DataAccessException (unchecked)
     */
    public abstract void updatePor(final POR por) throws DataAccessException;

    /**
     * Inserts a POR when one does not already exist for a station
     * @param por The POR to insert
     * @throws DataAccessException (unchecked)
     */
    public abstract void insertPor(final POR por) throws DataAccessException;
}
