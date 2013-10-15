package gov.noaa.ncdc.crn.spring;

import static org.junit.Assert.assertNotNull;
import gov.noaa.ncdc.crn.domain.Station;
import gov.noaa.ncdc.crn.service.StationService;
import gov.noaa.ncdc.crn.spring.ApplicationContextProvider.Contexts;

import org.junit.Test;
import org.springframework.context.ApplicationContext;

public class ApplicationContextProviderTest {

	@Test
	public void testUnitContext() {
		ApplicationContext context = Contexts.UNIT.getApplicationContext();
		Station station = stationFromContext(context);
		assertNotNull("did not get station from UNIT", station);
	}
	@Test
	public void testProdContext() {
		ApplicationContext context = Contexts.PROD.getApplicationContext();
		Station station = stationFromContext(context);
		assertNotNull("did not get station from PROD", station);
	}
	@Test
	public void testProdROContext() {
		ApplicationContext context = Contexts.PROD_RO.getApplicationContext();
		Station station = stationFromContext(context);
		assertNotNull("did not get station from PROD_RO", station);
	}
	@Test
	public void testProdROMemcacheContext() {
		ApplicationContext context = Contexts.PROD_RO_MEMCACHE.getApplicationContext();
		Station station = stationFromContext(context);
		assertNotNull("did not get station from PROD_RO_MEMCACHE", station);
	}
	private Station stationFromContext(ApplicationContext context) {
		StationService service = context.getBean(StationService.class);
		return service.getStation(1026);
	}

}
