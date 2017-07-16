package com.dwg.egou.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2016/4/23.
 */
public class MyUtils {
    private static final String RANDOMS = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static String getCheckCode(int num) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < num; i++) {
            int rand = (int) (Math.random() * RANDOMS.length());
            buffer.append(RANDOMS.charAt(rand));
        }
        return buffer.toString();
    }
    public static String trimMs(Timestamp time){
        String s = df.format(time);
        return s;
    }
}
