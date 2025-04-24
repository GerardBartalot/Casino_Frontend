package com.example.casinoapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.casinoapp.R
import com.example.casinoapp.screen.games.PixelDisplay
import com.example.casinoapp.ui.components.ExperienceProgressBar
import com.example.casinoapp.viewModel.GameViewModel
import com.example.casinoapp.viewModel.RemoteViewModel
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.casinoapp.screen.profile.rememberImageFromBase64

@Composable
fun HomeScreen(
    remoteViewModel: RemoteViewModel,
    gameViewModel: GameViewModel,
    onNavigateToRoulette: () -> Unit,
    onNavigateToSlotMachine: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToScratchCard: () -> Unit,
) {

    val loggedInUser by remoteViewModel.loggedInUser.collectAsState()
    val vmFondocoins by gameViewModel.fondocoins.collectAsState()
    val vmExperience by gameViewModel.experience.collectAsState()
    val profileImage by remember { derivedStateOf {
        loggedInUser?.profilePicture?.takeIf { it.isNotEmpty() }
    } }
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

            GameButtonWithBackground(
                text = "",
                imageRes = R.drawable.slot_machine_img,
                enabled = true,
                onClick = onNavigateToSlotMachine
            )

            Spacer(modifier = Modifier.height(35.dp))

            GameButtonWithBackground(
                text = "",
                imageRes = R.drawable.scratch_cards_img,
                enabled = currentLevel >= 2,
                onClick = onNavigateToScratchCard,
                requiredLevel = 2
            )

            Spacer(modifier = Modifier.height(35.dp))

            GameButtonWithBackground(
                text = "",
                imageRes = R.drawable.roulette_img,
                enabled = currentLevel >= 5,
                onClick = onNavigateToRoulette,
                requiredLevel = 5
            )

            Spacer(modifier = Modifier.height(35.dp))

            GameButtonWithBackground(
                text = "",
                imageRes = R.drawable.black_jack_img,
                enabled = currentLevel >= 10,
                onClick = onNavigateToScratchCard,
                requiredLevel = 10
            )
        }
    }
}

@Composable
fun GameButtonWithBackground(
    text: String,
    imageRes: Int,
    enabled: Boolean,
    onClick: () -> Unit,
    requiredLevel: Int = 0
){
    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(105.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled) { onClick() }
            .border(
                width = 2.dp,
                color = Color.White,
                shape = RoundedCornerShape(10.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (enabled) 1f else 0.4f),
            contentScale = ContentScale.Crop
        )

        if (!enabled) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.lock),
                    contentDescription = "Locked",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Nivell $requiredLevel requerit",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }

        if (text.isNotEmpty()) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }

    @Composable
    fun rememberImageFromBase64(base64: String): ImageBitmap {
        val bitmap = remember(base64) {
            try {
                val imageBytes = Base64.decode(base64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    ?.asImageBitmap()
            } catch (e: Exception) {
                null
            }
        }

        return bitmap ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888).asImageBitmap()
    }
}