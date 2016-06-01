package com.brioal.pocketcode.util;

import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Brioal on 2016/5/11.
 */
public class DateFormat {
    public static String format(long time) {
        long seconds = time / 1000;
        int hour = (int) (seconds / 60 / 60); //时
        seconds = seconds - 60 * 60 * hour;
        int minute = (int) (seconds / 60);//分
        seconds = seconds - (60 * minute); //秒
        String h = hour < 10 ? ("0" + hour) : hour + "";
        String m = minute < 10 ? "0" + minute : minute + "";
        String s = seconds < 10 ? "0" + seconds : seconds + "";
        return h + ":" + m + ":" + s;
    }

    public static void setDate(long time, TextView mText) {
        Calendar calender = Calendar.getInstance();
        Date date = new Date();
        date.setYear(calender.YEAR);
        date.setMonth(calender.MONTH);
        date.setDate(calender.DAY_OF_MONTH);
        long today = date.getTime(); //今天0点的时间
        date.setDate(calender.DAY_OF_MONTH - 1);
        long yestaday = date.getTime(); //z昨天0点的时间
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        if (time < yestaday) { //昨天之前
            SimpleDateFormat f = new SimpleDateFormat("MM-dd HH:mm:ss");
            mText.setText(f.format(time));
        } else if (time > today) {
            mText.setText("今天 " + format.format(time));
        } else {
            mText.setText("昨天 " + format.format(time));
        }
    }
}
