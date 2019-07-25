package com.project.yash;

import java.util.regex.Pattern;

public class schedule {
    String time,subject;
    schedule(String s1,String s2)
    {
        time=s1;
        subject=s2;
    }

    public String getTime() {
        return time;
    }

    public String getSubject() {
        return subject;
    }

}
