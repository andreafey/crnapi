package gov.noaa.ncdc.crn.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ElementGroupTest {
	
	private static final Integer[] elids = {5, 6, 7, 443};
	private static final ElementGroup group = 
			new ElementGroup(1, 4, "test", 443, Arrays.asList(elids));

	@Test
	public void testElementGroup() {
		// deprecated
	}

	@Test
	public void testElementGroupIntIntStringIntListOfInteger() {
		ElementGroup group = 
				new ElementGroup(1, 4, "test", 443, Arrays.asList(elids));
		assertNotNull(group);
	}

	@Test
	public void testSetElementGroupId() {
		// deprecated
	}

	@Test
	public void testGetElementGroupId() {
		assertEquals(1,group.getElementGroupId());
	}

	@Test
	public void testSetOrdinal() {
		// deprecated
	}

	@Test
	public void testGetOrdinal() {
		assertEquals(4,group.getOrdinal());
	}

	@Test
	public void testSetDescription() {
		// deprecated
	}

	@Test
	public void testGetDescription() {
		assertEquals("test",group.getDescription());
	}

	@Test
	public void testGetRepresentativeElement() {
		assertEquals(443,group.getRepresentativeElement());
	}

	@Test
	public void testGetMemberElements() {
		List<Integer> expected = Arrays.asList(elids);
		assertEquals("didn't get correct member elements",
				expected, group.getMemberElements());
	}

	@Test
	public void testCompareTo() {
		ElementGroup group = 
				new ElementGroup(1, 4, "test", 443, Arrays.asList(elids));
		assertEquals("identical, expected 0",0, group.compareTo(ElementGroupTest.group));
		group = new ElementGroup(1, 5, "test", 443, Arrays.asList(elids));
		assertTrue("expected local group to fall after in order",
				group.compareTo(ElementGroupTest.group)>0);
		group = new ElementGroup(2, 3, "test", 443, Arrays.asList(elids));
		assertTrue("expected local group to fall after in order",
				group.compareTo(ElementGroupTest.group)>0);
	}

	@Test
	public void testHashCode() {
		group.hashCode();
	}

	@Test
	public void testEqualsObject() {
		String foo = "foo";
		assertFalse("foo string not expected equal",group.equals(foo));
		ElementGroup group = 
				new ElementGroup(1, 4, "test", 443, Arrays.asList(elids));
		assertTrue("identical ElementGroup construction",
				ElementGroupTest.group.equals(group));
		assertTrue("identical ElementGroup construction opposite dir",
				group.equals(ElementGroupTest.group));
	}

	@Test
	public void testToString() {
		group.toString();
	}

}
