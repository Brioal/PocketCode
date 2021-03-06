package com.brioal.pocketcode.entiy;

import cn.bmob.v3.BmobObject;

/**
 * 文章实体类
 * Created by Brioal on 2016/5/12.
 */
public class ContentModel extends BmobObject {
    private int mId;
    private String mAuthorId;//作者ID
    private String mTitle; //标题
    private String mDesc; //描述
    private String mClassify; //分类
    private long mTime; //时间
    private int mComment; //评论数
    private int mPraise; //点赞数
    private int mRead; //阅读量
    private int mCollect; //收藏数量
    private String mUrl; //链接

    private boolean isPraise ;
    private boolean isCollect;

    private boolean setPraise = false;
    private boolean setCollect = false;

    private String mHeadUrl;

    public ContentModel() {
    }

    public ContentModel(String mAuthorId, String mTitle, String mDesc, String mClassify, long mTime, int mComment, int mPraise, int mRead, int mCollect, String mUrl, String mHeadUrl) {
        this.mAuthorId = mAuthorId;
        this.mTitle = mTitle;
        this.mDesc = mDesc;
        this.mClassify = mClassify;
        this.mTime = mTime;
        this.mComment = mComment;
        this.mPraise = mPraise;
        this.mRead = mRead;
        this.mCollect = mCollect;
        this.mUrl = mUrl;
        this.mHeadUrl = mHeadUrl;
    }

    public boolean isPraise() {
        return isPraise;
    }

    public void setPraise(boolean praise) {
        setPraise = true;
        isPraise = praise;
    }

    public boolean isCollect() {
        return isCollect;
    }

    public void setCollect(boolean collect) {
        setCollect = true;
        isCollect = collect;
    }

    public boolean isSetPraise() {
        return setPraise;
    }

    public boolean isSetCollect() {
        return setCollect;
    }

    public int getmCollect() {
        return mCollect;
    }


    public String getmHeadUrl() {
        return mHeadUrl;
    }

    public void setmHeadUrl(String mHeadUrl) {
        this.mHeadUrl = mHeadUrl;
    }

    public String getmUrl() {
        return mUrl;
    }

    public String getmHeadObject() {
        return mAuthorId;
    }

    public String getmAuthorId() {
        return mAuthorId;
    }

    public String getmClassify() {
        return mClassify;
    }


    public long getmTime() {
        return mTime;
    }


    public int getmRead() {
        return mRead;
    }

    public String getmTitle() {
        return mTitle;
    }


    public String getmDesc() {
        return mDesc;
    }

    public void setmDesc(String mDesc) {
        this.mDesc = mDesc;
    }

    public int getmPraise() {
        return mPraise;
    }

    public int getmComment() {
        return mComment;
    }

    public void setmComment(int mComment) {
        this.mComment = mComment;
    }

    @Override
    public boolean equals(Object o) {
        ContentModel model = (ContentModel) o;
        if (getmUrl() == model.getmUrl()) {
            return true;
        }
        if (getmTitle().equals(model.getmTitle())) {
            return true;
        }
        if (getmDesc().equals(model.getmDesc())) {
            return true;
        }
        if (getmClassify().equals(model.getmClassify())) {
            return true;
        }
        if (getmTime() == model.getmTime()) {
            return true;
        }
        if (getmComment() == model.getmComment()) {
            return true;
        }
        if (getmRead() == model.getmComment()) {
            return true;
        }
        if (getmPraise() == model.getmPraise()) {
            return true;
        }
        return getmUrl().equals(model.getmUrl());
    }
}
