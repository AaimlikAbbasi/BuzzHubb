package com.example.buzzhub

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class post : AppCompatActivity() {

    private lateinit var selectedImageUri: Uri
    private lateinit var imageView: ImageView
    private lateinit var captionEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        // Initialize views
        imageView = findViewById(R.id.man1)
        captionEditText = findViewById(R.id.caption)

        // Open gallery when imageView is clicked
        imageView.setOnClickListener {
            openGallery()
        }

        // Upload post when save button is clicked
        findViewById<Button>(R.id.save).setOnClickListener {
            uploadPost()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                selectedImageUri = uri
                imageView.setImageURI(uri)
            }
        }
    }

    private fun uploadPost() {
        val caption = captionEditText.text.toString().trim()
        if (caption.isEmpty() || !this::selectedImageUri.isInitialized) {
            // Handle empty caption or no image selected
            return
        }

            // Upload image to Firebase Storage
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("images/${FirebaseAuth.getInstance().currentUser?.uid}/${System.currentTimeMillis()}")
        imageRef.putFile(selectedImageUri)
            .addOnSuccessListener { taskSnapshot ->
                // Image uploaded successfully, get download URL
                imageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                    // Save post data to Firebase Realtime Database
                    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    val postRef = FirebaseDatabase.getInstance().reference.child("posts").child(currentUserUid).push()
                    val post = Postmodel(imageUrl.toString(), caption)
                    postRef.setValue(post)
                        .addOnSuccessListener {
                            Toast.makeText(this@post, "Saved successfully", Toast.LENGTH_SHORT).show()
                            // Clear caption EditText
                            captionEditText.text.clear()
                            selectedImageUri = Uri.EMPTY
                            // Clear imageView
                            imageView.setImageResource(0)
                            // Optionally, you can navigate back to the previous screen or show a success message
                        }
                        .addOnFailureListener { exception ->
                            // Handle errors while saving post data
                        }
                }
            }
            .addOnFailureListener { exception ->
                // Handle errors while uploading image
            }
    }

    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 100
    }
}

data class Postmodel(
    var imageUrl: String = "",
    val caption: String = ""
)