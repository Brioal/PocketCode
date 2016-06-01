package com.brioal.pocketcode.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.brioal.pocketcode.util.BrioalUtil;
import com.brioal.pocketcode.MainActivity;
import com.brioal.pocketcode.R;
import com.brioal.pocketcode.util.BrioalConstan;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;


public class LauncherActivity extends AppCompatActivity {


    @Bind(R.id.launcher_logo)
    ImageView mLogo;
    @Bind(R.id.launcher_icon)
    ImageView mDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        ButterKnife.bind(this);
        initWindow();
        initSdk();
        startAnimation();
    }

    private void initWindow() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

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

    private void initSdk() {
        Bmob.initialize(this, BrioalConstan.APPID);
        BrioalUtil.init(this);
    }
}
