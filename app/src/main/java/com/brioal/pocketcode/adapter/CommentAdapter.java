package com.brioal.pocketcode.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.entiy.CommentModel;
import com.brioal.pocketcode.entiy.MyUser;
import com.brioal.pocketcode.view.CircleImageView;
import com.bumptech.glide.Glide;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;

/**
 * Created by Brioal on 2016/6/3.
 */

public class CommentAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private List<CommentModel> mGroup;
    private List<List<CommentModel>> mChilds;
    private final String TAG = "CommentInfo";

    public CommentAdapter(Context mContext, List<CommentModel> mGroup, List<List<CommentModel>> mChilds) {
        this.mContext = mContext;
        this.mGroup = mGroup;
        this.mChilds = mChilds;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getGroupCount() {
        return mGroup.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChilds.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroup.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mChilds.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_comment_group, parent, false);
            holder = new GroupViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }
        CommentModel enity = mGroup.get(groupPosition);
        holder.mTime.setText(enity.getCreatedAt());
        holder.mContent.setText(enity.getmContent());
        BmobQuery<MyUser> query = new BmobQuery<>();
        final GroupViewHolder finalHolder = holder;
        query.getObject(mContext, enity.getmUserId(), new GetListener<MyUser>() {
            @Override
            public void onSuccess(MyUser myUser) {
                Log.i(TAG, "onSuccess: 查询用户信息成功");
                Glide.with(mContext).load(myUser.getmHeadUrl(mContext)).into(finalHolder.mHead);
                finalHolder.mName.setText(myUser.getUsername());
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_reply, parent, false);
            holder = new ChildViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        final CommentModel enity = mChilds.get(groupPosition).get(childPosition);
        BmobQuery<MyUser> query = new BmobQuery<>();
        final ChildViewHolder finalHolder = holder;
        query.getObject(mContext, enity.getmUserId(), new GetListener<MyUser>() {
            @Override
            public void onSuccess(MyUser myUser) {
                Log.i(TAG, "onSuccess: 加载用户数据成功");
                String name = myUser.getUsername();
                finalHolder.mContent.setText(name + ":" + enity.getmContent());
            }

            @Override
            public void onFailure(int i, String s) {
                Log.i(TAG, "onFailure: 加载用户数据失败");
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {

    }

    @Override
    public void onGroupCollapsed(int groupPosition) {

    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        return 0;
    }


    //评论的布局
    class GroupViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView mHead;
        private TextView mName;
        private TextView mTime;
        private TextView mContent;

        public GroupViewHolder(View itemView) {
            super(itemView);
            mHead = (CircleImageView) itemView.findViewById(R.id.item_comment_head);
            mName = (TextView) itemView.findViewById(R.id.item_comment_name);
            mTime = (TextView) itemView.findViewById(R.id.item_comment_time);
            mContent = (TextView) itemView.findViewById(R.id.item_comment_content);
        }
    }

    //回复的布局
    class ChildViewHolder extends RecyclerView.ViewHolder {
        private TextView mContent;

        public ChildViewHolder(View itemView) {
            super(itemView);
            mContent = (TextView) itemView.findViewById(R.id.item_reply_content);
        }
    }
}
