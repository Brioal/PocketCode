package com.brioal.pocketcode.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.base.BaseActivity;
import com.brioal.pocketcode.fragment.JoinFragment;
import com.brioal.pocketcode.fragment.LoginFragment;
import com.brioal.pocketcode.util.StatusBarUtils;

import butterknife.ButterKnife;

public class LoginAndRegisterActivity extends BaseActivity {

    TabLayout mTab;
    ViewPager mContainer;
    private String[] mTitles = new String[]{
            "登录",
            "加入我们"
    };

    private ViewPagerAdapter mAdapter;

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setContentView(R.layout.activity_login_and_register);
        ButterKnife.bind(this);
        initId();
        initTab();
    }

    @Override
    public void loadDataNet() {
        super.loadDataNet();
    }

    @Override
    public void setView() {
        super.setView();
    }

    private void initTab() {
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mContainer.setAdapter(mAdapter);
        mTab.setupWithViewPager(mContainer);
        mContainer.setCurrentItem(0);
        StatusBarUtils.setTranslucent(this);
    }

    private void initId() {
        mTab = (TabLayout) findViewById(R.id.login_tab);
        mContainer = (ViewPager) findViewById(R.id.login_container);
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {


        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return LoginFragment.getInstance();
                case 1:
                    return JoinFragment.getInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }
}
