package com.dwg.egou.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/4/3.
 */
public class readUtil  {
    private final  static String FILE_NAME = "dianping";
    private final  static String IS_FIRST = "isfirst";
    public static boolean getWelcomeBoolean(Context context)
    {
     return  context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE).getBoolean(IS_FIRST,true);
    }
    public static void WriteWelcome(Context context)
    {
        SharedPreferences.Editor edit = context.getSharedPreferences(FILE_NAME, Context.MODE_APPEND).edit();
        edit.putBoolean(IS_FIRST,false);
        edit.commit();
    }
//    public static void addCity(Context context,String cityName) {
//        SharedPreferences.Editor editor = (SharedPreferences.Editor) context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
//        editor.putString("cityName", cityName);
//        editor.commit();
//    }
        public static String getCityName(Context context)
    {
       return context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE)
               .getString("cityName","选择城市");

    }
        public static void SET_SESSIONID(Context context,String SESSION_ID){
            SharedPreferences.Editor editor = context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE).edit();
            editor.putString("SESSION_ID",SESSION_ID);
            editor.commit();
            return ;
        }
    public static String GET_SESSIONID(Context context){
        return context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE).getString("SESSION_ID",null);
    }
    public static void setUsername(Context context,String username){
        SharedPreferences.Editor editor= context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE).edit();
        editor.putString("username",username);
        editor.commit();
    }
    public static String getUsername(Context context){
        return context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE).getString("username",null);
    }
    public static void setPassword(Context context,String password){
        SharedPreferences.Editor editor= context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE).edit();
        editor.putString("password",password);
        editor.commit();
    }
    public static String getPassword(Context context){
        return context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE).getString("password",null);
    }

    public static void setUsernickName(Context context, String nickname) {
        SharedPreferences.Editor editor= context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE).edit();
        editor.putString("nickname",nickname);
        editor.commit();
    }
    public static String getUsernickname(Context context){
        return context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE).getString("nickname",null);
    }
}
