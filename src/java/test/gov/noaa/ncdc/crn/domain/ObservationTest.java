package gov.noaa.ncdc.crn.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.noaa.ncdc.crn.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.xml.bind.JAXBException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.gson.Gson;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations={"classpath:application-context.xml"})
@ActiveProfiles(profiles="unittest")
public class ObservationTest {
	final static String TMP_DIR = "tmp";
	final static String TEST_DATA_DIR = "src/resources/test/data/obs";
	private SimpleDateFormat dateFormat =
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	Observation testOb;
	
	@Before
	public void setup() throws JAXBException, IOException {
		File file = new File(TMP_DIR);
		if (file.exists()&&!file.isDirectory()) {
			file.delete();
		}
		if (!file.exists()) {
			file.mkdir();
		}
		Gson gson = new Gson();
    	String filepath = TEST_DATA_DIR+"/ob-1001-90001.json";
    	String json = FileUtils.readWholeFile(filepath);
    	testOb = gson.fromJson(json, Observation.class);

	}
	@AfterClass
	public static void testResource() {
		/* These are here b/c I ran into classpath issues when compiling with 
		 * java 6 from a java 7 compiler without setting bootclasspath option.
		 * Leaving to avoid future problems */
		File myfile = new File(TEST_DATA_DIR+"/ob-1001-90001.json");
//		This was helpful in determining the execution directory was different than expected
//		System.out.println("absolute path: "+myfile.getAbsolutePath());
		assertTrue("Missing test file. Check execution path.", myfile.exists());
	}
	@After
	public void cleanup() {
		// empty test directory
		File tmpdir = new File(TMP_DIR);
		File[] files = tmpdir.listFiles();
		for (File file : files) {
			file.delete();
		}
	}

	@Test
	public void testHashCode() {
		Observation ob = 
				new Observation(1001, 90001, 8, 1, "Crn_201101100602.lrgs", 2);
		testOb.setLastModified(null);
		testOb.setInitialLoad(null);
		testOb.setTimeExportedToIsd(null);
		testOb.setTimeLoaded(null);
		assertEquals(testOb.hashCode(),ob.hashCode());
	}

	@Test
	public void testObservationIntIntIntIntStringInt() {
		Observation ob = 
				new Observation(1001, 90001, 8, 1, "Crn_201101100602.lrgs", 2);
		testOb.setLastModified(null);
		testOb.setInitialLoad(null);
		testOb.setTimeExportedToIsd(null);
		testOb.setTimeLoaded(null);
		assertEquals(testOb,ob);

	}
//    <stationId>1001</stationId>
//    <datetimeId>90001</datetimeId>
//    <streamId>8</streamId>
//    <source>1</source>
//    <initialLoad>2011-01-10 11:02:02</initialLoad>
//    <lastModified>2012-03-06 14:37:39</lastModified>
//    <exportedToIsd>2011-04-20 18:30:09</exportedToIsd>
//    <file>Crn_201101100602.lrgs</file>
//    <line>2</line>
//    <fileLoadTime>2011-01-10 11:02:02</fileLoadTime>
	@Test
	public void testGetDataSourceId() {
//	    <source>1</source>
		assertEquals(1, testOb.getDataSourceId());
	}

	@Test
	public void testGetDatetimeId() {
//	    <datetimeId>90001</datetimeId>
		assertEquals(90001, testOb.getDatetimeId());
	}

	@Test
	public void testGetInitialLoad() throws ParseException {
//	    <initialLoad>2011-01-10 11:02:02</initialLoad>
		Timestamp expected = stringToTimestamp("2011-01-10 11:02:02");
		assertEquals(expected,testOb.getInitialLoad());
	}

	@Test
	public void testSetInitialLoad() throws ParseException {
		Timestamp expected = stringToTimestamp("2012-05-06 14:14:14");
		testOb.setInitialLoad(stringToTimestamp("2012-05-06 14:14:14"));
		assertEquals(expected, testOb.getInitialLoad());
	}

	@Test
	public void testGetLastModified() throws ParseException {
//	    <lastModified>2012-03-06 14:37:39</lastModified>
		Timestamp expected = stringToTimestamp("2012-03-06 14:37:39");
		assertEquals(expected,testOb.getLastModified());
	}

	@Test
	public void testSetLastModified() throws ParseException {
		Timestamp expected = stringToTimestamp("2012-05-06 14:14:14");
		testOb.setLastModified(stringToTimestamp("2012-05-06 14:14:14"));
		assertEquals(expected, testOb.getLastModified());
	}

	@Test
	public void testGetStationId() {
//	    <stationId>1001</stationId>
		assertEquals(1001, testOb.getStationId());
	}

	@Test
	public void testGetStreamId() {
//	    <streamId>8</streamId>
		assertEquals(8, testOb.getStreamId());
	}

	@Test
	public void testGetTimeExportedToIsd() throws ParseException {
	//  <exportedToIsd>2011-04-20 18:30:09</exportedToIsd>
		Timestamp expected = stringToTimestamp("2011-04-20 18:30:09");
		assertEquals(expected,testOb.getTimeExportedToIsd());
	}

	@Test
	public void testSetTimeExportedToIsd() throws ParseException {
		Timestamp expected = stringToTimestamp("2012-05-06 14:14:14");
		testOb.setTimeExportedToIsd(stringToTimestamp("2012-05-06 14:14:14"));
		assertEquals(expected, testOb.getTimeExportedToIsd());
	}

	@Test
	public void testGetFileName() {
//	    <file>Crn_201101100602.lrgs</file>
		assertEquals("Crn_201101100602.lrgs", testOb.getFileName());
	}

	@Test
	public void testGetLineNumber() {
//	    <line>2</line>
		assertEquals(Integer.valueOf(2), testOb.getLineNumber());
	}

	@Test
	public void testGetTimeLoaded() throws ParseException {
	//  <fileLoadTime>2011-01-10 11:02:02</fileLoadTime>
		Timestamp expected = stringToTimestamp("2011-01-10 11:02:02");
		assertEquals(expected,testOb.getTimeLoaded());
	}

	@Test
	public void testSetTimeLoaded() throws ParseException {
		Timestamp expected = stringToTimestamp("2012-05-06 14:14:14");
		testOb.setTimeLoaded(stringToTimestamp("2012-05-06 14:14:14"));
		assertEquals(expected, testOb.getTimeLoaded());
	}

	@Test
	public void testGetStationDate() {
		StationDate expected = new StationDate(1001,90001);
		assertEquals(expected,testOb.getStationDate());
	}

	@Test
	public void testCompareTo() {
		Observation ob = 
				new Observation(1001, 90002, 8, 1, "Crn_201101100602.lrgs", 3);
		assertTrue(ob.compareTo(testOb)>0);
	}

	@Test
	public void testEquals() throws ParseException {
		Observation ob = 
				new Observation(1001, 90001, 8, 1, "Crn_201101100602.lrgs", 2);
		assertFalse(ob.equals(testOb));
		assertFalse(testOb.equals(ob));
//	    <initialLoad>2011-01-10 11:02:02</initialLoad>
//	    <lastModified>2012-03-06 14:37:39</lastModified>
//	    <exportedToIsd>2011-04-20 18:30:09</exportedToIsd>
//	    <fileLoadTime>2011-01-10 11:02:02</fileLoadTime>
		ob.setLastModified(stringToTimestamp("2012-03-06 14:37:39"));
		ob.setInitialLoad(stringToTimestamp("2011-01-10 11:02:02"));
		ob.setTimeExportedToIsd(stringToTimestamp("2011-04-20 18:30:09"));
		ob.setTimeLoaded(stringToTimestamp("2011-01-10 11:02:02"));
		assertTrue(ob.equals(testOb));
		assertTrue(testOb.equals(ob));
	}

	@Test
	public void testToString() {
		String toString = testOb.toString();
		assertTrue(toString.contains("1001"));
		assertTrue(toString.contains("90001"));
	}

	private Timestamp stringToTimestamp(String string) throws ParseException {
		return new Timestamp(dateFormat.parse(string).getTime());
	}

}
