package com.example.casinoapp.screen.auth

import android.app.DatePickerDialog
import android.widget.DatePicker
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
import java.util.*

@Composable
fun RegisterScreen(
    remoteViewModel: RemoteViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val context = LocalContext.current
    val registerMessageUiState by remoteViewModel.registerMessageUiState.collectAsState()

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
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            Image(
                painter = painterResource(id = R.drawable.logo_splash),
                contentDescription = "App Logo",
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.height(25.dp))
            Text(
                text = "Registre",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 50.dp),
                color = Color.White
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
            } ?: "Selecciona la teva data de naixement"

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
                modifier = Modifier.fillMaxWidth(0.8f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(birthDateText)
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color(0xFFFF5252))
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    val today = Calendar.getInstance()
                    val eighteenYearsAgo = Calendar.getInstance().apply {
                        add(Calendar.YEAR, -18)
                    }

                    when {
                        birthDate == null -> {
                            errorMessage = "Has de seleccionar la data de naixement."
                        }
                        birthDate!!.after(eighteenYearsAgo) -> {
                            errorMessage = "Has de tenir 18 anys o més per registrar-te."
                        }
                        password != confirmPassword -> {
                            errorMessage = "Les contrasenyes no coincideixen."
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
