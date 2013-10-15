package gov.noaa.ncdc.crn.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FlagsTest {

	@Test
	public void testFlags() {
		Flags flags = new Flags();
		assertNotNull(flags);
		assertFalse(flags.isFlagged());
	}

	@Test
	public void testFlagsInt() {
		Flags flags = new Flags();
		assertNotNull(flags);
		assertFalse(flags.isFlagged());
		flags = new Flags(4);
		assertNotNull(flags);
		assertTrue(flags.isFlagged());
		assertEquals(4,flags.getIntValue());
	}

	@Test
	public void testIsFlagged() {
		Flags flags = new Flags();
		assertFalse(flags.isFlagged());
		flags = new Flags(9);
		assertTrue(flags.isFlagged());
	}

	@Test
	public void testIsFlaggedFlagType() {
		Flags flags = new Flags();
		assertFalse(flags.isFlagged());
		for (FlagType type : FlagType.values()) {
			assertFalse(flags.isFlagged(type));
		}
		flags = new Flags(9); // DOOR & EXCEPTION
		assertTrue(flags.isFlagged(FlagType.RANGE));
		assertTrue(flags.isFlagged(FlagType.EXCEPTION));
		flags = new Flags(16); // FROZEN
		assertTrue(flags.isFlagged(FlagType.FROZEN));
		assertFalse(flags.isFlagged(FlagType.EXCEPTION));
		assertFalse(flags.isFlagged(FlagType.RANGE));
		assertFalse(flags.isFlagged(FlagType.DELTA));
		assertFalse(flags.isFlagged(FlagType.DOOR));
		assertFalse(flags.isFlagged(FlagType.SENSOR));
		flags = new Flags(32); // SENSOR
		assertTrue(flags.isFlagged(FlagType.SENSOR));
		assertFalse(flags.isFlagged(FlagType.EXCEPTION));
		assertFalse(flags.isFlagged(FlagType.RANGE));
		assertFalse(flags.isFlagged(FlagType.DELTA));
		assertFalse(flags.isFlagged(FlagType.DOOR));
		assertFalse(flags.isFlagged(FlagType.FROZEN));
		
		
	}

	@Test
	public void testSetFlaggedFlagTypeBoolean() {
		Flags flags = new Flags();
		flags.setFlagged(FlagType.RANGE,true);
		assertTrue(flags.isFlagged(FlagType.RANGE));
		int expectedInt = 1;
		assertEquals(expectedInt,flags.getIntValue());
		flags.setFlagged(FlagType.EXCEPTION,true);
		assertTrue(flags.isFlagged(FlagType.EXCEPTION));
		expectedInt = 9;
		assertEquals(expectedInt,flags.getIntValue());
		flags.setFlagged(FlagType.RANGE,false);
		assertFalse(flags.isFlagged(FlagType.RANGE));
		expectedInt=8;
		assertEquals(expectedInt,flags.getIntValue());
	}

	@Test
	public void testSetFlaggedFlagType() {
		Flags flags = new Flags();
		flags.setFlagged(FlagType.RANGE);
		assertTrue(flags.isFlagged(FlagType.RANGE));
		int expectedInt = 1;
		assertEquals(expectedInt,flags.getIntValue());
		flags.setFlagged(FlagType.EXCEPTION);
		assertTrue(flags.isFlagged(FlagType.EXCEPTION));
		expectedInt = 9;
		assertEquals(expectedInt,flags.getIntValue());
	}

	@Test
	public void testGetFlag() {
		Flags flags = new Flags();
		int flagValue=flags.getIntValue();
		int expected = 0;
		assertEquals(expected,flagValue);
		flags.setFlagged(FlagType.RANGE);
		flagValue=flags.getIntValue();
		expected=1;
		assertEquals(expected,flagValue);

		flags.setFlagged(FlagType.DOOR);
		flagValue=flags.getIntValue();
		expected=5;
		assertEquals(expected,flagValue);

		flags.setFlagged(FlagType.EXCEPTION);
		flagValue=flags.getIntValue();
		expected=13;
		assertEquals(expected,flagValue);
		
		flags.setFlagged(FlagType.DOOR,false);
		flagValue=flags.getIntValue();
		expected=9;
		assertEquals(expected,flagValue);

		flags.setFlagged(FlagType.RANGE,false);
		flagValue=flags.getIntValue();
		expected=8;
		assertEquals(expected,flagValue);
	}

}
