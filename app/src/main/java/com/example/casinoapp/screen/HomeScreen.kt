package com.example.casinoapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.casinoapp.R
import com.example.casinoapp.screen.games.PixelDisplay
import com.example.casinoapp.screen.profile.rememberImageFromBase64
import com.example.casinoapp.ui.components.ExperienceProgressBar
import com.example.casinoapp.ui.components.GameButtonsHome
import com.example.casinoapp.ui.components.LevelUpPopup
import com.example.casinoapp.viewModel.GameViewModel
import com.example.casinoapp.viewModel.RemoteViewModel
import java.util.Locale

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

    val loggedInUser by remoteViewModel.loggedInUser.collectAsState()
    val vmFondocoins by gameViewModel.fondocoins.collectAsState()
    val vmExperience by gameViewModel.experience.collectAsState()
    val games by gameViewModel.games.collectAsState()
    val profileImage by remember {
        derivedStateOf {
            loggedInUser?.profilePicture?.takeIf { it.isNotEmpty() }
        }
    }
    val profile by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.profile))
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

    LaunchedEffect(loggedInUser) {
        loggedInUser?.userId?.let {
            gameViewModel.getUserFondoCoins(it.toInt())
            gameViewModel.getUserExperience(it.toInt())
            gameViewModel.getAllGames()
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
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopEnd)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x30FFFFFF),
                            Color(0x00FFFFFF)
                        ),
                        radius = 300f
                    )
                )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 20.dp, bottom = 16.dp, top =45.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .clickable { onNavigateToProfile() }
            )
            {
                if (profileImage != null) {
                    Image(
                        bitmap = rememberImageFromBase64(profileImage!!),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    LottieAnimation(
                        composition = profile,
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
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Fondoscoins
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

            games.sortedBy { it.levelUnlock }.forEach { game ->
                val isEnabled = currentLevel >= game.levelUnlock
                val onClick = when (game.gameName.lowercase(Locale.ROOT)) {
                    "escurabutxaques" -> onNavigateToSlotMachine
                    "rasca i guanya" -> onNavigateToScratchCard
                    "ruleta" -> onNavigateToRoulette
                    "blackjack" -> onNavigateToBlackJack
                    else -> { {} }
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

fun getImageResourceForGame(gameName: String): Int {
    return when (gameName.lowercase(Locale.ROOT)) {
        "escurabutxaques" -> R.drawable.slot_machine_img
        "rasca i guanya" -> R.drawable.scratch_cards_img
        "ruleta" -> R.drawable.roulette_img
        "blackjack" -> R.drawable.black_jack_img
        else -> R.drawable.slot_machine_img // default image
    }
}
