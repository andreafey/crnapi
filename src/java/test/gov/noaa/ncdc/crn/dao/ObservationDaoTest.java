package gov.noaa.ncdc.crn.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.noaa.ncdc.crn.domain.Observation;
import gov.noaa.ncdc.crn.domain.StationDate;
import gov.noaa.ncdc.crn.util.TimeUtils;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations={"classpath:application-context.xml"})
@ActiveProfiles(profiles="unittest")
public class ObservationDaoTest {
    @Autowired
    private ObservationDao observationDao;
    @Test
    public final void testObservationDaoImpl() 
    {
        assertNotNull("observationDao not properly constructed",observationDao);
    }

    // not called by ObervationService
    @Test
    public final void testGetObservationsIntIntCollectionInt()  
    {
    	List<Integer> stations = Lists.newArrayList(1026, 1326);
    	
    	// get one hour's worth of data
    	int beginDatetimeId = TimeUtils
    		.computeDateTimeId(TimeUtils.createUTCCalendar("2008050602"));
    	int endDatetimeId = TimeUtils
    		.computeDateTimeId(TimeUtils.createUTCCalendar("2008050602"));
    	Map<StationDate,Observation> obs = 
    		observationDao.getObservations(beginDatetimeId, endDatetimeId, stations);
    	assertNotNull(obs);
    	assertEquals(2,obs.size());

    	// get two hour's worth of data
    	beginDatetimeId = TimeUtils
    		.computeDateTimeId(TimeUtils.createUTCCalendar("2008050602"));
    	endDatetimeId = TimeUtils
    		.computeDateTimeId(TimeUtils.createUTCCalendar("2008050603"));
    	obs =
    		observationDao.getObservations(beginDatetimeId, endDatetimeId, stations);
    	assertEquals(4,obs.size());
    	
    	// get one month's worth of data
    	beginDatetimeId = TimeUtils
    		.computeDateTimeId(TimeUtils.createUTCCalendar("2008050600"));
    	endDatetimeId = TimeUtils
    		.computeDateTimeId(TimeUtils.createUTCCalendar("2008060523"));
    	obs = 
    		observationDao.getObservations(beginDatetimeId, endDatetimeId, stations);
    	assertTrue(obs.size()>(30*24*2));
    	
    	// get one year's worth of data
    	beginDatetimeId = TimeUtils.computeDateTimeId(TimeUtils.createUTCCalendar("2008010100"));
    	endDatetimeId = TimeUtils.computeDateTimeId(TimeUtils.createUTCCalendar("2008123123"));
    	obs = observationDao.getObservations(beginDatetimeId, endDatetimeId, stations);
    	assertTrue(obs.size()>(360*24*2));
    }
	
//	Not called by ObservationService
    @Test
    @DirtiesContext
    @Rollback(true)
    @Transactional
    public void testUpdateTimeExportedToIsdIntInt() {
    	/* updating an existing ob causes 1 return and timestamp change */
        int datetimeId = 63500;
        int stationId = 1326;
    	// verify ob exists in database
    	Observation ob = observationDao.getObservation(datetimeId, stationId);
    	assertNotNull("expected existing Observation", ob);
    	// update last mod
    	observationDao.updateTimeExportedToIsd(datetimeId, stationId);
    	// verify timestamp differs but station/datetime the same
    	Observation compare = 
    			observationDao.getObservation(datetimeId, stationId);
    	assertEquals("stationId differs",
    			ob.getStationId(),compare.getStationId());
    	assertEquals("datetimeId differs",
    			ob.getDatetimeId(),compare.getDatetimeId());
    	assertTrue("expect different timestamps",
    			ob.getTimeExportedToIsd().before(compare.getTimeExportedToIsd()));
    	
    	/* validate updating a nonexistent ob causes 0 return */
    	datetimeId = 12345;
    	stationId = -9999;
    	// verify ob not in database
    	ob = observationDao.getObservation(datetimeId, stationId);
    	assertNull(ob);
    	// this is a no-op
    	observationDao.updateTimeExportedToIsd(datetimeId, stationId);
    	// success b/c no exception thrown
    }
//	Not called by ObservationService
    @Test
    @DirtiesContext
    @Rollback(true)
    @Transactional
    public void testUpdateTimeExportedToIsdCollection() {
    	/* updating an existing ob causes timestamp change */
        int datetimeId = 63500;
        int stationId = 1326;
    	// verify ob exists in database
    	Observation ob = observationDao.getObservation(datetimeId, stationId);
    	assertNotNull("expected existing Observation", ob);
    	// update last mod
    	observationDao.updateTimeExportedToIsd(Lists.newArrayList(ob));
    	// verify timestamp differs but station/datetime the same
    	Observation compare = 
    			observationDao.getObservation(datetimeId, stationId);
    	assertEquals("stationId differs",
    			ob.getStationId(),compare.getStationId());
    	assertEquals("datetimeId differs",
    			ob.getDatetimeId(),compare.getDatetimeId());
    	assertTrue("expect different timestamps",
    			ob.getTimeExportedToIsd().before(compare.getTimeExportedToIsd()));;
    }
}

