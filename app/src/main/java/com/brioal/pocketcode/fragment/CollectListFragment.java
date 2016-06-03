package com.brioal.pocketcode.fragment;

import android.content.Context;
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

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.adapter.CollectAdapter;
import com.brioal.pocketcode.database.DBHelper;
import com.brioal.pocketcode.entiy.CollectEnity;
import com.brioal.pocketcode.interfaces.FragmentInterface;
import com.brioal.pocketcode.util.BrioalConstan;
import com.brioal.pocketcode.util.NetWorkUtil;

import java.util.ArrayList;
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
    private SwipeRefreshLayout mRefreshLayout;
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
        mList = BrioalConstan.getmDataUtil(mContext).getCollects();
        if (mList.size() > 0) {
            mHandler.sendEmptyMessage(0);
        }
        if (NetWorkUtil.isNetworkConnected(mContext)) {
            BmobQuery<CollectEnity> query = new BmobQuery<>();
            query.order("-createdAt");
            query.findObjects(mContext, new FindListener<CollectEnity>() {
                @Override
                public void onSuccess(List<CollectEnity> list) {
                    mList = list;
                    Log.i(TAG, "onSuccess: 加载收藏成功" + list.size());
                    mHandler.sendEmptyMessage(0);
                }

                @Override
                public void onError(int i, String s) {
                    Log.i(TAG, "onError: 加载失败" + s);
                }
            });
        }
    }

    @Override
    public void initView() {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_collect_recyclerView);
        mRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_collect_refreshLayout);
        mRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(mRunnable).start();
            }
        });
        new Thread(mRunnable).start();
    }

    @Override
    public void setView() {
        mAdapter = new CollectAdapter(mContext, mList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        BrioalConstan.getmDataUtil(mContext).saveCollects(mList);
    }
}
