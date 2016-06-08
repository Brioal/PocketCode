package com.brioal.pocketcode;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.brioal.pocketcode.activity.AboutActivity;
import com.brioal.pocketcode.activity.AddContentActivity;
import com.brioal.pocketcode.activity.AttentionActivity;
import com.brioal.pocketcode.activity.CollectActivity;
import com.brioal.pocketcode.activity.LoginAndRegisterActivity;
import com.brioal.pocketcode.activity.ShareListActivity;
import com.brioal.pocketcode.activity.ThemeChooseActivity;
import com.brioal.pocketcode.activity.UserInfoActivity;
import com.brioal.pocketcode.base.BaseActivity;
import com.brioal.pocketcode.database.DBHelper;
import com.brioal.pocketcode.entiy.ClassifyModel;
import com.brioal.pocketcode.entiy.User;
import com.brioal.pocketcode.fragment.ContentFragment;
import com.brioal.pocketcode.fragment.MainFragment;
import com.brioal.pocketcode.util.Constants;
import com.brioal.pocketcode.util.NetWorkUtil;
import com.brioal.pocketcode.util.StatusBarUtils;
import com.brioal.pocketcode.util.ThemeUtil;
import com.brioal.pocketcode.view.CircleImageView;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    @Bind(R.id.main_toolbar)
    Toolbar mToolbar;
    @Bind(R.id.main_tabLayout)
    TabLayout mTabLayout;
    @Bind(R.id.main_viewPager)
    ViewPager mViewPager;
    @Bind(R.id.fab)
    FloatingActionButton mFab;
    @Bind(R.id.nav_view)
    NavigationView navView;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.main_container)
    CoordinatorLayout mainContainer;
    @Bind(R.id.main_appBar)
    AppBarLayout mAppBar;

    private View nav_headView;
    CircleImageView mHead;
    private TextView mName;
    private TextView mDesc;
    private long lastClick = 0;


    private ViewPagerAdapter mAdapter;
    private List<ClassifyModel> mClassifies; //数据源
    private DBHelper mHelper; //数据库操作类
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setView();
        }
    };
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            initData();
        }
    };
    private String TAG = "MainInfo";
    private User user;
    private int FAVORITE_REQUESTCODE = 4;
    private int SHARE_REQUESTCODE = 5;
    private int LOGIN_REQUESTION = 1;
    private int ADDCONTENT = 2;


    @Override
    public void initBar() {
        mToolbar.setTitle("口袋代码");
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

    }

    @Override
    public void initTheme() {
        String color = ThemeUtil.readThemeColor(mContext);
        StatusBarUtils.setColorForDrawerLayout(this, drawerLayout, color);
        mTabLayout.setBackgroundColor(Color.parseColor(color));
        mAppBar.setBackgroundColor(Color.parseColor(color));
        mToolbar.setBackgroundColor(Color.parseColor(color));
        nav_headView.setBackgroundColor(Color.parseColor(color));
    }

    @Override
    public void setView() {
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mFab.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mClassifies = Constants.getmDataUtil(mContext).getClassifyLocal();
        if (NetWorkUtil.isNetworkConnected(mContext)) {
            Constants.getmDataUtil(mContext).getClassifyNet(new FindListener<ClassifyModel>() {
                @Override
                public void onSuccess(List<ClassifyModel> list) {
                    Log.i(TAG, "onSuccess: 加载网络分类数据成功" + list.size());
                    if (list.size() > 0) {
                        mClassifies = list;
                        mHandler.sendEmptyMessage(0);
                    }
                }

                @Override
                public void onError(int i, String s) {
                    Log.i(TAG, "onError: 加载网络分类数据失败" + s);
                }
            });
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constants.getmDataUtil(mContext).saveClassifyLocal(mClassifies);
    }


    @Override
    public void loadDataNet() {
        super.loadDataNet();
        new Thread(mRunnable).start();
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (navView.getHeaderCount() == 0) {
            nav_headView = LayoutInflater.from(mContext).inflate(R.layout.nav_header_main, drawerLayout, false);
            navView.addHeaderView(nav_headView);
        }
        mHead = (CircleImageView) nav_headView.findViewById(R.id.nav_head_head);
        mName = (TextView) nav_headView.findViewById(R.id.nav_head_name);
        mDesc = (TextView) nav_headView.findViewById(R.id.nav_head_desc);
        navView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initUserInfo();
    }

    public void initUserInfo() {
        //初始化首页用户信息
        user = Constants.getmDataUtil(mContext).getUserLocal();
        if (user == null) { //未登陆
            mDesc.setVisibility(View.GONE);
            mName.setText("点击登录");
            mName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(mContext, LoginAndRegisterActivity.class), LOGIN_REQUESTION);
                }
            });
            mHead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(mContext, LoginAndRegisterActivity.class), LOGIN_REQUESTION);
                }
            });
        } else { //已登陆
            Glide.with(mContext).load(user.getmHeadUrl(mContext)).into(mHead);
            mName.setText(user.getUsername());
            mDesc.setText(user.getmDesc() == null || user.getmDesc().isEmpty() ? "这个人很懒,什么都没留下~" : user.getmDesc());
            mHead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, UserInfoActivity.class);
                    startActivity(intent);
                }
            });
            mName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, UserInfoActivity.class);

                    startActivity(intent);
                }
            });
            if (NetWorkUtil.isNetworkConnected(mContext)) {
                BmobQuery<User> query = new BmobQuery<>();
                query.getObject(mContext, user.getObjectId(), new GetListener<User>() {
                    @Override
                    public void onSuccess(User user) {
                        Glide.with(mContext).load(user.getmHeadUrl(mContext)).into(mHead);
                        mName.setText(user.getUsername());
                        mDesc.setText(user.getmDesc() == null || user.getmDesc().isEmpty() ? "这个人很懒,什么都没留下~" : user.getmDesc());
                        Constants.getmDataUtil(mContext).saveUserLocal(user);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Log.i(TAG, "onFailure: 加载用户失败" + s);
                    }
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUESTION && resultCode == RESULT_OK) {
            initUserInfo();
        } else if (requestCode == LOGIN_REQUESTION && resultCode == RESULT_CANCELED) {
            initUserInfo(); //退出登录之后的
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (System.currentTimeMillis() - lastClick < 2000) {

            super.onBackPressed();
        } else {
            Snackbar.make(mainContainer, "再按一次退出", Snackbar.LENGTH_SHORT).show();
            lastClick = System.currentTimeMillis();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_favorate) { // 查看我的收藏
            if (user == null) {
                startActivityForResult(new Intent(mContext, LoginAndRegisterActivity.class), LOGIN_REQUESTION);
            } else {
                Intent intent = new Intent(mContext, CollectActivity.class);
                intent.putExtra("AccountId", user.getObjectId());
                startActivityForResult(intent, FAVORITE_REQUESTCODE);
            }

        } else if (id == R.id.nav_share) { //查看我的分享
            if (user == null) {
                startActivityForResult(new Intent(mContext, LoginAndRegisterActivity.class), LOGIN_REQUESTION);
            } else {
                Intent intent = new Intent(mContext, ShareListActivity.class);
                intent.putExtra("AccountId", user.getObjectId());
                startActivityForResult(intent, SHARE_REQUESTCODE);
            }
        } else if (id == R.id.nav_theme) {
            startActivity(new Intent(mContext, ThemeChooseActivity.class));
        } else if (id == R.id.nav_attention) {
            if (user == null) {
                startActivityForResult(new Intent(mContext, LoginAndRegisterActivity.class), LOGIN_REQUESTION);
            } else {
                startActivity(new Intent(mContext, AttentionActivity.class));
            }
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(mContext, AboutActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Intent intent = new Intent(mContext, AddContentActivity.class);
                startActivityForResult(intent, ADDCONTENT);
                break;
        }
    }


    private class ViewPagerAdapter extends FragmentStatePagerAdapter {


        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return MainFragment.getInstance();
            }
            return ContentFragment.getInstance(getPageTitle(position).toString());
        }

        @Override
        public int getCount() {
            return mClassifies.size() + 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "精选";
            }
            return mClassifies.get(position - 1).getmClassify();
        }
    }
}
