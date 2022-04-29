package com.erh.easyreaderhelper.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.erh.easyreaderhelper.R;
import com.erh.easyreaderhelper.bean.Informationpublished;
import com.erh.easyreaderhelper.util.ImageFilter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.modules.conversation.model.EaseConversationInfo;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;


public class DetailedInformationActivity extends AppCompatActivity {

    private Button button_borrow,button_back,button_chat;
    private String name,objectId;
    private ImageView ivBg,img_chat;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inform_bottom);
        initViews();

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        objectId = intent.getStringExtra("objectId");


        //拿到初始图
        Bitmap bmp= BitmapFactory.decodeResource(getResources(),R.drawable.idol);
        //处理得到模糊效果的图
        Bitmap blurBitmap = ImageFilter.blurBitmap(this, bmp, 25f);
        ivBg.setImageBitmap(blurBitmap);

        //伸缩偏移量监听
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {//收缩时
                    collapsingToolbarLayout.setTitle("初学者-Study");
                    isShow = true;
                } else if (isShow) {//展开时
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });


        button_borrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                borrow();
            }
        });
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });
        img_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chat();
            }
        });
        /*
        button_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

*/
    }

    private void initViews() {
        button_borrow = (Button)findViewById(R.id.button_rent);
        button_back = (Button)findViewById(R.id.bt_guihuan);
        img_chat = (ImageView) findViewById(R.id.img_chat);
        //     button_chat = (Button) findViewById(R.id.button_chat);
        ivBg = findViewById(R.id.iv_bg);
        collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        appBarLayout = findViewById(R.id.appbar_layout);
    }

    private void chat() {
        Intent intent = new Intent(DetailedInformationActivity.this,ChattingActivity.class);
        intent.putExtra(EaseConstant.EXTRA_CONVERSATION_ID, name);
        startActivity(intent);
    }


    public void borrow(){
        Informationpublished p2 = new Informationpublished();
        p2.setState(1);
        p2.setRenter(BmobUser.getCurrentUser(BmobUser.class).getUsername());
        p2.update(objectId, new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if(e==null){
                    Toast.makeText(DetailedInformationActivity.this,"租借成功",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(DetailedInformationActivity.this,"租借失败",Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    //归还物品按钮
    public void back(){
        //更新Person表里面id为objectId的数据
        Informationpublished p = new Informationpublished();
        p.setState(2);
        p.setRenter(null);
        p.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Toast.makeText(DetailedInformationActivity.this,"归还成功",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(DetailedInformationActivity.this,"归还失败",Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

}
