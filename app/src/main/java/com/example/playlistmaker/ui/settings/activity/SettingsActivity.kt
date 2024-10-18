package com.example.playlistmaker.ui.settings.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {
    private lateinit var viewModel: SettingsViewModel
    private lateinit var binding: ActivitySettingsBinding

    private lateinit var settingsInteractor: SettingsInteractor
    private lateinit var sharingInteractor: SharingInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingsInteractor = Creator.provideSettingsInteractor()
        sharingInteractor = Creator.provideSharingInteractor()
        viewModel = ViewModelProvider(this, SettingsViewModel.getViewModelFactory(sharingInteractor, settingsInteractor))[SettingsViewModel::class.java]

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