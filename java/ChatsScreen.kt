package com.example.buzzhub

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.i212586.ActiveMembers
import com.example.i212586.MentorData
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class ChatsScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats_screen)
        val back = findViewById<ImageView>(R.id.backbutton)
        val home = findViewById<ImageView>(R.id.homeicon)
        val search = findViewById<ImageView>(R.id.searchicon)
        val profile = findViewById<ImageView>(R.id.profileicon)
        val chats = findViewById<ImageView>(R.id.chaticon)


        val rv = findViewById<RecyclerView>(R.id.activeparticipantsrecyclerview)
        val rv2 = findViewById<RecyclerView>(R.id.chatsrecyclerview)

        val mentorList = ArrayList<MentorData>() // Initialize mentorList

        val db = Firebase.database.reference.child("USER")
        db.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                for (mentorSnapshot in snapshot.children) {
                    val mentorId = mentorSnapshot.key
                    val name = mentorSnapshot.child("name").getValue(String::class.java)
                    val designation =
                        mentorSnapshot.child("designation").getValue(String::class.java)
                    val profileImage =
                        mentorSnapshot.child("profileImage").getValue(String::class.java)
                    val rate = mentorSnapshot.child("rate").getValue(String::class.java)
                    val availability =
                        mentorSnapshot.child("availability").getValue(String::class.java)
                    var activeIcon = R.drawable.greendots
                    if (availability == "Available") {
                        activeIcon = R.drawable.greendots
                        val mentorData = MentorData(
                            mentorId.toString(),
                            profileImage.toString(),
                            name.toString(),
                            designation.toString(),
                            activeIcon,
                            rate.toString(),
                            availability.toString()
                        )
                        mentorList.add(mentorData)
                    } else {
                        activeIcon = R.drawable.greydots
                    }


                }

                val adapter = ActiveMembers(mentorList, this)
                rv.adapter = adapter
                rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                //Toast.makeText(this, "Active mentors populated", Toast.LENGTH_SHORT).show()
            } else {
                Log.d(ContentValues.TAG, "No mentors found")
            }
        }

        val db2 = Firebase.database.reference.child("Chats")
        val chatList = ArrayList<ChatsScreenData>() // Initialize chatList
        val auth = Firebase.auth
        val user = auth.currentUser
        val uid = user!!.uid


        val adapter2 = ChatRowAdapter(chatList, this)
        rv2.adapter = adapter2
        rv2.layoutManager = LinearLayoutManager(this)


        db2.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                for (chatSnapshot in snapshot.children) {
                    val user1 = chatSnapshot.child("user1").getValue(String::class.java)
                    val user2 = chatSnapshot.child("user2").getValue(String::class.java)
                    var tempid: String = ""
                    if (user1 == uid) {
                        tempid = user2.toString()

                    } else if (user2 == uid) {
                        tempid = user1.toString()
                    }

                    Log.d("Tempid", tempid)


                    val db3 = Firebase.database.reference.child("USER")
                    db3.get().addOnSuccessListener { snapshot ->
                        if (snapshot.exists()) {
                            for (userSnapshot in snapshot.children) {
                                val userId = userSnapshot.key
                                val name = userSnapshot.child("name").getValue(String::class.java)
                                val profileImage =
                                    userSnapshot.child("profileImage").getValue(String::class.java)
                                val time = userSnapshot.child("time").getValue(String::class.java)

                                if (userId == tempid) {
                                    /* val chatData = ChatsScreenData(
                                        tempid,
                                        name.toString(),
                                        profileImage.toString(),
                                        time.toString()
                                    )*/
                                    retrieveImageFromFirebase(userId.toString()) { imageUrl ->
                                        val chatData = ChatsScreenData(
                                            tempid,
                                            name.toString(),
                                            imageUrl,
                                            time.toString()
                                        )

                                        chatList.add(chatData)
                                        adapter2.notifyDataSetChanged()
                                        chatList.forEach() {
                                            Log.d("ChatList", it.toString())
                                        }
                                    }
                                }
                            }
                        }

                    }

                    //Toast.makeText(this, "Chats populated", Toast.LENGTH_SHORT).show()
                }
            }
















            back.setOnClickListener {
                finish()
            }
            home.setOnClickListener {
                intent = Intent(this, Feed_Info::class.java)
                finish()
            }
            search.setOnClickListener {
                intent = Intent(this, SearchResults::class.java)
                finish()
            }
            profile.setOnClickListener {
                intent = Intent(this, Profile::class.java)
                finish()
            }
            chats.setOnClickListener {
                intent = Intent(this, ChatsScreen::class.java)
                finish()
            }
        }
    }

    private fun retrieveImageFromFirebase(userId: String, callback: (String) -> Unit) {
        val database = Firebase.database
        val userImagesRef = database.getReference("users").child(userId).child("images")

        // Read data from Firebase Database
        userImagesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val imageUrl = snapshot.getValue(String::class.java)
                    imageUrl?.let addListenerForSingleValueEvent@{
                        callback.invoke(imageUrl)
                        return@addListenerForSingleValueEvent  // Exit the loop after loading the first image
                    }
                }
                // If no URL is found
                callback.invoke("") // Pass an empty string to the callback
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                showToast("Error reading image URLs from database: ${databaseError.message}")
                callback.invoke("") // Pass an empty string to the callback
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}