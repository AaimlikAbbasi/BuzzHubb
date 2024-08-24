package com.example.buzzhub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.database.database

class PostInfo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_info)
        val back = findViewById<ImageView>(R.id.backbutton)
        val likerecycler = findViewById<RecyclerView>(R.id.likesRecyclerView)
        val commentrecycler = findViewById<RecyclerView>(R.id.commentRecyclerView)

        val dbref = Firebase.database.getReference("Comments")
        val dbref2 = Firebase.database.getReference("Likes")









        back.setOnClickListener {
            finish()
        }

    }
}