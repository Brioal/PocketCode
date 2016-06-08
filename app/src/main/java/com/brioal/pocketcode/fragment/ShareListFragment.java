package com.brioal.pocketcode.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.adapter.ShareListAdapter;
import com.brioal.pocketcode.base.BaseFragment;
import com.brioal.pocketcode.entiy.ContentModel;
import com.brioal.pocketcode.util.Constants;
import com.brioal.pocketcode.util.DataQuery;
import com.brioal.pocketcode.util.NetWorkUtil;

import java.util.List;

import cn.bmob.v3.listener.FindListener;

/**
 * 分享列表展示
 * Created by Brioal on 2016/5/31.
 */

public class ShareListFragment extends BaseFragment {
    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    public static ShareListFragment mFragment;
    private String mUserID;
    private List<ContentModel> mList; //分享列表数据源
    private ShareListAdapter mAdapter; //分享列表适配器
    private String TAG = "ShareListInfo";

    public static ShareListFragment getInstance() {
        if (mFragment == null) {
            mFragment = new ShareListFragment();
        }
        return mFragment;
    }

    @Override
    public void initView() {
        super.initView();
        mRootView = inflater.inflate(R.layout.fragment_share_list, container, false);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.fragment_share_recyclerView);
        mRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.fragment_share_refreshLayout);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(mRunnable).start();
            }
        });
    }

    @Override
    public void initData() {
        mUserID = Constants.getmDataUtil(mContext).getUserLocal().getObjectId();
    }

    @Override
    public void loadDataLocal() {
        super.loadDataLocal();
        mList = Constants.getmDataUtil(mContext).getShareLists(mUserID);
        if (mList.size() > 0) {
            mHandler.sendEmptyMessage(0);
        }
    }

    @Override
    public void loadDataNet() {
        super.loadDataNet();
        if (NetWorkUtil.isNetworkConnected(mContext)) {
            DataQuery<ContentModel> query = new DataQuery<>();
            query.getDatas(mContext, 100, 0, "-updatedAt", 0, "mAuthorId", mUserID, new FindListener<ContentModel>() {
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

    @Override
    public void setView() {
        super.setView();
        mAdapter = new ShareListAdapter(mContext, mList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
    }


}
