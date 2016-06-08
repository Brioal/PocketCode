package com.brioal.pocketcode.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**数据库操作类
 * Created by Brioal on 2016/5/12.
 */
public class DBHelper extends SQLiteOpenHelper {
    private final String CREATE_CLASSIFT_TABLE = "create table Classify ( _id integer primary key autoincrement , mId integer , mClassify )";
    private final String CREATE_BANNER_TABLE = "create table Banner ( _id integer primary key autoincrement ,mContentId ,  mTip , mUrl , mImageUrl )";
    private final String CREATE_CONTENT_TABLE = "create table Content ( _id integer primary key autoincrement ,mAuthorId , mTitle , mDesc ,mClassify , mTime long ,  mComment integer , mPraise integer , mRead integer ,mCollect integer ,mUrl , mHeadUrl , mObjectId )";
    private final String CREATE_FAVORITE_TABLE = "create table Favorite ( _id integer primary key autoincrement , mContentObjectId )";
    private final String CREATE_TAG_TABLE = "create table Tag ( _id integer primary key autoincrement , mTag )" ; //创建本地标签表
    private final String CREATE_COLLECT_TABLE = "create table Collect ( _id integer primary key autoincrement , mUserId , mMessageId )"; //创建用户收藏表
    private final String CREATE_ATTENTION_TABLE = "create table Attention ( _id integer primary key autoincrement , mUserId , mAuthorId )"; //关注列表

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CLASSIFT_TABLE);
        db.execSQL(CREATE_BANNER_TABLE);
        db.execSQL(CREATE_CONTENT_TABLE);
        db.execSQL(CREATE_FAVORITE_TABLE);
        db.execSQL(CREATE_TAG_TABLE);
        db.execSQL(CREATE_COLLECT_TABLE);
        db.execSQL(CREATE_ATTENTION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
