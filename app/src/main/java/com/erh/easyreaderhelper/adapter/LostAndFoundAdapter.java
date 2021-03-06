package com.erh.easyreaderhelper.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.erh.easyreaderhelper.R;
import com.erh.easyreaderhelper.bean.Informationpublished;
import com.erh.easyreaderhelper.ui.DetailedInformationActivity;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by Administrator on 2018/9/19.
 */

public class LostAndFoundAdapter extends RecyclerView.Adapter<LostAndFoundAdapter.LostAndFoundHolder> {
    private Context mContext;
    private List<Informationpublished> lostInfosData;
    private ItemClickListener mItemClickListener;
    public final static int EDIT_CODE = 998;
    public final static int DELETE_CODE = 997;

    public LostAndFoundAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<Informationpublished> data) {
        this.lostInfosData = data;
    }

    @Override
    public LostAndFoundHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.lost_item, parent, false);
        LostAndFoundHolder lostAndFoundHolder = new LostAndFoundHolder(view);
        return lostAndFoundHolder;
    }

    @Override
    public void onBindViewHolder(final LostAndFoundHolder holder, final int position) {
        if (lostInfosData != null) {
            Informationpublished informationpublished = lostInfosData.get(position);
            holder.title.setText(informationpublished.getTitle());
            holder.desc.setText(informationpublished.getDesc());
            holder.phoneNum.setText(informationpublished.getPhoneNum());
            holder.time.setText(informationpublished.getCreatedAt());
            String name = informationpublished.getName();
            String objectId = informationpublished.getObjectId();

            holder.llItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, DetailedInformationActivity.class);
                    intent.putExtra("name",name);
                    intent.putExtra("objectId",objectId);
                    mContext.startActivity(intent);
                }
            });
            /*
            holder.llItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //mLongClickListener.onLongClick(position);
                    showWindow(holder, position);
                    return false;
                }
            });
             */
        }
    }

    private void showWindow(LostAndFoundHolder holder, final int pos) {
        //??????????????????
        View contentview = LayoutInflater.from(mContext).inflate(R.layout.pop_window_view,null);
        final PopupWindow popupWindow = new PopupWindow(contentview, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        //????????????
        popupWindow.setFocusable(true);
        //????????????
        popupWindow.setOutsideTouchable(true);
        //???????????????????????????PopupWindow??????
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        //???????????????
        popupWindow.showAsDropDown(holder.time, 300, -100);

        //showAsDropDown(View anchor)????????????????????????????????????????????????????????????
       // showAsDropDown(View anchor, int xoff, int yoff)??????????????????????????????????????????
        //showAtLocation(View parent, int gravity, int x, int y)????????????????????????????????????????????????Gravity.CENTER?????????Gravity.BOTTOM???????????????????????????????????????

        //??????????????????
        contentview.findViewById(R.id.edit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //???????????????????????????????????????
                mItemClickListener.onEditOrDeleteClick(pos, EDIT_CODE);
                //???????????????
                popupWindow.dismiss();
            }
        });

        //??????????????????
        contentview.findViewById(R.id.delete_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //???????????????????????????????????????
                mItemClickListener.onEditOrDeleteClick(pos, DELETE_CODE);
                //???????????????
                popupWindow.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return lostInfosData.size() == 0 ? 0 : lostInfosData.size();
    }

    public class LostAndFoundHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView desc;
        private TextView time;
        private TextView phoneNum;
        private LinearLayout llItem;

        public LostAndFoundHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            desc = (TextView) itemView.findViewById(R.id.tv_desc);
            time = (TextView) itemView.findViewById(R.id.tv_time);
            phoneNum = (TextView) itemView.findViewById(R.id.tv_num);
            llItem = (LinearLayout) itemView.findViewById(R.id.ll_item);
        }
    }

    //??????????????????
    public void setLongClickListener(ItemClickListener clickListener) {
        this.mItemClickListener = clickListener;
    }

    public interface ItemClickListener {
        void onEditOrDeleteClick(int position, int code);
    }
}
