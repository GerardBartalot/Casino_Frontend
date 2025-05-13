package com.example.casinoapp.entity

data class GameSession(
    val user: User,
    val game: Game,
    val rounds: Int,
    val experienceEarned: Int,
    val fondocoinsSpent: Int,
    val fondocoinsEarned: Int
)