package com.example.playlistmaker.ui.player.model

import androidx.annotation.DrawableRes
import com.example.playlistmaker.R

sealed class PlayerState(val isPlayButtonEnabled: Boolean, @DrawableRes val buttonIcon: Int, val progress: String) {
    class Default: PlayerState(false, R.drawable.play, "00:00")
    class Prepared: PlayerState(true, R.drawable.play, "00:00")
    class Playing(progress: String): PlayerState(true, R.drawable.pause, progress)
    class Paused(progress: String): PlayerState(true, R.drawable.play, progress)
}