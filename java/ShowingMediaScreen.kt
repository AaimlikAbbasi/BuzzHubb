package com.example.buzzhub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide

class ShowingMediaScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showing_media_screen)
        val back = findViewById<ImageView>(R.id.backbtn)
        back.setOnClickListener {
            Intent(this, Feed_Info::class.java)
            finish()
        }
        val image = findViewById<ImageView>(R.id.mediashowimage)
      //  Glide.with.load(image, intent.getStringExtra("imageUrl").into(image))
        Glide.with(this).load(intent.getStringExtra("imageUrl")).into(image)

    }
}