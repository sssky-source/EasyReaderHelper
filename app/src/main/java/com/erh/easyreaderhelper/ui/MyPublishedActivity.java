package com.erh.easyreaderhelper.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.erh.easyreaderhelper.R;
import com.erh.easyreaderhelper.adapter.LostAndFoundAdapter;
import com.erh.easyreaderhelper.adapter.MyPublishedAdapter;
import com.erh.easyreaderhelper.bean.Informationpublished;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MyPublishedActivity extends AppCompatActivity {


    private List<Informationpublished> informationpublishedList;
    private RecyclerView recyclerView;
  //  private MyPublishedAdapter myPublishedAdapter;
    private LostAndFoundAdapter myPublishedAdapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_published);
        initViews();

        initData(0);
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        recyclerView = (RecyclerView)findViewById(R.id.mypub_recy);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyPublishedActivity.this, LinearLayoutManager.VERTICAL, false));
       // myPublishedAdapter = new MyPublishedAdapter(MyPublishedActivity.this);
        myPublishedAdapter = new LostAndFoundAdapter(MyPublishedActivity.this);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        //导入菜单布局
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //创建菜单项的点击事件
        switch (item.getItemId()) {
            case R.id.mune_uncomplete:
                Toast.makeText(this, "选择了已发布",Toast.LENGTH_SHORT).show();
                initData(0);
                break;
            case R.id.mune_ongoing:
                Toast.makeText(this, "选择了未完成",Toast.LENGTH_SHORT).show();
                initData(1);
                break;
            case R.id.mune_complete:
                initData(2);
                Toast.makeText(this, "选择了已完成",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    private void initData(int state) {
        BmobQuery<Informationpublished> lostInfomationReqBmobQuery = new BmobQuery<>();
        lostInfomationReqBmobQuery.addWhereEqualTo("name", BmobUser.getCurrentUser(BmobUser.class).getUsername());
        BmobQuery<Informationpublished> lostInfomationReqBmobQuery1 = new BmobQuery<>();
        lostInfomationReqBmobQuery1.addWhereEqualTo("state",state);
        List<BmobQuery<Informationpublished>> andquerys = new ArrayList<>();
        andquerys.add(lostInfomationReqBmobQuery);
        andquerys.add(lostInfomationReqBmobQuery1);
        //查询符合整个and条件的人
        BmobQuery<Informationpublished> query = new BmobQuery<>();
        query.and(andquerys);
        Log.d("My","111111111111");
       // lostInfomationReqBmobQuery.order("-updatedAt");//排序
        query.findObjects(new FindListener<Informationpublished>() {
            @Override
            public void done(List<Informationpublished> list, BmobException e) {
                if (e == null) {
                    informationpublishedList = list;
                    myPublishedAdapter.setData(list);
                    recyclerView.setAdapter(myPublishedAdapter);
                    myPublishedAdapter.notifyDataSetChanged();

                } else {
                    //showToast("查询数据失败");
                }
            }
        });
    }

    private void refreshData() {
        BmobQuery<Informationpublished> lostInfomationReqBmobQuery = new BmobQuery<>();
        lostInfomationReqBmobQuery.addWhereEqualTo("name", BmobUser.getCurrentUser(BmobUser.class).getUsername());
        lostInfomationReqBmobQuery.order("-updatedAt");//按更新时间排序
        lostInfomationReqBmobQuery.findObjects(new FindListener<Informationpublished>() {
            @Override
            public void done(List<Informationpublished> list, BmobException e) {
                if (e == null) {
                    informationpublishedList = list;
                    myPublishedAdapter.setData(list);
                    recyclerView.setAdapter(myPublishedAdapter);
                    myPublishedAdapter.notifyDataSetChanged();
                }
            }
        });
    }

}
