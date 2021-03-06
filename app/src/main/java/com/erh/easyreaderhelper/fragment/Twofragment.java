package com.erh.easyreaderhelper.fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.erh.easyreaderhelper.base.ErhApplication;
import com.erh.easyreaderhelper.R;
import com.erh.easyreaderhelper.adapter.LostAndFoundAdapter;
import com.erh.easyreaderhelper.bean.Informationpublished;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class Twofragment extends Fragment implements LostAndFoundAdapter.ItemClickListener{


    private RecyclerView recyclerView;
    private ImageView addBtn;
    private LostAndFoundAdapter lostAndFoundAdapter;
    private long exitTime = 0;
    private final static int REQUEST_CODE = 999;
    private List<Informationpublished> informationpublishedList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_two, container, false);
        initView(view);
        initData();

        return view;
    }

    private void initView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.rl_recyclerviewtwo);
        addBtn = (ImageView) view.findViewById(R.id.iv_add);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        lostAndFoundAdapter = new LostAndFoundAdapter(getContext());
        lostAndFoundAdapter.setLongClickListener(this);
    }

    private void initData() {
        BmobQuery<Informationpublished> lostInfomationReqBmobQuery = new BmobQuery<>();
        lostInfomationReqBmobQuery.order("-updatedAt");//??????
        lostInfomationReqBmobQuery.addWhereEqualTo("state", 0);
        lostInfomationReqBmobQuery.findObjects(new FindListener<Informationpublished>() {
            @Override
            public void done(List<Informationpublished> list, BmobException e) {
                if (e == null) {
                    informationpublishedList = list;
                    lostAndFoundAdapter.setData(list);
                    recyclerView.setAdapter(lostAndFoundAdapter);
                } else {
                    //showToast("??????????????????");
                }
            }
        });
    }



    /**
     * ?????????????????????????????????
     */
    private void refreshData() {
        BmobQuery<Informationpublished> lostInfomationReqBmobQuery = new BmobQuery<>();
        lostInfomationReqBmobQuery.order("-updatedAt");//?????????????????????
        lostInfomationReqBmobQuery.findObjects(new FindListener<Informationpublished>() {
            @Override
            public void done(List<Informationpublished> list, BmobException e) {
                if (e == null) {
                    informationpublishedList = list;
                    lostAndFoundAdapter.setData(list);
                    lostAndFoundAdapter.notifyDataSetChanged();
                }
            }
        });
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - exitTime > 2000) {
               //showToast("????????????????????????");
                exitTime = System.currentTimeMillis();
            } else {
                ErhApplication.getApplication().exit();
            }
        }
        return false;
    }



    private void deleteItemData(final int position) {
        if (informationpublishedList.size() != 0) {
            Informationpublished informationpublished = new Informationpublished();
            informationpublished.setObjectId(informationpublishedList.get(position).getObjectId());
            informationpublished.delete(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        informationpublishedList.remove(position);
                        lostAndFoundAdapter.setData(informationpublishedList);
                        lostAndFoundAdapter.notifyDataSetChanged();
                    } else {
                        //showToast("??????????????????");
                    }
                }
            });
        }
    }


    @Override
    public void onEditOrDeleteClick(int position, int code) {

    }
}
