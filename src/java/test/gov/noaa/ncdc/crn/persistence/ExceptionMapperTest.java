/**
 * 
 */
package gov.noaa.ncdc.crn.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.noaa.ncdc.crn.domain.CrnException;
import gov.noaa.ncdc.crn.domain.CrnExceptionFact;
import gov.noaa.ncdc.crn.domain.ExceptionStatus;
import gov.noaa.ncdc.crn.domain.ExceptionTicketSystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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

/**
 * @author Andrea.Fey
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations={"classpath:application-context.xml"})
@ActiveProfiles(profiles="unittest")
public class ExceptionMapperTest {
    @Autowired
    private ExceptionMapper mapper;

    /**
     * Test method for {@link gov.noaa.ncdc.crn.persistence.ExceptionMapper#selectException(int)}.
     */
    @Test
    public void testSelectException() {
        int exceptionId = 7017;
        CrnException exception = mapper.selectException(exceptionId);
        CrnException expected = new CrnException(7017,
                ExceptionTicketSystem.TRAC, "12", "dummy trac ticket",
                ExceptionStatus.OPEN);
        assertEquals("something differs from expected exception",
                expected, exception);
    }

    /**
     * Test method for {@link gov.noaa.ncdc.crn.persistence.ExceptionMapper#selectExceptionFacts(java.util.Map)}.
     */
    @Test
    public void testSelectExceptionFacts() {
        Map<String,Object> params = new HashMap<>();
        params.put("exceptionId", Integer.valueOf(7017));

        CrnExceptionFact expected1 = new CrnExceptionFact(1026, 93228, 439, 7017);
        List<CrnExceptionFact> facts = mapper.selectExceptionFacts(params);
        assertEquals("didn't get exactly correct # facts for id=7017", 4, facts.size());
        assertTrue("didn't get expected1", facts.contains(expected1));

        params.clear();
        params.put("stationId", 1026);
        facts = mapper.selectExceptionFacts(params);
        assertTrue("didn't get exactly 3 facts for station_id=1026",
                facts.size()==3);
        assertTrue("didn't get expected1", facts.contains(expected1));

        params.clear();
        params.put("elementId", 439);
        facts = mapper.selectExceptionFacts(params);
        assertTrue("didn't get exactly 2 facts for elementId=439",
                facts.size()==2);
        assertTrue("didn't get expected1", facts.contains(expected1));

        params.clear();
        params.put("datetimeId", 93228);
        facts = mapper.selectExceptionFacts(params);
        assertTrue("didn't get exactly 1 fact for datetimeId=93228",
                facts.size()==1);
        assertTrue("didn't get expected1", facts.contains(expected1));


        params.clear();
        params.put("beginDatetimeId", 93228);
        params.put("endDatetimeId", 93229);
        facts = mapper.selectExceptionFacts(params);
        assertTrue("didn't get exactly 2 facts for datetimeId 93228-9",
                facts.size()==2);
        assertTrue("didn't get expected1", facts.contains(expected1));
    }

    /**
     * Test method for {@link gov.noaa.ncdc.crn.persistence.ExceptionMapper#insertException(gov.noaa.ncdc.crn.domain.CrnException)}.
     */
    @Test
    @Transactional
    @Rollback(true)
    public void testInsertException() {
        CrnException ex = new CrnException(ExceptionTicketSystem.TRAC, "98",
                "dummy trac ticket 98", ExceptionStatus.OPEN);

        Collection<CrnException> results =
                mapper.selectExceptionsFromTicket(ExceptionTicketSystem.TRAC, "98");
        assertTrue("should not yet be in db", results.size()==0);

        CrnExceptionFact newFact = new CrnExceptionFact(1026, 93231, 439);
        List<CrnExceptionFact> newFacts = new ArrayList<CrnExceptionFact>();
        newFacts.add(newFact);

        mapper.insertException(ex);
        assertNotNull("should have assigned exceptionId from sequence",
                ex.getExceptionId());

        CrnException result = mapper.selectException(ex.getExceptionId());
        assertEquals("ex details should have been stored in db", ex, result);
    }

    /**
     * Test method for {@link gov.noaa.ncdc.crn.persistence.ExceptionMapper#insertExceptionFact(gov.noaa.ncdc.crn.domain.CrnExceptionFact)}.
     */
    @Test
    @Transactional
    @Rollback(true)
    public void testInsertExceptionFact() {
        CrnException ex = new CrnException(ExceptionTicketSystem.TRAC, "12",
                "dummy trac ticket", ExceptionStatus.OPEN);
        CrnExceptionFact newFact = new CrnExceptionFact(1026, 93231, 439);

        Map<String,Object> params = new HashMap<>();
        params.put("stationId", 1026);
        params.put("datetimeId", 93231);
        params.put("elementId", 439);
        List<CrnExceptionFact> resultFacts = mapper.selectExceptionFacts(params);
        assertTrue("facts should not be in db yet", resultFacts.size()==0);

        mapper.insertException(ex); // dependency on this for parent key
        mapper.insertExceptionFact(newFact);

        params.clear();
        params.put("stationId", 1026);
        resultFacts = mapper.selectExceptionFacts(params);

        assertTrue("expected only one insert", resultFacts.size()>=1);
        assertTrue("new fact should be present in collection",
                resultFacts.contains(newFact));
    }

    /**
     * Test method for {@link gov.noaa.ncdc.crn.persistence.ExceptionMapper#updateExceptionStatus(gov.noaa.ncdc.crn.domain.CrnException)}.
     */
    @Test
    @Transactional
    @Rollback(true)
    public void testUpdateExceptionStatus() {
        int exceptionId = 7017;
        CrnException ex = mapper.selectException(exceptionId);
        assertEquals("status s/b OPEN", ExceptionStatus.OPEN, ex.getStatus());
        ex.setStatus(ExceptionStatus.CLOSED_RESOLVED);
        mapper.updateExceptionStatus(ex);
        CrnException result = mapper.selectException(exceptionId);
        assertEquals("expected status change",
                ExceptionStatus.CLOSED_RESOLVED, result.getStatus());
    }

    /**
     * Test method for {@link gov.noaa.ncdc.crn.persistence.ExceptionMapper#selectExceptionsFromTicket(gov.noaa.ncdc.crn.domain.ExceptionTicketSystem, java.lang.String)}.
     */
    @Test
    @Transactional
    @Rollback(true)
    public void testSelectExceptionsFromTicket() {
        Collection<CrnException> myexs = mapper.selectExceptionsFromTicket(
                ExceptionTicketSystem.TRAC, "9999");
        assertEquals(0, myexs.size());
        myexs = mapper.selectExceptionsFromTicket
                (ExceptionTicketSystem.TRAC, "13");
        assertTrue("expected to retrieve at least two exceptions",
                myexs.size()>=2);
        CrnException myex2 = mapper.selectException(7020);
        assertTrue("exception not retrieved: 7020", myexs.contains(myex2));
        myex2 = mapper.selectException(7022);
        assertTrue("exception not retrieved: 7022", myexs.contains(myex2));
    }

}
