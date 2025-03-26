package com.example.casinoapp.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.casinoapp.viewModel.RemoteViewModel


@Composable
fun HomeScreen(
    navController: NavHostController,
    remoteViewModel: RemoteViewModel,
    onNavigateToRoulette: () -> Unit,
    onNavigateToSlotMachine: () -> Unit,
    onNavigateToProfile: () -> Unit,
) {

    val loggedInUser by remoteViewModel.loggedInUser.collectAsState()
    val fondocoins = loggedInUser?.fondocoins ?: 0


    Column(
        modifier = Modifier
            .fillMaxSize()
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
            text = "Tus Fondocoins: $fondocoins",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onNavigateToSlotMachine() },
            modifier = Modifier
                .fillMaxWidth(0.9f)
        ) {
            Text("Slot Machine Game")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onNavigateToRoulette() },
            modifier = Modifier
                .fillMaxWidth(0.9f)
        ) {
            Text("Roulette Game")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onNavigateToProfile() },
            modifier = Modifier
                .fillMaxWidth(0.9f)
        ) {
            Text("Profile")
        }

        Spacer(modifier = Modifier.height(100.dp))

        Button(onClick = {
            remoteViewModel.logout()
            navController.navigate("loginScreen") {
                popUpTo("homeScreen") { inclusive = true }
            }
        }) {
            Text(
                text = "Logout",
                style = TextStyle(
                    fontSize = 14.sp
                )
            )
        }
    }
}
