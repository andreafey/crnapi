package gov.noaa.ncdc.crn.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.noaa.ncdc.crn.domain.Element;

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
public class ElementServiceTest {
	@Autowired
	private ElementService service;
    @Autowired
    private DataSource dataSource;

    /*
     * ---------------------- ------------------------------ -------------------------------------------
     * 130                    WET125                         wetness sensor channel 1 minimum for 5 minutes ending at :25
     * 439                    T_MIN                          calculated minimum temp for hour
     * 440                    T_MAX                          calculated maximum temp for hour                                                                                                 
     */
    @Test
    public final void testGetElements() {
        Map<Integer,Element> result = service.getElements();
        assertTrue("didn't get enough elements",result.keySet().size()>325);
        Element el439 = result.get(439);
        assertEquals("1.el439 elementId wrong",439,el439.getElementId());
        assertEquals("1.el439 name wrong","T_MIN",el439.getName());
        assertEquals("1.el439 description wrong","calculated minimum temp for hour",el439.getDescription());
        Element el440 = result.get(440);
        assertEquals("2.el440 elementId wrong",440,el440.getElementId());
        assertEquals("2.el440 name wrong","T_MAX",el440.getName());
        assertEquals("2.el440 description wrong","calculated maximum temp for hour",el440.getDescription());
        Element el130 = result.get(130);
        assertEquals("3.el130 elementId wrong",130,el130.getElementId());
        assertEquals("3.el130 name wrong","WET125",el130.getName());
        assertEquals("3.el130 description wrong","wetness sensor channel 1 minimum for 5 minutes ending at :25",el130.getDescription());
    }
    
    @Test
    public final void testGetElementsCollectionOfInteger() {
        List<Integer> elIds = Lists.newArrayList(439,130,440);
        Map<Integer,Element> result = service.getElements(elIds);
        assertEquals("didn't get 3 elements",3,result.keySet().size());
        Element el439 = result.get(439);
        assertEquals("1.el439 elementId wrong",439,el439.getElementId());
        assertEquals("1.el439 name wrong","T_MIN",el439.getName());
        assertEquals("1.el439 description wrong","calculated minimum temp for hour",el439.getDescription());
        Element el440 = result.get(440);
        assertEquals("2.el440 elementId wrong",440,el440.getElementId());
        assertEquals("2.el440 name wrong","T_MAX",el440.getName());
        assertEquals("2.el440 description wrong","calculated maximum temp for hour",el440.getDescription());
        Element el130 = result.get(130);
        assertEquals("3.el130 elementId wrong",130,el130.getElementId());
        assertEquals("3.el130 name wrong","WET125",el130.getName());
        assertEquals("3.el130 description wrong","wetness sensor channel 1 minimum for 5 minutes ending at :25",el130.getDescription());
    }
	@Test
	public void testGetElementsByName() {
		Map<String,Element> result = service.getElementsByName();
        assertTrue("didn't get enough elements",result.keySet().size()>325);
        Element el439 = result.get("T_MIN");
        assertEquals("1.el439 elementId wrong",439,el439.getElementId());
        assertEquals("1.el439 name wrong","T_MIN",el439.getName());
        assertEquals("1.el439 description wrong","calculated minimum temp for hour",el439.getDescription());
// TODO
        //        assertEquals("1.el439 duration wrong", 60, el439.getDuration());
//        assertEquals("1.el439 endMinute wrong", 60, el439.getEndMinute());
        Element el440 = result.get("T_MAX");
        assertEquals("2.el440 elementId wrong",440,el440.getElementId());
        assertEquals("2.el440 name wrong","T_MAX",el440.getName());
        assertEquals("2.el440 description wrong","calculated maximum temp for hour",el440.getDescription());
        Element el130 = result.get("WET125");
        assertEquals("3.el130 elementId wrong",130,el130.getElementId());
        assertEquals("3.el130 name wrong","WET125",el130.getName());
        assertEquals("3.el130 description wrong","wetness sensor channel 1 minimum for 5 minutes ending at :25",el130.getDescription());
	}

	@Test
	public void testGetElementsByNameCollectionOfString() {
		List<String> names = Lists.newArrayList("T_MIN", "T_MAX", "WET125");
		Map<String,Element> elements = service.getElementsByName(names);
        assertTrue("expected the same number of elements as list size",
        		names.size()==elements.size());
        Element el439 = elements.get("T_MIN");
        assertEquals("1.el439 elementId wrong",439,el439.getElementId());
        assertEquals("1.el439 name wrong","T_MIN",el439.getName());
        assertEquals("1.el439 description wrong","calculated minimum temp for hour",el439.getDescription());
// TODO
        //        assertEquals("1.el439 duration wrong", 60, el439.getDuration());
//        assertEquals("1.el439 endMinute wrong", 60, el439.getEndMinute());
        Element el440 = elements.get("T_MAX");
        assertEquals("2.el440 elementId wrong",440,el440.getElementId());
        assertEquals("2.el440 name wrong","T_MAX",el440.getName());
        assertEquals("2.el440 description wrong","calculated maximum temp for hour",el440.getDescription());
        Element el130 = elements.get("WET125");
        assertEquals("3.el130 elementId wrong",130,el130.getElementId());
        assertEquals("3.el130 name wrong","WET125",el130.getName());
        assertEquals("3.el130 description wrong","wetness sensor channel 1 minimum for 5 minutes ending at :25",el130.getDescription());
	}

    @Test
    @Transactional
    @Rollback(true)
    public final void testInsertElements()  {
    	Element dummy1 = new Element(4567,"columbo","elephant element", false, 60, 60, 2, 1, null, -1);
    	Map<String,Element> elements = service.getElementsByName();
    	Element result = elements.get(dummy1.getName());
    	assertNull(result);
    	service.insertElements(dummy1);
    	elements = service.getElementsByName();
    	result = elements.get(dummy1.getName());
    	assertEquals(dummy1,result);
    }
    @Test
    @Transactional
    @Rollback(true)
    public final void testElementCache()  {
    	Element dummy1 = new Element(9965,"dumbo","elephant element", false, 60, 60, 2, 1, null, -1);
    	Element dummy2 = new Element(7899,"bambi","wheres my mommy", true, 60, 60, 2, 1, null, -1);
    	
    	Map<String,Element> elements = service.getElementsByName();
    	assertNull(elements.get(dummy1.getName()));
    	
    	// insert new element, bypassing iBATIS
    	SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("TABLE1");
    	Map<String,Object> params = new HashMap<>();
    	params.put("ID", dummy1.getElementId());
    	params.put("name", dummy1.getName());
    	params.put("description", dummy1.getDescription());
    	params.put("COL3", dummy1.getDuration());
    	params.put("COL4", dummy1.getEndMinute());
    	params.put("ID2", dummy1.getNetcdfId());
    	params.put("COL5", "N");
    	int rows = jdbcInsert.execute(params);
    	assertEquals(1,rows);
    	
    	// verifies that cache was used
    	elements = service.getElementsByName();
    	assertNull(elements.get(dummy1.getName()));
    	
    	// expect cache flush
    	service.insertElements(dummy2);
    	// validate the cache was flushed
    	elements = service.getElementsByName();
    	Element result = elements.get(dummy1.getName());
    	assertEquals(dummy1,result);
    	result = elements.get(dummy2.getName());
    	assertEquals(dummy2,result);
    }
	
}
