package gov.noaa.ncdc.crn.dao.mybatis;

import gov.noaa.ncdc.crn.dao.StationDao;
import gov.noaa.ncdc.crn.domain.Station;
import gov.noaa.ncdc.crn.domain.StationRainGauge;
import gov.noaa.ncdc.crn.persistence.StationMapper;
import gov.noaa.ncdc.crn.util.TimeUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.TreeMultimap;

@Repository
public class StationDaoImpl implements StationDao {
    @Autowired
    private StationMapper mapper;
    private static Log LOG = LogFactory.getLog(StationDaoImpl.class);

    @Override
    public Map<Integer, Station> getStations() throws DataAccessException {
        return mapper.selectStations(false);
    }

    @Override
    public Map<Integer, Station> getStations(final boolean includeSilent) throws DataAccessException {
        return mapper.selectStations(includeSilent);
    }

    @Override
    public Map<String, Station> getWbanStationMap() throws DataAccessException {
        return mapper.selectWbanStations();
    }

    @Override
    public Station getStationFromParams(final Map<String, Object> params) throws DataAccessException {
        return mapper.selectStation(params);
    }

    @Override
    public Map<Integer, Station> getStationsFromParams(final Map<String, Object> params) throws DataAccessException {
        return mapper.selectStations(params);
    }

    @Override
    public Station getStation(final int stationId) throws DataAccessException {
        return mapper.selectStation(stationId);
    }

    @Override
    public Map<Integer, Station> getStationsCurrentlyWithSmSt() throws DataAccessException {
        return mapper.selectStationsCurrentlyWithSmSt();
    }

    @Override
    public String getLastModForStation(final int stationId, final int begin, final int end) throws DataAccessException {
        return mapper.selectLastModForStation(stationId, begin, end);
    }

    @Override
    public Float getGeonorCapacity(final int stationId) throws DataAccessException {
        BigDecimal capacity = mapper.selectGeonorCapacity(stationId);
        return capacity != null ? capacity.floatValue() : null;
    }

    @Override
    public Integer getStationRainGaugeDepth(int stationId, int datetimeId) {
        TreeMultimap<Integer, StationRainGauge> map = getStationRainGaugeMap();
        Collection<StationRainGauge> gauges = map.get(stationId);
        StationRainGauge gauge = null;
        if (gauges == null || gauges.size() == 0) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(stationId + ": no gauges installed at this station");
            }
            return null;
        } else if (gauges.size() == 1) {
            gauge = gauges.iterator().next();
            if (LOG.isTraceEnabled()) {
                LOG.trace(stationId + ": only one rain gauge installed");
            }
        } else {
            // the requested calendar time for comparison
            final Calendar requestDate = TimeUtils.computeCalendarDate(datetimeId);
            Collection<StationRainGauge> preceding = Collections2.filter(gauges, new Predicate<StationRainGauge>() {
                @Override
                public boolean apply(StationRainGauge gauge) {
                    return !requestDate.getTime().before(gauge.getEventDate());
                }
            });
            // since the Guava filter is lazily evaluated, want to access it just once
            List<StationRainGauge> precedingList = new ArrayList<>(preceding);
            // get the last gauge which precedes the request date
            // or the first in the original list if none precedes
            if (precedingList.size() == 0) {
                // request date is before first gauge installation; use first gauge and log warning
                gauge = gauges.iterator().next();
                if (LOG.isWarnEnabled()) {
                    LOG.warn(String.format("%2$d: request date [%1$tY%1$tm%1$td] before any gauge installations",
                            requestDate, stationId));
                }
            } else if (precedingList.size() == gauges.size()) {
                // request date is after last gauge was installed; use last gauge installed
                gauge = precedingList.get(precedingList.size() - 1);
                if (LOG.isTraceEnabled()) {
                    LOG.trace(String.format("%2$d: request date [%1$tY%1$tm%1$td] after last gauge installation",
                            requestDate, stationId));
                }
            } else {
                // request date is between two gauge installations; use the last one which precedes it
                gauge = precedingList.get(precedingList.size() - 1);
                if (LOG.isTraceEnabled()) {
                    LOG.trace(String.format("%2$d: request date [%1$tY%1$tm%1$td] between gauge installations",
                            requestDate, stationId));
                }
            }
        }
        return gauge.getDepth();
    }

    private Collection<StationRainGauge> getStationGauges() throws DataAccessException {
        return mapper.selectRainGaugeDepths();
    }

    private TreeMultimap<Integer, StationRainGauge> getStationRainGaugeMap() {
        Collection<StationRainGauge> gaugeMap = getStationGauges();
        return transformGaugeMap(gaugeMap);
    }

    private TreeMultimap<Integer, StationRainGauge> transformGaugeMap(Collection<StationRainGauge> gauges) {
        TreeMultimap<Integer, StationRainGauge> mm = TreeMultimap.create();
        for (StationRainGauge gauge : gauges) {
            mm.put(gauge.getStationId(), gauge);
        }
        return mm;
    }

}
