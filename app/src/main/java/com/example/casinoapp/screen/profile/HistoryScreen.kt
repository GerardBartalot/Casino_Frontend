package com.example.casinoapp.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.casinoapp.R
import com.example.casinoapp.viewModel.RemoteViewModel
import com.example.casinoapp.entity.GameSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    remoteViewModel: RemoteViewModel,
    navController: NavController
) {
    val currentUser = remoteViewModel.loggedInUser.collectAsState().value
    val gameHistory by remoteViewModel.gameHistory.collectAsState()
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A1A1A),
            Color(0xFF2D2D2D),
            Color(0xFF1A1A1A)
        ),
        startY = 0f,
        endY = 1000f
    )

    LaunchedEffect(currentUser) {
        currentUser?.let {
            remoteViewModel.getUserGameHistory(it.userId) { result ->
            }
        }
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .height(100.dp)
                    .background(
                        brush = gradientBrush,
                        alpha = 0.7f
                    ),
                title = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "Historial de partides",
                            color = Color.White,
                            modifier = Modifier.padding(start = 0.dp)
                        )
                    }
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier.fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = {
                                navController.navigate("profileScreen") {
                                    popUpTo("historyScreen") { inclusive = true }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
                .padding(top = 60.dp)
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
                    .padding(16.dp)
                    .padding(bottom = 50.dp)
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                        .padding(horizontal = 8.dp)
                ) {
                    val last10Games = gameHistory.takeLast(10).reversed()

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(last10Games) { gameSession ->
                            CompactGameSessionItem(gameSession)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompactGameSessionItem(gameSession: GameSession) {
    val imageResId = when (gameSession.gameName) {
        "Slot Machine" -> R.drawable.slot_machine_img
        "Scratch Card" -> R.drawable.scratch_cards_img
        "Roulette" -> R.drawable.roulette_img
        "Black Jack" -> R.drawable.black_jack_img
        else -> R.drawable.subtle_texture
    }
    val diagonalCutShape = GenericShape { size, _ ->
        moveTo(0f, 0f)
        lineTo(size.width * 0.85f, 0f)
        lineTo(size.width, size.height)
        lineTo(0f, size.height)
        close()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(4.dp)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(2f)
                .height(70.dp)
                .clip(RoundedCornerShape(4.dp))
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = gameSession.gameName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(diagonalCutShape)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "RONDES: ${gameSession.rounds}",
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 5.dp),
            horizontalAlignment = Alignment.End
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.wrapContentSize()
            ) {
                Text(
                    text = if ((gameSession.fondocoinsEarned - gameSession.fondocoinsSpent) >= 0)
                        "+${gameSession.fondocoinsEarned - gameSession.fondocoinsSpent}"
                    else
                        "${gameSession.fondocoinsEarned - gameSession.fondocoinsSpent}",
                    fontSize = 15.sp,
                    color = if ((gameSession.fondocoinsEarned - gameSession.fondocoinsSpent) >= 0)
                        Color.Green
                    else
                        Color.Red,
                )
                Image(
                    painter = painterResource(id = R.drawable.fondocoin),
                    contentDescription = "Fondocoin",
                    modifier = Modifier.size(30.dp)
                )
            }

            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(end = 5.dp)
            ) {
                Text(
                    text = "+${gameSession.experienceEarned}",
                    fontSize = 15.sp,
                    color = if (gameSession.experienceEarned == 0) Color.Gray else Color.Green
                )
                Spacer(modifier = Modifier.width(5.dp))
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Level",
                    tint = Color.Yellow,
                    modifier = Modifier.size(19.dp)
                )
            }
        }
    }
}