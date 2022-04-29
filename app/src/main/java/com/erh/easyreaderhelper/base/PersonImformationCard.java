package com.erh.easyreaderhelper.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;



import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.erh.easyreaderhelper.R;
import com.erh.easyreaderhelper.ui.LoginActivity;
import com.erh.easyreaderhelper.ui.MainActivity;
import com.erh.easyreaderhelper.util.CircleUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.bmob.v3.BmobUser;

import static cn.bmob.v3.Bmob.getApplicationContext;

public class PersonImformationCard extends RelativeLayout {

    public static final int TAKE_PHOTO = 1;
    public Dialog setHeadDialog;
    public View mDialogView;
    public ImageView imageViewhead;
    public Uri imageUri;
    public TextView username;

    public PersonImformationCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_mine_top_1,this);
   //     initView();

       // setListeners();
    }
/*
    public PersonImformationCard(Activity activity) {
        super(activity);
        this.activity = activity;

    }


    @SuppressLint("WrongViewCast")
    private void initView() {
        imageViewhead = (ImageView)findViewById(R.id.head);
        username = (TextView) findViewById(R.id.username);

        if (BmobUser.getCurrentUser()==null){
            username.setText("未登录");
        }else {
            username.setText((CharSequence) BmobUser.getCurrentUser().getUsername());
        }
    }

    private void setListeners(){
        OnClick onClick = new OnClick();
        imageViewhead.setOnClickListener(onClick);
        username.setOnClickListener(onClick);
    }

    private class OnClick implements View.OnClickListener{
        @Override
        public void onClick(View v){
            switch (v.getId()){
                case R.id.head:
                    showDialog();
                    Toast.makeText(getContext(),"你点击了头像",Toast.LENGTH_SHORT).show();
                  //  ProgramSelectDialog dialog = new ProgramSelectDialog(getContext());
                    break;
                case R.id.username:
                    Toast.makeText(getContext(),"你点击了用户名",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private void showDialog() {
        setHeadDialog = new Dialog(getContext(),R.style.Theme_Light_Dialog);
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

        cameraButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                File outputImage = new File(getContext().getExternalCacheDir(),"output_image.jpg");
                try{
                    if (outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= 24){
                    imageUri = FileProvider.getUriForFile(getContext(),"com.erh.easyreaderhelper.fileprovider",outputImage);
                }else {
                    imageUri = Uri.fromFile(outputImage);
                }
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                activity.startActivityForResult(intent,TAKE_PHOTO);
//                onActivityResult(TAKE_PHOTO,Activity.RESULT_OK,intent);
                setHeadDialog.dismiss();

            }


        });
        photoButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setHeadDialog.dismiss();
            }
        });
        cancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setHeadDialog.dismiss();
            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode == Activity.RESULT_OK){
                    try{
                        Bitmap bitmap = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(imageUri)); //将一个流对象转化为一个Bitmap对象
                        Bitmap bitmap1 = CircleUtils.toRoundBitmapone(bitmap);
                        imageViewhead.setImageBitmap(bitmap1);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }
*/
}


