 package com.example.casinoapp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.casinoapp.viewModel.RemoteViewModel
/*
@Composable
fun ProfileScreen(createUsers: RemoteViewModel, onBackPressed: () -> Unit) {
    val user = createUsers.users.find { it.id == 1 } ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Button(onClick = onBackPressed) {
                Text(text = "Back")
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "PROFILE",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Name Field
        TextField(
            value = user.name,
            onValueChange = {},
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Username Field
        TextField(
            value = user.username,
            onValueChange = {},
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        TextField(
            value = user.password,
            onValueChange = {},
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { /* TODO: Implement update logic */ }) {
                Text("Update")
            }
            Button(onClick = { /* TODO: Implement delete logic */ }) {
                Text("Delete")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    val fakeViewModel = RemoteViewModel().apply {
        users = listOf(
            User(id = 1, name = "John Doe", username = "johnd", password = "1234")
        )
    }
    ProfileScreen(createUsers = fakeViewModel, onBackPressed = {})
}*/
