/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vox.util.followup;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import javame.io.Serializable;

/**
 *
 * @author Tug
 */
public class FollowUp implements Serializable {

    private int day, hours, min;
    private Date start;
    private int durationMins;

    public FollowUp() {
        this.day = 0;
        this.hours = 0;
        this.min = 0;
        this.start = null;
    }

    public FollowUp(int day, int hours, int min, int duration) {
        this.day = day;
        this.hours = hours;
        this.min = min;
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int currentDay = c.get(Calendar.DAY_OF_MONTH);
        if(day < currentDay) {
            int month = c.get(Calendar.MONTH);
            if(month == Calendar.DECEMBER) {
                c.set(Calendar.YEAR, c.get(Calendar.YEAR) + 1);
                c.set(Calendar.MONTH, Calendar.JANUARY);
            } else {
                c.set(Calendar.MONTH, month);
            }
        }
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hours);
        c.set(Calendar.MINUTE, min);
        c.set(Calendar.SECOND, 0);
        this.start = c.getTime();
        this.durationMins = duration;
    }

    public FollowUp(Date start, int duration) {
        this.start = start;
        this.durationMins = duration;
        loadWithDate();
    }

    private void loadWithDate() {
        Calendar c = Calendar.getInstance();
        c.setTime(start);
        this.day = c.get(Calendar.DAY_OF_MONTH);
        this.hours = c.get(Calendar.HOUR_OF_DAY);
        this.min = c.get(Calendar.MINUTE);
    }

    public Date getStart()
    {
        return start;
    }

    public void setStart(Date start)
    {
        this.start = start;
    }

    public int getDay()
    {
        return day;
    }

    public int getHours()
    {
        return hours;
    }

    public int getMin()
    {
        return min;
    }

    public int getDuration()
    {
        return durationMins;
    }

    public void setDuration(int newDuration)
    {
        this.durationMins = newDuration;
    }

    public void serialize(DataOutput output) throws IOException {
        output.writeLong(start.getTime());
        output.writeInt(durationMins);
    }

    public void deserialize(DataInput input) throws IOException {
        this.start = new Date(input.readLong());
        this.durationMins = input.readInt();
        loadWithDate();
    }

    public long startsIn() {
        return start.getTime()-System.currentTimeMillis();
    }

    public boolean startsNow() {
        long epsi = 3 * 60 * 1000L; // 3 minutes
        return startsIn() < epsi;
    }

    public long getEnd() {
        return start.getTime()+durationMins*60*1000;
    }

    public void setEnd(long end) {
        this.durationMins = (int)((end - start.getTime())/(60*1000));
    }

}
