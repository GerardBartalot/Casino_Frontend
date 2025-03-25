package com.example.casinoapp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.casinoapp.viewModel.User
import com.example.casinoapp.viewModel.RegisterMessageUiState
import com.example.casinoapp.viewModel.RemoteViewModel

@Composable
fun RegisterScreen(
    remoteViewModel: RemoteViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val registerMessageUiState by remoteViewModel.registerMessageUiState.collectAsState()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            Text(
                text = "Fondo Casino Royale",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 50.dp)
            )

            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = {
                val user = User(
                    userId = 0,
                    name = name,
                    username = username,
                    password = password,
                    fondocoins = 500,
                    experiencePoints = 0,
                    profilePicture = null
                )
                remoteViewModel.register(user) { resultMessage ->
                    if (resultMessage == "Registro exitoso") {
                        onNavigateToHome()
                    } else {
                        errorMessage = resultMessage
                    }
                }
            }) {
                Text("Register")
            }

            Spacer(modifier = Modifier.height(25.dp))

            when (registerMessageUiState) {
                is RegisterMessageUiState.Loading -> {
                }
                is RegisterMessageUiState.Success -> {
                    LaunchedEffect(Unit) {
                        onNavigateToHome()
                    }
                }
                is RegisterMessageUiState.Error -> {
                    Text("User could not be registered", color = Color.Red)
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Text(
                    text = "Already have an account?",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(
                    onClick = onNavigateToLogin
                ) {
                    Text("Login", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}