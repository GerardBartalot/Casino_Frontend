package com.example.casinoapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.casinoapp.entity.User
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
    var confirmPassword by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var isAdult by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF228B22))
        ) {
            Spacer(modifier = Modifier.height(200.dp))
            Text(
                text = "Register",
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
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "I confirm that I am 18 years or older")
                Checkbox(
                    checked = isAdult,
                    onCheckedChange = { isAdult = it }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    when {
                        !isAdult -> {
                            errorMessage = "You must be 18 years or older to register."
                        }
                        password != confirmPassword -> {
                            errorMessage = "Passwords do not match."
                        }
                        else -> {
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
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(50.dp)
                    .padding(vertical = 5.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Register")
            }

            Spacer(modifier = Modifier.height(25.dp))

            when (registerMessageUiState) {
                is RegisterMessageUiState.Loading -> {}
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
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Text(
                    text = "Already have an account?",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(
                    onClick = onNavigateToLogin
                ) {
                    Text(
                        text = "Login",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Blue
                    )
                }
            }
        }
    }
}
