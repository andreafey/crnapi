package gov.noaa.ncdc.crn.domain;

import static gov.noaa.ncdc.crn.domain.CrnDomains.DATETIME_ID;
import static gov.noaa.ncdc.crn.domain.CrnDomains.ELEMENT_ID;
import static gov.noaa.ncdc.crn.domain.CrnDomains.STATION_ID;
import static gov.noaa.ncdc.crn.domain.CrnDomains.matches;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;

public class ElementValuesTest {

    // turn this into an example
    //    public static Collection<ElementValue> filterStationDatetime(Collection<ElementValue> values, int stationId, int datetimeId) {
    //    	return FluentIterable.from(values)
    //    			.filter(matches(STATION_ID, stationId))
    //    			.filter(matches(DATETIME_ID, datetimeId))
    //    			.toList();
    //    }


    @Test
    public void testMatches() {
        ElementValue ev1 = mock(ElementValue.class);
        when(ev1.getStationId()).thenReturn(1326);
        ElementValue ev2 = mock(ElementValue.class);
        when(ev2.getStationId()).thenReturn(1326);
        ElementValue ev3 = mock(ElementValue.class);
        when(ev3.getStationId()).thenReturn(1026);
        ElementValue ev4 = mock(ElementValue.class);
        when(ev4.getStationId()).thenReturn(8484);
        Collection<ElementValue> unfiltered =
                Lists.newArrayList(ev1, ev2, ev3, ev4);

        Predicate<StationAware> matches = CrnDomains.matches
                (STATION_ID, Lists.newArrayList(1326, 1026));
        Collection<ElementValue> filtered = Collections2.filter(unfiltered, matches);
        assertEquals("expected three filtered ElementValues for stations 1326 & 1026",
                3, filtered.size());

        matches = CrnDomains.matches(STATION_ID, 1326);
        filtered = Collections2.filter(unfiltered, matches);
        assertEquals("expected 2 filtered ElementValues for station 1326",
                2, filtered.size());

        matches = CrnDomains.matches(STATION_ID, 1026, 8484);
        filtered = Collections2.filter(unfiltered, matches);
        assertEquals("expected 2 filtered ElementValues for stations 1026 & 8484",
                2, filtered.size());
    }
    @Test
    public void testFluentUsage() {
        ElementValue ev1 = mock(ElementValue.class);
        when(ev1.getDatetimeId()).thenReturn(70000);
        when(ev1.getElementId()).thenReturn(101);
        ElementValue ev2 = mock(ElementValue.class);
        when(ev2.getDatetimeId()).thenReturn(70000);
        when(ev2.getElementId()).thenReturn(102);
        ElementValue ev3 = mock(ElementValue.class);
        when(ev3.getDatetimeId()).thenReturn(70001);
        when(ev3.getElementId()).thenReturn(101);
        ElementValue ev4 = mock(ElementValue.class);
        when(ev4.getDatetimeId()).thenReturn(70002);
        when(ev4.getElementId()).thenReturn(101);
        Collection<ElementValue> unfiltered =
                Lists.newArrayList(ev1, ev2, ev3, ev4);

        Collection<ElementValue> filtered = FluentIterable.from(unfiltered)
                .filter(matches(ELEMENT_ID, 101))
                .filter(matches(DATETIME_ID, 70000))
                .toList();
        assertEquals("only one result for this element/datetime",
                1, filtered.size());

        Range<Integer> datetimeRange = Range.closed(70000,70001);
        filtered = FluentIterable.from(unfiltered)
                .filter(matches(ELEMENT_ID, 101))
                .filter(matches(DATETIME_ID, datetimeRange))
                .toList();
        assertEquals("expected 2 result for this element/datetime range",
                2, filtered.size());
    }
    /*
	@Test
	public void testFilterNulls() {
		ElementValue ev1 = mock(ElementValue.class);
		when(ev1.getValue()).thenReturn("1.2");
		ElementValue ev2 = mock(ElementValue.class);
		when(ev2.getValue()).thenReturn("3.0");
		ElementValue ev3 = mock(ElementValue.class);
		when(ev3.getValue()).thenReturn(null);
		Collection<ElementValue> unfiltered = Lists.newArrayList(ev1, ev2, ev3);

		Collection<ElementValue> filtered =
				Collections2.filter(unfiltered, Predicates.not(NULL_VALUE));
		assertEquals("expected null EV to be filtered from collection",
				2, filtered.size());
	} */

}
