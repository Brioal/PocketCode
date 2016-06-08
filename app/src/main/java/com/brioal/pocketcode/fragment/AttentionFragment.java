package com.brioal.pocketcode.fragment;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.adapter.AttentionAdapter;
import com.brioal.pocketcode.base.BaseFragment;
import com.brioal.pocketcode.database.DBHelper;
import com.brioal.pocketcode.entiy.AttentionEnity;
import com.brioal.pocketcode.entiy.User;
import com.brioal.pocketcode.interfaces.OnLoaderMoreListener;
import com.brioal.pocketcode.util.Constants;
import com.brioal.pocketcode.util.DataQuery;
import com.brioal.pocketcode.util.NetWorkUtil;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import static com.brioal.pocketcode.fragment.MainFragment.LOAD_LIMIT;

/**
 * Created by Brioal on 2016/5/31.
 */

public class AttentionFragment extends BaseFragment implements  OnLoaderMoreListener {
    public static AttentionFragment mFragment;
    private AttentionAdapter mAdapter;
    private List<AttentionEnity> mList;
    private DBHelper mHelper;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    private User user;
    private int mCount = 0;
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
            if (msg.what == 0) {
                setView();
            } else if (msg.what == 1) {
                mAdapter.notifyItemRangeChanged(mCount, mList.size());
            }
        }
    };
    private String TAG = "AttentionInfo";

    public static AttentionFragment getInstance() {
        if (mFragment == null) {
            mFragment = new AttentionFragment();
        }
        return mFragment;
    }


    @Override
    public void loadDataLocal() {
        super.loadDataLocal();
        mList = Constants.getmDataUtil(mContext).getAttentions(user.getObjectId());
        if (mList.size() > 0) {
            mHandler.sendEmptyMessage(0);
        }
    }

    @Override
    public void loadDataNet() {
        super.loadDataNet();
        if (NetWorkUtil.isNetworkConnected(mContext)) {
            DataQuery<AttentionEnity> query = new DataQuery<>();
            query.getDatas(mContext,100,0,"-createdAt",0,"mUserId", user.getObjectId(),new FindListener<AttentionEnity>() {
                @Override
                public void onSuccess(List<AttentionEnity> list) {
                    Log.i(TAG, "onSuccess: 获取关注数据成功");
                    mList = list;
                    if (mList.size() > 0) {
                        mHandler.sendEmptyMessage(0);
                    }
                }

                @Override
                public void onError(int i, String s) {
                    Log.i(TAG, "onError: 加载关注数据失败" + s);
                }
            });
        }
    }
    @Override
    public void initData() {
        user = Constants.getmDataUtil(mContext).getUserLocal();
    }


    @Override
    public void initView() {
        mRootView = inflater.inflate(R.layout.fragment_attention, container, false);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.fragment_attention_recyclerView);
        mRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.fragment_attention_refreshLayout);
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

        mAdapter = new AttentionAdapter(mContext, mList, AttentionAdapter.TYPE_ATTENTION);
        mAdapter.setLoaderMoreListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
    }
    @Override
    public void loadMore() {
        mCount = mList.size();
        BmobQuery<AttentionEnity> queryContent = new BmobQuery<>();
        queryContent.order("-createdAt");
        queryContent.setSkip(mCount);
        queryContent.setLimit(LOAD_LIMIT);
        queryContent.findObjects(mContext, new FindListener<AttentionEnity>() {
            @Override
            public void onSuccess(List<AttentionEnity> list) {
                Log.i(TAG, "onSuccess: 加载成功" + list.size() + "条内容");
                for (int i = 0; i < list.size(); i++) {
                    mList.add(list.get(i));
                }
                mHandler.sendEmptyMessage(1);
            }

            @Override
            public void onError(int i, String s) {
                Log.i(TAG, "onError: 加载失败" + s);
            }
        });
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: 保存数据到本地");
        Constants.getmDataUtil(mContext).saveAttentions(mList, user.getObjectId());
        super.onDestroy();
    }
}
