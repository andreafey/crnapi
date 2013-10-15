package gov.noaa.ncdc.crn.domain;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Test;

import com.google.common.collect.Lists;

public class ExceptionsTest {

	@Test
	public void testFilterResolutionFactsForResolution() {
		Collection<ExceptionResolutionFact> facts = 
				Lists.newArrayList(mockFact(25), mockFact(22), mockFact(22), mockFact(30));
		Collection<ExceptionResolutionFact> filtered =
				Exceptions.filterResolutionFactsForResolution(facts, 22);
		assertEquals("expected 2 for id=22", 2, filtered.size());
		filtered = Exceptions.filterResolutionFactsForResolution(facts, 30);
		assertEquals("expected 1 for id=30", 1, filtered.size());
		filtered = Exceptions.filterResolutionFactsForResolution(facts, 25);
		assertEquals("expected 1 for id=25", 1, filtered.size());
	}
	

	private static ExceptionResolutionFact 
	mockFact(int stationId, int datetimeId) {
		ExceptionResolutionFact fact = mock(ExceptionResolutionFact.class);
		when(fact.getDatetimeId()).thenReturn(datetimeId);
		when(fact.getStationId()).thenReturn(stationId);
		return fact;
	}
	private static ExceptionResolutionFact 
	mockFact(int stationId, int datetimeId, int elementId) {
		ExceptionResolutionFact fact = mockFact(stationId, datetimeId);
		when(fact.getElementId()).thenReturn(elementId);
		return fact;
	}
	private static ExceptionResolutionFact mockFact(int resolutionId) {
		ExceptionResolutionFact fact = mock(ExceptionResolutionFact.class);
		when(fact.getResolutionId()).thenReturn(resolutionId);
		return fact;
	}

}
