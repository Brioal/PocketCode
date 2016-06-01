package com.brioal.pocketcode.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.brioal.pocketcode.util.StatusBarUtils;
import com.brioal.pocketcode.R;
import com.brioal.pocketcode.fragment.JoinFragment;
import com.brioal.pocketcode.fragment.LoginFragment;

import butterknife.ButterKnife;

public class LoginAndRegisterActivity extends AppCompatActivity {

    TabLayout mTab;
    ViewPager mContainer;
    private String[] mTitles = new String[]{
            "登录",
            "加入我们"
    };

    private ViewPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_and_register);
        ButterKnife.bind(this);
        initId();
        initTab();
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
}
