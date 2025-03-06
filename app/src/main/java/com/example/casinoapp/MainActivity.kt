package com.example.casinoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.casinoapp.viewModel.RemoteViewModel
import com.example.casinoapp.screen.LoginScreen
import com.example.casinoapp.screen.RegisterScreen
import com.example.casinoapp.screen.SearchScreen
import com.example.casinoapp.ui.theme.CasinoAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CasinoAppTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val  remoteViewModel = RemoteViewModel()
    Surface(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = "login") {
            composable("register") {
                RegisterScreen(
                    remoteViewModel = remoteViewModel,
                    onNavigateToLogin = { navController.navigate("login") }
                )
            }
            composable("login") {
                LoginScreen(
                    remoteViewModel = remoteViewModel,
                    onNavigateToRegister = { navController.navigate("register") },
                    onNavigateToSearch = { navController.navigate("search") },
                    onBackPressed = { navController.popBackStack() }
                )
            }

            /*composable("getAll") {
                GetAllScreen(
                    remoteViewModel = remoteViewModel,
                    onBackPressed = { navController.popBackStack() }
                )
            }*/
            composable("findByName") {
                SearchScreen(
                    remoteViewModel = remoteViewModel,
                    onBackPressed = { navController.popBackStack() }
                )
            }
            composable("search") {
                SearchScreen(navController = navController)
            }
            /*composable("profile") {
                ProfileScreen(
                    createNurses = RemoteViewModel(),
                    onBackPressed = { navController.popBackStack() })
            }*/
        }
    }
}