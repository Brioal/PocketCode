package com.brioal.pocketcode.fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.adapter.ShareListAdapter;
import com.brioal.pocketcode.database.DBHelper;
import com.brioal.pocketcode.entiy.ContentModel;
import com.brioal.pocketcode.util.BrioalConstan;
import com.brioal.pocketcode.util.ContentModelCompare;
import com.brioal.pocketcode.util.LocalUserUtil;
import com.brioal.pocketcode.util.NetWorkUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * 分享列表展示
 * Created by Brioal on 2016/5/31.
 */

public class ShareListFragment extends Fragment {
    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    public static ShareListFragment mFragment;
    private String mAccountId;
    private DBHelper mHelper;
    private List<ContentModel> mList; //分享列表数据源
    private ShareListAdapter mAdapter; //分享列表适配器
    private Context mContext;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Collections.sort(mList, new ContentModelCompare());
            initView();
        }
    };
    private String TAG = "ShareListInfo";

    //更新布局
    public void initView() {
        mAdapter = new ShareListAdapter(mContext, mList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
    }


    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            initData();
        }
    };

    public static ShareListFragment getInstance() {
        if (mFragment == null) {
            mFragment = new ShareListFragment();
        }

        return mFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        mHelper = BrioalConstan.getDbHelper(mContext);
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_share_list, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_share_recyclerView);
        mRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_share_refreshLayout);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initId();
        new Thread(mRunnable).start();
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(mRunnable).start();
            }
        });
        mRefreshLayout.setRefreshing(true);
    }


    //获取数据
    private void initData() {
        readLocalData();
        if (NetWorkUtil.isNetworkConnected(mContext)) {
            BmobQuery<ContentModel> query = new BmobQuery<>();
            Log.i(TAG, "initData: " + mAccountId);
            query.addWhereEqualTo("mAuthorId", mAccountId);
            query.setLimit(100);
            query.findObjects(mContext, new FindListener<ContentModel>() {
                @Override
                public void onSuccess(List<ContentModel> list) {
                    Log.i(TAG, "onSuccess: 加载成功" + list.size() + "条发布内容");
                    mList = list;
                    mHandler.sendEmptyMessage(0);
                }

                @Override
                public void onError(int i, String s) {
                    Log.i(TAG, "onError: 加载失败" + s);
                }
            });
        }
    }

    //读取本地自己分享的文章
    private void readLocalData() {
        if (mList == null) {
            mList = new ArrayList<>();
        } else {
            mList.clear();
        }
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from Content where mAuthorId like '%" + mAccountId + "%'", null);
        while (cursor.moveToNext()) {
            ContentModel model = new ContentModel(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getLong(5), cursor.getInt(6), cursor.getInt(7), cursor.getInt(8), cursor.getInt(9), cursor.getString(10), cursor.getString(11));
            mList.add(model);
        }
        cursor.close();
        mHandler.sendEmptyMessage(0);
    }


    //获取出入的数据
    private void initId() {
        mAccountId = LocalUserUtil.Read(mContext).getObjectId();
    }


}
