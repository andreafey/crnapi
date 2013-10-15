package gov.noaa.ncdc.crn.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.noaa.ncdc.crn.dao.DatetimeDao;
import gov.noaa.ncdc.crn.domain.Datetime;

import java.sql.Date;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations={"classpath:application-context.xml"})
@ActiveProfiles(profiles="unittest")
public class TimeUtilsTest {

    private static final Log LOGGER = LogFactory.getLog(TimeUtilsTest.class);
    private Calendar cal;
    private final String datetime = "200802031500";
    private final String expectedDay = datetime.substring(0, 8);
    private Calendar calMidnight;
    private final String datetimeMidnight = "200901010000";
    private final String expectedDayMidnight = "20081231";
    @Autowired
    private DatetimeDao datetimeDao;

    @Before
    public void setUp() {
        cal = Calendar.getInstance();
        cal.set(new Integer(datetime.substring(0,4)),
                new Integer(datetime.substring(4,6))-1,
                new Integer(datetime.substring(6,8)),
                new Integer(datetime.substring(8,10)), 0);
        calMidnight = Calendar.getInstance();
        calMidnight.set(new Integer(datetimeMidnight.substring(0,4)),
                new Integer(datetimeMidnight.substring(4,6))-1,
                new Integer(datetimeMidnight.substring(6,8)),
                new Integer(datetimeMidnight.substring(8,10)), 0);
    }
    @Test
    public final void testChangeTimeZone() {
        TimeZone newYork = TimeZone.getTimeZone("America/New York");
        Calendar begin = Calendar.getInstance(newYork);
        Calendar result = TimeUtils.changeTimeZone(begin, "UTC");
        assertEquals("time moment should not have changed",
                begin.getTimeInMillis(), result.getTimeInMillis());
        assertFalse("expected different time zone",
                begin.getTimeZone().equals(result.getTimeZone()));

        result = TimeUtils.changeTimeZone(begin, "America/New York");
        assertEquals("calendars s/b identical", begin, result);
        assertEquals("time zones s/b identical",
                begin.getTimeZone(), result.getTimeZone());
    }
    @Test
    public final void testConvertToUTC() {
        String start = "2009050614";
        String expected = "2009050618";
        String timeZoneID = "US/Eastern";
        String result = TimeUtils.convertToUTC(start, timeZoneID);
        assertEquals("start: "+start+", timeZone: "+timeZoneID, expected,result);
        start = "2009030610";
        expected = "2009030615";
        result = TimeUtils.convertToUTC(start, timeZoneID);
        assertEquals("start: "+start+", timeZone: "+timeZoneID, expected,result);
    }
    @Test
    public final void testConvertLSTToUTC() {
        String start = "2009050614";
        String expected = "2009050619";
        String timeZoneID = "US/Eastern";
        String result = TimeUtils.convertLSTToUTC(start, timeZoneID);
        assertEquals("start: "+start+", timeZone: "+timeZoneID, expected,result);
        start = "2009030610";
        expected = "2009030615";
        result = TimeUtils.convertLSTToUTC(start, timeZoneID);
        assertEquals("start: "+start+", timeZone: "+timeZoneID, expected,result);
    }
    // TODO new tests
    // convertLSTToUTC

    @Test
    public final void testConvertFromUTC() {
        String testDate = "2009050618";
        String tz = "US/Eastern";
        String expected = "2009050614";
        String result = TimeUtils.convertFromUTC(testDate, tz);
        assertEquals("incorrect LDT during daylight saving time", expected, result);
        testDate = "2009010619";
        expected = "2009010614";
        result = TimeUtils.convertFromUTC(testDate, tz);
        assertEquals("incorrect LST during standard time", expected, result);
    }
    @Test
    public final void testConvertLSTFromUTC() {
        String testDate = "2009050619";
        String tz = "US/Eastern";
        String expected = "2009050614";
        String result = TimeUtils.convertLSTFromUTC(testDate, tz);
        assertEquals("incorrect LST during daylight saving time", expected, result);
        testDate = "2009010619";
        expected = "2009010614";
        result = TimeUtils.convertLSTFromUTC(testDate, tz);
        assertEquals("incorrect LST during standard time", expected, result);
    }
    @Test
    public final void testGetYYYYMMDDHH24mm() {
        String formatted = TimeUtils.getYYYYMMDDHH24mm(cal);
        int year = new Integer(formatted.substring(0,4));
        int month = new Integer(formatted.substring(4,6));
        int day = new Integer(formatted.substring(6,8));
        int expected = cal.get(Calendar.YEAR);
        assertEquals("year differ",expected,year);
        expected = cal.get(Calendar.MONTH)+1;
        assertEquals("days differ",expected,month);
        expected = cal.get(Calendar.DAY_OF_MONTH);
        assertEquals("days differ",expected,day);
        int hr = new Integer(formatted.substring(8,10));
        expected = cal.get(Calendar.HOUR_OF_DAY);
        assertEquals("days differ",expected,hr);
    }
    @Test
    public final void testGetYYYYMMDDCalendar() {
        String formatted = TimeUtils.getYYYYMMDD(cal);
        int year = new Integer(formatted.substring(0,4));
        int month = new Integer(formatted.substring(4,6));
        int day = new Integer(formatted.substring(6,8));
        int expected = cal.get(Calendar.YEAR);
        assertEquals("year differ",expected,year);
        expected = cal.get(Calendar.MONTH)+1;
        assertEquals("days differ",expected,month);
        expected = cal.get(Calendar.DAY_OF_MONTH);
        assertEquals("days differ",expected,day);

    }
    @Test
    public final void testGetYYYYMMDDHH24Calendar(){
        /* Test converting current time works as expected */
        String formatted = TimeUtils.getYYYYMMDDHH24(cal);
        int year = new Integer(formatted.substring(0,4));
        int month = new Integer(formatted.substring(4,6));
        int day = new Integer(formatted.substring(6,8));
        int expected_yr = cal.get(Calendar.YEAR);
        assertEquals("year differ",expected_yr,year);
        int expected_mo = cal.get(Calendar.MONTH)+1;
        assertEquals("days differ",expected_mo,month);
        int expected_day = cal.get(Calendar.DAY_OF_MONTH);
        assertEquals("days differ",expected_day,day);
        int hr = new Integer(formatted.substring(8,10));
        int expected_hr = cal.get(Calendar.HOUR_OF_DAY);
        assertEquals("days differ",expected_hr,hr);

        /* Test that when using hour=24 for the end of
         * the day, this method still works */
        String yyyyStr = "2010";
        String moStr = "08";
        String dayStr = "31";
        String hour_24 = "24";
        String expected = "2010090100";
        String result = TimeUtils.getYYYYMMDDHH24
                (TimeUtils.createCalendar(yyyyStr+moStr+dayStr+hour_24, "UTC"));
        assertEquals("createCalendar(String,String)",expected,result);
        result = TimeUtils.getYYYYMMDDHH24(
                TimeUtils.createCalendar(Integer.valueOf(yyyyStr),
                        Integer.valueOf(moStr)-1,Integer.valueOf(dayStr),
                        Integer.valueOf(hour_24),0, "UTC"));
        assertEquals("createCalendar(int,int,int,int,int,String)",
                expected,result);
    }
    @Test
    public final void testGetYYYYMMDDHH24IntIntInt() {
        int year = 2009;
        int dayOfYear = 126;
        int hour = 21;
        String expected = "2009050621";
        String result = TimeUtils.getYYYYMMDDHH24(year, dayOfYear, hour);
        assertEquals("bad datestring",expected,result);
        dayOfYear = 146;
        hour = 6;
        expected = "2009052606";
        result = TimeUtils.getYYYYMMDDHH24(year, dayOfYear, hour);
        assertEquals("bad datestring",expected,result);
        dayOfYear = 171;
        hour = 0;
        expected = "2009062000";
        result = TimeUtils.getYYYYMMDDHH24(year, dayOfYear, hour);
        assertEquals("bad datestring",expected,result);
    }
    @Test
    public final void testGetYYYYMMDDDate() {
        Calendar now = Calendar.getInstance();
        Date date = new Date(now.getTimeInMillis());
        String expected = String.format("%1$tY%1$tm%1$td",now);
        String result = TimeUtils.getYYYYMMDD(date);
        assertEquals("did not get expected date format",expected, result);
    }
    @Test
    public final void testCreateCalendarIntIntIntIntString() {
        int year = 2009;
        int mon = 4; // may - 0 indexed
        int day = 6;
        int hour = 21;
        int min = 42;
        Calendar cal = TimeUtils.createCalendar(year,mon,day,hour,min,"UTC");
        assertEquals("bad year",year,cal.get(Calendar.YEAR));
        assertEquals("bad month",mon,cal.get(Calendar.MONTH));
        assertEquals("bad hour",day,cal.get(Calendar.DAY_OF_MONTH));
        assertEquals("bad hour",hour,cal.get(Calendar.HOUR_OF_DAY));
        assertEquals("bad min",min,cal.get(Calendar.MINUTE));
        assertEquals("bad timezone",0,cal.get(Calendar.ZONE_OFFSET));
        cal = TimeUtils.createCalendar(year,mon,day,hour,min,"EST");
        assertEquals("bad year",year,cal.get(Calendar.YEAR));
        assertEquals("bad month",mon,cal.get(Calendar.MONTH));
        assertEquals("bad hour",day,cal.get(Calendar.DAY_OF_MONTH));
        assertEquals("bad hour",hour,cal.get(Calendar.HOUR_OF_DAY));
        assertEquals("bad min",min,cal.get(Calendar.MINUTE));
        // offset to GMT in millis
        assertEquals("bad timezone",-5*60*60*1000,cal.get(Calendar.ZONE_OFFSET));

    }
    @Test
    public final void testCreateCalendarStringString() {
        String expected = "201205061412";
        Calendar cal = TimeUtils.createCalendar(expected, "UTC");
        assertEquals("didn't get correct time coversion",
                expected,TimeUtils.getYYYYMMDDHH24mm(cal));
    }
    @Test
    public final void testCreateUTCCalendarIntIntInt() {
        int year = 2009;
        int dayOfYear = 126;
        int hour = 21;
        Calendar cal = TimeUtils.createUTCCalendar(year,dayOfYear,hour);
        assertEquals("bad year",year,cal.get(Calendar.YEAR));
        assertEquals("bad dayofyear",dayOfYear,cal.get(Calendar.DAY_OF_YEAR));
        assertEquals("bad hour",hour,cal.get(Calendar.HOUR_OF_DAY));
    }
    @Test
    public final void testNowLocal() {
        // yyyymmddhh24mi
        String now = TimeUtils.nowLocal();
        Calendar cal = Calendar.getInstance();
        String expected = String.format("%1$tY%1$tm%1$td%1$tH%1$tM", cal);
        assertEquals("different string representations",expected,now);
    }
    @Test
    public final void testNowLocalCalendar() {
        Calendar nowlocal = TimeUtils.nowLocalCalendar();
        Calendar now = Calendar.getInstance();
        long diff = Math.abs(now.getTimeInMillis()-nowlocal.getTimeInMillis());
        assertTrue("millis differ by more than a sec",
                diff<1000);
        TimeZone tz = nowlocal.getTimeZone();
        assertEquals("timezones differ",
                now.getTimeZone(),tz);
        TimeZone utc = TimeZone.getTimeZone("UTC");
        assertFalse("timezone is utc",utc.equals(tz));
    }
    @Test
    public final void testNowUTC() {
        // yyyymmddhh24mi
        String now = TimeUtils.nowUTC();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        String expected = String.format("%1$tY%1$tm%1$td%1$tH%1$tM", cal);
        assertEquals("different string representations",expected,now);
    }
    @Test
    public final void testNowUTCCalendar() {
        TimeZone utc = TimeZone.getTimeZone("UTC");
        Calendar nowUTC = TimeUtils.nowUTCCalendar();
        Calendar expected = Calendar.getInstance(utc);
        long diff = Math.abs(expected.getTimeInMillis()-nowUTC.getTimeInMillis());
        assertTrue("millis differ by more than a sec", diff<1000);
        TimeZone tz = nowUTC.getTimeZone();
        assertEquals("timezones differ",
                expected.getTimeZone(),tz);
        assertEquals("timezone is utc",utc,tz);
    }
    @Test
    public final void testCreateCalendarDate() {
        Calendar now = TimeUtils.nowUTCCalendar();
        Date date = new Date(now.getTimeInMillis());
        Calendar cal = TimeUtils.createUTCCalendar(date);
        // unit test failed on lwf-d when milliseconds differed by one; modifying
        // to check to the nearest second instead
        // previous error:
        //		calendars differ with conversion: expected Wed Sep 12 19:02:25 UTC 2012, was Wed Sep 12 19:02:25 UTC 2012
        //		expected:<java.util.GregorianCalendar[time=1347476545099,areFieldsSet=true,areAllFieldsSet=true,lenient=true,zone=sun.util.calendar.ZoneInfo[id="UTC",offset=0,dstSavings=0,useDaylight=false,transitions=0,lastRule=null],firstDayOfWeek=1,minimalDaysInFirstWeek=1,ERA=1,YEAR=2012,MONTH=8,WEEK_OF_YEAR=37,WEEK_OF_MONTH=3,DAY_OF_MONTH=12,DAY_OF_YEAR=256,DAY_OF_WEEK=4,DAY_OF_WEEK_IN_MONTH=2,AM_PM=1,HOUR=7,HOUR_OF_DAY=19,MINUTE=2,SECOND=25,MILLISECOND=99,ZONE_OFFSET=0,DST_OFFSET=0]>
        //		 but was:<java.util.GregorianCalendar[time=1347476545100,areFieldsSet=true,areAllFieldsSet=true,lenient=true,zone=sun.util.calendar.ZoneInfo[id="UTC",offset=0,dstSavings=0,useDaylight=false,transitions=0,lastRule=null],firstDayOfWeek=1,minimalDaysInFirstWeek=1,ERA=1,YEAR=2012,MONTH=8,WEEK_OF_YEAR=37,WEEK_OF_MONTH=3,DAY_OF_MONTH=12,DAY_OF_YEAR=256,DAY_OF_WEEK=4,DAY_OF_WEEK_IN_MONTH=2,AM_PM=1,HOUR=7,HOUR_OF_DAY=19,MINUTE=2,SECOND=25,MILLISECOND=100,ZONE_OFFSET=0,DST_OFFSET=0]>

        String expected = String.format("%1$tc", now);
        String result = String.format("%1$tc", cal);
        assertEquals(String.format(
                "calendars differ with conversion: expected %1$tc, was %2$tc",
                now,cal),
                expected,result);
    }
    @Test
    public final void testComputerCalendarDate() {
        Map<Integer,Datetime> map = datetimeDao.getDatetimeMap("2012050100", "2012050623");
        for (int key : map.keySet()) {
            Calendar cal = TimeUtils.computeCalendarDate(key);
            Datetime dt = map.get(key);
            assertEquals(String.format(
                    "times not equal, expected %1$tY%1$tm%1$td, got %2$tY%2$tm%2$td",
                    dt.getUtcCal(),cal),
                    dt.getUtcCal(),cal);
        }
    }

    @Test
    public final void testComputeDateTimeId() {
        Map<Integer,Datetime> map = datetimeDao.getDatetimeMap("2012050100", "2012050623");
        for (int key : map.keySet()) {
            Datetime dt = map.get(key);
            int result = TimeUtils.computeDateTimeId(dt.getUtcCal());
            assertEquals(String.format(
                    "datetimeIs not equal, expected %1$d, got %2$d",
                    key, result),
                    key, result);
        }

    }
    @Test
    public final void testAddHours() {
        int hours = 501;
        Calendar now = Calendar.getInstance();
        Calendar result = TimeUtils.addHours(now, hours);
        assertEquals("didn't get correct number of hours",
                hours,hoursDiff(now,result));
        hours=-97;
        result = TimeUtils.addHours(now, hours);
        assertEquals("didn't get correct number of hours",
                hours,hoursDiff(now,result));
    }
    private int hoursDiff(Calendar before, Calendar after) {
        int millisPerHour = 60*60*1000;
        return
                (int) (after.getTimeInMillis()-before.getTimeInMillis())/millisPerHour;
    }
    @Test
    public final void testGetObservationDayCalendar() {
        String day = TimeUtils.getObservationDay(cal);
        assertEquals("day differs",expectedDay,day);
        day = TimeUtils.getObservationDay(calMidnight);
        assertEquals("midnight day differs",expectedDayMidnight,day);
    }
    @Test
    public final void testGetObservationDayString() {
        String day = TimeUtils.getObservationDay(datetime);
        assertEquals("day differs",expectedDay,day);
        day = TimeUtils.getObservationDay(datetimeMidnight);
        assertEquals("midnight day differs",
                expectedDayMidnight,day);
    }
    @Test
    public final void testGetObservationMonthCalendar() {
        String month = TimeUtils.getObservationMonth(cal);
        assertEquals("month differs",expectedDay.substring(0, 6),month);
        month = TimeUtils.getObservationMonth(calMidnight);
        assertEquals("midnight month differs",
                expectedDayMidnight.substring(0, 6),month);
    }
    @Test
    public final void testGetObservationMonthString()
    {
        String month = TimeUtils.getObservationMonth(datetime);
        assertEquals("month differs",expectedDay.substring(0,6),month);
        month = TimeUtils.getObservationMonth(datetimeMidnight);
        assertEquals("midnight month differs",
                expectedDayMidnight.substring(0,6),month);
    }
    @Test
    public final void testGetObservationMonthInt()
    {
        Integer month = TimeUtils.getObservationMonthInt(datetime);
        assertEquals("month differs",Integer.valueOf(expectedDay.substring(4,6)),month);
        month = TimeUtils.getObservationMonthInt(datetimeMidnight);
        assertEquals("midnight month differs",
                Integer.valueOf(expectedDayMidnight.substring(4,6)),month);
    }
    @Test
    public final void testGetObservationYearCalendar()
    {
        String year = TimeUtils.getObservationYear(cal);
        assertEquals("year differs",expectedDay.substring(0,4),year);
        year = TimeUtils.getObservationYear(calMidnight);
        assertEquals("midnight year differs",
                expectedDayMidnight.substring(0,4),year);
    }
    @Test
    public final void testGetObservationYearString() {
        String year = TimeUtils.getObservationYear(datetime);
        assertEquals("year differs",expectedDay.substring(0,4),year);
        year = TimeUtils.getObservationYear(datetimeMidnight);
        assertEquals("midnight year differs",
                expectedDayMidnight.substring(0,4),year);
    }
    @Test
    public final void testGetCalendar()
    {
        /* TODO throw in some tests with expected millis */
        String date = "200805062104";
        String expected = date;
        Calendar cal = TimeUtils.getCalendar(date);
        assertEquals("1 dates not equal",
                expected, TimeUtils.getYYYYMMDDHH24mm(cal));
        date = "2001";
        expected = "200101010000";
        cal = TimeUtils.getCalendar(date);
        assertEquals("2 dates not equal",
                expected, TimeUtils.getYYYYMMDDHH24mm(cal));
        date = "200305";
        expected = "200305010000";
        cal = TimeUtils.getCalendar(date);
        assertEquals("3 dates not equal",
                expected, TimeUtils.getYYYYMMDDHH24mm(cal));
        date = "20040603";
        expected = "200406030000";
        cal = TimeUtils.getCalendar(date);
        assertEquals("4 dates not equal",
                expected, TimeUtils.getYYYYMMDDHH24mm(cal));
        date = "2002103123";
        expected = "200210312300";
        cal = TimeUtils.getCalendar(date);
        assertEquals("5 dates not equal",
                expected, TimeUtils.getYYYYMMDDHH24mm(cal));
    }
    @Test
    public final void testIsCurrentMonth()
    {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        String date = TimeUtils.getYYYYMMDDHH24mm(now);
        assertTrue("current date s/b current month: "+date,
                TimeUtils.isCurrentMonth(date));
        now.add(Calendar.YEAR, -1);
        date = TimeUtils.getYYYYMMDDHH24mm(now);
        assertFalse("last year shouldn't be current month: "+date,
                TimeUtils.isCurrentMonth(date));
        now.add(Calendar.YEAR, 1);
        now.add(Calendar.MONTH, -1);
        date = TimeUtils.getYYYYMMDDHH24mm(now);
        assertFalse("last month shouldn't be current month: "+date,
                TimeUtils.isCurrentMonth(date));
        now.add(Calendar.MONTH, 2);
        date = TimeUtils.getYYYYMMDDHH24mm(now);
        assertFalse("next month shouldn't be current month: "+date,
                TimeUtils.isCurrentMonth(date));
    }
    @Test
    public final void  testIsCurrentWeek() {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        String date = TimeUtils.getYYYYMMDDHH24mm(now);
        assertTrue("current date s/b current week: "+date,
                TimeUtils.isCurrentWeek(date));
        now.add(Calendar.YEAR, -1);
        date = TimeUtils.getYYYYMMDDHH24mm(now);
        assertFalse("last year shouldn't be current week: "+date,
                TimeUtils.isCurrentWeek(date));
        now.add(Calendar.YEAR, 1);
        now.add(Calendar.MONTH, -1);
        date = TimeUtils.getYYYYMMDDHH24mm(now);
        assertFalse("last month shouldn't be current week: "+date,
                TimeUtils.isCurrentWeek(date));
        now.add(Calendar.MONTH, 2);
        date = TimeUtils.getYYYYMMDDHH24mm(now);
        assertFalse("next month shouldn't be current week: "+date,
                TimeUtils.isCurrentWeek(date));
        now.add(Calendar.MONTH, -1);
        now.add(Calendar.WEEK_OF_YEAR, -1);
        assertFalse("last week shouldn't be current week: "+date,
                TimeUtils.isCurrentWeek(date));
        now.add(Calendar.WEEK_OF_YEAR, 2);
        assertFalse("next week shouldn't be current week: "+date,
                TimeUtils.isCurrentWeek(date));
    }

    @Test
    public final void testIsCurrentYear() {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        String date = TimeUtils.getYYYYMMDDHH24mm(now);
        assertTrue("current date s/b current year: "+date,
                TimeUtils.isCurrentYear(date));
        now.add(Calendar.YEAR, -1);
        date = TimeUtils.getYYYYMMDDHH24mm(now);
        assertFalse("last year shouldn't be current year: "+date,
                TimeUtils.isCurrentYear(date));
        now.add(Calendar.YEAR, 2);
        assertFalse("next year shouldn't be current year: "+date,
                TimeUtils.isCurrentYear(date));
    }
    @Test
    // Note this test failed in Hudson 6/17/10; not sure why; added diffDays test to replicate the time of failure
    /*
Error Message

1. days old wrong expected:<20> but was:<19>

Stacktrace

junit.framework.AssertionFailedError: 1. days old wrong expected:<20> but was:<19>
    at gov.noaa.ncdc.crn.util.TimeUtilsTest.testDaysOld(TimeUtilsTest.java:150)
    at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:74)
    at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:82)
    at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:72)
    at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:240)
    at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61)
    at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70)
    at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:180)

Standard Error

Jun 17, 2010 10:05:56 AM org.springframework.test.context.TestContextManager retrieveTestExecutionListeners
INFO: @TestExecutionListeners is not present for class [class gov.noaa.ncdc.crn.util.TimeUtilsTest]: using defaults.
Jun 17, 2010 10:05:56 AM org.springframework.beans.factory.xml.XmlBeanDefinitionReader loadBeanDefinitions
INFO: Loading XML bean definitions from class path resource [application-context-jdbc.xml]
Jun 17, 2010 10:05:56 AM org.springframework.beans.factory.xml.XmlBeanDefinitionReader loadBeanDefinitions
INFO: Loading XML bean definitions from class path resource [context-properties-jdbc-shared.xml]
Jun 17, 2010 10:05:56 AM org.springframework.beans.factory.xml.XmlBeanDefinitionReader loadBeanDefinitions
INFO: Loading XML bean definitions from class path resource [daos-shared.xml]
Jun 17, 2010 10:05:56 AM org.springframework.context.support.AbstractApplicationContext prepareRefresh
INFO: Refreshing org.springframework.context.support.GenericApplicationContext@25fa1bb6: startup date [Thu Jun 17 10:05:56 EDT 2010]; root of context hierarchy
Jun 17, 2010 10:05:56 AM org.springframework.core.io.support.PropertiesLoaderSupport loadProperties
INFO: Loading properties file from class path resource [jdbc.properties]
Jun 17, 2010 10:05:56 AM org.springframework.beans.factory.support.DefaultListableBeanFactory preInstantiateSingletons
INFO: Pre-instantiating singletons in org.springframework.beans.factory.support.DefaultListableBeanFactory@42bad8a8: defining beans [propertyConfigurer,dataSource,sqlMapClient,stationDao,datetimeDao,elementDao,observationDao,porDao,org.springframework.context.annotation.internalConfigurationAnnotationProcessor,org.springframework.context.annotation.internalAutowiredAnnotationProcessor,org.springframework.context.annotation.internalRequiredAnnotationProcessor,org.springframework.context.annotation.internalCommonAnnotationProcessor]; root of factory hierarchy
     */
    public final void testDaysOld()
    {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        now.add(Calendar.DAY_OF_MONTH, -20);
        String date = TimeUtils.getYYYYMMDDHH24mm(now);
        int expected = 20;
        int days = TimeUtils.daysOld(date);
        assertEquals("1. days old wrong from: "+date,expected,days);
    }
    @Test
    public final void testDiffDays()
    {
        String beginString = "2004040106";
        String endString = "2004040206";
        String beginTz = "PST";
        String endTz = "PST";
        Calendar begin = TimeUtils.createCalendar(beginString, beginTz);
        Calendar end = TimeUtils.createCalendar(endString, endTz);
        long expected = 1L;
        long result = TimeUtils.diffDays(begin, end);
        assertEquals("wrong result between "+
                beginString+beginTz+" and "+endString+endTz,expected,result);

        endString = "2004040306";
        end = TimeUtils.createCalendar(endString, endTz);
        expected = 2L;
        result = TimeUtils.diffDays(begin, end);
        assertEquals("wrong result between "+
                beginString+beginTz+" and "+endString+endTz,expected,result);


        endString = "2004040406";
        endTz = "PDT";
        end = TimeUtils.createCalendar(endString, endTz);
        expected = 3L;
        result = TimeUtils.diffDays(begin, end);
        assertEquals("wrong result between "+
                beginString+beginTz+" and "+endString+endTz,expected,result);

        beginString = "20041031000001";
        beginTz = "PDT";
        endString = "20041031235959";
        endTz = "PST";
        begin = TimeUtils.createCalendar(beginString, beginTz);
        end = TimeUtils.createCalendar(endString, endTz);
        expected = 0L;
        result = TimeUtils.diffDays(begin, end);
        assertEquals("wrong result between "+
                beginString+beginTz+" and "+endString+endTz,expected,result);


        // Setting to June 17, 2010 10:04:13 AM EDT to replicate failed test
        // instance of daysOld() test
        Calendar jun172010 = Calendar.getInstance(TimeZone.getTimeZone("EDT"));
        // 0-indexed month
        jun172010.set(2010, 5, 17, 10, 4, 13);
        Calendar jun172010_less20
        = Calendar.getInstance(TimeZone.getTimeZone("EDT"));
        jun172010_less20.setTimeInMillis(jun172010.getTimeInMillis());
        jun172010_less20.add(Calendar.DAY_OF_MONTH, -20);
        result = TimeUtils.diffDays(jun172010_less20, jun172010);
        expected = 20L;
        assertEquals("expected 20 day diff",expected,result);

        // in recreating the earlier problem, the june 17 represents "current
        // time" and the less_20 represents the current time before a few other
        // calls were made. just trying to see if this affects anything
        jun172010.set(Calendar.MILLISECOND, 999);
        jun172010_less20.set(Calendar.MILLISECOND, 0);
        result = TimeUtils.diffDays(jun172010_less20, jun172010);
        assertEquals("expected 20 day diff",expected,result);
    }
    @Test
    public final void testDiffHours() {
        long expected = 24;
        /** Expect 24 hours noon to noon */
        long result = TimeUtils.diffHours("2013050614", "2013050714");
        assertEquals("Expect 24 hours noon to noon", expected, result);

        /** Expect 24 hours noon to noon over change of year */
        result = TimeUtils.diffHours("2012123114", "2013010114");
        assertEquals("Expect 24 hours noon to noon over change of year", expected, result);

        /** Expect 24 hours noon to noon even when over daylight savings change;
         *  2013 change is Sun, Mar 10 @2:00 AM */
        result = TimeUtils.diffHours("2013030914", "2013031014");
        assertEquals("Expect 24 hours noon to noon even when over daylight savings change", expected, result);
    }
    @Test
    public final void testIsSameWeek() {
        String date1 = "20120430";
        String date2 = "20120505";
        String date3 = "20120507";
        Calendar cal1 = TimeUtils.getCalendar(date1);
        Calendar cal2 = TimeUtils.getCalendar(date2);
        Calendar cal3 = TimeUtils.getCalendar(date3);
        assertTrue("dates s/b in same week: "+date1+","+date2,
                TimeUtils.isSameWeek(cal1, cal2));
        assertFalse("dates s/n/b in same week: "+date2+","+date3,
                TimeUtils.isSameWeek(cal2, cal3));

    }
    @Test
    public final void testIsSameYear()
    {
        String date1 = "20080506";
        String date2 = "20081031";
        Calendar cal1 = TimeUtils.getCalendar(date1);
        Calendar cal2 = TimeUtils.getCalendar(date2);
        assertTrue("dates s/b in same year: "+date1+","+date2,
                TimeUtils.isSameYear(cal1, cal2));
        date2 = "20070506";
        cal2 = TimeUtils.getCalendar(date2);
        assertFalse("dates s/n/b in same year: "+date1+","+date2,
                TimeUtils.isSameYear(cal1, cal2));
    }
    @Test
    public final void testIsSameMonth()
    {
        String date1 = "20080506";
        String date2 = "20081031";
        Calendar cal1 = TimeUtils.getCalendar(date1);
        Calendar cal2 = TimeUtils.getCalendar(date2);
        assertFalse("dates s/n/b in same month: "+date1+","+date2,
                TimeUtils.isSameMonth(cal1, cal2));
        date2 = "20070506";
        cal2 = TimeUtils.getCalendar(date2);
        assertFalse("dates s/n/b in same month: "+date1+","+date2,
                TimeUtils.isSameMonth(cal1, cal2));
        date2 = "20080521";
        cal2 = TimeUtils.getCalendar(date2);
        assertTrue("dates s/b in same month: "+date1+","+date2,
                TimeUtils.isSameMonth(cal1, cal2));
    }

    @Test
    public final void testGetDatestring()
    {
        String date = "2008";
        String expected = "200801010000";
        assertEquals("year test doesn't work: "+date,expected,
                TimeUtils.getDatestring(date));
        date = "200805";
        expected = "200805010000";
        assertEquals("month test doesn't work: "+date,expected,
                TimeUtils.getDatestring(date));
        date = "20080531";
        expected = "200805310000";
        assertEquals("day test doesn't work: "+date,expected,
                TimeUtils.getDatestring(date));
        date = "2008053123";
        expected = "200805312300";
        assertEquals("hour test doesn't work: "+date,expected,
                TimeUtils.getDatestring(date));
        date = "200805312353";
        expected = date;
        assertEquals("min test doesn't work: "+date,expected,
                TimeUtils.getDatestring(date));
        date = "20085";
        assertNull("incorrect date format test doesn't work: "+date,
                TimeUtils.getDatestring(date));
    }
    @Test
    public final void testCreateUTCCalendarString()
    {
        String yyyymmddhh = "2006090411";
        Calendar utcCal = TimeUtils.createUTCCalendar(yyyymmddhh);
        String result = String.format("%1$tY%1$tm%1$td%1$tH", utcCal);
        assertEquals("didn't get correct time from calendar creation",
                yyyymmddhh,result);
        yyyymmddhh = "2006090413";
        utcCal = TimeUtils.createUTCCalendar(yyyymmddhh);
        result = String.format("%1$tY%1$tm%1$td%1$tH", utcCal);
        assertEquals("didn't get correct time from calendar creation",
                yyyymmddhh,result);
        yyyymmddhh = "2006090423";
        utcCal = TimeUtils.createUTCCalendar(yyyymmddhh);
        result = String.format("%1$tY%1$tm%1$td%1$tH", utcCal);
        assertEquals("didn't get correct time from calendar creation",
                yyyymmddhh,result);
        yyyymmddhh = "2006090400";
        utcCal = TimeUtils.createUTCCalendar(yyyymmddhh);
        result = String.format("%1$tY%1$tm%1$td%1$tH", utcCal);
        assertEquals("didn't get correct time from calendar creation",
                yyyymmddhh,result);
    }
    @Test
    public final void testEpoch()
    {
        int[] begins = {950,29504,38499,70132,85439};
        int size = 1000;
        for (int beginDatetimeId : begins) {
            int endDatetimeId = beginDatetimeId+size;
            Map<Integer,Datetime> datetimeMap
            = datetimeDao.getDatetimeMap(beginDatetimeId, endDatetimeId);
            testDatetimeRange(beginDatetimeId, endDatetimeId, datetimeMap);
        }
    }

    private void testDatetimeRange
    (int beginDatetimeId, int endDatetimeId, Map<Integer, Datetime> datetimeMap)
    {
        Datetime datetime = null;
        String result = null;
        Calendar calendar = null;
        int increment = 17;
        for(int datetimeId=beginDatetimeId; datetimeId<endDatetimeId;
                datetimeId+=increment){
            datetime = datetimeMap.get(datetimeId);
            calendar = TimeUtils.computeCalendarDate(datetimeId);
            result = TimeUtils.getYYYYMMDDHH24(calendar);
            assertEquals(datetime.getDatetime0_23(), result);

            int testId = TimeUtils.computeDateTimeId(calendar);
            LOGGER.debug(datetimeId+" = "+datetime.getDatetime0_23()+" = "+
                    result+" = "+testId);
            assertEquals(datetimeId, testId);
        }
    }
    /**
     * Converts a UTC time datestring to a datestring in a *standard* timezone.
     * For example, US/Eastern (standard time) is offset -5 hours from UTC, so
     * convertFromUTC("2009050619","US/Eastern") returns "2009050614"
     * @param yyyymmddhh in UTC
     * @param timeZoneID
     * @return yyyymmddhh in requested time zone
     */
    //    public static String convertFromUTC(String yyyymmddhh, String timeZoneID) {

    @Test
    public final void testGetObservationDayOfYear() {
        Calendar cal = TimeUtils.createUTCCalendar("2009010100");
        int expected = 366;
        int result = TimeUtils.getObservationDayOfYear(cal);
        assertEquals("did not get correct *Observation* day of year: " +
                TimeUtils.getYYYYMMDDHH24(cal), expected, result);
        cal = TimeUtils.createUTCCalendar("2009010101");
        expected = 1;
        result = TimeUtils.getObservationDayOfYear(cal);
        assertEquals("did not get correct *Observation* day of year" +
                TimeUtils.getYYYYMMDDHH24(cal), expected, result);
    }

}
