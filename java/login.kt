package com.example.buzzhub

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject

class login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharedPreferences = getSharedPreferences("YourSharedPreferencesName", Context.MODE_PRIVATE)

        val emailEditText = findViewById<EditText>(R.id.email)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signupText = findViewById<TextView>(R.id.signupText)
        auth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            loginuser(email,password)
            // Check if email and password fields are not empty
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(baseContext, "Please enter both email and password.",
                    Toast.LENGTH_SHORT).show()
            } else {
                // Proceed with sign-in if fields are not empty
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val user = auth.currentUser

                            ///new code for the phone i am writing check this out
                            val currentUser = FirebaseAuth.getInstance().currentUser

                            //till here
                            startActivity(Intent(this, Feed_Info::class.java))
                            finish()
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }


        // Set click listener for "Forget Your Password?" TextView

        signupText.setOnClickListener {
            startActivity(Intent(this, Sign_up::class.java))
        }

        // Check if user is already signed in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, Profile::class.java))
            finish()
        }


    }


    private fun loginuser(txtEmail: String, txtPassword: String) {
        val url = "http://192.168.18.135/Buzzhub/login.php"
        val requestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                val jsonResponse = JSONObject(response)
                val message = jsonResponse.getString("message")
                val status = jsonResponse.getString("status")
                if (status == "1") {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    val userId = jsonResponse.getInt("ID") // Retrieve user ID from response
                    setLoggedIn(true) // Set logged in status
                    navigateToHome()
                    // Create intent and pass user ID to the next activity
                    //----------------------------------------------------------
                    //----------------------------------------------------------
                    intent.putExtra("currentid", userId)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["email"] = txtEmail
                params["password"] = txtPassword
                return params
            }
        }
        requestQueue.add(stringRequest)
    }


    private fun setLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean("isLoggedIn", isLoggedIn).apply()
    }
    private fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }


    private fun navigateToHome() {
        startActivity(Intent(this, Feed_Info::class.java))
        finish()
    }

}