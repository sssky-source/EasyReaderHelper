package com.erh.easyreaderhelper.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.erh.easyreaderhelper.R;

public class Onefragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_one, container, false);
        TextView textView = view.findViewById(R.id.one_text);
        textView.setText("onetext");
        return view;
    }

}
