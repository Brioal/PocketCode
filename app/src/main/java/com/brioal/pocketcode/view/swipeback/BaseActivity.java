package com.brioal.pocketcode.view.swipeback;

import android.support.v7.app.AppCompatActivity;

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.util.StatusBarUtils;


/**
 * Created by Jaeger on 16/2/14.
 * StatusBarDemo
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setStatusBar();
    }

    protected void setStatusBar() {
        StatusBarUtils.setColor(this, getResources().getColor(R.color.colorPrimary));
    }

}
