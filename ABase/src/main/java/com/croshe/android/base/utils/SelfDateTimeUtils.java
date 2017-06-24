package com.croshe.android.base.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Janesen on 16/4/15.
 */
public class SelfDateTimeUtils {


    public static String formatDateTime(String dateTime){
        if(dateTime==null||dateTime.equals("null"))return "";
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Calendar cd=Calendar.getInstance();
        try {
            cd.setTime(sdf.parse(dateTime));

            long minu=(System.currentTimeMillis()-cd.getTimeInMillis())/1000/60;

            if(minu==0){
                return "刚刚";
            }

            if(minu<60&&minu>0){

                return minu+"分钟前";
            }else{
                if(minu<12*60&&minu>0){//12个小时内
                    return minu/60+"小时"+minu%60+"分钟前";
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateTime;
    }


    public static String formatDate(String dateTime){
        return formatDateStr(dateTime,"");
    }


    public static String formatDateMinute(String dateTime){
        return formatDateStr(dateTime," HH:mm");
    }

    public static String formatDateMinuteSecond(String dateTime){
        return formatDateStr(dateTime," HH:mm:ss");
    }

    private static String formatDateStr(String dateTime,String pattern){
        if(dateTime==null||dateTime.equals("null"))return "";
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Calendar cd=Calendar.getInstance();
            cd.setTime(sdf.parse(dateTime));
            SimpleDateFormat sdf2=new SimpleDateFormat(pattern);

            int subDay = subDay(cd.getTime(), new Date());
            if(subDay==0){
                return "今天"+sdf2.format(cd.getTime());
            }else if(subDay==1){
                return "昨天"+sdf2.format(cd.getTime());
            }else if(subDay==2){
                return "前天"+sdf2.format(cd.getTime());
            }
            Date date=sdf.parse(dateTime);
            return new SimpleDateFormat("yyyy-MM-dd").format(date) + "" + sdf2.format(date);
        } catch (Exception e) {
        }
        return dateTime;
    }


    /**
     * 获得当前时间
     * @return
     */
    public static String getDateTimeStr(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(new Date());
    }

    /**
     * 获得当前时间
     * @return
     */
    public static String getDateTimeStr(String pattern){
        SimpleDateFormat sdf=new SimpleDateFormat(pattern);
        return sdf.format(new Date());
    }


    /**
     * 计算日期差
     * @param first
     * @param two
     * @return
     */
    public static int subDay(Date first, Date two) {
        try {
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            return Math.abs((int) ((sdf.parse(sdf.format(first)).getTime() - sdf.parse(sdf.format(two)).getTime()) / (60 * 60 * 1000 * 24)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获得当前时间
     * @return
     */
    public static String formatDateTime(String dateTime,String pattern){
        if (StringUtils.isNotEmpty(dateTime)) {
            SimpleDateFormat sdf=new SimpleDateFormat(pattern);
            try {
                return sdf.format(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateTime));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return dateTime;
    }



}
