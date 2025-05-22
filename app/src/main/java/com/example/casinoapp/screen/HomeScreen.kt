package com.example.casinoapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.example.casinoapp.ui.components.LevelUpPopup
import com.example.casinoapp.viewModel.GameViewModel
import com.example.casinoapp.viewModel.RemoteViewModel
import kotlinx.coroutines.delay
import java.util.*
import java.util.concurrent.TimeUnit

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
    val loggedInUser by remoteViewModel.loggedInUser.collectAsStateWithLifecycle()
    val fondocoins by gameViewModel.fondocoins.collectAsStateWithLifecycle()
    val experience by gameViewModel.experience.collectAsStateWithLifecycle()
    val games by gameViewModel.games.collectAsStateWithLifecycle()
    val canClaim by gameViewModel.canClaimDailyReward.collectAsStateWithLifecycle()
    val lastRewardTime by gameViewModel.lastDailyReward.collectAsStateWithLifecycle()

    val profileImage by remember {
        derivedStateOf {
            loggedInUser?.profilePicture?.takeIf { it.isNotEmpty() }
        }
    }

    var timeLeftText by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val profileAnim by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.profile))
    val rewardAnim by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.daily_reward_anim))

    val currentLevel = (experience / 1000) + 1

    LaunchedEffect(loggedInUser?.userId) {
        loggedInUser?.userId?.let {
            gameViewModel.getUserFondoCoins(it)
            gameViewModel.getUserExperience(it)
            gameViewModel.getAllGames()
            gameViewModel.getLastDailyReward(it)
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

    val gradientBrush = Brush.verticalGradient(
        listOf(Color(0xFF1A1A1A), Color(0xFF2D2D2D), Color(0xFF1A1A1A)),
        startY = 0f, endY = 1000f
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
                .padding(start = 16.dp, top = 15.dp)
                .size(100.dp)
                .clip(CircleShape)
                .clickable(
                    enabled = canClaim,
                    onClick = {
                        loggedInUser?.userId?.let {
                            gameViewModel.claimDailyReward(it)
                        }
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (canClaim) {
                LottieAnimation(
                    composition = rewardAnim,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier.size(100.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF808080)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = timeLeftText,
                        color = Color.White,
                        style = TextStyle(fontSize = 10.sp)
                    )
                }
            }
        }

        // Botón de perfil
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 25.dp, start = 16.dp, end = 20.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .clickable { onNavigateToProfile() }
            ) {
                if (profileImage != null) {
                    Image(
                        bitmap = rememberImageFromBase64(profileImage!!),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .fillMaxSize()
                            .size(30.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    LottieAnimation(
                        composition = profileAnim,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier
                            .fillMaxSize()
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
            Spacer(modifier = Modifier.height(10.dp))

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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    PixelDisplay(fondocoins)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    ExperienceProgressBar(
                        currentXp = experience,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                games.sortedBy { it.levelUnlock }.forEach { game ->
                    val isEnabled = currentLevel >= game.levelUnlock && !game.gameName.equals("blackjack", ignoreCase = true)
                    val onClick: () -> Unit = when (game.gameName.lowercase(Locale.ROOT)) {
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
                        isBeta = game.gameName.equals("blackjack", ignoreCase = true)
                    )

                    Spacer(modifier = Modifier.height(35.dp))
                }
            }
        }

        // Popup de nivel (si usas uno personalizado)
        if (gameViewModel.showLevelUpPopup) {
            LevelUpPopup(
                currentLevel = gameViewModel.currentPopupLevel,
                allGames = games,
                onDismiss = { gameViewModel.dismissLevelUpPopup() }
            )
        }

    }
}

fun getImageResourceForGame(gameName: String): Int {
    return when (gameName.lowercase(Locale.ROOT)) {
        "escurabutxaques" -> R.drawable.slot_machine_img
        "rasca i guanya" -> R.drawable.scratch_cards_img
        "ruleta" -> R.drawable.roulette_img
        "blackjack" -> R.drawable.black_jack_img
        else -> R.drawable.slot_machine_img
    }
}
