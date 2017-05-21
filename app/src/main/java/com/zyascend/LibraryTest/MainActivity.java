package com.zyascend.LibraryTest;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.zyascend.amazingadapter.ItemClickListener;
import com.zyascend.amazingadapter.LoadMoreListener;
import com.zyascend.amazingadapter.MultiAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemClickListener, LoadMoreListener {

    private static final String TAG = "TAG";
    private RecyclerView recyclerView;
    private MyMultiAdapter adapter;
    private boolean isFailed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_recycler);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyMultiAdapter(this);
        adapter.setLoadMoreListener(this);
        adapter.setItemClickListener(this);
        recyclerView.setAdapter(adapter);

        //延时3s刷新列表
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<String> data = new ArrayList<>();
                for (int i = 0; i < 12; i++) {
                    data.add("item--" + i);
                }
                //刷新数据
                adapter.addDatas(data,false);
            }
        }, 3000);
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "点击了"+(position+1), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadMore(boolean isReload) {
        Log.e(TAG, "onLoadMore: called");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (adapter.getItemCount() > 15 && isFailed) {
                    isFailed = false;
                    //加载失败，更新footer view提示
                    adapter.toggleStatus(MultiAdapter.STATUS_ERROR);
                } else if (adapter.getItemCount() > 17) {
                    //加载完成，更新footer view提示
                    adapter.toggleStatus(MultiAdapter.STATUS_END);
                } else {
                    final List<String> data = new ArrayList<>();
                    for (int i = 0; i < 12; i++) {
                        data.add("item--" + (adapter.getItemCount() + i - 1));
                    }
                    //刷新数据
                    adapter.addDatas(data,false);
                }
            }
        },2000);

    }
}
