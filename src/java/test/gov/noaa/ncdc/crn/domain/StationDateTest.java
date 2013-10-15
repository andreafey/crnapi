package gov.noaa.ncdc.crn.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class StationDateTest {

	int datetimeId = 74123;
	int stationId = 1234;
	StationDate sd;
//	StationDate sdNothingSet;
	@Before
	public void setup() {
		sd = new StationDate(stationId,datetimeId);
//		sdNothingSet = new StationDate();
	}
	@Test
	public void testGetStationId() {
		assertEquals(stationId,sd.getStationId());
	}

//	@Test
//	public void testSetStationId() {
//		assertEquals(0,sdNothingSet.getStationId());
//		sdNothingSet.setStationId(stationId);
//		assertEquals(stationId,sdNothingSet.getStationId());
//	}

	@Test
	public void testGetDatetimeId() {
		assertEquals(datetimeId,sd.getDatetimeId());
	}

//	@Test
//	public void testSetDatetimeId() {
//		assertEquals(0,sdNothingSet.getDatetimeId());
//		sdNothingSet.setDatetimeId(datetimeId);
//		assertEquals(datetimeId,sdNothingSet.getDatetimeId());
//	}

	@Test
	public void testPreceding() {
		StationDate preceding = sd.preceding();
		assertEquals(sd.getStationId(),preceding.getStationId());
		assertEquals((sd.getDatetimeId()-1),preceding.getDatetimeId());
	}

	@Test
	public void testSucceeding() {
		StationDate succeeding = sd.succeeding();
		assertEquals(sd.getStationId(),succeeding.getStationId());
		assertEquals((sd.getDatetimeId()+1),succeeding.getDatetimeId());
	}
	
	/**
	 * Constructors
	 */
//	@Test
//	public void testStationDate() {
//		assertNotNull(sdNothingSet);
//		assertEquals(0,sdNothingSet.getDatetimeId());
//		assertEquals(0,sdNothingSet.getStationId());
//	}
	@Test
	public void testStationDateIntInt() {
		assertNotNull(sd);
		assertEquals(datetimeId,sd.getDatetimeId());
		assertEquals(stationId,sd.getStationId());
	}

	/**
	 * CompareTo, Equals, HashCode and ToString methods
	 */
	@Test
	public void testEqualsObject() {
//		assertFalse(sd.equals(sdNothingSet));
//		assertFalse(sdNothingSet.equals(sd));
		assertFalse(sd.equals(new String("foo")));
		assertFalse(sd.equals((StationDate)null));
		StationDate resultSd = new StationDate(stationId,datetimeId);
		assertTrue(sd.equals(resultSd));
		assertTrue(resultSd.equals(sd));
		resultSd = new StationDate(stationId,datetimeId-1);
		assertFalse(sd.equals(resultSd));
		assertFalse(resultSd.equals(sd));
		resultSd = new StationDate(stationId-1,datetimeId);
		assertFalse(sd.equals(resultSd));
		assertFalse(resultSd.equals(sd));
	}

	@Test
	public void testCompareTo() {
		StationDate resultSd = new StationDate(stationId,datetimeId);
		assertEquals(0,sd.compareTo(resultSd));
		resultSd = new StationDate(stationId,datetimeId-1);
		assertTrue(sd.compareTo(resultSd)>0);
		assertTrue(resultSd.compareTo(sd)<0);
		resultSd = new StationDate(stationId,datetimeId+1);
		assertTrue(sd.compareTo(resultSd)<0);
		assertTrue(resultSd.compareTo(sd)>0);
		resultSd = new StationDate(stationId-1,datetimeId);
		assertTrue(sd.compareTo(resultSd)>0);
		assertTrue(resultSd.compareTo(sd)<0);
		resultSd = new StationDate(stationId+1,datetimeId);
		assertTrue(sd.compareTo(resultSd)<0);
		assertTrue(resultSd.compareTo(sd)>0);
		resultSd = new StationDate(stationId-1,datetimeId+1);
		assertTrue(sd.compareTo(resultSd)>0);
		assertTrue(resultSd.compareTo(sd)<0);
	}
	
	@Test
	public void testHashCode() {
		sd.hashCode();
		StationDate //sd = new StationDate();
//		sd.hashCode();
		sd = new StationDate(1234,74123);
		sd.hashCode();
	}

	@Test
	public void testToString() {
		sd.toString();
		StationDate //sd = new StationDate();
//		sd.toString();
		sd = new StationDate(1234,74123);
		sd.toString();
	}

}
