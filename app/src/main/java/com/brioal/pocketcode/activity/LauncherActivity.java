package com.brioal.pocketcode.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.brioal.pocketcode.MainActivity;
import com.brioal.pocketcode.R;
import com.brioal.pocketcode.base.BaseActivity;
import com.brioal.pocketcode.util.BrioalUtil;
import com.brioal.pocketcode.util.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;


public class LauncherActivity extends BaseActivity {


    @Bind(R.id.launcher_logo)
    ImageView mLogo;
    @Bind(R.id.launcher_icon)
    ImageView mDesc;

    @Override
    public void initData() {
        setTheme(R.style.AppTheme_NoActionBar);
        super.initData();
        initSdk();
    }

    @Override
    public void loadDataLocal() {
        super.loadDataLocal();
        mHandler.sendEmptyMessage(0);
    }

    @Override
    public void setView() {
        super.setView();
        startAnimation();
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        ButterKnife.bind(this);
        initWindow();
    }

    //全屏设置
    private void initWindow() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    //开始动画
    private void startAnimation() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.launcher_logo);
        animation.setDuration(1500);
        animation.setFillAfter(true);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(LauncherActivity.this, MainActivity.class));
                LauncherActivity.this.finish();
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_top);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mLogo.startAnimation(animation);
        mDesc.startAnimation(animation);
    }

    //初始化后台接口
    private void initSdk() {
        Bmob.initialize(mContext, Constants.APPID);
        BrioalUtil.init(this);
    }
}
