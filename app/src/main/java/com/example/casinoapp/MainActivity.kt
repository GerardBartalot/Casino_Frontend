package com.example.casinoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.casinoapp.screen.HomeScreen
import com.example.casinoapp.screen.LoginScreen
import com.example.casinoapp.screen.RegisterScreen
import com.example.casinoapp.screen.RouletteScreen
import com.example.casinoapp.screen.SlotMachineScreen
import com.example.casinoapp.screen.SplashScreen
import com.example.casinoapp.screen.ProfileScreen
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
                    composable("profileScreen") {
                        ProfileScreen(
                            remoteViewModel = remoteViewModel,
                            navController = navController,
                            onNavigateToProfile = { navController.navigate("profileScreen") }
                        )
                    }
                }
            }
        }
    }
}