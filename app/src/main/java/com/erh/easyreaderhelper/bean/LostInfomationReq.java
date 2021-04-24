package com.erh.easyreaderhelper.bean;

import cn.bmob.v3.BmobObject;


public class LostInfomationReq extends BmobObject{
    private String title;  //标题
    private String phoneNum;//手机号码
    private String desc;//描述

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
