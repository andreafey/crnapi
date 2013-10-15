package gov.noaa.ncdc.crn.util;

import com.google.common.base.Predicate;

public class CrnPredicates {

    /** returns true for positive integers */
    // note not checking for null values because it's known that args converted
    // from int[]
    public static Predicate<Integer> isPositive = new Predicate<Integer>() {
        @Override
        public boolean apply(Integer value) {
            return value > 0;
        }
    };
}
