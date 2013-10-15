package gov.noaa.ncdc.crn.domain;

import com.google.common.base.Predicate;

public class ElementValues {

    /**
     * Predicate which returns true when {@link gov.noaa.ncdc.crn.domain.ElementValue#value ElementValue.value} is
     * {@code null}
     */
    public static Predicate<ElementValue> NULL_VALUE = new Predicate<ElementValue>() {
        @Override
        public boolean apply(final ElementValue value) {
            return value.getValue() == null;
        }
    };

}
