package com.example.casinoapp.screen.auth

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.casinoapp.R
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

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A1A1A),
            Color(0xFF2D2D2D),
            Color(0xFF1A1A1A)
        ),
        startY = 0f,
        endY = 1000f
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
    ) {
        Image(
            painter = painterResource(id = R.drawable.subtle_texture),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.1f),
            contentScale = ContentScale.Crop
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            Image(
                painter = painterResource(id = R.drawable.logo_splash),
                contentDescription = "App Logo",
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.height(25.dp))
            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 50.dp),
                color = Color.White
            )
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username", color = Color.White) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF333333),
                    unfocusedContainerColor = Color(0xFF333333),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color(0xFFFFD700),
                    unfocusedLabelColor = Color.LightGray,
                    cursorColor = Color(0xFFFFD700),
                    focusedIndicatorColor = Color(0xFFFFD700),
                    unfocusedIndicatorColor = Color.Gray
                ),
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = Color.White) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF333333),
                    unfocusedContainerColor = Color(0xFF333333),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color(0xFFFFD700),
                    unfocusedLabelColor = Color.LightGray,
                    cursorColor = Color(0xFFFFD700),
                    focusedIndicatorColor = Color(0xFFFFD700),
                    unfocusedIndicatorColor = Color.Gray
                ),
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
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD700),
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(60.dp)
                    .padding(vertical = 5.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    "Login",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            when (loginMessageUiState) {
                is LoginMessageUiState.Success -> {
                }
                is LoginMessageUiState.Error -> {
                    Text("Incorrect username or password", color = Color(0xFFFF5252))
                }
                is LoginMessageUiState.Loading -> {
                }
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Don't have an account?",
                    fontSize = 15.sp,
                    color = Color.White
                )
                TextButton(onClick = onNavigateToRegister) {
                    Text(
                        text = "Register now!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFFFFD700)
                    )
                }
            }
        }
    }
}