package com.brioal.pocketcode.util;

import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期格式化工具
 * Created by Brioal on 2016/5/11.
 */
public class DateFormat {
    //计算毫秒值
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
    //格式化当前的时间,设置为今天,昨天,昨天之前显示完整时间
    public static void setDate(long time, TextView mText) {
        Date date = new Date();
        long l = 24 * 60 * 60 * 1000; //每天的毫秒数
        Calendar calender = Calendar.getInstance();
        date.setYear(calender.YEAR);
        date.setMonth(calender.MONTH);
        date.setDate(calender.DAY_OF_MONTH);
        long today = (date.getTime() - (date.getTime() % l) - 8 * 60 * 60 * 1000); //今天0点的时间
        date.setDate(calender.DAY_OF_MONTH - 1);
        long yestaday = today - l; //z昨天0点的时间
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
