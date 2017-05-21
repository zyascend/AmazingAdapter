package com.zyascend.amazingadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能: 单布局Adapter
 * 作者：zyascend on 2017/5/13 13:28
 * 邮箱：zyascend@qq.com
 */

public abstract class AmazingAdapter<T> extends RecyclerView.Adapter{
    protected static final String TAG = "AmazingAdapter";
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;

    }

    public ItemClickListener itemClickListener;
    protected List<T> dataList = new ArrayList<>();
    public Context mContext;

    public void addDatas(List<T> newDatas,boolean clear){
        if (newDatas == null || newDatas.isEmpty())return;
        if (clear){
            dataList.clear();
            dataList.addAll(newDatas);
            notifyDataSetChanged();
        }else{
            int startPosition = dataList.size();
            dataList.addAll(newDatas);
            notifyItemRangeInserted(startPosition,newDatas.size());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindView(holder,position);
    }

    protected abstract void bindView(RecyclerView.ViewHolder holder, int position);

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }
}
