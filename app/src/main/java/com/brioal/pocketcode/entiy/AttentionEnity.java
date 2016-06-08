package com.brioal.pocketcode.entiy;

import cn.bmob.v3.BmobObject;

/**"我"关注别人
 * Created by Brioal on 2016/5/31.
 */

public class AttentionEnity extends BmobObject {
    private String mUserId ; //"我"的的ID
    private String mAuthorId ; //别人的ID

    private String mName ; //用户的名称
    private String mDesc ; //用户的描述
    private String mUserHeadUrl ; //用户的头像连接

    public AttentionEnity() {
    }

    public AttentionEnity(String mUserId, String mAuthorId) {
        this.mUserId = mUserId;
        this.mAuthorId = mAuthorId;
    }


    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmDesc() {
        return mDesc;
    }

    public void setmDesc(String mDesc) {
        this.mDesc = mDesc;
    }

    public String getmUserHeadUrl() {
        return mUserHeadUrl;
    }

    public void setmUserHeadUrl(String mUserHeadUrl) {
        this.mUserHeadUrl = mUserHeadUrl;
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
