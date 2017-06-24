package com.croshe.android.base.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ExitApplication {



	static Handler handler=new Handler(Looper.getMainLooper());
	static List<Runnable> releaseRunable=new ArrayList<Runnable>();
	static List<Activity> activityList=new LinkedList<Activity>();

    static boolean isExit;


	public static void addReleaseRunable(Runnable runnable){
		releaseRunable.add(runnable);
	}

	public static void addActivity(Activity activity){
		activityList.add(activity);
        isExit=false;
	}

    public static void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

	/**
	 * 退出app
	 */
	public static void exitApp(){
		try {
			for (Activity activity : activityList) {
				activity.finish();
			}
		} catch (Exception e) {}
        release();
        isExit=true;
	}


	/**
	 * 释放资源
	 */
	public static void release(){
		for (Runnable runnable : releaseRunable) {
			if(runnable!=null){
				handler.post(runnable);
			}
		}
		System.gc();
	}


    public static boolean isExited() {
        return isExit;
    }


    public static void finishActivity(Class<? extends  Activity> activityClass) {
        for (Activity activity : activityList) {
            if (activity.getClass() == activityClass) {
                activity.finish();
            }
        }
    }

    public static Activity getLastActivity() {
        return activityList.get(activityList.size()-1);
    }
}
