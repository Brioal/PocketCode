package com.brioal.pocketcode.activity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.brioal.pocketcode.util.NetWorkUtil;
import com.brioal.pocketcode.util.StatusBarUtils;
import com.brioal.pocketcode.util.ToastUtils;
import com.brioal.pocketcode.R;
import com.brioal.pocketcode.database.DBHelper;
import com.brioal.pocketcode.interfaces.ActivityInterFace;
import com.brioal.pocketcode.entiy.ContentModel;
import com.brioal.pocketcode.entiy.MyUser;
import com.brioal.pocketcode.util.LocalUserUtil;
import com.brioal.pocketcode.util.ThemeUtil;
import com.brioal.pocketcode.view.Tag;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.listener.SaveListener;

public class AddContentActivity extends AppCompatActivity implements View.OnClickListener, ActivityInterFace {

    @Bind(R.id.add_toolBar)
    Toolbar mToolBar;
    @Bind(R.id.add_title)
    EditText mTitle;
    @Bind(R.id.add_desc)
    EditText mDesc;
    @Bind(R.id.add_url)
    EditText mUrl;
    @Bind(R.id.add_tag)
    EditText mTag;
    @Bind(R.id.add_addTag)
    ImageButton mBtnAddTag;
    @Bind(R.id.add_tag_layout)
    LinearLayout mTagLayout;
    @Bind(R.id.add_tag_used)
    LinearLayout mTagUsed;

    private Context mContext;
    private MyUser user;
    private String mTags = "";
    private String title;
    private String desc;
    private String url;
    private String TAG = "AddInfo";
    private ProgressDialog dialog;
    private AlertDialog.Builder builder;
    private DBHelper mHelper;

    private List<String> mLocalTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mHelper = new DBHelper(mContext, "PocketCode.db3", null, 1);
        setContentView(R.layout.activity_add_content);
        ButterKnife.bind(this);
        initBar();
        initData();
        initActions();
        initView();
    }

    @Override
    public void initBar() {
        mToolBar.setTitle("分享文章");
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
    public void initView() {
        dialog = new ProgressDialog(mContext);
        dialog.setTitle("请稍等");
        dialog.setMessage("正在发表,请稍等");
        dialog.setCancelable(false);
        builder = new AlertDialog.Builder(mContext);
        builder.setTitle("警告");
        builder.setCancelable(false);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //获取标题
                if (NetWorkUtil.isNetworkConnected(mContext)) {
                    dialog.setMessage("正在获取标题");
                    dialog.show();
                    MessageAsy task = new MessageAsy();
                    task.execute(s.toString());
                } else {
                    builder.setMessage("当前网络不可用,无法自动获取标题");
                    builder.create().show();
                }
            }
        });
    }

    @Override
    public void setView() {
        for (int i = 0; i < mLocalTags.size(); i++) {
            final String text = mLocalTags.get(i);
            final Tag tag = new Tag(mContext);
            tag.setText(text);
            tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mTags.contains(text)) {
                        addTag(text);
                    }
                }
            });
        }
    }

    private void initActions() {
        mBtnAddTag.setOnClickListener(this);
    }

    //获取当前属性
    public void initData() {
        if (mLocalTags == null) {
            mLocalTags = new ArrayList<>();
        } else {
            mLocalTags.clear();
        }
        user = LocalUserUtil.Read(this);
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from Tag", null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            mLocalTags.add(cursor.getString(1));
        }
        cursor.close();
    }


    //添加文章
    private void addContent() {
        user = LocalUserUtil.Read(mContext);
        if (user == null) {
            ToastUtils.showToast(mContext, "未登陆,请登陆后再操作~");
            startActivity(new Intent(mContext, LoginAndRegisterActivity.class));
            return;
        }
        if (NetWorkUtil.isNetworkConnected(mContext)) {
            dialog.setMessage("正在发表,请稍等....");
            dialog.setCancelable(false);
            dialog.show();
            title = mTitle.getText().toString();
            desc = mDesc.getText().toString();
            url = mUrl.getText().toString();
            //用户自己输入标题
            ContentModel model = new ContentModel(user.getObjectId(), title, desc, mTags, System.currentTimeMillis(), 0, 0, 0, 0, url, user.getmHeadUrl(mContext));
            model.save(mContext, new SaveListener() {
                @Override
                public void onSuccess() {
                    Log.i(TAG, "onSuccess: 保存数据到网络成功");
                    dialog.dismiss();
                    finish();
                }

                @Override
                public void onFailure(int i, String s) {
                    Log.i(TAG, "onFailure: 保存数据到网络失败" + s);
                }
            });

        } else {
            builder.setMessage("当前网络不可用,请等网络可用时重试");
        }
    }


    //添加标签
    private void addTag(String text) {
        String tagText = null;
        if (text == null) {
            tagText = mTag.getText().toString();
        } else {
            tagText = text;
        }
        if (tagText.isEmpty()) {
            mTag.setError("标签不能为空");
        } else {
            if (mTags.isEmpty()) {
                mTags += tagText;
            } else {
                mTags += "," + tagText;
            }
            Tag tag = new Tag(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tag.setTextSize(16);
            tag.setText(tagText);
            tag.setPadding(10, 10, 10, 10);
            params.setMargins(20, 10, 10, 10);
            mTagLayout.addView(tag, params);
            mTag.setText("");
        }

    }

    //判断是否有内容，有就保存
    public void judgeContent() {
        if (mUrl.getText().toString().isEmpty()) {
            finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("提示").setMessage("存在正在编辑的内容，现在退出将会丢失所有内容，是否退出").setNegativeButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();

                }
            }).setPositiveButton("不退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        }
    }


    protected void onResume() {
        super.onResume();
        final ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData data = cm.getPrimaryClip();
        if (data != null) {
            ClipData.Item item = data.getItemAt(0);
            String content = item.getText().toString();
            System.out.println(item.getText().toString());
            SharedPreferences preferences = getSharedPreferences("Url", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            String lastContent = "";
            if (content.contains("http") || content.contains("www")) {
                lastContent = preferences.getString("lastContent", "");
                if (!lastContent.equals(content)) {
                    mUrl.setText(content);
                    MessageAsy asy = new MessageAsy();
                    asy.execute(content);
                    editor.putString("lastContent", content);
                    editor.apply();
                }
            }
        }
        initTheme();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_content, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                judgeContent();
                break;
            case R.id.action_add:
                if (!mUrl.getText().toString().isEmpty()) {
                    addContent();
                } else {
                    mUrl.setError("链接不能为空");
                    mUrl.requestFocus();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_addTag:
                addTag(null);
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveTags();
    }

    public void saveTags() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        db.execSQL("delete from Tag where _id > 0");
        String[] texts = mTags.split(",");
        for (int i = 0; i < texts.length; i++) {
            db.execSQL("insert into Tag values ( null , ? )", new Object[]{texts[i]});
        }
        db.close();
    }


    private class MessageAsy extends AsyncTask<String, Integer, String> {
        public String doInBackground(String... params) {
            String url = params[0];
            String title = null;
            try {
                Document localDocument = Jsoup.connect(url).get();
                title = localDocument.title();
            } catch (IOException localIOException) {
                localIOException.printStackTrace();
            }

            return title;
        }


        public void onPostExecute(String params) {
            Log.i(TAG, "onPostExecute: " + params);
            title = params;
            mTitle.setText(title);
            dialog.dismiss();
        }
    }
}
