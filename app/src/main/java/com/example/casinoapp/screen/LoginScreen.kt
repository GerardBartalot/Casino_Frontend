package com.example.casinoapp.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.casinoapp.viewModel.LoginMessageUiState
import com.example.casinoapp.viewModel.RemoteViewModel

@Composable
fun LoginScreen(
    remoteViewModel: RemoteViewModel,
    onBackPressed: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToSearch: () -> Unit
) {

    val loginMessageUiState by remoteViewModel.loginMessageUiState.collectAsState()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize().padding(vertical = 200.dp)
        ) {
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
            Button(onClick = {
                remoteViewModel.loginUser(username, password) { resultMessage ->
                    if (resultMessage == "Login exitoso") {
                        Log.d("LoginScreen", "Login exitoso, navegando a SearchScreen")
                        onNavigateToSearch()
                    } else {
                        Log.e("LoginScreen", "Error en login: $resultMessage")
                    }
                }
            }) {
                Text("Login")
            }

            Spacer(modifier = Modifier.height(25.dp))
            when (loginMessageUiState) {
                is LoginMessageUiState.Loading -> {
                }
                is LoginMessageUiState.Success -> {
                    Text("Login successful", color = Color.Green)
                    Log.d("LoginScreen", "Inicio de sesión exitoso")
                        onNavigateToSearch()
                }
                is LoginMessageUiState.Error -> {
                    Text("Incorrect username or password", color = Color.Red)
                    Log.e("LoginScreen", "Usuario o contraseña incorrectos")
                }
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Don't have an account? ")
                TextButton(onClick = onNavigateToRegister) {
                    Text("Register here")
                }
            }
        }
    }
}