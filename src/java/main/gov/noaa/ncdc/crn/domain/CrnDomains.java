package gov.noaa.ncdc.crn.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.in;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Range;

/**
 * Guava Functions related to CRN domain objects
 * @author Andrea.Fey
 */
public class CrnDomains {

    /**
     * {@code Function} which returns a {@link StationAware}'s stationId
     */
    public static Function<StationAware, Integer> STATION_ID = new Function<StationAware, Integer>() {
        @Override
        public Integer apply(StationAware sa) {
            return sa.getStationId();
        }
    };
    /**
     * {@code Function} which returns an {@link ElementAware}'s elementId
     */
    public static Function<ElementAware, Integer> ELEMENT_ID = new Function<ElementAware, Integer>() {
        @Override
        public Integer apply(ElementAware element) {
            return element.getElementId();
        }
    };
    /**
     * {@code Function} which returns an {@link DatetimeAware}'s datetimeId
     */
    public static Function<DatetimeAware, Integer> DATETIME_ID = new Function<DatetimeAware, Integer>() {
        @Override
        public Integer apply(DatetimeAware datetime) {
            return datetime.getDatetimeId();
        }
    };

    /**
     * Composes a {@code Predicate<V>} which evaluates to true if {@code Function<V,T>} (ELEMENT_ID, for example) is in
     * the {@code Collection<T>}. Usage: Filter a {@code Collection<ExceptionResolutionFact>} based on resulting
     * {@code Predicate}.
     * Example: Filter {@link ExceptionResolutionFact}s for a stations, datetimes, or elements
     * 
     * <pre>
     * <code>
     * List{@code <ExceptionResolutionFact>} facts = ...; // get a bunch to conserve db hits
     * 
     * // All the facts for a single datetime using FluentIterable
     * List{@code <ExceptionResolutionFact>} filtered = FluentIterable.from(facts)
     *         .filter(matches(DATETIME_ID, 100000)).toList();
     * 
     * // Although if you're applying just one filter, it may be simpler this way
     * Collection{@code <ExceptionResolutionFact>} filtered2 =
     *         Collections2.filter(facts, matches(DATETIME_ID, 100000));
     * 
     * // All the facts for datetime/elements
     * filtered = FluentIterable.from(facts)
     *         .filter(matches(DATETIME_ID, 100000, 100001))
     *         .filter(matches(ELEMENT_ID, 440)).toList();
     * 
     * // all the elements #439 which have null values
     * filtered = FluentIterable.from(facts)
     *         .filter(matches(ELEMENT_ID, 439))
     *         .filter(ElementValues.NULL_VALUE).toList();
     * 
     * // all the ExceptionResolutionFacts for a single fact
     * // in this case, there can be multiple resolved exceptions on a fact
     * filtered = FluentIterable.from(facts)
     *         .filter(matches(DATETIME_ID, 100000))
     *         .filter(matches(ELEMENT_ID, 440))
     *         .filter(matches(STATION_ID, 1026)).toList();
     * </code>
     * </pre>
     * 
     * Example: Filter ElementValues for a single station/datetime/element
     * 
     * <pre>
     * <code>
     * List{@code <ElementValue>} vals = ...;
     * // when you expect exactly one (not nullable) to be found
     * ElementValue exactlyOne = FluentIterable.from(facts)
     *         .filter(matches(DATETIME_ID, 100000))
     *         .filter(matches(ELEMENT_ID, 440))
     *         .filter(matches(STATION_ID, 1026)).first().get();
     * // same as above, but ElementValue now null if not present
     * ElementValue nullableEv = FluentIterable.from(facts)
     *         .filter(matches(DATETIME_ID, 100000))
     *         .filter(matches(ELEMENT_ID, 440))
     *         .filter(matches(STATION_ID, 1026)).first().orNull();
     * </code>
     * </pre>
     * @param function The function to apply
     * @param matches The Collection of members to match to
     * @return Predicate<V> which evaluates to true if f(V) is in the Collection
     */
    public static <T, V> Predicate<V> matches(final Function<V, T> function, final Collection<T> matches) {
        checkNotNull(matches, "matches argument not nullable.");
        // applies Predicates.in to function results
        return compose(in(matches), function);
    }
    /**
     * Composes a {@code Predicate<V>} which evaluates to true if {@code Function<V,T>} (ELEMENT_ID, for example) is in
     * the {@code Range<T>}.
     * 
     * Usage: Filter a {@code Collection<ExceptionResolutionFact>} based on resulting Predicate.
     * Example: Filter {@link ExceptionResolutionFact}s for a stations, datetimes, or elements
     * 
     * <pre>
     * <code>
     * List{@code <ExceptionResolutionFact>} facts = ...; // get a bunch to conserve db hits
     * 
     * // All the facts for a datetime range over 2 days
     * Range{@code <Integer>} datetimeRange = Range.closed(70001, 70048);
     * filtered = FluentIterable.from(facts)
     *         .filter(matches(DATETIME_ID, datetimeRange))
     *         .toList();
     * </code>
     * </pre>
     * 
     * @param function The function to apply
     * @param range The {@code Range} of members to match to; type must extend {@code Comparable}
     * @return {@code Predicate<V>} which evaluates to true if f(V) is in the {@code Collection}
     */
    public static <T extends Comparable<T>, V> Predicate<V> matches(final Function<V, T> function, final Range<T> range) {
        checkNotNull(range, "range argument not nullable.");
        // applies Predicates.in to function results
        return compose(range, function);
    }

    @SafeVarargs
    // Just calls overloaded typesafe function with a Collection<T>
    public static <T, V> Predicate<V> matches(final Function<V, T> function, final T... matches) {
        return matches(function, Arrays.asList(matches));
    }

}
