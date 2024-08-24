package com.example.buzzhub


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class postviewAdapter (list:  MutableList<Postmodel>, c: Context):
    RecyclerView.Adapter<postviewAdapter.MyViewHolder>() {
    var list = list
    var context = c
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v: View = LayoutInflater
            .from(context)
            .inflate(R.layout.top_post, parent, false)
        return MyViewHolder(v)
    }
    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val post = list[position]
        holder.firsttext.text = post.caption


            Glide.with(context)
                .load(post.imageUrl)
                .into(holder.imageView)

    }


    override fun getItemCount(): Int {

        return list.size
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val firsttext: TextView = itemView.findViewById(R.id.description)

        val imageView: ImageView = itemView.findViewById(R.id.post)

        fun bind(post: Postmodel) {
            firsttext.text = post.caption
            Glide.with(context)
                .load(post.imageUrl) // Assuming imageUrl is the URL of the image
                .into(imageView)
        }
    }

}