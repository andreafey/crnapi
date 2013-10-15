package gov.noaa.ncdc.crn.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class StationTest {

    private Station station;
    private int stationId=9999;
    private String state = "NC";
    private String loc = "Charlotte";
    private String vector = "2 SE";
    private int offset = -5;
    
    @Before
    public void setUp() {
        station = mockStation(stationId, state, loc, vector, offset);
    }

    @Test
    public final void testGetNameString() {
        String expected = state+" "+loc+" "+vector;
        assertEquals("didn't get correct namestring",expected,station.getNameString());
    }

//    private int stationId=9999;
//    private String state = "NC";
//    private String loc = "Charlotte";
//    private String vector = "2 SE";
    @Test
    public final void testCompareTo() {
        Station mystation = mockStation(-1, state, loc, vector, offset);
        assertTrue("expected zero compareTo",station.compareTo(mystation)==0);
        assertTrue("expected zero compareTo",mystation.compareTo(station)==0);
        mystation = mockStation(-1, "MN", loc, vector, offset);
        assertTrue("expected pos compareTo",station.compareTo(mystation)>0);
        assertTrue("expected neg compareTo",mystation.compareTo(station)<0);
        int offset = -5;
        mystation = mockSWCCM(stationId, state, loc, vector, offset);
        assertTrue("expected zero compareTo",station.compareTo(mystation)==0);
        assertTrue("expected zero compareTo",mystation.compareTo(station)==0);
        mystation = mockSWCCM(stationId, "MN", loc, vector, offset);
        assertTrue("expected pos compareTo",station.compareTo(mystation)>0);
        assertTrue("expected neg compareTo",mystation.compareTo(station)<0);
    }
    private static StationWithCompleteCurrentMetadata 
    mockSWCCM(int id, String state, String loc, String vector, int offset)
    {
    	return new StationWithCompleteCurrentMetadata(id, state, loc, 
    			vector, 1, null, null, null, null, offset, null,
    			null, null, null, null, null, -1, false, null, null, null);
    }
    private static Station
    mockStation(int id, String state, String loc, String vector, int offset)
    {
    	return new Station(id, state, loc, 
    			vector, 1, null, null, null, null, offset, null,
    			null, null, null, null, null, -1, false, null, null, false);
    }

    @Test
    public final void testEqualsObject() {
        Station mystation = mockStation(stationId, state, loc, vector, offset);
        assertTrue("expected equal",station.equals(mystation));
        assertTrue("expected equal",mystation.equals(station));
        mystation = new Station(stationId-1, state, loc, vector);
        assertFalse("expected not equal",station.equals(mystation));
        assertFalse("expected not equal",mystation.equals(station));
        // only have default constructor with this object
        mystation = mockSWCCM(stationId, state, loc, vector, offset);
        assertTrue("expected equal",station.equals(mystation));
        // if override equals method in subclass, this test could fail in the future
        assertTrue("expected equal",mystation.equals(station));
        // only have default constructor with this object
        mystation = mockSWCCM(stationId-1, state, loc, vector, offset);
        assertFalse("expected not equal",station.equals(mystation));
        assertFalse("expected not equal",mystation.equals(station));
    }

    @Test
    public final void testHashCode() {
        station.hashCode();
    }

    @Test
    public final void testToString() {
        station.toString();
    }

}
