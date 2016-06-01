package com.brioal.pocketcode.entiy;

import cn.bmob.v3.BmobObject;

/**
 * Created by Brioal on 2016/5/31.
 */

public class AttentionEnity extends BmobObject {
    private String mUserId ; //用户的ID
    private String mAuthorId ; //所关注的人的ID

    public AttentionEnity(String mUserId, String mAuthorId) {
        this.mUserId = mUserId;
        this.mAuthorId = mAuthorId;
    }

    public String getmUserId() {
        return mUserId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public String getmAuthorId() {
        return mAuthorId;
    }

    public void setmAuthorId(String mAuthorId) {
        this.mAuthorId = mAuthorId;
    }
}
