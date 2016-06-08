package com.brioal.pocketcode.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.brioal.pocketcode.R;
import com.brioal.pocketcode.activity.WebViewActivity;
import com.brioal.pocketcode.entiy.BannerModel;
import com.brioal.pocketcode.entiy.CommentModel;
import com.brioal.pocketcode.entiy.ContentModel;
import com.brioal.pocketcode.entiy.User;
import com.brioal.pocketcode.fragment.MainFragment;
import com.brioal.pocketcode.interfaces.OnLoaderMoreListener;
import com.brioal.pocketcode.util.NetWorkUtil;
import com.brioal.pocketcode.view.CircleImageView;
import com.brioal.pocketcode.view.bgabanner.BGABanner;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * 文章的适配器
 * Created by Brioal on 2016/5/12.
 */
public class ContentAdapter extends RecyclerView.Adapter {
    private int TYPE_BANNER = 0; //轮播
    private int TYPE_LOAD_MORE = 1;//加载更多
    private int TYPE_NO_MORE = 2;//没有更多
    private int TYPE_CONTENT = 3;//内容
    private String TAG = "ContextAdapter";

    private Context mContext;
    private List<ContentModel> mList;
    private List<BannerModel> mBannerList;
    private OnLoaderMoreListener loaderMoreListener;


    public void setLoaderMoreListener(OnLoaderMoreListener loaderMoreListener) {
        this.loaderMoreListener = loaderMoreListener;
    }

    public ContentAdapter(Context mContext, List<ContentModel> mList, List<BannerModel> mBannerList) {
        this.mContext = mContext;
        this.mList = mList;
        this.mBannerList = mBannerList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_BANNER) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_banner, parent, false);
            return new BannerViewHolder(itemView);
        } else if (viewType == TYPE_CONTENT) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_content, parent, false);
            return new ContentViewHolder(itemView);
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
        if (holder instanceof BannerViewHolder) {
            if (mBannerList.size() > 3) {
                List<View> views = new ArrayList<>();
                List<String> tips = new ArrayList<>();
                for (int i = 0; i < mBannerList.size(); i++) {
                    final BannerModel model = mBannerList.get(i);
                    ImageView imageView = new ImageView(mContext);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    Glide.with(mContext).load(model.getmImageUrl(mContext)).into(imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, WebViewActivity.class);
                            intent.putExtra("MessageId", model.getmContentId());
                            mContext.startActivity(intent);
                        }
                    });
                    views.add(imageView);
                    tips.add(model.getmTip());
                }

                ((BannerViewHolder) holder).mBanner.setViews(views);
                ((BannerViewHolder) holder).mBanner.setTips(tips);
                ((BannerViewHolder) holder).mBanner.setPageChangeDuration(2000);
            }

        } else if (holder instanceof ContentViewHolder) {
            final ContentModel model = mList.get(mBannerList == null ? position : position - 1);
            ((ContentViewHolder) holder).mTitle.setText(model.getmTitle());
            ((ContentViewHolder) holder).mDesc.setText(model.getmDesc());
            String mHeadUrl = model.getmHeadUrl();
            if (mHeadUrl == null) {
                BmobQuery<User> query = new BmobQuery<User>();
                query.addWhereEqualTo("objectId", model.getmHeadObject());
                query.findObjects(mContext, new FindListener<User>() {
                    @Override
                    public void onSuccess(List<User> object) {
                        Log.i(TAG, "onSuccess: 查询用户成功");
                        User user = object.get(0);
                        String mUrl = user.getmHeadUrl(mContext);
                        model.setmHeadUrl(mUrl);
                        Glide.with(mContext).load(mUrl).into(((ContentViewHolder) holder).mHead);
                    }

                    @Override
                    public void onError(int code, String msg) {
                        Log.i(TAG, "onError: 查询失败");
                    }
                });
            } else {
                Glide.with(mContext).load(mHeadUrl).into(((ContentViewHolder) holder).mHead);
            }

            ((ContentViewHolder) holder).mClassify.setText(model.getmClassify());
            ((ContentViewHolder) holder).mParise.setText(model.getmPraise() + "");
            ((ContentViewHolder) holder).mComment.setText(model.getmComment() + "");
            ((ContentViewHolder) holder).mRead.setText(model.getmRead() + "");
            ((ContentViewHolder) holder).mCollect.setText(model.getmCollect() + "");
            ((ContentViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, WebViewActivity.class);
                    intent.putExtra("Message", model);
                    mContext.startActivity(intent);
                }
            });
            if (model.getmComment() == -1) {
                BmobQuery<CommentModel> query = new BmobQuery<>();
                query.setLimit(1000);
                query.addWhereEqualTo("mMessageId", model.getObjectId());
                query.findObjects(mContext, new FindListener<CommentModel>() {
                    @Override
                    public void onSuccess(final List<CommentModel> list) {
                        Log.i(TAG, "onSuccess: 查询评论数量成功" + list.size());
                        ((ContentViewHolder) holder).mComment.post(new Runnable() {
                            @Override
                            public void run() {
                                ((ContentViewHolder) holder).mComment.setText(list.size() + "");

                            }
                        });
                        model.setmComment(list.size());
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
            } else {
                ((ContentViewHolder) holder).mComment.setText(model.getmComment() + "");
            }
        } else if (holder instanceof LoadMoreViewHolder) {
            if (NetWorkUtil.isNetworkConnected(mContext)) {
                loaderMoreListener.loadMore();
            }
        }

    }

    @Override
    public int getItemCount() {
        int count = mList.size();
        if (mBannerList != null) { //banner存在,则多一个
            count += 1;
        }
        if (mList.size() >= 7) {//还有数据,或者数据大小合适,应该显示底部信息
            count += 1;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (mBannerList != null && position == 0) {
            return TYPE_BANNER;
        } else if (position == getItemCount() - 1) {
            if (!NetWorkUtil.isNetworkConnected(mContext)) {
                return TYPE_NO_MORE; //没有更多布局
            } else {
                if (mList.size() % MainFragment.LOAD_LIMIT == 0) {
                    return TYPE_LOAD_MORE; //加载更多布局
                } else if (mList.size() >= 7) {
                    return TYPE_NO_MORE; //没有更多布局
                } else {
                    return TYPE_CONTENT; //内容布局
                }
            }
        }
        return TYPE_CONTENT;
    }

    //内容布局
    class ContentViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_content_title)
        TextView mTitle;
        @Bind(R.id.item_content_desc)
        TextView mDesc;
        @Bind(R.id.item_content_head)
        CircleImageView mHead;
        @Bind(R.id.item_content_classify)
        TextView mClassify;
        @Bind(R.id.item_content_parise)
        CheckBox mParise;
        @Bind(R.id.item_content_msg)
        TextView mComment;
        @Bind(R.id.item_content_read)
        TextView mRead;
        @Bind(R.id.item_content_collect)
        CheckBox mCollect;
        View itemView;

        public ContentViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }

    //轮播布局
    class BannerViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.main_banner)
        BGABanner mBanner;

        public BannerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

    //加载更多布局
    private class LoadMoreViewHolder extends RecyclerView.ViewHolder {

        public LoadMoreViewHolder(View itemView) {
            super(itemView);
        }
    }

    //没有更多布局
    private class NoMoreViewHolder extends RecyclerView.ViewHolder {

        public NoMoreViewHolder(View itemView) {
            super(itemView);
        }
    }
}
