package com.example.buzzhub

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class addstory : AppCompatActivity() {


    private var selectedUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addstory)

        val videoImageView: ImageView = findViewById(R.id.man2)
        val cameraImageView: ImageView = findViewById(R.id.man1)
        val saveButton: Button = findViewById(R.id.save)
        val backButton: ImageView = findViewById(R.id.backbutton)
        val home : ImageView = findViewById(R.id.homeicon)
        val search : ImageView = findViewById(R.id.searchicon)
        val add : ImageView = findViewById(R.id.FAB)
        val chat : ImageView = findViewById(R.id.chaticon)
        val profile : ImageView = findViewById(R.id.profileicon)
        backButton.setOnClickListener {
            startActivity(Intent(this, Feed_Info::class.java))
            finish()
        }
        home.setOnClickListener {
            startActivity(Intent(this, Feed_Info::class.java))
            finish()
        }
        search.setOnClickListener {
            startActivity(Intent(this, SearchScreen::class.java))
            finish()
        }
        add.setOnClickListener {
            startActivity(Intent(this, addstory::class.java))
            finish()
        }
        chat.setOnClickListener {
            startActivity(Intent(this, ChatScreen::class.java))
            finish()
        }
        profile.setOnClickListener {
            startActivity(Intent(this, Profile::class.java))
            finish()
        }


        videoImageView.setOnClickListener {
            pickVideoFromGallery()
        }

        cameraImageView.setOnClickListener {
            pickImageFromGallery()
        }

        saveButton.setOnClickListener {
            selectedUri?.let { uri ->
                uploadStory(uri)
            } ?: run {
                Toast.makeText(this, "Please select a video or image first", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun pickVideoFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "video/*"
        startActivityForResult(intent, REQUEST_CODE_PICK_VIDEO)
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    private fun uploadStory(uri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference
        val storyRef = storageRef.child("stories").child("${System.currentTimeMillis()}")

        storyRef.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                // Get the download URL of the uploaded file
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                    saveStoryToDatabase(downloadUri.toString())
                }.addOnFailureListener { exception ->
                    Toast.makeText(
                        this,
                        "Failed to get download URL: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Failed to upload story: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    private fun saveStoryToDatabase(downloadUrl: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val storyData = HashMap<String, Any>()
            storyData["imageUrl"] = downloadUrl

            FirebaseDatabase.getInstance().reference
                .child("stories")
                .child(userId)
                .push()
                .setValue(storyData)
                .addOnSuccessListener {
                    Intent(this, Feed_Info::class.java)
                    Toast.makeText(this, "Story uploaded successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { exception ->
                    Intent(this, Feed_Info::class.java)
                    Toast.makeText(
                        this,
                        "Failed to upload story to database: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_PICK_VIDEO, REQUEST_CODE_PICK_IMAGE -> {
                    data?.data?.let { uri ->
                        selectedUri = uri
                    }
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_PICK_VIDEO = 101
        private const val REQUEST_CODE_PICK_IMAGE = 102
    }
}