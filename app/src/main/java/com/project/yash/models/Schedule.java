package com.project.yash.models;


public class Schedule {
    String time, subject;

    public Schedule(String s1, String s2) {
        time = s1;
        subject = s2;
    }

    public String getTime() {
        return time;
    }

    public String getSubject() {
        return subject;
    }

}
