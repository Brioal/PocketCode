package com.brioal.pocketcode.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.activity.UserEditActivity;
import com.brioal.pocketcode.base.BaseFragment;
import com.brioal.pocketcode.entiy.User;
import com.brioal.pocketcode.util.ToastUtils;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * 注册Fragment
 * Created by Brioal on 2016/5/10.
 */
public class JoinFragment extends BaseFragment implements View.OnClickListener {
    public static JoinFragment mFragment;
    private EditText mPhone;
    private Button mBtnCode;
    private EditText mCode;
    private EditText mPassword;
    private Button mBtnJoin;
    private int secondes = 60;
    private User mUser;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (secondes != 0) {
                mBtnCode.setText(secondes + "秒后重新获取");
                secondes--;
            } else {
                mTimer.cancel();
                mBtnCode.setEnabled(true);
                mBtnCode.setText("重新获取");
            }
        }
    };
    private Timer mTimer;
    private String TAG = "JoinInfo";

    public static JoinFragment getInstance() {
        if (mFragment == null) {
            mFragment = new JoinFragment();
        }
        return mFragment;
    }

    @Override
    public void initView() {
        super.initView();
        mRootView = inflater.inflate(R.layout.fragment_join, container, false);
        mPhone = (EditText) mRootView.findViewById(R.id.join_et_phone);
        mPassword = (EditText) mRootView.findViewById(R.id.join_et_password);
        mCode = (EditText) mRootView.findViewById(R.id.join_et_code);
        mBtnCode = (Button) mRootView.findViewById(R.id.join_btn_code);
        mBtnJoin = (Button) mRootView.findViewById(R.id.join_btn_join);
        mBtnCode.setOnClickListener(this);
        mBtnJoin.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    //获取验证码
    public void getCode() {
        mBtnCode.setEnabled(false);
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        }, 10, 1000);
        BmobSMS.requestSMSCode(mContext, mPhone.getText().toString(), "注册", new RequestSMSCodeListener() {
            @Override
            public void done(Integer smsId, BmobException ex) {
                if (ex == null) {//验证码发送成功
                    Log.i("smile", "短信id：" + smsId);//用于查询本次短信发送详情
                } else {
                    mPhone.setError("请核对手机号后重试！");
                }
            }
        });
    }

    //一键注册或者登陆
    public void join() {
        showProgressDialog("请稍等", "正在注册");
        mUser = new User();
        mUser.setMobilePhoneNumber(mPhone.getText().toString());//设置手机号码（必填）
        mUser.setPassword(mPassword.getText().toString());                  //设置用户密码
        mUser.signOrLogin(mContext, mCode.getText().toString(), new SaveListener() {
            @Override
            public void onSuccess() {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                Intent intent = new Intent(mContext, UserEditActivity.class);
                intent.putExtra("User", mUser);
                mContext.startActivityForResult(intent, 0);
                ToastUtils.showToast(mContext, "注册成功,现在请完善信息");
            }

            @Override
            public void onFailure(int code, String msg) {
                Log.i(TAG, "onFailure: " + msg);
                mPhone.setError("请全部核对后重试");
                mCode.setError("请全部核对后重试");
            }
        });
    }
    //显示加载进度条
    public void showProgressDialog(String title, String message) {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private ProgressDialog mProgressDialog;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.join_btn_code) {
            getCode();
        } else if (id == R.id.join_btn_join) {
            join();
        }
    }
}
