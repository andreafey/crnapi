package gov.noaa.ncdc.crn.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.noaa.ncdc.crn.dao.ElementDao;
import gov.noaa.ncdc.crn.domain.CrnException;
import gov.noaa.ncdc.crn.domain.CrnExceptionFact;
import gov.noaa.ncdc.crn.domain.ElementValue;
import gov.noaa.ncdc.crn.domain.ExceptionReapply;
import gov.noaa.ncdc.crn.domain.ExceptionResolution;
import gov.noaa.ncdc.crn.domain.ExceptionResolutionFact;
import gov.noaa.ncdc.crn.domain.ExceptionStatus;
import gov.noaa.ncdc.crn.domain.ExceptionTicketSystem;
import gov.noaa.ncdc.crn.domain.Observation;
import gov.noaa.ncdc.crn.domain.ObservationWithData;
import gov.noaa.ncdc.crn.domain.ResolutionWithFacts;
import gov.noaa.ncdc.crn.domain.StationDateElement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations={"classpath:application-context.xml"})
@ActiveProfiles(profiles="unittest")
public class ExceptionServiceTest {

    @Autowired
    ExceptionService service;
    @Autowired
    ObservationService obService;
    @Autowired
    ElementDao elementDao;
    @Autowired
    SqlSession session;


    @Test
    @Transactional
    @Rollback(true)
    public void testUpdateFlags() {
        // ob doesn't exist already
        final int datetimeId_1=103001, stationId_1=1026,
                elementId_1=131, elementId_2=132, elementId_3=133;

        // insert a dummy ob with flagged evs
        ObservationWithData ob =
                new ObservationWithData(new Observation(stationId_1, datetimeId_1, 12,1, "fakefile.txt",125));
        ob.addNewElementValue(elementId_1, new BigDecimal("4.0"), 4);
        ob.addNewElementValue(elementId_2, new BigDecimal("4.1"), 4);
        ob.addNewElementValue(elementId_3, new BigDecimal("4.2"));
        obService.insertObservation(ob);

        ElementValue ev1 = elementDao.getElementValue(
                new StationDateElement(ob.getStationId(), ob.getDatetimeId(), elementId_1));
        assertEquals("should be flagged already",4,ev1.getFlags().getIntValue());
        // flag was 4, now 6
        ev1.getFlags().setFlagsFromInt(6);
        ElementValue ev2 = elementDao.getElementValue(
                new StationDateElement(ob.getStationId(), ob.getDatetimeId(), elementId_2));
        assertEquals("should be flagged already",4,ev2.getFlags().getIntValue());
        // flag was 4, now unflagged
        ev2.getFlags().setFlagsFromInt(0);
        ElementValue ev3 = elementDao.getElementValue(
                new StationDateElement(ob.getStationId(), ob.getDatetimeId(), elementId_3));
        assertEquals("should not be flagged already",0,ev3.getFlags().getIntValue());
        // was unflagged, now 8
        ev3.getFlags().setFlagsFromInt(8);
        List<ElementValue> vals = Lists.newArrayList(ev1,ev2,ev3);
        service.updateFlags(vals);
        ElementValue result = elementDao.getElementValue(
                new StationDateElement(ob.getStationId(), ob.getDatetimeId(), elementId_1));
        assertEquals("should update flag value", 6, result.getFlags().getIntValue());
        result = elementDao.getElementValue(
                new StationDateElement(ob.getStationId(), ob.getDatetimeId(), elementId_2));
        assertEquals("should remove flag value", 0, result.getFlags().getIntValue());
        result = elementDao.getElementValue(
                new StationDateElement(ob.getStationId(), ob.getDatetimeId(), elementId_3));
        assertEquals("should add flag value", 8, result.getFlags().getIntValue());

    }
    @Test
    @Transactional
    @Rollback(true)
    public void testUpdateFacts() {
        // ob doesn't exist already
        final int datetimeId_1=103001, stationId_1=1026,
                elementId_1=131, elementId_2=132, elementId_3=133, elementId_4c=439;

        // insert a dummy ob with flagged evs
        ObservationWithData ob =
                new ObservationWithData(new Observation(stationId_1, datetimeId_1, 12,1, "fakefile.txt",125));
        ob.addNewElementValue(elementId_1, new BigDecimal("4.0"), 4);
        ob.addNewElementValue(elementId_2, new BigDecimal("4.1"), 4);
        ob.addNewElementValue(elementId_3, new BigDecimal("4.2"));
        ob.addNewElementValue(elementId_4c, new BigDecimal("8.8"));
        obService.insertObservation(ob);

        ElementValue ev1 = elementDao.getElementValue(
                new StationDateElement(ob.getStationId(), ob.getDatetimeId(), elementId_1));
        assertEquals("should be flagged already",4,ev1.getFlags().getIntValue());
        BigDecimal expected = new BigDecimal("4");
        assertEquals("fact value unexpected",expected,ev1.getValue());
        // flag was 4, now 6, value was 4.0, now 4.5
        ev1 = new ElementValue(ev1.getStationId(), ev1.getDatetimeId(), ev1.getElementId(),
                new BigDecimal("4.5"), 6, ev1.getDecimalPlaces(), ev1.getPublishedDecimalPlaces());

        ElementValue ev2 = elementDao.getElementValue(
                new StationDateElement(ob.getStationId(), ob.getDatetimeId(), elementId_2));
        assertEquals("should be flagged already",4,ev2.getFlags().getIntValue());
        expected = new BigDecimal("4.1");
        assertEquals("fact value unexpected",expected,ev2.getValue());
        // flag was 4, now unflagged, value was 4.1 now 10.6
        ev2 = new ElementValue(ev2.getStationId(), ev2.getDatetimeId(), ev2.getElementId(),
                new BigDecimal("10.6"), 0, ev2.getDecimalPlaces(), ev2.getPublishedDecimalPlaces());

        ElementValue ev3 = elementDao.getElementValue(
                new StationDateElement(ob.getStationId(), ob.getDatetimeId(), elementId_3));
        assertEquals("should not be flagged already",0,ev3.getFlags().getIntValue());
        expected = new BigDecimal("4.2");
        assertEquals("fact value unexpected",expected,ev3.getValue());
        // was unflagged, now 8, value was 4.2, now -2.3
        ev3 = new ElementValue(ev3.getStationId(), ev3.getDatetimeId(), ev3.getElementId(),
                new BigDecimal("-2.3"), 8, ev3.getDecimalPlaces(), ev3.getPublishedDecimalPlaces());

        ElementValue ev4 = elementDao.getElementValue(
                new StationDateElement(ob.getStationId(), ob.getDatetimeId(), elementId_4c));
        assertEquals("should not be flagged already",0,ev4.getFlags().getIntValue());
        expected = new BigDecimal("8.8");
        assertEquals("fact value unexpected",expected,ev4.getValue());
        // calculated element value null; delete
        ev4 = new ElementValue(ev4.getStationId(), ev4.getDatetimeId(), ev4.getElementId(),
                null, 0, ev4.getDecimalPlaces(), ev4.getPublishedDecimalPlaces());
        List<ElementValue> vals = Lists.newArrayList(ev1,ev2,ev3,ev4);
        service.updateFacts(vals);

        ElementValue result = elementDao.getElementValue(
                new StationDateElement(ob.getStationId(), ob.getDatetimeId(), elementId_1));
        assertEquals("should update flag value", 6, result.getFlags().getIntValue());
        expected = new BigDecimal("4.5");
        assertEquals("fact value unexpected",expected,result.getValue());
        result = elementDao.getElementValue(
                new StationDateElement(ob.getStationId(), ob.getDatetimeId(), elementId_2));
        assertEquals("should remove flag value", 0, result.getFlags().getIntValue());
        expected = new BigDecimal("10.6");
        assertEquals("fact value unexpected",expected,result.getValue());
        result = elementDao.getElementValue(
                new StationDateElement(ob.getStationId(), ob.getDatetimeId(), elementId_3));
        assertEquals("should add flag value", 8, result.getFlags().getIntValue());
        expected = new BigDecimal("-2.3");
        assertEquals("fact value unexpected",expected,result.getValue());
        result = elementDao.getElementValue(
                new StationDateElement(ob.getStationId(), ob.getDatetimeId(), elementId_4c));
        assertNull("should delete fact", result);

    }

    @Test
    public void testGetException() {
        CrnException myex = service.getException(-1);
        assertNull(myex);
        CrnException myex2 = service.getException(7017);
        CrnException expected = new CrnException(7017,
                ExceptionTicketSystem.TRAC, "12", "dummy trac ticket",
                ExceptionStatus.OPEN);
        assertEquals("something differs from expected exception",
                expected, myex2);
    }

    @Test
    public void testGetExceptionFacts() {
        CrnExceptionFact expected = new CrnExceptionFact(1026, 93228, 439, 7017);
        List<CrnExceptionFact> facts = service.getExceptionFacts(7017);
        assertEquals("expect exactly 4 facts for id=7017", 4, facts.size());
        assertTrue("didn't get expected1", facts.contains(expected));
        /* test that when exception has multiple resolutions on the same fact,
	---------------------- ---------------------- ---------------------- ---------------------- ----------------------
	1234                   100400                 331                    7001                   153
	1234                   100400                 331                    7001                   154
         */

        expected = new CrnExceptionFact(1234, 100400, 331, 7001);
        facts = service.getExceptionFacts(7001);
        assertEquals("expect exactly 1 fact for id=7001", 1, facts.size());
        assertTrue("didn't get expected1", facts.contains(expected));
    }


    @Test
    public void testGetExceptionsFromTicket() {
        Collection<CrnException> myexs = service.getExceptionsFromTicket(
                ExceptionTicketSystem.TRAC, "9999");
        assertEquals("no ticket entry for id=9999", 0, myexs.size());
        myexs = service.getExceptionsFromTicket
                (ExceptionTicketSystem.TRAC, "13");
        assertTrue("expected to retrieve at least two exceptions",
                myexs.size()>=2);
        CrnException myex2 = service.getException(7020);
        assertTrue("exception not retrieved: 7020", myexs.contains(myex2));
        myex2 = service.getException(7022);
        assertTrue("exception not retrieved: 7022", myexs.contains(myex2));
        myexs = service.getExceptionsFromTicket(
                ExceptionTicketSystem.TRAC, "1");
        assertEquals("only expect one ticket for id=1", 1, myexs.size());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testInsertException() {
        CrnException ex = new CrnException(ExceptionTicketSystem.TRAC, "12",
                "dummy trac ticket", ExceptionStatus.OPEN);
        CrnExceptionFact newFact = new CrnExceptionFact(1026, 93231, 439);
        List<CrnExceptionFact> newFacts = new ArrayList<CrnExceptionFact>();
        newFacts.add(newFact);

        service.insertException(ex, newFacts);
        assertNotNull("should have assigned exceptionId from sequence",
                ex.getExceptionId());
        assertEquals("id of exception and fact should be the same",
                ex.getExceptionId(), newFacts.get(0).getExceptionId());

        CrnException result = service.getException(ex.getExceptionId());
        assertEquals("ex details should have been stored in db", ex, result);
        List<CrnExceptionFact> resultFacts =
                service.getExceptionFacts(ex.getExceptionId());
        assertEquals("expected only one insert", 1, resultFacts.size());
        assertTrue("new fact should be present in collection",
                resultFacts.contains(newFacts.get(0)));
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUpdateExceptionStatus() {
        int exceptionId = 7017;
        CrnException ex = service.getException(exceptionId);
        assertEquals("status s/b OPEN", ExceptionStatus.OPEN, ex.getStatus());
        ex.setStatus(ExceptionStatus.CLOSED_RESOLVED);
        service.updateExceptionStatus(ex);
        CrnException result = service.getException(exceptionId);
        assertEquals("expected status change",
                ExceptionStatus.CLOSED_RESOLVED, result.getStatus());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testInsertExceptionResolution() {
        int datetimeId = 93228;
        int stationId = 1026;
        ExceptionResolution resolution = new ExceptionResolution(null, 7017,
                "foo.yaml", ExceptionReapply.AUTO);
        List<ResolutionWithFacts> resolutions =
                service.getResolutions(stationId, datetimeId, ExceptionReapply.AUTO);
        assertEquals("didn't expect any resolutions yet", 0, resolutions.size());

        service.insertExceptionResolution(resolution);
        assertTrue("resolution id did not get set", resolution.getResolutionId()>0);
        resolutions = service.getResolutions(stationId, datetimeId, ExceptionReapply.AUTO);
        assertEquals("expected exactly one resolution", 1, resolutions.size());
        assertEquals("resolutions don't match", resolution, resolutions.get(0).getResolution());
        assertEquals("didn't expecty related facts", 0, resolutions.get(0).getFacts().size());
    }
    @Test
    @Transactional
    @Rollback(true)
    public void testInsertExceptionResolutionFacts() {
        int stationId = 1026;
        int datetimeId = 93228;
        ExceptionResolution resolution = new ExceptionResolution(null, 7017,
                "foo.yaml", ExceptionReapply.AUTO);
        service.insertExceptionResolution(resolution);
        session.flushStatements();

        Collection<ExceptionResolutionFact> facts = new ArrayList<>();
        ExceptionResolutionFact fact1 =
                new ExceptionResolutionFact(stationId, datetimeId, 439, resolution.getResolutionId(), "4.9");
        facts.add(fact1);
        service.insertExceptionResolutionFacts(facts);
        session.flushStatements();

        List<ResolutionWithFacts> resolutions =
                service.getResolutions(stationId, datetimeId, ExceptionReapply.AUTO);
        ExceptionResolutionFact onlyFact =
                Iterables.getOnlyElement(resolutions.get(0).getFacts());
        assertNotNull("expected just one related fact", onlyFact);
        assertEquals("didn't receive all the correct info", fact1, onlyFact);
    }
    @Test
    public void testGetResolutions() {
        // there is exactly one resolution with NO reapply status
        int datetimeId = 93228;
        int stationId = 1026;
        List<ResolutionWithFacts> resolutions =
                service.getResolutions(stationId, datetimeId, ExceptionReapply.AUTO);
        assertEquals("didn't expect any resolutions for AUTO", 0, resolutions.size());
        resolutions = service.getResolutions(stationId, datetimeId, ExceptionReapply.MANUAL);
        assertEquals("didn't expect any resolutions for MANUAL status", 0, resolutions.size());

        resolutions = service.getResolutions(stationId, datetimeId, ExceptionReapply.NO);
        // This shows the status selection is being handled correctly
        assertEquals("expected exactly one resolution for NO status", 1, resolutions.size());

        resolutions = service.getResolutions(stationId, datetimeId, null);
        // This shows the status selection is being handled correctly
        assertEquals("expected exactly one resolution when all statuses selected", 1, resolutions.size());
    }
    @Test
    public void testGetResolutionsRange() {
        // there are 3 resolutions for range with NO reapply status (dt 93228-30)
        int beginDt = 93228;
        int endDt = 93230;
        int stationId = 1026;
        List<ResolutionWithFacts> resolutions =
                service.getResolutions(stationId, beginDt, endDt, ExceptionReapply.AUTO);
        assertEquals("didn't expect any resolutions for AUTO", 0, resolutions.size());
        resolutions = service.getResolutions(stationId, beginDt, endDt, ExceptionReapply.MANUAL);
        assertEquals("didn't expect any resolutions for MANUAL status", 0, resolutions.size());

        resolutions = service.getResolutions(stationId, beginDt, endDt, ExceptionReapply.NO);
        // This shows the status selection is being handled correctly
        assertEquals("expected exactly 3 resolutions for NO status", 3, resolutions.size());

        resolutions = service.getResolutions(stationId, beginDt, endDt, null);
        // This shows the status selection is being handled correctly
        assertEquals("expected exactly 3 resolutions when all statuses selected", 3, resolutions.size());
    }
    /*
	 All the unresolved facts
---------------------- ---------------------- ---------------------- ----------------------
1026                   93229                  439                    7017
1234                   100400                 331                    7001
1026                   93228                  439                    7017
1026                   93230                  440                    7017

     */
    @Test
    public void testGetUnresolvedExceptionFacts() {
        int begin = 98623;
        int end = begin;
        List<Integer> stationIds = Lists.newArrayList(1234);
        List<CrnExceptionFact> unresolved = service.getUnresolvedExceptionFacts(stationIds, begin, end, 87, 96);

        assertEquals("nothing expected for this station/datetime",
                0, unresolved.size());

        begin = 93228;
        end = 93230;
        stationIds = Lists.newArrayList(1026);
        unresolved = service.getUnresolvedExceptionFacts(stationIds, begin, end, 439, 440);

        assertEquals("3 unresolved exception facts expected for this station/datetime",
                3, unresolved.size());
    }
    @Test
    public void testGetUnresolvedExceptionFactsInt() {
        int exceptionId = 1026;
        List<CrnExceptionFact> unresolved = service.getUnresolvedExceptionFacts(exceptionId);

        assertEquals("nothing expected for this exceptionId", 0, unresolved.size());

        exceptionId = 7001;
        unresolved = service.getUnresolvedExceptionFacts(exceptionId);
        assertEquals("1 unresolved exception facts expected for this exceptionId", 1, unresolved.size());

        exceptionId = 7017;
        unresolved = service.getUnresolvedExceptionFacts(exceptionId);
        assertEquals("4 unresolved exception facts expected for this exceptionId", 4, unresolved.size());
    }

}
