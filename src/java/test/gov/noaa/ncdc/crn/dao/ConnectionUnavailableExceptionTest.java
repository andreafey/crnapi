package gov.noaa.ncdc.crn.dao;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ConnectionUnavailableExceptionTest {

	@Test
	public void testConnectionUnavailableException() {
		String expected = ConnectionUnavailableException.DEFAULT_ERROR;
		Exception e = new ConnectionUnavailableException();
		assertEquals("wrong error", expected,e.getMessage());
	}

	@Test
	public void testConnectionUnavailableExceptionString() {
		Exception e = new ConnectionUnavailableException(null);
		String expected = ConnectionUnavailableException.DEFAULT_ERROR;
		assertEquals("wrong error", expected,e.getMessage());
		expected = "WRONG";
		e = new ConnectionUnavailableException(expected);
		assertEquals("wrong error", expected,e.getMessage());
	}

	@Test
	public void testGetMessage() {
		String expected = ConnectionUnavailableException.DEFAULT_ERROR;
		Exception e = new ConnectionUnavailableException();
		assertEquals("wrong error", expected,e.getMessage());
	}

}
