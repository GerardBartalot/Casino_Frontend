package com.example.casinoapp.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.casinoapp.viewModel.RemoteViewModel
import com.example.casinoapp.entity.GameSession

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    remoteViewModel: RemoteViewModel,
    navController: NavController
) {
    val currentUser = remoteViewModel.loggedInUser.collectAsState().value
    val gameHistory by remoteViewModel.gameHistory.collectAsState()

    LaunchedEffect(currentUser) {
        currentUser?.let {
            remoteViewModel.getUserGameHistory(it.userId) { result ->
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(top = 20.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "HISTORIAL PARTIDAS",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(30.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(650.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.DarkGray)
                    .border(2.dp, Color.Yellow, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                if (gameHistory.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = Color.Yellow
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Cargando historial...",
                            color = Color.White
                        )
                    }
                } else {
                    val last10Games = gameHistory.takeLast(10).reversed()

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(last10Games) { gameSession ->
                            CompactGameSessionItem(gameSession)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Yellow,
                    contentColor = Color.Black
                )
            ) {
                Text(text = "VOLVER AL PERFIL")
            }
        }
    }
}

@Composable
fun CompactGameSessionItem(gameSession: GameSession) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = gameSession.gameName.uppercase(),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.weight(2f)
        )

        Text(
            text = "RONDAS: ${gameSession.rounds}",
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = if ((gameSession.fondocoinsEarned - gameSession.fondocoinsSpent) >= 0)
                    "+${gameSession.fondocoinsEarned - gameSession.fondocoinsSpent}"
                else
                    "${gameSession.fondocoinsEarned - gameSession.fondocoinsSpent}",
                fontSize = 14.sp,
                color = if ((gameSession.fondocoinsEarned - gameSession.fondocoinsSpent) >= 0)
                    Color.Green
                else
                    Color.Red,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "+${gameSession.experienceEarned}KP",
                fontSize = 12.sp,
                color = Color.Yellow
            )
        }
    }
}