package com.example.casinoapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun ProfileScreen(
    navController: NavController
) {

    Column(
        modifier = androidx.compose.ui.Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        TopBarProfile(navController)

        Spacer(modifier = androidx.compose.ui.Modifier.height(30.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = androidx.compose.ui.Modifier.size(250.dp)
        ) {
            Text("PERFIL POR HACER")
        }
    }
}

@Composable
fun TopBarProfile(navController: NavController) {
    Row(
        modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Volver atrás",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/*val user = createUsers.users.find { it.id == 1 } ?: return


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
}*/

/*@Composable
fun Row(
    modifier: fillMaxWidth,
    horizontalArrangement: SpaceEvenly,
    content: @Composable () -> Button
) {
    TODO("Not yet implemented")
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    val createUsers = RemoteViewModel()
    ProfileScreen(createUsers = createUsers, onBackPressed = {})
}*/