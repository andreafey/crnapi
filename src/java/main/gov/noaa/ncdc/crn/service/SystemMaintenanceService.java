package gov.noaa.ncdc.crn.service;

import gov.noaa.ncdc.crn.domain.Datetime;
import gov.noaa.ncdc.crn.persistence.DatetimeMapper;
import gov.noaa.ncdc.crn.spring.ApplicationContextProvider.Contexts;
import gov.noaa.ncdc.crn.util.TimeUtils;

import java.sql.BatchUpdateException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;

@Service
public class SystemMaintenanceService {

    @Autowired
    private DatetimeMapper datetimeMapper;

    public Datetime getLastDatetime() {
        return datetimeMapper.selectLastDatetime();
    }

    /**
     * Inserts between 1 and 10 years' worth of new Datetimes after the last Datetime in the database.
     * @param years total number of years to insert (between 1 and 10)
     * @throws BatchUpdateException
     */
    @Transactional
    public void insertNewDatetimes(int years) throws BatchUpdateException {
        Preconditions.checkArgument(years > 0 && years < 11, "years must be between 1 and 10");
        Datetime curr = datetimeMapper.selectLastDatetime();
        int endId = calcEndDatetime(years, curr);
        insertDatetimes(curr.next(), endId - curr.getDatetimeId());
    }

    /**
     * Returns a representation of the observation time in the format 201312312400
     * @param cal The observation time
     * @return observation time in the format 201312312400
     */
    private static String getDatetime1_24_00(Calendar cal) {
        String yyyymmdd = TimeUtils.getObservationDay(cal);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if (hour == 0) {
            hour = 24;
        }
        return String.format("%1s%02d00", yyyymmdd, hour);
    }

    /**
     * Calculates long timeInMillis for datetime plus years
     * @param years number of years to add
     * @param datetime beginning datetime
     * @return long timeInMillis for datetime plus years
     */
    private static int calcEndDatetime(int years, Datetime datetime) {
        // using clone() to prevent side effect from Datetime changing
        Calendar endCal = (Calendar) datetime.getUtcCal().clone();
        endCal.add(Calendar.YEAR, years);
        return TimeUtils.computeDateTimeId(endCal);
    }

    /**
     * Inserts datetimes from the application context database into the unittest database
     * @param begin The beginning datetimeId of the range to insert
     * @param end The end datetimeId of the range to insert
     */
    @Transactional
    void insertDatetimeRangeIntoUnitTestFromContext(int begin, int end) {
        int hours = end - begin + 1; // total number of datetimes to insert
        DatetimeMapper unitDbMapper = Contexts.UNIT.getApplicationContext().getBean(DatetimeMapper.class);
        Datetime beginDt = datetimeMapper.selectDatetimes(begin);
        insertDatetimes(beginDt, hours, unitDbMapper);
    }
    @Transactional
    private void insertDatetimes(Datetime beginDt, int hours)
    {
        insertDatetimes(beginDt, hours, datetimeMapper);
    }

    @Transactional
    private static void insertDatetimes(Datetime beginDt, int hours, DatetimeMapper mapper) {
        Preconditions.checkArgument(hours > 0);
        Datetime curr = beginDt;
        int count = 0;
        while (count < hours) {
            try {
                insertDatetime(curr, mapper);
            } catch (DataAccessException | BatchUpdateException e) {
                if (e.getCause().getClass() == SQLIntegrityConstraintViolationException.class) {
                    // if try to insert duplicate
                    System.out.println("constraint violation (duplicate datetime?): " + curr);
                } else {
                    throw new RuntimeException("uncaught exception in insertDatetimes", e);
                }
            }
            curr = curr.next();
            count++;
        }
        System.out.println("inserted " + count + " datetimes, beginning with " + beginDt);
    }

    private static void insertDatetime(Datetime datetime, DatetimeMapper mapper) throws BatchUpdateException {
        String datetime1_24_00 = getDatetime1_24_00(datetime.getUtcCal());
        int ztime = Integer.valueOf(datetime1_24_00.substring(8));
        mapper.insertDatetime(datetime.getDatetimeId(), datetime.getYear(),
                TimeUtils.getObservationDayOfYear(datetime.getUtcCal()), ztime, datetime1_24_00,
                datetime.getDatetime0_23());
    }
}
