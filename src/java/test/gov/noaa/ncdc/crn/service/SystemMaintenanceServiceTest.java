package gov.noaa.ncdc.crn.service;

import static org.junit.Assert.*;

import java.sql.BatchUpdateException;

import gov.noaa.ncdc.crn.domain.Datetime;

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
public class SystemMaintenanceServiceTest {
    @Autowired
    SystemMaintenanceService service;

    @Test
    public void testGetLastDatetime() {
        Datetime last = service.getLastDatetime();
        int expected = 108087;
        assertEquals("incorrect last datetime", expected, last.getDatetimeId());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testInsertNewDatetimes() throws BatchUpdateException {
        Datetime last = service.getLastDatetime();
        int expected = 108087;
        assertEquals("incorrect last datetime before insert", expected, last.getDatetimeId());
        service.insertNewDatetimes(1);
        last = service.getLastDatetime();
        expected = 108087 + 24*365;
        assertEquals("incorrect last datetime after insert", expected, last.getDatetimeId());
    }

}
