package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        val sharedPrefs = getSharedPreferences(App.PLAYLIST_PREFERENCES, MODE_PRIVATE)
        themeSwitcher.isChecked = sharedPrefs.getBoolean(App.IS_DARK_THEME, false)
        themeSwitcher.setOnCheckedChangeListener { switcher, isChecked ->
            (applicationContext as App).switchTheme(isChecked)
            sharedPrefs.edit()
                .putBoolean(App.IS_DARK_THEME, isChecked)
                .apply()
        }


        val shareButton = findViewById<Button>(R.id.shareButton)
        shareButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message))
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, null))
        }

        val supportButton = findViewById<Button>(R.id.supportButton)
        supportButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_mail)))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_mail_subject))
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_mail_message))
            startActivity(intent)
        }

        val aggreementButton = findViewById<Button>(R.id.agreementButton)
        aggreementButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(getString(R.string.agreement_uri))
            startActivity(intent)
        }
    }
}