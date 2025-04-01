package com.example.casinoapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.casinoapp.R
import com.example.casinoapp.viewModel.RemoteViewModel

@Composable
fun ProfileScreen(
    remoteViewModel: RemoteViewModel,
    navController: NavController,
    onNavigateToProfile: () -> Unit,
    onNavigateToEditProfileScreen: () -> Unit,
) {
    val currentUser = remoteViewModel.loggedInUser.collectAsState().value
    val profile by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.profile))

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
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver atrás",
                        tint = Color.White
                    )
                }
            }

            LottieAnimation(
                composition = profile,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.size(300.dp)
            )

            currentUser?.let { user ->
                Text(
                    text = "Hola, ${user.name}!",
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "@${user.username}",
                    fontSize = 14.sp,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(60.dp))

                ProfileButton("Historial de partidas") { onNavigateToProfile() }
                Spacer(modifier = Modifier.height(30.dp))
                ProfileButton("Canviar contrasenya") { onNavigateToProfile() }
                Spacer(modifier = Modifier.height(30.dp))
                ProfileButton("Editar perfil") { onNavigateToEditProfileScreen() }

                Spacer(modifier = Modifier.height(120.dp))

                Button(
                    onClick = {
                        remoteViewModel.logout()
                        navController.navigate("loginScreen")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA52A2A)),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Tancar sessió", color = Color.White, fontSize = 16.sp)
                }
            } ?: run {
                Text(
                    text = "No hay usuario logueado",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun ProfileButton(text: String, onNavigate: () -> Unit) {
    Button(
        onClick = onNavigate,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(50.dp)
            .padding(vertical = 5.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(text = text, color = Color.Black, fontSize = 16.sp)
    }
}