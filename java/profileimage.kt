package com.example.buzzhub

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.HashMap
import java.util.UUID

class profileimage : AppCompatActivity() {

    lateinit var chooseImage: ImageView
    private var encodedImage: String = ""
    private lateinit var bitmap: Bitmap
    private var fileUri: Uri? = null


    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var currentUserRef: DatabaseReference
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profileimage)



        chooseImage = findViewById(R.id.man1)

        // Set click listener for image selection
        chooseImage.setOnClickListener {
            openGalleryForImage()
        }
    }

    // Function to open gallery for image selection
    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    // Function to handle image selection result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            data.data?.let { imageUri ->
                fileUri = imageUri
                displaySelectedImage(imageUri)
                uploadImageToFirebaseStorage(imageUri)
            }
        }
    }

    // Function to display selected image
    private fun displaySelectedImage(imageUri: Uri) {
        val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        chooseImage.setImageBitmap(bitmap)
    }

    // Function to upload image to Firebase Storage
    private fun uploadImageToFirebaseStorage(imageUri: Uri) {
        val storage = Firebase.storage
        val storageRef = storage.reference

        // Create a reference to the location where you want to store the image
        val imagesRef = storageRef.child("images/${UUID.randomUUID()}")

        // Upload the image to Firebase Storage
        imagesRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // Get the download URL of the uploaded image
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->

                    auth = FirebaseAuth.getInstance()
                    database = FirebaseDatabase.getInstance()

                    // Get the current user's ID
                    userId = auth.currentUser?.uid ?: ""
                    // Store the download URL in Firebase Realtime Database
                    storeImageUrlInDatabase(uri, userId)
                }.addOnFailureListener { e ->
                    // Handle errors while getting the download URL
                    showToast("Error getting download URL: ${e.message}")
                }
            }
            .addOnFailureListener { e ->
                // Handle errors while uploading the image
                showToast("Error uploading image: ${e.message}")
            }
    }

    // Function to store image URL in Firebase Realtime Database under current user ID
    private fun storeImageUrlInDatabase(uri: Uri, userId: String) {
        val database = Firebase.database
        val userImagesRef = database.getReference("users").child(userId).child("images")

        // Generate a unique key for the image entry
        val key = userImagesRef.push().key ?: ""

        // Store the image URL under the generated key
        userImagesRef.child(key).setValue(uri.toString())
            .addOnSuccessListener {
                showToast("Image URL stored in database")
            }
            .addOnFailureListener { e ->
                showToast("Error storing image URL in database: ${e.message}")
            }
    }

    // Function to show toast message
    private fun showToast(message: String) {
        Toast.makeText(this, "display successfully", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val GALLERY_REQUEST_CODE = 1
    }
}