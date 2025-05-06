package com.example.casinoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.casinoapp.screen.profile.EditProfileScreen
import com.example.casinoapp.screen.HomeScreen
import com.example.casinoapp.screen.loaders.LoadingScreen
import com.example.casinoapp.screen.auth.LoginScreen
import com.example.casinoapp.screen.auth.RegisterScreen
import com.example.casinoapp.screen.games.BlackjackScreen
import com.example.casinoapp.screen.games.RouletteScreen
import com.example.casinoapp.screen.games.SlotMachineScreen
import com.example.casinoapp.screen.loaders.SplashScreen
import com.example.casinoapp.screen.profile.ProfileScreen
import com.example.casinoapp.screen.games.ScratchCardScreen
import com.example.casinoapp.screen.loaders.LoadingHistoryScreen
import com.example.casinoapp.screen.profile.HistoryScreen
import com.example.casinoapp.viewModel.GameViewModel
import com.example.casinoapp.viewModel.RemoteViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val remoteViewModel: RemoteViewModel = viewModel()
            val gameViewModel: GameViewModel = viewModel()
            MaterialTheme {
                NavHost(navController = navController, startDestination = "splashScreen") {
                    composable("splashScreen") {
                        SplashScreen(navController = navController)
                    }
                    composable("loadingScreen") {
                        LoadingScreen()
                    }
                    composable("loginScreen") {
                        LoginScreen(
                            remoteViewModel = remoteViewModel,
                            onNavigateToRegister = { navController.navigate("registerScreen") },
                            onNavigateToHome = { navController.navigate("homeScreen") }
                        )
                    }
                    composable("homeScreen") {
                        HomeScreen(
                            remoteViewModel = remoteViewModel,
                            gameViewModel = gameViewModel,
                            onNavigateToRoulette = { navController.navigate("rouletteScreen") },
                            onNavigateToSlotMachine = { navController.navigate("slotMachineScreen") },
                            onNavigateToProfile = { navController.navigate("profileScreen") },
                            onNavigateToScratchCard = { navController.navigate("scratchCardScreen") },
                            onNavigateToBlackJack = { navController.navigate("blackJackScreen") },
                        )
                    }
                    composable("registerScreen") {
                        RegisterScreen(
                            remoteViewModel = remoteViewModel,
                            onNavigateToLogin = { navController.navigate("loginScreen") },
                            onNavigateToHome = { navController.navigate("homeScreen") }
                        )
                    }
                    composable("slotMachineScreen") {
                        SlotMachineScreen(
                            navController,
                            remoteViewModel = remoteViewModel,
                            gameViewModel = gameViewModel,
                        )
                    }
                    composable("rouletteScreen") {
                        RouletteScreen(
                            navController,
                            gameViewModel = gameViewModel,
                            remoteViewModel = remoteViewModel,
                        )
                    }
                    composable("blackJackScreen") {
                        BlackjackScreen(
                            navController,
                            gameViewModel = gameViewModel,
                            remoteViewModel = remoteViewModel,
                        )
                    }
                    composable("scratchCardScreen") {
                        ScratchCardScreen(
                            navController,
                            gameViewModel = gameViewModel,
                            remoteViewModel = remoteViewModel,
                        )
                    }
                    composable("profileScreen") {
                        ProfileScreen(
                            remoteViewModel = remoteViewModel,
                            navController = navController,
                            onNavigateToEditProfileScreen = { navController.navigate("editProfileScreen") },
                            onNavigateToHistoryScreen = { navController.navigate("historyScreen") },
                            onNavigateToLoadingHistoryScreen = { navController.navigate("loadingHistoryScreen") },
                        )
                    }
                    composable("editProfileScreen") {
                        EditProfileScreen(
                            remoteViewModel = remoteViewModel,
                            navController = navController
                        )
                    }
                    composable("historyScreen") {
                        HistoryScreen(
                            remoteViewModel = remoteViewModel,
                            navController = navController
                        )
                    }
                    composable("loadingHistoryScreen") {
                        LoadingHistoryScreen(remoteViewModel, navController)
                    }
                }
            }
        }
    }
}
