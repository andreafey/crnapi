package gov.noaa.ncdc.crn.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StreamElementTest {

	@Test
	public final void testEqualsObject() 
	{

	    int elementId=9999;
	    String name = "MYELEM";
	    String description = "This is a special element for me";
	    Element myelem = new Element(elementId,name,description, Boolean.TRUE, 60, 60, null, null, null, -1);
	    
	    StreamElement element = 
	    	new StreamElement(myelem, 8,".01", 2, 1);
	    StreamElement myelement = 
	    	new StreamElement(myelem, 8,".01", 2, 1);
	    	   
        assertEquals("streamelements not equal",element,myelement);
        assertTrue("streamelements not equal",element.equals(myelement));
        assertTrue("streamelements not equal",myelement.equals(element));

        assertEquals("elements not equal", element, myelement);
        assertTrue("elements not equal",element.equals(myelement));
        assertTrue("elements not equal",myelement.equals(element));
        
        // all fields must agree for stream element but not element
        myelement = new StreamElement(myelem, 8,".001", 2, 1);
        assertFalse("elements equal",element.equals(myelement));
        assertFalse("elements equal",myelement.equals(element));

        // all fields must agree for stream element but not element
        myelement = new StreamElement(myelem, 8,".01", 3, 1);
        assertFalse("elements equal",element.equals(myelement));
        assertFalse("elements equal",myelement.equals(element));

        // all fields must agree for stream element but not element
        myelement = new StreamElement(myelem, 8,".01", 2, 2);
        assertFalse("elements equal",element.equals(myelement));
        assertFalse("elements equal",myelement.equals(element));

	}
/*
	@Test
	public final void testToString() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testStreamElementIntStringStringIntStringIntegerInteger() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetStreamId() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetMultiplier() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetDecimalPlaces() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetPublishedDecimalPlaces() {
		fail("Not yet implemented"); // TODO
	}
*/
}
