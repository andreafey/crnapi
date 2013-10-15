package gov.noaa.ncdc.crn.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.noaa.ncdc.crn.dao.DatetimeDao;
import gov.noaa.ncdc.crn.dao.ElementDao;
import gov.noaa.ncdc.crn.domain.ElementValue;
import gov.noaa.ncdc.crn.domain.Observation;
import gov.noaa.ncdc.crn.domain.ObservationWithData;
import gov.noaa.ncdc.crn.domain.StationDate;
import gov.noaa.ncdc.crn.domain.StationDateElement;
import gov.noaa.ncdc.crn.spring.ApplicationContextProvider;
import gov.noaa.ncdc.crn.spring.ApplicationContextProvider.Contexts;
import gov.noaa.ncdc.crn.util.FileUtils;
import gov.noaa.ncdc.crn.util.TimeUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml" })
@ActiveProfiles(profiles = "unittest")
public class ObservationServiceTest {
    @Autowired
    private ObservationService service;
    @Autowired
    private ElementDao elementDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SQLExceptionTranslator translator;
    private int jan2008;

    private static final int insertDt = 6000; // has to precede station's POR or get null last nonmissing
    private static final int stationId = 1026;

    @BeforeClass
    public final static void inserts() {
        cleanup(); // just in case it did not run when last finished
        ApplicationContext prodRO = ApplicationContextProvider.Contexts.PROD_RO.getApplicationContext();
        SystemMaintenanceService maintSvc = prodRO.getBean(SystemMaintenanceService.class);
        maintSvc.insertDatetimeRangeIntoUnitTestFromContext(insertDt, insertDt + 1);

        // insert test obs for LastNonmissing tests
        Observation ob1 = new Observation(stationId, insertDt, 52, 3, "foo.txt", 4);
        ObservationWithData owd1 = new ObservationWithData(ob1, new HashMap<Integer, ElementValue>());
        owd1.addNewElementValue(16, new BigDecimal("4.2")); // not calculated
        owd1.addNewElementValue(342, new BigDecimal("4.0")); // T5_12
        Observation ob2 = new Observation(stationId, insertDt + 1, 52, 3, "foo.txt", 5);
        ObservationWithData owd2 = new ObservationWithData(ob2, new HashMap<Integer, ElementValue>());
        owd2.addNewElementValue(16, new BigDecimal("4.9")); // not calculated

        ApplicationContext context = ApplicationContextProvider.Contexts.UNIT.getApplicationContext();
        ObservationService service = context.getBean(ObservationService.class);
        service.insertObservations(Lists.newArrayList(owd1, owd2));

    }

    @AfterClass
    public final static void cleanup() {
        ApplicationContext context = ApplicationContextProvider.Contexts.UNIT.getApplicationContext();
        ObservationService service = context.getBean(ObservationService.class);
        service.deleteObservations(stationId, insertDt, insertDt + 1);
        JdbcTemplate template = context.getBean(JdbcTemplate.class);
        template.update("delete from TABLE1 where ID between ? and ?", insertDt, insertDt + 10);
    }

    private static Log LOG = LogFactory.getLog(ObservationServiceTest.class);

    int datetimeIdforIns = 94127;
    int stationIdforIns = 1326;

    int datetimeIdforInsCalc = 63500;
    int stationIdforInsCalc = 1326;
    Integer[] elemsforInsCalc = { 331, 332, 333 };
    // minutes in millis
    long TWO_MIN = 1000 * 60 * 2;
    long ONE_HOUR = 100 * 60 * 60;

    @Before
    public final void setup() throws JAXBException {
        DatetimeDao datetimeDao = Contexts.UNIT.getApplicationContext().getBean(DatetimeDao.class);
        jan2008 = datetimeDao.getDatetimeId("2008010101");
        Observation ob = service.getObservation(datetimeIdforInsCalc, stationIdforInsCalc);
        if (!ob.getInitialLoad().equals(ob.getLastModified())) {
            // test requires this condition, so update the db
            updateTestObDates(jdbcTemplate, datetimeIdforInsCalc, stationIdforInsCalc);
        }
        assertEquals("starting state should be ob dates unmodified", ob.getInitialLoad(), ob.getLastModified());
        boolean obexists = obRemnantsExistJdbc(datetimeIdforIns, stationIdforIns, jdbcTemplate);
        if (obexists) {
            LOG.trace("remnant exists: " + obexists);
            deleteRemnantsJdbc(datetimeIdforIns, stationIdforIns, jdbcTemplate);
            obexists = obRemnantsExistJdbc(datetimeIdforIns, stationIdforIns, jdbcTemplate);
            if (obexists)
                LOG.warn("remnant exists after delete");
        }
        boolean calcexists = obFactsExistJdbc(jdbcTemplate, datetimeIdforInsCalc, stationIdforInsCalc, elemsforInsCalc);
        if (calcexists) {
            LOG.trace("calc exists: " + calcexists);
            deleteFactRemnantsJdbc(jdbcTemplate, datetimeIdforInsCalc, stationIdforInsCalc, elemsforInsCalc);
            calcexists = obFactsExistJdbc(jdbcTemplate, datetimeIdforInsCalc, stationIdforInsCalc, elemsforInsCalc);
            if (calcexists)
                LOG.warn("remnant exists after delete");
        }
    }

    /**
     * <pre>
     * ---------- ---------- -----------
     *       1026        130       64127
     *       1026        439       64127
     *       1026        440       64127
     *       1026        130       64128
     *       1026        439       64128
     *       1026        440       64128
     *       1026        130       64129
     *       1026        439       64129
     *       1026        440       64129
     *       1326        130       64127
     *       1326        439       64127
     *       1326        440       64127
     *       1326        130       64128
     *       1326        439       64128
     *       1326        440       64128
     *       1326        130       64129
     *       1326        439       64129
     *       1326        440       64129
     * </pre>
     */
    // 439-440 are calculated, 130 is not
    @Test
    @Transactional
    @DirtiesContext
    @Rollback(true)
    public final void testDeleteCalculatedValues1() {
        int begin = 64127, end = 64129;
        int stationId = 1026;
        int[] elementIds = { 439, 440 };
        /* delete value that is calculated and exists for one station */

        // first check that the calc values exist prior to deletion
        List<ObservationWithData> existingObs = service.getObservationsWithData(begin, end, stationId);
        assertEquals("expected 3 obs", 3, existingObs.size());
        for (ObservationWithData testOb : existingObs) {
            Map<Integer, ElementValue> vals = testOb.getElementValues();
            assertNotNull("didn't get 439", vals.get(439));
            assertNotNull("didn't get 440", vals.get(440));
            assertNotNull("didn't get 130", vals.get(130));
        }

        // delete facts
        service.deleteCalculatedValuesFromObs(stationId, begin, end, elementIds);
        // mybatis doesn't support row count in BATCH mode
        // assertEquals("expected 6 values to be deleted", 6, numDeleted);

        // then check that the calc values are no longer in the ob; also
        // verifies the cache was cleared and that obs still exist
        existingObs = service.getObservationsWithData(begin, end, stationId);
        assertEquals("expected 3 obs", 3, existingObs.size());
        for (ObservationWithData testOb : existingObs) {
            Map<Integer, ElementValue> vals = testOb.getElementValues();
            assertNull("should have deleted 439", vals.get(439));
            assertNull("should have deleted 440", vals.get(440));
            assertNotNull("didn't get 130", vals.get(130));
        }

        // check last_mod date on observation was updated
        String obquery = "select ID1, ID2, COL1 from TABLE "
                + "where ID1 between 64127 and 64129 and ID2=1026";

        SqlRowSet rs = jdbcTemplate.queryForRowSet(obquery);
        Calendar now = TimeUtils.nowUTCCalendar();
        while (rs.next()) {
            int staId = rs.getInt("ID2");
            int datetimeId = rs.getInt("ID1");
            Date date = rs.getDate("COL1");
            assertNearEqual(now, date, staId, datetimeId);
        }
    }

    private void assertNearEqual(Calendar now, Date date, int staId, int datetimeId) {
        // very circuitous route around just comparing the two bleeping dates; comparing two *identical*
        // dates fails because the millis are very off (there was a 240 hour offset)
        Calendar stupidCal = getEditTime(date);
        long millisDiff = Math.abs(now.getTimeInMillis() - stupidCal.getTimeInMillis());
        // less than two min elapsed; note comparing time diffs on different machines; may need to increase
        assertTrue(String.format("[%d, %d] too much time elapsed: %3$tD %3$tT [now %4$tD %4$tT]", staId, datetimeId,
                stupidCal, now, millisDiff), millisDiff < TWO_MIN);
    }

    private Calendar getEditTime(Date date) {
        String stupidYymmddhhmiss = String.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS", date);
        LOG.debug("stupid: " + stupidYymmddhhmiss);
        Calendar stupidCal = TimeUtils.nowUTCCalendar();
        stupidCal.set(Integer.valueOf(stupidYymmddhhmiss.substring(0, 4)),
                (Integer.valueOf(stupidYymmddhhmiss.substring(4, 6)) - 1),
                Integer.valueOf(stupidYymmddhhmiss.substring(6, 8)),
                Integer.valueOf(stupidYymmddhhmiss.substring(8, 10)),
                Integer.valueOf(stupidYymmddhhmiss.substring(10, 12)),
                Integer.valueOf(stupidYymmddhhmiss.substring(12)));
        return stupidCal;
    }

    private void assertNotNearEqual(Calendar now, Date date, int staId, int datetimeId) {
        Calendar stupidCal = getEditTime(date);
        long millisDiff = Math.abs(now.getTimeInMillis() - stupidCal.getTimeInMillis());
        // less than two min elapsed; note comparing time diffs on different machines; may need to increase
        assertTrue(String.format("[%d, %d] too much time elapsed: %3$tD %3$tT [now %4$tD %4$tT]", staId, datetimeId,
                stupidCal, now, millisDiff), millisDiff > ONE_HOUR);
    }

    @Test
    @Transactional
    @DirtiesContext
    @Rollback(true)
    public final void testDeleteCalculatedValues2() {
        int begin = 64127, end = 64129;
        int[] stationIds = { 1026, 1326 };
        int[] elementIds = { 439, 440 };
        /* delete value that is calculated and exists for all stations */

        // first check that the calc values exist prior to deletion
        for (int stationId : stationIds) {
            List<ObservationWithData> existingObs = service.getObservationsWithData(begin, end, stationId);
            assertEquals("expected 3 obs", 3, existingObs.size());
            for (ObservationWithData testOb : existingObs) {
                Map<Integer, ElementValue> vals = testOb.getElementValues();
                assertNotNull("didn't get 439", vals.get(439));
                assertNotNull("didn't get 440", vals.get(440));
                assertNotNull("didn't get 130", vals.get(130));
            }
        }

        // delete facts
        service.deleteCalculatedValuesFromObs(null, begin, end, elementIds);
        // mybatis impl doesn't support row count in BATCH mode
        // assertTrue("expected to delete at least 4 facts",numDeleted>=4);

        for (int stationId : stationIds) {
            // then check that the calc values are no longer in the ob; also
            // verifies the cache was cleared and that obs still exist
            List<ObservationWithData> existingObs = service.getObservationsWithData(begin, end, stationId);
            assertEquals("expected 3 obs", 3, existingObs.size());
            for (ObservationWithData testOb : existingObs) {
                Map<Integer, ElementValue> vals = testOb.getElementValues();
                assertNull("should have deleted 439", vals.get(439));
                assertNull("should have deleted 440", vals.get(440));
                assertNotNull("didn't get 130", vals.get(130));
            }
        }

        // check last_mod date on observation was updated
        String obquery = String.format(
                "select ID1, ID2, COL1 from TABLE"
                        + " where ID2 between %d and %d and ID1 in (%d,%d)", begin, end, stationIds[0],
                        stationIds[1]);

        SqlRowSet rs = jdbcTemplate.queryForRowSet(obquery);
        Calendar now = TimeUtils.nowUTCCalendar();
        while (rs.next()) {
            int stationId = rs.getInt("ID1");
            int datetimeId = rs.getInt("ID2");
            Date date = rs.getDate("COL1");
            assertNearEqual(now, date, stationId, datetimeId);
        }
    }

    @Test
    @Transactional
    @DirtiesContext
    @Rollback(true)
    public final void testDeleteCalculatedValues3() {
        int begin = 64127, end = 64129;
        int stationId = 1026;
        int[] elementIds = { 130 };
        /* delete value that is not calculated and exists; expect no-op */

        // first check that the calc values exist prior to deletion
        List<ObservationWithData> existingObs = service.getObservationsWithData(begin, end, stationId);
        assertEquals("expected 3 obs", 3, existingObs.size());
        for (ObservationWithData testOb : existingObs) {
            Map<Integer, ElementValue> vals = testOb.getElementValues();
            assertNotNull("didn't get 439 (prior to delete); testOb: " + testOb, vals.get(439));
            assertNotNull("didn't get 440 (prior to delete); testOb: " + testOb, vals.get(440));
            assertNotNull("didn't get 130 (prior to delete); testOb: " + testOb, vals.get(130));
        }

        // delete facts
        service.deleteCalculatedValuesFromObs(stationId, begin, end, elementIds);
        // mybatis doesn't support row count in BATCH mode
        // assertEquals("didn't expect anything to be deleted", 0, numDeleted);

        // then check that the calc values are no longer in the ob; also
        // verifies the cache was cleared and that obs still exist
        existingObs = service.getObservationsWithData(begin, end, stationId);
        assertEquals("expected 3 obs", 3, existingObs.size());
        for (ObservationWithData testOb : existingObs) {
            Map<Integer, ElementValue> vals = testOb.getElementValues();
            assertNotNull("didn't get 439 (after delete of 130 only); testOb: " + testOb, vals.get(439));
            assertNotNull("didn't get 440 (after delete of 130 only); testOb: " + testOb, vals.get(440));
            assertNotNull("didn't get 130 (after delete of 130 [not calc element] only); testOb: " + testOb,
                    vals.get(130));
        }

        // check last_mod date on observation was updated
        String obquery = "select ID1, ID2, COL1 from TABLE3 "
                + "where ID2 between 64127 and 64129 and ID1=1026";

        SqlRowSet rs = jdbcTemplate.queryForRowSet(obquery);
        Calendar now = TimeUtils.nowUTCCalendar();
        while (rs.next()) {
            int staId = rs.getInt("ID1");
            int datetimeId = rs.getInt("ID2");
            Date date = rs.getDate("COL1");
            assertNotNearEqual(now, date, staId, datetimeId);
        }
    }

    @Test
    @Transactional
    @DirtiesContext
    @Rollback(true)
    public final void testDeleteCalculatedValues4() {
        int begin = 64127, end = 64129;
        int[] stationIds = { 1026, 1326 }; // stream 4, 52
        int[] elementIds = { 109, 110 }; // 109,110 are observed vals in both streams not in fact table
        /* delete value that is not calculated and does not exist; expect no-op */

        // first check that the values don't exist prior to deletion
        for (int stationId : stationIds) {
            List<ObservationWithData> existingObs = service.getObservationsWithData(begin, end, stationId);
            assertEquals("expected 3 obs", 3, existingObs.size());
            for (ObservationWithData testOb : existingObs) {
                Map<Integer, ElementValue> vals = testOb.getElementValues();
                assertNull("didn't get 109", vals.get(109));
                assertNull("didn't get 110", vals.get(110));
            }
        }

        // delete facts
        service.deleteCalculatedValuesFromObs(null, begin, end, elementIds);
        // just hoping nothing fails
        // mybatis doesn't support row count in BATCH mode
        // assertEquals("didn't expect anything to be deleted",0, numDeleted);

        // check last_mod date on observation was NOT updated
        String obquery = String.format(
                "ID1, ID2, COL1 from TABLE"
                        + " where ID2 between %d and %d and ID1 in (%d,%d)", begin, end, stationIds[0],
                        stationIds[1]);

        SqlRowSet rs = jdbcTemplate.queryForRowSet(obquery);
        Calendar now = TimeUtils.nowUTCCalendar();
        while (rs.next()) {
            int stationId = rs.getInt("ID1");
            int datetimeId = rs.getInt("ID2");
            Date date = rs.getDate("COL1");
            // This will fail if the data has been refreshed recently
            assertNotNearEqual(now, date, stationId, datetimeId);
        }
    }

    @Test
    @Transactional
    @DirtiesContext
    @Rollback(true)
    public final void testDeleteCalculatedValues5() {
        int begin = 64127, end = 64129;
        int[] stationIds = { 1026, 1326 }; // stream 4, 52
        int[] elementIds = { 343 }; // 343 is calc val in both streams not in fact table;
        /* delete value that is calculated and does not exist; expect no-op */

        // first check that the values don't exist prior to deletion
        for (int stationId : stationIds) {
            List<ObservationWithData> existingObs = service.getObservationsWithData(begin, end, stationId);
            assertEquals("expected 3 obs", 3, existingObs.size());
            for (ObservationWithData testOb : existingObs) {
                Map<Integer, ElementValue> vals = testOb.getElementValues();
                assertNull("didn't get 343", vals.get(343));
            }
        }

        // delete facts
        service.deleteCalculatedValuesFromObs(null, begin, end, elementIds);
        // just hoping nothing fails
        // mybatis doesn't support row count in BATCH mode
        // assertEquals("didn't expect anything to be deleted",0,numDeleted);

        // check last_mod date on observation was NOT updated
        String obquery = String.format(
                "ID1, ID2, COL1 from TABLE"
                        + " where ID2 between %d and %d and ID1 in (%d,%d)", begin, end, stationIds[0],
                        stationIds[1]);

        SqlRowSet rs = jdbcTemplate.queryForRowSet(obquery);
        Calendar now = TimeUtils.nowUTCCalendar();
        while (rs.next()) {
            int stationId = rs.getInt("ID1");
            int datetimeId = rs.getInt("ID2");
            Date date = rs.getDate("COL1");
            assertNotNearEqual(now, date, stationId, datetimeId);
        }
    }

    @Test
    @Transactional
    @DirtiesContext
    @Rollback(true)
    public final void testDeleteCalculatedValues6() {
        int begin = 64127, end = 64129;
        int[] stationIds = { 1026, 1326 };
        int[] elementIds = { 343, 109, 130, 439 }; // 343 (calc, not exist), 109 (not calc, not exist), 130 (not calc),
        // 439 (calc, exists); expect only 439 deleted
        /* delete combination; expect updates successful where value is calc and exists; all others no-op */

        // first check that the calc values exist prior to deletion
        for (int stationId : stationIds) {
            List<ObservationWithData> existingObs = service.getObservationsWithData(begin, end, stationId);
            assertEquals("expected 3 obs", 3, existingObs.size());
            for (ObservationWithData testOb : existingObs) {
                Map<Integer, ElementValue> vals = testOb.getElementValues();
                assertNull("didn't expect 343", vals.get(343));
                assertNull("didn't expect 109", vals.get(109));
                assertNotNull("didn't get 130", vals.get(130));
                assertNotNull("didn't get 439", vals.get(439));
            }
        }

        // delete facts
        service.deleteCalculatedValuesFromObs(null, begin, end, elementIds);
        // mybatis doesn't support row count in BATCH mode
        // assertTrue("expected 2 facts bo be deleted",numDeleted>=2);

        for (int stationId : stationIds) {
            // then check that the calc values are no longer in the ob
            // checks that non-calc obs still exist
            // also verifies the cache was cleared and that obs still exist
            List<ObservationWithData> existingObs = service.getObservationsWithData(begin, end, stationId);
            assertEquals("expected 3 obs", 3, existingObs.size());
            for (ObservationWithData testOb : existingObs) {
                Map<Integer, ElementValue> vals = testOb.getElementValues();
                assertNull("should have deleted 439", vals.get(439));
                assertNotNull("should not have deleted 130", vals.get(130));
            }
        }

        // check last_mod date on observation was updated
        String obquery = String.format(
                "ID1, ID2, COL1 from TABLE1"
                        + " where ID2 between %d and %d and ID1 in (%d,%d)", begin, end, stationIds[0],
                        stationIds[1]);

        SqlRowSet rs = jdbcTemplate.queryForRowSet(obquery);
        Calendar now = TimeUtils.nowUTCCalendar();
        while (rs.next()) {
            int stationId = rs.getInt("ID1");
            int datetimeId = rs.getInt("ID2");
            Date date = rs.getDate("COL1");
            assertNearEqual(now, date, stationId, datetimeId);
        }
    }

    @Test
    @Transactional
    @DirtiesContext
    @Rollback(true)
    public final void testDeleteCalculatedValues7() {
        /** verify that when calculated values are deleted, other values remain unchanged */

        int begin = 98628, end = 98629;
        int stationId = 1035;
        int[] elementIds = { 439 }; // 439 (calc, flag=29)
        // also 440 (calc, flag=29), 188 (not calc, flag=1)

        /*
         * delete flagged calc value; expect ob deleted but other flags not; validates existing bug has been fixed
         */

        // first check that the values prior to deletion
        ObservationWithData ob = service.getObservationWithData(begin, stationId);
        ElementValue val = ob.getElementValue(439);
        assertNotNull("439 is null", val);
        assertEquals("439 wrong flag val", 29, val.getFlags().getIntValue());
        val = ob.getElementValue(440);
        assertNotNull("440 is null", val);
        assertEquals("440 wrong flag val", 29, val.getFlags().getIntValue());
        val = ob.getElementValue(188);
        assertNotNull("188 is null", val);
        assertEquals("188 wrong flag val", 1, val.getFlags().getIntValue());

        // delete facts
        service.deleteCalculatedValuesFromObs(stationId, begin, end, elementIds);
        ob = service.getObservationWithData(begin, stationId);

        // now check that 439 deleted and 440, 188 still there and flagged
        val = ob.getElementValue(439);
        assertNull("439 is null", val);
        val = ob.getElementValue(440);
        assertNotNull("440 is null", val);
        assertEquals("440 wrong flag val", 29, val.getFlags().getIntValue());
        val = ob.getElementValue(188);
        assertNotNull("188 is null", val);
        assertEquals("188 wrong flag val", 1, val.getFlags().getIntValue());
    }

    @Test
    @DirtiesContext
    @Rollback(true)
    public final void testInsertObservation() {
        if (LOG.isTraceEnabled())
            LOG.trace("Executing testInsertObservation()");
        /*
         * insert ob which not already in database
         */
        ElementValue ev1 = new ElementValue(stationIdforIns, datetimeIdforIns, 142, new BigDecimal("12.3"), 3, null, null);
        ElementValue ev2 = new ElementValue(stationIdforIns, datetimeIdforIns, 151, new BigDecimal("12.4"), 0, null, null);
        ElementValue ev3 = new ElementValue(stationIdforIns, datetimeIdforIns, 149, new BigDecimal("12048"), 7, null, null);
        StationDate stationDate = new StationDate(stationIdforIns, datetimeIdforIns);
        StationDateElement sd1 = new StationDateElement(stationDate, ev1.getElementId());
        StationDateElement sd2 = new StationDateElement(stationDate, ev2.getElementId());
        StationDateElement sd3 = new StationDateElement(stationDate, ev3.getElementId());
        Observation ob = fakeOb(datetimeIdforIns, stationIdforIns);

        // check ob doesn't exist
        Observation resultOb = service.getObservation(datetimeIdforIns, stationIdforIns);
        assertNull("ob should be null", resultOb);

        // check facts don't exist
        ElementValue resultEv = elementDao.getElementValue(sd1);
        assertNull("should not get ev1", resultEv);
        resultEv = elementDao.getElementValue(sd2);
        assertNull("should not get ev2", resultEv);
        resultEv = elementDao.getElementValue(sd3);
        assertNull("should not get ev3", resultEv);

        // insert obwithdata
        Map<Integer, ElementValue> values = new HashMap<>();
        values.put(ev1.getElementId(), ev1);
        values.put(ev2.getElementId(), ev2);
        values.put(ev3.getElementId(), ev3);
        ObservationWithData owd = new ObservationWithData(ob, values);
        service.insertObservation(owd);
        // check obwithdata exists
        ObservationWithData resultOwd = service.getObservationWithData(datetimeIdforIns, stationIdforIns);
        assertNotNull("did not get owd", resultOwd);
        // check ob the same
        resultOb = resultOwd.getObservation();
        assertEquals("datetime differs", ob.getDatetimeId(), resultOb.getDatetimeId());
        assertEquals("station differs", ob.getStationId(), resultOb.getStationId());
        assertEquals("datasource differs", ob.getDataSourceId(), resultOb.getDataSourceId());
        assertEquals("stream differs", ob.getStreamId(), resultOb.getStreamId());
        // check obloadlog data the same
        assertEquals("file differs", ob.getFileName(), resultOb.getFileName());
        assertEquals("lineno differs", ob.getLineNumber(), resultOb.getLineNumber());
        // check facts the same
        Map<Integer, ElementValue> resultVals = resultOwd.getElementValues();
        ElementValue comp = resultVals.get(ev1.getElementId());
        assertEquals("expected value differs ev1", ev1.getValue(), comp.getValue());
        comp = resultVals.get(ev2.getElementId());
        assertEquals("expected value differs ev2", ev2.getValue(), comp.getValue());
        comp = resultVals.get(ev3.getElementId());
        assertEquals("expected value differs ev3", ev3.getValue(), comp.getValue());

        // check that trigger caused fact/flag time_loaded to be populated (ev1, ev3 have flags)
        String factquery = "select COL1 from TABLE1 where ID1=? and ID2=? and ID3=?";
        String flagquery = "select COl1 from TABLE2 where ID1=? and ID2=? and ID3=?";
        Date loaded = jdbcTemplate.queryForObject(factquery, Date.class, ev1.getDatetimeId(), ev1.getStationId(),
                ev1.getElementId());
        assertNotNull("timestamp should be populated by trigger [ev1]", loaded);
        loaded = jdbcTemplate.queryForObject(flagquery, Date.class, ev1.getDatetimeId(), ev1.getStationId(),
                ev1.getElementId());
        assertNotNull("flag timestamp should be populated by trigger [ev1]", loaded);
        loaded = jdbcTemplate.queryForObject(factquery, Date.class, ev2.getDatetimeId(), ev2.getStationId(),
                ev2.getElementId());
        assertNotNull("timestamp should be populated by trigger [ev2]", loaded);
        loaded = jdbcTemplate.queryForObject(factquery, Date.class, ev3.getDatetimeId(), ev3.getStationId(),
                ev3.getElementId());
        assertNotNull("timestamp should be populated by trigger [ev3]", loaded);
        loaded = jdbcTemplate.queryForObject(flagquery, Date.class, ev3.getDatetimeId(), ev3.getStationId(),
                ev3.getElementId());
        assertNotNull("flag timestamp should be populated by trigger [ev3]", loaded);
        LOG.debug("date: " + loaded);
        /*
         * attempt to insert ob already in database
         */
        resultOb = service.getObservation(64127, stationIdforIns);
        owd = new ObservationWithData(resultOb, new HashMap<Integer, ElementValue>());
        // owd.setObservation(resultOb);
        try {
            service.insertObservation(owd);
            fail("expected to throw exception on insert of non-unique observation");
        } catch (DataAccessException e) {
            // this is expected
        }
    }

    @Test
    @Transactional
    @DirtiesContext
    @Rollback(true)
    public final void testInsertObservations() {
        int station1 = 1326, station2 = 1026;
        Map<Integer, Observation> existingObs = service.getObservations(datetimeIdforIns);
        assertTrue("should not be any obs for this datetime", existingObs.isEmpty());

        /*
         * insert ob which not already in database
         */
        // create a couple fake obs
        ElementValue ev1 = new ElementValue(station1, datetimeIdforIns, 142, new BigDecimal("12.3"), 3, null, null);
        ElementValue ev2 = new ElementValue(station1, datetimeIdforIns, 151, new BigDecimal("12.4"), 0, null, null);
        ElementValue ev3 = new ElementValue(station1, datetimeIdforIns, 149, new BigDecimal("12048"), 7, null, null);
        Observation ob1 = fakeOb(datetimeIdforIns, station1);
        Map<Integer, ElementValue> values1 = new HashMap<>();
        values1.put(ev1.getElementId(), ev1);
        values1.put(ev2.getElementId(), ev2);
        values1.put(ev3.getElementId(), ev3);
        ObservationWithData owd1 = new ObservationWithData(ob1, values1);

        ElementValue ev4 = new ElementValue(station2, datetimeIdforIns, 142, new BigDecimal("12.3"), 3, null, null);
        ElementValue ev5 = new ElementValue(station2, datetimeIdforIns, 151, new BigDecimal("12.4"), 0, null, null);
        ElementValue ev6 = new ElementValue(station2, datetimeIdforIns, 149, new BigDecimal("12048"), 7, null, null);
        Observation ob2 = fakeOb(datetimeIdforIns, station2);
        Map<Integer, ElementValue> values2 = new HashMap<Integer, ElementValue>();
        values2.put(ev4.getElementId(), ev4);
        values2.put(ev5.getElementId(), ev5);
        values2.put(ev6.getElementId(), ev6);
        ObservationWithData owd2 = new ObservationWithData(ob2, values2);

        // insert obswithdata
        List<ObservationWithData> obsToInsert = new ArrayList<ObservationWithData>();
        obsToInsert.add(owd1);
        obsToInsert.add(owd2);
        // this has transaction management
        service.insertObservations(obsToInsert);

        // test proper insert
        existingObs = service.getObservations(datetimeIdforIns);
        assertEquals("expected 2 obs to be inserted", 2, existingObs.size());
        for (Observation ob : existingObs.values()) {
            ObservationWithData owd = service.getObservationWithData(ob.getDatetimeId(), ob.getStationId());
            Map<Integer, ElementValue> vals = owd.getElementValues();
            assertEquals("expected 3 facts to be inserted: " + owd.getObservation(), 3, vals.values().size());
        }
    }

    // no Transaction annotation because it does not rollback until after method complete, so can't
    // verify rollback on exception
    @Test
    @DirtiesContext
    @Rollback(true)
    public final void testInsertObservationsFail() {
        int station3 = 1009;
        Map<Integer, Observation> noExistingObs = service.getObservations(datetimeIdforIns);
        assertTrue("should not be any obs for this datetime", noExistingObs.isEmpty());

        ObservationWithData existingOb = service.getObservationWithData(64131, station3);
        assertNotNull("ob should exist: ", existingOb);

        // obswithdata for insert
        List<ObservationWithData> obsToInsert = new ArrayList<ObservationWithData>();

        /*
         * Insert 2 obs - one is already in database; expect exception
         */
        ElementValue ev7 = new ElementValue(station3, datetimeIdforIns, 142, new BigDecimal("12.3"), 3, null, null);
        ElementValue ev8 = new ElementValue(station3, datetimeIdforIns, 151, new BigDecimal("12.4"), 0, null, null);
        ElementValue ev9 = new ElementValue(station3, datetimeIdforIns, 149, new BigDecimal("12048"), 7, null, null);
        Observation ob3 = fakeOb(datetimeIdforIns, station3);
        Map<Integer, ElementValue> values3 = new HashMap<Integer, ElementValue>();
        values3.put(ev7.getElementId(), ev7);
        values3.put(ev8.getElementId(), ev8);
        values3.put(ev9.getElementId(), ev9);
        ObservationWithData notExisting = new ObservationWithData(ob3, values3);

        // insert obswithdata
        obsToInsert.clear();
        // this one not in db
        obsToInsert.add(notExisting);
        obsToInsert.add(existingOb);
        try {
            service.insertObservations(obsToInsert);
            fail("Expected failure on trying to insert existingOb again");
        } catch (DataAccessException e) {
            // expected
        }
        System.out.println("notExisting: " + notExisting);
        ObservationWithData owd = service.getObservationWithData(notExisting.getDatetimeId(),
                notExisting.getStationId());
        System.out.println("owd : " + owd);
        assertNull("should not have inserted owd3: ", owd);
    }

    /**
     * <pre>
     *     ---------- ----------- --------- -------------- ------------ ------------- --------------------
     *           1009       64130         2              1 28-JAN-08    28-JAN-08     25-FEB-09
     *           1026       64130         4              1 10-DEC-08    10-DEC-08     25-FEB-09
     *           1326       64130        52              1 28-JAN-08    28-JAN-08     25-FEB-09
     * </pre>
     */
    @Test
    @Transactional
    @DirtiesContext
    @Rollback(true)
    public final void testDeleteObservation() {
        int stationId = 1326;
        int datetimeIdforDel = 64130;
        Map<String, Object> params = new HashMap<>();
        params.put("stationId", stationId);
        params.put("datetimeId", datetimeIdforDel);
        // check ob exists
        Observation ob = service.getObservation(datetimeIdforDel, stationId);
        assertNotNull("ob should be in the database to start", ob);

        service.deleteObservation(datetimeIdforDel, stationId);

        // check ob gone and that cache cleared
        ob = service.getObservation(datetimeIdforDel, stationId);
        assertNull("observation should be gone", ob);

        // check when no ob exists
        ob = service.getObservation(datetimeIdforIns, stationId);
        assertNull("ob should not yet exist", ob);

        service.deleteObservation(datetimeIdforIns, stationId);
    }

    /**
     * <pre>
     * ---------- ----------- --------- -------------- ------------ ------------- --------------------
     *       1009       64129         2              1 28-JAN-08    12-APR-12     25-FEB-09
     *       1026       64129         4              1 10-DEC-08    10-DEC-08     25-FEB-09
     *       1326       64129        52              1 28-JAN-08    28-JAN-08     25-FEB-09
     * </pre>
     */
    @Test
    @Transactional
    @DirtiesContext
    @Rollback(true)
    public final void testDeleteObservations() {
        Map<Integer, Observation> obs = service.getObservations(64129);
        assertTrue("expected 3 obs or more [" + obs.size() + "]", obs.size() >= 3);
        service.deleteObservations(obs.values());
        obs = service.getObservations(64129);
        assertEquals("expected no remaining obs after delete", 0, obs.size());
    }

    @Test
    @Transactional
    @DirtiesContext
    @Rollback(true)
    public final void testDeleteObservationsIntIntInt() {
        // ID1=1026, ID2=64810-64814 (5 obs, 13 facts, 1 flag)
        int stationId = 1026, begin = 64810, end = 64814;
        String obQ = "select ID2 from TABLE1 " + "where ID1=? and ID2 between ? and ?";
        String factQ = "select ID2, ID3 from TABLE2 "
                + "where ID1=? and ID2 between ? and ?";
        String flagQ = "select ID2, ID3 from TABLE3 "
                + "where ID1=? and ID2 between ? and ?";
        String obloadQ = "select ID2 from TABLE4 "
                + "where ID1=? and ID2 between ? and ?";

        // first prove the obs to be deleted exist
        Map<Integer, Observation> obs = service.getObservations(begin, end, stationId);
        assertEquals("should have 5 obs", 5, obs.size());

        // show number rows in each table
        List<Map<String, Object>> oblist = jdbcTemplate.queryForList(obQ, stationId, begin, end);
        assertEquals("wrong # obs", 5, oblist.size());
        List<Map<String, Object>> factlist = jdbcTemplate.queryForList(factQ, stationId, begin, end);
        assertEquals("wrong # facts", 13, factlist.size());
        List<Map<String, Object>> flaglist = jdbcTemplate.queryForList(flagQ, stationId, begin, end);
        assertEquals("wrong # flags", 1, flaglist.size());
        List<Map<String, Object>> loadlist = jdbcTemplate.queryForList(obloadQ, stationId, begin, end);
        assertEquals("wrong # ob load log", 5, loadlist.size());

        // now delete them
        service.deleteObservations(stationId, begin, end);

        // show the cache is cleared
        obs = service.getObservations(begin, end, stationId);
        assertEquals("should have deleted all obs", 0, obs.size());

        // show data deleted from all tables
        oblist = jdbcTemplate.queryForList(obQ, stationId, begin, end);
        assertEquals("wrong # obs", 0, oblist.size());
        factlist = jdbcTemplate.queryForList(factQ, stationId, begin, end);
        assertEquals("wrong # facts", 0, factlist.size());
        flaglist = jdbcTemplate.queryForList(flagQ, stationId, begin, end);
        assertEquals("wrong # flags", 0, flaglist.size());
        loadlist = jdbcTemplate.queryForList(obloadQ, stationId, begin, end);
        assertEquals("wrong # ob load log", 0, loadlist.size());
    }

    private Observation fakeOb(int datetimeId, int stationId) {
        return new Observation(stationId, datetimeId, 6, 1, "fakeofile.txt", 152);
    }

    /**
     * Checks all possible tables containing ob info for evidence of an ob's trace in the database; uses jdbcTemplate to
     * bypass dao caching Note that this may seem like overkill but helps to prove transactionality
     * @param datetimeId The datetimeId of the observation
     * @param stationId The stationId of the observation
     * @param template contains the JDBC Connection
     * @return true if any table contains data for the provided station/datetime
     */
    private boolean obRemnantsExistJdbc(int datetimeId, int stationId, JdbcTemplate template) {
        if (obExistsJdbc(datetimeId, stationId, template)) {
            if (LOG.isTraceEnabled())
                LOG.trace("ob in TABLE1");
            return true;
        } else if (obLoadlogExistsJdbc(datetimeId, stationId, template)) {
            if (LOG.isTraceEnabled())
                LOG.trace("ob in TABLE2");
            return true;
        } else if (obFactsExistJdbc(datetimeId, stationId, template)) {
            if (LOG.isTraceEnabled())
                LOG.trace("ob in TABLE3");
            return true;
        } else if (obFlagsExistJdbc(datetimeId, stationId, template)) {
            if (LOG.isTraceEnabled())
                LOG.trace("ob in TABLE4");
            return true;
        } else {
            if (LOG.isTraceEnabled())
                LOG.trace("ob remnants not found");
            return false;
        }

    }

    @Transactional
    @Rollback(false)
    private void deleteRemnantsJdbc(int datetimeId, int stationId, JdbcTemplate template) {
        String deleteOb = "delete from TABLE1 where ID1=? and ID2=?";
        template.update(deleteOb, datetimeId, stationId);
        deleteOb = "delete from TABLE2 where ID1=? and ID2=?";
        template.update(deleteOb, datetimeId, stationId);
        deleteOb = "delete from TABLE3 where ID1=? and ID2=?";
        template.update(deleteOb, datetimeId, stationId);
        deleteOb = "delete from TABLE4 where ID1=? and ID2=?";
        template.update(deleteOb, datetimeId, stationId);
        Connection conn;
        try {
            conn = template.getDataSource().getConnection();
        } catch (SQLException e) {
            throw translator.translate("SQLException thrown on dataSource.getConnection()", null, e);
        }
        try {
            conn.commit();
        } catch (SQLException e) {
            throw translator.translate("SQLException thrown on commit", null, e);
        }
    }

    /**
     * Tests whether an observation is in the observation table only; uses jdbcTemplate to bypass dao caching
     * @param datetimeId The datetimeId of the observation
     * @param stationId The stationId of the observation
     * @param template contains the JDBC Connection
     * @return true if an observation is in the observation table
     */
    private boolean obExistsJdbc(int datetimeId, int stationId, JdbcTemplate template) {
        String selectOb = "select * from TABLE1 where ID1=? and ID2=?";
        SqlRowSet rs = template.queryForRowSet(selectOb, datetimeId, stationId);
        return rs.next();
    }

    /**
     * Tests whether an observation is in the obloadlog table only; uses jdbcTemplate to bypass dao caching
     * @param datetimeId The datetimeId of the observation
     * @param stationId The stationId of the observation
     * @param template contains the JDBC Connection
     * @return true if an observation is in the obloadlog table
     */
    private boolean obLoadlogExistsJdbc(int datetimeId, int stationId, JdbcTemplate template) {
        String selectOb = "select * from TABLE2 where ID1=? and ID2=?";
        SqlRowSet rs = template.queryForRowSet(selectOb, datetimeId, stationId);
        return rs.next();
    }

    /**
     * Tests whether an observation is in the fact table only; uses jdbcTemplate to bypass dao caching
     * @param datetimeId The datetimeId of the observation
     * @param stationId The stationId of the observation
     * @param template contains the JDBC Connection
     * @return true if an observation is in the fact table
     */
    private boolean obFactsExistJdbc(int datetimeId, int stationId, JdbcTemplate template) {
        String selectOb = "select * from TABLE3 where ID1=? and ID2=?";
        SqlRowSet rs = template.queryForRowSet(selectOb, datetimeId, stationId);
        return rs.next();
    }

    /**
     * Tests whether certain facts are in the fact table; uses jdbcTemplate to bypass dao caching
     * @param template contains the JDBC Connection
     * @param datetimeId The datetimeId of the observation
     * @param stationId The stationId of the observation
     * @param elementIds The elementids of the facts
     * @return true if an observation is in the fact table
     */
    private boolean obFactsExistJdbc(JdbcTemplate template, int datetimeId, int stationId, Integer... elementIds) {
        String ids = StringUtils.arrayToDelimitedString(elementIds, ",");
        String selectOb = "select * from TABLE3 where ID1=? and ID2=? and ID3 in (" + ids + ")";
        SqlRowSet rs = template.queryForRowSet(selectOb, datetimeId, stationId);
        return rs.next();
    }

    private void deleteFactRemnantsJdbc(JdbcTemplate template, int datetimeId, int stationId, Integer[] elementIds) {
        String ids = StringUtils.arrayToDelimitedString(elementIds, ",");
        String deleteOb = "delete from TABLE3 where ID1=? and ID2=? and ID3 in (" + ids + ")";
        template.update(deleteOb, datetimeId, stationId);
        Connection conn;
        try {
            conn = template.getDataSource().getConnection();
        } catch (SQLException e) {
            throw translator.translate("SQLException thrown on dataSource.getConnection()", null, e);
        }
        try {
            conn.commit();
        } catch (SQLException e) {
            throw translator.translate("SQLException thrown on commit", null, e);
        }
    }

    /**
     * Updates this observation to have the last_mod and isd_export equal initial_load
     */
    private void updateTestObDates(JdbcTemplate template, int datetimeId, int stationId) {
        String updateOb = "update TABLE3 "
                + " set COL1=COL3, COL2=COL3"
                + " where ID1=? and ID2=?";
        template.update(updateOb, datetimeId, stationId);
        Connection conn;
        try {
            conn = template.getDataSource().getConnection();
        } catch (SQLException e) {
            throw translator.translate("SQLException thrown on dataSource.getConnection()", null, e);
        }
        try {
            conn.commit();
        } catch (SQLException e) {
            throw translator.translate("SQLException thrown on commit", null, e);
        }
    }

    /**
     * Tests whether an observation is in the flag table only; uses jdbcTemplate to bypass dao caching
     * @param datetimeId The datetimeId of the observation
     * @param stationId The stationId of the observation
     * @param template contains the JDBC Connection
     * @return true if an observation is in the flag table
     */
    private boolean obFlagsExistJdbc(int datetimeId, int stationId, JdbcTemplate template) {
        String selectOb = "select * from TABLE4 where ID1=? and ID2=?";
        SqlRowSet rs = template.queryForRowSet(selectOb, datetimeId, stationId);
        return rs.next();
    }

    @Test
    public final void testGetObservation() {
        Observation ob = null;
        int dt = 63500;
        int st = 1326;
        ob = service.getObservation(dt, st);
        assertNotNull("Retrieved null ob.", ob);

        int retrievedDtId = ob.getDatetimeId();
        int retrievedStId = ob.getStationId();

        assertEquals("Datetime IDs don't match: " + dt + " != " + retrievedDtId, dt, retrievedDtId);
        assertEquals("Station IDs don't match: " + st + " != " + retrievedStId, st, retrievedStId);

        dt = 83100;
        st = 1467;
        ob = service.getObservation(dt, st);
        assertNotNull("Retrieved null ob.", ob);
    }

    /**
     * <pre>
     *     ---------- ---------------------- ---------------------- ----------
     *     1326       130                    63500                  1157
     *     1326       439                    63500                  0.2
     *     1326       440                    63500                  1.2
     * </pre>
     */
    @Test
    public final void testGetObservationWithData() {
        ObservationWithData ob = null;
        int dt = 63500;
        int st = 1326;
        ob = service.getObservationWithData(dt, st);
        assertNotNull("Retrieved null ob.", ob);

        int retrievedDtId = ob.getObservation().getDatetimeId();
        int retrievedStId = ob.getObservation().getStationId();

        assertEquals("Datetime IDs don't match: " + dt + " != " + retrievedDtId, dt, retrievedDtId);
        assertEquals("Station IDs don't match: " + st + " != " + retrievedStId, st, retrievedStId);
        ElementValue result = ob.getElementValue(130);
        assertEquals(130, result.getElementId());
        assertEquals(dt, result.getDatetimeId());
        assertEquals(st, result.getStationId());
        assertEquals(new BigDecimal("1157"), result.getValue());
        result = ob.getElementValue(439);
        assertEquals(439, result.getElementId());
        assertEquals(dt, result.getDatetimeId());
        assertEquals(st, result.getStationId());
        BigDecimal expected = new BigDecimal(".2");
        assertEquals(expected, result.getValue());
        result = ob.getElementValue(440);
        assertEquals(440, result.getElementId());
        assertEquals(dt, result.getDatetimeId());
        assertEquals(st, result.getStationId());
        expected = new BigDecimal("1.2");
        assertEquals(expected, result.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGetObservationWithDataEndBeforeBegin() {
        int begin = 100010;
        service.getObservationsWithData(begin, begin - 10, 1234);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGetObservationWithDataTooManyHours() {
        int begin = 100010;
        // MAX allowed is ~8800
        service.getObservationsWithData(begin, begin + 10000, 1234);
    }

    /**
     * <pre>
     *           ---------- ---------- ----------- ----- ----
     *           1026       439        63000       4.4   0
     *           1026       439        63001       4.1   0
     *           1026       439        63002       3.7   0
     * </pre>
     */
    @Test
    public final void testGetObservationsWithData() {
        int stationId = 1026;
        List<ObservationWithData> obList = service.getObservationsWithData(63000, 63002, stationId);

        ObservationWithData result = obList.get(0);
        assertNotNull("didn't get datetime 63000", result);
        assertEquals("wrong station", stationId, result.getObservation().getStationId());
        assertNotNull("didn't get any values", result.getElementValues());
        BigDecimal expected = new BigDecimal("4.4");
        assertEquals("wrong value", expected, result.getElementValue(439).getValue());

        result = obList.get(1);
        assertNotNull("didn't get datetime 63001", result);
        assertEquals("wrong station", stationId, result.getObservation().getStationId());
        assertNotNull("didn't get any values", result.getElementValues());
        expected = new BigDecimal("4.1");
        assertEquals("wrong value", expected, result.getElementValue(439).getValue());

        result = obList.get(2);
        assertNotNull("didn't get datetime 63002", result);
        assertEquals("wrong station", stationId, result.getObservation().getStationId());
        assertNotNull("didn't get any values", result.getElementValues());
        expected = new BigDecimal("3.7");
        assertEquals("wrong value", expected, result.getElementValue(439).getValue());
    }

    @Test
    public final void testGetObservationsIntCollectionOfInteger() {
        Map<Integer, Observation> obs = null;
        int dt = 63500;
        // 123 retrieves no results
        List<Integer> stationIds = Arrays.asList(1326, 1009, 123);
        obs = service.getObservations(dt, stationIds);
        assertEquals("size retrieved should be 2", 2, obs.size());
        for (int stationId : stationIds) {
            if (stationId != 123) {
                Observation ob = obs.get(stationId);
                assertNotNull("No ob for stationId=" + stationId, ob);
                assertEquals("DatetimeId doesn't match for stationId=" + stationId, dt, ob.getDatetimeId());
                assertEquals("StationId doesn't match for stationId=" + stationId, stationId, ob.getStationId());
            }
        }
    }

    @Test
    public final void testGetObservationsInt() {
        Map<Integer, Observation> obs = null;
        int dt = 63500;
        int[] stationIds = { 1326, 1009, 1026 };
        obs = service.getObservations(dt);
        for (int stationId : stationIds) {
            Observation ob = obs.get(stationId);
            assertNotNull("No ob for stationId=" + stationId, ob);
            assertEquals("DatetimeId doesn't match for stationId=" + stationId, dt, ob.getDatetimeId());
            assertEquals("StationId doesn't match for stationId=" + stationId, stationId, ob.getStationId());
        }
    }

    @Test
    public final void testGetObservationsIntIntInt() {
        // get one hour's worth of data
        int beginDatetimeId = TimeUtils.computeDateTimeId(TimeUtils.createUTCCalendar("2008050602"));
        int endDatetimeId = TimeUtils.computeDateTimeId(TimeUtils.createUTCCalendar("2008050602"));
        Map<Integer, Observation> obs = service.getObservations(beginDatetimeId, endDatetimeId, 1026);
        assertNotNull(obs);
        assertEquals(1, obs.size());

        // get two hour's worth of data
        beginDatetimeId = TimeUtils.computeDateTimeId(TimeUtils.createUTCCalendar("2008050602"));
        endDatetimeId = TimeUtils.computeDateTimeId(TimeUtils.createUTCCalendar("2008050603"));
        obs = service.getObservations(beginDatetimeId, endDatetimeId, 1026);
        assertEquals(2, obs.size());

        // get one month's worth of data
        beginDatetimeId = TimeUtils.computeDateTimeId(TimeUtils.createUTCCalendar("2008050600"));
        endDatetimeId = TimeUtils.computeDateTimeId(TimeUtils.createUTCCalendar("2008060523"));
        obs = service.getObservations(beginDatetimeId, endDatetimeId, 1026);
        assertTrue(obs.size() > (30 * 24));

        // get one year's worth of data
        beginDatetimeId = TimeUtils.computeDateTimeId(TimeUtils.createUTCCalendar("2008010100"));
        endDatetimeId = TimeUtils.computeDateTimeId(TimeUtils.createUTCCalendar("2008123123"));
        obs = service.getObservations(beginDatetimeId, endDatetimeId, 1026);
        assertTrue(obs.size() > (360 * 24));
    }

    @Test
    public final void testGetCurrentObservations() {
        Map<Integer, Observation> obs = service.getCurrentObservations();
        assertTrue("didn't get enough obs (got " + obs.size(), obs.size() > 150);
        Observation ob = obs.get(1036);
        assertNotNull("didn't get station 1036", ob);
        assertEquals("didn't get datetime from por table", 78918, ob.getDatetimeId());
    }

    @Test
    public final void testGetCurrentObservationsCollectionOfInteger() {
        Integer[] stationIds = { 1143, 1550, 1652 };
        Map<Integer, Observation> obs = service.getCurrentObservations(Arrays.asList(stationIds));
        assertEquals("expected 3 current obs", 3, obs.size());
        for (int id : stationIds) {
            assertNotNull("didn't get station " + id, obs.get(id));
        }
        // test id=null
        stationIds[1] = null;
        obs = service.getCurrentObservations(Arrays.asList(stationIds));
        assertEquals("expected 3 current obs", 2, obs.size());
    }

    @Test
    public final void testGetCurrentObservation() {
        Integer[] stationIds = { 1143, 1550, 1652 };
        for (int id : stationIds) {
            assertNotNull("didn't get station " + id, service.getCurrentObservation(id));
        }
        // test stationId not in list
        assertNull("didn't expect ob for stationId=999", service.getCurrentObservation(999));
    }

    @Test
    public final void testGetCurrentObservationsForNetworks() {
        int net1sta = 1143, net2sta = 1550, net3sta = 1652;
        int net1MinExpected = 125, net2MinExpected = 17, net3MinExpected = 10;
        int net1Actual, net2Actual, net3Actual;
        Map<Integer, Observation> allObs = service.getCurrentObservations();
        Map<Integer, Observation> obs = service.getCurrentObservationsForNetworks(1, 2, 3);
        assertEquals("expected same size since requesting all networks", allObs.size(), obs.size());
        assertNotNull("didn't get station " + net1sta, obs.get(net1sta));
        assertNotNull("didn't get station " + net2sta, obs.get(net2sta));
        assertNotNull("didn't get station " + net3sta, obs.get(net3sta));

        obs = service.getCurrentObservationsForNetworks(1);
        net1Actual = obs.size();
        assertTrue("expected min of " + net1MinExpected + " in network 1, actual=" + obs.size(),
                net1MinExpected <= obs.size());
        assertNotNull("didn't get station " + net1sta, obs.get(net1sta));
        assertNull("didn't expect network 2 station " + net2sta, obs.get(net2sta));
        assertNull("didn't expect network 3 station " + net3sta, obs.get(net3sta));

        obs = service.getCurrentObservationsForNetworks(2);
        net2Actual = obs.size();
        assertTrue("expected min of " + net2MinExpected + " in network 2", net2MinExpected <= obs.size());
        assertNull("didn't expect network 1 station " + net1sta, obs.get(net1sta));
        assertNotNull("didn't get station " + net2sta, obs.get(net2sta));
        assertNull("didn't expect network 3 station " + net3sta, obs.get(net3sta));

        obs = service.getCurrentObservationsForNetworks(3);
        assertTrue("expected min of " + net3MinExpected + " in network 3", net3MinExpected <= obs.size());
        net3Actual = obs.size();
        assertNull("didn't expect network 1 station " + net1sta, obs.get(net1sta));
        assertNull("didn't expect network 2 station " + net2sta, obs.get(net2sta));
        assertNotNull("didn't get station " + net3sta, obs.get(net3sta));

        obs = service.getCurrentObservationsForNetworks(2, 3);
        assertTrue("expected min of " + (net2MinExpected + net3MinExpected) + " in networks 2 & 3",
                (net2MinExpected + net3MinExpected) <= obs.size());
        assertNull("didn't expect network 1 station " + net1sta, obs.get(net1sta));
        assertNotNull("didn't get station " + net2sta, obs.get(net2sta));
        assertNotNull("didn't get station " + net3sta, obs.get(net3sta));

        // check totals sum properly
        assertEquals("expected sums of all 3 networks individually to equal allobs", allObs.size(), (net1Actual
                + net2Actual + net3Actual));
        assertTrue("expected min of " + (net1MinExpected + net2MinExpected + net3MinExpected)
                + " in networks 1 & 2 & 3", (net1MinExpected + net2MinExpected + net3MinExpected) <= allObs.size());
    }

    @Test
    public final void testGetStepsPerHour() {
        int stationId = 1009;
        int datetimeId = 63000;
        Integer expected = 12;
        // Integer steps = service.getStepsPerHour(stationId, datetimeId);
        Integer steps = service.getStepsPerHour(datetimeId, stationId);
        assertNotNull("no steps for station[" + stationId + "]+datetime[" + datetimeId, steps);
        assertEquals("wrong frequency for station[" + stationId + "]+datetime[" + datetimeId, expected, steps);
        stationId = -1;
        datetimeId = 63000;
        steps = service.getStepsPerHour(datetimeId, stationId);
        assertNull("no observation expected for station[" + stationId + "]+datetime[" + datetimeId, steps);

    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGetObservationsIllegalQuantity() {
        // tests more than a year's obs requestsd
        int beginDatetimeId = TimeUtils.computeDateTimeId(TimeUtils.createUTCCalendar("2008010100"));
        int endDatetimeId = TimeUtils.computeDateTimeId(TimeUtils.createUTCCalendar("2009011023"));
        // throws exception because of illegal range
        service.getObservations(beginDatetimeId, endDatetimeId, 1026);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGetObservationsIllegalRange() {
        // tests end date less than beginning date
        int beginDatetimeId = TimeUtils.computeDateTimeId(TimeUtils.createUTCCalendar("2008020100"));
        int endDatetimeId = TimeUtils.computeDateTimeId(TimeUtils.createUTCCalendar("2008010223"));
        // throws exception because of illegal range
        service.getObservations(beginDatetimeId, endDatetimeId, 1026);
    }

    @Test
    @Transactional
    @Rollback(true)
    @DirtiesContext
    public final void testAddCalculatedValuesToObs1Map() {

        // test verifies insert fails if one succeeds but another doesn't
        // test verifies adding values when the ob doesn't exist fails
        // test verifies adding values for elements that are defined in the
        // stream_element table (therefore not calculated) fails
        // test verifies adding more than one set of legitimate updates succeeds

        Map<Observation, Collection<ElementValue>> updates = new HashMap<>();

        /*
         * Create a valid set of element values for insert. This is used several times paired with inserts expected to
         * fail and then an insert expected to succeed.
         */
        // get existing observationwithdata
        int datetimeId = datetimeIdforInsCalc;
        int stationId1 = stationIdforInsCalc;
        // stream 52
        ObservationWithData startob1 = service.getObservationWithData(datetimeId, stationId1);
        assertNotNull(startob1);
        assertNull("already contains 331", startob1.getElementValues().get(331));
        assertNull("already contains 332", startob1.getElementValues().get(332));
        assertNull("already contains 333", startob1.getElementValues().get(333));
        // calculate some values - valid calculated elements that are not already there (T5_1,T5_2,T5_3)
        Set<ElementValue> inserts1 = new HashSet<>();
        inserts1.add(startob1.addNewElementValue(331, new BigDecimal("4.1")));
        inserts1.add(startob1.addNewElementValue(332, new BigDecimal("4.1")));
        inserts1.add(startob1.addNewElementValue(333, new BigDecimal("4.2")));

        /*
         * TEST 1 Insert updates for two obs, one legitimate and one with observed values rather than calculated values.
         * Expect exception.
         */
        int stationId2 = 1009;
        // stream 2
        ObservationWithData startob2 = service.getObservationWithData(datetimeId, stationId2);
        assertNotNull(startob2);
        // calculate some values: *invalid* because not a *calculated* value,
        // even though not in database already (note this shouldn't happen, but
        // still it should be prevented)
        Set<ElementValue> inserts2 = new HashSet<>();
        inserts2.add(startob2.addNewElementValue(303, new BigDecimal("4.1")));

        // update observation, expect exception
        updates.put(startob1.getObservation(), inserts1);
        updates.put(startob2.getObservation(), inserts2);
        try {
            service.addCalculatedValuesToObs(updates);
            fail("expected exception on inserting observed value");
        } catch (Exception e) {
        }

    }

    @Test
    @Transactional
    @Rollback(true)
    @DirtiesContext
    public final void testAddCalculatedValuesToObs1Collection() {

        // test verifies insert fails if one succeeds but another doesn't
        // test verifies adding values when the ob doesn't exist fails
        // test verifies adding values for elements that are defined in the
        // stream_element table (therefore not calculated) fails
        // test verifies adding more than one set of legitimate updates succeeds

        /*
         * Create a valid set of element values for insert. This is used several times paired with inserts expected to
         * fail and then an insert expected to succeed.
         */
        // get existing observationwithdata
        int datetimeId = datetimeIdforInsCalc;
        int stationId1 = stationIdforInsCalc;
        // stream 52
        ObservationWithData startob1 = service.getObservationWithData(datetimeId, stationId1);
        assertNotNull(startob1);
        assertNull("already contains 331", startob1.getElementValues().get(331));
        assertNull("already contains 332", startob1.getElementValues().get(332));
        assertNull("already contains 333", startob1.getElementValues().get(333));
        // calculate some values - valid calculated elements that are not already there (T5_1,T5_2,T5_3)
        Set<ElementValue> inserts1 = new HashSet<>();
        inserts1.add(startob1.addNewElementValue(331, new BigDecimal("4.1")));
        inserts1.add(startob1.addNewElementValue(332, new BigDecimal("4.1")));
        inserts1.add(startob1.addNewElementValue(333, new BigDecimal("4.2")));

        /*
         * TEST 1 Insert updates for two obs, one legitimate and one with observed values rather than calculated values.
         * Expect exception.
         */
        int stationId2 = 1009;
        // stream 2
        ObservationWithData startob2 = service.getObservationWithData(datetimeId, stationId2);
        assertNotNull(startob2);
        // calculate some values: *invalid* because not a *calculated* value,
        // even though not in database already (note this shouldn't happen, but
        // still it should be prevented)
        Set<ElementValue> inserts2 = new HashSet<>();
        inserts2.add(startob2.addNewElementValue(303, new BigDecimal("4.1")));

        // update observation, expect exception
        Collection<ElementValue> combinedInserts = Lists.newArrayList(inserts1);
        combinedInserts.addAll(inserts2);
        try {
            service.addCalculatedValuesToObs(combinedInserts);
            fail("expected exception on inserting observed value");
        } catch (Exception e) {
        }

    }

    @Test
    @Transactional
    @Rollback(true)
    @DirtiesContext
    public final void testAddCalculatedValuesToObs2Map() {

        // test verifies insert fails if one succeeds but another doesn't
        // test verifies adding values when the ob doesn't exist fails
        // test verifies adding values for elements that are defined in the
        // stream_element table (therefore not calculated) fails
        // test verifies adding more than one set of legitimate updates succeeds

        Map<Observation, Collection<ElementValue>> updates = new HashMap<>();

        /*
         * Create a valid set of element values for insert. This is used several times paired with inserts expected to
         * fail and then an insert expected to succeed.
         */
        // get existing observationwithdata
        int datetimeId = datetimeIdforInsCalc;
        int stationId1 = stationIdforInsCalc;
        // stream 52
        ObservationWithData startob1 = service.getObservationWithData(datetimeId, stationId1);
        assertNotNull(startob1);
        assertNull("already contains 331", startob1.getElementValues().get(331));
        assertNull("already contains 332", startob1.getElementValues().get(332));
        assertNull("already contains 333", startob1.getElementValues().get(333));
        // calculate some values - valid calculated elements that are not already there (T5_1,T5_2,T5_3)
        Set<ElementValue> inserts1 = new HashSet<>();
        inserts1.add(startob1.addNewElementValue(331, new BigDecimal("4.1")));
        inserts1.add(startob1.addNewElementValue(332, new BigDecimal("4.1")));
        inserts1.add(startob1.addNewElementValue(333, new BigDecimal("4.2")));

        /*
         * TEST 2 Insert updates for two obs, one legitimate and one with calculated values already in the database.
         * Expect exception.
         */
        int stationId3 = 1026;
        // stream 4
        ObservationWithData startob3 = service.getObservationWithData(datetimeId, stationId3);
        assertNotNull(startob3);
        assertNotNull("should already contain 439", startob3.getElementValues().get(439));
        // calculate some values: *invalid* because it is in database already
        Set<ElementValue> inserts3 = new HashSet<>();
        inserts3.add(startob3.addNewElementValue(439, new BigDecimal("-5.3")));

        // update observation, expect exception
        // inserts1 are all legitimate inserts
        updates.put(startob1.getObservation(), inserts1);
        updates.put(startob3.getObservation(), inserts3);
        try {
            service.addCalculatedValuesToObs(updates);
            // TODO this should not be necessary
            startob3 = service.getObservationWithData(datetimeId, stationId3);
            fail("expected exception on inserting value already in database");
        } catch (Exception e) {
        }

    }

    @Test
    @Transactional
    @Rollback(true)
    @DirtiesContext
    public final void testAddCalculatedValuesToObs2Collection() {

        // test verifies insert fails if one succeeds but another doesn't
        // test verifies adding values when the ob doesn't exist fails
        // test verifies adding values for elements that are defined in the
        // stream_element table (therefore not calculated) fails
        // test verifies adding more than one set of legitimate updates succeeds

        /*
         * Create a valid set of element values for insert. This is used several times paired with inserts expected to
         * fail and then an insert expected to succeed.
         */
        // get existing observationwithdata
        int datetimeId = datetimeIdforInsCalc;
        int stationId1 = stationIdforInsCalc;
        // stream 52
        ObservationWithData startob1 = service.getObservationWithData(datetimeId, stationId1);
        assertNotNull(startob1);
        assertNull("already contains 331", startob1.getElementValues().get(331));
        assertNull("already contains 332", startob1.getElementValues().get(332));
        assertNull("already contains 333", startob1.getElementValues().get(333));
        // calculate some values - valid calculated elements that are not already there (T5_1,T5_2,T5_3)
        Set<ElementValue> inserts1 = new HashSet<>();
        inserts1.add(startob1.addNewElementValue(331, new BigDecimal("4.1")));
        inserts1.add(startob1.addNewElementValue(332, new BigDecimal("4.1")));
        inserts1.add(startob1.addNewElementValue(333, new BigDecimal("4.2")));

        /*
         * TEST 2 Insert updates for two obs, one legitimate and one with calculated values already in the database.
         * Expect exception.
         */
        int stationId3 = 1026;
        // stream 4
        ObservationWithData startob3 = service.getObservationWithData(datetimeId, stationId3);
        assertNotNull(startob3);
        assertNotNull("should already contain 439", startob3.getElementValues().get(439));
        // calculate some values: *invalid* because it is in database already
        Set<ElementValue> inserts3 = new HashSet<>();
        inserts3.add(startob3.addNewElementValue(439, new BigDecimal("-5.3")));

        // update observation, expect exception
        // inserts1 are all legitimate inserts
        Collection<ElementValue> combinedInserts = Lists.newArrayList(inserts1);
        combinedInserts.addAll(inserts3);
        try {
            service.addCalculatedValuesToObs(combinedInserts);
            // TODO this should not be necessary
            startob3 = service.getObservationWithData(datetimeId, stationId3);
            fail("expected exception on inserting value already in database");
        } catch (Exception e) {
        }

    }

    @Test
    @Transactional
    @Rollback(true)
    @DirtiesContext
    public final void testAddCalculatedValuesToObs3Map() {

        // test verifies insert fails if one succeeds but another doesn't
        // test verifies adding values when the ob doesn't exist fails
        // test verifies adding values for elements that are defined in the
        // stream_element table (therefore not calculated) fails
        // test verifies adding more than one set of legitimate updates succeeds

        Map<Observation, Collection<ElementValue>> updates = new HashMap<>();

        /*
         * Create a valid set of element values for insert. This is used several times paired with inserts expected to
         * fail and then an insert expected to succeed.
         */
        // get existing observationwithdata
        int datetimeId = datetimeIdforInsCalc;
        int stationId1 = stationIdforInsCalc;
        // stream 52
        ObservationWithData startob1 = service.getObservationWithData(datetimeId, stationId1);
        assertNotNull(startob1);
        assertNull("already contains 331", startob1.getElementValues().get(331));
        assertNull("already contains 332", startob1.getElementValues().get(332));
        assertNull("already contains 333", startob1.getElementValues().get(333));
        // calculate some values - valid calculated elements that are not already there (T5_1,T5_2,T5_3)
        Set<ElementValue> inserts1 = new HashSet<>();
        inserts1.add(startob1.addNewElementValue(331, new BigDecimal("4.1")));
        inserts1.add(startob1.addNewElementValue(332, new BigDecimal("4.1")));
        inserts1.add(startob1.addNewElementValue(333, new BigDecimal("4.2")));

        /*
         * TEST 3 Insert updates for two obs, one legitimate and one where the observation is not in the database.
         * Expect exception.
         */
        updates.clear();
        int stationId4 = 1009;
        int datetimeIdFake = 98630;
        // try to insert calculated values where there is not an observation in
        // the database
        ObservationWithData nope = service.getObservationWithData(datetimeIdFake, stationId4);
        assertNull(nope);
        Observation fakeOb = new Observation(datetimeIdFake, stationId4, 4, 1, "fakefile.txt", 123);
        // calculate some values: *invalid* because ob is not in database already
        Set<ElementValue> inserts4 = new HashSet<>();
        inserts4.add(new ElementValue(stationId4, datetimeIdFake, 439, new BigDecimal("-5.3"), 1, 1));
        // update observation, expect exception [WHY is exception expected; where's the check - db or app?]
        updates.put(startob1.getObservation(), inserts1);
        updates.put(fakeOb, inserts4);
        try {
            service.addCalculatedValuesToObs(updates);
            fail("expected exception on inserting value when ob is not in db");
        } catch (Exception e) {
        }
    }

    @Test
    @Transactional
    @Rollback(true)
    @DirtiesContext
    public final void testAddCalculatedValuesToObs3Collection() {

        // test verifies insert fails if one succeeds but another doesn't
        // test verifies adding values when the ob doesn't exist fails
        // test verifies adding values for elements that are defined in the
        // stream_element table (therefore not calculated) fails
        // test verifies adding more than one set of legitimate updates succeeds

        /*
         * Create a valid set of element values for insert. This is used several times paired with inserts expected to
         * fail and then an insert expected to succeed.
         */
        // get existing observationwithdata
        int datetimeId = datetimeIdforInsCalc;
        int stationId1 = stationIdforInsCalc;
        // stream 52
        ObservationWithData startob1 = service.getObservationWithData(datetimeId, stationId1);
        assertNotNull(startob1);
        assertNull("already contains 331", startob1.getElementValues().get(331));
        assertNull("already contains 332", startob1.getElementValues().get(332));
        assertNull("already contains 333", startob1.getElementValues().get(333));
        // calculate some values - valid calculated elements that are not already there (T5_1,T5_2,T5_3)
        Set<ElementValue> inserts1 = new HashSet<>();
        inserts1.add(startob1.addNewElementValue(331, new BigDecimal("4.1")));
        inserts1.add(startob1.addNewElementValue(332, new BigDecimal("4.1")));
        inserts1.add(startob1.addNewElementValue(333, new BigDecimal("4.2")));

        /*
         * TEST 3 Insert updates for two obs, one legitimate and one where the observation is not in the database.
         * Expect exception.
         */
        int stationId4 = 1009;
        int datetimeIdFake = 98630;
        // try to insert calculated values where there is not an observation in
        // the database
        ObservationWithData nope = service.getObservationWithData(datetimeIdFake, stationId4);
        assertNull(nope);
        // calculate some values: *invalid* because ob is not in database already
        Set<ElementValue> inserts4 = new HashSet<>();
        inserts4.add(new ElementValue(stationId4, datetimeIdFake, 439, new BigDecimal("-5.3"), 1, 1));
        // update observation, expect exception [WHY is exception expected; where's the check - db or app?]
        Collection<ElementValue> combinedInserts = Lists.newArrayList(inserts1);
        combinedInserts.addAll(inserts4);
        try {
            service.addCalculatedValuesToObs(combinedInserts);
            fail("expected exception on inserting value when ob is not in db");
        } catch (Exception e) {
        }
    }

    @Test
    @Transactional
    @Rollback(true)
    @DirtiesContext
    public final void testAddCalculatedValuesToObs4Map() {
        // test verifies insert fails if one succeeds but another doesn't
        // test verifies adding values when the ob doesn't exist fails
        // test verifies adding values for elements that are defined in the
        // stream_element table (therefore not calculated) fails
        // test verifies adding more than one set of legitimate updates succeeds

        Map<Observation, Collection<ElementValue>> updates = new HashMap<>();

        /*
         * Create a valid set of element values for insert. This is used several times paired with inserts expected to
         * fail and then an insert expected to succeed.
         */
        // get existing observationwithdata
        int datetimeId = datetimeIdforInsCalc;
        int stationId1 = stationIdforInsCalc;
        // stream 52
        ObservationWithData startob1 = service.getObservationWithData(datetimeId, stationId1);
        assertNotNull(startob1);
        assertNull("already contains 331", startob1.getElementValues().get(331));
        assertNull("already contains 332", startob1.getElementValues().get(332));
        assertNull("already contains 333", startob1.getElementValues().get(333));
        // calculate some values - valid calculated elements that are not already there (T5_1,T5_2,T5_3)
        Set<ElementValue> inserts1 = new HashSet<>();
        inserts1.add(startob1.addNewElementValue(331, new BigDecimal("4.1")));
        inserts1.add(startob1.addNewElementValue(332, new BigDecimal("4.1")));
        inserts1.add(startob1.addNewElementValue(333, new BigDecimal("4.2")));

        /*
         * TEST 4 Insert updates for two obs, both legitimate. Expect success.
         */
        int stationId5 = 1009;
        // stream 2 (note same as startob2, but starting fresh
        ObservationWithData startob5 = service.getObservationWithData(datetimeId, stationId5);
        // calculate some values - valid calculated elements that are not already there (T5_1,T5_2,T5_3)
        Set<ElementValue> inserts5 = new HashSet<>();
        inserts5.add(startob5.addNewElementValue(331, new BigDecimal("4.7")));
        inserts5.add(startob5.addNewElementValue(332, new BigDecimal("4.6")));
        inserts5.add(startob5.addNewElementValue(333, new BigDecimal("4.4")));

        // update observation, expect success
        updates.put(startob1.getObservation(), inserts1);
        updates.put(startob5.getObservation(), inserts5);
        service.addCalculatedValuesToObs(updates);

        // verify new values are in ob startob1 inserts
        ObservationWithData updatedob1 = service.getObservationWithData(startob1.getDatetimeId(),
                startob1.getStationId());
        assertEquals("element values differ", startob1.getElementValues(), updatedob1.getElementValues());
        BigDecimal expected = new BigDecimal("4.1");
        assertEquals("didn't get inserted elem value id=331", expected, updatedob1.getElementValue(331).getValue());
        // verify last modified timestamp updated
        // assertTrue("timestamp not updated",
        // startob1.getObservation().getLastModified().before
        // (updatedob1.getObservation().getLastModified()));

        // verify new values are in ob startob5 inserts
        ObservationWithData updatedob5 = service.getObservationWithData(startob5.getDatetimeId(),
                startob5.getStationId());
        expected = new BigDecimal("4.7");
        assertEquals("didn't get inserted elem value id=331", expected, updatedob5.getElementValue(331).getValue());
        assertEquals("element values differ", startob5.getElementValues(), updatedob5.getElementValues());
        // verify last modified timestamp updated
        // assertTrue("timestamp not updated",
        // startob5.getObservation().getLastModified().before
        // (updatedob5.getObservation().getLastModified()));
    }

    @Test
    @Transactional
    @Rollback(true)
    @DirtiesContext
    public final void testAddCalculatedValuesToObs4Collection() {
        // test verifies insert fails if one succeeds but another doesn't
        // test verifies adding values when the ob doesn't exist fails
        // test verifies adding values for elements that are defined in the
        // stream_element table (therefore not calculated) fails
        // test verifies adding more than one set of legitimate updates succeeds

        /*
         * Create a valid set of element values for insert. This is used several times paired with inserts expected to
         * fail and then an insert expected to succeed.
         */
        // get existing observationwithdata
        int datetimeId = datetimeIdforInsCalc;
        int stationId1 = stationIdforInsCalc;
        // stream 52
        ObservationWithData startob1 = service.getObservationWithData(datetimeId, stationId1);
        assertNotNull(startob1);
        assertNull("already contains 331", startob1.getElementValues().get(331));
        assertNull("already contains 332", startob1.getElementValues().get(332));
        assertNull("already contains 333", startob1.getElementValues().get(333));
        // calculate some values - valid calculated elements that are not already there (T5_1,T5_2,T5_3)
        Set<ElementValue> inserts1 = new HashSet<>();
        inserts1.add(startob1.addNewElementValue(331, new BigDecimal("4.1")));
        inserts1.add(startob1.addNewElementValue(332, new BigDecimal("4.1")));
        inserts1.add(startob1.addNewElementValue(333, new BigDecimal("4.2")));

        /*
         * TEST 4 Insert updates for two obs, both legitimate. Expect success.
         */
        int stationId5 = 1009;
        // stream 2 (note same as startob2, but starting fresh
        ObservationWithData startob5 = service.getObservationWithData(datetimeId, stationId5);
        // calculate some values - valid calculated elements that are not already there (T5_1,T5_2,T5_3)
        Set<ElementValue> inserts5 = new HashSet<>();
        inserts5.add(startob5.addNewElementValue(331, new BigDecimal("4.7")));
        inserts5.add(startob5.addNewElementValue(332, new BigDecimal("4.6")));
        inserts5.add(startob5.addNewElementValue(333, new BigDecimal("4.4")));

        // update observation, expect success
        Collection<ElementValue> combinedInserts = Lists.newArrayList(inserts1);
        combinedInserts.addAll(inserts5);
        service.addCalculatedValuesToObs(combinedInserts);

        // verify new values are in ob startob1 inserts
        ObservationWithData updatedob1 = service.getObservationWithData(startob1.getDatetimeId(),
                startob1.getStationId());
        assertEquals("element values differ", startob1.getElementValues(), updatedob1.getElementValues());
        BigDecimal expected = new BigDecimal("4.1");
        assertEquals("didn't get inserted elem value id=331", expected, updatedob1.getElementValue(331).getValue());
        // verify last modified timestamp updated
        // assertTrue("timestamp not updated",
        // startob1.getObservation().getLastModified().before
        // (updatedob1.getObservation().getLastModified()));

        // verify new values are in ob startob5 inserts
        ObservationWithData updatedob5 = service.getObservationWithData(startob5.getDatetimeId(),
                startob5.getStationId());
        expected = new BigDecimal("4.7");
        assertEquals("didn't get inserted elem value id=331", expected, updatedob5.getElementValue(331).getValue());
        assertEquals("element values differ", startob5.getElementValues(), updatedob5.getElementValues());
        // verify last modified timestamp updated
        // assertTrue("timestamp not updated",
        // startob5.getObservation().getLastModified().before
        // (updatedob5.getObservation().getLastModified()));
    }

    @Rollback(true)
    @DirtiesContext
    @Transactional
    @Test
    public final void testAddCalculatedValuesTransactionality() {
        /*
         * Create a valid set of element values for insert. This is used several times paired with inserts expected to
         * fail and then an insert expected to succeed.
         */
        // get existing observationwithdata
        int datetimeId = 63500;
        int stationId = 1326;
        // stream 52
        ObservationWithData startob1 = service.getObservationWithData(datetimeId, stationId);
        assertNotNull(startob1);
        assertNull("already contains 331", startob1.getElementValues().get(331));
        assertNull("already contains 332", startob1.getElementValues().get(332));
        assertNull("already contains 333", startob1.getElementValues().get(333));
        // calculate some values - valid calculated elements that are not
        // already there (T5_1,T5_2,T5_3)
        Set<ElementValue> inserts = new HashSet<>();
        inserts.add(startob1.addNewElementValue(331, new BigDecimal("4.9")));
        inserts.add(startob1.addNewElementValue(332, new BigDecimal("4.8")));
        inserts.add(startob1.addNewElementValue(333, new BigDecimal("4.7")));

        /*
         * AKF Note: no longer calling updateLastModified() renders the commented tests obsolete
         */
        String[] tests = {
                // throw *runtime* exception on insertElementValues
                "aop-test1.xml",
                // throw *runtime* exception on lastModified
                // "aop-test7.xml",
                // throw *sql* exception on insertElementValues
                "aop-test3.xml",
                // throw *sql* exception on lastModified
                // "aop-test8.xml",
                // throw *DataAccess*Exception on insertElementValues
                "aop-test5.xml",
                // throw *DataAccess*Exception on lastModified
                // "aop-test9.xml"
        };

        for (String testContext : tests) {
            checkExceptionRollback(testContext);
        }
    }

    @Test
    @Transactional
    @Rollback(true)
    @DirtiesContext
    public final void testAddCalculatedValuesToObs5() throws JAXBException, IOException {
        // This test is here because the batch insert is hanging on this insert

        final int stationId = 1021;
        final int datetimeId = 72258;
        Observation fromDb = service.getObservation(datetimeId, stationId);
        assertNull("expect ob not in database yet", fromDb);

        final String testDir = "src/resources/test/data";
        final String owdJson = "obs/owd-1021-72258.json";
        File fileOwd = new File(testDir, owdJson);
        String json = FileUtils.readWholeFile(fileOwd.getPath());
        Gson gson = new Gson();
        ObservationWithData owd = gson.fromJson(json, ObservationWithData.class);
        Collection<ElementValue> observedVals = owd.getElementValues().values();

        service.insertObservation(owd);

        final String calcevsJson = "evs/calcevs-1021-72258.json";
        File fileCalcEvs = new File(testDir, calcevsJson);
        json = FileUtils.readWholeFile(fileCalcEvs.getPath());
        Type type = new TypeToken<Collection<ElementValue>>() {
        }.getType();
        Collection<ElementValue> vals = gson.fromJson(json, type);

        Map<Observation, Collection<ElementValue>> updates = new HashMap<>();
        updates.put(owd.getObservation(), vals);
        service.addCalculatedValuesToObs(updates);

        ObservationWithData dbOwd = service.getObservationWithData(datetimeId, stationId);
        vals.addAll(observedVals);
        assertEquals("inserted elements not the same as retrieved", vals.size(), dbOwd.getElementValues().size());
        for (ElementValue val : vals) {
            assertNotNull("not found: " + val, dbOwd.getElementValue(val.getElementId()));
        }
    }

    @Transactional
    @Rollback(true)
    @DirtiesContext
    private void checkExceptionRollback(String testContext) {
        Map<Observation, Collection<ElementValue>> updates = new HashMap<>();
        String lastModQuery = "select COL1 from TABLE2 " + "where ID1=? and ID2=?";
        String factQuery = "select ID3, COL1 from TABLE1 " + "where ID1=? and ID2=?"
                + "  and ID3 in (?, ?, ?)";
        int datetimeId = 63500;
        int stationId = 1326;
        // creating separate application context that has pointcut to
        // cause runtime exception during insert
        // note changed applicationcontext construction with spring 3.1
        GenericXmlApplicationContext context = new GenericXmlApplicationContext();
        context.getEnvironment().setActiveProfiles("unittest");
        context.load("application-context.xml");
        context.load(testContext);
        context.refresh();

        ObservationService service = context.getBean(ObservationService.class);
        JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);

        ObservationWithData startob = service.getObservationWithData(datetimeId, stationId);
        assertNotNull(startob);
        assertNull("already contains 331", startob.getElementValues().get(331));
        assertNull("already contains 332", startob.getElementValues().get(332));
        assertNull("already contains 333", startob.getElementValues().get(333));
        // calculate some values - valid calculated elements that are not
        // already there (T5_1,T5_2,T5_3)
        Set<ElementValue> inserts = new HashSet<>();
        inserts.add(startob.addNewElementValue(331, new BigDecimal("4.1")));
        inserts.add(startob.addNewElementValue(332, new BigDecimal("4.1")));
        inserts.add(startob.addNewElementValue(333, new BigDecimal("4.2")));

        updates.put(startob.getObservation(), inserts);
        try {
            service.addCalculatedValuesToObs(updates);
            fail("Expected pointcut induced exception: " + testContext);
        } catch (Exception e) {
        }

        // check ob date not modified
        Timestamp ts = jdbcTemplate.queryForObject(lastModQuery, Timestamp.class, datetimeId, stationId);
        assertEquals("timestamp modified", startob.getObservation().getLastModified(), ts);
        // check facts not added
        SqlRowSet rs = jdbcTemplate.queryForRowSet(factQuery, datetimeId, stationId, 331, 332, 333);
        assertFalse("facts were added", rs.next());
    }

    /**
     * <pre>
     *       ---------------------- ---------------------- ---------------------- ---------------------- ----------------------
     *       1026                   439                    63000                  4.4                    0
     *       1026                   439                    63001                  4.1                    0
     *       1026                   439                    63002                  3.7                    0
     *       1326                   439                    63000                  12.4                   0
     *       1326                   439                    63001                  11.7                   0
     *       1326                   439                    63002                  11.9                   0
     *       1027                   319                    71866                  0                      0
     * </pre>
     */
    @Test
    public final void testGetElementValues() {
        ElementValue oneEV = service.getElementValue(1026, 63000, 439);
        assertNotNull("no ElementValue retrieved", oneEV);
        assertEquals("1.datetimeId incorrect", 63000, oneEV.getDatetimeId());
        assertEquals("1.stationId incorrect", 1026, oneEV.getStationId());
        assertEquals("1.elementId incorrect", 439, oneEV.getElementId());
        BigDecimal expected = new BigDecimal("4.4");
        assertEquals("1.value incorrect", expected, oneEV.getValue());
        assertEquals("1.flag incorrect", 0, oneEV.getFlags().getIntValue());

        ElementValue noEV = service.getElementValue(-9999, 63000, 439);
        assertNull("not EV not expected with dummy stationId", noEV);

        Collection<Integer> elementIds = Lists.newArrayList(439, 440);
        Map<StationDateElement, ElementValue> result = service.getElementValues(jan2008, jan2008 + 1440, 1026,
                elementIds);
        assertTrue("didn't get enough elements: " + result.size(), result.size() > 2000);
        assertTrue("too many elements: " + result.size(), result.size() < 2881);

        result = service.getElementValues(jan2008, jan2008 + 4320, 1026, elementIds);
        assertTrue("didn't get enough elements: " + result.size(), result.size() > 4000);
        assertTrue("too many elements: " + result.size(), result.size() < 8641);

        result = service.getElementValues(jan2008, jan2008 + 8640, 1026, elementIds);
        assertTrue("didn't get enough elements: " + result.size(), result.size() > 12000);
        assertTrue("too many elements: " + result.size(), result.size() < 17281);

        Map<Integer, ElementValue> resultMap = service.getElementValues(63000, 1026);
        assertNotNull("expected element 439", resultMap.get(439));
        assertNotNull("expected element 440", resultMap.get(440));

        Collection<ElementValue> resultColl = service.getElementValues(jan2008, stationId, elementIds);
        assertEquals("didn't get correct number of elements", 2, resultColl.size());
    }

    @Test
    @DirtiesContext
    @Rollback(true)
    @Transactional
    public void testGetLastNonmissingCalculatedDatetimeIdBefore() {
        int result = service.getLastNonmissingCalculatedDatetimeIdBefore(insertDt + 10, stationId);
        assertEquals("expected first inserted datetime; calculated matters", insertDt, result);
    }

    @Test
    @DirtiesContext
    @Rollback(true)
    @Transactional
    public void testGetLastNonmissingDatetimeIdBefore() {
        int result = service.getLastNonmissingDatetimeIdBefore(insertDt + 10, stationId);
        assertEquals("expected second inserted datetime; calc doesn't matter", insertDt + 1, result);
    }
}
