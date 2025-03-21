package com.example.casinoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.casinoapp.screen.HomeScreen
import com.example.casinoapp.screen.LoginScreen
import com.example.casinoapp.screen.RegisterScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            MaterialTheme {
                NavHost(navController = navController, startDestination = "nurseLoginScreen") {
                    composable("nurseLoginScreen") {
                        LoginScreen(navController = navController, remoteViewModel = viewModel())
                    }
                    composable("homeScreen") {
                        HomeScreen(navController = navController)
                    }
                    composable("registerScreen") {
                        RegisterScreen(navController = navController, remoteViewModel = viewModel())
                    }
                }
            }
        }
    }
}