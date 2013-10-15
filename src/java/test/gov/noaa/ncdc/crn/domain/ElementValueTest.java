package gov.noaa.ncdc.crn.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

public class ElementValueTest {

    int stationId=1026;
    int datetimeId=74123;
    int elementId=439;
    int flags=5;
    String value = "0.26";
    int decimalPlaces=2;
    int pubDecimalPlaces=1;
    ElementValue ev;
    ElementValue evNoFlags;
    ElementValue evNulls;

    @Before
    public void setUp() {
        ev=new ElementValue(stationId,datetimeId,elementId,new BigDecimal(value),flags,decimalPlaces,pubDecimalPlaces);
        evNoFlags=new ElementValue(stationId,datetimeId,elementId,new BigDecimal(value),decimalPlaces,pubDecimalPlaces);
        evNulls=new ElementValue(0,0,0,null,null,null,null);
        ev=new ElementValue(stationId,datetimeId,elementId,new BigDecimal(value),flags,decimalPlaces,pubDecimalPlaces);
    }

    @Test
    public final void testElementValue() {
        assertNotNull(evNulls);
        assertEquals(0,evNulls.getFlags().getIntValue());
    }

    @Test
    public final void testElementValueIntIntIntStringIntegerInteger() {
        assertNotNull(evNoFlags);
        assertEquals(0,evNoFlags.getFlags().getIntValue());
    }

    @Test
    public final void testElementValueIntIntIntStringIntegerIntegerInteger() {
        assertNotNull(ev);
        assertEquals(flags,ev.getFlags().getIntValue());
    }

    @Test
    public final void testGetValue() {
        assertEquals(value,ev.getValue().toPlainString());
        assertEquals(value,evNoFlags.getValue().toPlainString());
        assertEquals(null,evNulls.getValue());
    }

    @Test
    public final void testGetPublishedValue() {
        BigDecimal expected = new BigDecimal("0.3");
        assertEquals(expected, ev.getPublishedValue());
        assertEquals(expected, evNoFlags.getPublishedValue());
        assertNull(evNulls.getPublishedValue());
    }

    @Test
    public final void testGetFlags() {
        Flags expected = new Flags(flags);
        assertEquals(expected.getIntValue(),ev.getFlags().getIntValue());
        expected = new Flags(0);
        assertEquals(expected.getIntValue(),evNoFlags.getFlags().getIntValue());
        assertEquals(expected.getIntValue(),evNulls.getFlags().getIntValue());
    }

    @Test
    public final void testGetStationId() {
        assertEquals(stationId,ev.getStationId());
        assertEquals(stationId,evNoFlags.getStationId());
        assertEquals(0,evNulls.getStationId());
    }

    @Test
    public final void testGetDatetimeId() {
        assertEquals(datetimeId,ev.getDatetimeId());
        assertEquals(datetimeId,evNoFlags.getDatetimeId());
        assertEquals(0,evNulls.getDatetimeId());
    }

    @Test
    public final void testGetElementId() {
        assertEquals(elementId,ev.getElementId());
        assertEquals(elementId,evNoFlags.getElementId());
        assertEquals(0,evNulls.getElementId());
    }

    @Test
    public final void testGetDecimalPlaces() {
        assertEquals(decimalPlaces, ev.getDecimalPlaces().intValue());
        assertEquals(decimalPlaces, evNoFlags.getDecimalPlaces().intValue());
        assertNull(evNulls.getDecimalPlaces());
    }

    @Test
    public final void testGetPublishedDecimalPlaces() {
        assertEquals(pubDecimalPlaces,ev.getPublishedDecimalPlaces().intValue());
        assertEquals(pubDecimalPlaces,evNoFlags.getPublishedDecimalPlaces().intValue());
        assertNull(evNulls.getPublishedDecimalPlaces());
    }

    @Test
    public final void testToString() {
        assertTrue(ev.toString().contains(String.valueOf(elementId)));
        assertTrue(ev.toString().contains(String.valueOf(stationId)));
        assertTrue(ev.toString().contains(String.valueOf(datetimeId)));
        assertTrue(ev.toString().contains(value));
        assertTrue(ev.toString().contains("flagged"));
        assertTrue(ev.toString().contains("true"));
        assertTrue("don't have flags: "+ev.toString(),
                ev.toString().contains(String.valueOf(flags)));

        assertTrue(evNoFlags.toString().contains(String.valueOf(elementId)));
        assertTrue(evNoFlags.toString().contains(String.valueOf(stationId)));
        assertTrue(evNoFlags.toString().contains(String.valueOf(datetimeId)));
        assertTrue(evNoFlags.toString().contains(value));
        assertTrue(ev.toString().contains("flagged"));
        assertTrue(evNoFlags.toString().contains("false"));

        // elementId, datetimeId, stationId all zero
        assertTrue(evNulls.toString().contains(String.valueOf(0)));
        assertTrue(evNulls.toString().contains("null"));
        assertTrue(ev.toString().contains("flagged"));
        assertTrue(evNulls.toString().contains("false"));
    }

    @Test
    public final void testCompareTo() {
        ElementValue ev=new ElementValue(stationId,datetimeId,elementId,new BigDecimal(value),flags,decimalPlaces,pubDecimalPlaces);
        ElementValue evNoFlags=new ElementValue(stationId,datetimeId,elementId,new BigDecimal(value),decimalPlaces,pubDecimalPlaces);
        ElementValue evNulls=new ElementValue(0,0,0,null,null,null);
        assertTrue(this.ev.compareTo(ev)==0);
        assertTrue(ev.compareTo(this.ev)==0);
        assertTrue(ev.compareTo(ev)==0);
        assertTrue(this.evNoFlags.compareTo(evNoFlags)==0);
        assertTrue(evNoFlags.compareTo(this.evNoFlags)==0);
        assertTrue(evNoFlags.compareTo(evNoFlags)==0);
        assertTrue(this.evNulls.compareTo(evNulls)==0);
        assertTrue(evNulls.compareTo(this.evNulls)==0);
        assertTrue(evNulls.compareTo(evNulls)==0);

        // first by stationdateelement, then by value, then by flag
        ElementValue myev=new ElementValue(stationId+1,datetimeId,elementId,new BigDecimal(value),flags,decimalPlaces,pubDecimalPlaces);
        assertTrue(ev.compareTo(myev)<0);
        assertTrue(myev.compareTo(ev)>0);
        myev=new ElementValue(stationId,datetimeId+1,elementId,new BigDecimal(value),flags,decimalPlaces,pubDecimalPlaces);
        assertTrue(ev.compareTo(myev)<0);
        assertTrue(myev.compareTo(ev)>0);
        myev=new ElementValue(stationId,datetimeId,elementId+1,new BigDecimal(value),flags,decimalPlaces,pubDecimalPlaces);
        assertTrue(ev.compareTo(myev)<0);
        assertTrue(myev.compareTo(ev)>0);
        myev=new ElementValue(stationId,datetimeId,elementId,new BigDecimal("0.39"),flags,decimalPlaces,pubDecimalPlaces);
        assertTrue(ev.compareTo(myev)<0);
        assertTrue(myev.compareTo(ev)>0);
        myev=new ElementValue(stationId,datetimeId,elementId,new BigDecimal(value),flags+1,decimalPlaces,pubDecimalPlaces);
        assertTrue(ev.compareTo(myev)<0);
        assertTrue(myev.compareTo(ev)>0);
    }

    @Test
    public final void testEqualsObject() {
        ElementValue ev=new ElementValue(stationId,datetimeId,elementId,new BigDecimal(value),flags,decimalPlaces,pubDecimalPlaces);
        ElementValue evNoFlags=new ElementValue(stationId,datetimeId,elementId,new BigDecimal(value),decimalPlaces,pubDecimalPlaces);
        ElementValue evNulls=new ElementValue(0,0,0,null,null,null);

        assertTrue(this.ev.equals(ev));
        assertTrue(ev.equals(this.ev));
        assertTrue(ev.equals(ev));

        assertTrue(this.evNoFlags.equals(evNoFlags));
        assertTrue(evNoFlags.equals(this.evNoFlags));
        assertTrue(evNoFlags.equals(evNoFlags));

        assertTrue(this.evNulls.equals(evNulls));
        assertTrue(evNulls.equals(this.evNulls));
        assertTrue(evNulls.equals(evNulls));

        assertFalse(ev.equals(evNoFlags));
        assertFalse(ev.equals(evNulls));
        assertFalse(evNoFlags.equals(ev));
        assertFalse(evNoFlags.equals(evNulls));
        assertFalse(evNulls.equals(evNoFlags));
        assertFalse(evNulls.equals(ev));
    }

}
