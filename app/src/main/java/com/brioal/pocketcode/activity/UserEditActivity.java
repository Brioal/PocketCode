package com.brioal.pocketcode.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.base.BaseActivity;
import com.brioal.pocketcode.entiy.User;
import com.brioal.pocketcode.util.Constants;
import com.brioal.pocketcode.util.ImageTools;
import com.brioal.pocketcode.util.StatusBarUtils;
import com.brioal.pocketcode.util.ThemeUtil;
import com.brioal.pocketcode.util.ToastUtils;
import com.brioal.pocketcode.view.CircleImageView;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * 用户信息设置类
 * 传入MyUser
 */
public class UserEditActivity extends BaseActivity implements View.OnClickListener {


    @Bind(R.id.user_edit_toolbar)
    Toolbar mToolbar;
    @Bind(R.id.user_edit_head)
    CircleImageView mHead;
    @Bind(R.id.user_edit_headLayout)
    LinearLayout mHeadLayout;
    @Bind(R.id.user_edit_name)
    TextView mName;
    @Bind(R.id.user_edit_nameLayout)
    LinearLayout mNameLayout;
    @Bind(R.id.user_edit_desc)
    TextView mDesc;
    @Bind(R.id.user_edit_descLayout)
    LinearLayout mDescLayout;
    @Bind(R.id.user_edit_favorite)
    TextView mFavorite;
    @Bind(R.id.user_edit_favoriteLayout)
    LinearLayout mFavoriteLayout;
    @Bind(R.id.user_edit_blog)
    TextView mBlog;
    @Bind(R.id.user_edit_blogLayout)
    LinearLayout mBlogLayout;
    @Bind(R.id.user_edit_github)
    TextView mGithub;
    @Bind(R.id.user_edit_GitHubLayout)
    LinearLayout mGitHubLayout;
    @Bind(R.id.user_edit_qq)
    TextView mQq;
    @Bind(R.id.user_edit_qqLayout)
    LinearLayout mQqLayout;
    @Bind(R.id.user_edit_btn_out)
    Button mBtnOut;

    private User user;
    String HeadUrl;
    String Name;
    String Desc;
    String Interst;
    String Blog;
    String Github;
    String QQ;
    private String headPath;
    private String TAG = "UserInfo";
    private AlertDialog.Builder builder;
    private int GETPIC = 2;
    private boolean headHasChange = false;
    private boolean hasChenged = false;


    private void initActions() {
        mHeadLayout.setOnClickListener(this);
        mNameLayout.setOnClickListener(this);
        mDescLayout.setOnClickListener(this);
        mFavoriteLayout.setOnClickListener(this);
        mBlogLayout.setOnClickListener(this);
        mGitHubLayout.setOnClickListener(this);
        mQqLayout.setOnClickListener(this);
        mBtnOut.setOnClickListener(this);
    }

    public void showEdit(final TextView mTv, String title, final String key) {
        View edit_layout;
        builder = new AlertDialog.Builder(mContext);
        edit_layout = LayoutInflater.from(mContext).inflate(R.layout.dialog_edit, null, false);
        builder.setView(edit_layout);
        TextView mTitle = (TextView) edit_layout.findViewById(R.id.dialog_title);
        final EditText mEt = (EditText) edit_layout.findViewById(R.id.dialog_content);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hasChenged = true;
                mTv.setText(mEt.getText().toString());
                User myUser = new User();

                myUser.setValue(key, mTv.getText().toString());
                user.setValue(key, mTv.getText().toString());
                myUser.update(mContext, user.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        Log.i(TAG, "onSuccess: 保存数据成功" + key);
                        ToastUtils.showToast(mContext, "保存数据成功");
                        Constants.getmDataUtil(mContext).saveUserLocal(user);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Log.i(TAG, "onFailure: 保存数据失败" + s);
                        showNoticeDialog("错误", s);
                    }
                });
                dialog.dismiss();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mTitle.setText(title);
        mEt.setText(mTv.getText().toString());
        builder.create().show();
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setContentView(R.layout.activity_user_edit);
        ButterKnife.bind(this);
        HeadUrl = user.getmHeadUrl(mContext);
        Name = user.getUsername();
        Desc = user.getmDesc();
        Interst = user.getmFavorite();
        Blog = user.getmBlog();
        Github = user.getmGitHub();
        QQ = user.getmQQ();

        if (HeadUrl != null) {
            Glide.with(mContext).load(HeadUrl).into(mHead);
        } else {
            mHead.setImageResource(R.mipmap.ic_default_head);
        }
        if (Name != null) {
            mName.setText(Name);
        }
        if (Desc != null) {
            mDesc.setText(Desc);
        }
        if (Interst != null) {
            mFavorite.setText(Interst);
        }
        if (Blog != null) {
            mBlog.setText(Blog);
        }
        if (Github != null) {
            mGithub.setText(Github);
        }
        if (QQ != null) {
            mQq.setText(QQ);
        }

        initActions();
    }

    @Override
    public void setView() {

    }

    @Override
    public void initData() {
        user = (User) getIntent().getSerializableExtra("User");
    }

    @Override
    public void loadDataNet() {
        super.loadDataNet();
        mHandler.sendEmptyMessage(0);
    }

    public void initBar() {


    }

    @Override
    public void initTheme() {
        String color = ThemeUtil.readThemeColor(mContext);
        mToolbar.setBackgroundColor(Color.parseColor(color));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        StatusBarUtils.setColor(this, color);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initTheme();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (hasChenged) {
                    setResult(RESULT_OK);
                } else {
                    setResult(3);
                }
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //显示加载Dialog
    public void showProgressDialog(String title, String message, boolean isValue) {

        mProgressDialog = new ProgressDialog(mContext);
        if (isValue) {
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        } else {
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    //显示错误Dialog
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

    private AlertDialog mNoticeDialog;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_edit_headLayout: //头像更改
                showPicturePicker(this);
                break;
            case R.id.user_edit_nameLayout: //昵称更改:
                showEdit(mName, "修改昵称", "userName");
                break;
            case R.id.user_edit_descLayout: //简介更改:
                showEdit(mDesc, "修改简介", "mDesc");
                break;
            case R.id.user_edit_favoriteLayout: //兴趣更改:
                showEdit(mFavorite, "修改擅长领域", "mFavorite");
                break;
            case R.id.user_edit_blogLayout://博客修改
                showEdit(mBlog, "修改博客地址", "mBlog");
                break;
            case R.id.user_edit_GitHubLayout://修改github地址
                showEdit(mGithub, "修改博客地址", "mGithub");
                break;
            case R.id.user_edit_qqLayout://修改qq
                showEdit(mQq, "修改QQ", "mQQ");
                break;
            case R.id.user_edit_btn_out://退出登录:
                Constants.getmDataUtil(mContext).deleteUserLocal();
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GETPIC) {
                Bitmap photo = null;
                Uri photoUri = data.getData();
                if (photoUri != null) {
                    photo = BitmapFactory.decodeFile(photoUri.getPath());
                }
                if (photo == null) {
                    Bundle extra = data.getExtras();
                    if (extra != null) {
                        photo = (Bitmap) extra.get("data");
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    }
                }
                mHead.setImageBitmap(photo);
                File file = Environment.getExternalStorageDirectory();
                try {
                    headPath = file.getCanonicalFile().toString();
                    ImageTools.savePhotoToSDCard(photo, headPath, "head");
                    upLoadHead();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //上传头像
    public void upLoadHead() {
        final BmobFile bmobFile = new BmobFile(new File(headPath + "/head.png"));
        Log.i(TAG, "upLoadHead: " + headPath + "/head.png");
        showProgressDialog("请稍等", "正在上传头像", true);
        bmobFile.uploadblock(mContext, new UploadFileListener() {
            @Override
            public void onSuccess() {
                mProgressDialog.setMessage("头像上传成功,正在更新信息，请稍等");
                User myUser = new User();
                myUser.setmHead(bmobFile);
                myUser.update(mContext, user.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                        Log.i(TAG, "onSuccess: 更新成功");
                        ToastUtils.showToast(mContext, "头像更新成功");
                        user.setmHeadUrl(bmobFile.getFileUrl(mContext));
                        Constants.getmDataUtil(mContext).saveUserLocal(user);
                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                        new File(headPath + "head.png").delete();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                        showNoticeDialog("错误", s);
                        Log.i(TAG, "onFailure:更新失败 " + s);
                    }
                });
            }

            @Override
            public void onProgress(Integer value) {
                mProgressDialog.setProgress(value);
            }

            @Override
            public void onFailure(int code, String msg) {
                Log.i(TAG, "onFailure: 上传头像失败" + msg);
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                showNoticeDialog("错误", msg);
            }
        });
    }


    //显示图片选择
    public void showPicturePicker(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setNegativeButton("取消", null);
        builder.setItems(new String[]{"拍照", "相册"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Uri imageUri = null;
                        String fileName = null;
                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //删除上一次截图的临时文件
                        SharedPreferences sharedPreferences = activity.getSharedPreferences("temp", Context.MODE_WORLD_WRITEABLE);
                        ImageTools.deletePhotoAtPathAndName(Environment.getExternalStorageDirectory().getAbsolutePath(), sharedPreferences.getString("tempName", ""));

                        //保存本次截图临时文件名字
                        fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("tempName", fileName);
                        editor.commit();

                        imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), fileName));
                        //指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        activity.startActivityForResult(openCameraIntent, GETPIC);
                        break;

                    case 1:
                        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        activity.startActivityForResult(openAlbumIntent, GETPIC);
                        break;

                    default:
                        break;
                }
            }
        });
        builder.create().show();
    }
}
