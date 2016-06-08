package com.brioal.pocketcode.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.util.Constants;
import com.brioal.pocketcode.util.ToastUtils;
import com.brioal.pocketcode.view.CircleHead;
import com.brioal.pocketcode.view.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brioal on 2016/6/3.
 */

public class TagChooseAdapter extends RecyclerView.Adapter {
    private List<String> mGroups;
    private List<List<String>> mChilds;
    private Context mContext;
    private final int VIEW_TYPE_HEAD = 0;
    private final int VIEW_TYPE_CHILD = 1;
    private ArrayList<String> mSelectTags;


    public TagChooseAdapter(Context mContext) {
        this.mContext = mContext;
        mGroups = Constants.getmDataUtil(mContext).getGroups();
        mChilds = Constants.getmDataUtil(mContext).getChilds();
        if (mSelectTags == null) {
            mSelectTags = new ArrayList<>();
        } else {
            mSelectTags.clear();
        }
    }

    public ArrayList<String> getmSelectTags() {
        return mSelectTags;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_HEAD:
                View groupView = LayoutInflater.from(mContext).inflate(R.layout.item_tab_group, parent, false);
                return new GroupViewHodler(groupView);
            case VIEW_TYPE_CHILD:
                View childView = LayoutInflater.from(mContext).inflate(R.layout.item_tab_child, parent, false);
                return new ChildViewHolder(childView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GroupViewHodler) {
            String title = mGroups.get(position / 2);
            ((GroupViewHodler) holder).mHead.setmText(title.toCharArray()[0]);
            ((GroupViewHodler) holder).mTitle.setText(title);
        } else if (holder instanceof ChildViewHolder) {
            final List<String> tags = mChilds.get(position / 2);
            ((ChildViewHolder) holder).mGridView.setAdapter(new TagAdapter(mContext, tags));
            ((ChildViewHolder) holder).mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String text = tags.get(position);
                    if (mSelectTags.size() == 3) {
                        ToastUtils.showToast(mContext, "最多只能选择三个标签");
                    } else {
                        if (!mSelectTags.contains(text)) { //不包含
                            Tag tag = (Tag) view.findViewById(R.id.item_tag);
                            tag.setChecked(true);
                            mSelectTags.add(text);
                        } else {
                            Tag tag = (Tag) view.findViewById(R.id.item_tag);
                            tag.setChecked(false);
                            mSelectTags.remove(text);
                        }
                    }

                }
            });


        }
    }

    @Override
    public int getItemCount() {
        return mGroups.size() * 2;
    }

    @Override
    public int getItemViewType(int position) {

        return position % 2 == 0 ? VIEW_TYPE_HEAD : VIEW_TYPE_CHILD;
    }

    class GroupViewHodler extends RecyclerView.ViewHolder {
        private CircleHead mHead;
        private TextView mTitle;
        private View itemView;

        public GroupViewHodler(View itemView) {
            super(itemView);
            mHead = (CircleHead) itemView.findViewById(R.id.item_tab_group_head);
            mTitle = (TextView) itemView.findViewById(R.id.item_tab_group_title);
        }
    }

    class ChildViewHolder extends RecyclerView.ViewHolder {
        private GridView mGridView;
        private View itemView;

        public ChildViewHolder(View itemView) {
            super(itemView);
            mGridView = (GridView) itemView.findViewById(R.id.item_tab_child_gridView);
        }
    }
}
