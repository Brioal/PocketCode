package com.brioal.pocketcode.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.view.Tag;

import java.util.List;

/**每个item中的tag适配器
 * Created by Brioal on 2016/6/3.
 */

public class TagAdapter extends BaseAdapter {
    private List<String> mList;
    private Context mContext;

    public TagAdapter(Context mContext, List<String> mList) {
        this.mList = mList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TagViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_tag, parent, false);
            holder = new TagViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (TagViewHolder) convertView.getTag();
        }
        holder.mTag.setText(mList.get(position));
        return convertView;
    }

    class TagViewHolder {
        private Tag mTag;

        public TagViewHolder(View convertView) {
            mTag = (Tag) convertView.findViewById(R.id.item_tag);
        }
    }
}
