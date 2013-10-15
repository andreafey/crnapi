package gov.noaa.ncdc.crn.domain;

import static gov.noaa.ncdc.crn.domain.CrnDomains.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

public class CrnDomainsTest {

	@Test
	public void testMatchesFOfVTTArray() {
		Observation ob1 = mockOb(1, 1001);
		Observation ob2 = mockOb(1, 1002);
		Observation ob3 = mockOb(2, 1001);
		Observation ob4 = mockOb(2, 1002);
		Observation ob5 = mockOb(1, 1003);
		Observation ob6 = mockOb(3, 1001);
		
		Collection<Observation> obs = Lists.newArrayList(ob1, ob2, ob3, ob4, ob5, ob6);
		Predicate<StationAware> predicate = CrnDomains.matches(STATION_ID, 2, 3);
		Collection<Observation> filtered = Collections2.filter(obs, predicate);
		assertEquals("expected 3 obs for stations 2&3", 3, filtered.size());
	}
	@Test
	public void testFilterExceptionFacts() {
		CrnExceptionFact fact1 = mockExFact(1, 1000, 439);
		CrnExceptionFact fact2 = mockExFact(1, 1000, 440);
		CrnExceptionFact fact3 = mockExFact(2, 1000, 439);
		CrnExceptionFact fact4 = mockExFact(2, 1000, 440);
		CrnExceptionFact fact5 = mockExFact(1, 1001, 439);
		CrnExceptionFact fact6 = mockExFact(1, 1001, 440);
		List<CrnExceptionFact> facts = Lists.newArrayList(fact1, fact2, fact3, 
				fact4, fact5, fact6);
		
		CrnExceptionFact found = findExceptionFact(facts, 1, 1000, 439);
		assertEquals("facts differ", fact1, found);
		found = findExceptionFact(facts, 2, 1001, 440);
		assertNull("fact not expected", found);
		found = findExceptionFact(facts, 2, 1000, 440);
		assertEquals("facts differ (fact4)", fact4, found);
		
		Collection<CrnExceptionFact> filtered = filterExceptionFacts(facts, 1, 1000);
		assertEquals("expected 2 facts", 2, filtered.size());
		filtered = filterExceptionFacts(facts, 2, 1001);
		assertEquals("expected 0 facts", 0, filtered.size());
	}

	private CrnExceptionFact findExceptionFact(Collection<CrnExceptionFact> facts, 
			int stationId, int datetimeId, int elementId) {
		return FluentIterable.from(facts)
				.filter(matches(STATION_ID, stationId))
				.filter(matches(DATETIME_ID, datetimeId))
				.filter(matches(ELEMENT_ID, elementId)).first().orNull();
	}
	private Collection<CrnExceptionFact> filterExceptionFacts(Collection<CrnExceptionFact> facts, 
			int stationId, int datetimeId) {
		return FluentIterable.from(facts)
				.filter(matches(STATION_ID, stationId))
				.filter(matches(DATETIME_ID, datetimeId)).toList();
	}

	private static CrnExceptionFact 
	mockExFact(int stationId, int datetimeId) {
		CrnExceptionFact fact = mock(CrnExceptionFact.class);
		when(fact.getDatetimeId()).thenReturn(datetimeId);
		when(fact.getStationId()).thenReturn(stationId);
		return fact;
	}
	private static CrnExceptionFact 
	mockExFact(int stationId, int datetimeId, int elementId) {
		CrnExceptionFact fact = mockExFact(stationId, datetimeId);
		when(fact.getElementId()).thenReturn(elementId);
		return fact;
	}
	private CrnExceptionFact mockXFact(int stationId, int datetimeId, int elementId) {
		CrnExceptionFact xfact = mockXFact(stationId, datetimeId);
		when(xfact.getElementId()).thenReturn(elementId);
		return xfact;
	}
	private CrnExceptionFact mockXFact(int stationId, int datetimeId) {
		CrnExceptionFact xfact = mock(CrnExceptionFact.class);
		addStation(stationId, xfact);
		addDatetime(datetimeId, xfact);
		return xfact;
	}

	private ExceptionResolutionFact mockXRF(int stationId, int datetimeId, int elementId) {
		ExceptionResolutionFact xrf = mock(ExceptionResolutionFact.class);
		addStation(stationId, xrf);
		addDatetime(datetimeId, xrf);
		when(xrf.getElementId()).thenReturn(elementId);
		return xrf;
	}
	private ElementValue mockEV(int stationId, int datetimeId) {
		ElementValue fact = mock(ElementValue.class);
		addStation(stationId, fact);
		addDatetime(datetimeId, fact);
		return fact;
	}
	private Observation mockOb(int stationId, int datetimeId) {
		Observation ob = mock(Observation.class);
		addStation(stationId, ob);
		addDatetime(datetimeId, ob);
		return ob;
	}

	private void addStation(int stationId, StationAware aware) {
		when(aware.getStationId()).thenReturn(stationId);
	}

	private void addDatetime(int datetimeId, DatetimeAware aware) {
		when(aware.getDatetimeId()).thenReturn(datetimeId);
	}
	private ObservationWithData mockOwd(int stationId, int datetimeId) {
		ObservationWithData ob = mock(ObservationWithData.class);
		addStation(stationId, ob);
		addDatetime(datetimeId, ob);
		return ob;
	}

}
