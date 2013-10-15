package gov.noaa.ncdc.crn.util;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

public class ConversionUtilsTest {

	@Test
	public void testC_to_fDouble() {
		double expected=73.58;
		assertEquals(expected,ConversionUtils.c_to_f(23.1),.001);
	}

	@Test
	public void testC_to_fString() {
		String expected = "73.6";
		assertEquals(expected,ConversionUtils.c_to_f("23.1"));
	}

	@Test
	public void testC_to_fBigDecimal() {
		String expected = "73.58";
		assertEquals(expected,ConversionUtils.c_to_f(new BigDecimal("23.1")).toString());
	}

	@Test
	public void testMm_to_inchesDouble() {
		double expected = .909;
		assertEquals(expected,ConversionUtils.mm_to_inches(23.1),.001);
	}

	@Test
	public void testMm_to_inchesString() {
		String expected = "0.91";
		assertEquals(expected, ConversionUtils.mm_to_inches("23.1"));
	}

	@Test
	public void testMm_to_inchesBigDecimal() {
		String expected = "0.91";
		assertEquals(expected, ConversionUtils.mm_to_inches(new BigDecimal("23.1").toString()));
	}

}
