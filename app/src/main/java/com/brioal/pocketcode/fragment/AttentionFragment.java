package com.brioal.pocketcode.fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.brioal.pocketcode.util.LocalUserUtil;
import com.brioal.pocketcode.util.NetWorkUtil;
import com.brioal.pocketcode.util.UserEnityCompare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Brioal on 2016/5/31.
 */

public class AttentionFragment extends Fragment implements FragmentInterface {
    public static AttentionFragment mFragment;
    private View rootView;
    private Context mContext;
    private AttentionAdapter mAdapter;
    private List<AttentionEnity> mList;
    private DBHelper mHelper;
    private RecyclerView mRecyclerView;
    private MyUser myUser;

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
    private String TAG = "AttentionInfo";

    public static AttentionFragment getInstance() {
        if (mFragment == null) {
            mFragment = new AttentionFragment();
        }
        return mFragment;
    }

    @Override
    public void initData() {
        myUser = LocalUserUtil.Read(mContext);
        if (mList == null) {
            mList = new ArrayList<>();
        } else {
            mList.clear();
        }
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from Attention where mUserId = '"+myUser.getObjectId()+"'", null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            AttentionEnity enity = new AttentionEnity(cursor.getString(1), cursor.getString(2));
            mList.add(enity);
        }
        if (mList.size() > 0) {
            mHandler.sendEmptyMessage(0);
        }
        if (NetWorkUtil.isNetworkConnected(mContext)) {
            BmobQuery<AttentionEnity> query = new BmobQuery<>();
            query.setLimit(30);
            query.addWhereEqualTo("mUserId", myUser.getObjectId());
            query.findObjects(mContext, new FindListener<AttentionEnity>() {
                @Override
                public void onSuccess(List<AttentionEnity> list) {
                    Log.i(TAG, "onSuccess: 获取关注数据成功");
                    mList = list;
                    if (mList.size() > 0) {
                        if (mList.size() > 1) {
                            Collections.sort(mList, new UserEnityCompare());
                        }
                        saveData();
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

    //保存数据到本地
    private void saveData() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        db.execSQL("delete from Attention where mAuthorId ='" + myUser.getObjectId() + "'"); //删除原有内容
        for (int i = 0; i < mList.size(); i++) {
            AttentionEnity enity = mList.get(i);
            db.execSQL("insert into Attention values ( null , ? , ? )", new Object[]{
                    enity.getmUserId(),
                    enity.getmAuthorId()
            });
        }
        db.close();
    }

    @Override
    public void initView() {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_attention_recyclerView);
    }

    @Override
    public void setView() {

        mAdapter = new AttentionAdapter(mContext, mList, AttentionAdapter.TYPE_ATTENTION);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
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
}
