package gov.noaa.ncdc.crn.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import gov.noaa.ncdc.crn.domain.QcDeltaParam;
import gov.noaa.ncdc.crn.domain.QcRangeParam;
import gov.noaa.ncdc.crn.spring.ApplicationContextProvider.Contexts;

import java.math.BigDecimal;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

/*
 * TODO test if element not in stream, null
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml" })
@ActiveProfiles(profiles = "unittest")
public class QcDaoTest {

    @Autowired
    private QcDao qcDao;

    @BeforeClass
    public static final void updateTestData() {
        DataSource dataSource = Contexts.UNIT.getApplicationContext().getBean(DataSource.class);
        JdbcTemplate template = new JdbcTemplate(dataSource);
        // inserts test data into TABLE1, TABLE2, and TABLE3
        Resource resource = new ClassPathResource("data/testdata/scripts/QcDaoTest_data.sql");
        JdbcTestUtils.executeSqlScript(template, resource, false);
    }

    @AfterClass
    public static final void deleteTestData() {
        DataSource dataSource = Contexts.UNIT.getApplicationContext().getBean(DataSource.class);
        JdbcTemplate template = new JdbcTemplate(dataSource);
        // inserts test data into TABLE1, TABLE2, and TABLE3
        Resource resource = new ClassPathResource("data/testdata/scripts/QcDaoTest_data_rollback.sql");
        JdbcTestUtils.executeSqlScript(template, resource, false);
    }

    @Test
    public final void testQcDaoImpl() {
        assertNotNull(qcDao);
        assertEquals(gov.noaa.ncdc.crn.dao.mybatis.QcDaoImpl.class, qcDao.getClass());
    }

    @Test
    public final void testGetQcRangeParam() {
        QcRangeParam result = qcDao.getQcRangeParam(-99, -1, -1, -1);
        assertNull(result);
        result = qcDao.getQcRangeParam(-99, 8, 1026, 4);
        assertNull(result);
        int elementId = 463;
        result = qcDao.getQcRangeParam(elementId, 4, 1026, 4);
        assertNull("stream 4 does not have elementId=463", result);
        QcRangeParam expected = new QcRangeParam(elementId, -1, -1, -1, new BigDecimal("-30"), new BigDecimal("65"));
        result = qcDao.getQcRangeParam(elementId, 8, 1026, 4);
        assertEquals(expected, result);

        elementId = 188;
        expected = new QcRangeParam(elementId, -1, -1, -1, new BigDecimal("-60"), new BigDecimal("60"));
        result = qcDao.getQcRangeParam(elementId, 8, 1027, 4);
        assertEquals(expected, result);
        result = qcDao.getQcRangeParam(elementId, 4, 1027, 6);
        assertEquals(expected, result);
        expected = new QcRangeParam(elementId, 6, -1, -1, new BigDecimal("6.3"), new BigDecimal("18.9"));
        result = qcDao.getQcRangeParam(elementId, 6, 1011, 6);
        assertEquals(expected, result);
        result = qcDao.getQcRangeParam(elementId, 6, 1027, 9);
        assertEquals(expected, result);
        expected = new QcRangeParam(elementId, -1, 1026, 6, new BigDecimal("7.0"), new BigDecimal("26.0"));
        result = qcDao.getQcRangeParam(elementId, 4, 1026, 6);
        assertEquals(expected, result);
        expected = new QcRangeParam(elementId, -1, 1026, -1, new BigDecimal("4.8"), new BigDecimal("21.1"));
        result = qcDao.getQcRangeParam(elementId, 8, 1026, 4);
        assertEquals(expected, result);
    }

    @Test
    public final void testGetQcRangeParams() {
        Map<Integer, QcRangeParam> result = qcDao.getQcRangeParams(-1, -1, -1);
        assertNull(result.get(-99));
        result = qcDao.getQcRangeParams(8, 1026, 4);
        assertNull(result.get(-99));
        int elementId = 463;
        QcRangeParam expected = new QcRangeParam(elementId, -1, -1, -1, new BigDecimal("-30"), new BigDecimal("65"));
        result = qcDao.getQcRangeParams(8, 1026, 4);
        assertEquals(expected, result.get(elementId));
        result = qcDao.getQcRangeParams(4, 1026, 4);
        assertNull("stream 4 does not have elementId=463", result.get(elementId));

        elementId = 188;
        expected = new QcRangeParam(elementId, -1, -1, -1, new BigDecimal("-60"), new BigDecimal("60"));
        result = qcDao.getQcRangeParams(8, 1027, 4);
        assertEquals(expected, result.get(elementId));
        result = qcDao.getQcRangeParams(4, 1027, 6);
        assertEquals(expected, result.get(elementId));
        expected = new QcRangeParam(elementId, 6, -1, -1, new BigDecimal("6.3"), new BigDecimal("18.9"));
        result = qcDao.getQcRangeParams(6, 1011, 6);
        assertEquals(expected, result.get(elementId));
        result = qcDao.getQcRangeParams(6, 1027, 9);
        assertEquals(expected, result.get(elementId));
        expected = new QcRangeParam(elementId, -1, 1026, 6, new BigDecimal("7.0"), new BigDecimal("26.0"));
        result = qcDao.getQcRangeParams(4, 1026, 6);
        assertEquals(expected, result.get(elementId));
        expected = new QcRangeParam(elementId, -1, 1026, -1, new BigDecimal("4.8"), new BigDecimal("21.1"));
        result = qcDao.getQcRangeParams(8, 1026, 4);
        assertEquals(expected, result.get(elementId));
    }

    @Test
    public final void testGetQcDeltaParam() {
        QcDeltaParam result = qcDao.getQcDeltaParam(-99, -1, -1, -1);
        assertNull(result);
        QcDeltaParam expected = new QcDeltaParam(54, -1, -1, 87, 88, 89, new BigDecimal("0.8"));
        result = qcDao.getQcDeltaParam(87, 54, 1099, 4);
        assertEquals(expected, result);
        expected = new QcDeltaParam(99, 1027, -1, 855, 856, 857, new BigDecimal("1.2"));
        result = qcDao.getQcDeltaParam(855, 99, 1027, 4);
        assertEquals(expected, result);
        expected = new QcDeltaParam(-1, 1027, -1, 855, 856, 857, new BigDecimal("2.3"));
        result = qcDao.getQcDeltaParam(855, 98, 1027, 4);
        assertEquals(expected, result);
        expected = new QcDeltaParam(-1, 1027, -1, 855, 856, 857, new BigDecimal("2.3"));
        result = qcDao.getQcDeltaParam(855, 97, 1027, 4);
        assertEquals(expected, result);
        expected = new QcDeltaParam(-1, 1027, -1, 921, 922, 923, new BigDecimal("2.4"));
        result = qcDao.getQcDeltaParam(921, 98, 1027, 7);
        assertEquals(expected, result);
        expected = new QcDeltaParam(-1, -1, -1, 921, 922, 923, new BigDecimal("2.6"));
        result = qcDao.getQcDeltaParam(921, 96, 1026, 7);
        assertEquals(expected, result);
        expected = new QcDeltaParam(98, 1026, -1, 921, 922, 923, new BigDecimal("1.7"));
        result = qcDao.getQcDeltaParam(921, 98, 1026, 6);
        assertEquals(expected, result);
        expected = new QcDeltaParam(98, 1026, 7, 921, 922, 923, new BigDecimal("1.3"));
        result = qcDao.getQcDeltaParam(921, 98, 1026, 7);
        assertEquals(expected, result);
        expected = new QcDeltaParam(-1, 1027, -1, 921, 922, 923, new BigDecimal("2.4"));
        result = qcDao.getQcDeltaParam(921, 96, 1027, 7);
        assertEquals(expected, result);

        result = qcDao.getQcDeltaParam(-99, 96, 1027, 7);
        assertNull(result);
        result = qcDao.getQcDeltaParam(921, 8, 1027, 7);
        assertNull("stream 8 does not incude element 921", result);
    }

    @Test
    public final void testGetQcDeltaParams() {
        Map<Integer, QcDeltaParam> result = qcDao.getQcDeltaParams(-1, -1, -1);
        assertNull(result.get(-99));
        QcDeltaParam expected = new QcDeltaParam(54, -1, -1, 87, 88, 89, new BigDecimal("0.8"));
        result = qcDao.getQcDeltaParams(54, 1099, 4);
        assertEquals(expected, result.get(87));
        expected = new QcDeltaParam(99, 1027, -1, 855, 856, 857, new BigDecimal("1.2"));
        result = qcDao.getQcDeltaParams(99, 1027, 4);
        assertEquals(expected, result.get(855));
        expected = new QcDeltaParam(-1, 1027, -1, 855, 856, 857, new BigDecimal("2.3"));
        result = qcDao.getQcDeltaParams(98, 1027, 4);
        assertEquals(expected, result.get(855));
        expected = new QcDeltaParam(-1, 1027, -1, 855, 856, 857, new BigDecimal("2.3"));
        result = qcDao.getQcDeltaParams(97, 1027, 4);
        assertEquals(expected, result.get(855));
        expected = new QcDeltaParam(-1, 1027, -1, 921, 922, 923, new BigDecimal("2.4"));
        result = qcDao.getQcDeltaParams(98, 1027, 7);
        assertEquals(expected, result.get(921));
        expected = new QcDeltaParam(-1, -1, -1, 921, 922, 923, new BigDecimal("2.6"));
        result = qcDao.getQcDeltaParams(96, 1026, 7);
        assertEquals(expected, result.get(921));
        expected = new QcDeltaParam(98, 1026, -1, 921, 922, 923, new BigDecimal("1.7"));
        result = qcDao.getQcDeltaParams(98, 1026, 6);
        assertEquals(expected, result.get(921));
        expected = new QcDeltaParam(98, 1026, 7, 921, 922, 923, new BigDecimal("1.3"));
        result = qcDao.getQcDeltaParams(98, 1026, 7);
        assertEquals(expected, result.get(921));
        expected = new QcDeltaParam(-1, 1027, -1, 921, 922, 923, new BigDecimal("2.4"));
        result = qcDao.getQcDeltaParams(96, 1027, 7);
        assertEquals(expected, result.get(921));

        result = qcDao.getQcDeltaParams(96, 1027, 7);
        assertNull(result.get(-99));

        result = qcDao.getQcDeltaParams(8, 1027, 7);
        assertNull("stream 8 does not incude element 921", result.get(921));
    }

}
