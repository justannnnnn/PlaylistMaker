package com.example.playlistmaker.ui.settings.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {
    private val viewModel by viewModel<SettingsViewModel>()

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.backButton.setOnClickListener {
            finish()
        }


        binding.shareButton.setOnClickListener {
            startActivity(viewModel.shareApp())
        }

        binding.supportButton.setOnClickListener {
            startActivity(viewModel.openSupport())
        }

        binding.agreementButton.setOnClickListener {
            startActivity(viewModel.openAgreement())
        }

        initializationSwitch()
    }

    private fun initializationSwitch(){
        binding.themeSwitcher.isChecked = viewModel.getTheme()
        binding.themeSwitcher.setOnCheckedChangeListener{_, isChecked ->
            viewModel.setTheme(isChecked)
            recreate()
        }
    }
}