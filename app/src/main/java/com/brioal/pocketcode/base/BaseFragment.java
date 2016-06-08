package com.brioal.pocketcode.base;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brioal.pocketcode.interfaces.FragmentFormat;


/**
 * Fragment基类
 * Created by mm on 2016/6/4.
 */

public class BaseFragment extends Fragment implements FragmentFormat {
    protected Activity mContext;
    protected View mRootView;
    protected LayoutInflater inflater;
    protected ViewGroup container;
    protected Bundle saveInstanceState;
    protected String TAG = "BaseFragment";

    protected Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            loadDataNet();      //加载数据
        }
    };

    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setView(); //数据显示到布局
        }
    };


    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach: ");
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView: ");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        this.inflater = inflater;
        this.container = container;
        this.saveInstanceState = savedInstanceState;
        initData();
        initView();
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onViewCreated: ");
        super.onViewCreated(view, savedInstanceState);
        loadDataLocal();
        new Thread(mRunnable).start();
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {

    }

    @Override
    public void loadDataLocal() {

    }

    @Override
    public void loadDataNet() {

    }

    @Override
    public void setView() {

    }
}
