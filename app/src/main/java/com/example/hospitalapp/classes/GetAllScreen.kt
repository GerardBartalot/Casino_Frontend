package com.example.hospitalapp.classes

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NurseApp(remoteViewModel: RemoteViewModel, onBackPressed: () -> Unit) {
    //val state = viewModel.remoteMessageUiState
    val remoteMessageUiState = remoteViewModel.remoteMessageUiState

    LaunchedEffect(Unit) {
        remoteViewModel.getAllNurses()
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
                    NurseList(nurses = remoteMessageUiState.remoteMessage)
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
fun NurseList(nurses: List<Nurse>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(nurses) { nurse ->
            NurseCard(nurse)
        }
    }
}

@Composable
fun NurseCard(nurse: Nurse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "ID: ${nurse.nurse_id}", fontSize = 16.sp)
            Text(text = "Name: ${nurse.name}", fontSize = 14.sp)
            Text(text = "Username: ${nurse.username}", fontSize = 14.sp)
        }
    }
}