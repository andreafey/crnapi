package gov.noaa.ncdc.crn.dao.mybatis;

import gov.noaa.ncdc.crn.dao.QcDao;
import gov.noaa.ncdc.crn.domain.QcDeltaParam;
import gov.noaa.ncdc.crn.domain.QcRangeParam;
import gov.noaa.ncdc.crn.persistence.QcMapper;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

@Repository
public class QcDaoImpl implements QcDao {
    @Autowired
    private QcMapper mapper;

    @Override
    public QcRangeParam getQcRangeParam(final int elementId, final int streamId, final int stationId, final int month)
            throws DataAccessException {
        return getQcRangeParams(streamId, stationId, month).get(elementId);
    }

    @Override
    public Map<Integer, QcRangeParam> getQcRangeParams(final int streamId, final int stationId, final int month)
            throws DataAccessException {
        return mapper.selectRangeParams(streamId, stationId, month);
    }

    @Override
    public QcDeltaParam getQcDeltaParam(final int elementId, final int streamId, final int stationId, final int month)
            throws DataAccessException {
        return getQcDeltaParams(streamId, stationId, month).get(elementId);
    }

    @Override
    public Map<Integer, QcDeltaParam> getQcDeltaParams(final int streamId, final int stationId, final int month)
            throws DataAccessException {
        return mapper.selectDeltaParams(streamId, stationId, month);
    }

}
