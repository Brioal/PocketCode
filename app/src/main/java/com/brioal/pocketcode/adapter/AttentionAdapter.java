package com.brioal.pocketcode.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.activity.AttentionActivity;
import com.brioal.pocketcode.activity.UserInfoActivity;
import com.brioal.pocketcode.entiy.AttentionEnity;
import com.brioal.pocketcode.entiy.User;
import com.brioal.pocketcode.fragment.MainFragment;
import com.brioal.pocketcode.interfaces.OnLoaderMoreListener;
import com.brioal.pocketcode.view.CircleImageView;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;

/**
 * 我的关注的适配器
 * Created by Brioal on 2016/5/31.
 */

public class AttentionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int TYPE_LOAD_MORE = 1;//加载更多
    private int TYPE_NO_MORE = 2;//没有更多
    private int TYPE_CONTENT = 3;//内容

    private OnLoaderMoreListener loaderMoreListener;
    public static final int TYPE_ATTENTION = 0;
    public static final int TYPE_FANS = 1;
    private int mType;
    private Context mContext;
    private List<AttentionEnity> mList;
    private String TAG = "AttentionAdapterInfo";

    public AttentionAdapter(Context mContext, List<AttentionEnity> mList, int mType) {
        this.mContext = mContext;
        this.mList = mList;
        this.mType = mType;
    }

    public void setLoaderMoreListener(OnLoaderMoreListener loaderMoreListener) {
        this.loaderMoreListener = loaderMoreListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_CONTENT) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_attention, parent, false);
            return new AttentionViewHolder(itemView);
        } else if (viewType == TYPE_LOAD_MORE) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_loadmore, parent, false);
            return new LoadMoreViewHolder(itemView);
        } else if (viewType == TYPE_NO_MORE) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_nomore, parent, false);
            return new NoMoreViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AttentionViewHolder) {
            final AttentionEnity enity = mList.get(position);
            String id = null;
            if (mType == TYPE_ATTENTION) { //关注别人,显示被关注人的
                id = enity.getmAuthorId();
                final String finalId = id;
                ((AttentionViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, UserInfoActivity.class);
                        intent.putExtra("UserId", finalId);
                        if (mType == TYPE_ATTENTION) { //我关注别人
                            intent.putExtra("IsAttention", true);
                            intent.putExtra("AttentionId", enity.getObjectId());
                        } else if (mType == TYPE_FANS) { //我的粉丝
                            intent.putExtra("IsAttention", true);
                            intent.putExtra("AttentionId", enity.getObjectId());
                        }
                        ((AttentionActivity) mContext).startActivityForResult(intent, 0);
                    }
                });
            } else if (mType == TYPE_FANS) { //粉丝,显示关注人
                id = enity.getmUserId();
            }
            if (enity.getmName() == null || enity.getmDesc() == null || enity.getmUserHeadUrl() == null) {
                BmobQuery<User> query = new BmobQuery<>();
                query.getObject(mContext, id, new GetListener<User>() {
                    @Override
                    public void onSuccess(User bmobUser) {
                        Log.i(TAG, "onSuccess: 用户查询成功");
                        enity.setmName(bmobUser.getUsername());
                        enity.setmDesc(bmobUser.getmDesc());
                        enity.setmUserHeadUrl(bmobUser.getmHeadUrl(mContext));
                        ((AttentionViewHolder) holder).mName.setText(enity.getmName());
                        ((AttentionViewHolder) holder).mDesc.setText(enity.getmDesc());
                        Glide.with(mContext).load(enity.getmUserHeadUrl()).into(((AttentionViewHolder) holder).mHead);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Log.i(TAG, "onFailure: 用户查询失败");
                    }
                });
            } else {
                ((AttentionViewHolder) holder).mName.setText(enity.getmName());
                ((AttentionViewHolder) holder).mDesc.setText(enity.getmDesc());
                Glide.with(mContext).load(enity.getmUserHeadUrl()).into(((AttentionViewHolder) holder).mHead);
            }


        } else if (holder instanceof LoadMoreViewHolder) {
            loaderMoreListener.loadMore();
        }

    }

    @Override
    public int getItemCount() {
        int count = mList.size();
        if (mList.size() >= 7) {//还有数据,或者数据大小合适,应该显示底部信息
            count += 1;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            if (mList.size() % MainFragment.LOAD_LIMIT == 0) {
                return TYPE_LOAD_MORE; //加载更多布局
            } else if (mList.size() >= 7) {
                return TYPE_NO_MORE; //没有更多布局
            } else {
                return TYPE_CONTENT; //内容布局
            }
        }
        return TYPE_CONTENT;
    }

    class AttentionViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_attention_head)
        CircleImageView mHead;
        @Bind(R.id.item_attention_name)
        TextView mName;
        @Bind(R.id.item_attention_desc)
        TextView mDesc;

        View itemView;

        public AttentionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
        }
    }

    //加载更多布局
    class LoadMoreViewHolder extends RecyclerView.ViewHolder {

        public LoadMoreViewHolder(View itemView) {
            super(itemView);
        }
    }

    //没有更多布局
    class NoMoreViewHolder extends RecyclerView.ViewHolder {

        public NoMoreViewHolder(View itemView) {
            super(itemView);
        }
    }
}
