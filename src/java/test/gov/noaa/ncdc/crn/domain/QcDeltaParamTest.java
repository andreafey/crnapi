package gov.noaa.ncdc.crn.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

public class QcDeltaParamTest {

	private int elementId1=234;
	private int elementId2=235;
	private int elementId3=236;
	private Integer stationId=1234;
	private Integer streamId=52;
	private Integer month=4;
	private BigDecimal delta = 
			new BigDecimal("0.3").setScale(1);
	// extracting to global because I think subtrahend is a cool word
	private BigDecimal subtrahend = 
			new BigDecimal("0.1").setScale(1);
	private QcDeltaParam qcd;
	private QcDeltaParam qcdNull;
	@Before
	public void setUp() {
		qcd = new QcDeltaParam(streamId, stationId, month, elementId1, 
				elementId2, elementId3, delta);
		qcdNull = new QcDeltaParam(null, null, null, 0, 0, 0, null);
	}
	@Test
	public final void testQcDeltaParam() {
		assertNull(qcdNull.getStreamId());
		assertNull(qcdNull.getStationId());
		assertNull(qcdNull.getMonth());
		for (int i=0;i<3;i++) {
			assertEquals(0,qcdNull.getElementIds()[i]);
		}
		assertNull(qcdNull.getDelta());
	}

	@Test
	public final void testQcDeltaParamIntegerIntegerIntegerIntIntIntFloat() {
		assertEquals(elementId1,qcd.getElementIds()[0]);
		assertEquals(elementId2,qcd.getElementIds()[1]);
		assertEquals(elementId3,qcd.getElementIds()[2]);
		assertEquals(streamId, qcd.getStreamId());
		assertEquals(stationId, qcd.getStationId());
		assertEquals(month,qcd.getMonth());
		assertEquals(delta,qcd.getDelta());
	}

	@Test
	public final void testGetElementIds() {
		int[] expected = {elementId1,elementId2,elementId3};
		for (int i=0;i<3;i++) {
			assertEquals(expected[i],qcd.getElementIds()[i]);
		}
	}

	@Test
	public final void testGetStreamId() {
		assertEquals(streamId, qcd.getStreamId());
	}

	@Test
	public final void testGetStationId() {
		assertEquals(stationId, qcd.getStationId());
	}

	@Test
	public final void testGetMonth() {
		assertEquals(month,qcd.getMonth());
	}

	@Test
	public final void testGetDelta() {
		assertEquals(delta,qcd.getDelta());
	}

	@Test
	public final void testCompareTo() {
		QcDeltaParam compare = new QcDeltaParam(streamId, stationId, month, 
				elementId1, elementId2, elementId3, delta);
		assertTrue(compare.compareTo(qcd)==0);
		assertTrue(qcd.compareTo(compare)==0);
		compare = new QcDeltaParam(streamId, stationId, month, elementId1, 
				elementId2, elementId3, delta.subtract(subtrahend));
		// delta not considered in comparison
		assertTrue(compare.compareTo(qcd)==0);
		assertTrue(qcd.compareTo(compare)==0);
		compare = new QcDeltaParam(streamId-1, stationId, month, elementId1, 
				elementId2, elementId3, delta);
		assertTrue(compare.compareTo(qcd)<0);
		assertTrue(qcd.compareTo(compare)>0);
		compare = new QcDeltaParam(streamId, stationId-1, month, elementId1, 
				elementId2, elementId3, delta);
		assertTrue(compare.compareTo(qcd)<0);
		assertTrue(qcd.compareTo(compare)>0);
		compare = new QcDeltaParam(streamId, stationId, month-1, elementId1, 
				elementId2, elementId3, delta);
		assertTrue(compare.compareTo(qcd)<0);
		assertTrue(qcd.compareTo(compare)>0);
		compare = new QcDeltaParam(streamId, stationId, month, elementId1+10, 
				elementId2, elementId3, delta);
		assertTrue(compare.compareTo(qcd)>0);
		assertTrue(qcd.compareTo(compare)<0);
		compare = new QcDeltaParam(streamId, stationId, month, elementId1, 
				elementId2+10, elementId3, delta);
		// only elementId1 considered in comparison
		assertTrue(compare.compareTo(qcd)==0);
		assertTrue(qcd.compareTo(compare)==0);
		compare = new QcDeltaParam(streamId, stationId, month, elementId1, 
				elementId2, elementId3+10, delta);
		assertTrue(compare.compareTo(qcd)==0);
		assertTrue(qcd.compareTo(compare)==0);
		compare = new QcDeltaParam(null, stationId, month, elementId1, 
				elementId2, elementId3, delta);
		assertTrue(compare.compareTo(qcd)>0);
		assertTrue(qcd.compareTo(compare)<0);
		compare = new QcDeltaParam(streamId, null, month, elementId1, 
				elementId2, elementId3, delta);
		assertTrue(compare.compareTo(qcd)>0);
		assertTrue(qcd.compareTo(compare)<0);
		compare = new QcDeltaParam(streamId, stationId, null, elementId1, 
				elementId2, elementId3, delta);
		assertTrue(compare.compareTo(qcd)>0);
		assertTrue(qcd.compareTo(compare)<0);
		compare = new QcDeltaParam(streamId, stationId-1, month, elementId1, 
				elementId2, elementId3, delta);
		QcDeltaParam qcd1 = new QcDeltaParam(streamId-1, stationId, month, 
				elementId1, elementId2, elementId3, delta);
		assertTrue(compare.compareTo(qcd1)>0);
		assertTrue(qcd1.compareTo(compare)<0);
	}

	@Test
	public final void testEqualsObject() {
		QcDeltaParam compare = new QcDeltaParam(streamId, stationId, month, 
				elementId1, elementId2, elementId3, delta);
		assertTrue(compare.equals(qcd));
		assertTrue(qcd.equals(compare));
		compare = new QcDeltaParam(streamId, stationId, month, elementId1, 
				elementId2, elementId3, delta.subtract(subtrahend));
		assertFalse(compare.equals(qcd));
		assertFalse(qcd.equals(compare));
		compare = new QcDeltaParam(streamId-1, stationId, month, elementId1, 
				elementId2, elementId3, delta);
		assertFalse(compare.equals(qcd));
		assertFalse(qcd.equals(compare));
		compare = new QcDeltaParam(streamId, stationId-1, month, elementId1, 
				elementId2, elementId3, delta);
		assertFalse(compare.equals(qcd));
		assertFalse(qcd.equals(compare));
		compare = new QcDeltaParam(streamId, stationId, month-1, elementId1, 
				elementId2, elementId3, delta);
		assertFalse(compare.equals(qcd));
		assertFalse(qcd.equals(compare));
		compare = new QcDeltaParam(streamId, stationId, month, elementId1+10, 
				elementId2, elementId3, delta);
		assertFalse(compare.equals(qcd));
		assertFalse(qcd.equals(compare));
		compare = new QcDeltaParam(streamId, stationId, month, elementId1, 
				elementId2+10, elementId3, delta);
		assertFalse(compare.equals(qcd));
		assertFalse(qcd.equals(compare));
		compare = new QcDeltaParam(streamId, stationId, month, elementId1, 
				elementId2, elementId3+10, delta);
		assertFalse(compare.equals(qcd));
		assertFalse(qcd.equals(compare));
		compare = new QcDeltaParam(null, stationId, month, elementId1, 
				elementId2, elementId3, delta);
		assertFalse(compare.equals(qcd));
		assertFalse(qcd.equals(compare));
		compare = new QcDeltaParam(streamId, null, month, elementId1, 
				elementId2, elementId3, delta);
		assertFalse(compare.equals(qcd));
		assertFalse(qcd.equals(compare));
		compare = new QcDeltaParam(streamId, stationId, null, elementId1, 
				elementId2, elementId3, delta);
		assertFalse(compare.equals(qcd));
		assertFalse(qcd.equals(compare));
	}

	@Test
	public final void testHashCode() {
		qcd.hashCode();
        stationId=null;
        QcDeltaParam qcd = new QcDeltaParam(streamId, stationId, month, 
        		elementId1, elementId2, elementId3, delta);
		qcd.hashCode();
        month=null;
		qcd = new QcDeltaParam(streamId, stationId, month, elementId1, 
				elementId2, elementId3, delta);
		qcd.hashCode();
        streamId=null;
		qcd = new QcDeltaParam(streamId, stationId, month, elementId1, 
				elementId2, elementId3, delta);
		qcd.hashCode();
	}

	@Test
	public final void testToString() {
		qcd.toString();
	}

}
