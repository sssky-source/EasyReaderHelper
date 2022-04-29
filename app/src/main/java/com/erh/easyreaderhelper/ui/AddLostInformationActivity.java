package com.erh.easyreaderhelper.ui;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.erh.easyreaderhelper.R;
import com.erh.easyreaderhelper.bean.Informationpublished;
import com.erh.easyreaderhelper.base.BaseActivity;
import com.erh.easyreaderhelper.bean.Person;
import com.erh.easyreaderhelper.constants.Constants;
import com.erh.easyreaderhelper.util.BitmapUtils;
import com.erh.easyreaderhelper.util.CameraUtils;
import com.erh.easyreaderhelper.util.CircleUtils;
import com.erh.easyreaderhelper.util.SearchalbumUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class AddLostInformationActivity extends BaseActivity implements View.OnClickListener, AMapLocationListener {


    //权限申请
    private RxPermissions rxPermissions;
    private String base64Pic;
    //拍照和相册获取图片的Bitmap
    private Bitmap orc_bitmap;
    //是否拥有权限
    private boolean hasPermissions =false;

    //存储拍完照后的图片
    private File outputImagePath;
    //启动相机标识
    //启动相册标识
    public static final int SELECT_PHOTO = 2;
    public static final int TAKE_PHOTO = 1;
    public Uri imageUri;

    private ImageView back;
    private ImageView add;
    private EditText title;
    private EditText phoneNum;
    private EditText desc;
    private EditText address;
    private Informationpublished infomationReq;
    private boolean isChangeInfos;
    private Button button;
    private ImageView imageView;
    private double latitudeone,longitudeone;

    public Dialog setHeadDialog;
    public View mDialogView;
    public String imagePath = null;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;


    //Glide请求图片选项配置
    private RequestOptions requestOptions = RequestOptions.circleCropTransform()
            .diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
            .skipMemoryCache(true);//不做内存缓存


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_lost_infos_activity);
        Bmob.initialize(this, Constants.APPLICATION_ID);
        initView();

        initData();
        initLocation();
        mLocationClient.startLocation();

        initListener();
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.iv_back);
        add = (ImageView) findViewById(R.id.iv_add);
        title = (EditText) findViewById(R.id.et_title);
        phoneNum = (EditText) findViewById(R.id.et_phone_num);
        desc = (EditText) findViewById(R.id.et_desc);
        address = (EditText) findViewById(R.id.et_address);
        button = (Button) findViewById(R.id.et_button);
        imageView = (ImageView) findViewById(R.id.et_picture);
    }



    private void initData() {
        infomationReq = (Informationpublished) getIntent().getSerializableExtra("editData");
        if (infomationReq != null) {
            isChangeInfos = true;//设置是否是信息更新操作
            title.setText(infomationReq.getTitle());
            phoneNum.setText(infomationReq.getPhoneNum());
            desc.setText(infomationReq.getDesc());
            address.setText(infomationReq.getAddress());
        }
    }

    private void initListener() {
        back.setOnClickListener(this);
        add.setOnClickListener(this);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_add:
                addData();
                break;
            case R.id.et_button:
                showDialog();
                break;
            default:
                break;
        }
    }

    private void showDialog() {
        setHeadDialog = new Dialog(this,R.style.Theme_Light_Dialog);
        mDialogView = LayoutInflater.from(AddLostInformationActivity.this).inflate(R.layout.layout_dialog,null);
        Window window = setHeadDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialogStyle);
        window.getDecorView().setPadding(0,0,0,0);
        WindowManager.LayoutParams lp = setHeadDialog.getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //设置窗口高度为包裹内容
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        //将自定义布局加载到dialog上
        setHeadDialog.setContentView(mDialogView);
        setHeadDialog.show();
        bindDialogEvent();
    }


    private void bindDialogEvent() {
        Button cameraButton = (Button) mDialogView
                .findViewById(R.id.iv_userinfo_takepic);
        Button photoButton = (Button) mDialogView
                .findViewById(R.id.iv_userinfo_choosepic);
        Button cancelButton = (Button) mDialogView
                .findViewById(R.id.iv_userinfo_cancle);

        cameraButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                takePhoto();//拍照
                showMsg("拍照");
                setHeadDialog.dismiss();
            }
        });

        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                openAlbum();//开相册
                showMsg("打开相册");
                setHeadDialog.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setHeadDialog.dismiss();
            }
        });

    }

    //拍照
    private void takePhoto(){
        if(!hasPermissions){
            showMsg("未获取到权限");
            checkVersion();
            return;
        }
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String filename = timeStampFormat.format(new Date());
        outputImagePath = new File(this.getExternalCacheDir(),filename + ".jpg");
        Intent takePhotoIntent = CameraUtils.getTakePhotoIntent(AddLostInformationActivity.this,outputImagePath);
        //开启一个带有返回值的Activity，请求码为TAKE_PHOTO
        startActivityForResult(takePhotoIntent,TAKE_PHOTO);
    }


    //开相册
    private void openAlbum(){
        if(!hasPermissions){
            showMsg("未获取到权限");
            checkVersion();
            return;
        }
        startActivityForResult(CameraUtils.getSelectPhotoIntent(),SELECT_PHOTO);
    }

    //提示消息 Toast
    private void showMsg(String msg){
        Toast.makeText(AddLostInformationActivity.this,msg,Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("camera", "0000000000000000000");
        switch (requestCode) {
            case TAKE_PHOTO:
                Uri uri = data.getData();
                if (resultCode == RESULT_OK) {
                    displayImage(outputImagePath.getAbsolutePath());
                    Log.d("camera", "09999999999999990");
                }
                // upload(CameraUtils.getImagePath(uri,null,getContext()));
                break;
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    //判断手机系统版本号
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                        //4.4以上系统使用这个方法处理图片
                        Log.d("camera", "111111111111111111111");
                        imagePath = CameraUtils.getImageOnKitKatPath(data,AddLostInformationActivity.this);
                        Log.d("camera", "17777777777777771");
                        Log.d("camera", imagePath);
                        Log.d("camera", "1888888888888881");
                    } else {
                        imagePath = CameraUtils.getImageBeforeKitKatPath(data,AddLostInformationActivity.this);
                    }
                    //显示图片
                    displayImage(imagePath);
                    upload(imagePath);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void displayImage(String imagePath) {
        if(!TextUtils.isEmpty(imagePath)){
            //显示图片
            Glide.with(this).load(imagePath).apply(requestOptions).into(imageView);

            //压缩图片
            orc_bitmap = CameraUtils.compression(BitmapFactory.decodeFile(imagePath));
            //转Base64
            base64Pic = BitmapUtils.bitmapToBase64(orc_bitmap);
        }else
        {
            showMsg("图片获取失败");
        }

    }

    private void checkVersion(){
        //android 6.0及以上版本
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            rxPermissions = new RxPermissions(this);
            //  rxPermissions.request(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe()
            rxPermissions.request(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(granted ->{
                        if (granted){//申请成功
                            showMsg("已经获得权限");
                            hasPermissions = true;
                        }else{//申请失败
                            showMsg("权限未开启");
                            hasPermissions = false;
                        }
                    });
        }else{
            //android 6.0以下
            showMsg("无需申请权限");
        }
    }


    //上传图片到表中
    private void upload(String imgpath){
        Log.d("MainActivity111","11111111111111111111111111111");
        BmobFile bmobFile = new BmobFile(new File(imgpath));
        Log.d("MainActivity111","22222222222222222222222222222");
        Log.d("MainActivity111",imgpath);
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                Log.d("MainActivity111","3333333333333333333333333333333");
                if(e==null){

                    Toast.makeText(AddLostInformationActivity.this,"上传文件成功:" + bmobFile.getFileUrl(),Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(AddLostInformationActivity.this,"上传文件失败：" + e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

        });
        Log.d("MainActivity111","5555555555555555555555555555555555");
    }


        /**
         * @param titleName 标题
         * @param num       电话号码
         * @param descridle 描述
         * @param address
         */
    private void updataInfo(String titleName, String num, String descridle, String address,double latitudeone,double longitudeone) {
        Informationpublished informationpublished = new Informationpublished();
        informationpublished.setTitle(titleName);//titleName为用户输入的标题
        informationpublished.setPhoneNum(num);//num为用户输入的号码
        informationpublished.setDesc(descridle);//descridle为信息描述
        informationpublished.setAddress(address);
        informationpublished.setLatitude(latitudeone);
        informationpublished.setLatitude(longitudeone);
        informationpublished.update(infomationReq.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    showToast("更新信息成功");
                    //更新数据后提示主界面进行数据刷新
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    private void addData() {
        String titleName = title.getText().toString().trim();
        String Num = phoneNum.getText().toString().trim();
        String descridle = desc.getText().toString().trim();
        String addressone = address.getText().toString().trim();
        if (TextUtils.isEmpty(titleName)) {
            showToast("标题不能为空");
            return;
        }

        if (TextUtils.isEmpty(Num)) {
            showToast("手机号码不能为空");
            return;
        }

        if (TextUtils.isEmpty(descridle)) {
            showToast("描述不能为空");
            return;
        }

        //判断是发表新的信息还是更改信息
        if (isChangeInfos) {
            updataInfo(titleName, Num, descridle,addressone,latitudeone,longitudeone);
            //upload();
        } else {
            publishLostInfo(titleName, Num, descridle,addressone,latitudeone,longitudeone,imagePath);
        }
    }

    /**
     * @param titleName 标题
     * @param num       电话号码
     * @param descridle 描述
     * @param address
     */
    private void publishLostInfo(String titleName, String num, String descridle, String address,double latitudeon,double longitudeone,String imagepath) {

        BmobFile bmobFile = new BmobFile(new File(imagepath));
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Informationpublished informationpublished = new Informationpublished();
                    informationpublished.setTitle(titleName);//titleName为用户输入的标题
                    informationpublished.setPhoneNum(num);//num为用户输入的号码
                    informationpublished.setDesc(descridle);//descridle为信息描述
                    informationpublished.setAddress(address);
                    informationpublished.setLatitude(latitudeon);
                    informationpublished.setLongitude(longitudeone);
                    informationpublished.setImage(bmobFile);
                    informationpublished.setState(0);
                    informationpublished.setName(BmobUser.getCurrentUser(BmobUser.class).getUsername());
                    informationpublished.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            showToast("信息发布成功");
                            finish();
                        }
                    });
                }
            }
        });
       // informationpublished.setImage(bmobFile);

        /*
        Log.d("hello", "address : " + address);
        informationpublished.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    showToast("招领信息发布成功");

                    //成功后提示主界面刷新数据
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    //成功后将页面销毁
                    finish();
                } else {
                    showToast("信息发布失败");
                }
            }
        });

         */
    }

    private void initLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        Log.d("hello", "addr : 11111111111111");
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置定位请求超时时间，单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);
        //关闭缓存机制，高精度定位会产生缓存。
        mLocationOption.setLocationCacheEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //地址
                String addre = aMapLocation.getAddress();
                double latitude = aMapLocation.getLatitude();
                double longitude = aMapLocation.getLongitude();
                address.setText(addre);
                latitudeone = latitude;
                longitudeone = longitude;
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("纬度：" + latitude + "\n");
                stringBuffer.append("经度：" + longitude + "\n");
                stringBuffer.append("地址：" + addre + "\n");

            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }
}
