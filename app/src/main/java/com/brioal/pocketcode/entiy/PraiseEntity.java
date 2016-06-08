package com.brioal.pocketcode.entiy;

import cn.bmob.v3.BmobObject;

/**点赞的实体类
 * Created by Brioal on 2016/6/7.
 */
public class PraiseEntity extends BmobObject {
    private String mUserId; //用户的ID
    private String mMessageId; //文章的ID

    public PraiseEntity() {
    }

    public PraiseEntity(String mUserId, String mMessageId) {
        this.mUserId = mUserId;
        this.mMessageId = mMessageId;
    }

    public String getmMessageId() {
        return mMessageId;
    }

    public void setmMessageId(String mMessageId) {
        this.mMessageId = mMessageId;
    }

    public String getmUserId() {
        return mUserId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }
}
