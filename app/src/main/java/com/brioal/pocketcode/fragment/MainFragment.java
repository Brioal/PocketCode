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
import com.brioal.pocketcode.adapter.ContentAdapter;
import com.brioal.pocketcode.database.DBHelper;
import com.brioal.pocketcode.entiy.BannerModel;
import com.brioal.pocketcode.entiy.ContentModel;
import com.brioal.pocketcode.interfaces.OnLoaderMoreListener;
import com.brioal.pocketcode.util.BrioalConstan;
import com.brioal.pocketcode.util.NetWorkUtil;

import java.util.List;

import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Brioal on 2016/5/12.
 */
public class MainFragment extends Fragment implements OnLoaderMoreListener {
    public static final int LOAD_LIMIT = 10;
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
            if (msg.what == 0) {
                initContentView();
            } else if (msg.what == 1) {
                mAdapter.notifyItemRangeChanged(mCount, mContentList.size());
            }
        }
    };
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
        mBannerList = BrioalConstan.getmDataUtil(mContext).getBanners();
        mContentList = BrioalConstan.getmDataUtil(mContext).getContentModels("精选");
        if (mBannerList.size() > 3) {
            mHandler.sendEmptyMessage(0);
        }
        if (mContentList.size() > 0) {
            mHandler.sendEmptyMessage(0);
        }
        if (NetWorkUtil.isNetworkConnected(mContext)) {
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
            queryContent.setLimit(LOAD_LIMIT);
            queryContent.order("-createdAt");
            queryContent.findObjects(mContext, new FindListener<ContentModel>() {
                @Override
                public void onSuccess(List<ContentModel> list) {
                    Log.i(TAG, "onSuccess: 加载成功" + list.size() + "条内容");
                    mContentList = list;
                    mHandler.sendEmptyMessage(0);
                }

                @Override
                public void onError(int i, String s) {
                    Log.i(TAG, "onError: 加载失败" + s);
                }
            });
        }
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
        mRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED);
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
        BrioalConstan.getmDataUtil(mContext).saveContentModel(mContentList, mBannerList);
        ButterKnife.unbind(this);
    }


    @Override
    public void loadMore() {
        mCount = mContentList.size();
        BmobQuery<ContentModel> queryContent = new BmobQuery<>();
        queryContent.order("-createdAt");
        queryContent.setSkip(mCount);
        queryContent.setLimit(LOAD_LIMIT);
        queryContent.findObjects(mContext, new FindListener<ContentModel>() {
            @Override
            public void onSuccess(List<ContentModel> list) {
                Log.i(TAG, "onSuccess: 加载成功" + list.size() + "条内容");
                for (int i = 0; i < list.size(); i++) {
                    mContentList.add(list.get(i));
                }
                mHandler.sendEmptyMessage(1);
            }

            @Override
            public void onError(int i, String s) {
                Log.i(TAG, "onError: 加载失败" + s);
            }
        });
    }
}
