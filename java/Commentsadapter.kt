package com.example.buzzhub

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
class Commentsadapter(list: ArrayList<commentsdata>, c: Context) : RecyclerView.Adapter<Commentsadapter.MyViewHolder>(){
    class MyViewHolder:RecyclerView.ViewHolder {
        constructor(itemView: View):super(itemView)
        //val card = itemView.findViewById<CardView>(R.id.mentorcardlayout)
        var card:RelativeLayout = itemView.findViewById(R.id.commentsview)
        val mentorImage: CircleImageView = itemView.findViewById(R.id.pf1)
        val mentorName: TextView = itemView.findViewById(R.id.thecomment)

    }
    var context:Context = c
    var list:ArrayList<commentsdata> = list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v : View = LayoutInflater.from(context).inflate(R.layout.comment,parent,false)
        return MyViewHolder(v)
    }
    override fun getItemCount(): Int {
        return list.size
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val mentor = list[position]
        Log.d("This is from adapter" , "Mentor ID: ${mentor.id}")
        Log.d(ContentValues.TAG, "Name: ${mentor.name}")
        Log.d(ContentValues.TAG, "Profile Image: ${mentor.imageResource}")
        Log.d(ContentValues.TAG, "Comment: ${mentor.comment}")
        // Bind mentor data to views
        Glide.with(holder.itemView.context).load(mentor.imageResource).into(holder.mentorImage)
        holder.mentorName.text = mentor.name + " : " + mentor.comment
        //holder.comment.text = mentor.comment
        holder.card.setOnClickListener {
            val intent = Intent(context, Profile::class.java)
            intent.putExtra("UserId", mentor.id)
            context.startActivity(intent)
        }
    }
}