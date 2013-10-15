package gov.noaa.ncdc.crn.persistence;

import gov.noaa.ncdc.crn.domain.Datetime;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

public interface DatetimeMapper {

    public Integer selectDatetimeIds(@Param("datestring") final String yyyymmddhh);

    public List<Integer> selectDatetimeIds(@Param("begin") String beginDate, @Param("end") String endDate);

    public Datetime selectDatetimes(@Param("datestring") final String yyyymmddhh);

    public Datetime selectDatetimes(@Param("datetimeId") final int datetimeId);

    public List<Datetime> selectDatetimes(@Param("begin") final String begin, @Param("end") final String end);

    @MapKey("datetimeId")
    public Map<Integer, Datetime> selectDatetimes(@Param("datetimeIds") final Collection<Integer> datetimeIds);

    public void insertDatetime(@Param("datetimeId") final int datetimeId, @Param("year") final int year,
            @Param("doy") final int doy, @Param("ztime") final int ztime,
            @Param("datetime1_24") final String datetime1_24, @Param("utcDatestring") final String utcDatestring);

    public Datetime selectLastDatetime();

}
