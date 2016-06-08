package com.brioal.pocketcode.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.fragment.CollectListFragment;
import com.brioal.pocketcode.interfaces.ActivityFormat;
import com.brioal.pocketcode.util.StatusBarUtils;
import com.brioal.pocketcode.util.ThemeUtil;
import com.brioal.pocketcode.view.swipeback.app.SwipeBackActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 传入用户的ObjectId
 * 在收藏类中查询文章的Id
 * 在文章中查询数据
 */
public class CollectActivity extends SwipeBackActivity implements ActivityFormat {


    @Bind(R.id.toolBar)
    Toolbar mToolBar;


    @Override
    public void setView() {
        getSupportFragmentManager().beginTransaction().add(R.id.collect_container, new CollectListFragment()).commit();
    }

    public void initBar() {
        mToolBar.setTitle("我的收藏");
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
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_collect);
        ButterKnife.bind(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        initTheme();
    }

    @Override
    public void loadDataNet() {
        super.loadDataNet();
        mHandler.sendEmptyMessage(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_collect, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_search:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
