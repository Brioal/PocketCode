package com.brioal.pocketcode.entiy;

import android.content.Context;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**首页轮播实体类
 * Created by Brioal on 2016/5/12.
 */
public class BannerModel  extends BmobObject{
    // TODO: 2016/5/13 添加网络数据 
    private BmobFile mImage ; //图片
    private String mContentId; //文章的id
    private String mTip ;//提示
    private String mUrl; //链接

    private String mImageUrl ;

    public BannerModel(String mContentId , String mTip, String mUrl, String mImageUrl) {
        this.mContentId = mContentId;
        this.mTip = mTip;
        this.mUrl = mUrl;
        this.mImageUrl = mImageUrl;
    }

    public String getmContentId() {
        return mContentId;
    }


    public String getmImageUrl(Context mContext) {
        return mImageUrl==null?mImage.getFileUrl(mContext):mImageUrl;
    }


    public String getmTip() {
        return mTip;
    }


    public String getmUrl() {
        return mUrl;
    }

}
