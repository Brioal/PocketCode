package com.brioal.pocketcode.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.entiy.CommentModel;
import com.brioal.pocketcode.entiy.User;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;

/**回复item的适配器
 * Created by Brioal on 2016/6/6.
 */
public class ReplyAdapter extends BaseAdapter {
    private Context mContext;
    private List<CommentModel> mList;
    private String TAG = "ReplayAdapterInfo";

    public ReplyAdapter(Context mContext, List<CommentModel> mList) {
        this.mContext = mContext;
        this.mList = mList;
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
        final CommentModel model = mList.get(position);
        ReplyViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_reply, parent, false);
            holder = new ReplyViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ReplyViewHolder) convertView.getTag();
        }
        BmobQuery<User> query = new BmobQuery<>();
        final ReplyViewHolder finalHolder = holder;
        query.getObject(mContext, model.getmUserId(), new GetListener<User>() {
            @Override
            public void onSuccess(final User user) {
                Log.i(TAG, "onSuccess: 回复用户信息查找成功"+ user.getUsername() + ":" + model.getmContent());
                finalHolder.mContent.post(new Runnable() {
                    @Override
                    public void run() {
                        finalHolder.mContent.setText(user.getUsername() + ":" + model.getmContent());
                    }
                });

            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
        return convertView;
    }

    static class ReplyViewHolder {
        @Bind(R.id.item_reply_content)
        TextView mContent;

        ReplyViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}