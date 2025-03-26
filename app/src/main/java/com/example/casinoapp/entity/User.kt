package com.example.casinoapp.entity

data class User(
    val userId: Int,
    val name: String,
    val username: String,
    val password: String,
    val fondocoins: Int,
    val experiencePoints: Int,
    val profilePicture: String?,
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val username: String,
    val password: String,
)