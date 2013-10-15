package gov.noaa.ncdc.crn.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.noaa.ncdc.crn.dao.ConnectionDao;
import gov.noaa.ncdc.crn.dao.PorDao;
import gov.noaa.ncdc.crn.dao.StationDao;
import gov.noaa.ncdc.crn.domain.POR;
import gov.noaa.ncdc.crn.domain.Station;
import gov.noaa.ncdc.crn.domain.StationWithCompleteCurrentMetadata;
import gov.noaa.ncdc.crn.spring.ApplicationContextProvider.Contexts;
import gov.noaa.ncdc.crn.util.TimeUtils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;
//TODO replace when StationService switches to mybatis impls
//import gov.noaa.ncdc.crn.persistence.PorDao;
//import gov.noaa.ncdc.crn.persistence.StationDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations={"classpath:application-context.xml"})
@ActiveProfiles("unittest")
public class StationServiceTest {

	@Autowired
	private StationService service;
	@Autowired
	private StationDao stationDao;
	@Autowired
	private PorDao porDao;
    @Autowired
    private ConnectionDao connectionDao;
	@Autowired
	private BasicDataSource dataSource;
	@Autowired
	private SqlSession session;

    @SuppressWarnings("deprecation") // TODO when upgrade to spring 3.2 "remove all the Simple prefixes"
	@BeforeClass
    public static final void updateTestData() {
    	DataSource dataSource= Contexts.UNIT.getApplicationContext().getBean(DataSource.class);
		SimpleJdbcTemplate template = new SimpleJdbcTemplate(dataSource);
		// inserts test data into TABLE1, TABLE2, and updates 
		// period of record to ensure stations 1000, 1121 are considered current stations
		// when testing smst
		Resource resource = new ClassPathResource("data/testdata/scripts/StationDaoTest_data.sql");
		SimpleJdbcTestUtils.executeSqlScript(template, resource, false);
    }
    @AfterClass
    public static final void deleteTestData() {
    	DataSource dataSource= Contexts.UNIT
    			.getApplicationContext().getBean(DataSource.class);
		SimpleJdbcTemplate template = new SimpleJdbcTemplate(dataSource);
		// deletes test stations from tables
		Resource resource = new ClassPathResource("data/testdata/scripts/StationDaoTest_data_rollback.sql");
		SimpleJdbcTestUtils.executeSqlScript(template, resource, false);
    }

	@Before
	public final void setUp() {
	    connectionDao.flushDataCache();
	}

//	----------  --------- ------- --------------------- 
//	1068        Y         C       OK Goodwell 2 E      
//	1147        T         N       TN Oakridge 0 N      
//	1650        N         A       NM Dulce 1 NW        
//	1305        T         C       VA Sterling 0 N        
	@Test
	public final void testGetClosedStations()
	{
		Map<Integer,Station> stations = service.getClosedStations();
		assertNotNull("st. george not in list",stations.get(1721));
		assertNull("oakridge should not be in list", stations.get(1147));
		assertNull("dulce should not be in list", stations.get(1650));
		assertNull("va sterling should not be in list", stations.get(1305));
	}
	@Test
	public final void testGetCommissionedStations() {
		Map<Integer,Station> commissioned = service.getCommissionedStations();
		assertTrue("expect ~114 commissioned stations", commissioned.size()>100);
		for (Station station : commissioned.values()) {
			assertEquals("station not commissioned: "+station, 
					"Y", station.getCommCode());
		}
	}

	@Test
	public final void testStationService()
	{
		assertNotNull("stationService not properly constructed",service);
	}

	/*
	 * ----------- -------- ------- ------------------ --------- ---------- --------- ---------  -------------- -------  ---------
	 * 1026        0246CA   53877   NC Asheville 8 SSW   35.4945   -82.6142   Y         20001114     N             Y      1027
	 * 1326        0A278A   63891   AL Clanton 2 NE      32.8516   -86.6115   N         UN           N             E      -1
	 * 1552        0A426C   23801   AL Troy 2 W          31.7901   -86.0004   N         UN           N             E      -1
	 * 1147        026026   UN      TN Oak Ridge 0 N     36.003    -84.429    N         UN           Y             N      -1                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          
	 */
	@Test
	public final void testGetStation()
	{
		Station sta1026 = service.getStation(1026);
		assertNotNull("no sta1026",sta1026);
		assertEquals("sta1026 stationid incorrect",1026,sta1026.getStationId());
		assertEquals("sta1026 goesid incorrect","CD0246CA",sta1026.getGoesId());
		assertEquals("sta1026 wbanno incorrect","53877",sta1026.getWbanno());
		assertEquals("sta1026 name incorrect",
		    "NC Asheville 8 SSW",sta1026.getNameString());
		assertEquals("sta1026 latitude incorrect",
		    "35.4945",sta1026.getLatitude());
		assertEquals("sta1026 longitude incorrect",
		    "-82.6142",sta1026.getLongitude());
		assertEquals("sta1026 commcode incorrect","Y",sta1026.getCommCode());
		assertEquals("sta1026 commdate incorrect",
		    "20001114",sta1026.getCommDate());
		assertFalse("sta1026 testSiteOnly incorrect",sta1026.getTestSiteOnly());
		assertEquals("sta1026 opstat incorrect","Y",sta1026.getOpStatus());
		assertEquals("sta1026 pairstn incorrect",
		    1027,sta1026.getPairStationId());
		Station sta1147 = service.getStation(1147);
		assertNull("elevation NA-null didn't translate properly",
		    sta1147.getElevation());
		Station sta6789 = service.getStation(6789);
		assertNotNull("station 6789 is null",
				sta6789);
	}

	@Test
	public final void testGetStations() {
		Map<Integer,Station> stations = service.getStations();
		Station sta1026 = stations.get(1026);
		assertNotNull("no sta1026",sta1026);
		Station sta1326 = stations.get(1326);
		assertNotNull("no sta1326",sta1326);
		Station sta1552 = stations.get(1552);
		assertNotNull("no sta1552",sta1552);

		assertEquals("sta1026 stationid incorrect",1026,sta1026.getStationId());
		assertEquals("sta1026 goesid incorrect","CD0246CA",sta1026.getGoesId());
		assertEquals("sta1026 wbanno incorrect","53877",sta1026.getWbanno());
		assertEquals("sta1026 name incorrect",
		    "NC Asheville 8 SSW",sta1026.getNameString());
		assertEquals("sta1026 latitude incorrect",
		    "35.4945",sta1026.getLatitude());
		assertEquals("sta1026 longitude incorrect",
		    "-82.6142",sta1026.getLongitude());
		assertEquals("sta1026 commcode incorrect","Y",sta1026.getCommCode());
		assertEquals("sta1026 commdate incorrect",
		    "20001114",sta1026.getCommDate());
		assertFalse("sta1026 testSiteOnly incorrect",sta1026.getTestSiteOnly());
		assertEquals("sta1026 opstat incorrect","Y",sta1026.getOpStatus());
		assertEquals("sta1026 pairstn incorrect",
		    1027,sta1026.getPairStationId());

		assertEquals("sta1326 stationid incorrect",1326,sta1326.getStationId());
		assertEquals("sta1326 goesid incorrect","CD0A278A",sta1326.getGoesId());
		assertEquals("sta1326 wbanno incorrect","63891",sta1326.getWbanno());
		assertEquals("sta1326 name incorrect",
		    "AL Clanton 2 NE",sta1326.getNameString());
		assertEquals("sta1326 latitude incorrect",
		    "32.8516",sta1326.getLatitude());
		assertEquals("sta1326 longitude incorrect",
		    "-86.6115",sta1326.getLongitude());
		assertEquals("sta1326 commcode incorrect","E",sta1326.getCommCode());
		assertEquals("sta1326 commdate incorrect","UN",sta1326.getCommDate());

		assertEquals("sta1552 stationid incorrect",1552,sta1552.getStationId());
		assertEquals("sta1552 goesid incorrect","CD0A426C",sta1552.getGoesId());
		assertEquals("sta1552 wbanno incorrect","23801",sta1552.getWbanno());
		assertEquals("sta1552 name incorrect",
		    "AL Troy 2 W",sta1552.getNameString());
		assertEquals("sta1552 latitude incorrect",
		    "31.7901",sta1552.getLatitude());
		assertEquals("sta1552 longitude incorrect",
		    "-86.0004",sta1552.getLongitude());
		assertEquals("sta1552 commcode incorrect","E",sta1552.getCommCode());
		assertEquals("sta1552 commdate incorrect","UN",sta1552.getCommDate());
	}

	@Test
	public void testGetWbanStationMap()
	{
		Map<String,Station> stations = service.getWbanStationMap();

		Station sta1026 = stations.get("53877");
		assertNotNull("no sta1026",sta1026);
		Station sta1326 = stations.get("63891");
		assertNotNull("no sta1326",sta1326);
		Station sta1552 = stations.get("23801");
		assertNotNull("no sta1552",sta1552);

		assertEquals("sta1026 stationid incorrect",1026,sta1026.getStationId());
		assertEquals("sta1026 goesid incorrect","CD0246CA",sta1026.getGoesId());
		assertEquals("sta1026 wbanno incorrect","53877",sta1026.getWbanno());
		assertEquals("sta1026 name incorrect",
		    "NC Asheville 8 SSW",sta1026.getNameString());
		assertEquals("sta1026 latitude incorrect",
		    "35.4945",sta1026.getLatitude());
		assertEquals("sta1026 longitude incorrect",
		    "-82.6142",sta1026.getLongitude());
		assertEquals("sta1026 commcode incorrect","Y",sta1026.getCommCode());
		assertEquals("sta1026 commdate incorrect",
		    "20001114",sta1026.getCommDate());

		assertEquals("sta1326 stationid incorrect",1326,sta1326.getStationId());
		assertEquals("sta1326 goesid incorrect","CD0A278A",sta1326.getGoesId());
		assertEquals("sta1326 wbanno incorrect","63891",sta1326.getWbanno());
		assertEquals("sta1326 name incorrect",
		    "AL Clanton 2 NE",sta1326.getNameString());
		assertEquals("sta1326 latitude incorrect",
		    "32.8516",sta1326.getLatitude());
		assertEquals("sta1326 longitude incorrect",
		    "-86.6115",sta1326.getLongitude());
		assertEquals("sta1326 commcode incorrect","E",sta1326.getCommCode());
		assertEquals("sta1326 commdate incorrect","UN",sta1326.getCommDate());

		assertEquals("sta1552 stationid incorrect",1552,sta1552.getStationId());
		assertEquals("sta1552 goesid incorrect","CD0A426C",sta1552.getGoesId());
		assertEquals("sta1552 wbanno incorrect","23801",sta1552.getWbanno());
		assertEquals("sta1552 name incorrect",
		    "AL Troy 2 W",sta1552.getNameString());
		assertEquals("sta1552 latitude incorrect",
		    "31.7901",sta1552.getLatitude());
		assertEquals("sta1552 longitude incorrect",
		    "-86.0004",sta1552.getLongitude());
		assertEquals("sta1552 commcode incorrect","E",sta1552.getCommCode());
		assertEquals("sta1552 commdate incorrect","UN",sta1552.getCommDate());
	}

	@Test
	public final void testGetStationsListOfInteger() 
	{
		List<Integer> idList = new ArrayList<Integer>();
		idList.add(1026);
		idList.add(1326);
		idList.add(1552);
        Map<Integer,Station> stations = service.getStations(idList);
		assertEquals("incorrect number of stations mapped ("+
		    stations.keySet().size()+")",
		    3,stations.keySet().size());
		Station sta1026 = stations.get(1026);
		assertNotNull("no sta1026",sta1026);
		Station sta1326 = stations.get(1326);
		assertNotNull("no sta1326",sta1326);
		Station sta1552 = stations.get(1552);
		assertNotNull("no sta1552",sta1552);

		assertEquals("sta1026 stationid incorrect",1026,sta1026.getStationId());
		assertEquals("sta1026 goesid incorrect","CD0246CA",sta1026.getGoesId());
		assertEquals("sta1026 wbanno incorrect","53877",sta1026.getWbanno());
		assertEquals("sta1026 name incorrect",
		    "NC Asheville 8 SSW",sta1026.getNameString());
		assertEquals("sta1026 latitude incorrect",
		    "35.4945",sta1026.getLatitude());
		assertEquals("sta1026 longitude incorrect",
		    "-82.6142",sta1026.getLongitude());
		assertEquals("sta1026 commcode incorrect","Y",sta1026.getCommCode());
		assertEquals("sta1026 commdate incorrect",
		    "20001114",sta1026.getCommDate());

		assertEquals("sta1326 stationid incorrect",1326,sta1326.getStationId());
		assertEquals("sta1326 goesid incorrect","CD0A278A",sta1326.getGoesId());
		assertEquals("sta1326 wbanno incorrect","63891",sta1326.getWbanno());
		assertEquals("sta1326 name incorrect",
		    "AL Clanton 2 NE",sta1326.getNameString());
		assertEquals("sta1326 latitude incorrect",
		    "32.8516",sta1326.getLatitude());
		assertEquals("sta1326 longitude incorrect",
		    "-86.6115",sta1326.getLongitude());
		assertEquals("sta1326 commcode incorrect","E",sta1326.getCommCode());
		assertEquals("sta1326 commdate incorrect","UN",sta1326.getCommDate());

		assertEquals("sta1552 stationid incorrect",1552,sta1552.getStationId());
		assertEquals("sta1552 goesid incorrect","CD0A426C",sta1552.getGoesId());
		assertEquals("sta1552 wbanno incorrect","23801",sta1552.getWbanno());
		assertEquals("sta1552 name incorrect",
		    "AL Troy 2 W",sta1552.getNameString());
		assertEquals("sta1552 latitude incorrect",
		    "31.7901",sta1552.getLatitude());
		assertEquals("sta1552 longitude incorrect",
		    "-86.0004",sta1552.getLongitude());
		assertEquals("sta1552 commcode incorrect","E",sta1552.getCommCode());
		assertEquals("sta1552 commdate incorrect","UN",sta1552.getCommDate());
	}


	@Test
	public void testGetStationsForNetworks()
	{
		Map<Integer, Station> stations = service.getStationsForNetworks(1,2);
		int[] alStnIds = {1326,1552};
		for (int id:alStnIds) {
			assertNotNull("didn't get "+id,stations.get(id));
		}
		int[] azStnIds = {1010,1011,1613,1590};
		for (int id:azStnIds) {
			assertNotNull("didn't get "+id,stations.get(id));
		}
        stations = service.getStationsForNetworks(2);
		for (int id:alStnIds) {
			assertNotNull("didn't get "+id,stations.get(id));
		}
		assertEquals("should have gotten only 17 al hcnm stations",
		    17,stations.size());
	}

	@Test
	public void testGetStationsFromStates()
	{
		/* multiple states */
		int[] stnIds = {1010,1011,1613,1590,1510};
        Map<Integer,Station> stations
            = service.getStationsFromStates("AZ","IN");
		for (int id:stnIds){
			assertNotNull("didn't get "+id,stations.get(id));
		}

		/* single state */
		int[] azSntIds = {1010,1011,1613,1590};
        stations = service.getStationsFromStates("AZ");
		for (int id:azSntIds){
			assertNotNull("didn't get "+id,stations.get(id));
		}    }


	@Test(expected=IllegalArgumentException.class)
	public void testGetStationsFromStatesBadState()
	{
		// states must be 2 chars long
        Map<Integer,Station> stations
            = service.getStationsFromStates("AZ","IN","FLA");
	}
	@Test
	public void testGetLastModForStation()
	{
		String lastMod = service.getLastModForStation(1326, 63000, 63010);
		assertEquals("expected different last mod date","2007121215",lastMod);
	}
	/*
	 * ----------- -------- ------- ------------------ --------- ---------- --------- ---------
	 * 1026        0246CA   53877   NC Asheville 8 SSW   35.4945   -82.6142   Y         20001114
	 * 1326        0A278A   63891   AL Clanton 2 NE      32.8516   -86.6115   N         UN
	 * 1552        0A426C   23801   AL Troy 2 W          31.7901   -86.0004   N         UN   
	 * 9997      CD1A426C   99801   IL Chicago 2 W       29.7901   -76.0004   N         UN                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               
	 */
	// TODO insert a station w/ CD, DA goes id and test that functionality
	@Test
	public void testStationFromIdentifier() 
	{
		String[] avl8ssws = {"1026","0246CA","53877"};
		String[] clant2nes = {"1326","0A278A","63891"};
		String[] troy2ws = {"1552","0A426C","23801"};
		String[] bads = {"9999","123","HIMOM"};

		Station result = null;
		Station expected = service.getStation(1026);
		for (String id : avl8ssws) {
			result = service.stationFromIdentifier(id);
			assertEquals("didn't retrieve Avl 8 SSW with id="+id,
			    expected,result);
		}
		expected = service.getStation(1326);
		for (String id : clant2nes) {
			result = service.stationFromIdentifier(id);
			assertEquals("didn't retrieve Clanton 2 NE with id="+id,
			    expected,result);
		}
		expected = service.getStation(1552);
		for (String id : troy2ws) {
			result = service.stationFromIdentifier(id);
			assertEquals("didn't retrieve Troy 2 W with id="+id,
			    expected,result);
		}
		for (String id : bads) {
			result = service.stationFromIdentifier(id);
			assertNull("didn't expect a station with id="+id,result);
		}
		String[] chicagoGoesIds = {"CD1A426C","1A426C"};
		for (String id : chicagoGoesIds) {
			result = service.stationFromIdentifier(id);
			assertNotNull("expect chicago from id="+id,result);
			assertEquals("expect chicago from id="+id,
			    9997,result.getStationId());
		}
	}
	/*
	 * ----------- -------- 
	 * 1026        101
	 * 1326        1110
	 * 1552        1112                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               
	 */
	@Test
	public void testStationFromAtddno() 
	{
		String[] atddnos = {"101","1110","1112"};
		int[] stationIds = {1026,1326,1552};
		Station result = null;
		for (int i=0;i<atddnos.length;i++) {
			String atddno = atddnos[i];
			result = service.stationFromAtddno(atddno);
			assertNotNull("expect result from id="+atddno,result);
			assertEquals("expect different stationid from id="+atddno,
			    stationIds[i],result.getStationId());
		}
	}
	@Test(expected=NumberFormatException.class)
	public void testStationFromAtddnoNan() {
		Station result = service.stationFromAtddno("FOO");
	}
	@Test(expected=IllegalArgumentException.class)
	public void testStationFromAtddnoNegative() { 
		Station result = service.stationFromAtddno("-1112");
	}

	/*
	 * ----------- -------- ------- ------------------ --------- ---------- --------- ---------
	 * 1026        0246CA   53877   NC Asheville 8 SSW   35.4945   -82.6142   Y         20001114
	 * 1326        0A278A   63891   AL Clanton 2 NE      32.8516   -86.6115   N         UN
	 * 1552        0A426C   23801   AL Troy 2 W          31.7901   -86.0004   N         UN     
	 * 1000        05FE46   987654  IN Noblesville 2 E                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
	 */
	@Test
	public final void testStationsFromIdentifiers()
	{
		// 1000 NEEDS POR ENTRY
		String[] ids = {"1026","1000","1326","1552","1139"};
		Map<Integer,Station> stations = service.getStations();
		List<Station> results
		    = service.stationsFromIdentifiers(Arrays.asList(ids));
		assertEquals("expected 5 stations",5,results.size());
		for (String id : ids) {
			assertTrue("station not found: "+id,
			    results.contains(stations.get(Integer.valueOf(id))));
		}
		String[] goesids = {"0246CA","0A278A","0A426C","05FE46","05C2EA"};
		results = service.stationsFromIdentifiers(Arrays.asList(goesids));
		assertEquals("expected 5 stations",5,results.size());
		for (String id : ids) {
			assertTrue("station not found: "+id,
			    results.contains(stations.get(Integer.valueOf(id))));
		}
		/* TODO add this test after syncfromisis is changed */
		String[] goesids2 = {"CD0246CA","CD0A278A","CD0A426C","CD05FE46","CD05C2EA"};
		results = service.stationsFromIdentifiers(Arrays.asList(goesids2));
		assertEquals("expected 5 stations",5,results.size());
		for (String id : ids) {
			assertTrue("station not found: "+id,
			    results.contains(stations.get(Integer.valueOf(id))));
		}
		String[] wbans = {"53877","63891","23801","987654","94080"};
		results = service.stationsFromIdentifiers(Arrays.asList(wbans));
		assertEquals("expected 5 stations",5,results.size());
		for (String id : ids) {
			assertTrue("station not found: "+id,
			    results.contains(stations.get(Integer.valueOf(id))));
		}

		String[] bads = {"foo",null};
		results = service.stationsFromIdentifiers(Arrays.asList(bads));
		assertEquals("didn't expect results from data not present",
		    0,results.size());
	}
	@Test(expected=IllegalArgumentException.class)
	public final void testStationsFromIdentifiersEmptyArgs()
	{
		String[] ids = {};
		List<Station> results
		    = service.stationsFromIdentifiers(Arrays.asList(ids));
	}
	@Test
	public final void testStationFromName()
	{
		Station station = service.stationFromName("AR", "Batesville", "8 WNW");
		assertTrue("Couldn't retrieve a station by name.",
		    station.getNameString().equalsIgnoreCase("AR Batesville 8 WNW"));
		station = service.stationFromName("AL", "Gadsden", "19 N");
		assertTrue("Couldn't retrieve a station by name.",
		    station.getNameString().equalsIgnoreCase("AL Gadsden 19 N"));
		// TODO implemnt test where slv has two transmitters
	}
	@Test(expected=IllegalArgumentException.class)
	public final void testStationFromNameBadVectorNumber() {
		// throw exception when checking precondition that vector is valid
		Station station = service.stationFromName("AL", "Gadsden", "GG N");
	}
	@Test(expected=IllegalArgumentException.class)
	public final void testStationFromNameBadVectorFormat() {
		// throw exception when checking precondition that vector is valid
		Station station = service.stationFromName("AL", "Gadsden", "10N");
	}


	@Test(expected=IllegalArgumentException.class)
	public final void testStationFromNameInvVector()
	{
		// vector not valid
		service.stationFromName("AR", "Batesville", "foo");
	}
	@Test(expected=NullPointerException.class)
	public final void testStationFromNameNulls1()
	{
		service.stationFromName("AL", "Gadsden", null);
	}
	@Test(expected=NullPointerException.class)
	public final void testStationFromNameNulls2()
	{
		service.stationFromName("AL", null, "19 N");
	}
	@Test(expected=NullPointerException.class)
	public final void testStationFromNameNulls3()
	{
		service.stationFromName(null, "Gadsden", "19 N");
	}
	@Test(expected=IllegalArgumentException.class)
	public final void testStationFromNameBadArg1()
	{
		service.stationFromName("ALF", "Gadsden", "19 N");
	}
	@Test(expected=IllegalArgumentException.class)
	public final void testStationFromNameBadArg2()
	{
		service.stationFromName("A", "Gadsden", "19 N");
	}
	@Test(expected=IllegalArgumentException.class)
	public final void testStationFromNameBadArg3()
	{
		service.stationFromName("AL", "Gadsden", "19 F");
	}
	@Test(expected=IllegalArgumentException.class)
	public final void testStationFromNameBadArg4()
	{
		service.stationFromName("AL", "Gadsden", "190 N");
	}
	@Test(expected=IllegalArgumentException.class)
	public final void testStationFromNameBadArg5()
	{
		service.stationFromName("AL", "Gadsden", "-19 N");
	}
	@Test(expected=IllegalArgumentException.class)
	public final void testStationFromNameBadArg6()
	{
		service.stationFromName("ALF", "Gadsden", "GR N");
	}

	@Test
	@Transactional
	@Rollback(value=true)
	public void testGetVisibleStations()
	{
		Map<Integer,Station> visibleStations = service.getVisibleStations();;
		Map<Integer,Station> stations = service.getStations();
		// IN Noblesville 1 E (not a real station)
		assertNotNull("should get nonvisible station in regular query",
		    stations.get(1000));
		assertNull("shouldn't get nonvisible station: 1000",
		    visibleStations.get(1000)); 
		// VA Sterling 0 N (closed test station)
		assertNotNull("should get nonvisible station in regular query",
			    stations.get(1305));
		assertNull("shouldn't get test station: 1305",
				visibleStations.get(1305)); 
		int[] stnIds = {1010,1011,1613,1590,1510};
		for (int id:stnIds){
			assertNotNull("didn't get "+id,stations.get(id));
		}
		assertTrue("visible stations size not less than than all stations", 
				visibleStations.size()<stations.size());
		assertNotNull("Should get closed station UT St. George", stations.get(1721));
	}

    @Test
    @Transactional
    @Rollback(value=true)
    public void testGetVisibleStationsCollection()
    {
        Integer[] stnIds = {1000,1010,1011,1613,1590,1721,1305}; // includes closed 1721 UT St. George
        List<Integer> paramIds = Arrays.asList(stnIds);
        Map<Integer,Station> visibleStations 
            = service.getVisibleStations(paramIds);;
        Map<Integer,Station> stations = service.getStations();
        assertNotNull("should get nonvisible station in regular query",
            stations.get(1000));
        // IN Noblesville 1 E (not a real station)
        assertNull("shouldn't get nonvisible station: 1000",
            visibleStations.get(1000)); 
		// VA Sterling 0 N (closed test station)
		assertNotNull("should get nonvisible station in regular query",
			    stations.get(1305));
		assertNull("shouldn't get nonvisible station: 1305",
		    visibleStations.get(1305));
        assertNull("shouldn't get visible station not in list: 1510",
            visibleStations.get(1510)); 
        for (int id:stnIds){
            assertNotNull("didn't get "+id,stations.get(id));
        }
        assertTrue("visible stations size not less than than all stations", 
            visibleStations.size()<stations.size());
    }

	@Test
	public void testGetVisibleStationsForNetworks()
	{
		Map<Integer,Station> stations12 = 
		    service.getVisibleStationsForNetworks(1,2);
		Map<Integer,Station> stations = service.getStations();
		// IN Noblesville 1 E (not a real station)
		assertNull("shouldn't get nonvisible station: 1000",
		    stations12.get(1000)); 
		// VA Sterling 0 N (closed test station)
		assertNull("shouldn't get test station: 1305",
		    stations12.get(1305)); 
		int[] stnIds = {1010,1011,1613,1590,1510};
		for (int id:stnIds){
			assertNotNull("didn't get "+id,stations.get(id));
		}
		assertTrue("visible stations size not less than than all stations", 
		    stations12.size()<stations.size());
		// IN Noblesville 1 E (not a real station)
		assertNull("shouldn't get nonvisible station: 1000",
		    stations12.get(1000));
		/* single network */
		Map<Integer,Station> visibleStations1
		    = service.getVisibleStationsForNetworks(1);
		Map<Integer,Station> stations1 = service.getStationsForNetworks(1);
		Map<Integer,Station> visibleStationsAll = service.getVisibleStations();
		// IN Noblesville 1 E (not a real station)
		assertNull("shouldn't get nonvisible station: 1000",
		    visibleStations1.get(1000)); 
		for (int id:stnIds){
			assertNotNull("didn't get "+id,stations1.get(id));
		}
		assertTrue("visible stations size not less than than all stations", 
		    visibleStations1.size()<stations1.size());
		assertTrue("visible stations size for network=1 not less than than " +
				"all visible stations",
		    visibleStations1.size()<visibleStationsAll.size());
		// verify closed station is visible
		Map<Integer,Station> stations3 = 
			    service.getVisibleStationsForNetworks(3);
		assertNotNull("Should get closed station UT St. George", stations3.get(1721));
	}

	// TODO this unit test is not passing; need to work on it, but leaving for now
//	@Test
	@Transactional
	@Rollback(true)
	public final void testStationCache() {
		StationWithCompleteCurrentMetadata dummy1 =
				dummyStationWCCM(9989, "WHAT", "1235", -5, 1);
		StationWithCompleteCurrentMetadata dummy2 =
				dummyStationWCCM(9966, "WHAT", "1234", -6, 2);

		Map<Integer,Station> stations = service.getStations();
		// verifies dummy stations don't already exist in the database
		assertNull(stations.get(dummy1.getStationId()));
		assertNull(stations.get(dummy2.getStationId()));

		// insert new station, bypassing MyBatis
		SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(dataSource)
		    .withTableName("TABLE1");
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("ID1", dummy1.getStationId());
		params.put("ID2", dummy1.getWbanno());
		params.put("ID3", dummy1.getGoesId());
		params.put("COL1",dummy1.getOffset());
		params.put("COL5",dummy1.getNetworkId());
		int rows = jdbcInsert.execute(params);
		// validates that one row was inserted into the database
		assertEquals(1,rows);
		// getstations joins on period of record table, so need an entry there to retrieve the
		// station later 
		jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("TABLE4");
		params.clear();
		params.put("ID1", dummy1.getStationId());
		params.put("START", 94613);
		params.put("END", 94614);
		rows = jdbcInsert.execute(params);
		// validates that one row was inserted into the database
		assertEquals(1,rows);

		// verifies that cache was used
		stations = service.getStations();
		assertNull(stations.get(dummy1.getStationId()));
		assertNull(stations.get(dummy2.getStationId()));

		POR dummypor = new POR(dummy2.getStationId(),94616,94619);
		porDao.insertPor(dummypor);
		session.flushStatements();
		// validate the cache was flushed
		stations = service.getStations();

		Station result = stations.get(dummy1.getStationId());
		assertNotNull(result);
		assertEquals(dummy1.getWbanno(),result.getWbanno());
		assertEquals(dummy1.getGoesId(),result.getGoesId());
		assertEquals(dummy1.getOffset(),result.getOffset());
		result = stations.get(dummy2.getStationId());
		assertNotNull(result);
		assertEquals(dummy2.getWbanno(),result.getWbanno());
		assertEquals(dummy2.getGoesId(),result.getGoesId());
		assertEquals(dummy2.getOffset(),result.getOffset());
	}

	private static StationWithCompleteCurrentMetadata dummyStationWCCM(int stationId,
			String goesId, String wbanno, int offset, int networkid) {
		final String state = "NC";
		final String city = "Asheville";
		final String vector = "99E";
		final String longname = "I'm a dummy station";
		final String atddno = "ATDD1";
		final String longitude = "85.2";
		final String latitude = "-35.4";
		final Integer elevation = Integer.valueOf(1895);
		final String commCode = "Y";
		// TODO also try UN (default) but shouldn't null automatically map to UN?
		final String commDate = "20121212";
		final String opStatus = "Y";
		final int pairStationId = -1;
		final Boolean testSite = Boolean.TRUE;
		final String goesSat = "E";
		// TODO test null replacement ("UN')
		// TODO closeddate actually was not set
		final String closedDate = "20121213";
		
		StationWithCompleteCurrentMetadata station =
				new StationWithCompleteCurrentMetadata(stationId, state, city, vector, 
					networkid, longname, goesId, wbanno, atddno, offset, longitude, latitude, 
					elevation, commCode, commDate, opStatus, pairStationId, testSite, goesSat, 
					closedDate, false);
		station.setGovernmentPropertyId("fake_govt_id");
		station.setRaingaugeDepth(Integer.valueOf(1000));
		station.setRhSerialNo("RH3456");
		station.setCountry("US");
		long millis = TimeUtils.createUTCCalendar("201212130520").getTimeInMillis();
		station.setLastEvent(new Date(millis));
		millis = TimeUtils.createUTCCalendar("201212121558").getTimeInMillis();
		station.setActive(new Date(millis));
		station.setTimezone("EST");
		// TODO Noaa region - needs to be added to unit test db
		// TODO coopno
		return station;
	}

	/* TODO using this method for the update fails miserably. methodically 
	 * figure out which of these fields are not nullable 
	 */
	private StationWithCompleteCurrentMetadata dummyStationWCCMwithnulls(int stationId,
			String goesId, String wbanno, int offset, int networkid) {
		return new StationWithCompleteCurrentMetadata(stationId, null, null, null, 
					networkid, null, goesId, wbanno, null, offset, null, null, 
					null, null, null, null, -1, true, null, null, false);
	}

	/*
---------- -------- ------ ----------------- --------------------------------
      1118 0460E8   64756  NY Millbrook 3 W  UN                               
      1783 0B039C   53184  AZ Meadview 7 N   UN                               
      1650 0B039C   03075  NM Dulce 1 NW     20100801
	 */
	@Test
	public void testStationFromGoesId() {
		// dulce closed date is 20110304; datetimeid 91264
		int datetimeId = 91264; // near the end of Dulce's closed date
		String goesId = "0460E8";
		// test that a goesid associated with just one station succeeds
		Station station = service.stationFromGoesId(goesId, 100000);
		assertNotNull("didn't get Millbrook",station);
		assertEquals("Millbrook stationid incorrect",1118,station.getStationId());
		
		// test that a goesid associated with two stations returns the earlier
		// station when the datetime precedes its POR
		goesId = "0B039C"; // dulce/meadview
		station = service.stationFromGoesId(goesId, 1000);
		assertNotNull("didn't get Dulce",station);
		assertEquals("Dulce stationid incorrect",1650,station.getStationId());
		
		// test that a goesid associated with two stations returns the earlier 
		// station when the datetime precedes its closed date
		goesId = "0B039C"; // dulce/meadview
		station = service.stationFromGoesId(goesId, datetimeId-2*24);
		assertNotNull("didn't get Dulce",station);
		assertEquals("Dulce stationid incorrect",1650,station.getStationId());
		
		// test that a goesid associated with two stations returns the earlier 
		// station when the datetime follows its closed date but is within the 
		// grace period
		datetimeId += 24*6; // 5-6 days after Dulce's closed date
		goesId = "0B039C"; // dulce/meadview
		station = service.stationFromGoesId(goesId, datetimeId);
		assertNotNull("didn't get Dulce",station);
		assertEquals("Dulce stationid incorrect",1650,station.getStationId());
		
		// test that a goesid associated with two stations returns the later 
		// station when the datetime follows the grace period following the
		// earlier station's closed date but precedes the later station's POR
		datetimeId += 24*3; // 8-9 days after Dulce's closed date
		goesId = "0B039C"; // dulce/meadview
		station = service.stationFromGoesId(goesId, datetimeId);
		assertNotNull("didn't get Meadview",station);
		assertEquals("Meadview stationid incorrect",1783,station.getStationId());
		
		// test that a goesid associated with two stations returns the later 
		// station when the datetime follows its POR
		goesId = "0B039C"; // dulce/meadview
		station = service.stationFromGoesId(goesId, 150000);
		assertNotNull("didn't get Meadview",station);
		assertEquals("Meadview stationid incorrect",1783,station.getStationId());

		// test when a goes id belongs to a station which has not yet tx data
//		GOES ID 14247C, datetimeId 100334 [1778 UT Vernal 23 SSE]
//		GOES ID 1C8022, datetimeId 101480 [7779 AK Tok 70 SE]
//		GOES ID 14419A, datetimeId 100334 [1782 AZ Fredonia 7 SSE]
		station = service.stationFromGoesId("14247C", 100334);
		assertNotNull("didn't get Vernal",station);
		assertEquals("Vernal stationid incorrect",1778,station.getStationId());
		station = service.stationFromGoesId("1C8022", 101480);
		assertNotNull("didn't get Tok bkup",station);
		assertEquals("Tok bkup stationid incorrect",7779,station.getStationId());
		station = service.stationFromGoesId("14419A", 100334);
		assertNotNull("didn't get Fredonia",station);
		assertEquals("Fredonia stationid incorrect",1782,station.getStationId());
		
		// test when station has not yet tx data and goesId is being reused
		// (other station closed)
		goesId="654321"; // Indianapolis dummy with POR (closed 20100801) AND Ames, IA dummy with no POR
		station = service.stationFromGoesId(goesId, 75000);
		assertNotNull("didn't get dummy Indianapolis",station);
		assertEquals("Indianapolis stationid incorrect",
				9996,station.getStationId());

		station = service.stationFromGoesId(goesId, 100000);
		assertNotNull("didn't get dummy Ames",station);
		assertEquals("Ames stationid incorrect",
				9995,station.getStationId());

		goesId="notrealid";
		station = service.stationFromGoesId(goesId, 100000);
		assertNull("didn't expect fake goesId to return station",station);
		
		/* shows that both CD prefixed and non-prefixed stations can be used */
		// TODO eventually remove support for this when syncfromisis updated
		goesId="00A0CC";
		datetimeId = 100300;
		station=service.stationFromGoesId(goesId, datetimeId);
		assertNotNull("MT Wolf Point 34 NE expected", station);
		
		goesId="CD00A0CC";
		datetimeId = 100300;
		station=service.stationFromGoesId(goesId, datetimeId);
		assertNotNull("MT Wolf Point 34 NE expected", station);
	}

    /*
     * ---------------------- ----------------------
     * 1026                   1004
     * 1326                   56454
     * -- 1610 isn't in the refresh db yet
     * 1610                   69031   
     * 1552                   63831               
     */
    @Test
    public final void testGetPor() {
        Map<Integer, POR> porMap = service.getPor();
        assertTrue("wrong number of stations",152<porMap.keySet().size());
        POR por1026 = porMap.get(1026);
        assertNotNull("no por1026",por1026);
        POR por1326 = porMap.get(1326);
        assertNotNull("no por1326",por1326);
        POR por1552 = porMap.get(1552);
        assertNotNull("no por1552",por1552);
        assertEquals("incorrect stationid on por1026",1026,por1026.getStationId());
        assertEquals("incorrect start_dt on por1026",1004,por1026.getStartDatetime());
        assertEquals("incorrect stationid on por1326",1326,por1326.getStationId());
        assertEquals("incorrect datetime on por1326",56454,por1326.getStartDatetime());
        assertEquals("incorrect stationid on por1552",1552,por1552.getStationId());
        assertEquals("incorrect datetime on por1552",63831,por1552.getStartDatetime());
    }

    @Test
    public final void testGetPorInt() {
        POR por = service.getPor(1326);
        assertNotNull("no por(1326)",por);
        assertEquals("incorrect stationid on por(1326)",1326,por.getStationId());
        assertEquals("incorrect datetime on por1326",56454,por.getStartDatetime());
    }
    /*
"600"	1610	10-AUG-08	0
"1000"	1610	22-JUL-11	0
"1000"	1779	26-SEP-11	0
     */
    // TODO all code branches not reached
    @Test
    public final void testGetStationRainGaugeDepth()
    {
    	Integer result = service.getStationRainGaugeDepth(1779, 1);
    	Integer expected = new Integer(1000);
    	assertEquals("only one option",expected,result);
    	result = service.getStationRainGaugeDepth(1779, 100000);
    	expected = new Integer(1000);
    	assertEquals("only one option",expected,result);
    	result = service.getStationRainGaugeDepth(-1, 100000);
    	assertNull("nonexistent stationid",result);
//    	"600"	1610	10-AUG-08	0   -- datetimeId~=68800
//    	"1000"	1610	22-JUL-11	0   -- datetimeId~=94624
    	result = service.getStationRainGaugeDepth(1610, 68000); // 7/7/08
    	expected = new Integer(600);
    	assertEquals("expect first option",expected,result);
    	result = service.getStationRainGaugeDepth(1610, 69000);
    	expected = new Integer(600);
    	assertEquals("expect first option (between gauges)",expected,result);
    	result = service.getStationRainGaugeDepth(1610, 95000);
    	expected = new Integer(1000);
    	assertEquals("expect last option",expected,result);
    }


}
