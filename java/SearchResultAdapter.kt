package com.example.i212586

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.buzzhub.Profile
import com.example.buzzhub.R
import com.example.buzzhub.likesdata
import de.hdodenhof.circleimageview.CircleImageView

class SearchResultAdapter(list: ArrayList<likesdata>, c: Context) : RecyclerView.Adapter<SearchResultAdapter.MyViewHolder>(){
    class MyViewHolder: RecyclerView.ViewHolder {
        constructor(itemView: View):super(itemView)
        //val card = itemView.findViewById<CardView>(R.id.mentorcardlayout)


        var card: RelativeLayout = itemView.findViewById(R.id.searchresultrow)
        val mentorImage: CircleImageView = itemView.findViewById(R.id.pf1)
        val mentorName: TextView = itemView.findViewById(R.id.resultname)


    }

    var context: Context = c
    var list:ArrayList<likesdata> = list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v : View = LayoutInflater.from(context).inflate(R.layout.searchresultrow,parent,false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val mentor = list[position]
        Toast.makeText(context, "Search adapter test", Toast.LENGTH_SHORT).show()
        Log.d("This is from search adapter" , "Mentor ID: ${mentor.id}")
        Log.d(ContentValues.TAG, "Name: ${mentor.name}")
        Log.d(ContentValues.TAG, "Profile Image: ${mentor.imageResource}")


        // Bind mentor data to views
        Glide.with(holder.itemView.context).load(mentor.imageResource).into(holder.mentorImage)

        holder.mentorName.text = mentor.name

        holder.card.setOnClickListener {
            val intent = Intent(context, Profile::class.java)
            intent.putExtra("mentorid", mentor.id)

            context.startActivity(intent)
        }





    }}
