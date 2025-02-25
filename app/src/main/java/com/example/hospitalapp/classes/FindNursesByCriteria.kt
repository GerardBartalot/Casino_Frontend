package com.example.hospitalapp.classes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SearchScreen(remoteViewModel: RemoteViewModel, onBackPressed: () -> Unit) {
    var query by remember { mutableStateOf("") }
    var foundNurse by remember { mutableStateOf<Nurse?>(null) }
    var isSearchPerformed by remember { mutableStateOf(false) }

    val remoteMessageUiState = remoteViewModel.remoteMessageUiState

    LaunchedEffect(Unit) {
        remoteViewModel.getAllNurses()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 0.dp, top = 30.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Button(onClick = onBackPressed) {
                Text(text = "Back")
            }
        }
        Spacer(modifier = Modifier.height(100.dp))
        TextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search Nurse") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            isSearchPerformed = true
            when (remoteMessageUiState) {
                is RemoteMessageUiState.Success -> {
                    foundNurse = (remoteMessageUiState as RemoteMessageUiState.Success).remoteMessage.find { nurse ->
                        nurse.name.contains(query, ignoreCase = true) ||
                                nurse.username.contains(query, ignoreCase = true) ||
                                nurse.nurse_id.toString() == query
                    }
                }
                else -> foundNurse = null
            }
        }) {
            Text(text = "Search")
        }
        Spacer(modifier = Modifier.height(16.dp))

        when (remoteMessageUiState) {
            is RemoteMessageUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
            is RemoteMessageUiState.Error -> {
                Text(
                    text = "Error loading data.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            is RemoteMessageUiState.Success -> {
                foundNurse?.let {
                    NurseCard(nurse = it)
                } ?: run {
                    if (isSearchPerformed && query.isNotEmpty()) {
                        Text(
                            text = "Nurse not found.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFindByNameScreen() {
    val remoteViewModel = RemoteViewModel()

    SearchScreen(
        remoteViewModel = remoteViewModel,
        onBackPressed = {}
    )
}
