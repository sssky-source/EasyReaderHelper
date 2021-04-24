package com.erh.easyreaderhelper.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.erh.easyreaderhelper.constants.Constants;

import java.util.LinkedList;
import java.util.List;

import cn.bmob.v3.Bmob;


public class ErhApplication extends Application {

    public static Context applicationContext;
    private List<Activity> activityList = new LinkedList();//退出应用集合
    private List<Activity> temporaryActivityList = new LinkedList();//临时销毁Activity集合
    private static ErhApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
        instance = this;

        //第一：默认初始化
        Bmob.initialize(this, Constants.APPLICATION_ID);
    }

    public static ErhApplication getApplication() {
        return instance;
    }

    /**
     * 获取全局上下文
     */
    public static Context getContext() {
        return applicationContext;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    //添加Activity到容器中
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public void clearActivity() {
        activityList.clear();
    }

    //遍历所有Activity并finish
    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
    }

    //临时添加Activity到容器中
    public void temporaryAddActivity(Activity activity) {
        temporaryActivityList.add(activity);
    }

    //临时遍历所有Activity并finish
    public void temporaryExit() {
        for (Activity activity : temporaryActivityList) {
            activity.finish();
        }
    }
}