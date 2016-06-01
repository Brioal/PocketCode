package com.brioal.pocketcode.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brioal.pocketcode.view.swipeback.app.SwipeBackActivity;
import com.brioal.pocketcode.util.StatusBarUtils;
import com.brioal.pocketcode.util.ToastUtils;
import com.brioal.pocketcode.R;
import com.brioal.pocketcode.entiy.ContentModel;
import com.brioal.pocketcode.interfaces.ActivityInterFace;
import com.brioal.pocketcode.util.ThemeUtil;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.UpdateListener;

public class WebViewActivity extends SwipeBackActivity implements ActivityInterFace {

    @Bind(R.id.web_toolbar)
    Toolbar mToolbar;
    @Bind(R.id.web_webView)
    WebView mWebView;
    @Bind(R.id.web_refresh)
    SwipeRefreshLayout mRefresh;
    @Bind(R.id.web_commit)
    EditText mCommit;
    @Bind(R.id.web_collect)
    CheckBox mCollect;
    @Bind(R.id.web_parise)
    CheckBox mPraise;
    @Bind(R.id.web_main_container)
    CoordinatorLayout mContainer;
    @Bind(R.id.web_bottomLayout)
    LinearLayout mBottomLayout;
    @Bind(R.id.web_read)
    TextView mRead;

    private ContentModel mModel;

    private String mContentId;
    private String mUrl;
    private String mTitle;
    Context mContext;
    private int Praise;
    private int Collect;
    private int Read;

    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //显示网络超时
            if (mRefresh.isRefreshing()) {
                mRefresh.setRefreshing(false);
                Snackbar.make(mContainer, "网络暂时不可用,请稍后再试", Snackbar.LENGTH_SHORT).show();
            }
        }
    };
    private String TAG = "WebInfo";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);
        mContext = this;
        initData();
        initBar();
        initRefreshLayout();
        initView();
        setView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initTheme();
    }

    public void initBar() {
        mToolbar.setTitle(mTitle);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void initTheme() {
        String color = ThemeUtil.readThemeColor(mContext);
        mToolbar.setBackgroundColor(Color.parseColor(color));
        StatusBarUtils.setColor(this, color);
    }

    private void initRefreshLayout() {
        mRefresh.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
    }

    @Override
    public void initData() {
        mUrl = getIntent().getStringExtra("Url");
        mTitle = getIntent().getStringExtra("Title");
        mContentId = getIntent().getStringExtra("Id");
    }

    public void initView() {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }


        });

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);  //支持js
        settings.setSupportZoom(true);  //支持缩放，默认为true。是下面那个的前提。
        settings.setBuiltInZoomControls(true); //设置内置的缩放控件。
        settings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
        settings.supportMultipleWindows();  //多窗口
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //关闭webview中缓存
        settings.setAllowFileAccess(true);  //设置可以访问文件
        settings.setNeedInitialFocus(true); //当webview调用requestFocus时为webview设置节点
        settings.setLoadsImagesAutomatically(true);  //支持自动加载图片
        settings.setDefaultTextEncodingName("utf-8");//设置编码格式
        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setSupportZoom(true);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setDefaultTextEncodingName("utf-8");
        mWebSettings.setLoadsImagesAutomatically(true);
        //调用JS方法.安卓版本大于17,加上注解 @JavascriptInterface
        saveData(mWebSettings);
        mWebView.setWebChromeClient(webChromeClient);
        mWebView.setWebViewClient(webViewClient);
        mWebView.loadUrl(mUrl);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.loadUrl(mUrl);
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mHander.sendEmptyMessage(0);
                    }
                }, 9000);
            }
        });
        //显示收藏点赞的数量
        BmobQuery<ContentModel> query = new BmobQuery<>();
        query.getObject(this, mContentId, new GetListener<ContentModel>() {
            @Override
            public void onSuccess(ContentModel contentModel) {
                if (contentModel != null) {
                    mModel = contentModel;
                    Praise = mModel.getmPraise();
                    Collect = mModel.getmCollect();
                    Read = mModel.getmRead();
                    mCollect.setText(Collect + "");
                    mPraise.setText(Praise + "");
                    mRead.setText(Read + "");
                    mModel.increment("mRead");
                    mModel.update(mContext, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            Log.i(TAG, "Read++");
                            Read++;
                            mRead.setText(Read + "");
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Log.i(TAG, "onFailure: " + s);
                        }
                    });
                }
            }

            @Override
            public void onFailure(int i, String s) {
                ToastUtils.showToast(mContext, s);
            }
        });

    }

    @Override
    public void setView() {
        mPraise.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mModel == null) {
                    return;
                }
                if (isChecked) {
                    mModel.increment("mPraise");
                    Praise++;
                } else {
                    mModel.increment("mPraise", -1);
                    Praise--;
                }
                mModel.update(mContext, mContentId, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        mPraise.setText(Praise + "");
                        Log.i(TAG, "mPraise--");
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Log.i(TAG, "onFailure: " + s);
                    }
                });
            }
        });
        mCollect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mModel == null) {
                    return;
                }
                if (isChecked) {
                    mModel.increment("mCollect");
                    Collect++;
                } else {
                    mModel.increment("mCollect", -1);
                    Collect--;
                }
                mModel.update(mContext, mContentId, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        mCollect.setText(Collect + "");
                        Log.i(TAG, "mCollect++");
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Log.i(TAG, "onFailure: " + s);
                    }
                });
            }
        });
    }

    private void saveData(WebSettings mWebSettings) {
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setDatabaseEnabled(true);
        mWebSettings.setAppCacheEnabled(true);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        mWebSettings.setAppCachePath(appCachePath);
    }

    WebViewClient webViewClient = new WebViewClient() {

        /**
         * 多页面在同一个WebView中打开，就是不新建activity或者调用系统浏览器打开
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

    };

    WebChromeClient webChromeClient = new WebChromeClient() {

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
        }

        @Override
        public void onGeolocationPermissionsHidePrompt() {
            super.onGeolocationPermissionsHidePrompt();
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);//注意个函数，第二个参数就是是否同意定位权限，第三个是是否希望内核记住
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (title != null) {
                getSupportActionBar().setTitle(title);
            }
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress != 100) {
                mRefresh.setRefreshing(true);
            } else {
                mRefresh.setRefreshing(false);
            }

        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();

            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflator = new MenuInflater(this);
        //装填R.menu.my_menu对应的菜单,并添加到menu中
        inflator.inflate(R.menu.menu_web, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.action_up:
                mWebView.scrollTo(0, 0);
                break;
            case R.id.action_copy:
                ClipboardManager c = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                c.setText(mUrl);//设置Clipboard 的内容
                Toast.makeText(mContext, "复制成功", Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, mUrl);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "选择要分享的方式"));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
