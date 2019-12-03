package com.android.simple.v6

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.simple.R
import com.android.simple.ui.v6.StackScrollLayout
import com.android.simple.ui.v6.V6Adapter
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

        stackScrollLayout.onRefreshListener = object : StackScrollLayout.OnRefreshListener {
            override fun onRefreshProgress(progress: Float) {
                Toast.makeText(this@StackScrollActivity, "下拉刷新进度 = $progress", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "下拉刷新进度 = $progress")
            }

            override fun onRefresh() {
                Toast.makeText(this@StackScrollActivity, "下拉刷新了", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "下拉刷新了")
            }

        }
    }
}
