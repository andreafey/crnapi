package gov.noaa.ncdc.crn.domain;

import static gov.noaa.ncdc.crn.domain.CrnDomains.matches;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

public class StationsTest {

    @Test
    public void testFindWbanno() {
        Station sta1 = mock(Station.class);
        when(sta1.getWbanno()).thenReturn("FOO");
        Station sta2 = mock(Station.class);
        when(sta2.getWbanno()).thenReturn("BAR");
        List<Station> stations = Lists.newArrayList(sta1, sta2);

        Station result = Stations.findWbanno(stations, "FOO");
        assertNotNull("didn't find station in list", result);
        assertEquals("incorrect wbanno", "FOO", result.getWbanno());

        result = Stations.findWbanno(stations, "BAZ");
        assertNull("should not have found station with BAZ", result);

        result = Stations.findWbanno(stations, null);
        assertNull("should not have found station with null", result);
    }

    @Test
    public void testFilterClosed() {
        Station sta1 = mock(Station.class);
        when(sta1.isClosed()).thenReturn(true);
        when(sta1.getStationId()).thenReturn(1);
        Station sta2 = mock(Station.class);
        when(sta2.isClosed()).thenReturn(false);
        when(sta2.getStationId()).thenReturn(2);
        List<Station> stations = Lists.newArrayList(sta1, sta2);

        Collection<Station> result = Stations.filterClosed(stations);
        assertEquals("expected 1 closed station", 1, result.size());
        assertEquals("wrong station id for closed station", 1, result.iterator().next().getStationId());
        result = Stations.filterNotClosed(stations);
        assertEquals("expected 1 not closed station", 1, result.size());
        assertEquals("wrong station id for not closed station", 2, result.iterator().next().getStationId());
    }

    @Test
    public void testFilterVisible() {
        Station sta1 = mock(Station.class);
        when(sta1.getTestSiteOnly()).thenReturn(true);
        when(sta1.getOpStatus()).thenReturn("Y");
        when(sta1.getStationId()).thenReturn(1);
        Station sta2 = mock(Station.class);
        when(sta2.getTestSiteOnly()).thenReturn(false);
        when(sta2.getOpStatus()).thenReturn("A");
        when(sta2.getStationId()).thenReturn(2);
        Station sta3 = mock(Station.class);
        when(sta3.getTestSiteOnly()).thenReturn(false);
        when(sta3.getOpStatus()).thenReturn("N");
        when(sta3.getStationId()).thenReturn(3);
        List<Station> stations = Lists.newArrayList(sta1, sta2, sta3);

        Collection<Station> result = Stations.filterVisible(stations);
        assertEquals("expected 1 visible station", 1, result.size());
        assertEquals("wrong station id for visible station", 3, result.iterator().next().getStationId());
    }

    @Test
    public void testCrnEtc() {
        Station sta1 = mock(Station.class);
        when(sta1.getStationId()).thenReturn(10);
        when(sta1.getNetworkId()).thenReturn(1);
        when(sta1.isPseudoRcrn()).thenReturn(true);
        Station sta2 = mock(Station.class);
        when(sta2.getStationId()).thenReturn(11);
        when(sta2.getNetworkId()).thenReturn(2);
        when(sta2.isPseudoRcrn()).thenReturn(false);
        Station sta3 = mock(Station.class);
        when(sta3.getStationId()).thenReturn(12);
        when(sta3.getNetworkId()).thenReturn(3);
        when(sta3.isPseudoRcrn()).thenReturn(false);

        List<Station> stations = Lists.newArrayList(sta1, sta2, sta3);

        Collection<Station> result = Collections2.filter(stations, Stations.CRN);
        assertEquals("expected 2 CRN stations", 1, result.size());
        for (Station station : result) {
            int stationId = station.getStationId();
            assertTrue("station not in correct network: "+station.getStationId(), stationId==10 || stationId==11);
        }

        result = Collections2.filter(stations, Stations.RCRN);
        assertEquals("expected 2 true RCRN stations (AL now included)", 2, result.size());
        List<Integer> ids = Lists.transform(new ArrayList<Station>(result), CrnDomains.STATION_ID);
        assertTrue("station #12 is true rcrn", ids.contains(12));
        assertTrue("station #11 is AL rcrn", ids.contains(11));

        result = Collections2.filter(stations, Stations.RCRN_INCLUDE_REPS);
        assertEquals("expected 3 RCRN stations when include reps", 3, result.size());
        ids = Lists.transform(new ArrayList<Station>(result), CrnDomains.STATION_ID);
        assertTrue("station #12 is true rcrn", ids.contains(12));
        assertTrue("station #11 is AL rcrn", ids.contains(11));
        assertTrue("station #10 is rcrn rep", ids.contains(10));
    }

    @Test
    public void testMiscFunctions() {
        Station sta1 = mockStation(10, "IN", null);
        when(sta1.getNameString()).thenReturn("IN Indy 500W");
        when(sta1.getCommCode()).thenReturn("Y");
        when(sta1.getNetworkId()).thenReturn(1);
        when(sta1.getOpStatus()).thenReturn("Y");
        Station sta2 = mockStation(11, "AL", null);
        when(sta2.getNameString()).thenReturn("AL Birm 3E");
        when(sta2.getCommCode()).thenReturn("N");
        when(sta2.getNetworkId()).thenReturn(2);
        when(sta2.getOpStatus()).thenReturn("Y");
        Station sta3 = mockStation(12, "NC", null);
        when(sta3.getNameString()).thenReturn("NC Ocracoke 1N");
        when(sta3.getCommCode()).thenReturn("Y");
        when(sta3.getNetworkId()).thenReturn(1);
        when(sta3.getOpStatus()).thenReturn("N");

        List<Station> stations = Lists.newArrayList(sta1, sta2, sta3);

        Collection<Station> result = Collections2.filter(stations, matches(Stations.NAME_STRING,"IN Indy 500W"));
        assertEquals("expected 1 station", 1, result.size());
        assertEquals("expected id #10", 10, result.iterator().next().getStationId());

        result = Collections2.filter(stations, matches(Stations.STATE, "NC", "AL"));
        assertEquals("expected 2 stations", 2, result.size());
        for (Station station : result) {
            int stationId = station.getStationId();
            assertTrue("wrong stationId: "+stationId, stationId==11 || stationId==12);
        }

        result = Collections2.filter(stations, Stations.COMMISSIONED);
        assertEquals("expected 2 stations", 2, result.size());
        for (Station station : result) {
            int stationId = station.getStationId();
            assertTrue("wrong stationId: "+stationId, stationId==10 || stationId==12);
        }

        Station sta = FluentIterable.from(stations)
                .filter(matches(Stations.NETWORK_ID, 2,3)).get(0);
        assertNotNull("station not found", sta);
        assertEquals("wrong networkid",2,sta.getNetworkId());

        result = FluentIterable.from(stations)
                .filter(matches(Stations.OP_STATUS, "Y")).toList();
        assertEquals("expected 2 stations", 2, result.size());
        for (Station station : result) {
            String opstat = station.getOpStatus();
            assertEquals("only expect opstat=y","Y",opstat);
        }

    }

    private final List<Station> unfiltered = Lists.newArrayList(
            mockStation(1,"NC","345345"),
            mockStation(2,"AL","123123"),
            mockStation(3,"AL","246642"),
            mockStation(4,"FL","898989"),
            mockStation(5,"GA",null)
            );


    private Station mockStation(int id, String state, String wbanno) {
        Station station = mock(Station.class);
        StationName name = mock(StationName.class);
        when(name.getState()).thenReturn(state);
        when(station.getName()).thenReturn(name);
        when(station.getStationId()).thenReturn(id);
        when(station.getWbanno()).thenReturn(wbanno);
        return station;
        //		return new Station(id, state, "Washington", "1 N", networkId,
        //				"Dummy mock site", goesId, wbanno, "9876", -5, "34.54", "85.22",
        //				987, commCode, "NA", opStatus, -1, false, "W", "NA", false);
    }
    @Test
    public void testMatchesFunctionOfStationTCollectionOfT() {
        // wban=345345
        String wbanno = "345345";
        Collection<String> wbans = Lists.newArrayList(wbanno);
        Predicate<Station> matches = matches(Stations.WBANNO, wbans);
        Collection<Station> filtered = Collections2.filter(unfiltered, matches);
        assertEquals("expected one station with wbanno="+wbanno, 1, filtered.size());
        assertEquals("wrong wbanno",wbanno,filtered.iterator().next().getWbanno());

        // stations with one of two wbans
        wbans.add("123123");
        matches = matches(Stations.WBANNO, wbans);
        filtered = Collections2.filter(unfiltered, matches);
        assertEquals("expected two stations",2, filtered.size());

        // stations with wbannos not found
        wbans.clear();
        wbans.add("foo");
        matches = matches(Stations.WBANNO, wbans);
        filtered = Collections2.filter(unfiltered, matches);
        assertEquals("expected empty collection wbanno=foo",
                0, filtered.size());

        // stations with null wbannos
        wbans.clear();
        wbans.add(null);
        matches = matches(Stations.WBANNO, wbans);
        filtered = Collections2.filter(unfiltered, matches);
        assertEquals("expected one station with null wbanno", 1, filtered.size());
        assertNull("expected wban=null",filtered.iterator().next().getWbanno());

        // stations with empty wbannos
        wbans.clear();
        matches = matches(Stations.WBANNO, wbans);
        filtered = Collections2.filter(unfiltered, matches);
        assertEquals("expected empty collection empty collection",
                0, filtered.size());

        // test other Functions
        matches = matches(Stations.STATE, "AL","FL");
        filtered = Collections2.filter(unfiltered, matches);
        assertEquals("expected 2 AL stations plus 1 FL station",3,filtered.size());
    }

    @Test
    public void testMatchesFunctionOfStationTTArray() {
        // wban=345345
        String wbanno = "345345";
        Predicate<Station> matches = matches(Stations.WBANNO, wbanno);
        Collection<Station> filtered = Collections2.filter(unfiltered, matches);
        assertEquals("expected one station with wbanno="+wbanno, 1, filtered.size());
        assertEquals("wrong wbanno",wbanno,filtered.iterator().next().getWbanno());

        String wbanno2 = "123123";
        matches = matches(Stations.WBANNO, wbanno, wbanno2);
        filtered = Collections2.filter(unfiltered, matches);
        assertEquals("expected two stations",2, filtered.size());

        wbanno = "foo";
        matches = matches(Stations.WBANNO, wbanno);
        filtered = Collections2.filter(unfiltered, matches);
        assertEquals("expected empty collection wbanno="+wbanno,
                0, filtered.size());

        // stations with null wbannos
        matches = matches(Stations.WBANNO, (String) null);
        filtered = Collections2.filter(unfiltered, matches);
        assertEquals("expected one station with null wbanno", 1, filtered.size());
        assertNull("expected wban=null",filtered.iterator().next().getWbanno());

        String[] empty = {};
        matches = matches(Stations.WBANNO, empty);
        filtered = Collections2.filter(unfiltered, matches);
        assertEquals("expected empty collection empty array",
                0, filtered.size());
    }

}
