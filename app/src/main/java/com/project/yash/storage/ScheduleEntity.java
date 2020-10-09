package com.project.yash.storage;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ScheduleEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String time;
    private String mon, tue, wed, thu, fri, sat, sun;

    public ScheduleEntity(String time, String mon, String tue, String wed, String thu, String fri, String sat, String sun) {
        this.time = time;
        this.mon = mon;
        this.tue = tue;
        this.wed = wed;
        this.thu = thu;
        this.fri = fri;
        this.sat = sat;
        this.sun = sun;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMon() {
        return mon;
    }

    public void setMon(String mon) {
        this.mon = mon;
    }

    public String getTue() {
        return tue;
    }

    public void setTue(String tue) {
        this.tue = tue;
    }

    public String getWed() {
        return wed;
    }

    public void setWed(String wed) {
        this.wed = wed;
    }

    public String getThu() {
        return thu;
    }

    public void setThu(String thu) {
        this.thu = thu;
    }

    public String getFri() {
        return fri;
    }

    public void setFri(String fri) {
        this.fri = fri;
    }

    public String getSat() {
        return sat;
    }

    public void setSat(String sat) {
        this.sat = sat;
    }

    public String getSun() {
        return sun;
    }

    public void setSun(String sun) {
        this.sun = sun;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @NonNull
    public String toString() {
        return "{Time : " + time + ", Mon : " + mon + ", Tue : " + tue + ", Wed : " + wed + ", Thu : " + thu + ", Fri : " + fri + ", Sat : " + sat + ", Sun : " + sun + "}";
    }
}
