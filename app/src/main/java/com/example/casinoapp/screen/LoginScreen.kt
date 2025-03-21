package com.example.casinoapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.casinoapp.R
import com.example.casinoapp.viewModel.LoginMessageUiState
import com.example.casinoapp.viewModel.RemoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavHostController,
    remoteViewModel: RemoteViewModel
) {
    val loginMessageUiState by remoteViewModel.loginMessageUiState.collectAsState()

    var user by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var connectMessage by remember { mutableStateOf(false) }

    LaunchedEffect(loginMessageUiState) {
        if (loginMessageUiState is LoginMessageUiState.Success) {
            navController.navigate("homeScreen")
        } else if (loginMessageUiState is LoginMessageUiState.Error) {
            errorMessage = "Login failed. Please check your username or password."
            connectMessage = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF228B22))) {
        TopAppBar(
            title = { Text("Inici de sessió", color = Color.White) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFB22222))
        )


        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(35.dp)
                .clip(RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_splash),
                    contentDescription = "Logo de la app",
                    modifier = Modifier
                        .size(150.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                OutlinedTextField(
                    value = user,
                    onValueChange = { user = it },
                    label = { Text(text = "Username", color = Color.Gray) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Username")
                    },
                )


                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(text = "Password", color = Color.Gray) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = "Password")
                    },
                )


                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (user.isBlank() || password.isBlank()) {
                            errorMessage = "Please fill in all fields."
                        } else {
                            errorMessage = ""
                            remoteViewModel.login(user, password, navController.context)
                            connectMessage = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                ) {
                    Text(text = "Login", fontSize = 18.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (connectMessage && loginMessageUiState is LoginMessageUiState.Loading) {
                    Text(
                        text = "Connecting...",
                        style = TextStyle(color = Color.Blue, fontSize = 16.sp),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Don't have an account yet? Click below to register",
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(textAlign = TextAlign.Center, color = Color.White)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { navController.navigate("registerScreen") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                ) {
                    Text(text = "Register", fontSize = 18.sp, color = Color.White)
                }
            }
        }
    }
}