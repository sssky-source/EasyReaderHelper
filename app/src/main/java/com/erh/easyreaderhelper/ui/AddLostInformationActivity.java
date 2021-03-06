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


    //????????????
    private RxPermissions rxPermissions;
    private String base64Pic;
    //??????????????????????????????Bitmap
    private Bitmap orc_bitmap;
    //??????????????????
    private boolean hasPermissions =false;

    //???????????????????????????
    private File outputImagePath;
    //??????????????????
    //??????????????????
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

    //??????AMapLocationClient?????????
    public AMapLocationClient mLocationClient = null;
    //??????AMapLocationClientOption??????
    public AMapLocationClientOption mLocationOption = null;


    //Glide????????????????????????
    private RequestOptions requestOptions = RequestOptions.circleCropTransform()
            .diskCacheStrategy(DiskCacheStrategy.NONE)//??????????????????
            .skipMemoryCache(true);//??????????????????


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
            isChangeInfos = true;//?????????????????????????????????
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
        //?????????????????????????????????
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //?????????????????????set??????
        window.setAttributes(lp);
        //???????????????????????????dialog???
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
                takePhoto();//??????
                showMsg("??????");
                setHeadDialog.dismiss();
            }
        });

        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                openAlbum();//?????????
                showMsg("????????????");
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

    //??????
    private void takePhoto(){
        if(!hasPermissions){
            showMsg("??????????????????");
            checkVersion();
            return;
        }
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String filename = timeStampFormat.format(new Date());
        outputImagePath = new File(this.getExternalCacheDir(),filename + ".jpg");
        Intent takePhotoIntent = CameraUtils.getTakePhotoIntent(AddLostInformationActivity.this,outputImagePath);
        //??????????????????????????????Activity???????????????TAKE_PHOTO
        startActivityForResult(takePhotoIntent,TAKE_PHOTO);
    }


    //?????????
    private void openAlbum(){
        if(!hasPermissions){
            showMsg("??????????????????");
            checkVersion();
            return;
        }
        startActivityForResult(CameraUtils.getSelectPhotoIntent(),SELECT_PHOTO);
    }

    //???????????? Toast
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
                    //???????????????????????????
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                        //4.4??????????????????????????????????????????
                        Log.d("camera", "111111111111111111111");
                        imagePath = CameraUtils.getImageOnKitKatPath(data,AddLostInformationActivity.this);
                        Log.d("camera", "17777777777777771");
                        Log.d("camera", imagePath);
                        Log.d("camera", "1888888888888881");
                    } else {
                        imagePath = CameraUtils.getImageBeforeKitKatPath(data,AddLostInformationActivity.this);
                    }
                    //????????????
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
            //????????????
            Glide.with(this).load(imagePath).apply(requestOptions).into(imageView);

            //????????????
            orc_bitmap = CameraUtils.compression(BitmapFactory.decodeFile(imagePath));
            //???Base64
            base64Pic = BitmapUtils.bitmapToBase64(orc_bitmap);
        }else
        {
            showMsg("??????????????????");
        }

    }

    private void checkVersion(){
        //android 6.0???????????????
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            rxPermissions = new RxPermissions(this);
            //  rxPermissions.request(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe()
            rxPermissions.request(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(granted ->{
                        if (granted){//????????????
                            showMsg("??????????????????");
                            hasPermissions = true;
                        }else{//????????????
                            showMsg("???????????????");
                            hasPermissions = false;
                        }
                    });
        }else{
            //android 6.0??????
            showMsg("??????????????????");
        }
    }


    //?????????????????????
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

                    Toast.makeText(AddLostInformationActivity.this,"??????????????????:" + bmobFile.getFileUrl(),Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(AddLostInformationActivity.this,"?????????????????????" + e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

        });
        Log.d("MainActivity111","5555555555555555555555555555555555");
    }


        /**
         * @param titleName ??????
         * @param num       ????????????
         * @param descridle ??????
         * @param address
         */
    private void updataInfo(String titleName, String num, String descridle, String address,double latitudeone,double longitudeone) {
        Informationpublished informationpublished = new Informationpublished();
        informationpublished.setTitle(titleName);//titleName????????????????????????
        informationpublished.setPhoneNum(num);//num????????????????????????
        informationpublished.setDesc(descridle);//descridle???????????????
        informationpublished.setAddress(address);
        informationpublished.setLatitude(latitudeone);
        informationpublished.setLatitude(longitudeone);
        informationpublished.update(infomationReq.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    showToast("??????????????????");
                    //????????????????????????????????????????????????
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
            showToast("??????????????????");
            return;
        }

        if (TextUtils.isEmpty(Num)) {
            showToast("????????????????????????");
            return;
        }

        if (TextUtils.isEmpty(descridle)) {
            showToast("??????????????????");
            return;
        }

        //?????????????????????????????????????????????
        if (isChangeInfos) {
            updataInfo(titleName, Num, descridle,addressone,latitudeone,longitudeone);
            //upload();
        } else {
            publishLostInfo(titleName, Num, descridle,addressone,latitudeone,longitudeone,imagePath);
        }
    }

    /**
     * @param titleName ??????
     * @param num       ????????????
     * @param descridle ??????
     * @param address
     */
    private void publishLostInfo(String titleName, String num, String descridle, String address,double latitudeon,double longitudeone,String imagepath) {

        BmobFile bmobFile = new BmobFile(new File(imagepath));
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Informationpublished informationpublished = new Informationpublished();
                    informationpublished.setTitle(titleName);//titleName????????????????????????
                    informationpublished.setPhoneNum(num);//num????????????????????????
                    informationpublished.setDesc(descridle);//descridle???????????????
                    informationpublished.setAddress(address);
                    informationpublished.setLatitude(latitudeon);
                    informationpublished.setLongitude(longitudeone);
                    informationpublished.setImage(bmobFile);
                    informationpublished.setState(0);
                    informationpublished.setName(BmobUser.getCurrentUser(BmobUser.class).getUsername());
                    informationpublished.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            showToast("??????????????????");
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
                    showToast("????????????????????????");

                    //????????????????????????????????????
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    //????????????????????????
                    finish();
                } else {
                    showToast("??????????????????");
                }
            }
        });

         */
    }

    private void initLocation() {
        //???????????????
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //????????????????????????
        mLocationClient.setLocationListener(this);
        Log.d("hello", "addr : 11111111111111");
        //?????????AMapLocationClientOption??????
        mLocationOption = new AMapLocationClientOption();
        //?????????????????????AMapLocationMode.Hight_Accuracy?????????????????????
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //????????????3s???????????????????????????????????????
        //??????setOnceLocationLatest(boolean b)?????????true??????????????????SDK???????????????3s?????????????????????????????????????????????????????????true???setOnceLocation(boolean b)????????????????????????true???????????????????????????false???
        mLocationOption.setOnceLocationLatest(true);
        //????????????????????????????????????????????????????????????
        mLocationOption.setNeedAddress(true);
        //?????????????????????????????????????????????????????????30000???????????????????????????????????????8000?????????
        mLocationOption.setHttpTimeOut(20000);
        //??????????????????????????????????????????????????????
        mLocationOption.setLocationCacheEnable(false);
        //??????????????????????????????????????????
        mLocationClient.setLocationOption(mLocationOption);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //??????
                String addre = aMapLocation.getAddress();
                double latitude = aMapLocation.getLatitude();
                double longitude = aMapLocation.getLongitude();
                address.setText(addre);
                latitudeone = latitude;
                longitudeone = longitude;
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("?????????" + latitude + "\n");
                stringBuffer.append("?????????" + longitude + "\n");
                stringBuffer.append("?????????" + addre + "\n");

            } else {
                //???????????????????????????ErrCode????????????????????????????????????????????????errInfo???????????????????????????????????????
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }
}
