package com.example.buzzhub
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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MessageAdapter(private val list: ArrayList<MessageData>, private val context: Context, private val senderId: String) : RecyclerView.Adapter<MessageAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: RelativeLayout = itemView.findViewById(R.id.messagerow)
        val message: TextView = itemView.findViewById(R.id.messageinrow)
        val time: TextView = itemView.findViewById(R.id.timestamp)
        val receiverName: TextView = itemView.findViewById(R.id.name)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.messagerow, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val messageData = list[position]
        holder.message.text = messageData.message
        holder.time.text = messageData.time
        fetchReceiverName(messageData.sender, holder.receiverName)
    }

    private fun fetchReceiverName(senderId: String, receiverNameTextView: TextView) {
        val db = Firebase.database.reference.child("Users")
        db.orderByChild("id").equalTo(senderId).addValueEventListener(object : ValueEventListener {
             override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val name = userSnapshot.child("name").getValue(String::class.java)
                        receiverNameTextView.text = name
                    }
                } else {
                    receiverNameTextView.text = "Unknown" // Handle the case where the receiver's name is not found
                }
            }

             override fun onCancelled(error: DatabaseError) {
                Log.d(ContentValues.TAG, "Failed to read value.", error.toException())
                receiverNameTextView.text = "Error" // Handle the case of error
            }
        })
    }
}
