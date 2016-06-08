package com.brioal.pocketcode.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.adapter.AttentionAdapter;
import com.brioal.pocketcode.base.BaseFragment;
import com.brioal.pocketcode.entiy.AttentionEnity;
import com.brioal.pocketcode.entiy.User;
import com.brioal.pocketcode.util.Constants;
import com.brioal.pocketcode.util.DataQuery;
import com.brioal.pocketcode.util.NetWorkUtil;

import java.util.List;

import cn.bmob.v3.listener.FindListener;

/**
 * 粉丝显示界面
 * Created by Brioal on 2016/6/1.
 */

public class FansFragment extends BaseFragment {
    public static FansFragment mFragment;
    private final String TAG = "FansFragmentInfo";
    private User user;

    public static FansFragment getInstance() {
        if (mFragment == null) {
            mFragment = new FansFragment();
        }
        return mFragment;
    }
    private RecyclerView mRecyclerView;
    private List<AttentionEnity> mList;
    private AttentionAdapter mAdapter;

    @Override
    public void loadDataLocal() {
        super.loadDataLocal();
        mList = Constants.getmDataUtil(mContext).getFans(user.getObjectId());
        if (mList.size() > 0) {
            mHandler.sendEmptyMessage(0);
        }
    }

    @Override
    public void initData() {
        super.initData();
        user = Constants.getmDataUtil(mContext).getUserLocal();
    }

    @Override
    public void loadDataNet() {

        if (NetWorkUtil.isNetworkConnected(mContext)) {
            DataQuery<AttentionEnity> query = new DataQuery<>();
            query.getDatas(mContext, 30, 0, "-createdAt", 0, "mAuthorId", user.getObjectId(), new FindListener<AttentionEnity>() {
                @Override
                public void onSuccess(List<AttentionEnity> list) {
                    Log.i(TAG, "onSuccess: 加载粉丝成功" + list.size());
                    mList = list;
                    if (mList.size() > 0) {
                        mHandler.sendEmptyMessage(0);
                    }
                }

                @Override
                public void onError(int i, String s) {
                    Log.i(TAG, "onError: 加载粉丝失败" + s);
                }
            });
        }
    }

    @Override
    public void initView() {
        mRootView = inflater.inflate(R.layout.fragment_fans, container, false);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.fragment_fans_recyclerView);
    }

    @Override
    public void setView() {
        mAdapter = new AttentionAdapter(mContext, mList, AttentionAdapter.TYPE_FANS);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: 保存数据");
        Constants.getmDataUtil(mContext).saveFans(mList, user.getObjectId());
        super.onDestroy();
    }
}
