package gov.noaa.ncdc.crn.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations={"classpath:application-context.xml"})
@ActiveProfiles(profiles="unittest")
public class ConnectionDaoTest {

    @Autowired
    private ConnectionDao connectionDao;

    @Test
    public final void testConnectionDaoImpl() {
        assertNotNull("connectionDao not set in app context",connectionDao);
    }

    @Test
    public final void testTestConnection() {
        boolean available = connectionDao.testConnection();
        assertTrue("Connection unavailable",available);
    }

}
