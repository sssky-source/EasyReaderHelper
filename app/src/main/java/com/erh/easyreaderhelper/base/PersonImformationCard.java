package com.erh.easyreaderhelper.base;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.erh.easyreaderhelper.R;
import com.erh.easyreaderhelper.ui.LoginActivity;

import cn.bmob.v3.BmobUser;

public class PersonImformationCard extends RelativeLayout {

    public ImageView imageViewhead;
    public TextView username;

    public PersonImformationCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_mine_top_1,this);
        initView();
        username.setText((CharSequence) BmobUser.getCurrentUser().getUsername());
        setListeners();
    }

    private void initView() {
        imageViewhead = (ImageView)findViewById(R.id.head);
        username = (TextView) findViewById(R.id.username);
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
                    Toast.makeText(getContext(),"你点击了头像",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.username:



                    Toast.makeText(getContext(),"你点击了用户名",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

}
