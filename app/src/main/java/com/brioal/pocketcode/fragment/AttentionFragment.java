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
import com.brioal.pocketcode.adapter.AttentionAdapter;
import com.brioal.pocketcode.database.DBHelper;
import com.brioal.pocketcode.entiy.AttentionEnity;
import com.brioal.pocketcode.entiy.MyUser;
import com.brioal.pocketcode.interfaces.FragmentInterface;
import com.brioal.pocketcode.interfaces.OnLoaderMoreListener;
import com.brioal.pocketcode.util.BrioalConstan;
import com.brioal.pocketcode.util.NetWorkUtil;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import static com.brioal.pocketcode.fragment.MainFragment.LOAD_LIMIT;

/**
 * Created by Brioal on 2016/5/31.
 */

public class AttentionFragment extends Fragment implements FragmentInterface, OnLoaderMoreListener {
    public static AttentionFragment mFragment;
    private View rootView;
    private Context mContext;
    private AttentionAdapter mAdapter;
    private List<AttentionEnity> mList;
    private DBHelper mHelper;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    private MyUser myUser;
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
    public void initData() {
        myUser = BrioalConstan.getmLocalUser(mContext).getUser();
        mList = BrioalConstan.getmDataUtil(mContext).getAttentions(myUser.getObjectId());
        if (mList.size() > 0) {
            mHandler.sendEmptyMessage(0);
        }
        if (NetWorkUtil.isNetworkConnected(mContext)) {
            BmobQuery<AttentionEnity> query = new BmobQuery<>();
            query.setLimit(30);
            query.order("-createdAt");
            query.addWhereEqualTo("mUserId", myUser.getObjectId());
            query.findObjects(mContext, new FindListener<AttentionEnity>() {
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
    public void initView() {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_attention_recyclerView);
        mRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_attention_refreshLayout);
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        mHelper = BrioalConstan.getDbHelper(mContext);
        rootView = inflater.inflate(R.layout.fragment_attention, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        new Thread(mRunnable).start();
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
        BrioalConstan.getmDataUtil(mContext).saveAttentions(mList,myUser.getObjectId());
        super.onDestroy();
    }
}
