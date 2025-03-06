package com.example.casinoapp.viewModel

data class User(
    val userId: Int,
    val name: String,
    val username: String,
    val password: String,
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