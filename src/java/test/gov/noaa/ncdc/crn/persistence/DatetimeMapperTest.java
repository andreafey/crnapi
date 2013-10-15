package gov.noaa.ncdc.crn.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import gov.noaa.ncdc.crn.domain.Datetime;
import gov.noaa.ncdc.crn.util.TimeUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations={"classpath:application-context.xml"})
@ActiveProfiles(profiles="unittest")
public class DatetimeMapperTest {
	@Autowired
	private DatetimeMapper mapper;

	@Test
	public void testSelectDatetimeIdsString() {
		String yyyymmddhh = "2012050215";
		Integer id = mapper.selectDatetimeIds(yyyymmddhh);
		assertNotNull("should have retrieved datetimeId", id);
		assertEquals("incorrect id", Integer.valueOf(101478), id);
	}

	@Test
	public void testSelectDatetimeIdsStringString() {
		List<Integer> datetimeIds =
				mapper.selectDatetimeIds("2012050215", "2012050217");
		assertNotNull("should have retrieved id list", datetimeIds);
		assertEquals("expected 3 ids in list", 3, datetimeIds.size());
		assertEquals(Integer.valueOf(101478), datetimeIds.get(0));
		assertEquals(Integer.valueOf(101479), datetimeIds.get(1));
		assertEquals(Integer.valueOf(101480), datetimeIds.get(2));
	}

	@Test
	public void testSelectDatetimesString() {
		String yyyymmddhh = "2012050215";
		Datetime datetime = mapper.selectDatetimes(yyyymmddhh);
		assertNotNull("should have retrieved datetime", datetime);
		assertEquals("incorrect utc time", yyyymmddhh, datetime.getDatetime0_23());
	}

	@Test
	public void testSelectDatetimesInt() {
		int datetimeId = 101478;
    	Datetime datetime = mapper.selectDatetimes(datetimeId);
    	assertNotNull("datetime should be in database", datetime);
    	Datetime expected = new Datetime(datetimeId, "2012050215");
    	assertEquals("incorrect datetime retrieved", expected, datetime);
	}

	@Test
	public void testSelectDatetimesStringString() {
		List<Datetime> datetimes =
				mapper.selectDatetimes("2012050215", "2012050217");
		assertNotNull("should have retrieved datetime list", datetimes);
		assertEquals("expected 3 datetimes in list", 3, datetimes.size());
		assertEquals(101478, datetimes.get(0).getDatetimeId());
		assertEquals(101479, datetimes.get(1).getDatetimeId());
		assertEquals(101480, datetimes.get(2).getDatetimeId());
	}

	@Test
	public void testSelectDatetimesCollectionOfInteger() {
		Integer[] arr = {101478, 101479, 101480};
		Map<Integer,Datetime> map = mapper.selectDatetimes(Arrays.asList(arr));
		assertEquals("expected same number in map as requested", 
				arr.length, map.size());
		for (int id : arr) {
			assertNotNull("did not get datetime for "+id, map.get(id));
		}
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testInsertDatetime() {
		String yyyymmddhh = "2025050608";
    	Calendar cal = TimeUtils.createUTCCalendar(yyyymmddhh);
    	int datetimeId = TimeUtils.computeDateTimeId(cal);
    	
    	Datetime datetime = mapper.selectDatetimes(datetimeId);
    	assertNull("datetime should not yet be in database", datetime);
    	
    	mapper.insertDatetime(datetimeId, 2025, cal.get(Calendar.DAY_OF_YEAR),
    			800, yyyymmddhh+"00", yyyymmddhh);
    	datetime = mapper.selectDatetimes(datetimeId);
    	assertNotNull("datetime should have been inserted", datetime);
    	Datetime expected = new Datetime(datetimeId, yyyymmddhh);
    	assertEquals("expected datetime differs from inserted", 
    			expected, datetime);
	}

	@Test
	public void testSelectLastDatetime() {
		Datetime datetime = mapper.selectLastDatetime();
		int expected = 108087;
		assertEquals("incorrect last datetime - has new datetime been added?", 
				expected, datetime.getDatetimeId());
	}

}
