package com.zyascend.amazingadapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * 功能：多功能Adapter（Footer，Header，多item布局）
 * 作者：zyascend on 2017/5/13 14:55
 * 邮箱：zyascend@qq.com
 */

public abstract class MultiAdapter<T> extends AmazingAdapter<T> {

    public static final int STATUS_LOADING = 10000;
    public static final int STATUS_ERROR = 20000;
    public static final int STATUS_END = 30000;

    private static final int TYPE_FOOTER = 11111;
    private static final int TYPE_HEADER = 22222;

    private boolean useHeader = false;
    private boolean useFooter = true;

    private LoadMoreListener mLoadMoreListener;

    private RelativeLayout contentView;
    private View mLoadingView;
    private View mErrorView;
    private View mEndView;

    private boolean isAutoLoadMore = false;



    public MultiAdapter(Context context) {
        this.mContext = context;
        mEndView = inflate(R.layout.default_endview);
        mLoadingView = inflate(R.layout.default_loadingview);
        mErrorView = inflate(R.layout.default_errorview);
    }

    private View inflate(int layoutId) {
        if (mContext == null || layoutId <= 0)
            throw new IllegalStateException("mContext or layoutId cant be null");
        return LayoutInflater.from(mContext).inflate(layoutId,null);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        switch (viewType){

            case TYPE_FOOTER:
                if (contentView == null){
                    contentView = new RelativeLayout(mContext);
                }
                addFooterView(mLoadingView);
                holder = new SimpleHolder(contentView);
                break;
            case TYPE_HEADER:
                if (contentView == null){
                    contentView = new RelativeLayout(mContext);
                }
                //addFooterView(mLoadingView);
                holder = new SimpleHolder(contentView);
                break;
            default:
                holder = createCommonHolder(parent);
                break;
        }
        return holder;
    }

    protected boolean isCommonItemView(int viewType) {
        return viewType != TYPE_FOOTER && viewType != TYPE_HEADER;
    }

    @Override
    protected void bindView(RecyclerView.ViewHolder holder, int position) {
        if (isCommonItemView(getItemViewType(position))){
            bindCommonView(holder,position,getItemViewType(position));
        }
    }

    protected abstract void bindCommonView(RecyclerView.ViewHolder holder, int position, int viewType);

    protected abstract RecyclerView.ViewHolder createCommonHolder(ViewGroup parent);

    @Override
    public int getItemViewType(int position) {
        if (isFooterView(position))return TYPE_FOOTER;
        else if (isHeaderView(position))return TYPE_HEADER;
        else return getViewType(position, dataList.get(position));
    }


    public int getViewType(int position, T t) {
        return 0;
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
        return dataList.size()+extraCount;
    }


    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (isFooterView(holder.getLayoutPosition())) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }
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
        startLoadMore(recyclerView, layoutManager);
    }

    private void startLoadMore(RecyclerView recyclerView, final RecyclerView.LayoutManager layoutManager) {
        if (mLoadMoreListener == null) {
            return;
        }
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!isAutoLoadMore && findLastVisibleItemPosition(layoutManager) + 1 == getItemCount()) {
                        loadMore();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = findLastVisibleItemPosition(layoutManager);
                if (isAutoLoadMore && lastVisibleItem + 1 == getItemCount()) {
                    loadMore();
                } else if (isAutoLoadMore) {
                    isAutoLoadMore = false;
                }
            }
        });
    }
    private void loadMore() {
        if (mLoadMoreListener != null)mLoadMoreListener.onLoadMore(false);
    }

    public void toggleStatus(int status) {
        switch (status){
            case STATUS_LOADING:
                addFooterView(mLoadingView);
                break;
            case STATUS_ERROR:
               addFooterView(mErrorView);
                break;
            case STATUS_END:
                addFooterView(mEndView);
                break;
        }
    }

    private void addFooterView(View view) {
        if (view == null)return;
        if (contentView == null){
            contentView = new RelativeLayout(mContext);
        }
        contentView.setPadding(0,15,0,15);
        contentView.removeAllViews();
        RelativeLayout.LayoutParams lp =  new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        contentView.addView(view,lp);
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

    public int getFooterPosition() {
        return getItemCount()-1;
    }

    public void setAutoLoadMore(boolean autoLoadMore) {
        isAutoLoadMore = autoLoadMore;
    }

    public void setLoadMoreListener(LoadMoreListener mLoadMoreListener) {
        this.mLoadMoreListener = mLoadMoreListener;
    }

    public void setLoadingView(View mLoadingView) {
        this.mLoadingView = mLoadingView;

    }

    public void setErrorView(View mErrorView) {
        this.mErrorView = mErrorView;
    }

    public void setEndView(View mEndView) {
        this.mEndView = mEndView;

    }

    public void setLoadingView(int mloaingViewId) {
        this.mLoadingView = inflate(mloaingViewId);

    }

    public void setErrorView(int mErrorViewId) {
        this.mErrorView = inflate(mErrorViewId);

    }

    public void setEndView(int mEndViewId) {
        this.mEndView = inflate(mEndViewId);
    }

}
