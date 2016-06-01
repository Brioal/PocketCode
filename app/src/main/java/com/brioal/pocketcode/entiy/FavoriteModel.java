package com.brioal.pocketcode.entiy;

import cn.bmob.v3.BmobObject;

/**
 * Created by Brioal on 2016/5/18.
 */
public class FavoriteModel extends BmobObject {
    private String mAccountId; //用户的id
    private String mContentId; //文章的Id

    public FavoriteModel(String mAccountId, String mContentId) {
        this.mAccountId = mAccountId;
        this.mContentId = mContentId;
    }

    public String getmAccountId() {
        return mAccountId;
    }

    public void setmAccountId(String mAccountId) {
        this.mAccountId = mAccountId;
    }

    public String getmContentId() {
        return mContentId;
    }

    public void setmContentId(String mContentId) {

        this.mContentId = mContentId;
    }

    @Override
    public boolean equals(Object o) { //用于根据文章id来判断是否在收藏列表内
        FavoriteModel rh = (FavoriteModel) o;
        if (getmContentId().equals(rh.getmContentId())) {
            return true;
        } else {
            return false;
        }
    }
}
