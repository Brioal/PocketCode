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
import com.brioal.pocketcode.entiy.ContentModel;
import com.brioal.pocketcode.interfaces.OnLoaderMoreListener;
import com.brioal.pocketcode.util.Constants;
import com.brioal.pocketcode.util.DataQuery;
import com.brioal.pocketcode.util.NetWorkUtil;

import java.util.List;

import cn.bmob.v3.listener.FindListener;

import static com.brioal.pocketcode.fragment.MainFragment.LOAD_LIMIT;

/**显示文章内容
 * Created by Brioal on 2016/5/13.
 */
public class ContentFragment extends BaseFragment implements OnLoaderMoreListener {
    private final String TAG = "ContentFragmentInfo";
    private String mClassify;
    private int mCount = 0;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    private List<ContentModel> mList;
    private ContentAdapter mAdapter;
    private Handler handler = new Handler() {
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
    @Override
    public void setView() {
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
    public void initView() {
        mRootView = inflater.inflate(R.layout.fragment_content, container, false);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.content_recyclerView);
        mRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.content_refreshLayout);
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
        mList = Constants.getmDataUtil(mContext).getContentModels(mClassify);
        if (mList.size() > 0) {
            handler.sendEmptyMessage(0);
        }
    }

    @Override
    public void loadDataNet() {
        super.loadDataNet();
        if (NetWorkUtil.isNetworkConnected(mContext)) {
            Constants.getmDataUtil(mContext).getContentNet(mClassify, LOAD_LIMIT, new FindListener<ContentModel>() {
                @Override
                public void onSuccess(List<ContentModel> list) {
                    Log.i(TAG, "onSuccess: 加载成功" + list.size() + "条内容");
                    if (mList.size() > 0) {
                        mList.clear();
                    }
                    for (int i = 0; i < list.size(); i++) {
                        mList.add(list.get(i));
                    }
                    handler.sendEmptyMessage(0);
                }

                @Override
                public void onError(int i, String s) {
                    Log.i(TAG, "onError: 加载失败" + s);
                }
            });
        }
    }


    @Override
    public void loadMore() {
        mCount = mList.size();
        DataQuery<ContentModel> query = new DataQuery<>();
        query.getDatas(mContext, LOAD_LIMIT, mCount, "-createdAt", 0, "mClassify", mClassify, new FindListener<ContentModel>() {
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
