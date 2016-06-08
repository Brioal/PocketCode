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
import com.brioal.pocketcode.activity.WebViewActivity;
import com.brioal.pocketcode.entiy.CollectEnity;
import com.brioal.pocketcode.entiy.ContentModel;
import com.brioal.pocketcode.entiy.User;
import com.brioal.pocketcode.view.CircleImageView;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

/**
 * 收藏列表的适配器
 * Created by Brioal on 2016/5/31.
 */

public class CollectAdapter extends RecyclerView.Adapter<CollectAdapter.CollectViewHolder> {

    private Context mContext;
    private List<CollectEnity> mList;
    private String TAG = "CollectAdapterInfo";

    public CollectAdapter(Context mContext, List<CollectEnity> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public CollectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_content, parent, false);
        return new CollectViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CollectViewHolder holder, int position) {
        CollectEnity enity = mList.get(position);
        BmobQuery<ContentModel> messageQuery = new BmobQuery<>();
        messageQuery.getObject(mContext, enity.getmMessageId(), new GetListener<ContentModel>() {
            @Override
            public void onSuccess(final ContentModel contentModel) {
                Log.i(TAG, "onSuccess:加载文章成功 ");
                holder.mTitle.setText(contentModel.getmTitle());
                holder.mDesc.setText(contentModel.getmDesc());
                BmobQuery<User> query = new BmobQuery<User>();
                query.addWhereEqualTo("objectId", contentModel.getmHeadObject());
                query.findObjects(mContext, new FindListener<User>() {
                    @Override
                    public void onSuccess(List<User> object) {
                        Log.i(TAG, "onSuccess: 查询用户成功");
                        User user = object.get(0);
                        String mUrl = user.getmHeadUrl(mContext);
                        Glide.with(mContext).load(mUrl).into((holder).mHead);
                        contentModel.setmHeadUrl(mUrl);
                    }

                    @Override
                    public void onError(int code, String msg) {
                        Log.i(TAG, "onError: 查询失败");
                    }
                });
                holder.mClassify.setText(contentModel.getmClassify());
                holder.mPraise.setText(contentModel.getmPraise() + "");
                holder.mMsg.setText(contentModel.getmComment() + "");
                holder.mRead.setText(contentModel.getmRead() + "");
                holder.mCollect.setText(contentModel.getmCollect() + "");
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, WebViewActivity.class);
                        intent.putExtra("MessageId", contentModel.getObjectId());
                        mContext.startActivity(intent);
                    }
                });
            }


            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class CollectViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_content_title)
        TextView mTitle;
        @Bind(R.id.item_content_desc)
        TextView mDesc;
        @Bind(R.id.item_content_head)
        CircleImageView mHead;
        @Bind(R.id.item_content_classify)
        TextView mClassify;
        @Bind(R.id.item_content_parise)
        TextView mPraise;
        @Bind(R.id.item_content_msg)
        TextView mMsg;
        @Bind(R.id.item_content_collect)
        TextView mCollect;
        @Bind(R.id.item_content_read)
        TextView mRead;
        View itemView;

        public CollectViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
        }
    }
}
