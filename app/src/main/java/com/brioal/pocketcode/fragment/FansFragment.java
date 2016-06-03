package com.brioal.pocketcode.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.brioal.pocketcode.util.BrioalConstan;
import com.brioal.pocketcode.util.NetWorkUtil;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Brioal on 2016/6/1.
 */

public class FansFragment extends Fragment implements FragmentInterface {
    public static FansFragment mFragment;
    private final String TAG = "FansFragmentInfo";
    private MyUser myUser;

    public static FansFragment getInstance() {
        if (mFragment == null) {
            mFragment = new FansFragment();
        }
        return mFragment;
    }

    private DBHelper mHelper;
    private Context mContext;
    private View rootView;
    private RecyclerView mRecyclerView;
    private List<AttentionEnity> mList;
    private AttentionAdapter mAdapter;

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


    @Override
    public void initData() {
        myUser = BrioalConstan.getmLocalUser(mContext).getUser();
        mList = BrioalConstan.getmDataUtil(mContext).getFans(myUser.getObjectId());
        if (mList.size() > 0) {
            mHandler.sendEmptyMessage(0);
        }
        if (NetWorkUtil.isNetworkConnected(mContext)) {
            BmobQuery<AttentionEnity> query = new BmobQuery<>();
            query.setLimit(30);
            query.order("-updatedAt");
            query.addWhereEqualTo("mAuthorId", myUser.getObjectId());
            query.findObjects(mContext, new FindListener<AttentionEnity>() {
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
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_fans_recyclerView);
    }

    @Override
    public void setView() {

        mAdapter = new AttentionAdapter(mContext, mList, AttentionAdapter.TYPE_FANS);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        mHelper = BrioalConstan.getDbHelper(mContext);
        rootView = inflater.inflate(R.layout.fragment_fans, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        new Thread(mRunnable).start();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: 保存数据");
        BrioalConstan.getmDataUtil(mContext).saveFans(mList, myUser.getObjectId());
        super.onDestroy();
    }
}
