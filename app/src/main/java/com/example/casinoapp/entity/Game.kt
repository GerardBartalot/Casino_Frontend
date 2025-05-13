package com.example.casinoapp.entity

data class Game (
    val gameId: Int,
    val gameName: String,
    val gameDescription: String? = null,
    val levelUnlock: Int = 0,
    val gameImg: String? = null,
)
