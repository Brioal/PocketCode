package com.brioal.pocketcode.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.brioal.pocketcode.entiy.ClassifyModel;

import java.util.List;

/**
 * Created by Brioal on 2016/5/12.
 */
public class ClassifyAdapter extends RecyclerView.Adapter<ClassifyAdapter.ClassifyViewHolder> {
    private Context mContext ;
    private List<ClassifyModel> mList ;

    public ClassifyAdapter(Context mContext, List<ClassifyModel> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public ClassifyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ClassifyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ClassifyViewHolder extends RecyclerView.ViewHolder {

        public ClassifyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
