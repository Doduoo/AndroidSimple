package com.android.simple.xiami

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.simple.R
import kotlinx.android.synthetic.main.activity_stack_scroll.*

const val TAG = "StackScrollActivity"

class StackScrollActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stack_scroll)

        bannerRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        bannerRecyclerView.adapter = V6Adapter()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = V6Adapter()

        headButton.setOnClickListener {
            Toast.makeText(this, headButton.text, Toast.LENGTH_SHORT).show()
        }

        stackScrollLayout.setRefreshListener(object : StackScrollLayout.OnRefreshListener {
            override fun onRefreshMoving(progress: Float) {
                Log.d(TAG, "下拉刷新进度 = $progress")
            }

            override fun onRefresh() {
                Toast.makeText(this@StackScrollActivity, "下拉刷新了", Toast.LENGTH_SHORT).show()
            }
        })

        stackScrollLayout.setLoadMoreListener(object : StackScrollLayout.OnLoadMoreListener {
            override fun onLoadMoving(progress: Float) {
                Log.d(TAG, "加载更多进度 = $progress")
            }

            override fun onLoadMore() {
                Toast.makeText(this@StackScrollActivity, "加载更多", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
