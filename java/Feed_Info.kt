package com.example.buzzhub

import com.example.buzzhub.feedadapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Feed_Info : AppCompatActivity() {




    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var currentUserRef: DatabaseReference
    private lateinit var userId: String


    private lateinit var postList: MutableList<Post_feed>
    private lateinit var dbRef: DatabaseReference
    private lateinit var postRecyclerView: RecyclerView

    private lateinit var postAdapter: feedadapter

    private lateinit var postList1: MutableList<Postmodel>
    private lateinit var postRecyclerView1: RecyclerView
    private lateinit var postAdapter1: MediaAdapter




    private val TAG = "Profile"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_info)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        val home = findViewById<ImageView>(R.id.homeicon)
        val search = findViewById<ImageView>(R.id.searchicon)
        val add = findViewById<ImageView>(R.id.FAB)
        val chat = findViewById<ImageView>(R.id.chaticon)
        val profile = findViewById<ImageView>(R.id.profileicon)
        home.setOnClickListener {
            startActivity(Intent(this, Feed_Info::class.java))
            finish()
        }
        search.setOnClickListener {
            startActivity(Intent(this, SearchScreen::class.java))
            finish()
        }
        add.setOnClickListener {
            startActivity(Intent(this, PostInfo::class.java))
            finish()
        }
        chat.setOnClickListener {
            startActivity(Intent(this, ChatsScreen::class.java))
            finish()
        }
        profile.setOnClickListener {
            startActivity(Intent(this, Profile::class.java))
            finish()
        }
        val logout : Button = findViewById(R.id.logout)
        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, login::class.java))
            finish()
        }


        // Get the current user's ID
        userId = auth.currentUser?.uid ?: ""

        // Set up click listener for the profile icon
        val loginText = findViewById<ImageView>(R.id.profileicon)
        loginText.setOnClickListener {
            startActivity(Intent(this, Profile::class.java))
            finish()
        }


        postRecyclerView1 = findViewById(R.id.recyclerView2)
        postRecyclerView1.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Assuming you have an ArrayList<Postmodel> named 'postModelList' containing your data
        postList1 = mutableListOf()
        postAdapter1 = MediaAdapter(postList1, this)
        postRecyclerView1.adapter = postAdapter1

        getpostdata()




        postRecyclerView = findViewById(R.id.recyclerView)
        // Assuming you have an ArrayList<Post_feed> named 'posts' containing your data
        val posts: ArrayList<Post_feed> = ArrayList()
        for (i in 1..100) {
            posts.add(
                Post_feed(
                    "danielmalone_" + i,
                    "asdf asdf text here",
                    "https://picsum.photos/600/300?random&" + i
                )
            )
        }

// Initialize the RecyclerView and adapter
        postRecyclerView = findViewById(R.id.recyclerView)
        postRecyclerView.layoutManager = LinearLayoutManager(this)
        postAdapter = feedadapter(posts, this) // Pass the 'posts' ArrayList and the context
        postRecyclerView.adapter = postAdapter


    }




    private fun getpostdata() {
        // Get the current Firebase user
        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        // Reference to the "posts" node in Firebase Realtime Database
        val dbRef = FirebaseDatabase.getInstance().getReference("posts").child(userId)

        // Add a ValueEventListener to fetch data
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Initialize the list if it's null
                if (postList1 == null) {
                    postList1 = mutableListOf()
                } else {
                    // Clear the existing post list
                    postList1.clear()
                }

                // Inside onDataChange method of getpostdata function
                for (postSnapshot in snapshot.children) {
                    // Convert each child snapshot to a Postmodel object
                    val post = postSnapshot.getValue(Postmodel::class.java)
                    if (post != null) {
                        // Add the Postmodel object to the post list
                        postList1.add(post)

                        // Log the retrieved image URL for debugging
                        Log.d(TAG, "Image URL: ${post.imageUrl}")

                        //  Toast.makeText(this@Profile, "Post data found", Toast.LENGTH_SHORT).show()
                    }
                }

                // Notify the RecyclerView adapter of the data change
                if (::postAdapter1.isInitialized) {
                    postAdapter1.notifyDataSetChanged()
                } else {
                    // Initialize postAdapter if not initialized
                    postAdapter1 = MediaAdapter(postList1, applicationContext)
                    postRecyclerView1.adapter = postAdapter1
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors while reading data
                Log.e(TAG, "Failed to read value.", error.toException())
            }
        })
    }


}

data class Post_feed(val username: String, val text: String, val photo: String)

