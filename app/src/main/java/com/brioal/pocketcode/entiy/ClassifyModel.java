package com.brioal.pocketcode.entiy;

import cn.bmob.v3.BmobObject;

/**
 * Created by Brioal on 2016/5/12.
 */
public class ClassifyModel extends BmobObject {
    private int mId ;
    private String mClassify;

    public ClassifyModel(int mId, String mClassify) {
        this.mId = mId;
        this.mClassify = mClassify;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmClassify() {
        return mClassify;
    }

    public void setmClassify(String mClassify) {
        this.mClassify = mClassify;
    }
}
