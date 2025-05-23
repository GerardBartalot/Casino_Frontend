package com.example.casinoapp.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.example.casinoapp.R
import com.example.casinoapp.viewModel.RemoteViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    remoteViewModel: RemoteViewModel,
    navController: NavController,
    onNavigateToEditProfileScreen: () -> Unit,
    onNavigateToLoadingHistoryScreen: () -> Unit,
) {
    val currentUser = remoteViewModel.loggedInUser.collectAsState().value
    val profile by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.profile))
    val profileImage by remember { derivedStateOf {
        currentUser?.profilePicture?.takeIf { it.isNotEmpty() }
    } }
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF1A1A1A), Color(0xFF2D2D2D), Color(0xFF1A1A1A)),
        startY = 0f,
        endY = 1000f
    )
    val coroutineScope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .height(70.dp)
                    .background(brush = gradientBrush, alpha = 0.7f),
                title = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text("Perfil", color = Color.White)
                    }
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier.fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = {
                            navController.navigate("homeScreen") {
                                popUpTo("profileScreen") { inclusive = true }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
        ) {
            Image(
                painter = painterResource(id = R.drawable.subtle_texture),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.1f),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                currentUser?.let { user ->
                    Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
                        if (profileImage != null) {
                            Image(
                                bitmap = rememberImageFromBase64(profileImage!!),
                                contentDescription = "Foto de perfil",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            LottieAnimation(
                                composition = profile,
                                iterations = LottieConstants.IterateForever,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text("Hola, ${user.name}!", fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    Text("@${user.username}", fontSize = 18.sp, color = Color.LightGray, modifier = Modifier.padding(top = 8.dp))
                    Text("Data de naixement: ${user.dateOfBirth}", fontSize = 18.sp, color = Color.LightGray, modifier = Modifier.padding(top = 8.dp))

                    Spacer(modifier = Modifier.height(50.dp))

                    ProfileButton("Historial de partides") { onNavigateToLoadingHistoryScreen() }
                    Spacer(modifier = Modifier.height(20.dp))
                    ProfileButton("Editar perfil") { onNavigateToEditProfileScreen() }
                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(50.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Eliminar compte", color = Color.White, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            remoteViewModel.logout()
                            navController.navigate("loginScreen") {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA52A2A)),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(50.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Tancar sessió", color = Color.White, fontSize = 16.sp)
                    }
                    ConfirmDeleteDialog(
                        showDialog = showDeleteDialog,
                        onConfirm = {
                            coroutineScope.launch {
                                remoteViewModel.deleteAccount(user.userId) { result ->
                                    if (result.contains("èxit")) {
                                        remoteViewModel.logout()
                                        navController.navigate("loginScreen") {
                                            popUpTo(navController.graph.startDestinationId) {
                                                inclusive = true
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        onDismiss = { showDeleteDialog = false }
                    )
                } ?: run {
                    Text("No hay usuario logueado", color = Color.White, fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

@Composable
fun ProfileButton(text: String, onNavigate: () -> Unit) {
    Button(
        onClick = onNavigate,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFFD700),
            contentColor = Color.Black
        ),
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(60.dp)
            .padding(vertical = 5.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(text = text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}


@Composable
fun ConfirmDeleteDialog(
    showDialog: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "Eliminar compte",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Estàs segur que vols eliminar el teu compte de manera permanent? Aquesta acció no es pot desfer.",
                    color = Color.White
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFA52A2A)
                    )
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFD700)
                    )
                ) {
                    Text("Cancel·lar", color = Color.White)
                }
            },
            containerColor = Color(0xFF333333),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }
}