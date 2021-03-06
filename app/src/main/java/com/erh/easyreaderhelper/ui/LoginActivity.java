package com.erh.easyreaderhelper.ui;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.erh.easyreaderhelper.R;
import com.erh.easyreaderhelper.base.BaseActivity;
import com.erh.easyreaderhelper.bean.Person;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText accountLoginName;
    private EditText accountLoginPassword;
    private Button loginBtn,viewpagerbutton;
    private TextView registerAccountBtn;
    private ProgressBar progressBar;
    private LinearLayout llLogin;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_accountlogin);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        accountLoginName = (EditText) findViewById(R.id.i8_accountLogin_name);
        accountLoginPassword = (EditText) findViewById(R.id.i8_accountLogin_password);
        loginBtn = (Button) findViewById(R.id.i8_accountLogin_toLogin);
        viewpagerbutton = (Button) findViewById(R.id.viewpager);
        registerAccountBtn = (TextView) findViewById(R.id.register_account_btn);
        progressBar = (ProgressBar) findViewById(R.id.pb);
        llLogin = (LinearLayout) findViewById(R.id.ll_login);
        checkBox = (CheckBox) findViewById(R.id.login_check);
        registerAccountBtn.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG ); //?????????
        registerAccountBtn.getPaint().setAntiAlias(true);//?????????
        accountLoginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        checkBox.setBackgroundResource(R.drawable.bukejian);
    }

    private void initData() {

    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        llLogin.setVisibility(View.GONE);
        viewpagerbutton.setVisibility(View.GONE);
    }

    private void hiddenProgressBar() {
        progressBar.setVisibility(View.GONE);
        llLogin.setVisibility(View.VISIBLE);
        viewpagerbutton.setVisibility(View.VISIBLE);
    }

    private void initListener() {
        loginBtn.setOnClickListener(this);
        registerAccountBtn.setOnClickListener(this);
        viewpagerbutton.setOnClickListener(this);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    //CheckBox?????????????????????
                    accountLoginPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    checkBox.setBackgroundResource(R.drawable.kejian);
                } else {
                    //CheckBox???????????????????????????
                    accountLoginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    checkBox.setBackgroundResource(R.drawable.bukejian);
                }
                //?????????????????????
                accountLoginPassword.setSelection(accountLoginPassword.getText().toString().length());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.i8_accountLogin_toLogin:
                bmobUserAccountLogin();//bmob??????
                break;
            case R.id.register_account_btn:
                //?????????????????????
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.viewpager:
                Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }

    private void bmobUserAccountLogin() {
        final String accountName = accountLoginName.getText().toString().trim();//??????
        final String accountPassword = accountLoginPassword.getText().toString().trim();//??????

        if (TextUtils.isEmpty(accountName)) {
            showToast("??????????????????");
            return;
        }

        if (TextUtils.isEmpty(accountPassword)) {
            showToast("??????????????????");
            return;
        }

        //????????????
        showProgressBar();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //BmobUser??????Bmob??????????????????
                BmobUser bmobUser = new BmobUser();
                bmobUser.setUsername(accountName);
                bmobUser.setPassword(accountPassword);
/*
                //????????????
                BmobQuery<Person> query=new BmobQuery<Person>();
                query.findObjects(new FindListener<Person>() {
                    @Override
                    public void done(List<Person> list, BmobException e) {
                        if(e == null){
                            show_ad(list);
                        }else{
                            Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });

 */
                bmobUser.login(new SaveListener<BmobUser>() {
                    @Override
                    public void done(BmobUser bmobUser, BmobException e) {
                        if (e == null) {
                            loadhuanxin(accountName,accountPassword);
                            //??????????????????????????????
                        //    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                         //   startActivity(intent);
                            finish();
                        } else {
                            showToast("" + e.getMessage());
                            hiddenProgressBar();//??????
                        }
                    }
                });
            }
        }, 3000);
    }

    private void loadhuanxin(String accountName, String accountPassword) {
        EMClient.getInstance().login(accountName, accountPassword, new EMCallBack() {
            /**
             * ?????????????????????
             */
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // ???????????????????????????
                        EMClient.getInstance().chatManager().loadAllConversations();
                        // ?????????????????????????????????????????????????????????
                        // EMClient.getInstance().groupManager().loadAllGroups();

                        // ????????????????????????
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }

            /**
             * ?????????????????????
             * @param i
             * @param s
             */
            @Override
            public void onError(final int i, final String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("lzan13", "???????????? Error code:" + i + ", message:" + s);
                        /**
                         * ?????????????????????????????????api????????????
                         * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1_e_m_error.html
                         */
                        switch (i) {
                            // ???????????? 2
                            case EMError.NETWORK_ERROR:
                                Toast.makeText(LoginActivity.this, "???????????? code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // ?????????????????? 101
                            case EMError.INVALID_USER_NAME:
                                Toast.makeText(LoginActivity.this, "?????????????????? code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // ??????????????? 102
                            case EMError.INVALID_PASSWORD:
                                Toast.makeText(LoginActivity.this, "??????????????? code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // ????????????????????????????????????????????? 202
                            case EMError.USER_AUTHENTICATION_FAILED:
                                Toast.makeText(LoginActivity.this, "????????????????????????????????????????????? code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // ??????????????? 204
                            case EMError.USER_NOT_FOUND:
                                Toast.makeText(LoginActivity.this, "??????????????? code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // ???????????????????????? 300
                            case EMError.SERVER_NOT_REACHABLE:
                                Toast.makeText(LoginActivity.this, "???????????????????????? code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // ??????????????????????????? 301
                            case EMError.SERVER_TIMEOUT:
                                Toast.makeText(LoginActivity.this, "??????????????????????????? code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // ??????????????? 302
                            case EMError.SERVER_BUSY:
                                Toast.makeText(LoginActivity.this, "??????????????? code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // ?????? Server ?????? 303 ?????????????????????????????????
                            case EMError.SERVER_UNKNOWN_ERROR:
                                Toast.makeText(LoginActivity.this, "???????????????????????? code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            default:
                                Toast.makeText(LoginActivity.this, "ml_sign_in_failed code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }
/*
    public void show_ad(List<Person> list){
        for (Person ad : list){
            if(ad.getName() != null && BmobUser.getCurrentUser().getUsername().equals(ad.getName())){
                BmobFile icon= ad.getImage();
                icon.download(new DownloadFileListener() {
                    @Override
                    public void onProgress(Integer integer, long l) {

                    }
                    @Override
                    public void done(String s, BmobException e) {
                        if(e == null){
                            //???????????????????????????
                            h_head.setImageBitmap(round_Util.toRoundBitmap(BitmapFactory.decodeFile(s))); //?????????????????????????????????
                        }
                    }
                });
                break;
            }
        }

    }

 */

}
