package com.erh.easyreaderhelper.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.erh.easyreaderhelper.constants.Constants;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseIM;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
     //   applicationContext = getApplicationContext();
        instance = this;
        //第一：默认初始化
        Bmob.initialize(this, Constants.APPLICATION_ID);
        //初始化环信EaseIM
        EMOptions options=new EMOptions();
        options.setAcceptInvitationAlways(false);//设置需要同意后才能接受邀请
        options.setAutoAcceptGroupInvitation(false);//设置需要同意后才能接受群邀请

        //EaseIM初始化
        //EaseIM.getInstance().init(this,options);

        //EaseIM初始化
        if(EaseIM.getInstance().init(this, options)){
            //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
            EMClient.getInstance().setDebugMode(true);
            //EaseIM初始化成功之后再去调用注册消息监听的代码 ...
        }

        //初始化数据模型层类
 //       Model.getInstance().init(this);

        //初始化全局上下文
        applicationContext=this;

    }

    public  static Context getGlobalApplication() {
        return applicationContext;
    }

    /**
     * 获取当前运行的进程名
     *
     * @return
     */
    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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