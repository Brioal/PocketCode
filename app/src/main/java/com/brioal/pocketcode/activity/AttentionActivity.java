package com.brioal.pocketcode.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.fragment.AttentionFragment;
import com.brioal.pocketcode.interfaces.ActivityInterFace;
import com.brioal.pocketcode.util.StatusBarUtils;
import com.brioal.pocketcode.util.ThemeUtil;
import com.brioal.pocketcode.view.swipeback.app.SwipeBackActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AttentionActivity extends SwipeBackActivity implements ActivityInterFace {


    @Bind(R.id.toolBar)
    Toolbar mToolBar;

    private Context mContext;


    @Override
    public void initBar() {
        mToolBar.setTitle("我的关注");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void initTheme() {
        String color = ThemeUtil.readThemeColor(mContext);
        mToolBar.setBackgroundColor(Color.parseColor(color));
        StatusBarUtils.setColor(this, color);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {

    }

    @Override
    public void setView() {
        getSupportFragmentManager().beginTransaction().add(R.id.attention_container, new AttentionFragment()).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_attention);
        ButterKnife.bind(this);
        initBar();
        setView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initTheme();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
