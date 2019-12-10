package com.android.simple.v6.layout

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.simple.R

/**
 *
 * @author LiuYong
 */
class V6Adapter : RecyclerView.Adapter<VH>() {

    var mData = listOf(
        "item 01",
        "item 02",
        "item 03",
        "item 04",
        "item 05",
        "item 06",
        "item 07",
        "item 08",
        "item 09",
        "item 10"
    )

    var mColor = listOf(
        Color.CYAN,
        Color.DKGRAY,
        Color.GRAY,
        Color.BLUE,
        Color.GREEN,
        Color.LTGRAY,
        Color.LTGRAY,
        Color.MAGENTA,
        Color.RED,
        Color.YELLOW
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_v6, parent, false)
        return VH(itemView)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.contentText.text = mData[position]
        if (position == 0) {
            holder.contentText.setTextColor(Color.WHITE)
        } else {
            holder.contentText.setTextColor(Color.BLACK)
            holder.contentText.setBackgroundColor(Color.WHITE)
        }

        if(position == 1) {
            holder.contentText.setBackgroundResource(R.drawable.shape_home_content_rv_bg)
        }

        holder.contentText.setOnClickListener {
            Toast.makeText(it.context, holder.contentText.text, Toast.LENGTH_SHORT).show()
        }
    }
}

class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val contentText: TextView = itemView.findViewById(R.id.contentText)
}