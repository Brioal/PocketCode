package com.brioal.pocketcode.entiy;

import cn.bmob.v3.BmobObject;

/**评论实体类
 * Created by Brioal on 2016/6/3.
 */

public class CommentModel extends BmobObject {
    private String mUserId ; //用户的id
    private String mContent ; // 评论的内容
    private String mParent ; //所属的父节点
    private String mMessageId ; //所属的文章id


    public CommentModel() {
    }

    public CommentModel(String mUserId, String mContent, String mParent, String mMessageId) {
        this.mUserId = mUserId;
        this.mContent = mContent;
        this.mParent = mParent;
        this.mMessageId = mMessageId;
    }

    public String getmUserId() {
        return mUserId;
    }


    public String getmContent() {
        return mContent;
    }

    public String getmParent() {
        return mParent;
    }

}
