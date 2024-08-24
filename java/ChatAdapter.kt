package com.example.buzzhub

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import de.hdodenhof.circleimageview.CircleImageView
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class ChatAdapter(private val context: Context, private val chatList: ArrayList<MessageData>) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private val MESSAGE_TYPE_LEFT = 0
    private val MESSAGE_TYPE_RIGHT = 1
    var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == MESSAGE_TYPE_RIGHT) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_right, parent, false)
            return ViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_left, parent, false)
            return ViewHolder(view)
        }

    }

    override fun getItemCount(): Int {
        return chatList.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chatList[position]
        holder.txtUserName.text = chat.message
        val tim = chat.time
        val timestampLong = tim.toLong()
        val date = Date(timestampLong)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(date)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val formattedTime = timeFormat.format(date)

        holder.time.text = formattedTime

        if (chat.message.startsWith("https://firebasestorage.googleapis.com") &&
            chat.message.contains("images")) {
            // If the message is a Firebase URL, load the image
            holder.image.visibility = View.VISIBLE
            Glide.with(context).load(chat.message).into(holder.image)
            holder.txtUserName.text = "Image"
            holder.image.maxHeight = 150
            holder.image.maxWidth = 150
        } else {
            // Otherwise, hide the image view
            holder.image.visibility = View.GONE

        }

        if (chat.message.startsWith("https://firebasestorage.googleapis.com") &&
            chat.message.contains("videos")) {
            // If the message is a Firebase URL pointing to a video, load the video thumbnail
            holder.video.visibility = View.VISIBLE

            holder.video.setVideoURI(Uri.parse(chat.message));

            // You may need to handle image positioning differently, depending on your layout
            // holder.image.x = 150.0f;
            // holder.image.y = 150f;
        } else {
            // Otherwise, hide the image view
            holder.video.visibility = View.GONE

        }

        holder.linearLayout.setOnLongClickListener {
            val currentTimeMillis = System.currentTimeMillis()

            // Assuming chat.time is the timestamp of the message
            val messageTimeMillis = chat.time.toLong()

            // Calculate the difference in milliseconds
            val timeDifferenceMillis = currentTimeMillis - messageTimeMillis

            // Convert milliseconds to minutes
            val minutesPassed = timeDifferenceMillis / (1000 * 60)

            // Check if less than 5 minutes have passed
            if (minutesPassed < 5) {
                //if time less than 5 minutes passed then show popup_menu
                val intent = Intent(context, popup_menu::class.java)
                intent.putExtra("sender", chat.sender)
                intent.putExtra("receiver", chat.receiver)
                intent.putExtra("message", chat.message)
                intent.putExtra("time", chat.time)
                context.startActivity(intent)
            }
            true
        }
    }
    /*override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chatList[position]
        holder.txtUserName.text = chat.message
        val tim = chat.time

        val timestampLong = tim.toLong()

        val date = Date(timestampLong)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val formattedDate = dateFormat.format(date)

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val formattedTime = timeFormat.format(date)



        holder.time.text = formattedTime



        holder.linearLayout.setOnLongClickListener {
        val currentTimeMillis = System.currentTimeMillis()

// Assuming chat.time is the timestamp of the message
        val messageTimeMillis = chat.time.toLong()

// Calculate the difference in milliseconds
                val timeDifferenceMillis = currentTimeMillis - messageTimeMillis

// Convert milliseconds to minutes
                val minutesPassed = timeDifferenceMillis / (1000 * 60)

// Check if less than 5 minutes have passed

                if(minutesPassed < 5){
                    //if time less than 5 minutes passed then show popup_menu
                    val intent = Intent(context, popup_menu::class.java)
                    intent.putExtra("sender", chat.sender, )
                    intent.putExtra("receiver", chat.receiver)
                    intent.putExtra("message", chat.message)
                    intent.putExtra("time", chat.time)
                    context.startActivity(intent)

                }
                true
                //if time less than 5 minutes passed then show popup_menu

            }
        //Glide.with(context).load(user.profileImage).placeholder(R.drawable.profile_image).into(holder.imgUser)

    }*/




    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtUserName: TextView = view.findViewById(R.id.tvMessage)
        val imgUser: CircleImageView = view.findViewById(R.id.userImage)
        val linearLayout: LinearLayout = view.findViewById(R.id.layoutUser)
        val time: TextView = view.findViewById(R.id.timestamp)
        val image :ImageView = view.findViewById(R.id.messageimage)
        val video: VideoView = view.findViewById(R.id.messagevideo)
    }

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if (chatList[position].sender == firebaseUser!!.uid) {
            return MESSAGE_TYPE_RIGHT
        } else {
            return MESSAGE_TYPE_LEFT
        }

    }
}