package com.brioal.pocketcode.entiy;

import cn.bmob.v3.BmobObject;

/**
 * Created by Brioal on 2016/5/31.
 */

public class CollectEnity extends BmobObject {
    private String mUserId; //用户id
    private String mMessageId ; //文章的id

    public CollectEnity(String mUserId, String mMessageId) {
        this.mUserId = mUserId;
        this.mMessageId = mMessageId;
    }

    public String getmUserId() {
        return mUserId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public String getmMessageId() {
        return mMessageId;
    }

    public void setmMessageId(String mMessageId) {
        this.mMessageId = mMessageId;
    }
}
