package com.brioal.pocketcode.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.adapter.UserInfoViewPager;
import com.brioal.pocketcode.entiy.MyUser;
import com.brioal.pocketcode.interfaces.ActivityInterFace;
import com.brioal.pocketcode.util.BlurUtil;
import com.brioal.pocketcode.util.StatusBarUtils;
import com.brioal.pocketcode.util.ThemeUtil;
import com.brioal.pocketcode.view.CircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import butterknife.Bind;
import butterknife.ButterKnife;


public class UserInfoActivity extends AppCompatActivity implements ActivityInterFace {
    @Bind(R.id.user_info_name)
    TextView mName;
    @Bind(R.id.user_info_blog)
    TextView mBlog;
    @Bind(R.id.user_info_head)
    CircleImageView mHead;
    @Bind(R.id.user_info_desc)
    TextView mDesc;
    @Bind(R.id.user_info_share)
    TextView mShare;
    @Bind(R.id.user_info_favorite)
    TextView mFavorite;
    @Bind(R.id.user_info_read)
    TextView mRead;
    @Bind(R.id.user_info_edit)
    TextView mEdit;
    @Bind(R.id.user_info_headLayout)
    LinearLayout mHeadLayout;
    @Bind(R.id.user_info_toolbar)
    Toolbar mToolbar;
    @Bind(R.id.user_info_toolLayout)
    CollapsingToolbarLayout mToolLayout;
    @Bind(R.id.user_info_tabLayout)
    TabLayout mTabLayout;
    @Bind(R.id.user_info_appBar)
    AppBarLayout mAppBar;
    @Bind(R.id.user_info_viewpager)
    ViewPager mViewpager;

    private int mType;
    private MyUser myUser;
    private Context mContext;
    private boolean isAdd = false;
    private boolean isDelete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        initBar();
        initData();
        initView();
    }

    @Override
    public void initView() {
        mName.setText(myUser.getUsername());
        mBlog.setText(myUser.getmFavorite());
        mDesc.setText(myUser.getmDesc());
        mShare.setText("10篇");
        mFavorite.setText("100次");
        mRead.setText("100次");
        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(mContext, UserEditActivity.class), 0);
            }
        });
        String mUrl = myUser.getmHeadUrl(mContext);
        Glide.with(mContext).load(mUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                mAppBar.setBackgroundDrawable(new BitmapDrawable(BlurUtil.fastblur(mContext, bitmap, 80)));
                //使用CollapsingToolbarLayout必须把title设置到CollapsingToolbarLayout上，设置到Toolbar上则不会显示
                mToolLayout.setContentScrim(new BitmapDrawable(BlurUtil.fastblur(mContext, bitmap, 80)));
                mHead.setImageBitmap(bitmap);
            }
        });
        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset <= -mHeadLayout.getHeight() / 2) {
                    mToolLayout.setTitle(myUser.getUsername());
                    mToolbar.setBackgroundColor(Color.parseColor(ThemeUtil.readThemeColor(mContext)
                    ));
                    mToolLayout.setExpandedTitleColor(Color.WHITE);
                    StatusBarUtils.setColor(UserInfoActivity.this, ThemeUtil.readThemeColor(mContext)
                    );
                    isAdd = true;
                    isDelete = false;
                } else {
                    mToolLayout.setTitle(" ");
                    mToolbar.setBackgroundColor(Color.parseColor("#00000000"));
                    if (isAdd && !isDelete) {
                        StatusBarUtils.cleanColor(UserInfoActivity.this);
                        isDelete = true;
                    }
                }
            }
        });
        UserInfoViewPager vpAdapter = new UserInfoViewPager(getSupportFragmentManager(), mType);
        mViewpager.setAdapter(vpAdapter);
        mViewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewpager));
    }

    @Override
    public void setView() {

    }

    @Override
    public void initBar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void initTheme() {

    }

    @Override
    public void initData() {
        myUser = (MyUser) getIntent().getSerializableExtra("User");
        mType = getIntent().getIntExtra("Type", 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_CANCELED) {
            //退出登录
            setResult(RESULT_CANCELED);
            finish();
        } else if (requestCode == 0 && resultCode == RESULT_OK) {
            //修改资料
            setResult(RESULT_OK);
            finish();
        }
    }
}
