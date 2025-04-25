package com.example.casinoapp.screen.loaders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.casinoapp.R
import com.example.casinoapp.viewModel.RemoteViewModel
import kotlinx.coroutines.delay

@Composable
fun LoadingHistoryScreen(
    remoteViewModel: RemoteViewModel,
    navController: NavController
) {
    val gameHistory by remoteViewModel.gameHistory.collectAsState()
    val currentUser = remoteViewModel.loggedInUser.collectAsState().value

    LaunchedEffect(currentUser) {
        currentUser?.let {
            remoteViewModel.getUserGameHistory(it.userId) { result ->
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.cartas_loading))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Carregant historial de partides...", color = Color.White)

        }
    }

    LaunchedEffect(gameHistory) {
        if (gameHistory.isNotEmpty()) {
            delay(1500)
            navController.navigate("historyScreen") {
                popUpTo("loadingToHistoryScreen") { inclusive = true }
            }
        } else {
            delay(1500)
            navController.navigate("historyScreen") {
                popUpTo("loadingToHistoryScreen") { inclusive = true }
            }
        }

    }
}
