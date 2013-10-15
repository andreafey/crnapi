package gov.noaa.ncdc.crn.dao.mybatis.typehandler;

/**
 * A MyBatis type handler callback to switch between Y, N, and E, with a default of N.
 * @author Andrea.Fey
 */
public class YNTETypeHandler extends StringTypeHandler {
    final String Y = "Y";
    final String DEFAULT = "N";
    final String E = "E";
    final String T = "T";

    @Override
    String dbToJava(String s) {
        if (s == null) {
            return DEFAULT;
        }
        switch (s.trim()) {
        case Y:
            return Y;
        case E:
            return E;
        case T:
            return T;
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