package com.brioal.pocketcode.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.fragment.ShareListFragment;
import com.brioal.pocketcode.interfaces.ActivityFormat;
import com.brioal.pocketcode.util.StatusBarUtils;
import com.brioal.pocketcode.util.ThemeUtil;
import com.brioal.pocketcode.view.swipeback.app.SwipeBackActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 查看分享的文章
 * 传入用户的ObjectId
 * 从本地读取和从网路加载
 * 提供添加分享功能 ,删除分享功能 , 返回分享的数据数量
 */
public class ShareListActivity extends SwipeBackActivity implements ActivityFormat {

    @Bind(R.id.toolBar)
    Toolbar mToolBar;

    @Override
    public void setView() {
        getSupportFragmentManager().beginTransaction().add(R.id.share_container, new ShareListFragment()).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initTheme();
    }

    //标题栏设置
    public void initBar() {
        mToolBar.setTitle("我的分享");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //显示返回按钮
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
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_share_list);
        ButterKnife.bind(this);
    }

    @Override
    public void loadDataNet() {
        mHandler.sendEmptyMessage(0);
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
