package gov.noaa.ncdc.crn.domain;

import gov.noaa.ncdc.crn.util.TimeUtils;

import java.io.Serializable;
import java.util.Calendar;
import java.util.TimeZone;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

/**
 * {@code Datetime} represents an observation hour in the CRN network history. The time represents the top of the hour
 * at the end of the observation hour, and so minutes, seconds, and smaller units of time are always zero.
 * 
 * {@code Datetime} provides previous() and next() methods to create sequential {@code Datetime}s, and a self-modifying
 * add(int) method.
 * 
 * It also provides a method to convert its native UTC time to Local Standard Time (LST).
 */
@SuppressWarnings("serial")
public class Datetime implements DatetimeAware, Comparable<Object>, Serializable {

    /**
     * The datetimeId represents a number of hours since a certain time in the CRN network history. Exercise caution
     * when modifying this value to ensure that the id and the date do not get out of sync.
     */
    private final int datetimeId;
    /**
     * datetime0_23 - the date in yyyymmddhhmm format
     */
    private final String datetime0_23;

    /**
     * utcCal is the calendar instance representing datetime0_23
     */
    private final Calendar utcCal;

    /**
     * Creates a new {@code Datetime} object with from the datetime id and UTC datestring. Does not validate agreement
     * against the database.
     * @param datetimeId The datetime id
     * @param yyyymmddhhmm The UTC datetimeString in YYYYMMDDHH24MI format
     */
    public Datetime(final int datetimeId, final String yyyymmddhhmm) {
        this.datetimeId = datetimeId;
        this.datetime0_23 = yyyymmddhhmm;
        utcCal = TimeUtils.createUTCCalendar(datetime0_23);
    }

    /**
     * Returns the datetime id
     * @return the datetime id
     */
    @Override
    public int getDatetimeId() {
        return datetimeId;
    }

    /**
     * Returns a {@code String} that positionally encodes a date and time. The String consists of numeric digits whose
     * positions represent the following: [0-3] represents the four digit year. [4-5] represents the two digit month
     * values (01 through 12). [6-7] represents the two digit day-of-month values (01 through 31). [8-9] represents the
     * two digit hour-of-day values (00-23). [9-10] represents the two digit minute-of-hour values (01-59).
     * @return A {@code String} encoded date and time.
     */
    public String getDatetime0_23() {
        return datetime0_23;
    }

    public Calendar getUtcCal() {
        return utcCal;
    }

    /**
     * Returns a datetime {@code String} representing the offset-adjusted time from datetime0_23; if provided, minutes
     * are translated to 00
     * @param datetime0_23 the starting time in YYYYMMDDH24[mm]
     * @param offset the offset to add to the datetime
     * @return a datetime {@code String} in YYYYMMDDHH2400
     */
    public static String getLstDatetime0_23(String datetime0_23, int offset) {
        Calendar utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utcCal.set(new Integer(datetime0_23.substring(0, 4)), new Integer(datetime0_23.substring(4, 6)) - 1,
                new Integer(datetime0_23.substring(6, 8)), new Integer(datetime0_23.substring(8, 10)), 0);
        utcCal.add(Calendar.HOUR, offset);
        String lst = TimeUtils.getYYYYMMDDHH24mm(utcCal).substring(0, 10);
        return lst;
    }

    /**
     * Returns a datetime {@code String} representing the offset-adjusted time from this.datetime0_23; if provided,
     * minutes are translated to 00
     * @param offset the offset to add to the datetime
     * @return a datetime {@code String} in YYYYMMDDHH2400
     */
    // synchronized to prevent accidental calendar access during offsetting
    public synchronized String getLstDatetime0_23(int offset) {
        utcCal.add(Calendar.HOUR, offset);
        String lst = TimeUtils.getYYYYMMDDHH24mm(utcCal).substring(0, 10);
        utcCal.add(Calendar.HOUR, -offset);
        return lst;
    }

    /**
     * Returns the year in UTC time.
     * @return The year in UTC time.
     */
    public int getYear() {
        return utcCal.get(Calendar.YEAR);
    }

    /**
     * Returns the month index in UTC time. Note returns array index, so January is 0, February is 1, etc.
     * @return The zero-indexed month {@code int} in UTC time.
     */
    public int getMonth() {
        return utcCal.get(Calendar.MONTH);
    }

    /**
     * Returns the day of month in UTC time.
     * @return The day of month in UTC time.
     */
    public int getDay() {
        return utcCal.get(Calendar.DATE);
    }

    /**
     * Returns the hour of the day [0-23] in UTC time.
     * @return The hour of the day in UTC time.
     */
    public int getHour() {
        return utcCal.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * Creates a new object representing the next Datetime in sequence, bypassing the database.
     */
    public Datetime next() {
        return this.add(1);
    }

    /**
     * Creates a new object representing the previous Datetime in sequence, bypassing the database.
     */
    public Datetime previous() {
        return this.add(-1);
    }

    /**
     * Returns a new {@code Datetime} with a {@code Calendar} adjusted according to hours argument.
     * @param hours number of hours to adjust
     */
    public Datetime add(int hours) {
        Calendar cal = TimeUtils.extendDate(utcCal, 0, 0, 0, hours, 0);
        String newYyyymmddhh = TimeUtils.getYYYYMMDDHH24(cal);
        int newDatetimeId = this.datetimeId + hours;
        return new Datetime(newDatetimeId, newYyyymmddhh);
    }

    /**
     * Compares this object with the specified {@code Datetime} or {@code Calendar} object for order. Returns a negative
     * int, zero, or a positive int as this object precedes, is equal to, or follows the specified object.
     * @param o - the object to be compared.
     * @return a negative int, zero, or a positive int as this object is less than, equal to, or greater than
     * the specified object.
     * @throws ClassCastException - if the specified object cannot be cast to a {@code Datetime} or {@code Calendar} object
     */
    @Override
    public int compareTo(Object o) {
        try {
            return this.datetimeId - ((Datetime) o).getDatetimeId();
        } catch (ClassCastException e) {
            return utcCal.compareTo((Calendar) o);
        }
    }

    @Override
    public boolean equals(Object o) {
        // equal if they're the same object
        if (this == o) {
            return true;
        }
        // equal if o is a Datetime object, and both objects represent the same datetimeId
        if (o != null && o instanceof Datetime) {
            Datetime d = (Datetime) o;
            return ComparisonChain.start().compare(datetimeId, d.datetimeId).compare(utcCal, d.utcCal)
                    .compare(datetime0_23, d.datetime0_23).result() == 0;
        }
        return false;
    }

    /**
     * Creates a new {@code Datetime} object which is equal to this one
     * @return
     */
    public Datetime copy() {
        return new Datetime(datetimeId, datetime0_23);
    }

    @Override
    public int hashCode() {
        return datetimeId;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(datetimeId).addValue(datetime0_23 + " UTC").toString();
    }

}
