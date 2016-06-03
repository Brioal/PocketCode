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
import com.brioal.pocketcode.entiy.ContentModel;
import com.brioal.pocketcode.interfaces.OnLoaderMoreListener;
import com.brioal.pocketcode.util.BrioalConstan;
import com.brioal.pocketcode.util.NetWorkUtil;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import static com.brioal.pocketcode.fragment.MainFragment.LOAD_LIMIT;

/**
 * Created by Brioal on 2016/5/13.
 */
public class ContentFragment extends Fragment implements OnLoaderMoreListener {
    private final String TAG = "ContentFragmentInfo";
    private Context mContext;
    private DBHelper mHelper;
    private String mClassify;
    private int mCount = 0;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    private List<ContentModel> mList;
    private ContentAdapter mAdapter;
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
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            initData();
        }
    };

    //初始化数据
    private void initData() {
        mList = BrioalConstan.getmDataUtil(mContext).getContentModels(mClassify);
        if (mList.size() > 0) {
            mHandler.sendEmptyMessage(0);
        }
        if (NetWorkUtil.isNetworkConnected(mContext)) {
            BmobQuery<ContentModel> query = new BmobQuery<>();
            query.addWhereContains("mClassify", mClassify);
            query.setLimit(LOAD_LIMIT);
            query.order("-updatedAt");
            query.findObjects(mContext, new FindListener<ContentModel>() {
                @Override
                public void onSuccess(List<ContentModel> list) {
                    Log.i(TAG, "onSuccess: 加载成功" + list.size() + "条内容");
                    if (mList.size() > 0) {
                        mList.clear();
                    }
                    for (int i = 0; i < list.size(); i++) {
                        mList.add(list.get(i));
                    }
                    mHandler.sendEmptyMessage(0);
                }

                @Override
                public void onError(int i, String s) {
                    Log.i(TAG, "onError: 加载失败" + s);
                }
            });
        }
    }

    private void setView() {
        mAdapter = new ContentAdapter(mContext, mList, null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter.setLoaderMoreListener(this);
        mRecyclerView.setAdapter(mAdapter);
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
    }


    public static ContentFragment getInstance(String mClassify) {
        ContentFragment fragment = new ContentFragment();
        fragment.setClassify(mClassify);

        return fragment;
    }

    public void setClassify(String mClassify) {
        this.mClassify = mClassify;
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
        View rootView = inflater.inflate(R.layout.fragment_content, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.content_recyclerView);
        mRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.content_refreshLayout);
        initView();
        return rootView;
    }

    private void initView() {
        mRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(mRunnable).start();
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Thread(mRunnable).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void loadMore() {
        mCount = mList.size();
        BmobQuery<ContentModel> queryContent = new BmobQuery<>();
        queryContent.setSkip(mCount);
        queryContent.setLimit(LOAD_LIMIT);
        queryContent.findObjects(mContext, new FindListener<ContentModel>() {
            @Override
            public void onSuccess(List<ContentModel> list) {
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
}
