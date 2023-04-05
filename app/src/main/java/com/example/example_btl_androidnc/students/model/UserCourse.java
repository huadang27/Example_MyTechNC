package com.example.example_btl_androidnc.students.model;

public class UserCourse {
    private String courseId;
    private String enrollDate;
    private int status;

    public UserCourse(String courseId, int status) {
        this.courseId = courseId;
        this.status = status;
    }

    public UserCourse() {
    }

    public UserCourse(String courseId, String enrollDate, int status) {
        this.courseId = courseId;
        this.enrollDate = enrollDate;
        this.status = status;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getEnrollDate() {
        return enrollDate;
    }

    public void setEnrollDate(String enrollDate) {
        this.enrollDate = enrollDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}