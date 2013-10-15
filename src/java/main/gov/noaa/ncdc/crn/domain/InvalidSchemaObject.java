package gov.noaa.ncdc.crn.domain;

/**
 * Domain object to represent an item in USER_TABLES which is INVALID (e.g. does not compile.)
 * @author Andrea Fey
 */
public class InvalidSchemaObject {

    /** Database object name */
    private final String name;
    /**
     * Oracle database object type MATERIALIZED VIEW, VIEW, PROCEDURE, PACKAGE BODY, etc.
     */
    private final String type;

    public InvalidSchemaObject(final String name, final String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
