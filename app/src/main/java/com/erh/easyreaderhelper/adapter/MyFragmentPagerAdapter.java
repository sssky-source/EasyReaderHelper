package com.erh.easyreaderhelper.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.erh.easyreaderhelper.ui.MainActivity;
import com.erh.easyreaderhelper.fragment.Fourfragment;
import com.erh.easyreaderhelper.fragment.Onefragment;
import com.erh.easyreaderhelper.fragment.Threefragment;
import com.erh.easyreaderhelper.fragment.Twofragment;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private final int PAGER_COUNT = 4;

    private Onefragment onefragment;
    private Twofragment twofragment;
    private Threefragment threefragment;
    private Fourfragment fourfragment;

    public MyFragmentPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
        onefragment = new Onefragment();
        twofragment = new Twofragment();
        threefragment = new Threefragment();
        fourfragment = new Fourfragment();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case MainActivity.PAGE_ONE:
                fragment = onefragment;
                break;
            case MainActivity.PAGE_TWO:
                fragment = twofragment;
                break;
            case MainActivity.PAGE_THREE:
                fragment = threefragment;
                break;
            case MainActivity.PAGE_FOUR:
                fragment = fourfragment;
                break;
        }
        return fragment;

    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }
}
