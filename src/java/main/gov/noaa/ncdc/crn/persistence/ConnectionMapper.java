package gov.noaa.ncdc.crn.persistence;

public interface ConnectionMapper {
    /**
     * Returns 1 if getting a proper database connection
     * @return 1
     */
    public int test();
}
