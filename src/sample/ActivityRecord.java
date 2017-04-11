package sample;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ActivityRecord {

    private Time startTime;
    private Time endTime;
    private String taskName;
    private int durationMinutes;

    public boolean checkEndTimeIsAferStartTime(Time startTime, Time endTime) {
        return endTime.after(startTime);
    }

    public boolean setStartTime(String typedStartTime) {
        boolean success;
        if (typedStartTime != null) {
            try {
                this.startTime = Time.valueOf(typedStartTime);
                success = true;
            } catch (Exception e) {
                success = false;
            }
            calculateDuration(startTime, endTime);
            System.out.println("Start time set to " + this.getStartTime().toString());
        } else {
            this.endTime = null;
            success = true;
            System.out.println("Start time set to null");
        }
        return success;
    }

    public boolean setEndTime(String typedEndTime) {
        boolean success;
        if (typedEndTime != null) {
            try {
                this.endTime = Time.valueOf(typedEndTime);
                success = true;
            } catch (Exception e) {
                success = false;
            }
            calculateDuration(startTime, endTime);
            System.out.println("End end set to " + this.getEndTime().toString());
        } else {
            this.endTime = null;
            success = true;
            System.out.println("End time set to null");
        }
        return success;
    }

    public void calculateDuration(Time startTime, Time endTime) {
        if (startTime != null & endTime != null) {
            long startTimeSeconds = startTime.getTime();
            long endTimeSeconds = endTime.getTime();
            this.durationMinutes = (int) ((endTimeSeconds - startTimeSeconds) / 60000);
            System.out.println("Duration minutes: " + this.durationMinutes);
        }
    }

    public void setStartTime() {
        this.startTime = Time.valueOf(getCurrentTime());
        calculateDuration(startTime, endTime);
        System.out.println("Start time set to system time: " + this.getStartTime().toString());

    }

    public void setEndTime() {
        this.endTime = Time.valueOf(getCurrentTime());
        calculateDuration(startTime, endTime);
        System.out.println("End time set to system tyme: " + this.getEndTime().toString());
    }


    public boolean isComplete() {
        return this.startTime != null & this.endTime != null & this.taskName != null;
    }

    private String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(cal.getTime()) ;
    }

    public Time getStartTime() {
        return startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
        System.out.println("Task name set to: " + taskName);
    }

    public String getTaskName() {
        return taskName;
    }
}