package com.example.buzzhub

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class Sign_up : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {



        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)



        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Check if user is already logged in



        val signupButton = findViewById<TextView>(R.id.loginButton)

        val spinner: Spinner = findViewById(R.id.GenderSpinner)
        val cities = arrayOf("Enter gender", "MALE","FEMALE") // Example data
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        auth = FirebaseAuth.getInstance()

        signupButton.setOnClickListener {

            val name=findViewById<EditText>(R.id.name).text.toString()
            val email = findViewById<TextView>(R.id.email).text.toString()
            val bio = findViewById<EditText>(R.id.BIO).text.toString()
            val detail=findViewById<EditText>(R.id.countrySpinner).text.toString()
            val gender=spinner.selectedItem.toString()
            val password = findViewById<TextView>(R.id.password).text.toString()


            // Validate email, password, city, country, and contact number fields
            if (email.isEmpty() || password.isEmpty() || gender == "Enter gender" || bio.isEmpty() || detail.isEmpty()) {
                Toast.makeText(this, "Please fill in all the required fields.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Sign up the user
            registerUser(name,email, password,bio,detail,gender)
          //  saveUserData(name,email,bio,detail,gender,password)
            registerUserserver(name, email, password, bio, detail, gender)


            val defaultImageUri = Uri.parse("android.resource://${packageName}/${R.drawable.girls}")

            val storageRef = FirebaseStorage.getInstance().reference


            val personName= name


            val filename = "${System.currentTimeMillis()}_dp.jpg"


            val folderRef = storageRef.child(personName)


            val fileRef = folderRef.child(filename)


            fileRef.putFile(defaultImageUri)
                .addOnSuccessListener {
                    // Image upload successful
                    Toast.makeText(this, "Default Image Uploaded", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    // Image upload failed
                    Toast.makeText(this, "Failed to Upload Default Image: $exception", Toast.LENGTH_SHORT).show()
                }







        }



        dbRef = FirebaseDatabase.getInstance().getReference("USER")



    }

    private fun registerUser(name:String,email: String, password: String, bio: String, detail: String, gender: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Registration successful
                    val user = auth.currentUser
                    val uid = user?.uid ?: ""

                    // Update user profile with additional information
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName("$gender")
                        .build()
                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { profileUpdateTask ->
                            if (profileUpdateTask.isSuccessful) {
                                Log.d("Registration", "User profile updated.")
                            } else {
                                Log.w("Registration", "Failed to update user profile.", profileUpdateTask.exception)
                            }
                        }

                    // Save user data in the Realtime Database
                    saveUserData(uid,name, email, bio, detail, gender, password)

                    Toast.makeText(this@Sign_up, "Sign up successful.", Toast.LENGTH_SHORT).show()
                } else {
                    // Registration failed
                    Log.w("Registration", "User registration failed", task.exception)
                    Toast.makeText(this@Sign_up, "Sign up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }





    private fun saveUserData(uid: String, name: String, email: String, bio: String, detail: String, gender: String, password: String) {

        // Create a reference to the current user's data in the database
        val currentUserRef = dbRef.child(uid)

        // Create an EmployeeModel object with the user's data
        val employee = EmployeeModel(uid, name, email, bio, detail, gender, password)

        // Set the value of the current user's data in the database
        currentUserRef.setValue(employee)
            .addOnCompleteListener {
                // Data insertion successful
                Toast.makeText(this, "Data inserted successfully", Toast.LENGTH_LONG).show()

                // Clear input fields
                findViewById<EditText>(R.id.name).text.clear()
                findViewById<EditText>(R.id.email).text.clear()
                findViewById<EditText>(R.id.BIO).text.clear()
                findViewById<EditText>(R.id.countrySpinner).text.clear()
                findViewById<EditText>(R.id.password).text.clear()

                // Set the selection of the city spinner to the default position
                val citySpinner = findViewById<Spinner>(R.id.GenderSpinner)
                val defaultCityIndex = 0 // Assuming "Enter gender" is the first item in the spinner
                citySpinner.setSelection(defaultCityIndex)
            }.addOnFailureListener { err ->
                // Data insertion failed
                Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun registerUserserver(name: String, email: String, password: String, bio: String, detail: String, gender: String) {
        val url = "http://192.168.18.135/Buzzhub/registration.php"
        val requestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                val jsonResponse = JSONObject(response)
                val message = jsonResponse.getString("message")
                val status = jsonResponse.getString("status")
                if (status == "1") {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                 // Create intent and pass user ID to the next activity
                    //----------------------------------------------------------
                    //----------------------------------------------------------

                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                // Log the error message with additional details
                if (error is VolleyError) {
                    if (error.networkResponse != null) {
                        val statusCode = error.networkResponse.statusCode
                        val errorMessage = String(error.networkResponse.data)
                        Log.e("Volley Error", "Error Code: $statusCode, Error Message: $errorMessage")
                    } else {
                        Log.e("Volley Error", "Unknown Volley Error: ${error.message}")
                    }
                } else {
                    Log.e("Volley Error", "Unknown Error: $error")
                }

                // Display a toast with the error message
                Toast.makeText(this, "Connection error. Please try again later.", Toast.LENGTH_SHORT).show()
            })
            {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["name"] = name
                params["email"] = email
                params["password"] = password
                params["Bio"] = bio
                params["Detail"] =detail
                params["Gender"] = gender
                return params
            }
        }
        requestQueue.add(stringRequest)
    }

    private fun navigateToHome(userId: Int) {
        val intent = Intent(this, Profile::class.java)
        intent.putExtra("currentid", userId)
        startActivity(intent)
        finish()
    }
    private fun setLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean("isLoggedIn", isLoggedIn).apply()
    }

}

data class EmployeeModel(
    val  empId: String = "",
    val  name: String = "",
    val  email: String = "",
    val  bio: String = "",
    val  detail: String = "",
    val gender: String = "",
    val  password: String = ""
)

