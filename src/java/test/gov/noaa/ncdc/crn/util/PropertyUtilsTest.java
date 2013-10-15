package gov.noaa.ncdc.crn.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations={"classpath:application-context-unit-props.xml"})
@ActiveProfiles(profiles="unittest")
public class PropertyUtilsTest {

	@Test
	public void testGetProperty() {
		String propval = PropertyUtils.getProperty("my.test.prop.true");
		assertEquals("expected my.test.prop.true set to true","true",propval);
		propval = PropertyUtils.getProperty("my.fake.prop");
		assertNull("fake prop",propval);
	}

	@Test
	public void testIsPropertyTrue() {
		boolean propval = PropertyUtils.isPropertyTrue("my.test.prop.true");
		assertTrue("my.test.prop.true expected true",propval);
		propval = PropertyUtils.isPropertyTrue("my.test.prop.false");
		assertFalse("my.test.prop.false expected not true",propval);
		propval = PropertyUtils.isPropertyTrue("my.test.prop.blue");
		assertFalse("my.test.prop.blue expected not true",propval);
		propval = PropertyUtils.isPropertyTrue("my.fake.prop");
		assertFalse("my.fake.prop expected not true",propval);
	}

}
