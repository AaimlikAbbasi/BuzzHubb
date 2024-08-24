package com.example.buzzhub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class SearchScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_screen)
        val searchres : ImageView = findViewById(R.id.searchbuttonss)
        searchres.setOnClickListener{
            var search = findViewById<android.widget.EditText>(R.id.searchbar)

            val intent = Intent(this, SearchResults::class.java)
            intent.putExtra("search", search.text.toString())
            startActivity(intent)
        }

        val back : ImageView = findViewById(R.id.backbtn)
        back.setOnClickListener{
            val intent = Intent(this, Feed_Info::class.java)
            startActivity(intent)
        }
    }
}