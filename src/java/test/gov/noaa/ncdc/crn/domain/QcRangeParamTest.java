package gov.noaa.ncdc.crn.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

public class QcRangeParamTest {

	private int elementId=234;
	private Integer stationId=1234;
	private Integer streamId=52;
	private Integer month=4;
	private BigDecimal lo=new BigDecimal("-100").setScale(1);
	private BigDecimal hi=new BigDecimal("150").setScale(1);
	// extracting to global because I think subtrahend is a cool word
	private BigDecimal subtrahend = new BigDecimal("0.1").setScale(1);
	private QcRangeParam qcr;
	@Before
	public void setUp() {
		qcr = new QcRangeParam(elementId,streamId,stationId,month,lo,hi);
	}
	@Test
	public final void testQcRangeParamIntIntegerIntegerIntegerFloatFloat() {
		assertEquals(elementId,qcr.getElementId());
		assertEquals(streamId, qcr.getStreamId());
		assertEquals(stationId, qcr.getStationId());
		assertEquals(month,qcr.getMonth());
		assertEquals(hi,qcr.getHi());
		assertEquals(lo,qcr.getLo());
	}

	@Test
	public final void testGetElementId() {
		assertEquals(elementId,qcr.getElementId());
	}

	@Test
	public final void testGetStreamId() {
		assertEquals(streamId, qcr.getStreamId());
	}

	@Test
	public final void testGetStationId() {
		assertEquals(stationId, qcr.getStationId());
	}

	@Test
	public final void testGetMonth() {
		assertEquals(month,qcr.getMonth());
	}

	@Test
	public final void testGetLo() {
		assertEquals(lo,qcr.getLo());
	}

	@Test
	public final void testGetHi() {
		assertEquals(hi,qcr.getHi());
	}

	@Test
	public final void testCompareTo() {
		QcRangeParam compare = new QcRangeParam(elementId, streamId, stationId, month, lo, hi);
		assertTrue(compare.compareTo(qcr)==0);
		assertTrue(qcr.compareTo(compare)==0);
		// compareTo does not consider range (lo/hi) for ordering
		compare = new QcRangeParam(elementId, streamId, stationId, month, lo, hi.subtract(subtrahend));
		assertTrue(compare.compareTo(qcr)==0);
		assertTrue(qcr.compareTo(compare)==0);
		compare = new QcRangeParam(elementId, streamId, stationId, month, lo.subtract(subtrahend), hi);
		assertTrue(compare.compareTo(qcr)==0);
		assertTrue(qcr.compareTo(compare)==0);
		compare = new QcRangeParam(elementId, streamId, stationId, (month-1), lo, hi);
		assertTrue(compare.compareTo(qcr)<0);
		assertTrue(qcr.compareTo(compare)>0);
		compare = new QcRangeParam(elementId, streamId, (stationId-1), month, lo, hi);
		assertTrue(compare.compareTo(qcr)<0);
		assertTrue(qcr.compareTo(compare)>0);
		compare = new QcRangeParam(elementId, (streamId-1), stationId, month, lo, hi);
		assertTrue(compare.compareTo(qcr)<0);
		assertTrue(qcr.compareTo(compare)>0);
		compare = new QcRangeParam((elementId-1), streamId, stationId, month, lo, hi);
		assertTrue(compare.compareTo(qcr)<0);
		assertTrue(qcr.compareTo(compare)>0);
		compare = new QcRangeParam(elementId, streamId, stationId, month, lo, null);
		assertTrue(compare.compareTo(qcr)==0);
		assertTrue(qcr.compareTo(compare)==0);
		compare = new QcRangeParam(elementId, streamId, stationId, month, null, hi);
		assertTrue(compare.compareTo(qcr)==0);
		assertTrue(qcr.compareTo(compare)==0);
		compare = new QcRangeParam(elementId, streamId, stationId, null, lo, hi);
		assertTrue(compare.compareTo(qcr)>0);
		assertTrue(qcr.compareTo(compare)<0);
		compare = new QcRangeParam(elementId, streamId, null, month, lo, hi);
		assertTrue(compare.compareTo(qcr)>0);
		assertTrue(qcr.compareTo(compare)<0);
		compare = new QcRangeParam(elementId, null, stationId, month, lo, hi);
		assertTrue(compare.compareTo(qcr)>0);
		assertTrue(qcr.compareTo(compare)<0);
		compare = new QcRangeParam((elementId-1), (streamId+1), stationId, month, lo, hi);
		assertTrue(compare.compareTo(qcr)<0);
		assertTrue(qcr.compareTo(compare)>0);
		compare = new QcRangeParam(elementId, null, stationId, null, lo, hi);
		assertTrue(compare.compareTo(qcr)>0);
		assertTrue(qcr.compareTo(compare)<0);
		compare = new QcRangeParam(elementId, null, null, null, lo, hi);
		assertTrue(compare.compareTo(qcr)>0);
		assertTrue(qcr.compareTo(compare)<0);
		QcRangeParam qcr1 = new QcRangeParam(elementId, null, stationId, null, lo, hi);
		compare = new QcRangeParam(elementId, null, (stationId-1), null, lo, hi);
		assertTrue(compare.compareTo(qcr1)<0);
		assertTrue(qcr1.compareTo(compare)>0);
	}

	@Test
	public final void testEqualsObject() {
		QcRangeParam compare = new QcRangeParam(elementId, streamId, stationId, month, lo, hi);
		assertTrue(compare.equals(qcr));
		assertTrue(qcr.equals(compare));
		compare = new QcRangeParam(elementId, streamId, stationId, month, lo, hi.subtract(subtrahend));
		assertFalse(compare.equals(qcr));
		assertFalse(qcr.equals(compare));
		compare = new QcRangeParam(elementId, streamId, stationId, month, lo.subtract(subtrahend), hi);
		assertFalse(compare.equals(qcr));
		assertFalse(qcr.equals(compare));
		compare = new QcRangeParam(elementId, streamId, stationId, (month-1), lo, hi);
		assertFalse(compare.equals(qcr));
		assertFalse(qcr.equals(compare));
		compare = new QcRangeParam(elementId, streamId, (stationId-1), month, lo, hi);
		assertFalse(compare.equals(qcr));
		assertFalse(qcr.equals(compare));
		compare = new QcRangeParam(elementId, (streamId-1), stationId, month, lo, hi);
		assertFalse(compare.equals(qcr));
		assertFalse(qcr.equals(compare));
		compare = new QcRangeParam((elementId-1), streamId, stationId, month, lo, hi);
		assertFalse(compare.equals(qcr));
		assertFalse(qcr.equals(compare));
		compare = new QcRangeParam(elementId, streamId, stationId, month, lo, BigDecimal.ZERO);
		assertFalse(compare.equals(qcr));
		assertFalse(qcr.equals(compare));
		compare = new QcRangeParam(elementId, streamId, stationId, month, BigDecimal.ZERO, hi);
		assertFalse(compare.equals(qcr));
		assertFalse(qcr.equals(compare));
		compare = new QcRangeParam(elementId, streamId, stationId, null, lo, hi);
		assertFalse(compare.equals(qcr));
		assertFalse(qcr.equals(compare));
		compare = new QcRangeParam(elementId, streamId, null, month, lo, hi);
		assertFalse(compare.equals(qcr));
		assertFalse(qcr.equals(compare));
	}

	// just making sure nothing crashes by repeatedly calling hashCode()
	@Test
	public final void testHashCode() {
		int elementId=234;
		Integer stationId=1234;
		int streamId=52;
		Integer month=4;
		
		QcRangeParam qcr = 
				new QcRangeParam(elementId,streamId,stationId,month,lo,hi);
		qcr.hashCode();
        stationId=null;
        qcr = new QcRangeParam(elementId,streamId,stationId,month,lo,hi);
        qcr.hashCode();
        month=null;
        qcr = new QcRangeParam(elementId,streamId,stationId,month,lo,hi);
        qcr.hashCode();
        lo=null;
        qcr = new QcRangeParam(elementId,streamId,stationId,month,lo,hi);
        qcr.hashCode();
        hi=null;
        qcr = new QcRangeParam(elementId,streamId,stationId,month,lo,hi);
        qcr.hashCode();
	}


	// just making sure nothing crashes by calling toString()
	@Test
	public final void testToString() {
		int elementId=234;
		Integer stationId=1234;
		int streamId=52;
		Integer month=4;
		
		QcRangeParam qcr = 
				new QcRangeParam(elementId,streamId,stationId,month,lo,hi);
		qcr.toString();
        stationId=null;
        qcr = new QcRangeParam(elementId,streamId,stationId,month,lo,hi);
        qcr.toString();
        month=null;
        qcr = new QcRangeParam(elementId,streamId,stationId,month,lo,hi);
        qcr.toString();
        lo=null;
        qcr = new QcRangeParam(elementId,streamId,stationId,month,lo,hi);
        qcr.toString();
        hi=null;
        qcr = new QcRangeParam(elementId,streamId,stationId,month,lo,hi);
		qcr.toString();
	}

}
