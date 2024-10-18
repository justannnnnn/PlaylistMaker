package com.example.playlistmaker.domain.sharing.model

data class EmailData(
    val rcpt: String,
    val subject: String,
    val text: String
)