/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Tug
 */
public class DateUtil {

    public static final long MILLIS_PER_MIN = 60 * 1000L;
    public static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MIN;
    public static final long MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR;
    public static final long MILLIS_PER_WEEK = 7 * MILLIS_PER_DAY;

    public static Date addMinutes(Date date, int minutes)
    {
        return new Date(date.getTime() + MILLIS_PER_MIN*minutes);
    }

    public static Date addHours(Date date, int hours)
    {
        return new Date(date.getTime() + MILLIS_PER_HOUR*hours);
    }

    public static Date addDays(Date date, int days)
    {
        return new Date(date.getTime() + MILLIS_PER_DAY*days);
    }

    public static Date addWeeks(Date date, int weeks)
    {
        return new Date(date.getTime() + MILLIS_PER_WEEK*weeks);
    }

    public static Date addMonths(Date date, int months)
    {
        if(months >= 12 || months <= -12) {
            int yearAdd = months / 12;
            if(months <= -12) yearAdd--;
            date = addYears(date, months / 12);
            months = months % 12;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int currmonth = c.get(Calendar.MONTH);
        if(currmonth == Calendar.DECEMBER) {
            c.set(Calendar.YEAR, c.get(Calendar.YEAR) + 1);
            c.set(Calendar.MONTH, Calendar.JANUARY+months-1);
        } else {
            c.set(Calendar.MONTH, currmonth + months);
        }
        return c.getTime();
    }

    public static Date addYears(Date date, int years)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.YEAR, c.get(Calendar.YEAR) + years);
        return c.getTime();
    }

}
