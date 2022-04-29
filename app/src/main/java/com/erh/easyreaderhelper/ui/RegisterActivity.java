package com.erh.easyreaderhelper.ui;

import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.erh.easyreaderhelper.R;
import com.erh.easyreaderhelper.base.BaseActivity;
import com.erh.easyreaderhelper.bean.Person;
import com.erh.easyreaderhelper.util.CircleUtils;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executors;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class RegisterActivity extends BaseActivity {

    private EditText accountRegisterName;
    private EditText accountRegisterPassword;
    private Button registerBtn;
    private CheckBox checkBox;

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
        checkBox = (CheckBox) findViewById(R.id.checkbox);
        accountRegisterPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        checkBox.setBackgroundResource(R.drawable.bukejian);
    }

    private void initData() {

    }

    private void initListener() {
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bmobRegisterAccount();
            }
        });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                    if (isChecked) {
                        //CheckBox选中，显示明文
                        accountRegisterPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        checkBox.setBackgroundResource(R.drawable.kejian);
                    } else {
                        //CheckBox取消选中，显示暗文
                        accountRegisterPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        checkBox.setBackgroundResource(R.drawable.bukejian);
                    }
                    //光标移至最末端
                    accountRegisterPassword.setSelection(accountRegisterPassword.getText().toString().length());
                }

        });
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
                    registerinhuanxin(registerName,registerPassword);
                    showToast("恭喜，注册账号成功");
                    finish();
                } else {
                    showToast("register fail:" + e.getMessage());
                }
            }
        });
        setHead("http://www.mythreecat.top/2021/05/09/a88b26af9d8842cfa62a2c7d31679139.jpg",registerName);
      //  upload("http://www.mythreecat.top/2021/05/09/a88b26af9d8842cfa62a2c7d31679139.jpg",registerName);
    }

    private void registerinhuanxin(String registerName, String registerPassword) {
        // 注册是耗时过程，所以要显示一个dialog来提示下用户

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(registerName, registerPassword);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!RegisterActivity.this.isFinishing()) {
                                //mDialog.dismiss();
                            }
                            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!RegisterActivity.this.isFinishing()) {
                              //  mDialog.dismiss();
                            }
                            /**
                             * 关于错误码可以参考官方api详细说明
                             * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1_e_m_error.html
                             */
                            int errorCode = e.getErrorCode();
                            String message = e.getMessage();
                            Log.d("lzan13", String.format("sign up - errorCode:%d, errorMsg:%s", errorCode, e.getMessage()));
                            switch (errorCode) {
                                // 网络错误
                                case EMError.NETWORK_ERROR:
                                    Toast.makeText(RegisterActivity.this, "网络错误 code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
                                    break;
                                // 用户已存在
                                case EMError.USER_ALREADY_EXIST:
                                    Toast.makeText(RegisterActivity.this, "用户已存在 code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
                                    break;
                                // 参数不合法，一般情况是username 使用了uuid导致，不能使用uuid注册
                                case EMError.USER_ILLEGAL_ARGUMENT:
                                    Toast.makeText(RegisterActivity.this, "参数不合法，一般情况是username 使用了uuid导致，不能使用uuid注册 code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
                                    break;
                                // 服务器未知错误
                                case EMError.SERVER_UNKNOWN_ERROR:
                                    Toast.makeText(RegisterActivity.this, "服务器未知错误 code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
                                    break;
                                case EMError.USER_REG_FAILED:
                                    Toast.makeText(RegisterActivity.this, "账户注册失败 code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
                                    break;
                                default:
                                    Toast.makeText(RegisterActivity.this, "ml_sign_up_failed code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
                                    break;
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //给账号绑定一张默认头像
    private void setHead(String imgpath,String name) {

        BmobFile bmobFile = new BmobFile("mmexport1620538672908.jpg","",imgpath);
        Person person = new Person();
        person.setName(name);
        person.setImage(bmobFile);
        person.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    Toast.makeText(RegisterActivity.this,"头像上传成功：" + e.getMessage(),Toast.LENGTH_SHORT).show();
                }else {

                }
            }
        });
    }

    //上传图片到表中
    private void upload(String imgpath,String name){
        Log.d("MainActivity111","11111111111111111111111111111");
        BmobFile bmobFile = new BmobFile(new File(imgpath));
        Log.d("MainActivity111","22222222222222222222222222222");
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                Log.d("MainActivity111","3333333333333333333333333333333");
                if(e==null){
                    Person person = new Person();
                    person.setName(name);
                    person.setImage(bmobFile);
                    person.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {

                        }
                    });
                } else{
                    Toast.makeText(RegisterActivity.this,"上传文件失败：" + e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
