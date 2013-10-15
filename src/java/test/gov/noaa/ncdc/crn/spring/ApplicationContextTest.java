package gov.noaa.ncdc.crn.spring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.noaa.ncdc.crn.dao.ConnectionDao;
import gov.noaa.ncdc.crn.spring.ApplicationContextProvider.Contexts;

import java.net.InetAddress;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class ApplicationContextTest
{
	// TODO add supported hosts and/or use a property to configure
	public enum SUPPORTED_HOSTS {
		AFEYNB
	}
	
	@Test
	public void testProvidedProfiles() {
		validateContextIntegrity(Contexts.UNIT.getApplicationContext(), 
				"unittest", "crnunittest", "DEV");

		// Subsequent lines in this test won't run on machines not specified
		// to contain production database configuration
		org.junit.Assume.assumeTrue(supportedProdHost());
		validateContextIntegrity(Contexts.PROD_RO.getApplicationContext(), 
				"prod-ro", "crnselect", "PROD");
		validateContextIntegrity(Contexts.PROD.getApplicationContext(), "prod", "crn", "PROD");
		validateContextIntegrity(Contexts.PROD_RO_MEMCACHE.getApplicationContext(), 
				"prod-ro-memcache", "crnselect", "PROD");
	}
	private boolean supportedProdHost() {
		try { 
			String host= InetAddress.getLocalHost().getHostName();
			return SUPPORTED_HOSTS.valueOf(host)!=null;
		} catch(Exception e) {
			return false;
		}
	}
	@Test
	public void testProfilesFromXml() {
		String profile = "unittest";
		GenericXmlApplicationContext context = createXmlContext(profile);
		validateContextIntegrity(context, profile, "crnunittest", "DEV");

		// Subsequent lines in this test won't run on machines not specified
		// to contain production database configuration
		org.junit.Assume.assumeTrue(supportedProdHost());
		
		// first test one without providing profile
		context = createXmlContext(null);
		// prod-ro is default profile, but active profile is empty
		validateContextIntegrity(context, "", "crnselect", "PROD");
		
		profile = "prod";
		context = createXmlContext(profile);
		validateContextIntegrity(context, profile, "crn", "PROD");
		
		profile = "prod-ro";
		context = createXmlContext(profile);
		validateContextIntegrity(context, profile, "crnselect", "PROD");

		profile = "prod-ro-memcache";
		context = createXmlContext(profile);
		validateContextIntegrity(context, profile, "crnselect", "PROD");
		
		profile = "test-rw";
		context = createXmlContext(profile);
		validateContextIntegrity(context, profile, "crn", "TEST");
		
		profile = "test-ro";
		context = createXmlContext(profile);
		validateContextIntegrity(context, profile, "crnselect", "TEST");
		
		profile = "dev-rw";
		context = createXmlContext(profile);
		validateContextIntegrity(context, profile, "crn", "DEV");
		
		profile = "dev-ro";
		context = createXmlContext(profile);
		validateContextIntegrity(context, profile, "crnselect", "DEV");
		
		profile = "custom";
		context = createXmlContext(profile);
		validateContextIntegrity(context, profile, "afey", "DEV");
	}
	private GenericXmlApplicationContext createXmlContext(String profile) {
		GenericXmlApplicationContext context = new GenericXmlApplicationContext();
		if (profile!=null) {
			context.getEnvironment().setActiveProfiles(profile);
		}
		context.load("application-context.xml");
		context.refresh();
		return context;
	}
	private void validateContextIntegrity(ApplicationContext context,
			String profile, String expectedUser, String expectedDb) {
		// profile is active
		assertTrue(context.getEnvironment().toString()
				.contains("activeProfiles=["+profile+"]"));
		// username is correct and database correctly chosen
		checkDataSourceConfig(context, expectedUser, expectedDb);
		// connection to database succeeds
		testConnection(context);
		// beans retrievable by name and class
		assertBeanExists(context, "stationService");
		assertBeanExists(context, JdbcTemplate.class);
	}
	private static void testConnection(ApplicationContext context) {
		ConnectionDao connDao = context.getBean(ConnectionDao.class);
		assertTrue("did not successfully validate connection", connDao.testConnection());
	}
	private static void
	checkDataSourceConfig(ApplicationContext context, String expectedUser, String expectedDb) {
		BasicDataSource datasource = context.getBean(BasicDataSource.class);
		assertEquals("incorrect username", expectedUser, datasource.getUsername());
		String database = datasource.getUrl();
		// expecteddb in dev, prod, test
		switch(expectedDb) {
		case "DEV":
			assertTrue("wrong database", database.contains("-d.ncdc.noaa.gov"));
			break;
		case "TEST":
			assertTrue("wrong database", database.contains("-t.ncdc.noaa.gov"));
			break;
		case "PROD":
			assertTrue("wrong database", database.contains(".ncdc.noaa.gov")
					&& database.indexOf("-")<0);
			break;
		default:
			fail("unsupported database option");
			break;
		}
	}
	private void assertBeanExists(ApplicationContext context, String name) {
		assertNotNull(name+" not found in "+context.getDisplayName(), 
				context.getBean(name));
	}
	@SuppressWarnings("unchecked")
	private void assertBeanExists(ApplicationContext context, 
			@SuppressWarnings("rawtypes") Class clazz) {
		assertNotNull(clazz+" not found in "+context.getDisplayName(), 
				context.getBean(clazz));
	}

}
