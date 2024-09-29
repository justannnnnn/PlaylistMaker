package com.example.playlistmaker.presentation.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.ThemeInteractor
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    private lateinit var switcher: SwitchMaterial
    private lateinit var themeInteractor: ThemeInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        switcher = findViewById(R.id.themeSwitcher)
        themeInteractor = Creator.provideThemeInteractor()

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

        initializationSwitch()
        switcher.isChecked = themeInteractor.isDarkTheme()
    }

    private fun initializationSwitch(){
        switcher.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked != themeInteractor.isDarkTheme()){
                themeInteractor.setTheme(isChecked)
                themeInteractor.applyTheme(object: ThemeInteractor.ThemeConsumer{
                    override fun consume(isDark: Boolean) {
                        AppCompatDelegate.setDefaultNightMode(
                            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
                            else AppCompatDelegate.MODE_NIGHT_NO
                        )
                    }
                })
                recreate()
            }
        }
    }
}