package gov.noaa.ncdc.crn.domain.sort;

import static gov.noaa.ncdc.crn.domain.CrnDomains.STATION_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import gov.noaa.ncdc.crn.domain.POR;
import gov.noaa.ncdc.crn.domain.Station;
import gov.noaa.ncdc.crn.domain.sort.StationComparator.SORTS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class StationComparatorTest {

	Map<Integer,Station> stationMap;
	Station a, b, c, d;
	Map<Integer,POR> porMap;
	
	// stationid order: abcd
	// netid order:     [bc]ad
	// net+goesid order:cbad
	// net+stationid order:  bcad
	// goesid order:    cdba
	// name order:      dacb
    // closed order:    [ab]dc
	@Before
	public void setUp() {
		stationMap = new HashMap<>();
		porMap = new HashMap<>();
		a = mockStation("CT", "Bridgeport", "2 SSE", 123, 2, "CD0A123", "N", "20120425");
		stationMap.put(a.getStationId(), a);
		POR por = new POR(a.getStationId(), 106, 1010);
		porMap.put(a.getStationId(), por);

		b = mockStation("OH","Cincinatti","6 WSW", 124, 1, "CD04123", "Y", "20110425");
		stationMap.put(b.getStationId(), b);
		por = new POR(b.getStationId(), 103, 1010);
		porMap.put(b.getStationId(), por);

		c = mockStation("MN","Duluth","8 ENE", 125, 1, "CD01123", "A", null);
		stationMap.put(c.getStationId(), c);
		por = new POR(c.getStationId(), 108, 1010);
		porMap.put(c.getStationId(), por);

		d = mockStation("AZ","Tucson","4 SW", 126, 3, "CD02123", "C", "20110115");
		stationMap.put(d.getStationId(), d);
		por = new POR(d.getStationId(), 105, 1010);
		porMap.put(d.getStationId(), por);
	}

	private static Station mockStation(String state, String location, String vector, 
			int id, int netid, String goesid, 
			String opstat, String closeddate) {
		// Mockito didn't work very well here because of testing comparator
		return new Station(id, state, location, vector, netid, null, goesid, 
				null, null, 0, null, null, null, null, null, opstat, -1, null, 
				null, closeddate, null);
	}

	@Test
	public final void testStationComparatorSORTSArray() {
		// tests constructor; testing varargs
		StationComparator<Station> stacomp = new StationComparator<Station>(SORTS.NETWORK_ID,SORTS.GOES_ID);
		assertNotNull(stacomp);
		stacomp = new StationComparator<Station>(SORTS.STATION_ID);
		assertNotNull(stacomp);
	}

	@Test
	public final void testStationComparatorMapOfIntegerStationSORTSArray() {
		// tests constructor with map; testing varargs
		StationComparator<Integer> stacomp = new StationComparator<>(stationMap,SORTS.NETWORK_ID,SORTS.GOES_ID);
		assertNotNull(stacomp);
		stacomp = new StationComparator<>(stationMap,SORTS.STATION_ID);
		assertNotNull(stacomp);
	}

	@Test
	public final void testCompareTT() {
		StationComparator<Integer> stacomp = new StationComparator<>(stationMap,SORTS.STATION_ID);
		List<Integer> ids = new ArrayList<>(stationMap.keySet());
		Collections.sort(ids, stacomp);
		// stationid order: abcd
		assertEquals(a,stationMap.get(ids.get(0)));
		assertEquals(b,stationMap.get(ids.get(1)));
		assertEquals(c,stationMap.get(ids.get(2)));
		assertEquals(d,stationMap.get(ids.get(3)));
		
		// map not needed for stationid comparison
		stacomp = new StationComparator<>(SORTS.STATION_ID);
		ids.clear();
		ids.addAll(stationMap.keySet());
		Collections.sort(ids, stacomp);
		// stationid order: abcd
		assertEquals(a,stationMap.get(ids.get(0)));
		assertEquals(b,stationMap.get(ids.get(1)));
		assertEquals(c,stationMap.get(ids.get(2)));
		assertEquals(d,stationMap.get(ids.get(3)));

		stacomp = new StationComparator<>(stationMap,SORTS.NETWORK_ID,SORTS.GOES_ID);
		ids.clear();
		ids.addAll(stationMap.keySet());
		Collections.sort(ids, stacomp);
//		// net+goesid order: cbad
		assertEquals(c,stationMap.get(ids.get(0)));
		assertEquals(b,stationMap.get(ids.get(1)));
		assertEquals(a,stationMap.get(ids.get(2)));
		assertEquals(d,stationMap.get(ids.get(3)));

        stacomp = new StationComparator<>(stationMap,SORTS.CLOSED);
        ids.clear();
        ids.addAll(stationMap.keySet());
        Collections.sort(ids, stacomp);
        // closed order:    [ab]dc
        assertEquals(b,stationMap.get(ids.get(0)));
        assertEquals(a,stationMap.get(ids.get(1)));
        assertEquals(d,stationMap.get(ids.get(2)));
        assertEquals(c,stationMap.get(ids.get(3)));

        stacomp = new StationComparator<>(stationMap,SORTS.CLOSED, SORTS.NAME);
        ids.clear();
        ids.addAll(stationMap.keySet());
        Collections.sort(ids, stacomp);
        // closed+name order:    abdc
        assertEquals(a,stationMap.get(ids.get(0)));
        assertEquals(b,stationMap.get(ids.get(1)));
        assertEquals(d,stationMap.get(ids.get(2)));
        assertEquals(c,stationMap.get(ids.get(3)));

        stacomp = new StationComparator<>(stationMap,SORTS.CLOSED, SORTS.GOES_ID);
        ids.clear();
        ids.addAll(stationMap.keySet());
        Collections.sort(ids, stacomp);
        // closed+goes order:    badc
        assertEquals(b,stationMap.get(ids.get(0)));
        assertEquals(a,stationMap.get(ids.get(1)));
        assertEquals(d,stationMap.get(ids.get(2)));
        assertEquals(c,stationMap.get(ids.get(3)));
		
		List<Station> stas = new ArrayList<>(stationMap.values());
		StationComparator<Station> stacomp2 = new StationComparator<>(SORTS.GOES_ID);
		Collections.sort(stas,stacomp2);
		// goesid order:    cdba
		assertEquals(c,stas.get(0));
		assertEquals(d,stas.get(1));
		assertEquals(b,stas.get(2));
		assertEquals(a,stas.get(3));
		
		stacomp = new StationComparator<>(stationMap,porMap,SORTS.POR);
		ids.clear();
		ids.addAll(stationMap.keySet());
		Collections.sort(ids, stacomp);
		//por order: bdac
		assertEquals(b,stationMap.get(ids.get(0)));
		assertEquals(d,stationMap.get(ids.get(1)));
		assertEquals(a,stationMap.get(ids.get(2)));
		assertEquals(c,stationMap.get(ids.get(3)));

		
		stacomp2 = new StationComparator<>(SORTS.CLOSED_DATE);
		stas.clear();
		stas.addAll(stationMap.values());
		Collections.sort(stas, stacomp2);
		//closed date order: dbac
		assertEquals(d,stas.get(0));
		assertEquals(b,stas.get(1));
		assertEquals(a,stas.get(2));
		assertEquals(c,stas.get(3));
	}

	@Test
	public final void testEqualsTT() {
		// NOTE Integer comparators constructed with a map always compare the items in that map 
		StationComparator<Integer> stacomp = new StationComparator<>(stationMap,SORTS.STATION_ID);
		Station test = mock(Station.class);
		when(test.getStationId()).thenReturn(a.getStationId());
		assertTrue(stacomp.equals(a.getStationId(), test.getStationId()));
		when(test.getStationId()).thenReturn(b.getStationId());
		assertFalse(stacomp.equals(a.getStationId(), test.getStationId()));
		
		// map not needed for stationid comparison
		stacomp = new StationComparator<>(SORTS.STATION_ID);
		when(test.getStationId()).thenReturn(a.getStationId());
		assertTrue(stacomp.equals(a.getStationId(), test.getStationId()));
		when(test.getStationId()).thenReturn(b.getStationId());
		assertFalse(stacomp.equals(a.getStationId(), test.getStationId()));

		stacomp = new StationComparator<>(stationMap,SORTS.NETWORK_ID);
//		// cb are in same network
		assertTrue(stacomp.equals(b.getStationId(), c.getStationId()));
		assertFalse(stacomp.equals(a.getStationId(), b.getStationId()));
		assertFalse(stacomp.equals(a.getStationId(), c.getStationId()));
		assertFalse(stacomp.equals(c.getStationId(), d.getStationId()));
		
//		Note testing StationComparator stationId comparison here; only the stationid is relevant; don't get confused by this
		when(test.getStationId()).thenReturn(a.getStationId());
//		test.setStationId(a.getStationId());
//		test.setNetworkId(b.getNetworkId());
		
		// These two tests are identical
		assertFalse(stacomp.equals(b.getStationId(), test.getStationId()));
		assertFalse(stacomp.equals(b.getStationId(), a.getStationId()));

		StationComparator<Station> stacomp2 = new StationComparator<>(SORTS.GOES_ID);
		assertFalse(stacomp2.equals(a,b));
		assertFalse(stacomp2.equals(a,c));
		assertFalse(stacomp2.equals(a,d));
		assertFalse(stacomp2.equals(b,c));
		assertFalse(stacomp2.equals(b,d));
		assertFalse(stacomp2.equals(c,d));
		assertTrue(stacomp2.equals(a,a));
		assertTrue(stacomp2.equals(b,b));
		assertTrue(stacomp2.equals(c,c));
		assertTrue(stacomp2.equals(d,d));
		
		when(test.getGoesId()).thenReturn(a.getGoesId());
		when(test.getClosedDate()).thenReturn(a.getClosedDate());
		assertTrue(stacomp2.equals(a,test));
		when(test.getGoesId()).thenReturn(b.getGoesId());
		when(test.getClosedDate()).thenReturn(b.getClosedDate());
		assertTrue(stacomp2.equals(b,test));
	}
// missing tests: compare by test, goes satellite, stationid
	@Test
	public final void testCompareStationStationSORTS() {
		StationComparator<Integer> stacomp = new StationComparator<>(stationMap,SORTS.STATION_ID);
		// stationid order: abcd
		assertTrue(stacomp.compare(a.getStationId(), b.getStationId())<0);
		assertTrue(stacomp.compare(b.getStationId(), a.getStationId())>0);
		assertTrue(stacomp.compare(b.getStationId(), c.getStationId())<0);
		assertTrue(stacomp.compare(c.getStationId(), d.getStationId())<0);
		
		// map not needed for stationid comparison
		stacomp = new StationComparator<>(SORTS.STATION_ID);
		// stationid order: abcd
		assertTrue(stacomp.compare(a.getStationId(), b.getStationId())<0);
		assertTrue(stacomp.compare(b.getStationId(), a.getStationId())>0);
		assertTrue(stacomp.compare(b.getStationId(), c.getStationId())<0);
		assertTrue(stacomp.compare(c.getStationId(), d.getStationId())<0);

		stacomp = new StationComparator<>(stationMap,SORTS.NETWORK_ID,SORTS.GOES_ID);
//		// net+goesid order:cbad
		assertTrue(stacomp.compare(c.getStationId(), b.getStationId())<0);
		assertTrue(stacomp.compare(b.getStationId(), c.getStationId())>0);
		assertTrue(stacomp.compare(b.getStationId(), a.getStationId())<0);
		assertTrue(stacomp.compare(a.getStationId(), d.getStationId())<0);
		
		List<Station> stas = new ArrayList<>(stationMap.values());
		StationComparator<Station> stacomp2 = new StationComparator<>(SORTS.GOES_ID);
		Collections.sort(stas,stacomp2);
		// goesid order:    cdba
		assertTrue(stacomp2.compare(c,d)<0);
		assertTrue(stacomp2.compare(d,c)>0);
		assertTrue(stacomp2.compare(d,b)<0);
		assertTrue(stacomp2.compare(b,a)<0);
	}
	// stationid order: abcd
	// netid order:     [bc]ad
	// net+stationid order:  bcad
	// net+goesid order:cbad
	// goesid order:    cdba
	// name order:      dacb

	/* This tests the example usage in the StationComparator documentation */
    @Test
    public final void testSample() {
		List<Station> stations = new ArrayList<>();
		stations.addAll(stationMap.values());
		// sorts by station name (state,location,vector) using Station.compareTo(Station);
		Collections.sort(stations);
		// name order:      dacb
		assertEquals(d,stations.get(0));
		assertEquals(a,stations.get(1));
		assertEquals(c,stations.get(2));
		assertEquals(b,stations.get(3));
		// sorts all stations by goesid
		Collections.sort(stations, new StationComparator<>(SORTS.GOES_ID));
		// goesid order:    cdba
		assertEquals(c,stations.get(0));
		assertEquals(d,stations.get(1));
		assertEquals(b,stations.get(2));
		assertEquals(a,stations.get(3));
		List<Integer> stationIds = new ArrayList<>();
		stationIds.addAll(stationMap.keySet());
		// sorts stationIds first by networkId and then by stationId
		Collections.sort(stationIds, 
				new StationComparator<>(stationMap,SORTS.NETWORK_ID,SORTS.STATION_ID));
		// net+stationid order:  bcad
		assertEquals(b,stationMap.get(stationIds.get(0)));
		assertEquals(c,stationMap.get(stationIds.get(1)));
		assertEquals(a,stationMap.get(stationIds.get(2)));
		assertEquals(d,stationMap.get(stationIds.get(3)));
    }

  @Test
	public void testCompareByTest() { // isTestSiteOnly status
	  	Station one = mock(Station.class);
	  	when(one.getTestSiteOnly()).thenReturn(false);
	  	when(one.getStationId()).thenReturn(1);
	  	Station two = mock(Station.class);
	  	when(two.getTestSiteOnly()).thenReturn(true);
	  	when(two.getStationId()).thenReturn(2);
	  	Station three = mock(Station.class);
	  	when(three.getTestSiteOnly()).thenReturn(false);
	  	when(three.getStationId()).thenReturn(3);
	  	List<Station> stations = Lists.newArrayList(one, two, three);
	  	Map<Integer,Station> stationMap = Maps.uniqueIndex(stations, STATION_ID);
	  	Collections.sort(stations,
					new StationComparator<>(stationMap,SORTS.TEST));
	  	assertEquals("incorrect order", 1, stations.get(0).getStationId());
	  	assertEquals("incorrect order", 3, stations.get(1).getStationId());
	  	assertEquals("incorrect order", 2, stations.get(2).getStationId());
	}
    @Test
	public void testCompareByGoesSatellite() {
    	Station one = mock(Station.class);
    	when(one.getGoesSat()).thenReturn("W");
    	when(one.getStationId()).thenReturn(1);
    	Station two = mock(Station.class);
    	when(two.getGoesSat()).thenReturn("E");
    	when(two.getStationId()).thenReturn(2);
    	Station three = mock(Station.class);
    	when(three.getGoesSat()).thenReturn("E");
    	when(three.getStationId()).thenReturn(3);
    	List<Station> stations = Lists.newArrayList(one, two, three);
    	Map<Integer,Station> stationMap = Maps.uniqueIndex(stations, STATION_ID);
    	Collections.sort(stations,
				new StationComparator<>(stationMap,SORTS.GOES_SATELLITE));
    	assertEquals("incorrect order", 2, stations.get(0).getStationId());
    	assertEquals("incorrect order", 3, stations.get(1).getStationId());
    	assertEquals("incorrect order", 1, stations.get(2).getStationId());
	}
    @Test
	public void testCompareByStationId() {
    	Station one = mock(Station.class);
    	when(one.getStationId()).thenReturn(1);
    	Station two = mock(Station.class);
    	when(two.getStationId()).thenReturn(2);
    	Station three = mock(Station.class);
    	when(three.getStationId()).thenReturn(3);
    	List<Station> stations = Lists.newArrayList(three, one, two);
    	Map<Integer,Station> stationMap = Maps.uniqueIndex(stations, STATION_ID);
    	Collections.sort(stations,
				new StationComparator<>(stationMap,SORTS.STATION_ID));
    	assertEquals("incorrect order", 1, stations.get(0).getStationId());
    	assertEquals("incorrect order", 2, stations.get(1).getStationId());
    	assertEquals("incorrect order", 3, stations.get(2).getStationId());
	}
    @Test
	public void testCompareByPor() {
    	Station one = mock(Station.class);
    	when(one.getStationId()).thenReturn(1);
    	Station two = mock(Station.class);
    	when(two.getStationId()).thenReturn(2);
    	Station three = mock(Station.class);
    	when(three.getStationId()).thenReturn(3);
    	List<Station> stations = Lists.newArrayList(one, two, three);
    	Map<Integer,Station> stationMap = Maps.uniqueIndex(stations, STATION_ID);
    	
    	POR por1 = mock(POR.class);
    	when(por1.getStationId()).thenReturn(1);
    	when(por1.getStartDatetime()).thenReturn(132);
    	POR por2 = mock(POR.class);
    	when(por2.getStationId()).thenReturn(2);
    	when(por2.getStartDatetime()).thenReturn(120);
    	POR por3 = mock(POR.class);
    	when(por3.getStationId()).thenReturn(3);
    	when(por3.getStartDatetime()).thenReturn(125);
    	List<POR> pors = Lists.newArrayList(por1, por2, por3);
    	Map<Integer,POR> porMap = Maps.uniqueIndex(pors, STATION_ID);
    	
    	// sorts by begin datetime order
    	Collections.sort(stations,
				new StationComparator<>(stationMap, porMap, SORTS.POR));
    	assertEquals("incorrect order", 2, stations.get(0).getStationId());
    	assertEquals("incorrect order", 3, stations.get(1).getStationId());
    	assertEquals("incorrect order", 1, stations.get(2).getStationId());
	}

}
