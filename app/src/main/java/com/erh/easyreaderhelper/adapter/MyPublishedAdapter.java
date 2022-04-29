package com.erh.easyreaderhelper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.erh.easyreaderhelper.R;
import com.erh.easyreaderhelper.bean.Informationpublished;
import com.erh.easyreaderhelper.ui.MyPublishedActivity;

import java.util.List;

public class MyPublishedAdapter extends RecyclerView.Adapter<MyPublishedAdapter.ViewHolder> {

    private Context mContext;
    private List<Informationpublished> lostInfosData;

    public MyPublishedAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<Informationpublished> data) {
        this.lostInfosData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.lost_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return lostInfosData.size() == 0 ? 0 : lostInfosData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
