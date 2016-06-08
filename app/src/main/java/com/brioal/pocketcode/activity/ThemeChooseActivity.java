package com.brioal.pocketcode.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.interfaces.ActivityFormat;
import com.brioal.pocketcode.util.StatusBarUtils;
import com.brioal.pocketcode.util.ThemeUtil;
import com.brioal.pocketcode.util.ToastUtils;
import com.brioal.pocketcode.view.CirclePoint;
import com.brioal.pocketcode.view.swipeback.app.SwipeBackActivity;

import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Brioal on 2016/5/20.
 */

public class ThemeChooseActivity extends SwipeBackActivity implements ActivityFormat {
    @Bind(R.id.theme_toolBar)
    Toolbar mToolBar;
    @Bind(R.id.theme_choose_gridView)
    GridView mGridView;
    private Random random;
    private GridAdapter mAdapter;
    private String[] mColors;

    @Override
    public void initBar() {
        mToolBar.setTitle("主题颜色选择");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initTheme();
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
        setContentView(R.layout.dialog_theme);
        ButterKnife.bind(this);
        random = new Random(16777216);
    }

    @Override
    public void loadDataNet() {
        super.loadDataNet();
        mHandler.sendEmptyMessage(0);
    }

    @Override
    public void setView() {
        mColors = new String[]{
            "#3eb6d1",
            "#FF25A7C4",
            "#FF0C97B6",
            "#FF0F6C81",

            "#FFE95889",
            "#FF4081",
            "#FFD72E67",
            "#FFBF0A47",

            "#FFA12DDF",
            "#FF8125B3",
            "#FF740DAC",
            "#FF7000AC",

            "#FF4B42F9",
            "#FF3B31FC",
            "#FF28229B",
            "#FF1D1969",

        };

        mGridView.setNumColumns(4);
        mAdapter = new GridAdapter();
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String color = mColors[position];
                ThemeUtil.saveThemeColor(mContext, color);
                ToastUtils.showToast(mContext, "更换主题成功");
                finish();
            }
        });
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

    public String getColor() {
        String hex = Integer.toHexString(-random.nextInt());
        return "#" + hex;
    }


    private class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 16;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CirclePoint point = new CirclePoint(mContext);
            point.setColor(Color.parseColor(mColors[position]));
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            params.weight = 1;
//            point.setLayoutParams(params);
            return point;
        }
    }

}
