package com.example.buzzhub

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class feedadapter(val feedItems: ArrayList<Post_feed>, private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_POST: Int = 0
        const val TYPE_AD: Int = 1
    }

    override fun getItemCount() = feedItems.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_POST) {
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.top_post, parent, false)
            PostViewHolder(view)
        } else {
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.top_post, parent, false)
            AdViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if ((position % 5) == 0 && position != 0) {
            TYPE_AD
        } else {
            TYPE_POST
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_POST) {
            val postHolder = holder as PostViewHolder
            val postItem = feedItems[position]
            postHolder.caption.text = postItem.text
            Picasso.get().load(postItem.photo).into(postHolder.photo)

        } else {
            // Handle ads here if needed
        }
    }


    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val username: TextView = itemView.findViewById(R.id.name)
        val caption: TextView = itemView.findViewById(R.id.description)
        val photo: ImageView = itemView.findViewById(R.id.post)
    }

    inner class AdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Add views for ad if needed
    }
}
