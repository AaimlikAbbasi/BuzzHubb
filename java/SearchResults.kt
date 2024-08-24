package com.example.buzzhub

import android.content.ContentValues
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.i212586.SearchResultAdapter
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.lang.Thread.sleep


class SearchResults : AppCompatActivity() {
    var mentorList = ArrayList<likesdata>()
    lateinit var rv: androidx.recyclerview.widget.RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)
        rv = findViewById(R.id.searchResultRV)

        val query = intent.getStringExtra("search")
        Toast.makeText(this, "Searching for $query", Toast.LENGTH_SHORT).show()

        val db = Firebase.database.reference.child("USER")
        db.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                for (mentorSnapshot in snapshot.children) {
                    // Get the unique ID of the mentor
                    val mentorId = mentorSnapshot.key
                    val name = mentorSnapshot.child("name").getValue(String::class.java)
                    val profileImage = mentorSnapshot.child("profileImage").getValue(String::class.java)

                    retrieveImageFromFirebase(mentorId.toString()) { imageUrl ->
                        val mentorData = likesdata(
                            mentorId.toString(),
                            imageUrl,
                            name.toString()
                        )
                        Log.d("Testing for name", "Name: $name")
                        Log.d("Testing for name", "Name: $name")
                        if (mentorData.name == query) {
                            mentorList.add(mentorData)
                            mentorList.forEach {
                                Log.d(
                                    "This is the list in search results",
                                    "Mentor ID: ${it.id}"
                                )
                                Log.d(ContentValues.TAG, "Name: ${it.name}")
                                Log.d(ContentValues.TAG, "Profile Image: ${it.imageResource}")
                            }
                            val adapter = SearchResultAdapter(mentorList, this@SearchResults)
                            rv.adapter = adapter
                            rv.layoutManager = LinearLayoutManager(this@SearchResults)
                            Toast.makeText(this, "Mentor found", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Log.d(TAG, "No mentors found")
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