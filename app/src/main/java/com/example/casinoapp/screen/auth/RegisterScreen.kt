package com.example.casinoapp.screen.auth

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.casinoapp.R
import com.example.casinoapp.entity.User
import com.example.casinoapp.viewModel.RegisterMessageUiState
import com.example.casinoapp.viewModel.RemoteViewModel
import java.text.SimpleDateFormat
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import java.util.*

@Composable
fun RegisterScreen(
    remoteViewModel: RemoteViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val context = LocalContext.current
    val registerMessageUiState by remoteViewModel.registerMessageUiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(registerMessageUiState) {
        when (registerMessageUiState) {
            is RegisterMessageUiState.Success -> {
                onNavigateToHome()
            }
            else -> {}
        }
    }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf<Calendar?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A1A1A),
            Color(0xFF2D2D2D),
            Color(0xFF1A1A1A)
        )
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
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            Image(
                painter = painterResource(id = R.drawable.logo_splash),
                contentDescription = "App Logo",
                modifier = Modifier.size(190.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Registre",
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFFFD700), Color(0xFFFFA500))
                    ),
                    shadow = Shadow(
                        color = Color(0xFFFFA500),
                        offset = Offset(3f, 3f),
                        blurRadius = 3f
                    )
                ),
                modifier = Modifier.padding(bottom = 50.dp)
            )

            // Campo nombre
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nom", color = Color.White) },
                colors = textFieldColors(),
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Campo usuario
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nom d'usuari", color = Color.White) },
                colors = textFieldColors(),
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Campo contraseña
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contrasenya", color = Color.White) },
                visualTransformation = PasswordVisualTransformation(),
                colors = textFieldColors(),
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Campo confirmar contraseña
            TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirma la contrasenya", color = Color.White) },
                visualTransformation = PasswordVisualTransformation(),
                colors = textFieldColors(),
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Selector de fecha de nacimiento
            val birthDateText = birthDate?.let {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it.time)
            } ?: "Data de naixement"

            Button(
                onClick = {
                    val calendar = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                            birthDate = Calendar.getInstance().apply {
                                set(year, month, dayOfMonth)
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF333333),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(60.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = "Seleccionar data de naixement",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = birthDateText,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (errorMessage.isNotEmpty() && errorMessage != "Registre exitós") {
                Text(text = errorMessage, color = Color(0xFFFF5252))
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    if (name.isBlank() || username.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                        errorMessage = "Tots els camps han d'estar omplerts."
                    } else if (birthDate == null) {
                        errorMessage = "Has de seleccionar la data de naixement."
                    } else {
                        val minimumDate = Calendar.getInstance().apply {
                            add(Calendar.YEAR, -18)
                        }

                        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val dateString = outputFormat.format(birthDate!!.time)

                        if (birthDate!!.after(minimumDate)) {
                            errorMessage = "Has de tenir 18 anys o més per registrar-te."
                        } else if (password != confirmPassword) {
                            errorMessage = "Les contrasenyes no coincideixen."
                        } else {
                            val user = User(
                                userId = 0,
                                name = name,
                                username = username,
                                password = password,
                                dateOfBirth = dateString,
                                fondocoins = 500,
                                experiencePoints = 0,
                                profilePicture = null
                            )
                            remoteViewModel.register(user) { resultMessage ->
                                if (resultMessage == "Registre exitós") {
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
                    "Registrar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Ja tens un compte?",
                    fontSize = 15.sp,
                    color = Color.White
                )
                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        text = "Inicia sessió ara!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFFFFD700)
                    )
                }
            }
        }
    }
}

@Composable
fun textFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color(0xFF333333),
    unfocusedContainerColor = Color(0xFF333333),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedLabelColor = Color(0xFFFFD700),
    unfocusedLabelColor = Color.LightGray,
    cursorColor = Color(0xFFFFD700),
    focusedIndicatorColor = Color(0xFFFFD700),
    unfocusedIndicatorColor = Color.Gray
)
