package com.brioal.pocketcode.entiy;

import android.content.Context;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Brioal on 2016/5/14.
 */
public class MyUser extends BmobUser {
    private BmobFile mHead; //头像
    private String mDesc; //个人签名
    private String mFavorite; //兴趣
    private String mBlog; //博客地址
    private String mGitHub; //Github地址
    private String mQQ; //QQ

    private int mShare; //分享的文章的数量
    private int mBeFavorite; //被收藏的文章的数量
    private int mBeRead; //一共被阅读的次数
    private int mCollect; //收集文章的数量


    private String mHeadUrl;  //头像链接

    public String getmHeadUrl(Context mContext) {
        return mHeadUrl == null ? mHead.getFileUrl(mContext) : mHeadUrl;
    }

    public void setmHead(BmobFile mHead) {
        this.mHead = mHead;
    }

    public int getmShare() {
        return mShare;
    }

    public void setmShare(int mShare) {
        this.mShare = mShare;
    }

    public int getmBeFavorite() {
        return mBeFavorite;
    }

    public void setmBeFavorite(int mBeFavorite) {
        this.mBeFavorite = mBeFavorite;
    }

    public int getmBeRead() {
        return mBeRead;
    }

    public void setmBeRead(int mBeRead) {
        this.mBeRead = mBeRead;
    }

    public int getmCollect() {
        return mCollect;
    }

    public void setmCollect(int mCollect) {
        this.mCollect = mCollect;
    }

    public String getmFavorite() {
        return mFavorite;
    }

    public void setmFavorite(String mFavorite) {
        this.mFavorite = mFavorite;
    }

    public String getmBlog() {
        return mBlog;
    }

    public void setmBlog(String mBlog) {
        this.mBlog = mBlog;
    }

    public String getmGitHub() {
        return mGitHub;
    }

    public void setmGitHub(String mGitHub) {
        this.mGitHub = mGitHub;
    }

    public String getmQQ() {
        return mQQ;
    }

    public void setmQQ(String mQQ) {
        this.mQQ = mQQ;
    }

    public BmobFile getmHead() {
        return mHead;
    }

    public String getmDesc() {
        return mDesc;
    }

    public void setmDesc(String mDesc) {
        this.mDesc = mDesc;
    }


    public void setmHeadUrl(String mHeadUrl) {
        this.mHeadUrl = mHeadUrl;
    }
}
