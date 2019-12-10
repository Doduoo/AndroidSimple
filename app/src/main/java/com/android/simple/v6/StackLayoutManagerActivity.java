package com.android.simple.v6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.simple.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

public class StackLayoutManagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stack_layout_manager);

        final ImageView cornerImage = findViewById(R.id.cornerImage);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        V6StackLayoutManager v6StackLayoutManager = new V6StackLayoutManager();
        recyclerView.setLayoutManager(v6StackLayoutManager);
        recyclerView.setAdapter(new V6Adapter());
        v6StackLayoutManager.setOnCoverItemListener(new V6StackLayoutManager.OnCoverItemListener() {
            @Override
            public void onCoverItem(boolean isCover) {
                cornerImage.setVisibility(isCover ? View.VISIBLE : View.GONE);
            }
        });

        SmartRefreshLayout smartRefreshLayout = findViewById(R.id.refreshView);
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
            }
        });
        smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(2000);
            }
        });
    }
}
