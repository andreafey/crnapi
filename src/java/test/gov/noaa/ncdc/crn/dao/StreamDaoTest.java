package gov.noaa.ncdc.crn.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.noaa.ncdc.crn.domain.Element;
import gov.noaa.ncdc.crn.domain.ElementValue;
import gov.noaa.ncdc.crn.domain.Stream;
import gov.noaa.ncdc.crn.domain.StreamElement;
import gov.noaa.ncdc.crn.spring.ApplicationContextProvider;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations={"classpath:application-context.xml"})
@ActiveProfiles(profiles="unittest")
public class StreamDaoTest {
    @Autowired
    private StreamDao streamDao;
    Element longitude = new Element(5, "LONGITUDE", "station longitude",
    		Boolean.FALSE, 60, 60, 2, 2, null, -1);
    Element latitude = new Element(4, "LATITUDE", "station latitude",
    		Boolean.FALSE, 60, 60, 3, 2, null, -1);
    Element temp1 = new Element(6, "TEMP1","temp sensor 1 average temp for hour", 
            Boolean.FALSE, 60, 60, null, 1, "deg_C", 1);
    public static Predicate<StreamElement> PUBLIC_PRECISION_NEG1 = new Predicate<StreamElement>() {
        @Override
        public boolean apply(final StreamElement value) {
            if (value.getPublishedDecimalPlaces()==null) return false;
            return value.getPublishedDecimalPlaces() == -1;
        }
    };
    public static Predicate<StreamElement> PUBLIC_PRECISION_ZERO = new Predicate<StreamElement>() {
        @Override
        public boolean apply(final StreamElement value) {
            if (value.getPublishedDecimalPlaces()==null) return false;
            return value.getPublishedDecimalPlaces() == 0;
        }
    };
    public static Predicate<StreamElement> STORED_PRECISION_NEG1 = new Predicate<StreamElement>() {
        @Override
        public boolean apply(final StreamElement value) {
            if (value.getDecimalPlaces()==null) return false;
            return value.getDecimalPlaces() == -1;
        }
    };
    public static Predicate<StreamElement> STORED_PRECISION_ZERO = new Predicate<StreamElement>() {
        @Override
        public boolean apply(final StreamElement value) {
            if (value.getDecimalPlaces()==null) return false;
            return value.getDecimalPlaces() == 0;
        }
    };

    @Test
    public final void testStreamDaoImpl() {
        assertNotNull("streamDao not properly constructed",streamDao);
    }
    // TODO after update all TABLE7 values execute updateDummyData scripts and commit, then remove these two methods
    @BeforeClass
    public final static void updateDummyData() {
    	JdbcTemplate template = ApplicationContextProvider.Contexts.UNIT
    			.getApplicationContext().getBean(JdbcTemplate.class);
    	String update = "update TABLE8 set COL1=-1, COL2=-1 where ID1=8 and ID2=5";
    	template.execute(update);
    	update = "update TABLE8 set COL2=-1 where ID1=6 and ID2=4";
       	template.execute(update);
    }
    @AfterClass
    public final static void rollbackDummyData() {
    	JdbcTemplate template = ApplicationContextProvider.Contexts.UNIT
    			.getApplicationContext().getBean(JdbcTemplate.class);
    	String update = "update TABLE8 set COL1=2, COL2=2 where ID1=8 and ID2=5";
       	template.execute(update);
    	update = "update TABLE8 set COL2=2 where ID1=6 and ID2=4";
       	template.execute(update);
    }

    @Test
    public final void testGetStreamInt() {
        Stream stream = streamDao.getStream(8);
        assertTrue("measures temp",stream.getMeasuresTemp());
        assertTrue("measures precip",stream.getMeasuresPrecip());
        assertTrue("measures rh",stream.getMeasuresRH());
        assertTrue("measures wind",stream.getMeasuresWind());
        assertTrue("measures soil",stream.getMeasuresSoil());
        assertTrue("has end of observation marker",stream.getHasEndOfObMarker());
        assertEquals("element count",206,stream.getElementCount());
        assertFalse("doesn't have secondary transmitter",
        		stream.getHasSecondaryTransmitter());
        
        stream = streamDao.getStream(11);
        assertNotNull("didn't get stream 11",stream);
        assertTrue("expected secondary transmitter",
        		stream.getHasSecondaryTransmitter());

        /* verify correct number of fans */
        // 11,15,16 are AK streams with 2 fans
        int[] streams_with_2 = {11,15,16,51,52,53,54}; 
        for (int streamid : streams_with_2) {
            stream = streamDao.getStream(streamid);
            assertNotNull("null stream: "+streamid,stream);
            assertEquals("incorrect # fans stream "+stream.getStreamId(),
            		2,stream.getTempFans());
        }
        
        int[] streams_with_3 = {1,2,3,4,6,7,8,9,10,12,13,14};
        for (int streamid : streams_with_3) {
            stream = streamDao.getStream(streamid);
            assertEquals("incorrect # fans stream "+stream.getStreamId(),
            		3,stream.getTempFans());
        }
        /* Check whether or not the stream measures IR */
        // all but stream 5 + hcnm
        int[] streams_with_ir = {1,2,3,4,6,7,8,9,10,11,12,13,14,15,16};
        for (int streamid : streams_with_ir) {
            stream = streamDao.getStream(streamid);
            assertTrue("stream "+stream.getStreamId()+" measures IR",
            		stream.getMeasuresIr());
            if (streamid==1 || streamid==3 || streamid==6) {
            	assertFalse("stream "+streamid+" doesn't measure IR body temp",
            			stream.getMeasuresIrSensorTemp());
            } else {
            	assertTrue("stream "+streamid+" measures IR body temp",
            			stream.getMeasuresIrSensorTemp());
            }
        }
        int[] streams_without_ir = {5,51,52,53,54}; // other streams
        for (int streamid : streams_without_ir) {
            stream = streamDao.getStream(streamid);
            assertFalse("stream "+stream.getStreamId()+" does not measure IR",
            		stream.getMeasuresIr());
        }
        
        /* validate new 5 minute variables in new streams */
        int[] new_5_min_streams = {12,13,14};
        for (int streamid : new_5_min_streams) {
            stream = streamDao.getStream(streamid);
        	assertFalse("stream "+streamid+" doesn't measure 5 min winds at 10m", 
        			stream.getMeasures5MinWindAt10m());
        	assertTrue("stream "+streamid+" measures 5 min winds at 1.5m", 
        			stream.getMeasures5MinWindAt1_5m());
        	assertTrue("stream "+streamid+" measures 5 min solarad", 
        			stream.getMeasuresSolarad());
        	assertTrue("stream "+streamid+" measures 5 min solarad", 
        			stream.getMeasuresSolarad5Min());
        	assertTrue("stream "+streamid+" measures 5 min RH", 
        			stream.getMeasuresRh5Min());
        	assertTrue("stream "+streamid+" measures 5 min IR", 
        			stream.getMeasuresIr5Min());
        	assertTrue("stream "+streamid+" measures 5 min IR sensor body", 
        			stream.getMeasuresIrSensorTemp());
        	if (streamid!=14) {
            	assertTrue("stream "+streamid+" measures 5 min soil", 
            			stream.getMeasuresSoil5MinAt5cm());
        	} else {
            	assertFalse("stream 14 doesn't measure 5 min soil", 
            			stream.getMeasuresSoil5MinAt5cm());
        	}
        }
        int[] new_streams_201207 = {15,16};
        for (int streamid : new_streams_201207) {
        	stream = streamDao.getStream(streamid);
        	if (streamid==15) { 
            	assertFalse("stream "+streamid+" should not measure 6m winds", 
            			stream.getMeasures5MinWindAt6m());
        	} else {
            	assertTrue("stream "+streamid+" should measure 6m winds", 
            			stream.getMeasures5MinWindAt6m());
        	}
        	assertTrue("stream "+streamid+" should measure 5min ir", 
        			stream.getMeasuresIr5Min());
        	assertTrue("stream "+streamid+" should measure ir sensor temp",
        			stream.getMeasuresIrSensorTemp());
        	assertTrue("stream "+streamid+" should measure corrected ir",
        			stream.getMeasuresCorrectedIr5Min());
        	
        	assertTrue("stream "+streamid+" should measure 2nd ir sensor temp",
        			stream.getMeasuresSecondaryIrSensorTemp());
        	assertTrue("stream "+streamid+" should measure 2nd corrected ir",
        			stream.getMeasuresSecondaryCorrectedIr5Min());
        	assertTrue("stream "+streamid+" should measure 2nd panel temp",
        			stream.getMeasuresSecondaryPanelTemp());
        	assertTrue("stream "+streamid+" should measure 2nd diag temp",
        			stream.getMeasuresSecondaryDiagnosticTemps());
        }
    }

    @Test
    public final void testGetStreams() {
        Map<Integer,Stream> streams = streamDao.getStreams();
        assertTrue("incorrect size", streams.size()>=11);
    }

    @Test
    public final void testGetStreamString() {
        Stream stream = streamDao.getStream("1.303");
        assertEquals("expected stream 4",4,stream.getStreamId());
    }
    @Test
    public final void testGetStreamStringInt() {
        Stream stream = streamDao.getStream("1.001",80);
        assertNotNull("expected stream 3",stream);
        assertEquals("expected stream 3",3,stream.getStreamId());
        stream = streamDao.getStream("1.002",80);
        assertNotNull("expected stream 3",stream);
        assertEquals("expected stream 3",3,stream.getStreamId());
        stream = streamDao.getStream("1.003",80);
        assertNotNull("expected stream 3",stream);
        assertEquals("expected stream 3",3,stream.getStreamId());
        stream = streamDao.getStream("1.004",80);
        assertNotNull("expected stream 3",stream);
        assertEquals("expected stream 3",3,stream.getStreamId());
        stream = streamDao.getStream("1.001",52);
        assertNotNull("expected stream 6",stream);
        assertEquals("expected stream 6",6,stream.getStreamId());
        stream = streamDao.getStream("1.002",52);
        assertNotNull("expected stream 6",stream);
        assertEquals("expected stream 6",6,stream.getStreamId());
        stream = streamDao.getStream("1.003",52);
        assertNotNull("expected stream 6",stream);
        assertEquals("expected stream 6",6,stream.getStreamId());
        stream = streamDao.getStream("1.004",52);
        assertNotNull("expected stream 6",stream);
        assertEquals("expected stream 6",6,stream.getStreamId());
        
        stream = streamDao.getStream("1.001",53);
        assertNull("expected no stream (wrong # elements)",stream);
    }

    @Test
    public final void testGetStreamElementList() {
        List<Integer> remainingStreams = Lists.newArrayList(1,2,4,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,51,52,53,54);
        for (int streamId : remainingStreams) {
            streamCheck(streamId);
        }
        // spot check some additional ordering in streamElementList
        List<StreamElement> streamElements = streamDao.getStreamElementList(8);
        // longitude is the 5th value in stream 8
        StreamElement expected = new StreamElement(longitude, 8, "0.01", -1, -1);
        StreamElement result = streamElements.get(4);
		assertEquals("stream element list not properly ordered", 
				expected.getElementId(), result.getElementId());
		assertEquals("stream elements not equal", expected, result);
		// because defaults should be used, get from element object
		// most elements fall in this category
		assertEquals("decimals wrong", 
				longitude.getDefaultDecimalPlaces(), result.getDecimalPlaces());
		assertEquals("published decimals wrong", 
				longitude.getDefaultPublishedDecimalPlaces(), 
				result.getPublishedDecimalPlaces());
        
        streamElements = streamDao.getStreamElementList(6);
        expected = new StreamElement(temp1, 6, "1", 2, -1);
        result = streamElements.get(5);
		assertEquals("stream element list not properly ordered", 
				expected.getElementId(), result.getElementId());
		assertEquals("stream elements not equal", expected, result);
		// default should NOT be used for storage
		assertEquals("decimals wrong (override should be used)",
				Integer.valueOf(2), result.getDecimalPlaces());
        assertEquals("decimals wrong (default should be used)",
                Integer.valueOf(1), result.getPublishedDecimalPlaces());


        streamElements = streamDao.getStreamElementList(-4);
        assertEquals("shouldn't get any list for nonexistent stream",0,streamElements.size());
    }
    private void streamCheck(int streamId) {
        Stream stream;
        List<StreamElement> streamElements;
        stream = streamDao.getStream(streamId);
        streamElements = streamDao.getStreamElementList(streamId);
        checkStreamElementPrecision(streamElements);
        assertEquals ("wrong number of elements retrieved for stream "+streamId,
        		stream.getElementCount(),streamElements.size());
    }

    private void checkStreamElementPrecision(List<StreamElement> streamElements) {
        int streamId = streamElements.get(0).getStreamId();
        List<StreamElement> badElements = FluentIterable.from(streamElements)
            .filter(STORED_PRECISION_NEG1).toList();
        assertTrue("stream "+streamId+" contains -1 stored elements",badElements.size()==0);
        badElements = FluentIterable.from(streamElements).filter(PUBLIC_PRECISION_NEG1).toList();
        assertTrue("stream "+streamId+" contains -1 public precision elements",badElements.size()==0);
    }
    @Test
    public final void testGetStreamElementListForPda() {
        List<Integer> remainingStreams = Lists.newArrayList(1,2,4,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,51,52,53,54);
        for (int streamId : remainingStreams) {
            streamCheck(streamId);
        }
        
        List<StreamElement> streamElements = streamDao.getStreamElementListForPda(8);
        // get elementId=45 and assert its value=1
        StreamElement bvufl = getBvufl(streamElements);
        assertNotNull("no bvufl element found in stream 8",bvufl);
        assertEquals("didn't get correct multiplier",BigDecimal.ONE,bvufl.getMultiplier());

        StreamElement expected = new StreamElement(longitude, 8, "1", -1, -1);
        // longitude is the 5th value in stream 8
        StreamElement result = streamElements.get(4);
		assertEquals("stream element list not properly ordered", 
				expected.getElementId(), result.getElementId());
		assertEquals("stream elements not equal", expected, result);
		// because defaults should be used, get from element object
		// most elements fall in this category
		assertEquals("decimals wrong", 
				longitude.getDefaultDecimalPlaces(), result.getDecimalPlaces());
		assertEquals("published decimals wrong", 
				longitude.getDefaultPublishedDecimalPlaces(), 
				result.getPublishedDecimalPlaces());
        
        streamElements = streamDao.getStreamElementList(6);
        expected = new StreamElement(temp1, 6, "1", 2, -1);
        result = streamElements.get(5);
        assertEquals("stream element list not properly ordered", 
                expected.getElementId(), result.getElementId());
        assertEquals("stream elements not equal", expected, result);
        // default should NOT be used for storage
        assertEquals("decimals wrong (override should be used)",
                Integer.valueOf(2), result.getDecimalPlaces());
        assertEquals("decimals wrong (default should be used)",
                Integer.valueOf(1), result.getPublishedDecimalPlaces());

        streamElements = streamDao.getStreamElementListForPda(7);
        // get elementId=45 and assert its value=.01
        bvufl = getBvufl(streamElements);
        assertNotNull("no bvufl element found in stream 7",bvufl);
        assertEquals("didn't get correct multiplier","0.01",bvufl.getMultiplier().toPlainString());
        
        // stream 11 has no configuration; should default to 1
        streamElements = streamDao.getStreamElementListForPda(11);
        // get elementId=45 and assert its value=.01
        bvufl = getBvufl(streamElements);
        assertNotNull("no bvufl element found in stream 11",bvufl);
        assertEquals("didn't get correct multiplier","1",bvufl.getMultiplier().toPlainString());
    }

    private StreamElement getBvufl(List<StreamElement> streamElements) {
        StreamElement bvufl = null;
        String name = "BV_UFL";
        for (StreamElement selem : streamElements) {
            if (name.equals(selem.getName())) {
                bvufl=selem;
                break;
            }
        }
        return bvufl;
    }

}
