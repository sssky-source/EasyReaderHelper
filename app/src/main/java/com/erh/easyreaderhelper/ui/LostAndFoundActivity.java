package com.erh.easyreaderhelper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.erh.easyreaderhelper.base.BaseActivity;
import com.erh.easyreaderhelper.base.ErhApplication;
import com.erh.easyreaderhelper.R;
import com.erh.easyreaderhelper.adapter.LostAndFoundAdapter;
import com.erh.easyreaderhelper.bean.LostInfomationReq;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class LostAndFoundActivity extends BaseActivity implements View.OnClickListener, LostAndFoundAdapter.ItemClickListener {

    private RecyclerView recyclerView;
    private ImageView addBtn;
    private LostAndFoundAdapter lostAndFoundAdapter;
    private long exitTime = 0;
    private final static int REQUEST_CODE = 999;
    private List<LostInfomationReq> lostInfomationReqList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lostandfound_main_activity);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.rl_recyclerview);
        addBtn = (ImageView) findViewById(R.id.iv_add);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        lostAndFoundAdapter = new LostAndFoundAdapter(LostAndFoundActivity.this);
        lostAndFoundAdapter.setLongClickListener(this);
    }

    private void initData() {
        BmobQuery<LostInfomationReq> lostInfomationReqBmobQuery = new BmobQuery<>();
        lostInfomationReqBmobQuery.order("-updatedAt");//排序
        lostInfomationReqBmobQuery.findObjects(new FindListener<LostInfomationReq>() {
            @Override
            public void done(List<LostInfomationReq> list, BmobException e) {
                if (e == null) {
                    lostInfomationReqList = list;
                    lostAndFoundAdapter.setData(list);
                    recyclerView.setAdapter(lostAndFoundAdapter);
                } else {
                    showToast("查询数据失败");
                }
            }
        });
    }

    private void initListener() {
        addBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add:
                Intent intent = new Intent(LostAndFoundActivity.this, AddLostInformationActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    refreshData();//数据刷新
                }
                break;
            default:
                break;
        }
    }

    /**
     * 查询数据库中最新的数据
     */
    private void refreshData() {
        BmobQuery<LostInfomationReq> lostInfomationReqBmobQuery = new BmobQuery<>();
        lostInfomationReqBmobQuery.order("-updatedAt");//按更新时间排序
        lostInfomationReqBmobQuery.findObjects(new FindListener<LostInfomationReq>() {
            @Override
            public void done(List<LostInfomationReq> list, BmobException e) {
                if (e == null) {
                    lostInfomationReqList = list;
                    lostAndFoundAdapter.setData(list);
                    lostAndFoundAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                showToast("再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                ErhApplication.getApplication().exit();
            }
        }
        return false;
    }

    @Override
    public void onEditOrDeleteClick(int position, int code) {

        if (code == LostAndFoundAdapter.EDIT_CODE) {
            Intent intent = new Intent(LostAndFoundActivity.this, AddLostInformationActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("editData", lostInfomationReqList.get(position));
            intent.putExtras(bundle);
            startActivityForResult(intent, REQUEST_CODE);
        } else if (code == LostAndFoundAdapter.DELETE_CODE) {
            deleteItemData(position);
        }
    }

    private void deleteItemData(final int position) {
        if (lostInfomationReqList.size() != 0) {
            LostInfomationReq lostInfomationReq = new LostInfomationReq();
            lostInfomationReq.setObjectId(lostInfomationReqList.get(position).getObjectId());
            lostInfomationReq.delete(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        lostInfomationReqList.remove(position);
                        lostAndFoundAdapter.setData(lostInfomationReqList);
                        lostAndFoundAdapter.notifyDataSetChanged();
                    } else {
                        showToast("删除数据失败");
                    }
                }
            });
        }
    }
}
