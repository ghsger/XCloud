package cn.zf233.xcloud.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by zf233 on 11/28/20
 */
public class DateTimeUtil {

    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // timeMillis to string
    public static String timerToString(Long timer) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(STANDARD_FORMAT, Locale.CHINA);
        return simpleDateFormat.format(timer);
    }

    // timeMillis to date
    public static Date timerToDate(Long timer) {
        return new Date(timer);
    }

    // format string date to date
    public static Date stringToDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(STANDARD_FORMAT, Locale.CHINA);
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    // date to string
    public static String dateToString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(STANDARD_FORMAT, Locale.CHINA);
        return simpleDateFormat.format(date);
    }

    // get Current time of string
    public static String getNowDateTime() {
        return timerToString(System.currentTimeMillis());
    }

}
