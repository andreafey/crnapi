package gov.noaa.ncdc.crn.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.noaa.ncdc.crn.domain.Element;
import gov.noaa.ncdc.crn.domain.ElementGroup;
import gov.noaa.ncdc.crn.domain.ElementValue;
import gov.noaa.ncdc.crn.domain.Observation;
import gov.noaa.ncdc.crn.domain.ObservationWithData;
import gov.noaa.ncdc.crn.domain.Station;
import gov.noaa.ncdc.crn.domain.StationDate;
import gov.noaa.ncdc.crn.domain.StationDateElement;
import gov.noaa.ncdc.crn.persistence.ElementMapper;
import gov.noaa.ncdc.crn.util.JsonDataProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
//import gov.noaa.ncdc.crn.domain.NetcdfVariable;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations={"classpath:application-context.xml"})
@ActiveProfiles(profiles="unittest")
public class ElementDaoTest {

    @Autowired ElementDao elementDao;
    @Autowired ElementMapper elementMapper;
    @Autowired DatetimeDao datetimeDao;
    @Autowired ObservationDao observationDao;
    @Autowired
    private DataSource dataSource;
    int jan2008;
    // size may grow
    static int stationCount;

    // trios where facts don't exist already
    static final int datetimeId_1=103001, stationId_1=1026,
            elementId_1=131, elementId_2=132, elementId_3=133;

    @Before
    public void setUp() throws JAXBException, IOException {
        Map<Integer,Station> stations = JsonDataProvider.getStations();
        stationCount=stations.size();
        jan2008=datetimeDao.getDatetimeId("2008010101");
    }

    @Test
    public final void testElementDaoImpl() {
        assertNotNull("elementDao not properly constructed",elementDao);
    }
    /*
     * ---------------------- ---------------------- ---------------------- ---------------------- ----------------------
     * 1026                   439                    63000                  4.4                    0
     * 1026                   439                    63001                  4.1                    0
     * 1026                   439                    63002                  3.7                    0
     * 1326                   439                    63000                  12.4                   0
     * 1326                   439                    63001                  11.7                   0
     * 1326                   439                    63002                  11.9                   0
     * 1027                   319                    71866                  0                      0
     */
    @Test
    public final void testGetElementValues() {
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
            result = elementDao.getElementValues(params);
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
            result = elementDao.getElementValues(params);
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
            result = elementDao.getElementValues(params);
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
            result = elementDao.getElementValues(params);
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
            result = elementDao.getElementValues(params);
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
            result = elementDao.getElementValues(params);
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("DataAccessException caught");
        }
        assertEquals("7.incorrect number of objects retrieved",8,result.keySet().size());
        params.clear();
        params.put("stationId", 1026);
        params.put("begin", jan2008);
        params.put("end", jan2008+1440);
        params.put("elementIds", elementIds);
        try {
            // 1 station, 2 elements, 2 months; 1.6 s
            result = elementDao.getElementValues(params);
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("DataAccessException caught");
        }
        assertTrue("didn't get enough elements: "+result.size(),result.size()>2000);
        assertTrue("too many elements: "+result.size(),result.size()<2881);
        params.clear();
        params.put("stationId", 1026);
        params.put("begin", jan2008);
        params.put("elementIds", elementIds);
        params.put("end", jan2008+4320);
        try {
            // 1 station, 2 elements, 6 months; 2.2 s
            result = elementDao.getElementValues(params);
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("DataAccessException caught");
        }
        assertTrue("didn't get enough elements: "+result.size(),result.size()>4000);
        assertTrue("too many elements: "+result.size(),result.size()<8641);
        params.clear();
        params.put("stationId", 1026);
        params.put("begin", jan2008);
        params.put("elementIds", elementIds);
        params.put("end", jan2008+8640);
        try {
            // 1 station, 2 elements, 12 months; 3.0 s
            result = elementDao.getElementValues(params);
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("DataAccessException caught");
        }
        assertTrue("didn't get enough elements: "+result.size(),result.size()>12000);
        assertTrue("too many elements: "+result.size(),result.size()<17281);
    }
    /*
---------------------- ---------------------- ----------
1026                   63000                  2007121210
1026                   63001                  2007121211
1026                   63002                  2007121211
1026                   63003                  2007121213
1026                   63004                  2007121213
1326                   62999                  2007121210
1326                   63000                  2007121211
1326                   63001                  2007121211
1326                   63002                  2007121213
1326                   63003                  2007121213
     */

    @Test
    public final void testGetElementValuesForHours() {
        /* Single station, single element, default to 1 hour */
        oneSta1Elem1Hr();
        /* Single station, single element, 2 hours */
        oneSta1Elem2Hour();
        /* 2 stations, single element, default 1 hour */
        twoSta1Elem1Hour();
        /* 2 stations, single element, 3 hour */
        twoSta1Elem3Hour();
        /* 2 stations, 2 elements, 3 hour */
        twoSta2Elem3Hour();
    }

    /* 2 stations, 2 elements, 3 hour (default) */
    private void twoSta2Elem3Hour() {
        StationDateElement sde1009_63000_10_439 = new StationDateElement(1009,63000,439);
        StationDateElement sde1009_63000_10_440 = new StationDateElement(1009,63000,440);
        StationDateElement sde1009_63001_11_439 = new StationDateElement(1009,63001,439);
        StationDateElement sde1009_63001_11_440 = new StationDateElement(1009,63001,440);
        StationDateElement sde1009_63002_12_439 = new StationDateElement(1009,63002,439);
        StationDateElement sde1009_63002_12_440 = new StationDateElement(1009,63002,440);
        StationDateElement sde1009_63003_13_439 = new StationDateElement(1009,63003,439);
        StationDateElement sde1009_63003_13_440 = new StationDateElement(1009,63003,440);
        StationDateElement sde1009_63004_13_439 = new StationDateElement(1009,63004,439);
        StationDateElement sde1009_63004_13_440 = new StationDateElement(1009,63004,440);

        StationDateElement sde1326_63000_11_439 = new StationDateElement(1326,63000,439);
        StationDateElement sde1326_63000_11_440 = new StationDateElement(1326,63000,440);
        StationDateElement sde1326_63001_11_439 = new StationDateElement(1326,63001,439);
        StationDateElement sde1326_63001_11_440 = new StationDateElement(1326,63001,440);
        StationDateElement sde1326_63002_13_439 = new StationDateElement(1326,63002,439);
        StationDateElement sde1326_63002_13_440 = new StationDateElement(1326,63002,440);
        StationDateElement sde1326_63003_13_439 = new StationDateElement(1326,63003,439);
        StationDateElement sde1326_63003_13_440 = new StationDateElement(1326,63003,440);

        List<Integer> stationIds = new ArrayList<Integer>();
        stationIds.add(1009);
        stationIds.add(1326);

        List<Integer> elementIds = new ArrayList<Integer>();
        elementIds.add(440);
        elementIds.add(439);

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("elementIds", elementIds);
        params.put("stationIds", stationIds);
        params.put("endhour", "2007121214");
        params.put("hours", 3);

        Map<StationDateElement, ElementValue> result = null;
        try {
            result = elementDao.getElementValuesForHours(params);
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("5.DataAccessException caught");
        }
        assertEquals("5.1 incorrect number of objects retrieved",16,result.keySet().size());
        assertNull("5.2 didn't expect sde1009_63000_10_439",result.get(sde1009_63000_10_439));
        assertNull("5.2.1 didn't expect sde1009_63000_10_440",result.get(sde1009_63000_10_440));
        assertNotNull("5.3 didn't get sde1009_63001_11_439",result.get(sde1009_63001_11_439));
        assertNotNull("5.4 didn't get sde1009_63001_11_440",result.get(sde1009_63001_11_440));
        assertNotNull("5.5 didn't get sde1009_63002_12_439",result.get(sde1009_63002_12_439));
        assertNotNull("5.6 didn't get sde1009_63002_12_440",result.get(sde1009_63002_12_440));
        assertNotNull("5.7 didn't get sde1009_63003_13_439",result.get(sde1009_63003_13_439));
        assertNotNull("5.8 didn't get sde1009_63003_13_440",result.get(sde1009_63003_13_440));
        assertNotNull("5.9 didn't get sde1009_63004_13_439",result.get(sde1009_63004_13_439));
        assertNotNull("5.10 didn't get sde1009_63004_13_440",result.get(sde1009_63004_13_440));
        assertNotNull("5.11 didn't get sde1326_63000_11_439",result.get(sde1326_63000_11_439));
        assertNotNull("5.12 didn't get sde1326_63000_11_440",result.get(sde1326_63000_11_440));
        assertNotNull("5.13 didn't get sde1326_63001_11_439",result.get(sde1326_63001_11_439));
        assertNotNull("5.14 didn't get sde1326_63001_11_440",result.get(sde1326_63001_11_440));
        assertNotNull("5.15 didn't get sde1326_63002_13_439",result.get(sde1326_63002_13_439));
        assertNotNull("5.16 didn't get sde1326_63002_13_440",result.get(sde1326_63002_13_440));
        assertNotNull("5.17 didn't get sde1326_63003_13_439",result.get(sde1326_63003_13_439));
        assertNotNull("5.18 didn't get sde1326_63003_13_440",result.get(sde1326_63003_13_440));
    }

    /* 2 stations, single element, 3 hours */
    private void twoSta1Elem3Hour() {
        StationDateElement sde1009_63000_10_439 = new StationDateElement(1009,63000,439);
        StationDateElement sde1009_63001_11_439 = new StationDateElement(1009,63001,439);
        StationDateElement sde1009_63002_12_439 = new StationDateElement(1009,63002,439);
        StationDateElement sde1009_63003_13_439 = new StationDateElement(1009,63003,439);
        StationDateElement sde1009_63004_13_439 = new StationDateElement(1009,63004,439);

        StationDateElement sde1326_63000_11_439 = new StationDateElement(1326,63000,439);
        StationDateElement sde1326_63001_11_439 = new StationDateElement(1326,63001,439);
        StationDateElement sde1326_63002_13_439 = new StationDateElement(1326,63002,439);
        StationDateElement sde1326_63003_13_439 = new StationDateElement(1326,63003,439);

        List<Integer> stationIds = new ArrayList<Integer>();
        stationIds.add(1009);
        stationIds.add(1326);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("stationIds", stationIds);
        params.put("elementId", 439);
        params.put("endhour", "2007121214");
        params.put("hours", 3);
        Map<StationDateElement, ElementValue> result = null;
        try {
            result = elementDao.getElementValuesForHours(params);
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("4.DataAccessException caught");
        }
        assertEquals("4.1 incorrect number of objects retrieved",8,result.keySet().size());
        assertNull("4.2 didn't expect sde 1009_63000_10_439",result.get(sde1009_63000_10_439));
        assertNotNull("4.4 didn't get sde 1009_63001_11_439",result.get(sde1009_63001_11_439));
        assertNotNull("4.9 didn't get sde 1009_63002_12_439",result.get(sde1009_63002_12_439));
        assertNotNull("4.5 didn't get sde 1009_63003_13_439",result.get(sde1009_63003_13_439));
        assertNotNull("4.6 didn't get sde 1026_63004_13_439",result.get(sde1009_63004_13_439));
        assertNotNull("4.7 didn't get sde 1326_63000_11_439",result.get(sde1326_63000_11_439));
        assertNotNull("4.8 didn't get sde 1326_63001_11_439",result.get(sde1326_63001_11_439));
        assertNotNull("4.9 didn't get sde 1326_63002_13_439",result.get(sde1326_63002_13_439));
        assertNotNull("4.10 didn't get sde 1326_63003_13_439",result.get(sde1326_63003_13_439));
    }

    /* 2 stations, single element, default 1 hour */
    private void twoSta1Elem1Hour() {
        StationDateElement sde1326_63001_11_439 = new StationDateElement(1326,63001,439);
        StationDateElement sde1326_63000_11_439 = new StationDateElement(1326,63000,439);
        StationDateElement sde1009_63001_11_439 = new StationDateElement(1009,63001,439);

        List<Integer> stationIds = new ArrayList<Integer>();
        stationIds.add(1009);
        stationIds.add(1326);

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("stationIds", stationIds);
        params.put("endhour", "2007121212");
        params.put("elementId", 439);

        Map<StationDateElement, ElementValue> result = null;
        try {
            result = elementDao.getElementValuesForHours(params);
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("3.DataAccessException caught");
        }
        assertEquals("3.1 incorrect number of objects retrieved",3,result.keySet().size());
        assertNotNull("3.2 didn't get sde1009_63001_11_439",result.get(sde1009_63001_11_439));
        assertNotNull("3.4 didn't get sde1326_63000_11_439",result.get(sde1326_63000_11_439));
        assertNotNull("3.5 didn't get sde1326_63001_11_439",result.get(sde1326_63001_11_439));
    }

    /* Single station, single element, 2 hours */
    private void oneSta1Elem2Hour() {
        StationDateElement sde1009_63000_10_439 = new StationDateElement(1009,63000,439);
        StationDateElement sde1009_63001_11_439 = new StationDateElement(1009,63001,439);
        StationDateElement sde1009_63002_12_439 = new StationDateElement(1009,63002,439);

        Map<String, Object> params = new HashMap<String,Object>();
        params.put("stationId", 1009);
        params.put("endhour", "2007121212");
        params.put("elementId", 439);
        params.put("hours", 2);
        Map<StationDateElement, ElementValue> result = null;
        try {
            result = elementDao.getElementValuesForHours(params);
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("2.DataAccessException caught");
        }
        ElementValue oneEV;
        ElementValue twoEV;
        ElementValue threeEV;

        assertEquals("2.1 incorrect number of objects retrieved",2,result.keySet().size());
        oneEV = result.get(sde1009_63000_10_439);
        assertNotNull("2.1 no ElementValue retrieved",oneEV);
        assertEquals("2.1 datetimeId incorrect",sde1009_63000_10_439.getDatetimeId(),oneEV.getDatetimeId());
        assertEquals("2.1 stationId incorrect",sde1009_63000_10_439.getStationId(),oneEV.getStationId());
        assertEquals("2.1 elementId incorrect",sde1009_63000_10_439.getElementId(),oneEV.getElementId());
        assertEquals("2.1 value incorrect",new BigDecimal("-13.7"),oneEV.getValue());
        assertEquals("2.1 flag incorrect",0,oneEV.getFlags().getIntValue());
        twoEV = result.get(sde1009_63001_11_439);
        assertNotNull("2.2 no ElementValue retrieved for 63001",twoEV);
        assertEquals("2.2 datetimeId incorrect",sde1009_63001_11_439.getDatetimeId(),twoEV.getDatetimeId());
        assertEquals("2.2 stationId incorrect",sde1009_63001_11_439.getStationId(),twoEV.getStationId());
        assertEquals("2.2 elementId incorrect",sde1009_63001_11_439.getElementId(),twoEV.getElementId());
        assertEquals("2.2 value incorrect",new BigDecimal("-15.6"),twoEV.getValue());
        assertEquals("2.2 flag incorrect",0,twoEV.getFlags().getIntValue());
        threeEV = result.get(sde1009_63002_12_439);
        assertNull("2.3 ElementValue shouldn't be retrieved for 63002 - more an hour later",threeEV);
    }

    /* Single station, single element, default to 1 hour */
    private void oneSta1Elem1Hr() {
        StationDateElement sde1009_63000_10_439 = new StationDateElement(1009,63000,439);
        StationDateElement sde1009_63001_11_439 = new StationDateElement(1009,63001,439);
        StationDateElement sde1009_63002_12_439 = new StationDateElement(1009,63002,439);

        Map<String,Object> params = new HashMap<>();
        params.put("stationId", 1009);
        params.put("endhour", "2007121212");
        params.put("elementId", 439);
        Map<StationDateElement,ElementValue> result=null;
        try {
            result = elementDao.getElementValuesForHours(params);
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("1.DataAccessException caught");
        }
        /*
---------------------- ---------------------- ---------------------- ---------------------- ---------------------- ----------
1009                   63000                  439                    -13.7                  0                      2007121210
1009                   63000                  440                    -11.9                  0                      2007121210
1009                   63001                  439                    -15.6                  0                      2007121211
1009                   63001                  440                    -12.5                  0                      2007121211
1009                   63002                  439                    -15.3                  0                      2007121212
1009                   63002                  440                    -14.2                  0                      2007121212
1009                   63003                  439                    -16.1                  0                      2007121213
1009                   63003                  440                    -15.2                  0                      2007121213
1009                   63004                  439                    -16.1                  0                      2007121213
1009                   63004                  440                    -15                    0                      2007121213
1326                   62999                  439                    14.7                   0                      2007121210
1326                   62999                  440                    16.7                   0                      2007121210
1326                   63000                  439                    12.4                   0                      2007121211
1326                   63000                  440                    14.9                   0                      2007121211
1326                   63001                  439                    11.7                   0                      2007121211
1326                   63001                  440                    13.9                   0                      2007121211
1326                   63002                  439                    11.9                   0                      2007121213
1326                   63002                  440                    16.5                   0                      2007121213
1326                   63003                  439                    16.4                   0                      2007121213
1326                   63003                  440                    16.5                   0                      2007121213
         */

        assertEquals("1.1 incorrect number of objects retrieved",1,result.keySet().size());
        assertFalse("1.1 ElementValue shouldn't be retrieved for 63000 - more than an hour earlier",result.containsKey(sde1009_63000_10_439));
        assertTrue("1.1 expect ElementValue for 63001",result.containsKey(sde1009_63001_11_439));
        ElementValue oneEV = result.get(sde1009_63000_10_439);
        assertNull("1.1 ElementValue shouldn't be retrieved for 63000 - more than an hour earlier",oneEV);
        ElementValue twoEV = result.get(sde1009_63001_11_439);
        assertNotNull("1.2 no ElementValue retrieved for 63001",twoEV);
        assertEquals("1.2 datetimeId incorrect",sde1009_63001_11_439.getDatetimeId(),twoEV.getDatetimeId());
        assertEquals("1.2 stationId incorrect",sde1009_63001_11_439.getStationId(),twoEV.getStationId());
        assertEquals("1.2 elementId incorrect",sde1009_63001_11_439.getElementId(),twoEV.getElementId());
        BigDecimal expected = new BigDecimal("-15.6");
        assertEquals("1.2 value incorrect",expected,twoEV.getValue());
        assertEquals("1.2 flag incorrect",Integer.valueOf(0),Integer.valueOf(twoEV.getFlags().getIntValue()));
        ElementValue threeEV = result.get(sde1009_63002_12_439);
        assertNull("1.3 ElementValue shouldn't be retrieved for 63002 - more an hour later",threeEV);
    }

    /* 
---------------------- ---------------------- ----------
1026                   63000                  2007121210
1026                   63001                  2007121211
1026                   63002                  2007121211
1026                   63003                  2007121213
1026                   63004                  2007121213
1326                   62999                  2007121210
1326                   63000                  2007121211
1326                   63001                  2007121211
1326                   63002                  2007121213
1326                   63003                  2007121213

---------------------- ---------------------- ----------
1009                   63000                  2007121210
1009                   63001                  2007121211
1009                   63002                  2007121212
1009                   63003                  2007121213
1009                   63004                  2007121213
1326                   62999                  2007121210
1326                   63000                  2007121211
1326                   63001                  2007121211
1326                   63002                  2007121213
1326                   63003                  2007121213

     */
    @Test
    public void testGetCurrentElementValuesInt()  {
        Map<Integer,ElementValue> elVals = elementDao.getCurrentElementValues(439);
        assertTrue("retrieved too few values",elVals.size()>10);
    }
    @Test
    public void testGetCurrentElementValuesIntIntArray()  {
        int[] networks = {2,3};
        Map<Integer,ElementValue> elVals = elementDao.getCurrentElementValues(439,networks);
        assertTrue("retrieved too few values",elVals.size()>25);
        assertTrue("retrieved too many values",elVals.size()<100);
    }
    @Test
    public void testGetCurrentElementValuesListOfIntegerListOfInteger()  {
        List<Integer> elements = Lists.newArrayList(439,440);
        List<Integer> networks = Lists.newArrayList(2,3);
        Map<StationDateElement, ElementValue> elVals = elementDao.getCurrentElementValues(elements,networks);
        assertTrue("retrieved too few values",elVals.size()>50);
        assertTrue("retrieved too many values",elVals.size()<200);
    }
    @Test
    public void testGetCurrentElementValuesListOfInteger()  {
        List<Integer> elements = Lists.newArrayList(439,440);
        Map<StationDateElement, ElementValue> elVals = elementDao.getCurrentElementValues(elements);
        assertTrue("retrieved too few values: "+elVals.size(),elVals.size()>stationCount);
        assertTrue("retrieved too many values: "+elVals.size(),elVals.size()<=stationCount*2);
    }

    /*
     * ---------------------- ------------------------------ -------------------------------------------
     * 130                    WET125                         wetness sensor channel 1 minimum for 5 minutes ending at :25
     * 439                    T_MIN                          calculated minimum temp for hour
     * 440                    T_MAX                          calculated maximum temp for hour
     */
    @Test
    public final void testGetElements() {
        Map<Integer,Element> result=null;
        try {
            result = elementDao.getElements();
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

    @Test
    public final void testGetElement()  {
        Element result=elementDao.getElement("T_MIN");
        Element expected=new Element(439, "T_MIN", "calculated minimum temp for hour", Boolean.TRUE, 60, 60, 3, 1, "deg_C", 72);
        assertNotNull("T_MIN was null",result);
        assertEquals("1.T_MIN elementId or name wrong", expected, result);
        assertEquals("1.T_MIN description wrong",
                expected.getDescription(),result.getDescription());
        assertEquals("1.T_MIN calculated wrong",
                expected.isCalculated(), result.isCalculated());
        assertEquals("1.T_MIN default decimals wrong",
                expected.getDefaultDecimalPlaces(), result.getDefaultDecimalPlaces());
        assertEquals("1.T_MIN default pub decimals wrong",
                expected.getDefaultPublishedDecimalPlaces(),
                result.getDefaultPublishedDecimalPlaces());
        assertEquals("1.T_MIN duration wrong",
                expected.getDuration(), result.getDuration());
        assertEquals("1.T_MIN end min wrong",
                expected.getEndMinute(), result.getEndMinute());
        assertEquals("1.T_MIN units wrong",
                expected.getUnits(), result.getUnits());
        assertEquals("1.T_MIN netcdfId wrong",
                expected.getNetcdfId(), result.getNetcdfId());
        result=elementDao.getElement("T_MAX");
        expected=new Element(440, "T_MAX", "calculated maximum temp for hour", Boolean.TRUE, 60, 60, 3, 1, "deg_C", 73);
        assertNotNull("T_MAX was null",result);
        assertEquals("2.T_MAX elementId wrong",440,result.getElementId());
        assertEquals("2.T_MAX name wrong","T_MAX",result.getName());
        assertEquals("2.T_MAX description wrong","calculated maximum temp for hour",result.getDescription());
        assertEquals("T_MAXs not equal", expected, result);
        result=elementDao.getElement("WET125");
        expected=new Element(130, "WET125", "wetness sensor channel 1 minimum for 5 minutes ending at :25", Boolean.FALSE, 5, 25, 0, 0, "ohms", 201);
        assertNotNull("WET125 was null",result);
        assertEquals("3.WET125 elementId wrong",130,result.getElementId());
        assertEquals("3.WET125 name wrong","WET125",result.getName());
        assertEquals("3.WET125 description wrong","wetness sensor channel 1 minimum for 5 minutes ending at :25",result.getDescription());
        assertEquals("WET125s not equal", expected, result);
    }

    @Test
    public final void testGetElementValue() {
        StationDateElement one = new StationDateElement(1026,63000,439);
        ElementValue oneEV=null;
        try {
            oneEV = elementDao.getElementValue(one);
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

    @Test
    public final void testGetElementsByName() {
        Map<String,Element> result=null;
        try {
            result = elementDao.getElementsByName();
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("1.DataAccessException caught");
        }
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
    public final void testGetElementsByNameListOfString() {
        List<String> elNames = Lists.newArrayList("T_MAX", "T_MIN", "WET125");
        Map<String,Element> result=null;
        try {
            result = elementDao.getElementsByName(elNames);
        } catch (DataAccessException e) {
            e.printStackTrace();
            fail("1.DataAccessException caught");
        }
        assertEquals("didn't get 3 elements",3,result.keySet().size());
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

    @Test
    @Transactional
    @Rollback(true)
    public final void testInsertElementValues()  {
        int datetimeId=64127;
        int stationId=1326;
        ElementValue ev1 = new ElementValue(stationId,datetimeId,142,new BigDecimal("12.3"),3,null,null);
        ElementValue ev2 = new ElementValue(stationId,datetimeId,151,new BigDecimal("12.4"),0,null,null);
        ElementValue ev3 = new ElementValue(stationId,datetimeId,149,new BigDecimal("12048"),7,null,null);
        StationDate stationDate = new StationDate(stationId,datetimeId);
        StationDateElement sd1 = new StationDateElement(stationDate,ev1.getElementId());
        StationDateElement sd2 = new StationDateElement(stationDate,ev2.getElementId());
        StationDateElement sd3 = new StationDateElement(stationDate,ev3.getElementId());

        /*
         * Show that values are not already in the database
         */
        // show that the observation is, though
        Observation ob = observationDao.getObservation(datetimeId,stationId);
        assertNotNull("observation should exist in the database already",ob);
        ElementValue result = elementDao.getElementValue(sd1);
        assertNull("should not get ev1",result);
        result = elementDao.getElementValue(sd2);
        assertNull("should not get ev2",result);
        result = elementDao.getElementValue(sd3);
        assertNull("should not get ev3",result);

        List<ElementValue> values = new ArrayList<>();
        values.add(ev1);
        values.add(ev2);
        values.add(ev3);

        /*
         * Insert values into the database
         */
        elementDao.insertElementValues(values);

        // show values and flags inserted correctly
        result = elementDao.getElementValue(sd1);
        assertNotNull("should get ev1",result);
        assertEquals("ev1 values should be equal",
                ev1.getValue(),result.getValue());
        assertEquals("ev1 flags should be equal",
                ev1.getFlags().getIntValue(),result.getFlags().getIntValue());
        result = elementDao.getElementValue(sd2);
        assertNotNull("should get ev2",result);
        assertEquals("ev2 values should be equal",
                ev2.getValue(),result.getValue());
        assertEquals("ev2 flags should be equal",
                ev2.getFlags().getIntValue(),result.getFlags().getIntValue());
        result = elementDao.getElementValue(sd3);
        assertNotNull("should get ev3",result);
        assertEquals("ev3 values should be equal",
                ev3.getValue(),result.getValue());
        assertEquals("ev3 flags should be equal",
                ev3.getFlags().getIntValue(),result.getFlags().getIntValue());

    }


    @Test
    public final void testGetElementGroupMap()  {
        Map<Integer,ElementGroup> map = elementDao.getElementGroupMap();
        assertTrue("empty map",map.size()>=17);
        ElementGroup expected = expectedElGrp(); // Calculated Temperature
        ElementGroup result = map.get(1);
        assertEquals("inequal Calc Temp group",expected,result);
        assertEquals("inequal element list",
                expected.getMemberElements(),result.getMemberElements());
    }

    @Test
    public final void testGetElementGroups()  {
        List<ElementGroup> groups = elementDao.getElementGroups();
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
    @Test
    @DirtiesContext
    @Transactional
    @Rollback(true)
    public final void testUpdateFlagInsert() {
        // tests unflagged ob updated to be flagged

        // insert a dummy ob with unflagged ev
        ObservationWithData ob =
                new ObservationWithData(new Observation(stationId_1, datetimeId_1, 12,1, "fakefile.txt",125));
        ob.addNewElementValue(elementId_1, new BigDecimal("4.0"), 0);
        observationDao.insertObservation(ob);

        ElementValue ev = elementDao.getElementValue(
                new StationDateElement(ob.getStationId(), ob.getDatetimeId(), elementId_1));
        assertFalse("shouldn't be flagged yet",ev.getFlags().isFlagged());
        ev.getFlags().setFlagsFromInt(4);
        // testing protected method
        elementMapper.updateFlag(ev);
        ElementValue result = elementDao.getElementValue(
                new StationDateElement(ob.getStationId(), ob.getDatetimeId(), elementId_1));
        assertTrue("should be flagged after update",result.getFlags().isFlagged());
    }
    @Test
    @DirtiesContext
    @Transactional
    @Rollback(true)
    public final void testUpdateFlagDelete() {
        // tests where a flag exists and is being removed

        // insert a dummy ob with flagged ev
        ObservationWithData ob =
                new ObservationWithData(new Observation(stationId_1, datetimeId_1, 12,1, "fakefile.txt",125));
        ob.addNewElementValue(elementId_1, new BigDecimal("4.0"), 4);
        observationDao.insertObservation(ob);

        ElementValue ev = elementDao.getElementValue(
                new StationDateElement(ob.getStationId(), ob.getDatetimeId(), elementId_1));
        assertTrue("should be flagged already",ev.getFlags().isFlagged());
        ev.getFlags().setFlagsFromInt(4);
        // testing protected method
        elementMapper.updateFlag(ev);
        ElementValue result = elementDao.getElementValue(
                new StationDateElement(ob.getStationId(), ob.getDatetimeId(), elementId_1));
        assertTrue("shouldn't be flagged after delete",result.getFlags().isFlagged());
    }
    @Test
    @DirtiesContext
    @Transactional
    @Rollback(true)
    public final void testUpdateFlagUpdate() {
        // update where a flag exists already and is being changed to a nonzero value

        // insert a dummy ob with flagged ev
        ObservationWithData ob =
                new ObservationWithData(new Observation(stationId_1, datetimeId_1, 12,1, "fakefile.txt",125));
        ob.addNewElementValue(elementId_1, new BigDecimal("4.0"), 4);
        observationDao.insertObservation(ob);

        ElementValue ev = elementDao.getElementValue(
                new StationDateElement(ob.getStationId(), ob.getDatetimeId(), elementId_1));
        assertTrue("should be flagged already",ev.getFlags().isFlagged());
        ev.getFlags().setFlagsFromInt(6);
        // testing protected method
        elementMapper.updateFlag(ev);
        ElementValue result = elementDao.getElementValue(
                new StationDateElement(ob.getStationId(), ob.getDatetimeId(), elementId_1));
        assertEquals("should update flag value",6,result.getFlags().getIntValue());
    }
    @Test
    @DirtiesContext
    @Transactional
    @Rollback(true)
    public final void testUpdateFlagUpdates() {
        // insert a dummy ob with flagged evs
        ObservationWithData ob =
                new ObservationWithData(new Observation(stationId_1, datetimeId_1, 12,1, "fakefile.txt",125));
        ob.addNewElementValue(elementId_1, new BigDecimal("4.0"), 4);
        ob.addNewElementValue(elementId_2, new BigDecimal("4.1"), 4);
        ob.addNewElementValue(elementId_3, new BigDecimal("4.2"));
        observationDao.insertObservation(ob);

        ElementValue ev1 = elementDao.getElementValue(
                new StationDateElement(ob.getStationId(), ob.getDatetimeId(), elementId_1));
        assertEquals("should be flagged already",4,ev1.getFlags().getIntValue());
        ev1.getFlags().setFlagsFromInt(6);
        ElementValue ev2 = elementDao.getElementValue(
                new StationDateElement(ob.getStationId(), ob.getDatetimeId(), elementId_2));
        assertEquals("should be flagged already",4,ev2.getFlags().getIntValue());
        ev2.getFlags().setFlagsFromInt(0);
        ElementValue ev3 = elementDao.getElementValue(
                new StationDateElement(ob.getStationId(), ob.getDatetimeId(), elementId_3));
        assertEquals("should not be flagged already",0,ev3.getFlags().getIntValue());
        ev3.getFlags().setFlagsFromInt(8);
        List<ElementValue> vals = new ArrayList<ElementValue>(3);
        vals.add(ev1);
        vals.add(ev2);
        vals.add(ev3);
        elementDao.updateFlags(vals);
        ElementValue result = elementDao.getElementValue(
                new StationDateElement(ob.getStationId(), ob.getDatetimeId(), elementId_1));
        assertEquals("should update flag value", 6, result.getFlags().getIntValue());
        result = elementDao.getElementValue(
                new StationDateElement(ob.getStationId(), ob.getDatetimeId(), elementId_2));
        assertEquals("should remove flag value", 0, result.getFlags().getIntValue());
        result = elementDao.getElementValue(
                new StationDateElement(ob.getStationId(), ob.getDatetimeId(), elementId_3));
        assertEquals("should add flag value", 8, result.getFlags().getIntValue());
    }
}
