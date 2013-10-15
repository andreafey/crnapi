package gov.noaa.ncdc.crn.dao.mybatis.typehandler;

/**
 * A MyBatis type handler callback to switch between Y, N, C and A, with a default of N.
 * @author Andrea.Fey
 */
public class YNCATypeHandler extends StringTypeHandler {
    final String Y = "Y";
    final String DEFAULT = "N";
    final String C = "C";
    final String A = "A";

    @Override
    String dbToJava(String s) {
        switch (s) {
        case Y:
            return Y;
        case C:
            return C;
        case A:
            return A;
        default:
            return DEFAULT;
        }
    }

    /* An insert of null does not map to DEFAULT */
    @Override
    String javaToDb(String s) {
        return s == null ? s : s.trim();
    }

}