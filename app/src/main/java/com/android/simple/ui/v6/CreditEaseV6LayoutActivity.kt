package com.android.simple.ui.v6

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.simple.R
import kotlinx.android.synthetic.main.activity_main.*

class CreditEaseV6LayoutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = V6Adapter()

        headButton.setOnClickListener {
            Toast.makeText(this, headButton.text, Toast.LENGTH_SHORT).show()
        }

        refreshLayout.setOnRefreshListener {
            it.finishRefresh(1000)
        }

        refreshLayout.setOnLoadMoreListener {
            it.finishLoadMore(1000)
        }
    }
}