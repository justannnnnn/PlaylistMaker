package com.example.playlistmaker.ui.media.liked.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentLikedBinding
import com.example.playlistmaker.ui.media.liked.view_model.LikedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LikedFragment: Fragment() {

    private val likedViewModel: LikedViewModel by viewModel<LikedViewModel>()
    private lateinit var binding: FragmentLikedBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLikedBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object{
        fun newInstance() = LikedFragment()
    }
}