package gov.noaa.ncdc.crn.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.noaa.ncdc.crn.domain.Observation;
import gov.noaa.ncdc.crn.domain.ObservationWithData;
import gov.noaa.ncdc.crn.domain.Station;
import gov.noaa.ncdc.crn.service.ObservationService;
import gov.noaa.ncdc.crn.spring.ApplicationContextProvider.Contexts;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml" })
@ActiveProfiles(profiles = "unittest")
public class StationDaoTest {

    @Autowired
    private StationDao stationDao;
    @Autowired
    private ObservationService observationService;
    @Autowired
    private JdbcTemplate template;

    @BeforeClass
    public static final void updateTestData() {
        DataSource dataSource = Contexts.UNIT.getApplicationContext().getBean(DataSource.class);
        JdbcTemplate template = new JdbcTemplate(dataSource);
        // inserts test data and updates period of record to ensure stations 1000, 1121 are considered current stations
        // when testing smst
        Resource resource = new ClassPathResource("data/testdata/scripts/StationDaoTest_data.sql");
        JdbcTestUtils.executeSqlScript(template, resource, false);
    }

    @AfterClass
    public static final void deleteTestData() {
        DataSource dataSource = Contexts.UNIT.getApplicationContext().getBean(DataSource.class);
        JdbcTemplate template = new JdbcTemplate(dataSource);
        // deletes test stations
        Resource resource = new ClassPathResource("data/testdata/scripts/StationDaoTest_data_rollback.sql");
        JdbcTestUtils.executeSqlScript(template, resource, false);
    }

    /*
     * To refresh TABLE1 run SyncFromIsis; then need to update some stations' statuses when unit
     * tests fail; not really ideal, but it's the way things work now
     */
    @Test
    public final void testStationDaoImpl() {
        assertNotNull("StationDao not properly constructed", stationDao);
    }

    /*
     * ------ -------
     * ------ ------- ------------------ --------- --------- -------- -------- --------- ------- --------- 1026 CD0246CA
     * 53877 310308 NC Asheville 8 SSW 35.4945 -82.6142 Y 20001114 N Y 1027 1326 CD0A278A 63891 NA AL Clanton 2 NE
     * 32.8516 -86.6115 N UN N E -1 1552 CD0A426C 23801 NA AL Troy 2 W 31.7901 -86.0004 N UN N E -1 1147 CD026026 UN UN
     * TN Oak Ridge 0 N 36.003 -84.429 N UN Y N -1 1721 xxxxxxxx xxxxx UN UT St.George 19 N xx.xxx -xx.xxx E UN N C -1
     */
    @Test
    public final void testGetStation() {

        // Station sta1112 = stationDao.getStation(1112);

        Station sta1026 = stationDao.getStation(1026);
        assertNotNull("no sta1026", sta1026);
        assertEquals("sta1026 stationid incorrect", 1026, sta1026.getStationId());
        assertEquals("sta1026 goesid incorrect", "CD0246CA", sta1026.getGoesId());
        assertEquals("sta1026 wbanno incorrect", "53877", sta1026.getWbanno());
        assertEquals("sta1026 name incorrect", "NC Asheville 8 SSW", sta1026.getNameString());
        assertEquals("sta1026 latitude incorrect", "35.4945", sta1026.getLatitude());
        assertEquals("sta1026 longitude incorrect", "-82.6142", sta1026.getLongitude());
        assertEquals("sta1026 commcode incorrect", "Y", sta1026.getCommCode());
        assertEquals("sta1026 commdate incorrect", "20001114", sta1026.getCommDate());
        assertFalse("sta1026 testSiteOnly incorrect", sta1026.getTestSiteOnly());
        assertEquals("sta1026 opstat incorrect", "Y", sta1026.getOpStatus());
        assertEquals("sta1026 pairstn incorrect", 1027, sta1026.getPairStationId());
        assertEquals("sta1026 networkId incorrect", 1, sta1026.getNetworkId());
        assertNotNull("sta1026 longname shouldn't be null", sta1026.getLongName());
        assertTrue("sta1026 longname incorrect", sta1026.getLongName().contains("Bierbaum"));
        assertEquals("sta1026 elevation incorrect", Integer.valueOf(2151), sta1026.getElevation());
        assertEquals("sta1026 offset incorrect", -5, sta1026.getOffset());
        assertEquals("sta1026 atddno incorrect", "101", sta1026.getAtddno());
        assertEquals("sta1026 goesSat incorrect", "E", sta1026.getGoesSat());
        assertEquals("sta1026 closeddate incorrect", "UN", sta1026.getClosedDate());

        Station sta1147 = stationDao.getStation(1147);
        assertNull("elevation NA-null didn't translate properly", sta1147.getElevation());
        Station sta1721 = stationDao.getStation(1721);
        assertNotNull("didn't get UT St. George", sta1721);
        assertEquals("didn't get correct opStatus", "C", sta1721.getOpStatus());
    }

    @Test
    public final void testGetStationsBoolean() {
        Map<Integer, Station> stationsGen = stationDao.getStations();
        Map<Integer, Station> stationsWithPorOnly = stationDao.getStations(false);
        Map<Integer, Station> stationsWithoutPor = stationDao.getStations(true);

        // default is only stations with por
        assertEquals("expect default to be same as false arg", stationsGen.size(), stationsWithPorOnly.size());
        // there are several stations in unit test db without a por
        assertTrue("expect stations without por to be greater size",
                stationsWithoutPor.size() > stationsWithPorOnly.size());
        assertNull("don't expect dummy chicago station if false arg", stationsWithPorOnly.get(9997));
        assertNotNull("expect dummy chicago station if true arg", stationsWithoutPor.get(9997));
    }

    @Test
    public final void testGetStations() {
        Map<Integer, Station> stations = stationDao.getStations();
        Station sta1026 = stations.get(1026);
        assertNotNull("no sta1026", sta1026);
        Station sta1326 = stations.get(1326);
        assertNotNull("no sta1326", sta1326);
        Station sta1552 = stations.get(1552);
        assertNotNull("no sta1552", sta1552);

        assertEquals("sta1026 stationid incorrect", 1026, sta1026.getStationId());
        assertEquals("sta1026 goesid incorrect", "CD0246CA", sta1026.getGoesId());
        assertEquals("sta1026 wbanno incorrect", "53877", sta1026.getWbanno());
        assertEquals("sta1026 name incorrect", "NC Asheville 8 SSW", sta1026.getNameString());
        assertEquals("sta1026 latitude incorrect", "35.4945", sta1026.getLatitude());
        assertEquals("sta1026 longitude incorrect", "-82.6142", sta1026.getLongitude());
        assertEquals("sta1026 commcode incorrect", "Y", sta1026.getCommCode());
        assertEquals("sta1026 commdate incorrect", "20001114", sta1026.getCommDate());
        assertFalse("sta1026 testSiteOnly incorrect", sta1026.getTestSiteOnly());
        assertEquals("sta1026 opstat incorrect", "Y", sta1026.getOpStatus());
        assertEquals("sta1026 pairstn incorrect", 1027, sta1026.getPairStationId());

        assertEquals("sta1326 stationid incorrect", 1326, sta1326.getStationId());
        assertEquals("sta1326 goesid incorrect", "CD0A278A", sta1326.getGoesId());
        assertEquals("sta1326 wbanno incorrect", "63891", sta1326.getWbanno());
        assertEquals("sta1326 name incorrect", "AL Clanton 2 NE", sta1326.getNameString());
        assertEquals("sta1326 latitude incorrect", "32.8516", sta1326.getLatitude());
        assertEquals("sta1326 longitude incorrect", "-86.6115", sta1326.getLongitude());
        assertEquals("sta1326 commcode incorrect", "E", sta1326.getCommCode());
        assertEquals("sta1326 commdate incorrect", "UN", sta1326.getCommDate());

        assertEquals("sta1552 stationid incorrect", 1552, sta1552.getStationId());
        assertEquals("sta1552 goesid incorrect", "CD0A426C", sta1552.getGoesId());
        assertEquals("sta1552 wbanno incorrect", "23801", sta1552.getWbanno());
        assertEquals("sta1552 name incorrect", "AL Troy 2 W", sta1552.getNameString());
        assertEquals("sta1552 latitude incorrect", "31.7901", sta1552.getLatitude());
        assertEquals("sta1552 longitude incorrect", "-86.0004", sta1552.getLongitude());
        assertEquals("sta1552 commcode incorrect", "E", sta1552.getCommCode());
        assertEquals("sta1552 commdate incorrect", "UN", sta1552.getCommDate());
    }

    @Test
    public final void testGetWbanStationMap() {
        Map<String, Station> stations = stationDao.getWbanStationMap();
        Station sta1026 = stations.get("53877");
        assertNotNull("no sta1026", sta1026);
        Station sta1326 = stations.get("63891");
        assertNotNull("no sta1326", sta1326);
        Station sta1552 = stations.get("23801");
        assertNotNull("no sta1552", sta1552);

        assertEquals("sta1026 stationid incorrect", 1026, sta1026.getStationId());
        assertEquals("sta1026 goesid incorrect", "CD0246CA", sta1026.getGoesId());
        assertEquals("sta1026 wbanno incorrect", "53877", sta1026.getWbanno());
        assertEquals("sta1026 name incorrect", "NC Asheville 8 SSW", sta1026.getNameString());
        assertEquals("sta1026 latitude incorrect", "35.4945", sta1026.getLatitude());
        assertEquals("sta1026 longitude incorrect", "-82.6142", sta1026.getLongitude());
        assertEquals("sta1026 commcode incorrect", "Y", sta1026.getCommCode());
        assertEquals("sta1026 commdate incorrect", "20001114", sta1026.getCommDate());

        assertEquals("sta1326 stationid incorrect", 1326, sta1326.getStationId());
        assertEquals("sta1326 goesid incorrect", "CD0A278A", sta1326.getGoesId());
        assertEquals("sta1326 wbanno incorrect", "63891", sta1326.getWbanno());
        assertEquals("sta1326 name incorrect", "AL Clanton 2 NE", sta1326.getNameString());
        assertEquals("sta1326 latitude incorrect", "32.8516", sta1326.getLatitude());
        assertEquals("sta1326 longitude incorrect", "-86.6115", sta1326.getLongitude());
        assertEquals("sta1326 commcode incorrect", "E", sta1326.getCommCode());
        assertEquals("sta1326 commdate incorrect", "UN", sta1326.getCommDate());

        assertEquals("sta1552 stationid incorrect", 1552, sta1552.getStationId());
        assertEquals("sta1552 goesid incorrect", "CD0A426C", sta1552.getGoesId());
        assertEquals("sta1552 wbanno incorrect", "23801", sta1552.getWbanno());
        assertEquals("sta1552 name incorrect", "AL Troy 2 W", sta1552.getNameString());
        assertEquals("sta1552 latitude incorrect", "31.7901", sta1552.getLatitude());
        assertEquals("sta1552 longitude incorrect", "-86.0004", sta1552.getLongitude());
        assertEquals("sta1552 commcode incorrect", "E", sta1552.getCommCode());
        assertEquals("sta1552 commdate incorrect", "UN", sta1552.getCommDate());
    }

    @Test
    public final void testGetStationsFromParams() {
        String state = "AZ";
        int[] azStnIds = { 1010, 1011, 1613, 1590 };
        int[] alStnIds = { 1326, 1552 };
        Map<String, Object> params = new HashMap<>();
        params.put("state", state);
        Map<Integer, Station> stations = stationDao.getStationsFromParams(params);
        for (int id : azStnIds) {
            assertNotNull("didn't get " + id, stations.get(id));
        }

        // TODO after conversion, don't need to support this abbreviated format
        params.clear();
        params.put("id", "0102CE");
        stations = stationDao.getStationsFromParams(params);
        assertNotNull("didn't get AK Fairbanks", stations.get(1008));

        params.clear();
        params.put("id", "CD0102CE");
        stations = stationDao.getStationsFromParams(params);
        assertNotNull("didn't get AK Fairbanks (full goesid)", stations.get(1008));

        params.clear();
        params.put("states", Arrays.asList("AZ", "AL"));
        stations = stationDao.getStationsFromParams(params);
        for (int id : azStnIds) {
            assertNotNull("didn't get " + id, stations.get(id));
        }
        for (int id : alStnIds) {
            assertNotNull("didn't get " + id, stations.get(id));
        }

        params.clear();
        params.put("networkId", Integer.valueOf(2));
        stations = stationDao.getStationsFromParams(params);
        for (int id : alStnIds) {
            assertNotNull("didn't get " + id, stations.get(id));
        }

        params.clear();
        params.put("networks", Arrays.asList(1, 2));
        stations = stationDao.getStationsFromParams(params);
        for (int id : alStnIds) {
            assertNotNull("didn't get " + id, stations.get(id));
        }
        for (int id : azStnIds) {
            assertNotNull("didn't get " + id, stations.get(id));
        }

        params.clear();
        params.put("stationId", Integer.valueOf(1326));
        stations = stationDao.getStationsFromParams(params);
        assertNotNull("didn't get 1326", stations.get(1326));// AL Clanton 2 NE
        assertEquals("should only have one station", 1, stations.size());

        params.clear();
        params.put("stationIds", Arrays.asList(1326, 1010));
        stations = stationDao.getStationsFromParams(params);
        assertNotNull("didn't get 1326", stations.get(1326)); // AL Clanton 2 NE
        assertNotNull("didn't get 1010", stations.get(1010)); // AZ
        assertEquals("should have exactly two stations", 2, stations.size());

        params.clear();
        params.put("stationIds", Arrays.asList(1326, 1010));
        params.put("states", Arrays.asList("NC", "AL"));
        stations = stationDao.getStationsFromParams(params);

        assertNotNull("didn't get 1326", stations.get(1326)); // AL Clanton 2 NE
        assertNull("shouldn't get 1026", stations.get(1026)); // NC Asheville 8 SSW
        assertNull("shouldn't get 1010", stations.get(1010)); // AZ

        /*
         * Testing usrcrnonly and crnonly network param
         */
        params.put("network", "usrcrnonly");
        params.put("crnreps", true);

        List<Integer> usrcrnWannabees = template.queryForList("select ID from TALE9", Integer.class);
        Map<Integer, Station> stationMap = stationDao.getStationsFromParams(params);
        for (Station station : stationMap.values()) {
            if (station.getNetworkId() != 3) {
                if (!usrcrnWannabees.contains(station.getStationId())) {
                    fail("station not HCN-M or wannabee: " + station);
                }
            }
        }
        params.clear();
        params.put("network", "usrcrnonly");
        stationMap = stationDao.getStationsFromParams(params);
        for (Station station : stationMap.values()) {
            if (station.getNetworkId() != 3) {
                if (usrcrnWannabees.contains(station.getStationId())) {
                    fail("USRCRN wannabee shouldn't be in list: " + station);
                }
            }
        }
        params.clear();
        params.put("network", "crnonly");
        stationMap = stationDao.getStationsFromParams(params);
        for (Station station : stationMap.values()) {
            if (station.getNetworkId() != 1 && station.getNetworkId() != 2) {
                fail("station not CRN: " + station);
            }
        }
        int numCRNs = stationMap.size();
        params.clear();
        params.put("network", "");
        stationMap = stationDao.getStationsFromParams(params);
        assertTrue("not enough stations found (empty string)", numCRNs < stationMap.size());
        params.clear();
        params.put("network", "foo");
        stationMap = stationDao.getStationsFromParams(params);
        assertTrue("not enough stations found (foo)", numCRNs < stationMap.size());
        params.clear();
        params.put("network", null);
        stationMap = stationDao.getStationsFromParams(params);
        assertTrue("not enough stations found (null)", numCRNs < stationMap.size());

        params.clear();
        params.put("commissioned", true);
        stationMap = stationDao.getStationsFromParams(params);
        assertTrue("not enough stations found (commissioned)", 100 < stationMap.size());
        assertNotNull("didn't find AZ Tucson", stationMap.get(1011));
        assertNotNull("didn't find AZ Williams", stationMap.get(1011));
        assertNull("shouldn't find AK GustavusMock", stationMap.get(6789));

        params.clear();
        params.put("commdate", "20040112");
        stationMap = stationDao.getStationsFromParams(params);
        assertTrue("not enough stations found (commissioned by 20040112)", 20 < stationMap.size());
        assertNotNull("didn't find AZ Tucson", stationMap.get(1011));
        assertNull("shouldn't find AZ Williams (commissioned later)", stationMap.get(1613));
        assertNull("shouldn't find AK Barrow (commissioned Jul 2013)", stationMap.get(1007));
        assertNull("shouldn't find AK GustavusMock (not commissioned)", stationMap.get(6789));

        params.clear();
        params.put("visible", true);
        stationMap = stationDao.getStationsFromParams(params);

        assertTrue("not enough stations found", 20 < stationMap.size());
        assertNotNull("didn't find AZ Tucson", stationMap.get(1011));
        assertNull("shouldn't get nonvisible station: 1000", stationMap.get(1000)); // IN Noblesville 1 E (not a real
        // station)
    }

    @Test(expected = RuntimeException.class)
    public final void testGetStationFromParams() {
        Map<String, Object> params = new HashMap<>();
        // throws exception because multiple stations are retrieved
        stationDao.getStationFromParams(params);
    }

    @Test
    public final void testGetLastModForStation() {
        String lastMod = stationDao.getLastModForStation(1326, 63000, 63010);
        assertEquals("expected different last mod date", "2007121215", lastMod);
    }

    @Test
    public final void testGetStationsWithSmSt() {
        /*
         * In testGetStationsWithSmSt, Noblesville (1000) and Crossville (1121) are tested to determine that they are or
         * are not current soil stations. It is assumed that both stations are current, so update their POR in case
         * someone has modified the period of record table since the last run.
         */
        String maxq = "select max(END) from TABLE10";
        int maxDt = template.queryForInt(maxq);
        // test whether ob already exists
        Map<Integer, Observation> obMap = observationService.getObservations(maxDt, Arrays.asList(1000, 1121));
        if (obMap.size() < 2) {
            if (obMap.get(1000) == null) {
                // stream 6 does not measure soil
                observationService.insertObservation(fakeOb(1000, maxDt, 6));
            }
            if (obMap.get(1121) == null) {
                // stream 8 measures soil
                observationService.insertObservation(fakeOb(1121, maxDt, 8));
            }
            String update = "update TABLE10 set END=?" + " where ID in (1000,1121)";
            template.update(update, maxDt);
        }
        // insert obs for that datetime
        Map<Integer, Station> stations = stationDao.getStationsCurrentlyWithSmSt();
        Station station = stations.get(1121);// tn crossville
        assertNotNull("Crossville TN not found", station);
        station = stations.get(1000); // IN Noblesville 2 E (not a real station
        assertNull("IN Noblesville should not be found", station);
    }

    private ObservationWithData fakeOb(int stationId, int datetimeId, int streamId) {
        Observation ob = new Observation(stationId, datetimeId, streamId, 1, "fakeofile.txt", 152);
        ObservationWithData owd = new ObservationWithData(ob);
        owd.addNewElementValue(439, new BigDecimal("1.2"));
        owd.addNewElementValue(440, new BigDecimal("4.3"));
        return owd;
    }

    @Test
    public final void testGetGeonorCapacity() {
        Float capacity = stationDao.getGeonorCapacity(1028); // KY Versailles
        float expected = 600;
        assertNotNull("null capacity", capacity);
        assertEquals("got wrong capacity for versailles", expected, capacity, 0.000001);
        capacity = stationDao.getGeonorCapacity(1508); // yellowstone
        expected = 1000;
        assertEquals("got wrong capacity for yellowstone", expected, capacity, 0.000001);

    }

}
