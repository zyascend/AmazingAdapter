package com.zyascend.LibraryTest;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.zyascend.amazingadapter.ItemClickListener;
import com.zyascend.amazingadapter.LoadMoreListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemClickListener, LoadMoreListener {

    private RecyclerView recyclerView;
    private MyMultiAdapter adapter;

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

        loadData();
    }

    private void loadData() {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            data.add("DATA......");
        }
        adapter.addDatas(data,false);
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "点击了"+(position+1), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadMore(boolean isReload) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        },2000);

    }
}
