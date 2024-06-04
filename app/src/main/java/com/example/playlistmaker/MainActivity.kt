package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // hide navigation bar for best design
        getWindow().
        getDecorView().
        setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        val searchButton = findViewById<Button>(R.id.searchButton)
        val mediaButton = findViewById<Button>(R.id.mediaButton)
        val settingButton = findViewById<Button>(R.id.settingButton)

        searchButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Click on SEARCH", Toast.LENGTH_LONG).show()
        }
        val searchButtonClickListener: View.OnClickListener = object: View.OnClickListener{
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Click on SEARCH", Toast.LENGTH_LONG).show()
            }
        }
        searchButton.setOnClickListener(searchButtonClickListener)

        mediaButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Click on MEDIA", Toast.LENGTH_LONG).show()
        }
        val mediaButtonClickListener: View.OnClickListener = object: View.OnClickListener{
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Click on MEDIA", Toast.LENGTH_LONG).show()
            }
        }
        mediaButton.setOnClickListener(mediaButtonClickListener)

        settingButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Click on SETTINGS", Toast.LENGTH_LONG).show()
        }


    }
}