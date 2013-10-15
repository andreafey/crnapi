package gov.noaa.ncdc.crn.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class StationDateElementTest {
    private StationDateElement sde;
    private int stationId=9999;
    private int datetimeId=88888;
    private int elementId=37;

    @Before
    public void setUp() {
        sde = new StationDateElement(stationId,datetimeId,elementId);
    }

    @Test
    public final void testStationDateElementIntIntInt() {
        assertNotNull("sde null",sde);
        assertEquals("didn't get expected stationId",stationId,sde.getStationId());
        assertEquals("didn't get expected datetimeId",datetimeId,sde.getDatetimeId());
        assertEquals("didn't get expected elementId",elementId,sde.getElementId());
    }

    @Test
    public final void testGetStationDate() {
        StationDate expected = new StationDate(stationId,datetimeId);
        assertEquals("didn't get expected stationdate",expected,sde.getStationDate());
    }

    @Test
    public final void testEqualsObject() {
        StationDateElement mysde = new StationDateElement(stationId, datetimeId, elementId);
        assertTrue("expected equals",sde.equals(mysde));
        assertTrue("expected equals",mysde.equals(sde));
        mysde = new StationDateElement((stationId-1), datetimeId, elementId);
        assertFalse("expected not equals",sde.equals(mysde));
        assertFalse("expected not equals",mysde.equals(sde));
        mysde = new StationDateElement(stationId, (datetimeId-1), elementId);
        assertFalse("expected not equals",sde.equals(mysde));
        assertFalse("expected not equals",mysde.equals(sde));
        mysde = new StationDateElement(stationId, datetimeId, (elementId-1));
        assertFalse("expected not equals",sde.equals(mysde));
        assertFalse("expected not equals",mysde.equals(sde));
        
        assertFalse("don't want string equals",sde.equals("hi"));
    }

    @Test
    public final void testCompareTo() {
        StationDateElement mysde = new StationDateElement(stationId, datetimeId, elementId);
        assertTrue("expected equals",sde.compareTo(mysde)==0);
        assertTrue("expected equals",mysde.compareTo(sde)==0);
        mysde = new StationDateElement((stationId-1), datetimeId, elementId);
        assertTrue("expected not equals",sde.compareTo(mysde)>0);
        assertTrue("expected not equals",mysde.compareTo(sde)<0);
        mysde = new StationDateElement(stationId, (datetimeId-1), elementId);
        assertTrue("expected not equals",sde.compareTo(mysde)>0);
        assertTrue("expected not equals",mysde.compareTo(sde)<0);
        mysde = new StationDateElement(stationId, datetimeId, (elementId-1));
        assertTrue("expected not equals",sde.compareTo(mysde)>0);
        assertTrue("expected not equals",mysde.compareTo(sde)<0);

        assertFalse("don't want string equals",sde.compareTo("hi")==0);
    }

    // just making sure things don't crash when call
    @Test
    public final void testHashCode() {
        sde.hashCode();
    }

    // just making sure things don't crash when call
    @Test
    public final void testToString() {
        sde.toString();
    }

}
