package gov.noaa.ncdc.crn.util;

import java.math.BigDecimal;

public class ConversionUtils {

    private static double inches_per_mm = 1.0 / 25.4;
    private static BigDecimal C_TO_F_MULT = new BigDecimal("9.0").divide(new BigDecimal("5.0"));
    private static BigDecimal C_TO_F_ADD = new BigDecimal("32.0");
    private static BigDecimal MM_TO_IN_DIV = new BigDecimal("25.4");

    /**
     * Transforms a double representation of a Celsius value to a double representation of its corresponding Fahrenheit
     * value.
     * @param c double representation of a Celsius value
     * @return Fahrenheit value
     */
    public static double c_to_f(double c) {
        return 9.0 * c / 5.0 + 32.0;
    }

    /**
     * Transforms a String representation of a Celsius value to a String representation of its corresponding Fahrenheit
     * value to the nearest tenth.
     * @param c String representation of a Celsius value
     * @return Fahrenheit value
     */
    public static String c_to_f(String c) {
        BigDecimal bd_c = new BigDecimal(c);
        BigDecimal f = bd_c.multiply(C_TO_F_MULT).add(C_TO_F_ADD);
        return String.format("%1$3.1f", f);
    }

    /**
     * Transforms a BigDecimal Celsius value to its corresponding BigDecimal Fahrenheit value.
     * @param c Celsius value
     * @return Fahrenheit value
     */
    public static BigDecimal c_to_f(BigDecimal c) {
        BigDecimal cxmult = c.multiply(C_TO_F_MULT);
        BigDecimal f = cxmult.add(C_TO_F_ADD);
        return f;
    }

    /**
     * Transforms a double representation of a millimeter value to a double representation of its corresponding inches
     * value.
     * @param mm String representation of a millimeter value
     * @return value in inches
     */
    public static double mm_to_inches(double mm) {
        return mm * inches_per_mm;
    }

    /**
     * Transforms a String representation of a millimeter value to a String representation of its corresponding inches
     * value.
     * @param mm String representation of a millimeter value
     * @return value in inches
     */
    public static String mm_to_inches(String mm) {
        BigDecimal bd_mm = new BigDecimal(mm);
        BigDecimal inches = mm_to_inches(bd_mm);
        return inches.toString();
    }

    /**
     * Transforms a BigDecimal millimeter value to its corresponding value in inches.
     * @param mm millimeter value
     * @return value in inches
     */
    public static BigDecimal mm_to_inches(BigDecimal mm) {
        return MathUtils.divide(mm, MM_TO_IN_DIV, 2);
    }
}
