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
import com.example.casinoapp.screen.SlotMachineScreen
import com.example.casinoapp.viewModel.RemoteViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val remoteViewModel: RemoteViewModel = viewModel()
            MaterialTheme {
                NavHost(navController = navController, startDestination = "loginScreen") {
                    composable("loginScreen") {
                        LoginScreen(
                            remoteViewModel = remoteViewModel,
                            onNavigateToRegister = { navController.navigate("registerScreen") },
                            onNavigateToHome = { navController.navigate("homeScreen") }
                        )
                    }
                    composable("homeScreen") {
                        HomeScreen(
                            navController = navController,
                            remoteViewModel = remoteViewModel
                        )
                    }
                    composable("registerScreen") {
                        RegisterScreen(
                            remoteViewModel = remoteViewModel,
                            onNavigateToLogin = { navController.navigate("loginScreen") },
                            onNavigateToHome = { navController.navigate("homeScreen") }
                        )
                    }
                    composable("slotMachine") {
                        SlotMachineScreen(navController)
                    }
                }
            }
        }
    }
}