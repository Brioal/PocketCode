package com.brioal.pocketcode.fragment;

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
import android.widget.Button;

import com.brioal.pocketcode.util.ToastUtils;
import com.brioal.pocketcode.R;
import com.brioal.pocketcode.entiy.MyUser;

import butterknife.ButterKnife;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Brioal on 2016/5/10.
 */
public class JoinFragment extends Fragment implements View.OnClickListener {
    public static JoinFragment mFragment;
    AppCompatMultiAutoCompleteTextView mPhone;
    Button mBtnCode;
    AppCompatMultiAutoCompleteTextView mCode;
    AppCompatMultiAutoCompleteTextView mPassword;
    Button mBtnJoin;
    private int secondes = 60;
    private MyUser mUser;
    private boolean isComplete = false;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (secondes != 0) {
                mBtnCode.setText(secondes + "秒后重新获取");
                secondes--;
            } else {
                mBtnCode.setEnabled(true);
                mBtnCode.setText("重新获取");
            }
        }
    };
    private Context mContext;
    private String TAG = "JoinInfo";

    public static JoinFragment getInstance() {
        if (mFragment == null) {
            mFragment = new JoinFragment();
        }
        return mFragment;
    }

    public void initId(View rootView) {
        mPhone = (AppCompatMultiAutoCompleteTextView) rootView.findViewById(R.id.join_et_phone);
        mPassword = (AppCompatMultiAutoCompleteTextView) rootView.findViewById(R.id.join_et_password);
        mCode = (AppCompatMultiAutoCompleteTextView) rootView.findViewById(R.id.join_et_code);
        mBtnCode = (Button) rootView.findViewById(R.id.join_btn_code);
        mBtnJoin = (Button) rootView.findViewById(R.id.join_btn_join);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_join, container, false);
        initId(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initActions();
    }

    //设置点击事件
    private void initActions() {
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
        mUser = new MyUser();
        mUser.setMobilePhoneNumber(mPhone.getText().toString());//设置手机号码（必填）
        mUser.setPassword(mPassword.getText().toString());                  //设置用户密码
        mUser.signOrLogin(mContext, mCode.getText().toString(), new SaveListener() {

            @Override
            public void onSuccess() {
                isComplete = true; //登录成功
                // TODO: 2016/5/10 注册成功,跳转到信息完善
                ToastUtils.showToast(mContext, "注册成功");

            }

            @Override
            public void onFailure(int code, String msg) {
                Log.i(TAG, "onFailure: " + msg);
                mPhone.setError("请全部核对后重试");
                mCode.setError("请全部核对后重试");
            }
        });
    }

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
