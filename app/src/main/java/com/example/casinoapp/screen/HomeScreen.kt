package com.example.casinoapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    LaunchedEffect(loggedInUser) {
        loggedInUser?.userId?.let {
            gameViewModel.getUserFondoCoins(it.toInt())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF228B22))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(100.dp))

        Text(
            text = "Fondo Casino Royale",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Text(
            text = "Fondocoins: $vmFondocoins",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 30.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        HomeButton("Slot Machine") { onNavigateToSlotMachine() }

        Spacer(modifier = Modifier.height(30.dp))

        HomeButton("Roulette") { onNavigateToRoulette() }

        Spacer(modifier = Modifier.height(30.dp))

        HomeButton("Profile") { onNavigateToProfile() }
    }
}

@Composable
fun HomeButton(text: String, onNavigate: () -> Unit) {
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