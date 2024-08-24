package com.example.buzzhub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class popup_menu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popup_menu)

        intent.getStringExtra("recieverid")
        intent.getStringExtra("senderid")
        intent.getStringExtra("time")
        intent.getStringExtra("message")

        val delete= findViewById<Button>(R.id.deletebtn)
        val edit = findViewById<Button>(R.id.editbtn)

        val editmessage = findViewById<EditText>(R.id.editmessage)
        editmessage.setText(intent.getStringExtra("message"))
        val db = Firebase.database.reference.child("Chat")
        delete.setOnClickListener {
            val intentTime = intent.getStringExtra("time")
            db.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (childSnapshot in dataSnapshot.children) {
                        val time = childSnapshot.child("time").getValue(String::class.java)
                        if (time == intentTime) {
                            childSnapshot.ref.removeValue() // Remove the entire node
                            break
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error
                }
            })
            val intent = Intent(this, ChatMentor::class.java)
            startActivity(intent)
        }

        edit.setOnClickListener {
            val intentTime = intent.getStringExtra("time")
            db.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (childSnapshot in dataSnapshot.children) {
                        val time = childSnapshot.child("time").getValue(String::class.java)
                        if (time == intentTime) {
                            childSnapshot.ref.child("message").setValue(editmessage.text.toString())
                            break
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error
                }
            })
            finish()
        }



    }
}