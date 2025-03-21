package com.example.casinoapp.screen
/*

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.casinoapp.viewModel.User
import com.example.casinoapp.viewModel.RemoteMessageUiState
import com.example.casinoapp.viewModel.RemoteViewModel
/*
@Composable
fun CasinoApp(remoteViewModel: RemoteViewModel, onBackPressed: () -> Unit) {
    //val state = viewModel.remoteMessageUiState
    val remoteMessageUiState = remoteViewModel.remoteMessageUiState

    LaunchedEffect(Unit) {
        remoteViewModel.getAllUsers()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 40.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Button(onClick = onBackPressed) {
                    Text(text = "Back")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))


            when (remoteMessageUiState) {
                is RemoteMessageUiState.Loading -> {
                    Log.d("RemoteViewModel", "before circular")
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    Log.d("RemoteViewModel", "after circular")
                }
                is RemoteMessageUiState.Success -> {
                    Log.d("RemoteViewModel", "entra success")
                    UserList(user = remoteMessageUiState.remoteMessage)
                }
                is RemoteMessageUiState.Error -> {
                    Text(
                        text = "Error loading data.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun UserList(user: List<User>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(user) { user ->
            UserCard(user)
        }
    }
}

@Composable
fun UserCard(user: User) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "ID: ${user.userId}", fontSize = 16.sp)
            Text(text = "Name: ${user.name}", fontSize = 14.sp)
            Text(text = "Username: ${user.username}", fontSize = 14.sp)
        }
    }
}*/