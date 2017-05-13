package com.zyascend.amazingadapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 功能：
 * 作者：zyascend on 2017/5/13 14:55
 * 邮箱：zyascend@qq.com
 */

public abstract class MultiAdapter extends AmazingAdapter {

    private static final int TYPE_COMMON = 0;
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_HEADER = 2;
    private int footerRes = -1;
    private int headerRes = -1;
    private boolean useHeader = false;
    private boolean useFooter = true;
    private boolean canLoadMore = true;
    private LoadMoreListener mLoadMoreListener;
    private RecyclerView.ViewHolder holder;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType){
            case TYPE_COMMON:
                holder = createCommonHolder(parent);
                break;
            case TYPE_FOOTER:
                View footer = LayoutInflater.from(parent.getContext())
                        .inflate(footerRes == -1 ? R.layout.default_footer:footerRes,parent,false);
                holder = new FooterHolder(footer);
                break;
            case TYPE_HEADER:
                View header = LayoutInflater.from(parent.getContext())
                        .inflate(headerRes == -1 ? R.layout.default_header:headerRes,parent,false);
                holder = new HeaderHolder(header);
                break;
        }
        return holder;
    }


    protected abstract RecyclerView.ViewHolder createCommonHolder(ViewGroup parent);

    @Override
    public int getItemViewType(int position) {
        if (isFooterView(position))return TYPE_FOOTER;
        else if (isHeaderView(position))return TYPE_HEADER;
        else return TYPE_COMMON;
    }

    private boolean isHeaderView(int position) {
        return useHeader && position == 0;
    }

    private boolean isFooterView(int position) {
        return useFooter && (position >= getItemCount() - 1);
    }

    @Override
    public int getItemCount() {
        int extraCount = 0;
        if (useFooter)extraCount+=1;
        if (useHeader)extraCount+=1;
        return super.getItemCount()+extraCount;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) layoutManager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (isFooterView(position)) {
                        return gridManager.getSpanCount();
                    }
                    return 1;
                }
            });
        }
        setLoadMore(recyclerView,layoutManager);
    }

    private void setLoadMore(RecyclerView recyclerView, final RecyclerView.LayoutManager layoutManager) {
        if (!canLoadMore || mLoadMoreListener == null)return;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && findLastVisibleItemPosition(layoutManager)
                        + 1 == getItemCount()){
                    startLoadMore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (findLastVisibleItemPosition(layoutManager) + 1 == getItemCount())
                    startLoadMore();
            }
        });
    }

    private void startLoadMore() {
        if (mLoadMoreListener != null)mLoadMoreListener.onLoadMore(false);
        //切换状态
        ((FooterHolder)holder).toggleStatus(FooterHolder.STATUS_LOADING);

    }

    private int findLastVisibleItemPosition(RecyclerView.LayoutManager layoutManager) {
        //layoutmanager是LinearLayoutManager的话，直接返回
        if (layoutManager instanceof LinearLayoutManager)
            return ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        else if(layoutManager instanceof StaggeredGridLayoutManager){
            int[] lastItems = ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(null);
            //在这些Items中找最大的
            int maxPositions = lastItems[0];
            for (int item : lastItems){
                maxPositions = maxPositions > item ? maxPositions : item;
            }
            return maxPositions;
        }
        return -1;
    }

    public static int findMax(int[] lastVisiblePositions) {
        int max = lastVisiblePositions[0];
        for (int value : lastVisiblePositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        if (isFooterView(holder.getLayoutPosition()) || isHeaderView(holder.getLayoutPosition())) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }
    }


}
