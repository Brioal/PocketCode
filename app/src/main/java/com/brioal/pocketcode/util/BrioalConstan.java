package com.brioal.pocketcode.util;

import android.content.Context;

import com.brioal.pocketcode.database.DBHelper;

/**
 * Created by Brioal on 2016/5/8.
 */
public class BrioalConstan {
    public static final String APPID = "ef191dfb7fbeb86a15d6e307607c1c59";
    public static DBHelper dbHelper; //数据库操作类
    private static DataUtil mDataUtil; // 数据获取类
    private static LocalUser mLocalUser; //本地用户类

    public static DBHelper getDbHelper(Context mContext) {
        if (dbHelper == null) {
            dbHelper = new DBHelper(mContext, "PocketCode.db3", null, 1);
        }

        return dbHelper;
    }

    public static LocalUser getmLocalUser(Context context) {
        if (mLocalUser == null) {
            mLocalUser = new LocalUser(context);
        }

        return mLocalUser;
    }

    public static DataUtil getmDataUtil(Context context) {
        if (mDataUtil == null) {
            mDataUtil = new DataUtil(context);
        }

        return mDataUtil;
    }
}
