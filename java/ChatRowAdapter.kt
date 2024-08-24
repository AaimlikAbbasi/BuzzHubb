package com.example.buzzhub

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



class ChatRowAdapter(list: ArrayList<ChatsScreenData>, c: Context) : RecyclerView.Adapter<ChatRowAdapter.MyViewHolder>(){
    class MyViewHolder: RecyclerView.ViewHolder {
        constructor(itemView: View):super(itemView)
        //val card = itemView.findViewById<CardView>(R.id.mentorcardlayout)

        val card: RelativeLayout = itemView.findViewById(R.id.chat_recycler_row)
        val mentorImage: ImageView = itemView.findViewById(R.id.chatrowprofileimage)
        val chatName: TextView = itemView.findViewById(R.id.chatrowprofilename)

        //val unseen: TextView = itemView.findViewById(R.id.messagestatus)

    }
/*   class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
       val card: RelativeLayout = itemView.findViewById(R.id.chat_recycler_row)
       val mentorImage: ImageView = itemView.findViewById(R.id.chatrowprofileimage)
       val chatName: TextView = itemView.findViewById(R.id.chatrowprofilename)
   }*/


    var context: Context = c
    var list:ArrayList<ChatsScreenData> = list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        //val v : View = LayoutInflater.from(context).inflate(R.layout.activity_chats_screen,parent,false)
        val v : View = LayoutInflater.from(context).inflate(R.layout.chatrow,parent,false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val mentor = list[position]
        Toast.makeText(context, "Chat member test" , Toast.LENGTH_SHORT).show()



        // Bind mentor data to views
        Glide.with(holder.itemView.context).load(mentor.profileimage).into(holder.mentorImage)
        holder.chatName.text = mentor.name
        //Toast.makeText(context, "Chat row populated"  , Toast.LENGTH_SHORT).show()
        //holder.unseen.text = mentor.unread

        holder.card.setOnClickListener {
            val intent = Intent(context, ChatMentor::class.java)
            Log.d("ChatRowAdapterSent", mentor.id)
            intent.putExtra("recieverid", mentor.id)
            intent.putExtra("recievername", mentor.name)
            intent.putExtra("recieverimage", mentor.profileimage)
            context.startActivity(intent)
        }





    }}