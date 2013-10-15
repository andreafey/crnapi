package gov.noaa.ncdc.crn.util;

import gov.noaa.ncdc.crn.domain.Datetime;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TimeUtils {
    protected static final Log LOGGER = LogFactory.getLog(TimeUtils.class);

    /**
     * The CRN Epoch is defined as October 4, 2000; 9:00:00 a.m. This number represents the UNIX time (milliseconds
     * after 1/1/1970).
     */
    public static final long CRN_EPOCH = 970650000000L;

    /** All minutes have this many milliseconds except the last minute of the day on a day defined with a leap second. */
    public static final long MILLISECS_PER_MINUTE = 60 * 1_000;
    /** Number of milliseconds per hour, except when a leap second is inserted. */
    public static final long MILLISECS_PER_HOUR = 60 * MILLISECS_PER_MINUTE;
    /**
     * Number of leap seconds per day except on
     * <ol>
     * <li>days when a leap second has been inserted, e.g. 1999 JAN 1.</li>
     * <li>Daylight-savings "spring forward" or "fall back" days.</li>
     * </ol>
     */
    protected static final long MILLISECS_PER_DAY = 24 * MILLISECS_PER_HOUR;

    public static String getYYYYMMDDHH24mm(Calendar cal) {
        return String.format("%1$tY%1$tm%1$td%1$tH%1$tM", cal);
    }

    /**
     * Returns a String in the format YYYYMMDD of the day associated with the observation hour represented by Calendar
     * argument. For example, the observed hour 2009010100 actually represents an hour in the day 20081231, since the
     * observed hour's timestamp is the end of the last hour of that day. This utility is useful for determining, for
     * example, on what day a Station's period of record begins.
     * @param cal a representation of the end of the hour being observed
     * @return String in the format YYYYMMDD of the day associated with the observation hour
     */
    public static String getObservationDay(Calendar cal) {
        cal.add(Calendar.HOUR, -1);
        String obDay = TimeUtils.getYYYYMMDDHH24mm(cal).substring(0, 8);
        cal.add(Calendar.HOUR, 1);
        return obDay;
    }

    /**
     * Returns an int representing of the day of year associated with the observation hour represented by Calendar
     * argument. For example, the observed hour 2009010100 actually represents an hour in the day 20081231, since the
     * observed hour's timestamp is the end of the last hour of that day. This utility is useful for determining, for
     * example, on what day of year an observation falls.
     * @param cal a representation of the end of the hour being observed
     * @return int representing the day of year associated with the ob hour
     */
    public static int getObservationDayOfYear(Calendar cal) {
        cal.add(Calendar.HOUR, -1);
        int obDoy = cal.get(Calendar.DAY_OF_YEAR);
        cal.add(Calendar.HOUR, 1);
        return obDoy;
    }

    /**
     * Returns a String in the format YYYYMMDD of the day associated with the observation hour represented by yyyymmddhh
     * For example, the observed hour 2009010100 actually represents an hour in the day 20081231, since the observed
     * hour's timestamp is the end of the last hour of that day. This utility is useful for determining, for example, in
     * what day a station's period of record begins
     * @param yyyymmddhh a representation of the end of the hour being observed
     * @return String in the format YYYYMMDD of the day associated with the observation hour
     */
    public static String getObservationDay(String yyyymmddhh) {
        Calendar utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utcCal.set(new Integer(yyyymmddhh.substring(0, 4)), new Integer(yyyymmddhh.substring(4, 6)) - 1, new Integer(
                yyyymmddhh.substring(6, 8)), new Integer(yyyymmddhh.substring(8, 10)), 0);
        return getObservationDay(utcCal);
    }

    /**
     * Returns a String in the format YYYYMM of the month associated with the observation hour represented by cal For
     * example, the observed hour 2009010100 actually represents an hour in the month 200812, since the observed hour's
     * timestamp is the end of the last hour of that month. This utility is useful for determining, for example, in what
     * month a station's period of record begins
     * @param cal a representation of the end of the hour being observed
     * @return String in the format YYYYMM of the month associated with the observation hour
     */
    public static String getObservationMonth(Calendar cal) {
        return getObservationDay(cal).substring(0, 6);
    }

    /**
     * Returns a String in the format YYYYMM of the month associated with the observation hour represented by yyyymmddhh
     * For example, the observed hour 2009010100 actually represents an hour in the month 200812, since the observed
     * hour's timestamp is the end of the last hour of that day. This utility is useful for determining, for example, in
     * what month a station's period of record begins
     * @param yyyymmddhh a representation of the end of the hour being observed
     * @return String in the format YYYYMM of the month associated with the observation hour
     */
    public static String getObservationMonth(String yyyymmddhh) {
        return getObservationDay(yyyymmddhh).substring(0, 6);
    }

    /**
     * Returns an int representing the month associated with the yyyymmddhh observation datestring. For example, the
     * hour 2009010100 actually represents an hour in the month 200812, since the observed hour's timestamp is the end
     * of the last hour of that day. This utility would return 12 in that case.
     * @param yyyymmddhh a representation of the end of the hour being observed
     * @return the int month the observation occurs in (Jan=1, Feb=2, ...)
     */
    public static int getObservationMonthInt(String yyyymmddhh) {
        return Integer.valueOf(getObservationMonth(yyyymmddhh).substring(4));
    }

    /**
     * Returns a String in the format YYYY of the year associated with the observation hour represented by cal For
     * example, the observed hour 2009010100 actually represents an hour in the year 2008, since the observed hour's
     * timestamp is the end of the last hour of that year. This utility is useful for determining, for example, in what
     * year a station's period of record begins
     * @param cal a representation of the end of the hour being observed
     * @return String in the format YYYY of the year associated with the observation hour
     */
    public static String getObservationYear(Calendar cal) {
        return getObservationDay(cal).substring(0, 4);
    }

    /**
     * Returns a String in the format YYYY of the year associated with the observation hour represented by yyyymmddhh
     * For example, the observed hour 2009010100 actually represents an hour in the year 2008, since the observed hour's
     * timestamp is the end of the last hour of that year. This utility is useful for determining, for example, in what
     * year a station's period of record begins
     * @param yyyymmddhh a representation of the end of the hour being observed
     * @return String in the format YYYY of the year associated with the observation hour
     */
    public static String getObservationYear(String yyyymmddhh) {
        return getObservationDay(yyyymmddhh).substring(0, 4);
    }

    /**
     * Returns a UTC Calendar represented by a String in the format yyyy[mmddhh24mi]
     * @param date (yyyy,yyyymm,yyyymmdd,yyyymmddhh24,yyyymmddhh24mi)
     * @return Calendar represented by date
     */
    public static Calendar getCalendar(String date) {
        String datetime0_23 = getDatestring(date);
        Calendar cal = createUTCCalendar(datetime0_23);
        return cal;
    }

    /**
     * Returns true if date represents a UTC date that is within the current UTC date's month
     * @param date YYYYMMDD-type datestring
     * @return <code>true</code> if date represents a UTC date that is within the current UTC date's month
     */
    public static boolean isCurrentMonth(String date) {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Calendar arg = TimeUtils.createUTCCalendar(date);
        return isSameMonth(arg, now);
    }

    /**
     * Returns true if date represents a UTC date that is within the current UTC date's week of year
     * @param date YYYYMMDD-type datestring
     * @return <code>true</code> if date is in the current week.
     */
    public static boolean isCurrentWeek(String date) {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Calendar arg = TimeUtils.createUTCCalendar(date);
        return isSameWeek(arg, now);
    }

    /**
     * Returns true if date represents a UTC date that is within the current UTC date's year
     * @param date YYYYMMDD-type datestring
     * @return <code>true</code> if date is in the current year.
     */
    public static boolean isCurrentYear(String date) {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Calendar arg = TimeUtils.createUTCCalendar(date);
        return isSameYear(arg, now);
    }

    /**
     * Returns the number of complete days between date and now. Accounts for daylight sa
     * @param date The yyyy[mmddhh24mi] to consider
     * @return the number of days between date and now
     */
    public static int daysOld(String date) {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Calendar arg = TimeUtils.createUTCCalendar(date);
        return (int) diffDays(arg, now);
    }

    /**
     * <p>
     * Determines the number of complete days from this date to the given end date. Later end dates result in positive
     * values. Note this is not the same as subtracting day numbers. Just after midnight subtracted from just before
     * midnight is 0 days for this method while subtracting day numbers would yields 1 day. Accounts for daylight saving
     * time, so 4 AM local time - 4 AM local time the next day returns 1 whether or not there was an offset shift.
     * </p>
     * <p>
     * This code was authored by goodhill@xmission.com. See <a
     * href="http://www.xmission.com/~goodhill/dates/deltaDates.html">the original article</a> for details and
     * clarification.
     * </p>
     * @param begin - any Calendar representing the moment of time at the beginning of the interval for calculation.
     * @param end - any Calendar representing the moment of time at the end of the interval for calculation.
     * @return The number of complete days between begin and end
     */
    public static long diffDays(Calendar begin, Calendar end) {
        long endL = end.getTimeInMillis() + end.getTimeZone().getOffset(end.getTimeInMillis());
        long startL = begin.getTimeInMillis() + begin.getTimeZone().getOffset(begin.getTimeInMillis());
        return (endL - startL) / MILLISECS_PER_DAY;
    }

    /**
     * <p>
     * Determines the number of complete hours from one UTC date to another. Later end dates result in positive values.
     * Note this is not the same as subtracting hour numbers. Just after the hour subtracted from just before the hour
     * is 0 hours for this method.
     * </p>
     * <p>
     * Based on code authored by goodhill@xmission.com. See <a
     * href="http://www.xmission.com/~goodhill/dates/deltaDates.html">the original article</a> for details and
     * clarification.
     * </p>
     * @param yyyymmddhh0 - any String UTC date representing the moment of time at the beginning of the interval for
     * calculation.
     * @param yyyymmddhh1 - any String date representing the moment of time at the end of the interval for calculation.
     * @return The number of complete hours between begin and end
     */
    public static long diffHours(String yyyymmddhh0, String yyyymmddhh1) {
        Calendar begin = getCalendar(yyyymmddhh0);
        Calendar end = getCalendar(yyyymmddhh1);
        long endL = end.getTimeInMillis() + end.getTimeZone().getOffset(end.getTimeInMillis());
        long startL = begin.getTimeInMillis() + begin.getTimeZone().getOffset(begin.getTimeInMillis());
        return (endL - startL) / MILLISECS_PER_HOUR;
    }

    /**
     * Returns true if the two calendars are in the same year
     * @param cal1 - the first calendar to be compared.
     * @param cal2 - the second calendar to be compared.
     * @return true if the two calendars are in the same year
     */
    public static boolean isSameYear(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    /**
     * Returns true if the two calendars are in the same month of the same year
     * @param cal1 - the first calendar to be compared.
     * @param cal2 - the second calendar to be compared.
     * @return true if the two calendars are in the same month of the same year
     */
    public static boolean isSameMonth(Calendar cal1, Calendar cal2) {
        return isSameYear(cal1, cal2) && cal2.get(Calendar.MONTH) == cal1.get(Calendar.MONTH);
    }

    /**
     * Returns true if the two calendars are in the same week of the same year
     * @param cal1 - the first calendar to be compared.
     * @param cal2 - the second calendar to be compared.
     * @return true if the two calendars are in the same week of the same year
     */
    public static boolean isSameWeek(Calendar cal1, Calendar cal2) {
        return isSameYear(cal1, cal2) && cal2.get(Calendar.WEEK_OF_YEAR) == cal1.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * Returns a yyyymmddhh24mi String from a date in the format yyyy,yyyymm,yyyymmdd,yyyymmddhh24,yyyymmddhh24mi. For
     * example, when date=200801, datestring becomes 200801010000
     * @param date (yyyy,yyyymm,yyyymmdd,yyyymmddhh24,yyyymmddhh24mi)
     * @return yyyymmddhh24mi
     */
    public static String getDatestring(String date) {
        String datestring = null;
        int len = date.length();
        switch (len) {
        case 4:
            datestring = date + "01010000";
            break;
        case 6:
            datestring = date + "010000";
            break;
        case 8:
            datestring = date + "0000";
            break;
        case 10:
            datestring = date + "00";
            break;
        case 12:
            datestring = date;
            break;
        }
        return datestring;
    }

    /**
     * Parses a yyyymmdd datestring from a java.sql.Date. Note that java.sql.Date contains no TimeZone info, so the
     * result is UTC
     * @param date the Date to parse
     * @return a yyyymmdd datestring
     */
    public static String getYYYYMMDD(Date date) {
        return String.format("%1$tY%1$tm%1$td", date);
    }

    /**
     * Creates a YYYYMMDDHH24 String from a Calendar
     * @param cal The Calendar to parse
     * @return a YYYYMMDDHH24 String
     */
    public static String getYYYYMMDDHH24(Calendar cal) {
        return String.format("%1$tY%1$tm%1$td%1$tH", cal);
    }

    /**
     * Creates a YYYYMMDDHH24 UTC String from the year, month, day, hour
     * @param year
     * @param dayOfYear
     * @param hour
     * @return a YYYYMMDDHH24 UTC String
     */
    public static String getYYYYMMDDHH24(int year, int dayOfYear, int hour) {
        Calendar cal = createUTCCalendar(year, dayOfYear, hour);
        return getYYYYMMDDHH24(cal);
    }

    /**
     * Creates a YYYYMMDD String from a Calendar
     * @param cal The Calendar to parse
     * @return a YYYYMMDD String
     */
    public static String getYYYYMMDD(Calendar cal) {
        return String.format("%1$tY%1$tm%1$td", cal);
    }

    /**
     * Creates a Calendar from a YYYYMMDDHH24 String
     * @param yyyymmddhh The String to parse
     * @return a Calendar created from the YYYYMMDDHH24
     */
    public static Calendar createUTCCalendar(String yyyymmddhh) {
        return createCalendar(yyyymmddhh, "UTC");
    }

    /**
     * Creates a Calendar representation of a date string in a time zone. This will properly convert a date string in
     * the format "2010083124" (where the hour can equal 24).
     * @param date in the format yyyymmddhhmi, but as little as the year is required
     * @param timeZoneID a valid java.util.Calendar time zone
     * @return Calendar equivalent of this date and time zone
     */
    public static Calendar createCalendar(String date, String timeZoneID) {
        int year = 0;
        int month = 0;
        int day = 0;
        int hour = 0;
        int minute = 0;
        if (date.length() >= 4) {
            year = Integer.parseInt(date.substring(0, 4));
        }
        if (date.length() >= 6) {
            month = Integer.parseInt(date.substring(4, 6));
        }
        if (date.length() >= 8) {
            day = Integer.parseInt(date.substring(6, 8));
        }
        if (date.length() >= 10) {
            hour = Integer.parseInt(date.substring(8, 10));
        }
        if (date.length() >= 12) {
            minute = Integer.parseInt(date.substring(10, 12));
        }
        return createCalendar(year, month - 1, day, hour, minute, timeZoneID);
    }

    /**
     * Creates a UTC calendar from a Date. Can't create local time zone calendar, because java.sql.Date does not contain
     * time zone information
     * @param date the date to convert
     * @return a UTC calendar representing the same point in time.
     */
    public static Calendar createUTCCalendar(Date date) {
        Calendar cal = nowUTCCalendar();
        cal.getTime().setTime(date.getTime());
        return cal;
    }

    /**
     * Creates a new Calendar representing the same moment in time but in a different timezone. If the timezone does not
     * change, just returns a clone of the argument calendar.
     * @param calendar
     * @param timezone
     * @return a Calendar representing the same moment in time but in a different timezone
     */
    public static Calendar changeTimeZone(Calendar calendar, String timezone) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("changeTimeZone cal before conversion: "
                    + String.format("%1$tY%1$tm%1$td%1$tH (%1$tZ)", calendar));
        }

        Calendar result = new GregorianCalendar(TimeZone.getTimeZone(timezone));
        result.setTimeInMillis(calendar.getTimeInMillis());
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("changeTimeZone cal after conversion: "
                    + String.format("%1$tY%1$tm%1$td%1$tH (%1$tZ)", result));
        }
        return result;
    }

    /**
     * Creates a new calendar and adds hours to it
     * @param cal The base calendar to begin from
     * @param hours The number of hours to add (can be negative)
     * @return a new calendar offset by hours
     */
    public static Calendar addHours(Calendar cal, int hours) {
        Calendar newCal = (Calendar) cal.clone();
        newCal.add(Calendar.HOUR_OF_DAY, hours);
        return newCal;
    }

    /**
     * Creates a YYYYMMDDHH24MI String based on the current local time
     * @return a YYYYMMDDHH24MI String based on the current local time
     */
    public static String nowLocal() {
        Calendar c = nowLocalCalendar();
        return getYYYYMMDDHH24mm(c);
    }

    /**
     * Constructs and retrieves a java.util.GregorianCalendar based on the current local time.
     * @return Calendar based on the current local time
     */
    public static Calendar nowLocalCalendar() {
        return new GregorianCalendar();
    }

    /**
     * Creates a YYYYMMDDHH24MI String based on the current UTC time
     * @return a YYYYMMDDHH24MI String based on the current UTC time
     */
    public static String nowUTC() {
        Calendar c = nowUTCCalendar();
        return getYYYYMMDDHH24mm(c);
    }

    /**
     * Creates a Calendar based on the current UTC time
     * @return a Calendar based on the current UTC time
     */
    public static Calendar nowUTCCalendar() {
        return new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    }

    /**
     * Creates a Calendar with the referenced time values. Note that month must be zero-based, as is the convention in
     * Calendar. Zeroes the milliseconds.
     * @param year
     * @param month 0-based index; Jan=0, Feb=1, etc.
     * @param day
     * @param hour the hour of the day, from 0-24 (0 and 24 being the same hour of the day on consecutive days)
     * @param minute
     * @param timeZoneID The time zone to set the calendar to
     * @return Calendar with the referenced time values
     */
    public static Calendar createCalendar(int year, int month, int day, int hour, int minute, String timeZoneID) {
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone(timeZoneID));
        calendar.set(year, month, day, hour, minute, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("createCalendar(" + year + "," + month + "," + day + "," + hour + "," + minute + "," + "\""
                    + timeZoneID + "\") = " + String.format("%1$tY%1$tm%1$td%1$tH (%1$tZ)", calendar));
        }
        return calendar;
    }

    /**
     * Creates a UTC Calendar with the referenced time values. Zeroes the minutes, seconds, and milliseconds.
     * @param year
     * @param dayOfYear day of year
     * @param hour the hour of the day, from 0-23
     * @return UTC Calendar with the referenced time values
     */
    public static Calendar createUTCCalendar(int year, int dayOfYear, int hour) {
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("createUTCCalendar(" + year + "," + dayOfYear + "," + hour + "\") = "
                    + String.format("%1$tY%1$tm%1$td%1$tH (%1$tZ)", calendar));
        }
        return calendar;
    }

    /**
     * Creates a new calendar which is offset by the other arguments.
     * @param calendar The base calendar to start with
     * @param years The number of years to offset the base calendar
     * @param months The number of months to offset the base calendar
     * @param days The number of days to offset the base calendar
     * @param hours The number of hours to offset the base calendar
     * @param minutes The number of minutes to offset the base calendar
     * @return a new offset calendar
     */
    public static Calendar extendDate(Calendar calendar, int years, int months, int days, int hours, int minutes) {
        Calendar result = (Calendar) calendar.clone();
        result.add(Calendar.YEAR, years);
        result.add(Calendar.MONTH, months);
        result.add(Calendar.DATE, days);
        result.add(Calendar.HOUR, hours);
        result.add(Calendar.MINUTE, minutes);
        return result;
    }

    /**
     * Computes a database datetimeId from a Calendar object. Does not access the database to obtain result. Because
     * datetimeIds represent hourly intervals the minutes, seconds, and milliseconds of the Calendar will be discarded.
     * The resulting integer will represent the hour period in which the calendar time occurs according to CRN.
     * @param calendar The <code>Calendar</code> to compute an equivalent datetimeId for.
     * @return The id of the hour in which the calendar time occurs.
     */
    public static int computeDateTimeId(Calendar calendar) {
        long current = calendar.getTime().getTime();
        long millisDuration = current - CRN_EPOCH;
        long hoursDuration = millisDuration / MILLISECS_PER_HOUR;
        return (int) hoursDuration;
    }

    /**
     * Computes a Calendar from a CRN datetimeId. Does not access the database to obtain result. Because datetimeIds
     * represent hourly intervals the minutes, seconds, and milliseconds of the Calendar will be zero. The resulting
     * Calendar will represent the date at which the datetime occurs.
     * @param datetimeId The datetimeId to compute an equivalent Calendar date for.
     * @return The calendar time for which a datetimeId occurs.
     */
    public static Calendar computeCalendarDate(int datetimeId) {
        long millisDuration = datetimeId * MILLISECS_PER_HOUR;
        long date = CRN_EPOCH + millisDuration;
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(date);
        return calendar;
    }

    /**
     * Converts a local time datestring in one timezone to a datestring in UTC. For example, US/Eastern (daylight saving
     * time) is offset -4 hours from UTC, so convertToUTC("2009050614","US/Eastern") returns "2009050618"
     * @param yyyymmddhh in some time zone
     * @param timeZoneID
     * @return yyyymmddhh in UTC
     */
    public static String convertToUTC(String yyyymmddhh, String timeZoneID) {
        Calendar cal = createCalendar(yyyymmddhh, timeZoneID);
        // TODO THis is documented as LST but not used that way by other programs; create parallel method or something
        // set daylight saving offset to 0 per method documentation
        // cal.set(Calendar.DST_OFFSET, 0);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("convertToUTC(" + yyyymmddhh + ",\"" + timeZoneID + "\") cal before conversion: "
                    + String.format("%1$tY%1$tm%1$td%1$tH (%1$tZ)", cal));
        }
        cal = changeTimeZone(cal, "UTC");
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("cal after conversion: " + String.format("%1$tY%1$tm%1$td%1$tH (%1$tZ)", cal));
        }
        return getYYYYMMDDHH24(cal);
    }

    /**
     * Converts a UTC time datestring to a datestring in local timezone. For example, US/Eastern (daylight time) is
     * offset -4 hours from UTC, so convertFromUTC("2009050619","US/Eastern") returns "2009050614"
     * @param yyyymmddhh in UTC
     * @param timeZoneID
     * @return yyyymmddhh in requested time zone
     */
    public static String convertFromUTC(String yyyymmddhh, String timeZoneID) {
        Calendar cal = createCalendar(yyyymmddhh, "UTC");
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("convertFromUTC(" + yyyymmddhh + ",\"" + timeZoneID + "\") cal before conversion: "
                    + String.format("%1$tY%1$tm%1$td%1$tH (%1$tZ)", cal));
        }
        cal = changeTimeZone(cal, timeZoneID);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("cal after conversion: " + String.format("%1$tY%1$tm%1$td%1$tH (%1$tZ)", cal));
        }
        return getYYYYMMDDHH24(cal);
    }

    /**
     * Converts a *standard* time datestring in one timezone to a datestring in UTC. For example, US/Eastern (standard
     * time) is offset -5 hours from UTC, so convertLSTToUTC("2009050614","US/Eastern") returns "2009050619"
     * @param yyyymmddhh in some time zone
     * @param timeZoneID
     * @return yyyymmddhh in UTC
     */
    public static String convertLSTToUTC(String yyyymmddhh, String timeZoneID) {
        Calendar cal = createCalendar(yyyymmddhh, timeZoneID);
        // set daylight saving offset to 0 per method documentation
        cal.set(Calendar.DST_OFFSET, 0);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("convertToUTC(" + yyyymmddhh + ",\"" + timeZoneID + "\") cal before conversion: "
                    + String.format("%1$tY%1$tm%1$td%1$tH (%1$tZ)", cal));
        }
        cal = changeTimeZone(cal, "UTC");
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("cal after conversion: " + String.format("%1$tY%1$tm%1$td%1$tH (%1$tZ)", cal));
        }
        return getYYYYMMDDHH24(cal);
    }

    /**
     * Converts a UTC time datestring to a datestring in a *standard* timezone. For example, US/Eastern (standard time)
     * is offset -5 hours from UTC, so convertFromUTC("2009050619","US/Eastern") returns "2009050614"
     * @param yyyymmddhh in UTC
     * @param timeZoneID
     * @return yyyymmddhh in requested time zone
     */
    public static String convertLSTFromUTC(String yyyymmddhh, String timeZoneID) {
        Calendar cal = createCalendar(yyyymmddhh, "UTC");
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("convertFromUTC(" + yyyymmddhh + ",\"" + timeZoneID + "\") cal before conversion: "
                    + String.format("%1$tY%1$tm%1$td%1$tH (%1$tZ)", cal));
        }
        cal = changeTimeZone(cal, timeZoneID);
        // if we are in Daylight Saving Time, adjust the time; we're returning a String anyway
        cal.add(Calendar.MILLISECOND, -cal.get(Calendar.DST_OFFSET));
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("cal after conversion: " + String.format("%1$tY%1$tm%1$td%1$tH (%1$tZ)", cal));
        }
        return getYYYYMMDDHH24(cal);
    }

    /**
     * Returns the number of milliseconds since the epoch for a datetime
     * @return milliseconds since the epoch for this Datetime.
     */
    public static long getMsSinceEpoch(Datetime datetime) {
        return datetime.getUtcCal().getTimeInMillis();
    }

}
