package com.erh.easyreaderhelper.ui;

import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.erh.easyreaderhelper.R;
import com.erh.easyreaderhelper.adapter.MyFragmentPagerAdapter;
import com.erh.easyreaderhelper.base.BaseActivity;

public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {


    private RadioGroup rg_all;
    private RadioButton rb_one;
    private RadioButton rb_two;
    private RadioButton rb_three;
    private RadioButton rb_four;
    private ViewPager vpager;
    private ImageView imageView;

    private MyFragmentPagerAdapter myFragmentPagerAdapter;

    //代表页面的常量
    public static final int PAGE_ONE = 0;
    public static final int PAGE_TWO = 1;
    public static final int PAGE_THREE = 2;
    public static final int PAGE_FOUR = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        initView();
        rb_one.setChecked(true);

    }

    private void initView() {
        vpager = findViewById(R.id.fragment_container);
        rg_all = findViewById(R.id.tabs_rg);
        rb_one = findViewById(R.id.one_tab);
        rb_two = findViewById(R.id.two_tab);
        rb_three = findViewById(R.id.three_tab);
        rb_four = findViewById(R.id.four_tab);
        imageView = findViewById(R.id.sign_iv);
        rg_all.setOnCheckedChangeListener(this);
        vpager.setAdapter(myFragmentPagerAdapter);
        vpager.addOnPageChangeListener(this);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddLostInformationActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.one_tab:
                vpager.setCurrentItem(PAGE_ONE);
                break;
            case R.id.two_tab:
                vpager.setCurrentItem(PAGE_TWO);
                break;
            case R.id.three_tab:
                vpager.setCurrentItem(PAGE_THREE);
                break;
            case R.id.four_tab:
                vpager.setCurrentItem(PAGE_FOUR);
                break;
                default:
        }
    }


    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    public void onPageSelected(int position) {

    }

    public void onPageScrollStateChanged(int state) {
        if (state == 2) {
            switch (vpager.getCurrentItem()) {
                case PAGE_ONE:
                    rb_one.setChecked(true);
                    break;
                case PAGE_TWO:
                    rb_two.setChecked(true);
                    break;
                case PAGE_THREE:
                    rb_three.setChecked(true);
                    break;
                case PAGE_FOUR:
                    rb_four.setChecked(true);
                    break;
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}
