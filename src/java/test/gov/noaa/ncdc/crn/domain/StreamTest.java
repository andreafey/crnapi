package gov.noaa.ncdc.crn.domain;

import static org.junit.Assert.*;

import org.junit.Test;



public class StreamTest {

    @Test
    public void testToString() {
        // can't mock this because toString gets overridden
        Stream stream = new Stream(88,12,100, false, false, false, 3, false, false, false, false, false, false, false, false, false, 
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false );
        String toString = stream.toString();
        assertTrue(toString.contains("Stream"));
        assertTrue(toString.contains("id=88"));
        assertTrue(toString.contains("frequency=12"));
    }

}
