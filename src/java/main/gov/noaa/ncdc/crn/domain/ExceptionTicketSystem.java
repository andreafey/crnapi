package gov.noaa.ncdc.crn.domain;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * List of possible systems containing detailed information about {@code CrnException}s. For now, there is only trac
 * @author Andrea Fey
 */
public enum ExceptionTicketSystem {
    TRAC("http://crntools.cms-b.ncdc.noaa.gov/trac/exceptions", "ticket", ParamStyle.REST);

    private final String locator;
    private final String paramName;
    private final ParamStyle style;

    private enum ParamStyle {
        REST, QUERY
    }

    ExceptionTicketSystem(final String locator, final String paramName, final ParamStyle style) {
        this.locator = locator;
        this.paramName = paramName;
        this.style = style;
    }

    /*
     * Note that if we had a system which required query-parameter-style support, this would need to be changed.
     */
    public URL getURL(final String paramValue) {
        try {
            switch (style) {
            case REST:
                return new URL(locator + "/" + paramName + "/" + paramValue);
            case QUERY:
                return new URL(locator + "?" + paramName + "=" + paramValue);
            default:
                // impossible to get here unless add new param styles
                throw new RuntimeException("Unsupported param style.");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed URL caught");
        }
    }
}
