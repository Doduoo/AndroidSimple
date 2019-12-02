package com.android.simple.v6

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.simple.R
import com.android.simple.ui.v6.V6Adapter
import kotlinx.android.synthetic.main.activity_stack_scroll.*

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
    }
}
