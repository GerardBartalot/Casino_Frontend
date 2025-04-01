package com.example.casinoapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
            Spacer(modifier = Modifier.height(150.dp))
            Text(
                text = "Register",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 50.dp),
                color = Color.White
            )

            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name", color = Color.White) },
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
                visualTransformation = PasswordVisualTransformation(),
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
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password", color = Color.White) },
                visualTransformation = PasswordVisualTransformation(),
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "I confirm that I am 18 years or older",
                    color = Color.White
                )
                Checkbox(
                    checked = isAdult,
                    onCheckedChange = { isAdult = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFFFFD700),
                        uncheckedColor = Color.LightGray,
                        checkmarkColor = Color.Black
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color(0xFFFF5252))
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
                    "Register",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
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
                    Text("User could not be registered", color = Color(0xFFFF5252))
                }
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Already have an account?",
                    fontSize = 15.sp,
                    color = Color.White
                )
                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        text = "Login now!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFFFFD700)
                    )
                }
            }
        }
    }
}