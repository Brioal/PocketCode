package com.brioal.pocketcode.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.activity.WebViewActivity;
import com.brioal.pocketcode.entiy.ContentModel;
import com.brioal.pocketcode.util.DateFormat;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Brioal on 2016/5/18.
 */
public class ShareListAdapter extends RecyclerView.Adapter<ShareListAdapter.ShareListViewHolder> {

    private Context mContext;
    private List<ContentModel> mList;


    public ShareListAdapter(Context mContext, List<ContentModel> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public ShareListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_share_list, parent, false);
        return new ShareListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ShareListViewHolder holder, int position) {
        final ContentModel model = mList.get(position);
        holder.mShareTitle.setText(model.getmTitle());
        holder.mShareDesc.setText(model.getmDesc());
        holder.mShareClassify.setText(model.getmClassify());
        DateFormat.setDate(model.getmTime(), holder.mShareTime);
        holder.mShareParise.setText(model.getmPraise() + "");
        holder.mShareRead.setText(model.getmRead()+"");
        holder.mShareMsg.setText(model.getmComment()+"");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra("Url", model.getmUrl());
                intent.putExtra("Title", model.getmTitle());
                intent.putExtra("Id", model.getObjectId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    class ShareListViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_share_title)
        TextView mShareTitle;
        @Bind(R.id.item_share_desc)
        TextView mShareDesc;
        @Bind(R.id.item_share_classify)
        TextView mShareClassify;
        @Bind(R.id.item_share_time)
        TextView mShareTime;
        @Bind(R.id.item_share_parise)
        TextView mShareParise;
        @Bind(R.id.item_share_msg)
        TextView mShareMsg;
        @Bind(R.id.item_share_read)
        TextView mShareRead;

        View itemView;

        public ShareListViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }


}
