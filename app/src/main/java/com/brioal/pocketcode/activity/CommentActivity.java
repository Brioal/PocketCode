package com.brioal.pocketcode.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.adapter.CommentAdapter;
import com.brioal.pocketcode.entiy.CommentModel;
import com.brioal.pocketcode.interfaces.onCommentItemClickListener;
import com.brioal.pocketcode.util.ClipboardUtil;
import com.brioal.pocketcode.util.Constants;
import com.brioal.pocketcode.util.SoftInputUtil;
import com.brioal.pocketcode.util.StatusBarUtils;
import com.brioal.pocketcode.util.ThemeUtil;
import com.brioal.pocketcode.util.ToastUtils;
import com.brioal.pocketcode.view.swipeback.app.SwipeBackActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class CommentActivity extends SwipeBackActivity implements onCommentItemClickListener {

    @Bind(R.id.toolBar)
    Toolbar mToolBar;
    @Bind(R.id.comment_recyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.comment_et)
    EditText mEt;
    @Bind(R.id.comment_btn)
    Button mBtnadd;

    private String[] option1 = new String[]{"回复", "复制", "删除"};
    private String[] option2 = new String[]{"回复", "复制"};

    private String mMessageId ;
    private List<CommentModel> mList;
    private CommentAdapter mAdapter;
    private String TAG = "CommentInfo";
    private String mParent = null;


    @Override
    public void initData() {
        super.initData();
        mMessageId = getIntent().getStringExtra("MessageId");
    }

    @Override
    public void initBar() {
        mToolBar.setTitle("查看评论");
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
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mMessageId = getIntent().getStringExtra("MessageId");
        mBtnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment(mEt.getText().toString(), mParent);
            }
        });
    }

    @Override
    public void loadDataNet() {
        super.loadDataNet();
        Constants.getmDataUtil(mContext).getCommentNet(mMessageId, new FindListener<CommentModel>() {
            @Override
            public void onSuccess(List<CommentModel> list) {
                Log.i(TAG, "onSuccess: 加载评论成功"+list.size());
                mList = list;
                mHandler.sendEmptyMessage(0);
            }

            @Override
            public void onError(int i, String s) {
                Log.i(TAG, "onError: " + s);
            }
        });
    }

    //发表评论
    private void addComment(String content, String parent) {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        String userId = Constants.getmDataUtil(mContext).getUserLocal().getObjectId();
        CommentModel model = new CommentModel(userId, content, parent, mMessageId);
        showProgressDialog("请稍等", "正在上传评论中...");
        model.save(mContext, new SaveListener() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "onSuccess: 发表评论成功");
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                new Thread(mRunnable).start();
                mParent = null;
                mEt.setText("");
            }

            @Override
            public void onFailure(int i, String s) {
                Log.i(TAG, "onFailure: 加载评论失败" + s);
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                showNoticeDialog("出错了", s);

            }
        });
    }

    @Override
    public void setView() {
        mAdapter = new CommentAdapter(mContext, mList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setListener(this);
    }

    //显示加载Dialog
    public void showProgressDialog(String title, String message) {
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    //显示提示Dialog
    public void showNoticeDialog(String title, String message) {
        mNoticeBuild = new AlertDialog.Builder(mContext);
        mNoticeDialog = mNoticeBuild.setTitle(title).setMessage(message).setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
        mNoticeDialog.show();
    }

    private ProgressDialog mProgressDialog;
    private AlertDialog.Builder mNoticeBuild;
    private AlertDialog mNoticeDialog;


    //评论id , 父评论id , 用户id , 评论内容 , 评论的id
    @Override
    public void onClickItem(String commentId, String parentId, String userId, String content) {
        if (Constants.getmDataUtil(mContext).getUserLocal() == null) {
            startActivityForResult(new Intent(mContext, LoginAndRegisterActivity.class), 0);
        } else {
            if (parentId == null) {
                mParent = commentId;
            } else {
                mParent = parentId;
            }
            showChooseDialog(commentId, userId, content);
        }

    }

    //弹出选择框
    public void showChooseDialog(final String commentId, String userID, final String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        String[] options = null;
        if (userID.equals(Constants.getmDataUtil(mContext).getUserLocal().getObjectId())) {
            options = option1;
        } else {
            options = option2;
        }
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: //回复
                        mEt.requestFocus();
                        SoftInputUtil.judgeSoftInut(mContext);
                        break;
                    case 1: //复制
                        ClipboardUtil.setContent(mContext, content);
                        break;
                    case 2: //删除
                        showProgressDialog("请稍等","正在删除评论,请稍等");
                        CommentModel model = new CommentModel();
                        model.setObjectId(commentId);
                        model.delete(mContext, new DeleteListener() {
                            @Override
                            public void onSuccess() {
                                if (mProgressDialog.isShowing()) {
                                    mProgressDialog.dismiss();
                                }
                                ToastUtils.showToast(mContext, "评论删除成功");
                                new Thread(mRunnable).start();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                ToastUtils.showToast(mContext, s);

                            }
                        });
                        break;
                }
            }
        }).create().show();
    }
}
