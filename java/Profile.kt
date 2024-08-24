package com.example.buzzhub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView

class Profile : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var currentUserRef: DatabaseReference
    private lateinit var userId: String

    private lateinit var postList : MutableList<Postmodel>
    private lateinit var dbRef: DatabaseReference
    private lateinit var postRecyclerView: RecyclerView

    private lateinit var postAdapter: postviewAdapter

    private lateinit var circleImageView: CircleImageView
    private lateinit var storageReference: StorageReference
    private val TAG = "Profile"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val back = findViewById<ImageView>(R.id.backbutton)
        val home = findViewById<ImageView>(R.id.homeicon)
        val search = findViewById<ImageView>(R.id.searchicon)
        val add = findViewById<ImageView>(R.id.FAB)
        val chat = findViewById<ImageView>(R.id.chaticon)
        val profile = findViewById<ImageView>(R.id.profileicon)
        back.setOnClickListener {
            val intent = Intent(this, Feed_Info::class.java)
            startActivity(intent)
        }
        home.setOnClickListener {
            val intent = Intent(this, Feed_Info::class.java)
            startActivity(intent)
        }
        search.setOnClickListener {
            val intent = Intent(this, SearchScreen::class.java)
            startActivity(intent)
        }
        add.setOnClickListener {
            val intent = Intent(this, post::class.java)
            startActivity(intent)
        }
        chat.setOnClickListener {
            val intent = Intent(this, ChatsScreen::class.java)
            startActivity(intent)
        }
        profile.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }








        circleImageView = findViewById(R.id.pics)

        // Initialize Firebase Storage
        storageReference = FirebaseStorage.getInstance().reference


        // Retrieve the image from Firebase Storage and load it into the CircleImageView


        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Get the current user's ID
        userId = auth.currentUser?.uid ?: ""
        retrieveImageFromFirebase(userId)
        currentUserRef = database.reference.child("USER").child(userId)

        // In the receiving activity's onCreate() method or any other relevant place
        val receivedId = intent.getStringExtra("currentid")

// Now you can use the receivedId variable to access the value passed from the previous activity


        // Fetch the current user's data from Firebase Realtime Database
        currentUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentUser = dataSnapshot.getValue(EmployeeModel::class.java)

                if (currentUser != null) {
                    // Display the fetched data
                    //    Toast.makeText(applicationContext, "User ID: ${currentUser.empId}", Toast.LENGTH_SHORT).show()
                    //   Toast.makeText(applicationContext, "Description from database: ${currentUser.detail}", Toast.LENGTH_SHORT).show()

                    val descriptionTextView = findViewById<TextView>(R.id.description)
                    descriptionTextView.text = currentUser.detail
                } else {
                    Toast.makeText(applicationContext, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                Toast.makeText(applicationContext, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
                Log.e("ProfileActivity", "Failed to fetch user data", databaseError.toException())
            }
        })
        // Set up click listener for editing profile




        val loginText = findViewById<ImageView>(R.id.editprofile)
        loginText.setOnClickListener {
            startActivity(Intent(this, Edit_Profile::class.java))
            finish()
            intent.putExtra("currentid", receivedId)
            startActivity(intent)
        }

        val post1 = findViewById<TextView>(R.id.post)
        post1.setOnClickListener {
            startActivity(Intent(this, post::class.java))
            finish()
        }


        val upload = findViewById<ImageView>(R.id.camera)
        upload.setOnClickListener {
            intent.putExtra("currentid", receivedId)
            startActivity(Intent(this, profileimage::class.java))
            finish()
        }

        val addstory1=findViewById<ImageView>(R.id.addstory)
        addstory1.setOnClickListener {
            startActivity(Intent(this, addstory::class.java))
            finish()
        }


        val edit=findViewById<ImageView>(R.id.editprofile)
        edit.setOnClickListener {
            startActivity(Intent(this, Edit_Profile::class.java))
            finish()
        }

        postRecyclerView = findViewById(R.id.postRecyclerView)
        postRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize postList
        postList = mutableListOf()

        // Initialize postAdapter
        postAdapter = postviewAdapter(postList, applicationContext)
        postRecyclerView.adapter = postAdapter

        // Call getpostdata with context
        getpostdata()
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
                if (postList == null) {
                    postList = mutableListOf()
                } else {
                    // Clear the existing post list
                    postList.clear()
                }

                // Inside onDataChange method of getpostdata function
                for (postSnapshot in snapshot.children) {
                    // Convert each child snapshot to a Postmodel object
                    val post = postSnapshot.getValue(Postmodel::class.java)
                    if (post != null) {
                        // Add the Postmodel object to the post list
                        postList.add(post)

                        // Log the retrieved image URL for debugging
                        Log.d(TAG, "Image URL: ${post.imageUrl}")

                        //  Toast.makeText(this@Profile, "Post data found", Toast.LENGTH_SHORT).show()
                    }
                }

                // Notify the RecyclerView adapter of the data change
                if (::postAdapter.isInitialized) {
                    postAdapter.notifyDataSetChanged()
                } else {
                    // Initialize postAdapter if not initialized
                    postAdapter = postviewAdapter(postList, applicationContext)
                    postRecyclerView.adapter = postAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors while reading data
                Log.e(TAG, "Failed to read value.", error.toException())
            }
        })
    }


    private fun retrieveImageFromFirebase(userId: String) {
        val database = Firebase.database
        val userImagesRef = database.getReference("users").child(userId).child("images")

        // Read data from Firebase Database
        userImagesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val imageUrl = snapshot.getValue(String::class.java)
                    imageUrl?.let {
                        // Load the image into the CircleImageView using Glide
                        Glide.with(this@Profile)
                            .load(it)
                            .placeholder(R.drawable.man4) // Placeholder image while loading
                            .error(R.drawable.man4) // Image to display in case of error
                            .into(circleImageView)
                        return // Exit the loop after loading the first image
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                showToast("Error reading image URLs from database: ${databaseError.message}")
            }
        })
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }



}