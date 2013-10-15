package gov.noaa.ncdc.crn.domain;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public class Elements {

    /**
     * Function which returns an Element's name
     * Example
        List<String> names = FluentIterable.from(elements)
                .transform(Elements.ELEMENT_NAME).toList();
     */
    public static Function<Element, String> ELEMENT_NAME = new Function<Element, String>() {
        @Override
        public String apply(Element element) {
            return element.getName();
        }
    };
    /**
     * Predicate which is true when an Element is calculated
     */
    public static Predicate<Element> IS_CALCULATED = new Predicate<Element>() {
        @Override
        public boolean apply(Element element) {
            return element.isCalculated();
        }
    };
    /**
     * Function which returns an Element's duration in minutes
     */
    public static Function<Element, Integer> DURATION = new Function<Element, Integer>() {
        @Override
        public Integer apply(Element element) {
            return element.getDuration();
        }
    };

}
