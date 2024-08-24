package com.example.buzzhub

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class MediaAdapter(private val list: MutableList<Postmodel>, private val context: Context) :
    RecyclerView.Adapter<MediaAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.people, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = list[position]

        Glide.with(context)
            .load(currentItem.imageUrl)
            .into(holder.imageView)
        holder.imageView.setOnClickListener {
            val intent = Intent(context, ShowingMediaScreen::class.java)
            intent.putExtra("imageUrl", currentItem.imageUrl)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.man1)


    }
}