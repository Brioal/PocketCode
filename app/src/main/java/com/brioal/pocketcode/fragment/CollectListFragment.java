package com.brioal.pocketcode.fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.adapter.CollectAdapter;
import com.brioal.pocketcode.database.DBHelper;
import com.brioal.pocketcode.entiy.CollectEnity;
import com.brioal.pocketcode.interfaces.FragmentInterface;
import com.brioal.pocketcode.util.BrioalConstan;
import com.brioal.pocketcode.util.NetWorkUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * 收藏列表的
 * Created by Brioal on 2016/5/31.
 */

public class CollectListFragment extends Fragment implements FragmentInterface {
    public static CollectListFragment mFragment;
    private String TAG = "CollectFragmentInfo";
    private DBHelper mHelper;
    private Context mContext;
    private View rootView;
    private List<CollectEnity> mList;
    private CollectAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            initData();
        }
    };
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setView();
        }
    };

    public static CollectListFragment getInstance() {
        if (mFragment == null) {
            mFragment = new CollectListFragment();
        }

        return mFragment;
    }


    @Override
    public void initData() {
        if (mList == null) {
            mList = new ArrayList<>();
        } else {
            mList.clear();
        }
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from Collect", null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            CollectEnity enity = new CollectEnity(cursor.getString(1), cursor.getString(2));
            mList.add(enity);
        }
        mHandler.sendEmptyMessage(0);
        if (NetWorkUtil.isNetworkConnected(mContext)) {
            BmobQuery<CollectEnity> query = new BmobQuery<>();
            query.findObjects(mContext, new FindListener<CollectEnity>() {
                @Override
                public void onSuccess(List<CollectEnity> list) {
                    mList = list;
                    Log.i(TAG, "onSuccess: 加载收藏成功" + list.size());
                    if (mList.size() > 1) {
                        Collections.sort(mList, new CollectListCompare());
                    }
                    saveData();
                    mHandler.sendEmptyMessage(0);
                }

                @Override
                public void onError(int i, String s) {
                    Log.i(TAG, "onError: 加载失败" + s);
                }
            });
        }
    }

    //保存数据到本地
    private void saveData() {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        for (int i = 0; i < mList.size(); i++) {
            CollectEnity enity = mList.get(i);
            db.execSQL("insert into Collect values ( null , ? , ? )", new Object[]{
                    enity.getmUserId(),
                    enity.getmMessageId()
            });
        }
    }

    @Override
    public void initView() {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_collect_recyclerView);
        new Thread(mRunnable).start();
    }

    @Override
    public void setView() {

        mAdapter = new CollectAdapter(mContext, mList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        mHelper = BrioalConstan.getDbHelper(mContext);
        rootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_collect, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    class CollectListCompare implements Comparator<CollectEnity> {
        @Override
        public int compare(CollectEnity lhs, CollectEnity rhs) {
            String lhTime = lhs.getCreatedAt();
            String rhTime = rhs.getCreatedAt();
            return rhTime.compareTo(lhTime);
        }

        @Override
        public boolean equals(Object object) {
            return false;
        }
    }


}
