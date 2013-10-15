package gov.noaa.ncdc.crn.dao.mybatis;

import static gov.noaa.ncdc.crn.domain.CrnDomains.DATETIME_ID;
import gov.noaa.ncdc.crn.dao.DatetimeDao;
import gov.noaa.ncdc.crn.domain.Datetime;
import gov.noaa.ncdc.crn.persistence.DatetimeMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;

@Repository
public class DatetimeDaoImpl implements DatetimeDao {

    @Autowired
    private DatetimeMapper mapper;

    @Override
    public Integer getDatetimeId(final String YYYYMMDDHH24) throws DataAccessException {
        return mapper.selectDatetimeIds(YYYYMMDDHH24);
    }

    @Override
    public Datetime getDatetime(final String YYYYMMDDHH24) throws DataAccessException {
        return mapper.selectDatetimes(YYYYMMDDHH24);
    }

    @Override
    public List<Integer> getDatetimeIds(final String beginYYYYMMDDHH24, final String endYYYYMMDDHH24)
            throws DataAccessException {
        int begin = mapper.selectDatetimeIds(beginYYYYMMDDHH24);
        int end = mapper.selectDatetimeIds(endYYYYMMDDHH24);
        int curr = begin;
        List<Integer> ids = new ArrayList<>();
        while (curr <= end) {
            ids.add(curr++);
        }
        return ids;
    }

    @Override
    public List<Datetime> getDatetimeList(final String beginYYYYMMDDHH24, final String endYYYYMMDDHH24)
            throws DataAccessException {
        Datetime begin = mapper.selectDatetimes(beginYYYYMMDDHH24);
        Datetime curr = begin;
        List<Datetime> datetimes = new ArrayList<>();
        while (curr.getDatetime0_23().compareTo(endYYYYMMDDHH24) <= 0) {
            datetimes.add(curr);
            curr = curr.next();
        }
        return datetimes;
    }

    private List<Datetime> getDatetimeList(final int beginId, final int endId) throws DataAccessException {
        Datetime begin = mapper.selectDatetimes(beginId);
        Datetime curr = begin;
        List<Datetime> datetimes = new ArrayList<>();
        while (curr.getDatetimeId() <= endId) {
            datetimes.add(curr);
            curr = curr.next();
        }
        return datetimes;
    }

    @Override
    public Map<Integer, Datetime> getDatetimeMap(final String beginYYYYMMDDHH24, final String endYYYYMMDDHH24)
            throws DataAccessException {
        List<Datetime> datetimes = getDatetimeList(beginYYYYMMDDHH24, endYYYYMMDDHH24);
        return Maps.uniqueIndex(datetimes, DATETIME_ID);
    }

    @Override
    public Datetime getDatetime(final int datetimeId) throws DataAccessException {
        return mapper.selectDatetimes(datetimeId);
    }

    @Override
    public Map<Integer, Datetime> getDatetimeMap(final Integer beginDatetimeId, Integer endDatetimeId)
            throws DataAccessException {
        List<Datetime> datetimes = getDatetimeList(beginDatetimeId, endDatetimeId);
        return Maps.uniqueIndex(datetimes, DATETIME_ID);
    }

    @Override
    public Map<Integer, Datetime> getDatetimeMap(final Collection<Integer> datetimeIds) throws DataAccessException {
        return mapper.selectDatetimes(datetimeIds);
    }

}
