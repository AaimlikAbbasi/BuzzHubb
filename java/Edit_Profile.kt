package com.example.buzzhub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Edit_Profile : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        auth = Firebase.auth
        val home = findViewById<ImageView>(R.id.homeicon)
        val search = findViewById<ImageView>(R.id.searchicon)
        val add = findViewById<ImageView>(R.id.FAB)
        val chat = findViewById<ImageView>(R.id.chaticon)
        val profile = findViewById<ImageView>(R.id.profileicon)
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



        // Retrieve current user's UID
        val currentUserUid = auth.currentUser?.uid

        // Access Firebase Realtime Database
        val database = Firebase.database
        val usersRef = database.getReference("USER")

        val spinner: Spinner = findViewById(R.id.GenderSpinner)
        val cities = arrayOf("Enter gender", "MALE","FEMALE") // Example data
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Retrieve current user's data from Realtime Database
        currentUserUid?.let { uid ->
            usersRef.child(uid).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    // User data found, populate EditText fields with retrieved data
                    val userData = snapshot.getValue(EmployeeModel::class.java)
                    userData?.let { user ->
                        findViewById<EditText>(R.id.name).setText(user.name)
                        findViewById<TextView>(R.id.email).text = user.email
                        findViewById<EditText>(R.id.BIO).setText(user.bio)
                        findViewById<EditText>(R.id.countrySpinner).setText(user.detail)
                        spinner.setSelection(cities.indexOf(user.gender))
                        findViewById<TextView>(R.id.password).text = user.password
                    }
                } else {
                    // User data not found
                    Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                // Error retrieving user data
                Toast.makeText(
                    this,
                    "Error retrieving user data: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
       val save1= findViewById<Button>(R.id.save)
        // Update user profile when the "Save" button is clicked
        save1.setOnClickListener {
            // Retrieve edited information from EditText fields
            val name = findViewById<EditText>(R.id.name).text.toString()
            val email = findViewById<TextView>(R.id.email).text.toString()
            val bio = findViewById<EditText>(R.id.BIO).text.toString()
            val detail = findViewById<EditText>(R.id.countrySpinner).text.toString()
            val gender = spinner.selectedItem.toString()
            val password = findViewById<TextView>(R.id.password).text.toString()

            // Update user data in Firebase Realtime Database
            currentUserUid?.let { uid ->
                val updatedUserData = EmployeeModel(uid, name, email, bio, detail, gender, password)
                usersRef.child(uid).setValue(updatedUserData)
                    .addOnSuccessListener {
                        // Profile update successful
                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                        finish() // Finish the activity after updating profile
                    }
                    .addOnFailureListener { exception ->
                        // Profile update failed
                        Toast.makeText(this, "Failed to update profile: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}