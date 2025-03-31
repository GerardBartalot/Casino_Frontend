package com.example.casinoapp.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.casinoapp.viewModel.LoginMessageUiState
import com.example.casinoapp.viewModel.RemoteViewModel

@Composable
fun LoginScreen(
    remoteViewModel: RemoteViewModel,
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val loginMessageUiState by remoteViewModel.loginMessageUiState.collectAsState()
    val loggedInUser by remoteViewModel.loggedInUser.collectAsState(initial = null)

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(loggedInUser) {
        loggedInUser?.let {
            onNavigateToHome()
        }
    }

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
                text = "Login",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 50.dp)
            )
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
            Button(
                onClick = {
                    remoteViewModel.login(username, password) { resultMessage ->
                        if (resultMessage == "Login exitoso") {

                        } else {
                            Log.e("LoginScreen", "Error en login: $resultMessage")
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
                Text("Login")
            }

            Spacer(modifier = Modifier.height(25.dp))

            when (loginMessageUiState) {
                is LoginMessageUiState.Success -> {
                }
                is LoginMessageUiState.Error -> {
                    Text("Incorrect username or password", color = Color.Red)
                }
                is LoginMessageUiState.Loading -> {
                }
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Don't have an account? ")
                TextButton(
                    onClick = onNavigateToRegister) {
                        Text(
                            text = "Register now!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Blue
                        )
                    }
            }
        }
    }
}