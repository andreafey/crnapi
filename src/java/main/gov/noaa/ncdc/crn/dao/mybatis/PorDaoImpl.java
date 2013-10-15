package gov.noaa.ncdc.crn.dao.mybatis;

import gov.noaa.ncdc.crn.dao.PorDao;
import gov.noaa.ncdc.crn.domain.POR;
import gov.noaa.ncdc.crn.persistence.PorMapper;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.google.common.base.Predicates;
import com.google.common.collect.Maps;

@Repository
public class PorDaoImpl implements PorDao {

    @Autowired
    private PorMapper mapper;

    @Override
    public Map<Integer, POR> getPor() throws DataAccessException {
        return mapper.getPor();
    }

    @Override
    public Map<Integer, POR> getPor(final Collection<Integer> stationIds) throws DataAccessException {
        return Maps.filterKeys(getPor(), Predicates.in(stationIds));
    }

    @Override
    public POR getPor(final int stationId) throws DataAccessException {
        return getPor().get(stationId);
    }

    @Override
    public boolean updatePor(final POR por) throws DataAccessException {
        mapper.updatePor(por);
        return true;
    }

    @Override
    public void insertPor(final POR por) throws DataAccessException {
        mapper.insertPor(por);
    }

}
