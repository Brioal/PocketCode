package com.brioal.pocketcode.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.entiy.AttentionEnity;
import com.brioal.pocketcode.entiy.MyUser;
import com.brioal.pocketcode.view.CircleImageView;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;

import static android.R.attr.id;

/**
 * Created by Brioal on 2016/5/31.
 */

public class AttentionAdapter extends RecyclerView.Adapter<AttentionAdapter.AttentionViewHolder> {

    public static final int TYPE_ATTENTION = 0;
    public static final int TYPE_FANS= 1;
    private int mType ;
    private Context mContext;
    private List<AttentionEnity> mList;
    private String TAG = "AttentionAdapterInfo";

    public AttentionAdapter(Context mContext, List<AttentionEnity> mList,int mType) {
        this.mContext = mContext;
        this.mList = mList;
        this.mType = mType;
    }

    @Override
    public AttentionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_attention, parent, false);
        return new AttentionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AttentionViewHolder holder, int position) {
        AttentionEnity enity = mList.get(position);
        String id  =null;
        if (mType == TYPE_ATTENTION) { //关注别人,显示被关注人的
            id = enity.getmAuthorId();
        } else if (mType == TYPE_FANS) { //粉丝,显示关注人
            id = enity.getmUserId();
        }
        BmobQuery<MyUser> query = new BmobQuery<>();
        query.getObject(mContext, id, new GetListener<MyUser>() {
            @Override
            public void onSuccess(MyUser bmobUser) {
                Log.i(TAG, "onSuccess: 用户查询成功");
                MyUser user = (MyUser) bmobUser;
                holder.mName.setText(bmobUser.getUsername());
                holder.mDesc.setText(bmobUser.getmDesc());
                String url = bmobUser.getmHeadUrl(mContext);
                Glide.with(mContext).load(url).into(holder.mHead);
            }

            @Override
            public void onFailure(int i, String s) {
                Log.i(TAG, "onFailure: 用户查询失败");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
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
}
