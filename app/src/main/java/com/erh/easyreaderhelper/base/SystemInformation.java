package com.erh.easyreaderhelper.base;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.erh.easyreaderhelper.R;
import com.erh.easyreaderhelper.ui.LoginActivity;
import com.erh.easyreaderhelper.ui.MainActivity;

import cn.bmob.v3.BmobUser;

public class SystemInformation extends LinearLayout {

    public ImageView imageView1,imageView2,imageView3,imageView4;
    public Context mContext;

    public SystemInformation(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = getContext();
        LayoutInflater.from(context).inflate(R.layout.item_mine_top_2,this);
        initView();
        setListeners();
    }

    private void initView() {
        imageView1 = (ImageView) findViewById(R.id.image1);
        imageView2 = (ImageView) findViewById(R.id.image2);
        imageView3 = (ImageView) findViewById(R.id.image3);
        imageView4 = (ImageView) findViewById(R.id.image4);
    }

    private void setListeners(){
        OnClick onClick = new OnClick();
        imageView4.setOnClickListener(onClick);
        imageView3.setOnClickListener(onClick);
        imageView2.setOnClickListener(onClick);
        imageView1.setOnClickListener(onClick);
    }

    private class OnClick implements View.OnClickListener{
        @Override
        public void onClick(View v){

            switch (v.getId()){
                case R.id.image1:
                    Toast.makeText(getContext(),"你点击了设置",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.image2:
                    Toast.makeText(getContext(),"你点击了客服",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.image3:
                    Toast.makeText(getContext(),"你点击了关于",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.image4:
                    Intent intent = null;
                    intent = new Intent(getContext(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);
                    break;


            }
        }
    }

}
