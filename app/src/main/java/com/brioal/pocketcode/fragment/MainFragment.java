package com.brioal.pocketcode.fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brioal.pocketcode.util.NetWorkUtil;
import com.brioal.pocketcode.R;
import com.brioal.pocketcode.adapter.ContentAdapter;
import com.brioal.pocketcode.database.DBHelper;
import com.brioal.pocketcode.entiy.BannerModel;
import com.brioal.pocketcode.entiy.ContentModel;
import com.brioal.pocketcode.interfaces.OnLoaderMoreListener;
import com.brioal.pocketcode.util.ContentModelCompare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Brioal on 2016/5/12.
 */
public class MainFragment extends Fragment implements OnLoaderMoreListener {

    private String TAG = "MainFragmentInfo";
    public static MainFragment mainFragment;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    private Context mContext;
    private DBHelper mHelper;
    private List<BannerModel> mBannerList; //banner数据源
    private List<ContentModel> mContentList; //内容数据
    private ContentAdapter mAdapter; //内容适配器
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            initContentView();
            saveData();
        }
    };
    private int mLimitNum = 10;
    private int mCount = 0;

    //设置内容
    private void initContentView() {
        mAdapter = new ContentAdapter(mContext, mContentList, mBannerList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter.setLoaderMoreListener(this);
        mRecyclerView.setAdapter(mAdapter);

        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
    }

    private Runnable mContentRunnable = new Runnable() {
        @Override
        public void run() {
            initData();
        }


    };

    //获取Content的数据
    private void initData() {
        if (mBannerList == null) {
            mBannerList = new ArrayList<>();
        } else {
            mBannerList.clear();
        }
        if (mContentList == null) {
            mContentList = new ArrayList<>();
        } else {
            mContentList.clear();
        }
        SQLiteDatabase database = mHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from Banner", null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            BannerModel model = new BannerModel(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
            mBannerList.add(model);
        }
        if (mBannerList.size() > 3) {
            mHandler.sendEmptyMessage(0);
        }

        Cursor cursorContent = null;
        database = mHelper.getReadableDatabase();
        cursorContent = database.rawQuery("select * from Content", null);
        while (cursorContent.moveToNext()) {
            ContentModel model = new ContentModel(cursorContent.getString(1), cursorContent.getString(2), cursorContent.getString(3), cursorContent.getString(4), cursorContent.getLong(5), cursorContent.getInt(6), cursorContent.getInt(7), cursorContent.getInt(8), cursorContent.getInt(9), cursorContent.getString(10), cursorContent.getString(11));
            mContentList.add(model);
        }
        if (mContentList.size() > 0) {
            Collections.sort(mContentList, new ContentModelCompare());
            mHandler.sendEmptyMessage(0);
        }
        if (NetWorkUtil.isNetworkConnected(mContext)) {
            mRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mRefreshLayout.setProgressViewOffset(false, 0, 30);
                    mRefreshLayout.setRefreshing(true);
                }
            });

            BmobQuery<BannerModel> queryModel = new BmobQuery<BannerModel>();
            queryModel.setLimit(20);
            queryModel.findObjects(mContext, new FindListener<BannerModel>() {
                @Override
                public void onSuccess(List<BannerModel> list) {
                    Log.i(TAG, "onSuccess: 加载成功" + list.size() + "条Banner");
                    if (mBannerList.size() > 0) {
                        mBannerList.clear();
                    }
                    for (int i = 0; i < list.size(); i++) {
                        mBannerList.add(list.get(i));
                    }
                    if (mBannerList.size() > 3) {
                        mHandler.sendEmptyMessage(0);
                    }
                }

                @Override
                public void onError(int i, String s) {
                    Log.i(TAG, "onError: 加载失败" + s);
                }
            });

            BmobQuery<ContentModel> queryContent = new BmobQuery<>();
            queryContent.setLimit(mLimitNum);
            queryContent.findObjects(mContext, new FindListener<ContentModel>() {
                @Override
                public void onSuccess(List<ContentModel> list) {
                    Log.i(TAG, "onSuccess: 加载成功" + list.size() + "条内容");
                    mContentList = list;
                    Collections.sort(mContentList, new ContentModelCompare());
                    mHandler.sendEmptyMessage(0);
                }

                @Override
                public void onError(int i, String s) {
                    Log.i(TAG, "onError: 加载失败" + s);
                }
            });
        }
    }

    public void saveData() {
        Log.i(TAG, "saveData: 保存首页数据");
        SQLiteDatabase db = mHelper.getReadableDatabase();
        db.execSQL("delete from Banner where _id > 0 ");
        db.execSQL("delete from Content where _id > 0 ");
        for (int i = 0; i < mBannerList.size(); i++) {
            BannerModel model = mBannerList.get(i);
            db.execSQL("insert into Banner values (null , ? , ? , ? , ? )", new Object[]{
                    model.getmContentId(),
                    model.getmTip(),
                    model.getmUrl(),
                    model.getmImageUrl(mContext)
            });
        }
        for (int i = 0; i < mContentList.size(); i++) {
            ContentModel model = mContentList.get(i);
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


    public static MainFragment getInstance() {
        if (mainFragment == null) {
            mainFragment = new MainFragment();
        }
        return mainFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
        mHelper = new DBHelper(mContext, "PocketCode.db3", null, 1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_main_recyclerView);
        mRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_main_refreshLayout);
        mRefreshLayout.setColorSchemeColors(Color.BLUE,Color.GREEN,Color.RED);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(mContentRunnable).start();
            }
        });
        mRefreshLayout.setProgressViewOffset(false, 0, 30);
        mRefreshLayout.setRefreshing(true);
        new Thread(mContentRunnable).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @Override
    public void loadMore() {
        mCount += mContentList.size();
        BmobQuery<ContentModel> queryContent = new BmobQuery<>();
        queryContent.setSkip(mCount);
        queryContent.setLimit(mLimitNum);
        queryContent.findObjects(mContext, new FindListener<ContentModel>() {
            @Override
            public void onSuccess(List<ContentModel> list) {
                Log.i(TAG, "onSuccess: 加载成功" + list.size() + "条内容");
                for (int i = 0; i < list.size(); i++) {
                    mContentList.add(list.get(i));
                }
                Collections.sort(mContentList, new ContentModelCompare());
                mHandler.sendEmptyMessage(0);
            }

            @Override
            public void onError(int i, String s) {
                Log.i(TAG, "onError: 加载失败" + s);
            }
        });
    }
}
