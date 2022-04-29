package com.erh.easyreaderhelper.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.erh.easyreaderhelper.R;
import com.erh.easyreaderhelper.bean.Person;
import com.erh.easyreaderhelper.ui.LoginActivity;
import com.erh.easyreaderhelper.ui.MyBorrowedActivity;
import com.erh.easyreaderhelper.ui.MyPublishedActivity;
import com.erh.easyreaderhelper.ui.MyRentActivity;
import com.erh.easyreaderhelper.util.BitmapUtils;
import com.erh.easyreaderhelper.util.CameraUtils;
import com.erh.easyreaderhelper.util.CircleUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import static android.app.Activity.RESULT_OK;


public class Fourfragment extends Fragment{


    public Dialog setHeadDialog;
    public View mDialogView;
    public ImageView imageViewhead,mypublished,myborrowed,myrent;
    public Uri imageUri;
    public TextView username;
    public View view;
    public String path;
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

    //Glide请求图片选项配置
    private RequestOptions requestOptions = RequestOptions.circleCropTransform()
            .diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
            .skipMemoryCache(true);//不做内存缓存

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_four, container, false);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.detectFileUriExposure();
        }
        initView();
        setListeners();
        return view;
    }


    private void setListeners(){
        OnClick onClick = new OnClick();
        imageViewhead.setOnClickListener(onClick);
        username.setOnClickListener(onClick);
        mypublished.setOnClickListener(onClick);
        myborrowed.setOnClickListener(onClick);
        myrent.setOnClickListener(onClick);
    }

    private class OnClick implements View.OnClickListener{
        @Override
        public void onClick(View v){
            switch (v.getId()){
                case R.id.head:
                    showDialog();

                    Toast.makeText(getContext(),"你点击了头像",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.username:
                    Toast.makeText(getContext(),"你点击了用户名",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.image1:
                    Intent intent = new Intent(getActivity(), MyPublishedActivity.class);
                    startActivity(intent);
                    break;
                case R.id.image2:
                    Intent intent1 = new Intent(getActivity(), MyBorrowedActivity.class);
                    startActivity(intent1);
                    break;
                case R.id.image3:
                    Intent intent3 = new Intent(getActivity(), MyRentActivity.class);
                    startActivity(intent3);
                    break;
                    default:
                        break;
            }
        }
    }



    private void initView() {
        imageViewhead = (ImageView)view.findViewById(R.id.head);
        username = (TextView) view.findViewById(R.id.username);
        mypublished = (ImageView) view.findViewById(R.id.image1);
        myborrowed = (ImageView) view.findViewById(R.id.image2);
        myrent = (ImageView) view.findViewById(R.id.image3);
        setHead();
        if (BmobUser.getCurrentUser(BmobUser.class)==null){
            username.setText("未登录");
        }else {
            username.setText((CharSequence) BmobUser.getCurrentUser(BmobUser.class).getUsername());
        }
    }

    private void showDialog() {
        setHeadDialog = new Dialog(getActivity(),R.style.Theme_Light_Dialog);
        mDialogView = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog,null);
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
        outputImagePath = new File(getActivity().getExternalCacheDir(),filename + ".jpg");
        Intent takePhotoIntent = CameraUtils.getTakePhotoIntent(getContext(),outputImagePath);
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
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
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
               //  upload(CameraUtils.getImagePath(imageUri,null,getContext()));
                break;
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    String imagePath = null;
                    //判断手机系统版本号
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                        //4.4以上系统使用这个方法处理图片
                        Log.d("camera", "111111111111111111111");
                        imagePath = CameraUtils.getImageOnKitKatPath(data, getContext());
                        Log.d("camera", "17777777777777771");
                        Log.d("camera", imagePath);
                        Log.d("camera", "1888888888888881");
                    } else {
                        imagePath = CameraUtils.getImageBeforeKitKatPath(data, getContext());
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
            Glide.with(this).load(imagePath).apply(requestOptions).into(imageViewhead);

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
            rxPermissions = new RxPermissions(getActivity());
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


    //登陆后获取设置的与用户名匹配的头像
    private void setHead(){
        //下载图片
        BmobQuery<Person> query=new BmobQuery<Person>();
        query.findObjects(new FindListener<Person>() {
            @Override
            public void done(List<Person> list, BmobException e) {
                if(e == null){
                    show_ad(list);
                }else{
                    Toast.makeText(getContext(),""+e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public void show_ad(List<Person> list){
        for (Person ad : list){
            if(ad.getName() != null && BmobUser.getCurrentUser(BmobUser.class).getUsername().equals(ad.getName())){
                BmobFile icon= ad.getImage();
                icon.download(new DownloadFileListener() {
                    @Override
                    public void onProgress(Integer integer, long l) {

                    }
                    @Override
                    public void done(String s, BmobException e) {
                        if(e == null){
                            //设置圆形头像并显示
                            imageViewhead.setImageBitmap(CircleUtils.toRoundBitmapone(BitmapFactory.decodeFile(s))); //根据地址解码并显示图片
                        }
                    }
                });
                break;
            }
        }

    }

    //上传图片到表中
    private void upload(String imgpath){
        Log.d("MainActivity111","11111111111111111111111111111");
        BmobFile bmobFile = new BmobFile(new File(imgpath));
        Log.d("MainActivity111","22222222222222222222222222222");
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                Log.d("MainActivity111","3333333333333333333333333333333");
                if(e==null){
                    BmobQuery<Person> query = new BmobQuery<>();
                    query.addWhereEqualTo("name",BmobUser.getCurrentUser(BmobUser.class).getUsername());
                    final String[] objectId = new String[1];
                    query.findObjects(new FindListener<Person>() {
                        @Override
                        public void done(List<Person> list, BmobException e) {
                            if (e == null){
                                for (Person ad : list){
                                    objectId[0] = ad.getObjectId();
                                   // Log.w(TAG, "获取id成功"+ objectId[0]);
                                }
                                Person ad2 = new Person();
                                ad2.setImage(bmobFile);
                                ad2.update(objectId[0], new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null){
                                            Toast.makeText(getContext(), "信息更新成功", Toast.LENGTH_SHORT).show();
                                        }else {
                                        //    Log.w(TAG, "失败"+e.getErrorCode());
                                        }
                                    }
                                });
                            }else {
                               // Log.w(TAG, "获取id失败"+e.getErrorCode());
                            }
                        }
                    });
                }else{
                    Toast.makeText(getContext(),"上传文件失败：" + e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

}


