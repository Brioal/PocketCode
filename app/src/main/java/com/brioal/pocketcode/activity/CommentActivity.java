package com.brioal.pocketcode.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.adapter.CommentAdapter;
import com.brioal.pocketcode.entiy.CommentModel;
import com.brioal.pocketcode.interfaces.ActivityInterFace;
import com.brioal.pocketcode.util.BrioalConstan;
import com.brioal.pocketcode.util.StatusBarUtils;
import com.brioal.pocketcode.util.ThemeUtil;
import com.brioal.pocketcode.view.swipeback.app.SwipeBackActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class CommentActivity extends SwipeBackActivity implements ActivityInterFace {

    @Bind(R.id.toolBar)
    Toolbar mToolBar;
    @Bind(R.id.comment_listView)
    ExpandableListView mListView;
    @Bind(R.id.comment_et)
    EditText mEt;
    @Bind(R.id.comment_btn)
    Button mBtnadd;

    private String[] option1 = new String[]{"回复", "复制", "删除"};
    private String[] option2 = new String[]{"回复", "复制"};

    private String mMessageId = "123";
    private Context mContext;
    private List<CommentModel> mGroups;
    private List<List<CommentModel>> mChilds;
    private CommentAdapter mAdapter;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            initData();
        }
    };
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setView();
        }
    };
    private String TAG = "CommentInfo";
    private String mParent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);
        initBar();
        initView();
        new Thread(mRunnable).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initTheme();
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
    public void initData() {
        if (mGroups == null) {
            mGroups = new ArrayList<>();
        } else {
            mGroups.clear();
        }

        if (mChilds == null) {
            mChilds = new ArrayList<>();
        }
        BmobQuery<CommentModel> query = new BmobQuery<>();
        query.setLimit(100);
        query.addWhereEqualTo("mMessageId", mMessageId);
        query.order("-createdAt");
        query.findObjects(mContext, new FindListener<CommentModel>() {
            @Override
            public void onSuccess(List<CommentModel> list) {
                Log.i(TAG, "onSuccess: 加载评论成功" + list);
                mGroups = list;
                for (int i = 0; i < mGroups.size(); i++) {
                    mChilds.add(new ArrayList<CommentModel>());
                }
                for (int i = 0; i < mGroups.size(); i++) {
                    String id = mGroups.get(i).getObjectId();
                    for (int j = 0; j < mGroups.size(); j++) {
                        String parentId = mGroups.get(j).getmParent();
                        if (id.equals(parentId)) {
                            mChilds.get(i).add(mGroups.get(j));
                        }
                    }
                }
                for (int i = 0; i < mChilds.size(); i++) {
                    Log.i(TAG, "onSuccess: " + mChilds.get(i).size());
                }
                if (mGroups.size() > 0) {
                    mHandler.sendEmptyMessage(0);
                }
            }

            @Override
            public void onError(int i, String s) {
                Log.i(TAG, "onError: 加载评论失败" + s);
            }
        });
    }

    @Override
    public void initView() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mMessageId = getIntent().getStringExtra("MessageID");
        mBtnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment(mEt.getText().toString(), null);
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
        String userId = BrioalConstan.getmLocalUser(mContext).getUser().getObjectId();
        CommentModel model = new CommentModel(userId, content, parent, mMessageId);
        final ProgressDialog dialog = new ProgressDialog(mContext);

        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("上传评论中");
        dialog.setTitle("请稍等");
        dialog.show();
        model.save(mContext, new SaveListener() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "onSuccess: 发表评论成功");
                dialog.dismiss();
                new Thread(mRunnable).start();
            }

            @Override
            public void onFailure(int i, String s) {
                Log.i(TAG, "onFailure: 加载评论失败" + s);
                dialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("出错了").setMessage(s).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();

            }
        });
    }

    @Override
    public void setView() {
        mAdapter = new CommentAdapter(mContext, mGroups, mChilds);
        mListView.setAdapter(mAdapter);
        for (int i = 0; i < mGroups.size(); i++) {
            mListView.expandGroup(i, true);
        }
//        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//            @Override
//            public boolean onGroupClick(ExpandableListView parent, View v, final int groupPosition, long id) {
//                ToastUtils.showToast(mContext, "点击了评论");
//                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                String userId = mGroups.get(groupPosition).getmUserId();
//                String currentId = BrioalConstan.getmLocalUser(mContext).getUser().getObjectId();
//                builder.setItems(userId.equals(currentId) ? option1 : option2, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        switch (which) {
//                            case 0: //回复
//                                AlertDialog.Builder replyBuild = new AlertDialog.Builder(mContext);
//                                replyBuild.setTitle("书写回复内容");
//                                final EditText text = new EditText(mContext);
//                                builder.setView(text);
//                                builder.setPositiveButton("回复", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                        addComment(text.getText().toString(), mGroups.get(groupPosition).getObjectId());
//                                    }
//                                }).create().show();
//                                break;
//                            case 1: //复制
//                                ClipboardUtil.setContent(mContext, mGroups.get(groupPosition).getmContent());
//                                break;
//                            case 2://删除
//                                showPrigressDialog("请稍等", "删除回复中");
//                                mGroups.get(groupPosition).delete(mContext, new DeleteListener() {
//                                    @Override
//                                    public void onSuccess() {
//                                        Log.i(TAG, "onSuccess: 删除回复成功");
//                                        if (mProgressDialog.isShowing()) {
//                                            mProgressDialog.dismiss();
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onFailure(int i, String s) {
//                                        Log.i(TAG, "onFailure: 删除回复失败" + s);
//                                        showNoticeDialog("错误", s);
//                                    }
//                                });
//                        }
//
//                    }
//                }).create().show();
//
//                return true;
//            }
//        });
    }

    //显示加载Dialog
    public void showPrigressDialog(String title, String message) {
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

}
