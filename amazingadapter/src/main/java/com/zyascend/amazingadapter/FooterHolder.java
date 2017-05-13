package com.zyascend.amazingadapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 功能：
 * 作者：zyascend on 2017/5/13 15:05
 * 邮箱：zyascend@qq.com
 */

public class FooterHolder extends RecyclerView.ViewHolder {
    public static final int STATUS_LOADING = 0;
    public static final int STATUS_ERROR = 0;
    public static final int STATUS_END = 0;

    public FooterHolder(View itemView) {
        super(itemView);
    }

    public void toggleStatus(int status){

    }
}
