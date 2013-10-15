package gov.noaa.ncdc.crn.util;

import java.math.BigDecimal;
import java.util.BitSet;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Ordering;

public class MathUtils {

    private static final Log LOGGER = LogFactory.getLog(MathUtils.class);

    /**
     * <p>
     * Beginning in 2012 rounding in CRN will be in the positive direction. For now, rounding in CRN is symmetric
     * half-up rounding. Rounding is done with decimals in this method by using a string representation of the number to
     * be rounded to the number of places past the decimal. If places is zero, rounding will be to the nearest integer.
     * If it is negative, rounding will be to the appropriate place to the left of the decimal point. Note that it is
     * possible to return a string representation in scientific notation.
     * </p>
     * <p>
     * All rounding in CRN must be done with the methods in this class.
     * </p>
     * <ul>
     * Examples
     * <li>round("1.234",2) -> "1.23"</li>
     * <li>round("1.235",2) -> "1.24"</li>
     * <li>round("-1.234",2 ) -> "-1.23"</li>
     * <li>round("-1.235",2) -> "-1.24"</li>
     * <li>round("-75.78",0) -> "-76"</li>
     * <li>round("-75.78",-1) -> "-8E+1"</li>
     * <li>round("-75.78",-2) -> "-1E+2"</li>
     * </ul>
     * <p>
     * See also the <a href="https://local.ncdc.noaa.gov/wiki/index.php/CRN:Rounding"> CRN wiki page on rounding</a>.
     * </p>
     * @param value A string representation of the number to be rounded.
     * @param places The number of places past the decimal to round to.
     * @return A string representation of the rounded number.
     * @throws NullPointerException if value is null
     * @throws NumberFormatException if value is empty or otherwise not a number
     */
    public final static String round(String value, int places) {
        Preconditions.checkNotNull(value, "value is null");
        return round(new BigDecimal(value), places).toString();
    }

    /**
     * <p>
     * Beginning in September 2012, rounding in CRN is assymmetric half-up rounding always toward positive infinity.
     * Rounding is done with decimals in this method by using a String representation of the number to be rounded to the
     * number of places past the decimal. If the number of places is zero, rounding will be to the nearest integer. If
     * it is negative, rounding will be to the appropriate place to the left of the decimal point. Note that it is
     * possible to return a string representation in scientific notation.
     * </p>
     * <p>
     * All rounding in CRN must be done with the methods in this class.
     * </p>
     * <ul>
     * Examples
     * <li>round("1.234",2) -> "1.23"</li>
     * <li>round("1.235",2) -> "1.24"</li>
     * <li>round("-1.234",2 ) -> "-1.23"</li>
     * <li>round("-1.235",2) -> "-1.23"</li>
     * <li>round("75.78",0) -> "76"</li>
     * <li>round("-75.78",-1) -> "-7E+1"</li>
     * <li>round("-175.78",-2) -> "-1E+3"</li>
     * </ul>
     * <p>
     * See also the <a href="https://local.ncdc.noaa.gov/wiki/index.php/CRN:Rounding"> CRN wiki page on rounding</a>.
     * </p>
     * @param value A BigDecimal number to be rounded.
     * @param precision The number of places past the decimal to round to.
     * @return A *new* BigDecimal rounded to the proper precision (BigDecimal is immutable).
     * @throws NullPointerException if bd is null
     */
    public final static BigDecimal round(BigDecimal value, int precision) {
        Preconditions.checkNotNull(value, "value to be rounded is null");
        if (value.compareTo(BigDecimal.ZERO) >= 0) {
            value = value.setScale(precision, BigDecimal.ROUND_HALF_UP);
        } else {
            value = value.setScale(precision, BigDecimal.ROUND_HALF_DOWN);
        }
        return value;
    }

    /**
     * Returns the mean of a collection of BigDecimals. Null values are not included in the average, and if no numbers
     * are available for averaging returns null. Rounds according to the CRN rounding specification.
     * @param numbers The collection of numbers to average
     * @param scale The number of decimal places to keep
     * @return The average of the numbers in the collection
     * @throws NullPointerException if collection is null
     */
    public final static BigDecimal average(Collection<BigDecimal> numbers, int scale) {
        Preconditions.checkNotNull(numbers, "Collection to be averaged is null");
        Collection<BigDecimal> filteredNumbers = Collections2.filter(numbers, Predicates.notNull());
        if (filteredNumbers.size() > 0) {
            BigDecimal sum = BigDecimal.ZERO;
            for (BigDecimal number : filteredNumbers) {
                sum = sum.add(number);
            }
            return divide(sum, new BigDecimal(filteredNumbers.size()), scale);
        } else {
            return null;
        }
    }

    /**
     * A Function which returns the average of a Collection<BigDecimal>, allowing it to be combined with other Functions
     * and Preconditions. Rounds according to the CRN rounding specification to one decimal place. This Function simply
     * calls MathUtils.average().
     */
    public static final Function<Collection<BigDecimal>, BigDecimal> AVERAGE_10TH = new Function<Collection<BigDecimal>, BigDecimal>() {
        @Override
        public BigDecimal apply(Collection<BigDecimal> data) {
            return average(data, 1);
        }
    };

    /**
     * A Function which returns the average of a Collection<BigDecimal>, allowing it to be combined with other Functions
     * and Preconditions. Rounds according to the CRN rounding specification to two decimal places. This Function simply
     * calls MathUtils.average().
     */
    public static final Function<Collection<BigDecimal>, BigDecimal> AVERAGE_100TH = new Function<Collection<BigDecimal>, BigDecimal>() {
        @Override
        public BigDecimal apply(Collection<BigDecimal> data) {
            return average(data, 2);
        }
    };

    /**
     * Returns the sum of a Collection{@code <BigDecimals>}. Null values are not included in the sum.
     * @param numbers The collection of numbers to sum
     * @return The sum of the numbers in the collection
     * @throws NullPointerException if collection is null
     */
    public final static BigDecimal sum(Collection<BigDecimal> numbers) {
        Preconditions.checkNotNull(numbers, "Collection to sum is null.");
        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal number : numbers) {
            if (number != null) {
                sum = sum.add(number);
            }
        }
        return sum;
    }

    /**
     * A Function which returns the sum of a Collection{@code <BigDecimal>}, allowing it to be combined with other
     * Functions and Preconditions. This Function simply calls MathUtils.sum().
     */
    public static final Function<Collection<BigDecimal>, BigDecimal> SUM = new Function<Collection<BigDecimal>, BigDecimal>() {
        @Override
        public BigDecimal apply(Collection<BigDecimal> data) {
            return sum(data);
        }
    };

    /**
     * Returns a/b to the desired precision using CRN rounding methodology.
     * @param numerator the numerator of the division problem
     * @param denominator the divisor
     * @param scale The number of decimal places to keep
     * @return a/b
     * @throws NullPointerException if the numerator or denominator is null
     * @throws IllegalArgumentException if the denominator is zero
     */
    public final static BigDecimal divide(BigDecimal numerator, BigDecimal denominator, int scale) {
        Preconditions.checkNotNull(numerator, "Numerator is null");
        Preconditions.checkNotNull(denominator, "Denominator is null");
        Preconditions.checkArgument(!BigDecimal.ZERO.equals(denominator), "Attempting to divide by zero");
        // ^ is xor operator
        if (numerator.compareTo(BigDecimal.ZERO) < 0 ^ denominator.compareTo(BigDecimal.ZERO) < 0) {
            // only one is negative, the results must be negative, so round toward
            // zero (half_down rounds down in magnitude)
            return numerator.divide(denominator, scale, BigDecimal.ROUND_HALF_DOWN);
        } else {
            // the results must be positive, so round up in magnitude
            return numerator.divide(denominator, scale, BigDecimal.ROUND_HALF_UP);
        }
    }

    /**
     * Returns the smallest decimal that was found in the given Collection or <code>null</code> if the Collection is
     * empty or if all members of the Collection are null. Nulls will otherwise be excluded from consideration.
     * @param decimals The BigDecimals to search through.
     * @return The smallest value found.
     * @throws NullPointerException if collection is null
     */
    public final static BigDecimal minimum(Collection<BigDecimal> decimals) {
        Preconditions.checkNotNull(decimals, "Collection is null");
        Collection<BigDecimal> filtered = Collections2.filter(decimals, Predicates.notNull());
        return filtered.size() == 0 ? null : Ordering.natural().min(filtered);
    }

    /**
     * Returns the greatest decimal that was found in the given Collection or <code>null</code> if the Collection is
     * empty or if all members of the Collection are null. Nulls will otherwise be excluded from consideration.
     * @param decimals The BigDecimals to search through.
     * @return The greatest value found.
     * @throws NullPointerException if collection is null
     */
    public final static BigDecimal maximum(Collection<BigDecimal> decimals) {
        Preconditions.checkNotNull(decimals, "Collection is null");
        Collection<BigDecimal> filtered = Collections2.filter(decimals, Predicates.notNull());
        return filtered.size() == 0 ? null : Ordering.natural().max(filtered);
    }

    /**
     * <p>
     * Scales a value by a scaling factor and rounds to an integer. Assumes scaling factor is a power of 10.
     * </p>
     * <p>
     * Usage: Used in fixed width products which minimize space by establishing the scale of numeric values and not
     * showing decimals. For example, a field might be defined as "tenths of degrees C", (with a scaling factor of 10)
     * so "4.55 C" would round to "4.6 C" and be displayed as the integer "46".
     * </p>
     * @param value The String representation of the value to scale
     * @param scalingFactor The factor to scale (multiply) by
     * @return The String representation of the scaled integer
     * @throws IllegalArgumentException if scaling factor is not a power of 10
     * @throws NullPointerException if value is null
     * @throws NumberFormatException if value can't be converted to a number
     */
    public final static String scaleToInt(String value, double scalingFactor) {
        Preconditions.checkNotNull(value, "value to scale is null");
        double placesDouble = Math.log10(scalingFactor);
        Preconditions
        .checkArgument(!Double.isNaN(placesDouble), "Invalid scaling factor (NaN). Must be a power of 10.");
        Preconditions.checkArgument(Math.floor(placesDouble) == placesDouble,
                "Invalid scaling factor. Must be a power of 10.");
        int places = (int) placesDouble;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("scaling factor: " + scalingFactor + "; places: " + places);
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.movePointRight(places);
        return round(bd.toString(), 0);
    }

    /**
     * Unscales an integer value by its scaling factor. Assumes scaling factor is a power of 10.
     * <p>
     * Usage: Used to interpret scaled values from fixed width products which minimize space by establishing the scale
     * of numeric values and not showing decimals. For example, a field might be defined as "tenths of degrees
     * C", (with a scaling factor of 10) so "4.55 C" would round to "4.6 C" and be displayed as the integer "46". This
     * method returns the rounded "4.6" from "46"
     * @param value The String representation of the value to scale
     * @param scalingFactor The factor to scale (multiply) by
     * @return The String representation of the scaled integer
     * @throws NumberFormatException if value can't be converted to a number
     * @throws IllegalArgumentException if scaling factor is not a power of 10
     * @throws NullPointerException if value is null
     */
    public final static String scaleFromInt(Integer value, double scalingFactor) {
        Preconditions.checkNotNull(value, "value to scale is null");
        double placesDouble = Math.log10(scalingFactor);
        Preconditions
        .checkArgument(!Double.isNaN(placesDouble), "Invalid scaling factor (NaN). Must be a power of 10.");
        Preconditions.checkArgument(Math.floor(placesDouble) == placesDouble,
                "Invalid scaling factor. Must be a power of 10.");

        int places = (int) placesDouble;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("scaling factor: " + scalingFactor + "; places: " + places);
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.movePointLeft(places);
        return bd.toString();
    }

    /**
     * Transforms a BitSet to its unsigned integer value. For example, 0b011 (as an ordered array, that's [1,1,0])
     * becomes 3 and 0b100 ([0,0,1]) becomes 4.
     * @param bitset The BitSet to transform
     * @return The unsigned integer value of the BitSet
     * @throws NullPointerException if bitset is null
     */
    public final static int bitSetToUnsignedInt(BitSet bitset) {
        Preconditions.checkNotNull(bitset, "BitSet is null");
        int value = 0;
        for (int i = bitset.length() - 1; i >= 0; i--) {
            // shift bits one position to the left
            value = value << 1;
            // add one if the bit in this position is true
            if (bitset.get(i)) {
                value++;
            }
        }
        return value;
    }

    /**
     * Transforms an unsigned integer to a BitSet. For example, 3 becomes 0b011 (as an ordered array, that's [1,1,0])
     * and 4 becomes 0b100 ([0,0,1]).
     * @param intvalue The unsigned integer to transform
     * @return BitSet representation of the unsigned integer
     * @throws IllegalArgumentException if intvalue is negative
     */
    public final static BitSet unsignedIntToBitSet(int intvalue) {
        Preconditions.checkArgument(intvalue >= 0, "Nonnegative intvalue required");
        assert (intvalue >= 0);
        BitSet bitset = new BitSet();
        int index = 0;
        /*
         * this iterates through the bits in the integer from right to left while the integer is >0 the exit condition
         * is met when all nonzero bits have been shifted out
         */
        while (intvalue > 0) {
            /*
             * if the integer is odd, sets the bit in that position to true; otherwise sets it to false
             */
            bitset.set(index, intvalue % 2 > 0);
            index++;
            /*
             * shifts the bits of the integer one position to the left; that way we're always testing the right-most bit
             */
            intvalue = intvalue >>> 1;
        }
        return bitset;
    }
}
