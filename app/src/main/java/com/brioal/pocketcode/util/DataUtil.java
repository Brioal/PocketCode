package com.brioal.pocketcode.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.brioal.pocketcode.database.DBHelper;
import com.brioal.pocketcode.entiy.AttentionEnity;
import com.brioal.pocketcode.entiy.BannerModel;
import com.brioal.pocketcode.entiy.CollectEnity;
import com.brioal.pocketcode.entiy.ContentModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 数据操作工具类
 * Created by Brioal on 2016/6/2.
 */

public class DataUtil {
    private DBHelper mHelper;
    private Context mContext;
    private String TAG = "DataUtilInfo";


    public DataUtil(Context mContext) {
        this.mContext = mContext;
        mHelper = BrioalConstan.getDbHelper(mContext);
    }

    //根据分类返回文章内容
    public List<ContentModel> getContentModels(String classify) {
        List<ContentModel> list = new ArrayList<>();
        SQLiteDatabase database = mHelper.getReadableDatabase();
        Cursor cursorContent = null;
        database = mHelper.getReadableDatabase();
        if (classify.equals("精选")) {
            cursorContent = database.rawQuery("select * from Content", null);
        } else {
            cursorContent = database.rawQuery("select * from Content where mClassify like '" + classify + "%'", null);
        }
        while (cursorContent.moveToNext()) {
            ContentModel model = new ContentModel(cursorContent.getString(1), cursorContent.getString(2), cursorContent.getString(3), cursorContent.getString(4), cursorContent.getLong(5), cursorContent.getInt(6), cursorContent.getInt(7), cursorContent.getInt(8), cursorContent.getInt(9), cursorContent.getString(10), cursorContent.getString(11));
            list.add(model);
        }
        return list;
    }

    //保存文章数据及Banner
    public void saveContentModel(List<ContentModel> list, List<BannerModel> banners) {
        Log.i(TAG, "saveData: 保存首页数据");
        SQLiteDatabase db = mHelper.getReadableDatabase();
        if (banners != null) {
            db.execSQL("delete from Banner where _id > 0 ");
            for (int i = 0; i < banners.size(); i++) {
                BannerModel model = banners.get(i);
                db.execSQL("insert into Banner values (null , ? , ? , ? , ? )", new Object[]{
                        model.getmContentId(),
                        model.getmTip(),
                        model.getmUrl(),
                        model.getmImageUrl(mContext)
                });
            }
            for (int i = 0; i < banners.size(); i++) {
                BannerModel model = banners.get(i);
                db.execSQL("insert into Banner values (null , ? , ? , ? , ? )", new Object[]{
                        model.getmContentId(),
                        model.getmTip(),
                        model.getmUrl(),
                        model.getmImageUrl(mContext)
                });
            }
        }
        db.execSQL("delete from Content where _id > 0 ");
        for (int i = 0; i < list.size(); i++) {
            ContentModel model = list.get(i);
            db.execSQL("insert into Content values ( null , ? , ? , ? , ? , ? , ? , ? , ? ,? ,? , ?)", new Object[]{
                    model.getmHeadObject(),
                    model.getmTitle(),
                    model.getmDesc(),
                    model.getmClassify(),
                    model.getmTime(),
                    model.getmComment(),
                    model.getmPraise(),
                    model.getmRead(),
                    model.getmCollect(),
                    model.getmUrl(),
                    model.getmHeadUrl()
            });
        }
        db.close();
    }

    //获取首页Banner
    public List<BannerModel> getBanners() {
        List<BannerModel> banners = new ArrayList<>();
        SQLiteDatabase database = mHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from Banner", null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            BannerModel model = new BannerModel(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
            banners.add(model);
        }
        return banners;
    }

    //返回已收藏的文章
    public List<CollectEnity> getCollects() {
        List<CollectEnity> list = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from Collect", null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            CollectEnity enity = new CollectEnity(cursor.getString(1), cursor.getString(2));
            list.add(enity);
        }

        return list;
    }

    //保存收藏的文章列表
    public void saveCollects(List<CollectEnity> list) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        db.execSQL("delete from Collect where _id > 0 ");
        for (int i = 0; i < list.size(); i++) {
            CollectEnity enity = list.get(i);
            db.execSQL("insert into Collect values ( null , ? , ? )", new Object[]{
                    enity.getmUserId(),
                    enity.getmMessageId()
            });
        }
        db.close();
    }

    //获取关注内容
    public List<AttentionEnity> getAttentions(String userId) {
        List<AttentionEnity> list = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from Attention where mUserId = '" + userId + "'", null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            AttentionEnity enity = new AttentionEnity(cursor.getString(1), cursor.getString(2));
            list.add(enity);
        }

        return list;
    }


    //保存关注到本地
    public void saveAttentions(List<AttentionEnity> list, String userId) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        db.execSQL("delete from Attention where mAuthorId ='" + userId + "'"); //删除原有内容
        for (int i = 0; i < list.size(); i++) {
            AttentionEnity enity = list.get(i);
            db.execSQL("insert into Attention values ( null , ? , ? )", new Object[]{
                    enity.getmUserId(),
                    enity.getmAuthorId()
            });
        }
        db.close();
    }

    //获取粉丝信息
    public List<AttentionEnity> getFans(String userID) {
        List<AttentionEnity> list = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from Attention where mAuthorId = '" + userID + "'", null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            AttentionEnity enity = new AttentionEnity(cursor.getString(1), cursor.getString(2));
            list.add(enity);
        }

        return list;
    }

    //保存粉丝数据
    public void saveFans(List<AttentionEnity> list, String userId) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        db.execSQL("delete from Attention where mAuthorId ='" + userId + "'"); //删除原有内容
        for (int i = 0; i < list.size(); i++) {
            AttentionEnity enity = list.get(i);
            db.execSQL("insert into Attention values ( null , ? , ? )", new Object[]{
                    enity.getmUserId(),
                    enity.getmAuthorId()
            });
        }
        db.close();
    }

    //获取分享信息
    public List<ContentModel> getShareLists(String userId) {
        List<ContentModel> list = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from Content where mAuthorId like '%" + userId + "%'", null);
        while (cursor.moveToNext()) {
            ContentModel model = new ContentModel(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getLong(5), cursor.getInt(6), cursor.getInt(7), cursor.getInt(8), cursor.getInt(9), cursor.getString(10), cursor.getString(11));
            list.add(model);
        }
        cursor.close();
        return list;
    }

    //获取本地保存的Tag
    public List<String> getLocalTag() {
        List<String> strings = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from Tag", null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            strings.add(cursor.getString(1));
        }
        cursor.close();

        return strings;
    }

    //获取标签选择的大分类
    public List<String> getGroups() {
        //10个大分类
        String[] heads = new String[]{
                "移动开发",
                "编程语言",
                "游戏&图像",
                "系统&安全",
                "数据库",
                "研发工具",
        };
        return Arrays.asList(heads);
    }

    public List<List<String>> getChilds() {
        String[][] childs = new String[][]{

                {
                        "移动开发",
                        "Android",
                        "IOS",
                        "Swift",
                        "微信开发",
                },
                {
                        "编程语言",
                        "算法",
                        "JAVA",
                        "Python开发",
                        "Go",
                        "C#",
                        ".NET",
                        "PHP",
                        "C/C++",
                },
                {
                        "移动游戏",
                        "电脑游戏",
                        "图像处理",
                        "图片制作",
                },
                {
                        "系统",
                        "安全",
                        "Linux/UNIX",
                        "Windows",
                        "网络基础",
                        "Tomcat",
                },
                {
                        "数据库",
                        "PrestoDB",
                        "MySql",
                        "PostgreSQL",
                        "MongoDB",
                        "Redis",
                },
                {
                        "Eclipse",
                        "Idea",
                        "Android Studio",
                },

        };

        List<List<String>> result = new ArrayList<>();
        for (int i = 0; i < childs.length; i++) {
            result.add(Arrays.asList(childs[i]));
        }
        return result;
    }
}
