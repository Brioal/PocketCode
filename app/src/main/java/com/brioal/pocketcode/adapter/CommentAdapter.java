package com.brioal.pocketcode.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.entiy.CommentModel;
import com.brioal.pocketcode.entiy.User;
import com.brioal.pocketcode.interfaces.onCommentItemClickListener;
import com.brioal.pocketcode.view.CircleImageView;
import com.brioal.pocketcode.view.MyGridView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

/**评论的适配器
 * Created by Brioal on 2016/6/3.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context mContext;
    private List<CommentModel> mList;
    private final String TAG = "CommentInfo";
    private onCommentItemClickListener listener;

    public CommentAdapter(Context mContext, List<CommentModel> list) {
        this.mContext = mContext;
        mList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getmParent() == null) {
                mList.add(list.get(i));
            }
        }
    }



    public void setListener(onCommentItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_comment_group, parent, false);
        return new CommentViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final CommentViewHolder holder, int position) {
        final CommentModel model = mList.get(position);
        BmobQuery<User> query = new BmobQuery<>();
        query.getObject(mContext, model.getmUserId(), new GetListener<User>() {
            @Override
            public void onSuccess(User user) {
                Log.i(TAG, "onSuccess: 用户信息查找成功");
                Glide.with(mContext).load(user.getmHeadUrl(mContext)).into(holder.mHead);
                holder.mName.setText(user.getUsername());
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
        holder.mContent.setText(model.getmContent());
        holder.mTime.setText(model.getCreatedAt());
        BmobQuery<CommentModel> replys = new BmobQuery<>();
        replys.addWhereEqualTo("mParent", model.getObjectId());
        replys.findObjects(mContext, new FindListener<CommentModel>() {
            @Override
            public void onSuccess(final List<CommentModel> list) {
                Log.i(TAG, "onSuccess: 查找评论成功" + list.size());
                holder.mGrdiView.setAdapter(new ReplyAdapter(mContext, list));
                holder.mGrdiView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (listener != null) {
                            listener.onClickItem(list.get(position).getObjectId(),list.get(position).getmParent(),list.get(position).getmUserId(),list.get(position).getmContent());
                        }
                    }
                });
            }

            @Override
            public void onError(int i, String s) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickItem(model.getObjectId(),model.getmParent(),model.getmUserId(), model.getmContent());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    //评论的布局
    class CommentViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_comment_head)
        CircleImageView mHead;
        @Bind(R.id.item_comment_name)
        TextView mName;
        @Bind(R.id.item_comment_time)
        TextView mTime;
        @Bind(R.id.item_comment_content)
        TextView mContent;
        @Bind(R.id.item_comment_replayGrdiView)
        MyGridView mGrdiView;

        View itemView;

        public CommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
        }
    }
}
