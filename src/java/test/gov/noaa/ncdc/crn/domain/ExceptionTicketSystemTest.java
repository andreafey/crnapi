package gov.noaa.ncdc.crn.domain;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

public class ExceptionTicketSystemTest {

	@Test
	public void testGetURL() throws MalformedURLException {
		URL expected = new URL("http://crntools.cms-b.ncdc.noaa.gov/trac/exceptions/ticket/3");
		String id = Integer.toString(3);
		URL result = ExceptionTicketSystem.TRAC.getURL(id);
		assertEquals("urls differ", expected, result);
	}

}
