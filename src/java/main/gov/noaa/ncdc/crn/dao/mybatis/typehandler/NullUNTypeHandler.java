package gov.noaa.ncdc.crn.dao.mybatis.typehandler;

/**
 * A MyBatis type handler to trim the string and map null values to UN bidirectionally. If a value is <code>null</code>,
 * the resulting String will be 'UN'.
 * @author Andrea.Fey
 */
public class NullUNTypeHandler extends StringTypeHandler {
    private final String DEFAULT = "UN";

    @Override
    String dbToJava(String s) {
        return valueOf(s);
    }

    @Override
    String javaToDb(String s) {
        return valueOf(s);
    }

    private String valueOf(String s) {
        return s == null || s.length() == 0 ? DEFAULT : s.trim();
    }
}