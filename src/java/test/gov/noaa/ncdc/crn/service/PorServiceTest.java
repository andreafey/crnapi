package gov.noaa.ncdc.crn.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.noaa.ncdc.crn.domain.POR;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations={"classpath:application-context.xml"})
@ActiveProfiles(profiles="unittest")
public class PorServiceTest {
    @Autowired
    private PorService service;
    // for retrieving P_OFFICIAL and T_OFFICIAL
    @Autowired
    private ObservationService obService;
    @Autowired
    private DataSource dataSource;
    private int[] dummyIds = {123,124};

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
        assertEquals("incorrect startDatetime on por1026",1004,por1026.getStartDatetime());
        assertEquals("incorrect stationid on por1326",1326,por1326.getStationId());
        assertEquals("incorrect startDatetime on por1326",56454,por1326.getStartDatetime());
        assertEquals("incorrect stationid on por1552",1552,por1552.getStationId());
        assertEquals("incorrect startDatetime on por1552",63831,por1552.getStartDatetime());
    }

    @Test
    public final void testGetPorCollectionOfInteger() {
        List<Integer> stationIds= Lists.newArrayList(1552, 1026, 1326);
        Map<Integer, POR> porMap = service.getPor(stationIds);
        assertEquals("expected 3 stations",3,porMap.keySet().size());
        POR por1026 = porMap.get(1026);
        assertNotNull("no por1026",por1026);
        POR por1326 = porMap.get(1326);
        assertNotNull("no por1326",por1326);
        POR por1552 = porMap.get(1552);
        assertNotNull("no por1552",por1552);
        assertEquals("incorrect stationid on por1026",1026,por1026.getStationId());
        assertEquals("incorrect startDatetime on por1026",1004,por1026.getStartDatetime());
        assertEquals("incorrect stationid on por1326",1326,por1326.getStationId());
        assertEquals("incorrect startDatetime on por1326",56454,por1326.getStartDatetime());
        assertEquals("incorrect stationid on por1552",1552,por1552.getStationId());
        assertEquals("incorrect startDatetime on por1552",63831,por1552.getStartDatetime());
    }

    @Test
    public final void testGetPorInt() {
        POR por = service.getPor(1326);
        assertNotNull("no por(1326)",por);
        assertEquals("incorrect stationid on por(1326)",1326,por.getStationId());
        assertEquals("incorrect startDatetime on por1326",56454,por.getStartDatetime());
    }

    @Test
    @Transactional
    @Rollback(value=true)
    public final void testUpdatePor() {
    	int stationId=1326;
    	POR por = service.getPor(stationId);
//    	TOfficial, POfficial not available to POR class; test those updates
//    	if they eventually become available. 
    	
    	/* No updates when new POR does not extend existing POR */
    	POR newpor = new POR(stationId, por.getStartDatetime() + 1, por.getEndDatetime() - 1);
    	service.updatePor(newpor);
    	int expectedStart = por.getStartDatetime();
    	int expectedEnd = por.getEndDatetime();
    	
    	por = service.getPor(stationId);
    	assertEquals("did not expect change; inserted begin datetime follows existing datetime",
    			expectedStart,por.getStartDatetime());
    	assertEquals("did not expect change; inserted end datetime precedes existing datetime",
    			expectedEnd,por.getEndDatetime());
    	
    	/* Update end datetime but not beginning datetime when end extends range
    	 * but beginning doesn't
    	 */
    	newpor = new POR(stationId, por.getStartDatetime() + 1, por.getEndDatetime() + 1);
    	expectedStart = por.getStartDatetime();
    	expectedEnd = por.getEndDatetime() + 1;
    	service.updatePor(newpor);
    	
    	por = service.getPor(stationId);
    	assertEquals("did not expect change; inserted begin datetime follows existing datetime",
    			expectedStart,por.getStartDatetime());	
    	assertEquals("expected end datetime to update", 
    			expectedEnd,por.getEndDatetime());

    	/* Update begin datetime but not end datetime when beginning extends range
    	 * but end doesn't
    	 */
    	newpor = new POR(stationId, por.getStartDatetime() - 1, por.getEndDatetime() - 1);
    	expectedStart = por.getStartDatetime() - 1;
    	expectedEnd = por.getEndDatetime();
    	service.updatePor(newpor);
    	
    	por = service.getPor(stationId);
    	assertEquals("expected beginning datetime to update", expectedStart, por.getStartDatetime());
    	assertEquals("did not expect change; inserted end datetime precedes existing datetime",
    			expectedEnd,por.getEndDatetime());
    }
    
    @Test
    @Transactional
    @Rollback(value=true)
    public final void testInsertPor() {
    	int stationid=666;
    	int begindt=62390;
    	int enddt=63471;
    	POR por = new POR(stationid, begindt, enddt);
    	service.insertPor(por);
    	POR result = service.getPor(stationid);
    	assertEquals(por,result);
    }
    @Test
    @Transactional
    @Rollback(value=true)
    public final void testPorCache() {
    	Map<Integer,POR> pors = service.getPor();
    	int initSize = pors.size();
    	SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("TABLE6");
    	Map<String,Object> params = new HashMap<>();
    	params.put("ID", dummyIds[0]);
    	params.put("COL1", 78916);
    	params.put("COL2", 78918);
    	// note not bothering to add temp/precip/dates
    	int rows = jdbcInsert.execute(params);
    	assertEquals(1,rows);
    	
    	pors = service.getPor();
    	// verifies that cache was used 
    	assertEquals(initSize,pors.size());
    	
    	POR dummyPor = new POR(-9, 78916, 78918);
    	service.insertPor(dummyPor);
    	
    	pors = service.getPor();
    	// verifies that cache was not used 
    	assertEquals(initSize+2,pors.size());
    }
}