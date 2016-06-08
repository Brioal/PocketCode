package com.brioal.pocketcode.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.adapter.UserInfoViewPager;
import com.brioal.pocketcode.base.BaseActivity;
import com.brioal.pocketcode.entiy.AttentionEnity;
import com.brioal.pocketcode.entiy.User;
import com.brioal.pocketcode.util.BlurUtil;
import com.brioal.pocketcode.util.Constants;
import com.brioal.pocketcode.util.DataQuery;
import com.brioal.pocketcode.util.StatusBarUtils;
import com.brioal.pocketcode.util.ThemeUtil;
import com.brioal.pocketcode.util.ToastUtils;
import com.brioal.pocketcode.view.CircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 用户信息展示
 * 传入UserId ,当前用户传入null即可
 */

public class UserInfoActivity extends BaseActivity {
    @Bind(R.id.user_info_name)
    TextView mName;
    @Bind(R.id.user_info_blog)
    TextView mBlog;
    @Bind(R.id.user_info_head)
    CircleImageView mHead;
    @Bind(R.id.user_info_desc)
    TextView mDesc;
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
    private String mUserId;
    private User mUser;
    private boolean isAdd = false;
    private boolean isDelete = false;
    private boolean isAttention = false; //是否已经关注
    private String attentionId; //如果以关注,则传入关注item的id
    private AlertDialog mNoticeDialog;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        UserInfoViewPager vpAdapter = new UserInfoViewPager(getSupportFragmentManager(), mType);
        mViewpager.setAdapter(vpAdapter);
        mTabLayout.setupWithViewPager(mViewpager);
    }

    public void showNoticeDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        mNoticeDialog = builder.setTitle(title).setMessage(message).setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
        mNoticeDialog.show();
    }

    @Override
    public void setView() {
        if (mType == UserInfoViewPager.TYPE_MINE) {
            mEdit.setText("设置");
            mEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, UserEditActivity.class);
                    User user = Constants.getmDataUtil(mContext).getUserLocal();
                    intent.putExtra("User", user);
                    startActivityForResult(intent, 0);
                }
            });
        } else if (mType == UserInfoViewPager.TYPE_OTHER) {
            if (isAttention) { //已关注
                mEdit.setText("已关注");
            } else { //未关注
                mEdit.setText("未关注");
            }
            mEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "onClick: 添加关注");
                    User user = Constants.getmDataUtil(mContext).getUserLocal();
                    if (user == null) { //未登陆
                        Intent intent = new Intent(mContext, LoginAndRegisterActivity.class);
                        startActivityForResult(intent, 0);
                    } else {
                        if (isAttention) { //已关注
                            //取关操作
                            AttentionEnity enity = new AttentionEnity();
                            if (attentionId != null) {
                                enity.delete(mContext, attentionId, new DeleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        Log.i(TAG, "onSuccess: 取关成功");
                                        mEdit.setText("未关注");
                                        isAttention = false;
                                        setResult(RESULT_CANCELED);
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        Log.i(TAG, "onFailure: 取关失败" + s);
                                        showNoticeDialog("错误", s);
                                    }
                                });

                            } else {
                                ToastUtils.showToast(mContext, "获取数据失败,请重试");
                            }
                        } else { //未关注
                            //添加关注操作
                            AttentionEnity enity = new AttentionEnity(user.getObjectId(), mUserId);
                            enity.update(mContext, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    setResult(RESULT_OK);
                                    Log.i(TAG, "onSuccess: 上传关注数据成功");
                                    mEdit.setText("已关注");
                                    isAttention = true;
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    Log.i(TAG, "onFailure: 添加关注失败" + s);
                                    showNoticeDialog("错误", s);
                                }
                            });
                        }
                    }
                }
            });
        }
        mName.setText(mUser.getUsername());
        mBlog.setText(mUser.getmFavorite());
        mDesc.setText(mUser.getmDesc());
        String mUrl = mUser.getmHeadUrl(mContext);
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
                    mToolLayout.setTitle(mUser.getUsername());
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
        mUserId = getIntent().getStringExtra("UserId");
        isAttention = getIntent().getBooleanExtra("IsAttention", false);
        attentionId = getIntent().getStringExtra("AttentionId");
        if (mUserId == null) { //本地用户
            mType = UserInfoViewPager.TYPE_MINE;
            mUser = Constants.getmDataUtil(mContext).getUserLocal();
            mHandler.sendEmptyMessage(0);

        } else { //其他用户
            mType = UserInfoViewPager.TYPE_OTHER;
            DataQuery<User> query = new DataQuery<>();
            query.getData(mContext, mUserId, new GetListener<User>() {
                @Override
                public void onSuccess(User user) {
                    Log.i(TAG, "onSuccess: 加载用户信息成功");
                    mUser = user;
                    mHandler.sendEmptyMessage(0);
                }

                @Override
                public void onFailure(int i, String s) {
                    Log.i(TAG, "onFailure: 加载用户失败" + s);
                }
            });
        }
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
            loadDataLocal();
            setResult(RESULT_OK);
            finish();
        } else if (requestCode == 0 && resultCode == 3) { //未做任何改变

        }
    }
}
