package com.brioal.pocketcode.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.brioal.pocketcode.entiy.MyUser;

/**
 * Created by Brioal on 2016/5/17.
 */
public class LocalUserUtil {
    //保存用户信息
    public static void Save(Context mContext, MyUser myUser) {
        SharedPreferences preferences = mContext.getSharedPreferences("PocketCode", Context.MODE_APPEND);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Id", myUser.getObjectId());
        editor.putString("UserName", myUser.getUsername());
        editor.putString("HeadUrl", myUser.getmHeadUrl(mContext));
        editor.putString("Desc", myUser.getmDesc());
        editor.putString("Favorite", myUser.getmFavorite());
        editor.putString("Blog", myUser.getmBlog());
        editor.putString("GitHub", myUser.getmGitHub());
        editor.putString("QQ", myUser.getmQQ());

        editor.apply();
    }

    public static MyUser Read(Context mContext) {
        SharedPreferences preferences = mContext.getSharedPreferences("PocketCode", Context.MODE_APPEND);
        String mObjectId = preferences.getString("Id", "");
        String mUserName = preferences.getString("UserName", "");
        String mHeadUrl = preferences.getString("HeadUrl", "");
        String mDesc = preferences.getString("Desc", "");
        String mFavorite = preferences.getString("Favorite", "");
        String mBlog = preferences.getString("Blog", "");
        String mGithub = preferences.getString("GitHub", "");
        String mQQ = preferences.getString("QQ", "");
        if (mUserName.isEmpty()) {
            return null;
        } else {
            MyUser user = new MyUser();
            user.setObjectId(mObjectId);
            user.setUsername(mUserName);
            user.setmHeadUrl(mHeadUrl);
            user.setmFavorite(mFavorite);
            user.setmDesc(mDesc);
            user.setmBlog(mBlog);
            user.setmGitHub(mGithub);
            user.setmQQ(mQQ);
            return user;
        }
    }

    public static void Delete(Context mContext) {
        SharedPreferences preferences = mContext.getSharedPreferences("PocketCode", Context.MODE_APPEND);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}
