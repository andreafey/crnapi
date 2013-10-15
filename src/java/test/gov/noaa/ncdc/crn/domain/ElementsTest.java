package gov.noaa.ncdc.crn.domain;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

public class ElementsTest {

	@Test
	public void test() {
		Element element1 = mock(Element.class);
		when(element1.getName()).thenReturn("FOO");
		when(element1.getDuration()).thenReturn(60);
		when(element1.isCalculated()).thenReturn(true);
		Element element2 = mock(Element.class);
		when(element2.getName()).thenReturn("BAR");
		when(element2.getDuration()).thenReturn(5);
		when(element2.isCalculated()).thenReturn(false);
		List<Element> elements = Lists.newArrayList(element1, element2);
		
		List<String> names = FluentIterable.from(elements)
				.transform(Elements.ELEMENT_NAME).toList();
		assertEquals("didn't get correct names", 
				Lists.newArrayList("FOO","BAR"), names);
		List<Integer> durations = FluentIterable.from(elements)
				.transform(Elements.DURATION).toList();
		assertEquals("didn't get correct durations", 
				Lists.newArrayList(60,5), durations);
		List<Element> calculated = FluentIterable.from(elements)
				.filter(Elements.IS_CALCULATED).toList();
		assertEquals("didn't get correct calculated elements", 
				Lists.newArrayList(element1), calculated);
	}

}
