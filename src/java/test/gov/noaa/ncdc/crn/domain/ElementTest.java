package gov.noaa.ncdc.crn.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ElementTest {

    private Element element;
    private int elementId=9999;
    private String name = "MYELEM";
    private String description = "This is a special element for me";
    @Before
    public final void setup() {
        element = new Element(elementId,name,description, Boolean.FALSE, 60, 60, null, null, null, -1);
    }

    @Test
    public final void testGetElementId() {
        assertEquals("element ids don't agree",elementId,element.getElementId());
    }

    @Test
    public final void testGetName() {
        assertEquals("element names don't agree",name,element.getName());
    }

    @Test
    public final void testGetDescription() {
        assertEquals("element descriptions don't agree",description,element.getDescription());
    }

    @Test
    public final void testCompareTo() {
        Element myelement = new Element(elementId,name,description, Boolean.FALSE, 60, 60, null, null, null, -1);
        assertEquals("elements not equal",0,myelement.compareTo(element));
        assertEquals("elements not equal",0,element.compareTo(myelement));
        myelement = new Element(4,name,description, Boolean.FALSE, 60, 60, null, null, null, -1);
        assertTrue("elements not equal",myelement.compareTo(element)<0);
        assertTrue("elements not equal",element.compareTo(myelement)>0);
        myelement = new Element(elementId,"FOO","bar", Boolean.FALSE, 60, 60, null, null, null, -1);
        // expected behavior is to compare only elementids
        assertEquals("elements not equal",0,myelement.compareTo(element));
        assertEquals("elements not equal",0,element.compareTo(myelement));
    }

    @Test
    public final void testEqualsObject() {
        Element myelement = new Element(elementId,name,description, Boolean.FALSE, 60, 60, null, null, null, -1);
        assertEquals("elements not equal",element,myelement);
        assertTrue("elements not equal",element.equals(myelement));
        assertTrue("elements not equal",myelement.equals(element));
        myelement = new Element(4,name,description, Boolean.FALSE, 60, 60, null, null, null, -1);
        assertFalse("elements equal",element.equals(myelement));
        assertFalse("elements equal",myelement.equals(element));
        myelement = new Element(elementId,name,description, Boolean.FALSE, 60, 60, null, null, null, -1);
        assertEquals("elements not equal",element,myelement);
        assertTrue("elements not equal",element.equals(myelement));
        assertTrue("elements not equal",myelement.equals(element));
    }

}
