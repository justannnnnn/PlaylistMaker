package com.example.playlistmaker.ui.player.model

import androidx.annotation.DrawableRes
import com.example.playlistmaker.R

sealed class PlayerState(
    val isPlayButtonEnabled: Boolean,
    @DrawableRes val buttonIcon: Int,
    val progress: String,
    val isFavorite: Boolean = false
) {
    class Default(
        isFavorite: Boolean = false
    ) : PlayerState(
        false,
        R.drawable.play,
        "00:00",
        isFavorite
    )

    class Prepared(
        isFavorite: Boolean = false
    ) : PlayerState(
        true,
        R.drawable.play,
        "00:00",
        isFavorite
    )

    class Playing(
        progress: String,
        isFavorite: Boolean = false
    ) : PlayerState(
        true,
        R.drawable.pause,
        progress,
        isFavorite
    )

    class Paused(
        progress: String,
        isFavorite: Boolean = false
    ) : PlayerState(
        true,
        R.drawable.play,
        progress,
        isFavorite
    )
}