package gov.noaa.ncdc.crn.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.noaa.ncdc.crn.domain.Element;
import gov.noaa.ncdc.crn.domain.ElementGroup;
import gov.noaa.ncdc.crn.domain.ElementValue;
import gov.noaa.ncdc.crn.domain.StationDateElement;
import gov.noaa.ncdc.crn.util.JsonDataProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
/**
 * @author Andrea.Fey
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations={"classpath:application-context.xml"})
@ActiveProfiles(profiles="unittest")
public class ElementMapperTest {
    @Autowired
    private ElementMapper mapper;
    private static final int JAN1_2008 = 63472; // datetimeId for 2008010101

    /**
     * Test method for {@link gov.noaa.ncdc.crn.persistence.ElementMapper#selectElementValues(java.util.Map)}.
     */
    @Test
    public void testSelectElementValuesMapOfStringObject() {
        StationDateElement one = new StationDateElement(1026,63000,439);
        StationDateElement two = new StationDateElement(1326,63000,439);
        StationDateElement three = new StationDateElement(1326,63001,439);
        StationDateElement four = new StationDateElement(1027,71866,319);

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("begin", 71865);
        params.put("end", 71867);
        List<Integer> ids = Arrays.asList(319,439);
        params.put("elementIds", ids);
        Map<StationDateElement,ElementValue> result=null;
        try {
            result = mapper.selectElementValues(params);
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("0.9.DataAccessException caught");
        }
        assertTrue("0.9.didn't retrieve any objects",result.keySet().size()>0);
        assertTrue("0.9.no key mapped",result.containsKey(four));
        ElementValue fourEV = result.get(four);
        assertNotNull("0.9.no ElementValue retrieved",fourEV);
        assertEquals("0.9.datetimeId incorrect",four.getDatetimeId(),fourEV.getDatetimeId());
        assertEquals("0.9.stationId incorrect",four.getStationId(),fourEV.getStationId());
        assertEquals("0.9.elementId incorrect",four.getElementId(),fourEV.getElementId());
        assertEquals("0.9.value incorrect",BigDecimal.ZERO,fourEV.getValue());
        assertEquals("0.9.flag incorrect",0,fourEV.getFlags().getIntValue());

        params.clear();
        params.put("stationId", 1026);
        params.put("datetimeId", 63000);
        params.put("elementId", 439);
        try {
            result = mapper.selectElementValues(params);
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("1.DataAccessException caught");
        }
        assertEquals("1.incorrect number of objects retrieved",1,result.keySet().size());
        assertTrue("1.no key mapped",result.containsKey(one));
        ElementValue oneEV = result.get(one);
        assertNotNull("no ElementValue retrieved",oneEV);
        assertEquals("1.datetimeId incorrect",one.getDatetimeId(),oneEV.getDatetimeId());
        assertEquals("1.stationId incorrect",one.getStationId(),oneEV.getStationId());
        assertEquals("1.elementId incorrect",one.getElementId(),oneEV.getElementId());
        assertEquals("1.value incorrect",new BigDecimal("4.4"),oneEV.getValue());
        assertEquals("1.flag incorrect",0,oneEV.getFlags().getIntValue());

        params.put("stationId", -9999);

        try {
            result = mapper.selectElementValues(params);
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("1.1.DataAccessException caught");
        }
        assertEquals("1.1.incorrect number of objects retrieved",0,result.keySet().size());

        params.remove("stationId");
        int[] stationIds = {1026,1326};
        params.put("stationIds", stationIds);

        try {
            // single datetime, element; 2 stations
            result = mapper.selectElementValues(params);
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("2.DataAccessException caught");
        }
        assertEquals("2.incorrect number of objects retrieved",2,result.keySet().size());
        oneEV = result.get(one);
        assertNotNull("2.no ElementValue retrieved",oneEV);
        assertEquals("2.datetimeId incorrect",one.getDatetimeId(),oneEV.getDatetimeId());
        assertEquals("2.stationId incorrect",one.getStationId(),oneEV.getStationId());
        assertEquals("2.elementId incorrect",one.getElementId(),oneEV.getElementId());
        assertEquals("2.value incorrect",new BigDecimal("4.4"),oneEV.getValue());
        assertEquals("2.flag incorrect",0,oneEV.getFlags().getIntValue());
        ElementValue twoEV = result.get(two);
        assertNotNull("3.no ElementValue retrieved",twoEV);
        assertEquals("3.datetimeId incorrect",two.getDatetimeId(),twoEV.getDatetimeId());
        assertEquals("3.stationId incorrect",two.getStationId(),twoEV.getStationId());
        assertEquals("3.elementId incorrect",two.getElementId(),twoEV.getElementId());
        assertEquals("3.value incorrect",new BigDecimal("12.4"),twoEV.getValue());
        assertEquals("3.flag incorrect",0,oneEV.getFlags().getIntValue());

        params.remove("datetimeId");
        params.put("begin", 63000);
        params.put("end", 63001);
        try {
            // single element; 2 stations,datetimes
            result = mapper.selectElementValues(params);
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("DataAccessException caught");
        }
        assertEquals("4.incorrect number of objects retrieved",4,result.keySet().size());
        oneEV = result.get(one);
        assertNotNull("4.no ElementValue retrieved",oneEV);
        assertEquals("4.datetimeId incorrect",one.getDatetimeId(),oneEV.getDatetimeId());
        assertEquals("4.stationId incorrect",one.getStationId(),oneEV.getStationId());
        assertEquals("4.elementId incorrect",one.getElementId(),oneEV.getElementId());
        assertEquals("4.value incorrect",new BigDecimal("4.4"),oneEV.getValue());
        assertEquals("4.flag incorrect",0,oneEV.getFlags().getIntValue());
        twoEV = result.get(two);
        assertNotNull("5.no ElementValue retrieved",twoEV);
        assertEquals("5.datetimeId incorrect",two.getDatetimeId(),twoEV.getDatetimeId());
        assertEquals("5.stationId incorrect",two.getStationId(),twoEV.getStationId());
        assertEquals("5.elementId incorrect",two.getElementId(),twoEV.getElementId());
        assertEquals("5.value incorrect",new BigDecimal("12.4"),twoEV.getValue());
        assertEquals("5.flag incorrect",0,oneEV.getFlags().getIntValue());
        ElementValue threeEV = result.get(three);
        assertNotNull("6.no ElementValue retrieved",threeEV);
        assertEquals("6.datetimeId incorrect",three.getDatetimeId(),threeEV.getDatetimeId());
        assertEquals("6.stationId incorrect",three.getStationId(),threeEV.getStationId());
        assertEquals("6.elementId incorrect",three.getElementId(),threeEV.getElementId());
        assertEquals("6.value incorrect",new BigDecimal("11.7"),threeEV.getValue());
        assertEquals("6.flag incorrect",0,threeEV.getFlags().getIntValue());

        params.remove("elementId");
        int[] elementIds = {439,440};
        params.put("elementIds", elementIds);
        try {
            // 2 stations,element,datetimes
            result = mapper.selectElementValues(params);
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("DataAccessException caught");
        }
        assertEquals("7.incorrect number of objects retrieved",8,result.keySet().size());
        params.clear();
        params.put("stationId", 1026);
        params.put("begin", JAN1_2008);
        params.put("end", JAN1_2008+1440);
        params.put("elementIds", elementIds);
        try {
            // 1 station, 2 elements, 2 months; 1.6 s
            result = mapper.selectElementValues(params);
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("DataAccessException caught");
        }
        assertTrue("didn't get enough elements: "+result.size(),result.size()>2000);
        assertTrue("too many elements: "+result.size(),result.size()<2881);
        params.clear();
        params.put("stationId", 1026);
        params.put("begin", JAN1_2008);
        params.put("elementIds", elementIds);
        params.put("end", JAN1_2008+4320);
        try {
            // 1 station, 2 elements, 6 months; 2.2 s
            result = mapper.selectElementValues(params);
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("DataAccessException caught");
        }
        assertTrue("didn't get enough elements: "+result.size(),result.size()>4000);
        assertTrue("too many elements: "+result.size(),result.size()<8641);
        params.clear();
        params.put("stationId", 1026);
        params.put("begin", JAN1_2008);
        params.put("elementIds", elementIds);
        params.put("end", JAN1_2008+8640);
        try {
            // 1 station, 2 elements, 12 months; 3.0 s
            result = mapper.selectElementValues(params);
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("DataAccessException caught");
        }
        assertTrue("didn't get enough elements: "+result.size(),result.size()>12000);
        assertTrue("too many elements: "+result.size(),result.size()<17281);
    }


    /*
     * ---------------------- ------------------------------ -------------------------------------------
     * 130                    WET125                         wetness sensor channel 1 minimum for 5 minutes ending at :25
     * 439                    T_MIN                          calculated minimum temp for hour
     * 440                    T_MAX                          calculated maximum temp for hour
     */

    /**
     * Test method for {@link gov.noaa.ncdc.crn.persistence.ElementMapper#selectElements()}.
     */
    @Test
    public void testSelectElements() {
        Map<Integer,Element> result=null;
        try {
            result = mapper.selectElements();
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("1.DataAccessException caught");
        }
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

    /**
     * Test method for {@link gov.noaa.ncdc.crn.persistence.ElementMapper#selectElements(java.util.Collection)}.
     */
    @Test
    public void testSelectElementsCollectionOfInteger() {
        Map<Integer,Element> result=null;
        Integer[] idarr = {130, 439, 440};
        Collection<Integer> elIds = Arrays.asList(idarr);
        try {
            result = mapper.selectElements(elIds);
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("1.DataAccessException caught");
        }
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

    /**
     * Test method for {@link gov.noaa.ncdc.crn.persistence.ElementMapper#selectElementValue(gov.noaa.ncdc.crn.domain.StationDateElement)}.
     */
    @Test
    public void testSelectElementValue() {
        StationDateElement one = new StationDateElement(1026,63000,439);
        ElementValue oneEV=null;
        try {
            oneEV = mapper.selectElementValue(one);
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("DataAccessException caught");
        }
        assertNotNull("no ElementValue retrieved",oneEV);
        assertEquals("datetimeId incorrect",one.getDatetimeId(),oneEV.getDatetimeId());
        assertEquals("stationId incorrect",one.getStationId(),oneEV.getStationId());
        assertEquals("elementId incorrect",one.getElementId(),oneEV.getElementId());
        BigDecimal expected = new BigDecimal("4.4");
        assertEquals("value incorrect",expected,oneEV.getValue());
        assertEquals("flag incorrect",0,oneEV.getFlags().getIntValue());
    }

    /**
     * Test method for {@link gov.noaa.ncdc.crn.persistence.ElementMapper#selectElementsByName()}.
     */
    @Test
    public void testSelectElementsByName() {
        Map<String,Element> result=null;
        try {
            result = mapper.selectElementsByName();
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("1.DataAccessException caught");
        }
        assertTrue("didn't get enough elements",result.keySet().size()>325);
        Element el439 = result.get("T_MIN");
        assertEquals("1.el439 elementId wrong",439,el439.getElementId());
        assertEquals("1.el439 name wrong","T_MIN",el439.getName());
        assertEquals("1.el439 description wrong","calculated minimum temp for hour",el439.getDescription());
        Element el440 = result.get("T_MAX");
        assertEquals("2.el440 elementId wrong",440,el440.getElementId());
        assertEquals("2.el440 name wrong","T_MAX",el440.getName());
        assertEquals("2.el440 description wrong","calculated maximum temp for hour",el440.getDescription());
        Element el130 = result.get("WET125");
        assertEquals("3.el130 elementId wrong",130,el130.getElementId());
        assertEquals("3.el130 name wrong","WET125",el130.getName());
        assertEquals("3.el130 description wrong","wetness sensor channel 1 minimum for 5 minutes ending at :25",el130.getDescription());
    }

    /**
     * Test method for {@link gov.noaa.ncdc.crn.persistence.ElementMapper#selectElement(java.lang.String)}.
     */
    @Test
    public void testSelectElement() {
        Element result=mapper.selectElement("T_MIN");
        assertNotNull("T_MIN was null",result);
        assertEquals("1.T_MIN elementId wrong",439,result.getElementId());
        assertEquals("1.T_MIN name wrong","T_MIN",result.getName());
        assertEquals("1.T_MIN description wrong","calculated minimum temp for hour",result.getDescription());
        result=mapper.selectElement("T_MAX");
        assertNotNull("T_MAX was null",result);
        assertEquals("2.T_MAX elementId wrong",440,result.getElementId());
        assertEquals("2.T_MAX name wrong","T_MAX",result.getName());
        assertEquals("2.T_MAX description wrong","calculated maximum temp for hour",result.getDescription());
        result=mapper.selectElement("WET125");
        assertNotNull("WET125 was null",result);
        assertEquals("3.WET125 elementId wrong",130,result.getElementId());
        assertEquals("3.WET125 name wrong","WET125",result.getName());
        assertEquals("3.WET125 description wrong","wetness sensor channel 1 minimum for 5 minutes ending at :25",result.getDescription());
    }

    /**
     * Test method for {@link gov.noaa.ncdc.crn.persistence.ElementMapper#selectCurrentElementValues(int)}.
     */
    @Test
    public void testSelectCurrentElementValuesInt() {
        Map<Integer,ElementValue> elVals = mapper.selectCurrentElementValues(439);
        assertTrue("retrieved too few values",elVals.size()>100);
    }

    /**
     * Test method for {@link gov.noaa.ncdc.crn.persistence.ElementMapper#selectCurrentElementValues(int, int[])}.
     */
    @Test
    public void testSelectCurrentElementValuesIntIntArray() {
        int[] networks = {2,3};
        Map<Integer,ElementValue> elVals = mapper.selectCurrentElementValues(439,networks);
        assertTrue("retrieved too few values",elVals.size()>25);
        assertTrue("retrieved too many values",elVals.size()<100);
    }

    /**
     * Test method for {@link gov.noaa.ncdc.crn.persistence.ElementMapper#selectCurrentElementValues(java.util.Collection, java.util.Collection)}.
     */
    @Test
    public void testSelectCurrentElementValuesCollectionOfIntegerCollectionOfInteger() {
        Collection<Integer> elements = Lists.newArrayList(439, 440);
        List<Integer> networks = Lists.newArrayList(2,3);
        Map<StationDateElement, ElementValue> elVals = mapper.selectCurrentElementValues(elements,networks);
        assertTrue("retrieved too few values",elVals.size()>50);
        assertTrue("retrieved too many values",elVals.size()<200);
    }

    /**
     * Test method for {@link gov.noaa.ncdc.crn.persistence.ElementMapper#selectCurrentElementValues(java.util.Collection)}.
     * @throws IOException
     */
    @Test
    public void testSelectCurrentElementValuesCollectionOfInteger() throws IOException {
        Collection<Integer> elements = Lists.newArrayList(439, 440);
        int stationCount = JsonDataProvider.getStations().size();
        Map<StationDateElement, ElementValue> elVals = mapper.selectCurrentElementValues(elements);
        assertTrue("retrieved too few values: "+elVals.size(),elVals.size()>stationCount);
        assertTrue("retrieved too many values: "+elVals.size(),elVals.size()<=stationCount*2);
    }

    /**
     * Test method for {@link gov.noaa.ncdc.crn.persistence.ElementMapper#selectElementGroups()}.
     */
    @Test
    public void testSelectElementGroups() {
        List<ElementGroup> groups = mapper.selectElementGroups();
        assertTrue("empty list",groups.size()>=17);
        // Calculated Temperature
        ElementGroup expected = expectedElGrp();
        /* Calculated Temperature has an ordinal of 1, so it should be first in
         * the returned list */
        ElementGroup result = groups.get(0);
        assertEquals("inequal Calc Temp group",expected,result);
        assertEquals("inequal element list",
                expected.getMemberElements(),result.getMemberElements());
    }

    // returns an ElementGroup for Calculated Temperature
    private ElementGroup expectedElGrp() {
        List<Integer> memberElements = Arrays.asList
                (331,332,333,334,335,336,337,338,339,340,341,342,343,439,440);
        return new ElementGroup(1,1,"Calculated Temperature",342,memberElements);
    }

    /**
     * Test method for {@link gov.noaa.ncdc.crn.persistence.ElementMapper#insertFact(gov.noaa.ncdc.crn.domain.ElementValue)}.
     */
    @Test
    @Transactional
    @Rollback(true)
    public void testInsertFact() {
        int datetimeId=64127;
        int stationId=1326;
        int elementId = 142;
        Map<Integer,ElementValue> dbVals =
                mapper.selectElementValues(datetimeId, stationId);
        // first show the observation is in the database but not this element
        int size = dbVals.size();
        assertTrue("no obs for this ob time", size>0);
        assertNull("should not have value for this element yet", dbVals.get(elementId));

        ElementValue value =
                new ElementValue(stationId,datetimeId,elementId,new BigDecimal("4.3"),null,null);
        mapper.insertFact(value);
        dbVals = mapper.selectElementValues(datetimeId, stationId);
        assertEquals("expected size to increase by 1", size+1, dbVals.size());
        ElementValue result = dbVals.get(elementId);
        assertNotNull("expected to retrieve inserted value", result);
        assertEquals("wrong value", value.getValue(), result.getValue());
    }

}
