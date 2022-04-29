package com.erh.easyreaderhelper.fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.AutoTransition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.erh.easyreaderhelper.R;
import com.erh.easyreaderhelper.base.FloatingDraftButton;
import com.erh.easyreaderhelper.bean.Person;
import com.erh.easyreaderhelper.constants.Constants;
import com.erh.easyreaderhelper.util.AnimationUtil;
import com.erh.easyreaderhelper.util.BitmapUtils;
import com.erh.easyreaderhelper.util.CameraUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tbruyelle.rxpermissions2.RxPermissions;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import static android.app.Activity.RESULT_OK;

public class Threefragment extends Fragment {

   // @Bind(R.id.floatingActionButton)
    FloatingDraftButton floatingDraftButton;

  //  @Bind(R.id.floatingActionButton_liveness)
    FloatingActionButton liveness;
  //  @Bind(R.id.floatingActionButton_2)
    FloatingActionButton floatingActionButton2;
  //  @Bind(R.id.floatingActionButton_3)
    FloatingActionButton floatingActionButton3;
 //   @Bind(R.id.floatingActionButton_4)
    FloatingActionButton floatingActionButton4;
 //   @Bind(R.id.floatingActionButton_5)
    FloatingActionButton floatingActionButton5;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_three, container, false);
        Bmob.initialize(getActivity(), Constants.APPLICATION_ID);
       // Bmob.resetDomain("bmob-cdn-29723.b0.aicdn.com");
        floatingDraftButton = view.findViewById(R.id.floatingActionButton);
        liveness = view.findViewById(R.id.floatingActionButton_liveness);
        floatingActionButton2 = view.findViewById(R.id.floatingActionButton_2);
        floatingActionButton3 = view.findViewById(R.id.floatingActionButton_3);
        floatingActionButton4 = view.findViewById(R.id.floatingActionButton_4);
        floatingActionButton5 = view.findViewById(R.id.floatingActionButton_5);

        ButterKnife.bind(getActivity());


        floatingDraftButton.registerButton(liveness);
        floatingDraftButton.registerButton(floatingActionButton2);
        floatingDraftButton.registerButton(floatingActionButton3);
        floatingDraftButton.registerButton(floatingActionButton4);
        floatingDraftButton.registerButton(floatingActionButton5);


        floatingDraftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //弹出动态Button
                Toast.makeText(getContext(),"hello111111111",Toast.LENGTH_SHORT).show();
                AnimationUtil.slideButtons(getActivity(),floatingDraftButton);
            }
        });

        liveness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭动态Button
                 AnimationUtil.slideButtons(getActivity(),floatingDraftButton);
                Toast.makeText(getActivity().getApplicationContext(), "liveness", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

}
