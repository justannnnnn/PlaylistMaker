package com.example.playlistmaker.ui.media.playlists.new_playlist.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.Coil
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.domain.favorites.playlists.model.Playlist
import com.example.playlistmaker.ui.media.playlists.new_playlist.view_model.NewPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment : Fragment() {

    private lateinit var binding: FragmentNewPlaylistBinding
    lateinit var confirmDialog: MaterialAlertDialogBuilder

    private val viewModel: NewPlaylistViewModel by viewModel<NewPlaylistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            handleBackPress()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                handleBackPress()
            }
        })

        var coverPath = ""
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    val request = ImageRequest.Builder(requireContext())
                        .data(uri)
                        .transformations(RoundedCornersTransformation(80f))
                        .target { drawable ->
                            binding.photoPickupLL.background = drawable
                            binding.placeholderIV.isVisible = false
                            saveImageToPrivateStorage(uri)?.let {
                                coverPath = it
                            }
                        }
                        .build()
                    Coil.imageLoader(requireContext()).enqueue(request)
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }
        binding.photoPickupLL.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.nameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.createPlaylistButton.isEnabled = s.isNullOrEmpty().not()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        binding.createPlaylistButton.setOnClickListener {
            viewModel.onCreateButtonClicked(
                Playlist(
                    playlistId = 0,
                    playlistName = binding.nameInput.text.toString(),
                    playlistDescription = binding.descInput.text.toString(),
                    coverPath = coverPath,
                    tracks = emptyList(),
                    countTracks = 0
                )
            )
            findNavController().navigateUp()
            Toast.makeText(
                requireContext(),
                getString(R.string.playlist_created, binding.nameInput.text.toString()),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun handleBackPress(){
        if (binding.photoPickupLL.background == requireContext().getDrawable(R.drawable.add_photo_bg) ||
            binding.nameInput.text.isNullOrEmpty().not() ||
            binding.descInput.text.isNullOrEmpty().not()
        ) {
            confirmDialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle(requireContext().getString(R.string.finish_creation))
                .setMessage(requireContext().getString(R.string.finish_creation_desc))
                .setNeutralButton(requireContext().getString(R.string.cancel)) { dialog, which ->
                }
                .setNegativeButton(requireContext().getString(R.string.finish)) { dialog, which ->
                    findNavController().navigateUp()
                    dialog.dismiss()
                }
            confirmDialog.show()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri): String? {
        val filePath =
            File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, "playlist_${System.currentTimeMillis()}.jpg")
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        inputStream?.close()
        outputStream.close()
        return file.absolutePath
    }

    companion object {
        fun newInstance() = NewPlaylistFragment()
    }

}