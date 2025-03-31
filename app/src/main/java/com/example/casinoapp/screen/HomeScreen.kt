package com.example.casinoapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.casinoapp.R
import com.example.casinoapp.viewModel.GameViewModel
import com.example.casinoapp.viewModel.RemoteViewModel

@Composable
fun HomeScreen(
    remoteViewModel: RemoteViewModel,
    gameViewModel: GameViewModel,
    onNavigateToRoulette: () -> Unit,
    onNavigateToSlotMachine: () -> Unit,
    onNavigateToProfile: () -> Unit,
) {

    val loggedInUser by remoteViewModel.loggedInUser.collectAsState()
    val vmFondocoins by gameViewModel.fondocoins.collectAsState()
    val vmExperience by gameViewModel.experience.collectAsState()
    val diamondXP by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.diamond_xp))

    LaunchedEffect(loggedInUser) {
        loggedInUser?.userId?.let {
            gameViewModel.getUserFondoCoins(it.toInt())
            gameViewModel.getUserExperience(it.toInt())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF228B22))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(80.dp))

        Image(
            painter = painterResource(id = R.drawable.logo_splash),
            contentDescription = "App Logo",
            modifier = Modifier.size(200.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Fondocoins
            Image(
                painter = painterResource(id = R.drawable.fondocoin),
                contentDescription = "Fondocoin",
                modifier = Modifier.size(70.dp)
            )
            Text(
                "$vmFondocoins",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 30.sp),
                modifier = Modifier.padding(start = 4.dp, end = 16.dp)
            )

            // Experiencia
            LottieAnimation(
                composition = diamondXP,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.size(70.dp)
            )
            Text(
                "$vmExperience",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 30.sp),
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        HomeButton("Slot Machine", enabled = true) { onNavigateToSlotMachine() }

        Spacer(modifier = Modifier.height(30.dp))

        HomeButton("Roulette", enabled = vmExperience >= 100) { onNavigateToRoulette() }

        Spacer(modifier = Modifier.height(30.dp))

        HomeButton("Profile", enabled = true) { onNavigateToProfile() }
    }
}

@Composable
fun HomeButton(text: String, enabled: Boolean, onNavigate: () -> Unit) {
    Button(
        onClick = onNavigate,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) Color(0xFFFFD700) else Color.Gray
        ),
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(50.dp)
            .padding(vertical = 5.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(text = text, color = Color.Black, fontSize = 16.sp)
    }
}