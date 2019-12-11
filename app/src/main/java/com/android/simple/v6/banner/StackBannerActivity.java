package com.android.simple.v6.banner;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.simple.R;
import com.android.simple.v6.layout.V6Adapter;

public class StackBannerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stack_banner_manager);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        SkidRightLayoutManager stackLayoutManager = new SkidRightLayoutManager(0.9f, 0.9f);
        recyclerView.setLayoutManager(stackLayoutManager);
        recyclerView.setAdapter(new StackBannerAdapter());
    }
}
