package com.example.buzzhub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import java.lang.Thread.sleep

class BuzzHub : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buzz_hub)

        Firebase.database.setPersistenceEnabled(true)

        val m = Firebase.database.getReference("USER")
        m.keepSynced(true)

        val n = Firebase.database.getReference("users")
        n.keepSynced(true)

        val u = Firebase.database.getReference("posts")
        u.keepSynced(true)
        val v = Firebase.database.getReference("stories")
        v.keepSynced(true)

        val c = Firebase.database.getReference("Chats")
        c.keepSynced(true)
        val c1 = Firebase.database.getReference("Chat")
        c1.keepSynced(true)



        Handler().postDelayed({

            val mAuth = FirebaseAuth.getInstance()
            val currentUser = mAuth.currentUser
            if (currentUser != null) {
                val intent = Intent(this@BuzzHub, Feed_Info::class.java)
                startActivity(intent)
                finish()

            } else {
                val intent = Intent(this@BuzzHub, login::class.java)
                startActivity(intent)
                finish()
            }
        }, 1000)

        }






    }
