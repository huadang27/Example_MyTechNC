package com.example.example_btl_androidnc.students.model;

import java.io.Serializable;

public class Schedule implements Serializable {
    private  String id;
    private String dayOfWeek;
    private  String day;
    private String ca;
    private int status;
    private String course_id;

    private boolean attendance;
    private  int number;

    public Schedule() {
    }

    public Schedule(String id, String dayOfWeek, String duration, int status) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.ca = duration;
        this.status = status;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getDuration() {
        return ca;
    }

    public void setDuration(String duration) {
        this.ca = duration;
    }



    public void setStatus(int status) {
        this.status = status;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public boolean isAttendance() {
        return attendance;
    }

    public void setAttendance(boolean attendance) {
        this.attendance = attendance;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "id='" + id + '\'' +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", day='" + day + '\'' +
                ", ca='" + ca + '\'' +
                ", status=" + status +
                ", course_id='" + course_id + '\'' +
                ", attendance=" + attendance +
                ", number=" + number +
                '}';
    }
}
