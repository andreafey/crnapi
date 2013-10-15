package gov.noaa.ncdc.crn.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.noaa.ncdc.crn.util.TimeUtils;

import org.junit.Before;
import org.junit.Test;

public class DatetimeTest {
    private Datetime datetime;
    private int datetimeId = 10612;
    private String datetimeString = "2001122013";

    private Datetime yearEnd;
    private int yearEndId = 72255;
    private String yearEndString = "2008123124";

    /*
     * DATETIME        STRING
     * 10612           200112201300
     * 20003           200301152000
     * 26057           200309250200
     * 72255           200812312400
     */
    @Before
    public final void setup() {
        datetime = new Datetime(datetimeId,datetimeString);
        yearEnd = new Datetime(yearEndId,yearEndString);
    }
    @Test
    public final void testHashCode() {
        assertEquals("Expected datetimeId as hashCode",datetime.getDatetimeId(),datetime.hashCode());
    }

    @Test
    public final void testDatetimeIntString() {
        Datetime datetime = new Datetime(datetimeId,datetimeString);
        assertEquals("Didn't get correct datetimeId",datetimeId,datetime.getDatetimeId());
        assertEquals("Didn't get correct date string",datetimeString,datetime.getDatetime0_23());
        // additional testing from Steven to validate ms are getting properly set
        // create a new datetime.
        Datetime dt1 = new Datetime(82535, "201003050800");
        
        // sleep so that we have a difference.
        try{
            Thread.sleep(145);  /* milliseconds */
        }catch(Exception e){ }
        
        // should be equal to the first one.
        Datetime dt2 = new Datetime(82535, "201003050800");
        assertEquals("milliseconds are inequal",TimeUtils.getMsSinceEpoch(dt1),TimeUtils.getMsSinceEpoch(dt2));
    }

    @Test
    public final void testGetDatetimeId() {
        assertEquals("Didn't get correct datetimeId",datetimeId,datetime.getDatetimeId());
        assertEquals("Didn't get correct datetimeId",yearEndId,yearEnd.getDatetimeId());
    }

    @Test
    public final void testGetDatetime0_23() {
        assertEquals("Didn't get correct date string",datetimeString,datetime.getDatetime0_23());
        assertEquals("Didn't get correct date string",yearEndString,yearEnd.getDatetime0_23());
    }

    @Test
    public final void testGetUtcCal() {
        String format = "%1$tD %1$tR"; // MM/DD/YYYY HH24:MI
        String calString = String.format(format, datetime.getUtcCal());
        String expected = "12/20/01 13:00"; //2001122013
        assertEquals("didn't get expected calendar string",expected,calString);
        calString = String.format(format, yearEnd.getUtcCal());
        expected = "01/01/09 00:00"; //2008123124
        assertEquals("didn't get expected calendar string",expected,calString);
    }

    @Test
    public final void testGetLstDatetime0_23StringInt() {
        assertEquals("Didn't get expected dt0_23 with -5 offset","2001122008",Datetime.getLstDatetime0_23(datetimeString,-5));
        assertEquals("Didn't get expected dt0_23 with -5 offset","2008123119",Datetime.getLstDatetime0_23(yearEndString,-5));
    }

    @Test
    public final void testGetLstDatetime0_23Int() {
        assertEquals("Didn't get expected dt0_23 with -5 offset","2001122008",datetime.getLstDatetime0_23(-5));
        assertEquals("Didn't get expected dt0_23 with -5 offset","2008123119",yearEnd.getLstDatetime0_23(-5));
    }

    @Test
    public final void testGetYear() {
        assertEquals("Didn't get correct year",2001,datetime.getYear());
        assertEquals("Didn't get correct year",2009,yearEnd.getYear());
    }

    @Test
    public final void testGetMonth() {
        assertEquals("Didn't get correct month",11,datetime.getMonth());
        assertEquals("Didn't get correct month",0,yearEnd.getMonth());
    }

    @Test
    public final void testGetDay() {
        assertEquals("Didn't get correct day",20,datetime.getDay());
        assertEquals("Didn't get correct day",1,yearEnd.getDay());
    }

    @Test
    public final void testGetHour() {
        assertEquals("Didn't get correct hour",13,datetime.getHour());
        assertEquals("Didn't get correct hour",0,yearEnd.getHour());
    }

    @Test
    public final void testNext() {
        Datetime next = datetime.next();
        assertEquals("datetimeId difference incorrect",1,next.getDatetimeId()-datetimeId);
        assertEquals("wrong next datetime string","2001122014",next.getDatetime0_23());
        next=yearEnd.next();
        assertEquals("datetimeId difference incorrect",1,next.getDatetimeId()-yearEnd.getDatetimeId());
        assertEquals("wrong next datetime string","2009010101",next.getDatetime0_23());
    }

    @Test
    public final void testPrevious() {
        Datetime prev = datetime.previous();
        assertEquals("datetimeId difference incorrect",-1,prev.getDatetimeId()-datetimeId);
        assertEquals("wrong next datetime string","2001122012",prev.getDatetime0_23());
        prev=yearEnd.previous();
        assertEquals("datetimeId difference incorrect",-1,prev.getDatetimeId()-yearEnd.getDatetimeId());
        assertEquals("wrong next datetime string","2008123123",prev.getDatetime0_23());
    }

    @Test
    public final void testAdd() {
        Datetime nextDay = datetime.add(24);
        assertEquals("datetimeId difference incorrect",24,nextDay.getDatetimeId()-datetimeId);
        assertEquals("wrong add(24) datetime string","2001122113",nextDay.getDatetime0_23());
        Datetime add7 = yearEnd.add(7);
        assertEquals("datetimeId difference incorrect",7,add7.getDatetimeId()-yearEnd.getDatetimeId());
        assertEquals("wrong next datetime string","2009010107",add7.getDatetime0_23());
    }

    @Test
    public final void testCompareTo() {
        Datetime newDatetime = new Datetime(datetimeId,datetimeString);
        assertEquals("expect no difference",0,(datetime.compareTo(newDatetime)));
        assertEquals("expect no difference",0,(newDatetime.compareTo(datetime)));
        assertTrue("should be smaller than 2008 datetime",newDatetime.compareTo(yearEnd)<0);
        assertTrue("should be greater than 2008 datetime",yearEnd.compareTo(newDatetime)>0);
        newDatetime = new Datetime(yearEndId,yearEndString);
        assertEquals("expect no difference",0,(yearEnd.compareTo(newDatetime)));
        assertEquals("expect no difference",0,(newDatetime.compareTo(yearEnd)));
        assertTrue("should be smaller than 2008 datetime",newDatetime.compareTo(datetime)>0);
        assertTrue("should be greater than 2008 datetime",datetime.compareTo(newDatetime)<0);
    }

    @Test
    public final void testEqualsObject() {
        Datetime newDatetime = new Datetime(datetimeId,datetimeString);
        assertTrue("expect no difference",datetime.equals(newDatetime));
        assertTrue("expect no difference",newDatetime.equals(datetime));
        assertFalse("expect difference",yearEnd.equals(newDatetime));
        assertFalse("expect difference",newDatetime.equals(yearEnd));
        
        newDatetime = new Datetime(yearEndId,yearEndString);
        assertFalse("expect difference",datetime.equals(newDatetime));
        assertFalse("expect difference",newDatetime.equals(datetime));
        assertTrue("expect no difference",yearEnd.equals(newDatetime));
        assertTrue("expect no difference",newDatetime.equals(yearEnd));
    }

    @Test
    public final void testCopy() {
        Datetime newDatetime = datetime.copy();
        assertTrue("expect no difference",datetime.equals(newDatetime));
        assertTrue("expect no difference",newDatetime.equals(datetime));
        assertFalse("expect difference",yearEnd.equals(newDatetime));
        assertFalse("expect difference",newDatetime.equals(yearEnd));
        
        newDatetime = yearEnd.copy();
        assertFalse("expect difference",datetime.equals(newDatetime));
        assertFalse("expect difference",newDatetime.equals(datetime));
        assertTrue("expect no difference",yearEnd.equals(newDatetime));
        assertTrue("expect no difference",newDatetime.equals(yearEnd));
    }
}
