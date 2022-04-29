package com.erh.easyreaderhelper.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.erh.easyreaderhelper.R;
import com.erh.easyreaderhelper.adapter.LostAndFoundAdapter;
import com.erh.easyreaderhelper.bean.Informationpublished;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MyBorrowedActivity extends AppCompatActivity {


    private List<Informationpublished> informationpublishedList;
    private RecyclerView recyclerView;
    //  private MyPublishedAdapter myPublishedAdapter;
    private LostAndFoundAdapter myborrowedAdapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_borrowed);
        initViews();
        initData(1);
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_rent);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        recyclerView = (RecyclerView)findViewById(R.id.mybor_recy);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyBorrowedActivity.this, LinearLayoutManager.VERTICAL, false));
        // myPublishedAdapter = new MyPublishedAdapter(MyPublishedActivity.this);
        myborrowedAdapter = new LostAndFoundAdapter(MyBorrowedActivity.this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        //导入菜单布局
        getMenuInflater().inflate(R.menu.menu_rent, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //创建菜单项的点击事件
        switch (item.getItemId()) {
            case R.id.mune_rentongoing:
                Toast.makeText(this, "选择了已发布",Toast.LENGTH_SHORT).show();
                initData(1);
                break;
            case R.id.mune_rentcomplete:
                Toast.makeText(this, "选择了未完成",Toast.LENGTH_SHORT).show();
                initData(2);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initData(int state) {
        BmobQuery<Informationpublished> lostInfomationReqBmobQuery = new BmobQuery<>();
        lostInfomationReqBmobQuery.addWhereEqualTo("renter", BmobUser.getCurrentUser(BmobUser.class).getUsername());
        BmobQuery<Informationpublished> lostInfomationReqBmobQuery1 = new BmobQuery<>();
        lostInfomationReqBmobQuery1.addWhereEqualTo("state",state);
        //   lostInfomationReqBmobQuery.order("-updatedAt");//排序
        List<BmobQuery<Informationpublished>> andquerys = new ArrayList<>();
        andquerys.add(lostInfomationReqBmobQuery);
        andquerys.add(lostInfomationReqBmobQuery1);
        //查询符合整个and条件的人
        BmobQuery<Informationpublished> query = new BmobQuery<>();
        query.and(andquerys);
        query.findObjects(new FindListener<Informationpublished>() {
            @Override
            public void done(List<Informationpublished> list, BmobException e) {
                if (e == null) {
                    informationpublishedList = list;
                    myborrowedAdapter.setData(list);
                    recyclerView.setAdapter(myborrowedAdapter);
                    myborrowedAdapter.notifyDataSetChanged();
                } else {
                    //showToast("查询数据失败");
                }
            }
        });
    }


}
