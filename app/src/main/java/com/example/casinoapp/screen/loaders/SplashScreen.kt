package com.example.casinoapp.screen.loaders

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.casinoapp.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.fichas_loading))

    LaunchedEffect(Unit) {
        delay(3000)
        navController.navigate("loginScreen") {
            popUpTo("splashScreen") { inclusive = true }
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
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(150.dp))

            Image(
                painter = painterResource(id = R.drawable.logo_splash),
                contentDescription = "App Logo",
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(150.dp))
        }
    }
}