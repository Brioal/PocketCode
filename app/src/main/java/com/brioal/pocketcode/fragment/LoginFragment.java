package com.brioal.pocketcode.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.base.BaseFragment;
import com.brioal.pocketcode.entiy.User;
import com.brioal.pocketcode.util.Constants;
import com.brioal.pocketcode.util.ToastUtils;
import com.dd.CircularProgressButton;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.LogInListener;

/**
 * 登录界面
 * Created by Brioal on 2016/5/10.
 */
public class LoginFragment extends BaseFragment implements View.OnClickListener {
    public static LoginFragment mFragment;
    EditText mUsername;
    EditText mPassword;
    CircularProgressButton mBtnLogin;
    private Timer timer;
    private User mUser;
    private boolean isComplete = false;
    private String TAG = "LoginInfo";
    private int progress;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                if (isComplete) {
                    mBtnLogin.setProgress(100);
                } else {
                    mBtnLogin.setProgress(99);
                    progress = 0;
                }
            } else {
                mBtnLogin.setProgress(msg.what);
            }
        }
    };

    public static LoginFragment getInstance() {
        if (mFragment == null) {
            mFragment = new LoginFragment();
        }
        return mFragment;
    }

    @Override
    public void initView() {
        super.initView();
        mRootView = inflater.inflate(R.layout.fragment_login, container, false);
        mUsername = (EditText) mRootView.findViewById(R.id.login_et_username);
        mPassword = (EditText) mRootView.findViewById(R.id.login_et_password);
        mBtnLogin = (CircularProgressButton) mRootView.findViewById(R.id.login_et_btn_login);

        mBtnLogin.setOnClickListener(this);
    }

    //登陆
    public void login() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                progress += 20;
                if (progress == 120) {
                    progress = 100;
                }
                handler.sendEmptyMessage(progress);
            }
        }, 100);
        BmobUser.loginByAccount(mContext, mUsername.getText().toString(), mPassword.getText().toString(), new LogInListener<User>() {

            @Override
            public void done(User user, BmobException e) {
                if (user != null) {
                    mUser = user;
                    String objectId = mUser.getObjectId();
                    BmobQuery<User> query = new BmobQuery<User>();
                    query.addWhereEqualTo("objectId", objectId);
                    query.getObject(mContext, objectId, new GetListener<User>() {
                        @Override
                        public void onSuccess(User user) {
                            if (timer != null) {
                                timer.cancel();
                            }
                            Constants.getmDataUtil(mContext).saveUserLocal(user);
                            isComplete = true;
                            mBtnLogin.setProgress(100);
                            ToastUtils.showToast(mContext, "登录成功");
                            mBtnLogin.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getActivity().setResult(Activity.RESULT_OK);
                                    getActivity().finish();
                                }
                            }, 1000);
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Log.i(TAG, "done: 登陆失败");
                            showNoticeDialog(s);
                        }
                    });


                } else {


                }
            }
        });
    }
    //显示警告dialog
    public void showNoticeDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("警告").setMessage(msg).setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login_et_btn_login) {
            login();
        }
    }
}
