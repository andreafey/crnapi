package gov.noaa.ncdc.crn.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.noaa.ncdc.crn.domain.CrnExceptionFact;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations={"classpath:application-context.xml"})
@ActiveProfiles(profiles="unittest")
public class ExceptionDaoTest {
    @Autowired
    ExceptionDao exceptionDao;


    // this tests the parameter map pull and isn't tested under ExceptionServiceTest
    @Test
    public void testGetExceptionFacts() {
        CrnExceptionFact expected1 = new CrnExceptionFact(1026, 93228, 439, 7017);
        CrnExceptionFact expected2 = new CrnExceptionFact(1026, 93229, 439, 7017);
        CrnExceptionFact expected3 = new CrnExceptionFact(1026, 93230, 440, 7017);

        Map<String,Object> params = new HashMap<>();
        params.put("exceptionId", 7017);
        List<CrnExceptionFact> facts = exceptionDao.getExceptionFacts(params);
        assertEquals("didn't get exactly correct # facts for id=7017", 4, facts.size());

        params.clear();
        params.put("datetimeId", 93228);
        facts = exceptionDao.getExceptionFacts(params);
        assertTrue("didn't get expected fact", facts.contains(expected1));

        params.clear();
        params.put("beginDatetimeId", 93228);
        params.put("endDatetimeId", 93229);
        facts = exceptionDao.getExceptionFacts(params);
        assertTrue("didn't get expected fact (#1)", facts.contains(expected1));
        assertTrue("didn't get expected fact (#2)", facts.contains(expected2));
        assertFalse("didn't expect fact (#3)", facts.contains(expected3));

        params.clear();
        params.put("elementId", 440);
        facts = exceptionDao.getExceptionFacts(params);
        assertTrue("didn't get expected fact (#3)", facts.contains(expected3));
        assertFalse("didn't expect fact (#1)", facts.contains(expected1));
    }

}
