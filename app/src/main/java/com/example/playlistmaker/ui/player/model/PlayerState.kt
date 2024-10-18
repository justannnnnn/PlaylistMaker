package com.example.playlistmaker.ui.player.model

sealed interface PlayerState {
    data object Default: PlayerState
    data object Prepared: PlayerState
    data object Playing: PlayerState
    data object Paused: PlayerState
}