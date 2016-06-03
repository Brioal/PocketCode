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
import android.support.v7.app.AppCompatActivity;
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
import com.brioal.pocketcode.entiy.MyUser;
import com.brioal.pocketcode.interfaces.ActivityInterFace;
import com.brioal.pocketcode.util.BrioalConstan;
import com.brioal.pocketcode.util.ImageTools;
import com.brioal.pocketcode.util.NetWorkUtil;
import com.brioal.pocketcode.util.StatusBarUtils;
import com.brioal.pocketcode.util.ThemeUtil;
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

public class UserEditActivity extends AppCompatActivity implements View.OnClickListener, ActivityInterFace {


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

    private MyUser user;
    private Context mContext;
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
    private int CROP_PICTURE = 3;
    private boolean headHasChange = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);
        ButterKnife.bind(this);
        mContext = this;
        initData();
        initView();
        initActions();
    }

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

    public void showEdit(final TextView mTv, String title) {
        View edit_layout;
        builder = new AlertDialog.Builder(mContext);
        edit_layout = LayoutInflater.from(mContext).inflate(R.layout.dialog_edit, null, false);
        builder.setView(edit_layout);
        TextView mTitle = (TextView) edit_layout.findViewById(R.id.dialog_title);
        final EditText mEt = (EditText) edit_layout.findViewById(R.id.dialog_content);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTv.setText(mEt.getText().toString());
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

    public void initView() {
        HeadUrl = user.getmHeadUrl(mContext);
        Name = user.getUsername();
        Desc = user.getmDesc();
        Interst = user.getmFavorite();
        Blog = user.getmBlog();
        Github = user.getmGitHub();
        QQ = user.getmQQ();

        if (HeadUrl != null) {
            Glide.with(mContext).load(HeadUrl).into(mHead);
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

    }

    @Override
    public void setView() {

    }

    @Override
    public void initData() {
        user = BrioalConstan.getmLocalUser(mContext).getUser();
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
                if (hasChanged()) { //改变
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("提示").setMessage("内容已发生改变,是否保存").setPositiveButton("保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveData();
                        }
                    }).setNegativeButton("不保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setResult(10);
                            finish();
                        }
                    });
                    builder.create().show();
                } else { //未发生改变
                    setResult(10);
                    this.finish();
                }
                break;
            case R.id.action_save:
                if (hasChanged()) {
                    saveData();
                } else {
                    setResult(10);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //保存用户数据
    private void saveData() {
        if (NetWorkUtil.isNetworkConnected(mContext)) {
            final ProgressDialog dialog = new ProgressDialog(mContext);
            dialog.setCancelable(false);
            dialog.setTitle("请稍等");
            dialog.setMessage("正在保存个人信息，请稍等");
            dialog.show();
            if (!mName.getText().toString().equals(Name)) {
                user.setUsername(mName.getText().toString());
            }
            user.setmDesc(mDesc.getText().toString());
            user.setmBlog(mBlog.getText().toString());
            user.setmGitHub(mGithub.getText().toString());
            user.setmFavorite(mFavorite.getText().toString());
            user.setmQQ(mQq.getText().toString());
            // TODO: 2016/5/18 数据更新
            user.update(mContext, user.getObjectId(), new UpdateListener() {
                @Override
                public void onSuccess() {
                    dialog.dismiss();
                    Log.i(TAG, "onSuccess: 更新成功");
                    BrioalConstan.getmLocalUser(mContext).save(user);
                    finish();
                }

                @Override
                public void onFailure(int i, String s) {
                    dialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("出错了").setMessage(s).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
                    Log.i(TAG, "onFailure:更新失败 " + s);
                }
            });
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("错误").setMessage("网络不可用，请灯网络可用时再试");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        }


    }

    public boolean hasChanged() {
        if (headHasChange) {
            return true;
        }
        if (!mName.getText().toString().equals(Name)) {
            return true;
        }
        if (!mDesc.getText().toString().equals(Desc)) {
            return true;
        }
        if (!mFavorite.getText().toString().equals(Interst)) {
            return true;
        }
        if (!mBlog.getText().toString().equals(Blog)) {
            return true;
        }
        if (!mGithub.getText().toString().equals(Github)) {
            return true;
        }
        if (!mQq.getText().toString().equals(QQ)) {
            return true;
        }

        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        upDateUser();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        upDateUser();
    }

    public void upDateUser() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_edit_headLayout: //头像更改
                showPicturePicker(this);
                break;
            case R.id.user_edit_nameLayout: //昵称更改:
                showEdit(mName, "修改昵称");
                break;
            case R.id.user_edit_descLayout: //简介更改:
                showEdit(mDesc, "修改简介");
                break;
            case R.id.user_edit_favoriteLayout: //兴趣更改:
                showEdit(mFavorite, "修改擅长领域");
                break;
            case R.id.user_edit_blogLayout://博客修改
                showEdit(mBlog, "修改博客地址");
                break;
            case R.id.user_edit_GitHubLayout://修改github地址
                showEdit(mGithub, "修改博客地址");
                break;
            case R.id.user_edit_qqLayout://修改qq
                showEdit(mQq, "修改QQ");
                break;
            case R.id.user_edit_btn_out://退出登录:
                BrioalConstan.getmLocalUser(mContext).delete();
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
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setCancelable(false);
        dialog.setTitle("请稍等");
        dialog.setMessage("正在上传头像，请稍等");
        dialog.show();
        bmobFile.uploadblock(mContext, new UploadFileListener() {
            @Override
            public void onSuccess() {
                dialog.setMessage("头像上传成功,正在更新信息，请稍等");
                user.setmHead(bmobFile);
                user.update(mContext, user.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        Log.i(TAG, "onSuccess: 更新成功");
                        user.setmHeadUrl(bmobFile.getFileUrl(mContext));
                        dialog.dismiss();
                        new File(headPath + "head.png").delete();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("出错了").setMessage(s).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                        Log.i(TAG, "onFailure:更新失败 " + s);
                    }
                });
            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
            }

            @Override
            public void onFailure(int code, String msg) {
                Log.i(TAG, "onFailure: 上传头像失败" + msg);
            }
        });
    }

    //显示图片选择
    public void showPicturePicker(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("图片来源");
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
