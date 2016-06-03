package com.brioal.pocketcode.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.adapter.TagChooseAdapter;
import com.brioal.pocketcode.interfaces.ActivityInterFace;
import com.brioal.pocketcode.util.StatusBarUtils;
import com.brioal.pocketcode.util.ThemeUtil;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TagChooseActivity extends AppCompatActivity implements ActivityInterFace {

    @Bind(R.id.toolBar)
    Toolbar mToolBar;
    @Bind(R.id.tag_choose_recyclerView)
    RecyclerView mRecyclerView;
    private TagChooseAdapter mAdapter;

    private Context mcontext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mcontext = this;
        setContentView(R.layout.activity_tag_choose);
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
    public void initBar() {
        mToolBar.setTitle("选择标签");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void initTheme() {
        String color = ThemeUtil.readThemeColor(mcontext);
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
        if (mAdapter == null) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mcontext));
            mAdapter = new TagChooseAdapter(mcontext);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_content, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_add:
                ArrayList<String> tag = mAdapter.getmSelectTags();
                Intent intent = new Intent();
                intent.putExtra("Tags", tag);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
