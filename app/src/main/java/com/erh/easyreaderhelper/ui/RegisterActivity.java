package com.erh.easyreaderhelper.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.erh.easyreaderhelper.R;
import com.erh.easyreaderhelper.base.BaseActivity;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private EditText accountRegisterName;
    private EditText accountRegisterPassword;
    private Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_account);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        accountRegisterName = (EditText) findViewById(R.id.i8_accountRegister_name);
        accountRegisterPassword = (EditText) findViewById(R.id.i8_accountRegister_password);
        registerBtn = (Button) findViewById(R.id.i8_accountRegistern_toRegister);
    }

    private void initData() {

    }

    private void initListener() {
        registerBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.i8_accountRegistern_toRegister:
                bmobRegisterAccount();//Bmob
                break;
            default:
                break;
        }
    }

    private void bmobRegisterAccount() {
        final String registerName = accountRegisterName.getText().toString().trim();//账号
        final String registerPassword = accountRegisterPassword.getText().toString().trim();//密码

        if (TextUtils.isEmpty(registerName) || TextUtils.isEmpty(registerPassword)) {
            showToast("注册账号或密码为空");
            return;
        }

        BmobUser bmobUser = new BmobUser();
        bmobUser.setUsername(registerName);
        bmobUser.setPassword(registerPassword);
        bmobUser.signUp(new SaveListener<BmobUser>() {
            @Override
            public void done(BmobUser bmobUser, BmobException e) {
                if (e == null) {
                    showToast("恭喜，注册账号成功");
                    finish();
                } else {
                    showToast("register fail:" + e.getMessage());
                }
            }
        });
    }
}
