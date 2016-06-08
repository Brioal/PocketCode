package com.brioal.pocketcode.fragment;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.adapter.ContentAdapter;
import com.brioal.pocketcode.base.BaseFragment;
import com.brioal.pocketcode.entiy.BannerModel;
import com.brioal.pocketcode.entiy.ContentModel;
import com.brioal.pocketcode.interfaces.OnLoaderMoreListener;
import com.brioal.pocketcode.util.Constants;
import com.brioal.pocketcode.util.DataQuery;
import com.brioal.pocketcode.util.NetWorkUtil;

import java.util.List;

import butterknife.ButterKnife;
import cn.bmob.v3.listener.FindListener;

/**
 * 主界面数据展示
 * Created by Brioal on 2016/5/12.
 */
public class MainFragment extends BaseFragment implements OnLoaderMoreListener {
    public static final int LOAD_LIMIT = 10;
    private String TAG = "MainFragmentInfo";
    public static MainFragment mainFragment;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    private List<BannerModel> mBannerList; //banner数据源
    private List<ContentModel> mContentList; //内容数据
    private ContentAdapter mAdapter; //内容适配器
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                setView();
            } else if (msg.what == 1) {
                mAdapter.notifyItemRangeChanged(mCount, mContentList.size());
            }
        }
    };
    private int mCount = 0;


    @Override
    public void initView() {
        super.initView();
        mRootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.fragment_main_recyclerView);
        mRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.fragment_main_refreshLayout);
        mRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataNet();
            }
        });
    }

    @Override
    public void loadDataLocal() {
        super.loadDataLocal();
        mBannerList = Constants.getmDataUtil(mContext).getBanners();
        mContentList = Constants.getmDataUtil(mContext).getContentModels("精选");
        if (mBannerList.size() > 3) {
            mHandler.sendEmptyMessage(0);
        }
        if (mContentList.size() > 0) {
            mHandler.sendEmptyMessage(0);
        }
    }

    //获取Content的数据
    @Override
    public void loadDataNet() {
        if (NetWorkUtil.isNetworkConnected(mContext)) {
            DataQuery<BannerModel> queryBanner = new DataQuery<>();
            queryBanner.getDatas(mContext, 20, 0, null, -1, null, null, new FindListener<BannerModel>() {
                @Override
                public void onSuccess(List<BannerModel> list) {
                    Log.i(TAG, "onSuccess: 加载成功" + list.size() + "条Banner");
                    mBannerList = list;
                    if (mBannerList.size() > 3) {
                        handler.sendEmptyMessage(0);
                    }
                }

                @Override
                public void onError(int i, String s) {
                    Log.i(TAG, "onError: 加载Banner失败" + s);
                }
            });
            DataQuery<ContentModel> queryContent = new DataQuery<>();
            queryContent.getDatas(mContext, LOAD_LIMIT, 0, "-createdAt", -1, null, null, new FindListener<ContentModel>() {
                @Override
                public void onSuccess(List<ContentModel> list) {
                    Log.i(TAG, "onSuccess: 加载COntentModel成功" + list.size() + "条内容");
                    mContentList = list;
                    handler.sendEmptyMessage(0);
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
    public void setView() {
        super.setView();
        mAdapter = new ContentAdapter(mContext, mContentList, mBannerList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter.setLoaderMoreListener(this);
        mRecyclerView.setAdapter(mAdapter);

        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Constants.getmDataUtil(mContext).saveContentModel(mContentList, mBannerList);
        ButterKnife.unbind(this);
    }

    @Override
    public void loadMore() {
        mCount = mContentList.size();
        DataQuery<ContentModel> query = new DataQuery<>();
        query.getDatas(mContext, LOAD_LIMIT, mCount, "-createdAt", -1, null, null, new FindListener<ContentModel>() {
            @Override
            public void onSuccess(List<ContentModel> list) {
                Log.i(TAG, "onSuccess: 加载成功" + list.size() + "条内容");
                for (int i = 0; i < list.size(); i++) {
                    mContentList.add(list.get(i));
                }
                handler.sendEmptyMessage(1);
            }

            @Override
            public void onError(int i, String s) {
                Log.i(TAG, "onError: 加载失败" + s);
            }
        });
    }
}
