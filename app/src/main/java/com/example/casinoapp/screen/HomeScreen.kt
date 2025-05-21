package com.example.casinoapp.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.*
import com.example.casinoapp.R
import com.example.casinoapp.screen.games.PixelDisplay
import com.example.casinoapp.screen.profile.rememberImageFromBase64
import com.example.casinoapp.ui.components.ExperienceProgressBar
import com.example.casinoapp.ui.components.GameButtonsHome
import com.example.casinoapp.viewModel.GameViewModel
import com.example.casinoapp.viewModel.RemoteViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.text.toIntOrNull

@Composable
fun HomeScreen(
    remoteViewModel: RemoteViewModel,
    gameViewModel: GameViewModel,
    onNavigateToRoulette: () -> Unit,
    onNavigateToSlotMachine: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToScratchCard: () -> Unit,
    onNavigateToBlackJack: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val loggedInUser by remoteViewModel.loggedInUser.collectAsStateWithLifecycle()
    val vmFondocoins by gameViewModel.fondocoins.collectAsStateWithLifecycle()
    val vmExperience by gameViewModel.experience.collectAsStateWithLifecycle()
    val games by gameViewModel.games.collectAsStateWithLifecycle()
    val canClaimDailyReward by gameViewModel.canClaimDailyReward.collectAsStateWithLifecycle()
    val lastRewardTime by gameViewModel.lastDailyReward.collectAsStateWithLifecycle()
    val canClaim by gameViewModel.canClaimDailyReward.collectAsStateWithLifecycle()
    var timeLeftText by remember { mutableStateOf("") }

    val profileImage by remember {
        derivedStateOf {
            loggedInUser?.profilePicture?.takeIf { it.isNotEmpty() }
        }
    }

    val profileAnimation by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.profile))
    val dailyRewardAnimation by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.daily_reward_anim))
    val currentLevel = (vmExperience / 1000) + 1

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A1A1A),
            Color(0xFF2D2D2D),
            Color(0xFF1A1A1A)
        ),
        startY = 0f,
        endY = 1000f
    )

    // Efecto para cargar los datos del usuario cuando se inicia sesión
    LaunchedEffect(loggedInUser?.userId) {
        loggedInUser?.userId?.let { userId ->
            gameViewModel.getUserFondoCoins(userId)
            gameViewModel.getUserExperience(userId)
            gameViewModel.getAllGames()
            gameViewModel.getLastDailyReward(userId)
        }
    }
    LaunchedEffect(lastRewardTime, canClaim) {
        if (!canClaim && lastRewardTime != null) {
            while (!canClaim) {
                val elapsed = System.currentTimeMillis() - lastRewardTime!!
                val remaining = TimeUnit.HOURS.toMillis(24) - elapsed

                if (remaining > 0) {
                    val totalSeconds = remaining / 1000
                    val hours = totalSeconds / 3600
                    val minutes = (totalSeconds % 3600) / 60
                    val seconds = totalSeconds % 60

                    timeLeftText = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                } else {
                    timeLeftText = "00:00:00"
                    break
                }

                delay(1000)
            }
        } else {
            timeLeftText = ""
        }
    }


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

        // Efecto de luz radial decorativo
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopEnd)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0x30FFFFFF), Color(0x00FFFFFF)),
                        radius = 300f
                    )
                )
        )

        // Botón de recompensa diaria
        Box(
            modifier = Modifier
                .padding(start = 16.dp, top = 17.dp)
                .size(100.dp) // tamaño unificado
                .clip(CircleShape)
                .clickable(
                    enabled = canClaim,
                    onClick = {
                        loggedInUser?.userId?.let { userId ->
                            gameViewModel.claimDailyReward(userId)
                        }
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (canClaim) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimation(
                        composition = dailyRewardAnimation,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier
                            .size(100.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .padding(top = 5.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF808080)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = timeLeftText,
                        color = Color.White,
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }

        // Botón de perfil
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 20.dp, bottom = 16.dp, top = 45.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .clickable { onNavigateToProfile() }
            ) {
                if (profileImage != null) {
                    Image(
                        bitmap = rememberImageFromBase64(profileImage!!),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    LottieAnimation(
                        composition = profileAnimation,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Image(
                painter = painterResource(id = R.drawable.logo_splash),
                contentDescription = "App Logo",
                modifier = Modifier.size(180.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Fondocoins
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    PixelDisplay(vmFondocoins)
                }

                // Barra de experiencia
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    ExperienceProgressBar(
                        currentXp = vmExperience,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Lista de juegos
            games.sortedBy { it.levelUnlock }.forEach { game ->
                val isEnabled = currentLevel >= game.levelUnlock
                val onClick = when (game.gameName.lowercase(Locale.ROOT)) {
                    "escurabutxaques" -> onNavigateToSlotMachine
                    "rasca i guanya" -> onNavigateToScratchCard
                    "ruleta" -> onNavigateToRoulette
                    "blackjack" -> onNavigateToBlackJack
                    else -> ({})
                }

                GameButtonsHome(
                    imageRes = getImageResourceForGame(game.gameName),
                    enabled = isEnabled,
                    onClick = onClick,
                    requiredLevel = game.levelUnlock,
                    isBeta = game.gameName.equals("Blackjack", ignoreCase = true)
                )

                Spacer(modifier = Modifier.height(35.dp))
            }
        }
    }
}

// Asocia nombres de juegos con sus imágenes
fun getImageResourceForGame(gameName: String): Int {
    return when (gameName.lowercase(Locale.ROOT)) {
        "escurabutxaques" -> R.drawable.slot_machine_img
        "rasca i guanya" -> R.drawable.scratch_cards_img
        "ruleta" -> R.drawable.roulette_img
        "blackjack" -> R.drawable.black_jack_img
        else -> R.drawable.slot_machine_img
    }
}
