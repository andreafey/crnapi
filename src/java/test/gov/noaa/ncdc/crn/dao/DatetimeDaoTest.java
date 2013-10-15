package gov.noaa.ncdc.crn.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import gov.noaa.ncdc.crn.domain.Datetime;
import gov.noaa.ncdc.crn.persistence.DatetimeMapper;
import gov.noaa.ncdc.crn.util.TimeUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations={"classpath:application-context.xml"})
@ActiveProfiles(profiles="unittest")
public class DatetimeDaoTest {
    @Autowired
    private DatetimeDao datetimeDao;
    @Autowired
    private DatetimeMapper mapper;
    @Autowired
    private DataSource dataSource;  
    private final static long HOUR_MILLIS = 60*60*1_000;

    /** TODO need to add handling of cases where too much data is requested */
    /** TODO document what this tests */
    @Test
    public final void testDatetimeDiffs() { 
        List<Datetime> datetimes =
            datetimeDao.getDatetimeList("2008010101", "2008010102");
        String dt1 = datetimes.get(0).getDatetime0_23();
        String dt2 = datetimes.get(1).getDatetime0_23();
        Calendar utcCal1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utcCal1.set(new Integer(dt1.substring(0,4)), 
            new Integer(dt1.substring(4,6))-1, new Integer(dt1.substring(6,8)), 
            new Integer(dt1.substring(8,10)), 0, 0);
        utcCal1.set(Calendar.MILLISECOND, 0);
        Calendar utcCal2 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utcCal2.set(new Integer(dt2.substring(0,4)), 
            new Integer(dt2.substring(4,6))-1, new Integer(dt2.substring(6,8)),
            new Integer(dt2.substring(8,10)), 0, 0);
        utcCal2.set(Calendar.MILLISECOND, 0);
        long diff = utcCal2.getTimeInMillis()-utcCal1.getTimeInMillis();
        assertEquals(HOUR_MILLIS, diff);
        String dt3 = datetimeDao.getDatetime("2008123101").getDatetime0_23();
        Calendar utcCal3 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utcCal3.set(new Integer(dt3.substring(0,4)), 
            new Integer(dt3.substring(4,6))-1, 
            new Integer(dt3.substring(6,8)), 
            new Integer(dt3.substring(8,10)), 0, 0);
        utcCal3.set(Calendar.MILLISECOND, 0);
        diff = utcCal3.getTimeInMillis()-utcCal1.getTimeInMillis();
        int daysDiff = 
            utcCal3.get(Calendar.DAY_OF_YEAR)-utcCal1.get(Calendar.DAY_OF_YEAR);
        long expectedDiff = daysDiff*24*HOUR_MILLIS;
        assertEquals(expectedDiff,diff);
    }

    @Test
    public final void testDatetimeDaoImpl() {
        assertNotNull("datetimeDao not set in app context",datetimeDao);
    }

    @Test
    public final void testGetDatetimeId() {
        Integer datetimeId=-99999;
        try {
            datetimeId=datetimeDao.getDatetimeId("2008030401");
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("Caught exception getting datetimeId");
        }
        assertEquals("datetimeId incorrect",64984,datetimeId.intValue());
        try {
            datetimeId=datetimeDao.getDatetimeId("1999120101");
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("Caught exception getting datetimeId");
        }
        assertNull("datetimeId should be null on 1999120101",datetimeId);
        try {
            datetimeId=datetimeDao.getDatetimeId("2008010101");
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("Caught exception getting datetimeId");
        }
        assertNotNull("Datetime not retrieved for 2008010101 ",datetimeId);
        assertEquals("datetimeId incorrect",63472,datetimeId.intValue());
        try {
            datetimeId=datetimeDao.getDatetimeId("2008010200");
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("Caught exception getting datetimeId");
        }
        assertNotNull("Datetime not retrieved for 2008010200 ",datetimeId);
        assertEquals("datetimeId incorrect",63495,datetimeId.intValue());
        try {
            datetimeId=datetimeDao.getDatetimeId("2008010100");
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("Caught exception getting datetimeId");
        }
        assertNotNull("Datetime not retrieved for 2008010100 ",datetimeId);
        assertEquals("datetimeId incorrect",63471,datetimeId.intValue());
    }
    @Test(expected=DataAccessException.class)
    public final void testGetDatetimeIdException() {
        datetimeDao.getDatetimeId("foo");
    }

    @Test
    public final void testGetDatetimeString() {
        Datetime dt = null;
        try {
            dt=datetimeDao.getDatetime("2008010100");
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("Caught exception getting datetime");
        }
        assertNotNull("didn't get a datetime",dt);
        assertEquals("Datetime id incorrect",63471,dt.getDatetimeId());
        // datestring gest converted
        assertEquals("Datetime string incorrect",
            "2008010100",dt.getDatetime0_23());
    }

    @Test
    public final void testGetDatetimeIds() {
        List<Integer> datetimeIds = null;
        try {
            datetimeIds=datetimeDao.getDatetimeIds("2008010100","2008010102");
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("Caught exception getting datetimeId");
        }
        assertEquals("wrong list size",3,datetimeIds.size());
        assertEquals("DatetimeId(0) incorrect",
            new Integer(63471),datetimeIds.get(0));
        assertEquals("DatetimeId(1) incorrect",
            new Integer(63472),datetimeIds.get(1));
        assertEquals("DatetimeId(2) incorrect",
            new Integer(63473),datetimeIds.get(2));
    }
    @Test
    public final void testGetDatetimeList() {
        List<Datetime> datetimes = null;
        try {
            datetimes=datetimeDao.getDatetimeList("2008010100","2008010102");
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("Caught exception getting datetimes");
        }
        assertEquals("wrong list size",3,datetimes.size());
        assertEquals("Datetime(0) id incorrect",63471,
            datetimes.get(0).getDatetimeId());
        // datestring gets converted
        assertEquals("Datetime(0) string incorrect",
            "2008010100",datetimes.get(0).getDatetime0_23());
        assertEquals("Datetime(1) id incorrect",
            63472,datetimes.get(1).getDatetimeId());
        assertEquals("Datetime(1) string incorrect",
            "2008010101",datetimes.get(1).getDatetime0_23());
        assertEquals("Datetime(2) id incorrect",63473,
            datetimes.get(2).getDatetimeId());
        assertEquals("Datetime(2) string incorrect",
            "2008010102",datetimes.get(2).getDatetime0_23());
        try {
            datetimes=datetimeDao.getDatetimeList("2008010101","2008030100");
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("Caught exception getting datetimes");
        }
        // expect 1440 hours for 1st 2 months, .4 s
        assertEquals("wrong list size",1440,datetimes.size());
        try {
            datetimes=datetimeDao.getDatetimeList("2008010101","2008070100");
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("Caught exception getting datetimes");
        }
        // 4368 for 6, .8 s
        assertEquals("wrong list size",4368,datetimes.size());
        try {
            datetimes=datetimeDao.getDatetimeList("2008010101","2009010100");
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("Caught exception getting datetimes");
        }
        // 8784 for 12, 1.4 s
        assertEquals("wrong list size",8784,datetimes.size());
    }

    @Test
    public final void testGetDatetimeMapStringString() {
            Map<Integer,Datetime> datetimes = null;
            try {
                datetimes=datetimeDao.getDatetimeMap("2008010100","2008010102");
            } catch (DataAccessException e) {
                e.printStackTrace();
                fail("Caught exception getting datetimes");
            }
            assertEquals("wrong map size",3,datetimes.size());
            assertEquals("Datetime(63471) id incorrect",
                63471,datetimes.get(63471).getDatetimeId());
            // datestring gets converted
            assertEquals("Datetime(63471) string incorrect",
                "2008010100",datetimes.get(63471).getDatetime0_23());
            assertEquals("Datetime(63472) id incorrect",
                63472,datetimes.get(63472).getDatetimeId());
            assertEquals("Datetime(63472) string incorrect",
                "2008010101",datetimes.get(63472).getDatetime0_23());
            assertEquals("Datetime(63473) id incorrect",
                63473,datetimes.get(63473).getDatetimeId());
            assertEquals("Datetime(63473) string incorrect",
                "2008010102",datetimes.get(63473).getDatetime0_23());
    }

    @Test
    public final void testGetDatetimeInt() {
        int testId=63471;
        Datetime datetime = datetimeDao.getDatetime(testId);
        assertEquals("didn't get datetime",testId,datetime.getDatetimeId());
        testId=-1;
        datetime = datetimeDao.getDatetime(testId);
        assertNull("should get null datetime",datetime);
    }

    @Test
    public final void testGetDatetimeMapIntegerInteger() {
        Integer begin = 63471;
        Integer end = 63473;
        Map<Integer,Datetime> datetimes = null;
        try {
            datetimes=datetimeDao.getDatetimeMap(begin,end);
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("Caught exception getting datetimes");
        }
        assertEquals("wrong map size",3,datetimes.size());
        assertEquals("Datetime(63471) id incorrect",
            63471,datetimes.get(63471).getDatetimeId());
        assertEquals("Datetime(63472) id incorrect",
            63472,datetimes.get(63472).getDatetimeId());
        assertEquals("Datetime(63473) id incorrect",
            63473,datetimes.get(63473).getDatetimeId());
        
        // TODO begin and/or end are null
    }

    @Test
    public final void testGetDatetimeMapListOfInteger() {
        Integer ints[] = {63473,63471,63472};
        List<Integer> nums = Arrays.asList(ints);
        Map<Integer,Datetime> datetimes = null;
        try {
            datetimes=datetimeDao.getDatetimeMap(nums);
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("Caught exception getting datetimes from list");
        }
        assertEquals("wrong map size",3,datetimes.size());
        assertEquals("Datetime(63471) id incorrect",
            63471,datetimes.get(63471).getDatetimeId());
        assertEquals("Datetime(63472) id incorrect",
            63472,datetimes.get(63472).getDatetimeId());
        assertEquals("Datetime(63473) id incorrect",
            63473,datetimes.get(63473).getDatetimeId());
    }
    @Test
    @Transactional
    @Rollback(true)
    public final void testDatetimeCache() {
    	int dummyId = -9999;
    	Datetime notthere = datetimeDao.getDatetime(dummyId);
    	assertNull(notthere);
    	
    	// insert new datetime into TABLE (renamed tables and columns)
    	SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("TABLE");
    	Map<String,Object> params = new HashMap<>();
    	params.put("ID", dummyId);
    	params.put("YEAR", 2010);
    	params.put("DOY", 126);
    	params.put("UTCHOUR", 1200);
    	params.put("datetime", "2010050612");
    	params.put("time", TimeUtils.createUTCCalendar("2010050612").getTime());
    	int rows = jdbcInsert.execute(params);
    	assertEquals(1,rows);
    	
    	// verifies that cache was used
    	Datetime stillnotthere = datetimeDao.getDatetime(dummyId);
    	assertNull(stillnotthere);
    	
    	// note this doesn't confirm it's there after the cache is flushed, but 
    	// since this cache gets flushed so rarely, and there
    	// are no needed inserts, I'm skipping that test
    }
}
