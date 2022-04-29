package com.erh.easyreaderhelper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.erh.easyreaderhelper.R;
import com.erh.easyreaderhelper.base.BaseActivity;

import cn.bmob.v3.BmobUser;

public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //获取缓存中的用户对象
                BmobUser currentUser = BmobUser.getCurrentUser(BmobUser.class);
                if (currentUser != null) {
                    //允许用户使用应用，进入程序
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    //进入登录界面
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);
    }
}
