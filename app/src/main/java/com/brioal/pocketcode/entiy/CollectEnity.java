package com.brioal.pocketcode.entiy;

import cn.bmob.v3.BmobObject;

/**收藏列表实体类
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

    public String getmMessageId() {
        return mMessageId;
    }

}
