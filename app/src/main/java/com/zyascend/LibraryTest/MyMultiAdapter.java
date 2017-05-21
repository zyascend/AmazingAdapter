package com.zyascend.LibraryTest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zyascend.amazingadapter.MultiAdapter;

/**
 * 功能：
 * 作者：zyascend on 2017/5/14 11:41
 * 邮箱：zyascend@qq.com
 */

public class MyMultiAdapter extends MultiAdapter<String> {

    public MyMultiAdapter(Context context) {
        super(context);
    }

    @Override
    protected void bindCommonView(RecyclerView.ViewHolder holder, int position, int viewType) {
        CommonHolder commonHolder = (CommonHolder) holder;
        commonHolder.textView.setText(dataList.get(position));
    }

    @Override
    protected RecyclerView.ViewHolder createCommonHolder(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.my_item,parent,false);
        return new CommonHolder(view);
    }

    @Override
    protected int getViewType(int position, String s) {
        return 0;
    }

    private class CommonHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        public CommonHolder(View view) {
            super(view);
            bind(view);
        }

        private void bind(View item) {
            textView = (TextView) item.findViewById(R.id.text);
        }

    }
}
