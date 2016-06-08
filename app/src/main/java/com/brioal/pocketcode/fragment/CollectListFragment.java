package com.brioal.pocketcode.fragment;

import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.adapter.CollectAdapter;
import com.brioal.pocketcode.base.BaseFragment;
import com.brioal.pocketcode.entiy.CollectEnity;
import com.brioal.pocketcode.util.Constants;
import com.brioal.pocketcode.util.DataQuery;
import com.brioal.pocketcode.util.NetWorkUtil;

import java.util.List;

import cn.bmob.v3.listener.FindListener;

/**
 * 收藏列表的
 * Created by Brioal on 2016/5/31.
 */

public class CollectListFragment extends BaseFragment {
    public static CollectListFragment mFragment;
    private String TAG = "CollectFragmentInfo";
    private List<CollectEnity> mList;
    private CollectAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;

    public static CollectListFragment getInstance() {
        if (mFragment == null) {
            mFragment = new CollectListFragment();
        }
        return mFragment;
    }

    @Override
    public void loadDataLocal() {
        super.loadDataLocal();
        mList = Constants.getmDataUtil(mContext).getCollects();
        if (mList.size() > 0) {
            mHandler.sendEmptyMessage(0);
        }
    }

    @Override
    public void loadDataNet() {
        if (NetWorkUtil.isNetworkConnected(mContext)) {
            DataQuery<CollectEnity> query = new DataQuery<>();
            query.getDatas(mContext, 100, 0, "-createdAt", -1, null, null, new FindListener<CollectEnity>() {
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
        mRootView = inflater.inflate(R.layout.fragment_collect, container, false);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.fragment_collect_recyclerView);
        mRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.fragment_collect_refreshLayout);
        mRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(mRunnable).start();
            }
        });
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Constants.getmDataUtil(mContext).saveCollects(mList);
    }
}
