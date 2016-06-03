package com.brioal.pocketcode.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatMultiAutoCompleteTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.entiy.MyUser;
import com.brioal.pocketcode.util.BrioalConstan;
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

/**登录界面
 * Created by Brioal on 2016/5/10.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {
    public static LoginFragment mFragment;
    AppCompatMultiAutoCompleteTextView mUsername;
    AppCompatMultiAutoCompleteTextView mPassword;
    CircularProgressButton mBtnLogin;
    private Timer timer;

    private MyUser mUser;
    private Context mContext;
    private boolean isComplete = false;
    private String TAG = "LoginInfo";
    private int progress;

    private Handler mHandler = new Handler() {
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

    public void initId(View rootView) {
        mUsername = (AppCompatMultiAutoCompleteTextView) rootView.findViewById(R.id.login_et_username);
        mPassword = (AppCompatMultiAutoCompleteTextView) rootView.findViewById(R.id.login_et_password);
        mBtnLogin = (CircularProgressButton) rootView.findViewById(R.id.login_et_btn_login);


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        initId(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initActions();
        initView();
    }

    private void initActions() {
        mBtnLogin.setOnClickListener(this);
    }

    private void initView() {

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
                mHandler.sendEmptyMessage(progress);
            }
        }, 100);
        BmobUser.loginByAccount(mContext, mUsername.getText().toString(), mPassword.getText().toString(), new LogInListener<MyUser>() {

            @Override
            public void done(MyUser user, BmobException e) {
                if (user != null) {
                    mUser = user;
                    String objectId = mUser.getObjectId();
                    BmobQuery<MyUser> query = new BmobQuery<MyUser>();
                    query.addWhereEqualTo("objectId", objectId);
                    query.getObject(mContext, objectId, new GetListener<MyUser>() {
                        @Override
                        public void onSuccess(MyUser myUser) {
                            if (timer != null) {
                                timer.cancel();
                            }
                            BrioalConstan.getmLocalUser(mContext).save(myUser);
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

                        }
                    });


                } else {
                    Log.i(TAG, "done: 登陆失败");
                    // TODO: 2016/5/10 LoginFiled
                }
            }
        });
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
