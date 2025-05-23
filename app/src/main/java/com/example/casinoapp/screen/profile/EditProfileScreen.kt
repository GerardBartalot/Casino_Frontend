package com.example.casinoapp.screen.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.DatePicker
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.casinoapp.R
import com.example.casinoapp.screen.loaders.LoadingScreenEditProfile
import com.example.casinoapp.ui.components.ImagePickerDialog
import com.example.casinoapp.viewModel.RemoteViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    remoteViewModel: RemoteViewModel,
    navController: NavController
) {
    val currentUser by remoteViewModel.loggedInUser.collectAsState()
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf<Calendar?>(null) }
    var confirmPassword by remember { mutableStateOf("") }
    var updateMessage by remember { mutableStateOf<String?>(null) }
    var selectedImage by remember { mutableStateOf<String?>(null) }
    var showImagePicker by remember { mutableStateOf(false) }
    val profileAnimation by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.profile))
    val context = LocalContext.current
    var showLoading by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                val bitmap = uriToBitmap(context, it)
                bitmap?.let { bmp ->
                    selectedImage = bitmapToBase64(bmp)
                }
            }
        }
    )

    fun String.isValidPassword(): Boolean {
        val passwordRegex = "^(?=.*[A-Z])(?=.*\\d).{5,}$".toRegex()
        return passwordRegex.matches(this)
    }

    LaunchedEffect(currentUser) {
        currentUser?.let {
            name = it.name
            username = it.username
            password = ""
            selectedImage = it.profilePicture
            if (it.profilePicture.isNullOrEmpty()) {
                remoteViewModel.getProfilePicture(it.userId) {}
            }
        }
    }

    LaunchedEffect(updateMessage) {
        updateMessage?.let { message ->
            if (message.startsWith("Error") || message == "Les contrasenyes no coincideixen." || message == "La contrasenya ha de tenir mínim 5 caràcters, una majúscula i un número.") {
                snackbarHostState.showSnackbar(
                    message = message,
                    withDismissAction = true
                )
            }
        }
    }

    val birthDateText = birthDate?.let {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it.time)
    } ?: "Data de naixement"

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A1A1A),
            Color(0xFF2D2D2D),
            Color(0xFF1A1A1A)
        ),
        startY = 0f,
        endY = 1000f
    )

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .height(70.dp)
                    .background(
                        brush = gradientBrush,
                        alpha = 0.7f
                    ),
                title = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "Editar Perfil",
                            color = Color.White,
                            modifier = Modifier.padding(start = 0.dp)
                        )
                    }
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier.fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.size(48.dp)
                        ) {
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
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(
                        containerColor = Color(0xFFFF5252),
                        contentColor = Color.White,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(data.visuals.message)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
                .padding(innerPadding)
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
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF333333))
                        .clickable { showImagePicker = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (!selectedImage.isNullOrEmpty()) {
                        Image(
                            bitmap = rememberImageFromBase64(selectedImage!!),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        LottieAnimation(
                            composition = profileAnimation,
                            iterations = LottieConstants.IterateForever,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom", color = Color.White) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF333333),
                        unfocusedContainerColor = Color(0xFF333333),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color(0xFFFFD700),
                        unfocusedLabelColor = Color.LightGray,
                        cursorColor = Color(0xFFFFD700),
                        focusedIndicatorColor = Color(0xFFFFD700),
                        unfocusedIndicatorColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth(0.8f),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(5.dp))

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Nova contrasenya", color = Color.White) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF333333),
                        unfocusedContainerColor = Color(0xFF333333),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color(0xFFFFD700),
                        unfocusedLabelColor = Color.LightGray,
                        cursorColor = Color(0xFFFFD700),
                        focusedIndicatorColor = Color(0xFFFFD700),
                        unfocusedIndicatorColor = Color.Gray
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(0.8f),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(5.dp))

                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirma la nova contrasenya", color = Color.White) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF333333),
                        unfocusedContainerColor = Color(0xFF333333),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color(0xFFFFD700),
                        unfocusedLabelColor = Color.LightGray,
                        cursorColor = Color(0xFFFFD700),
                        focusedIndicatorColor = Color(0xFFFFD700),
                        unfocusedIndicatorColor = Color.Gray
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(0.8f),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(5.dp))

                Button(
                    onClick = {
                        val calendar = birthDate ?: Calendar.getInstance()
                        android.app.DatePickerDialog(
                            context,
                            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                                birthDate = Calendar.getInstance().apply {
                                    set(year, month, dayOfMonth)
                                }
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF333333),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(60.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = "Seleccionar data de naixement",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = birthDateText,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (password.isNotEmpty()) {
                            if (!password.isValidPassword()) {
                                updateMessage =
                                    "La contrasenya ha de tenir mínim 5 caràcters, una majúscula i un número."
                                return@Button
                            }
                            if (password != confirmPassword) {
                                updateMessage = "Les contrasenyes no coincideixen."
                                return@Button
                            }
                        }
                        showLoading = true
                        currentUser?.let { user ->
                            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val dateString =
                                birthDate?.let { outputFormat.format(it.time) } ?: user.dateOfBirth
                            val updatedUser = user.copy(
                                name = name,
                                username = username,
                                password = if (password.isNotEmpty()) password else user.password,
                                dateOfBirth = dateString,
                                profilePicture = selectedImage ?: user.profilePicture
                            )

                            remoteViewModel.updateUser(updatedUser) { message ->
                                if (message.startsWith("Error")) {
                                    updateMessage = message
                                    showLoading = false
                                } else {
                                    if (selectedImage != null && selectedImage != user.profilePicture) {
                                        remoteViewModel.updateProfilePicture(
                                            user.userId,
                                            selectedImage!!
                                        ) { profileMessage ->
                                            showLoading = false
                                            if (profileMessage.startsWith("Error")) {
                                                updateMessage = profileMessage
                                            } else {
                                                navController.navigate("profileScreen") {
                                                    popUpTo("editProfileScreen") {
                                                        inclusive = true
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        showLoading = false
                                        navController.navigate("profileScreen") {
                                            popUpTo("editProfileScreen") { inclusive = true }
                                        }
                                    }
                                }
                            }
                        }
                    },
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
                    Text(
                        text = "Aplicar canvis",
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

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

                ConfirmDeleteDialog(
                    showDialog = showDeleteDialog,
                    onConfirm = {
                        showDeleteDialog = false
                        coroutineScope.launch {
                            remoteViewModel.deleteAccount { result ->
                                if (result.contains("Compte eliminat correctament")) {
                                    navController.navigate("registerScreen") {
                                        popUpTo(navController.graph.startDestinationId) {
                                            inclusive = true
                                        }
                                    }
                                } else {
                                    updateMessage = result
                                }
                            }
                        }
                    },
                    onDismiss = { showDeleteDialog = false }
                )
            }

            if (showLoading) {
                LoadingScreenEditProfile(
                    modifier = Modifier.matchParentSize()
                )
            }
            if (showImagePicker) {
                ImagePickerDialog(
                    hasCurrentImage = !selectedImage.isNullOrEmpty(),
                    onDismiss = { showImagePicker = false },
                    onSelectFromGallery = {
                        galleryLauncher.launch("image/*")
                    },
                    onDeleteCurrent = {
                        currentUser?.userId?.let { userId ->
                            remoteViewModel.deleteProfilePicture(userId) { message ->
                                selectedImage = null
                                if (message.startsWith("Error")) {
                                    updateMessage = message
                                }
                                showImagePicker = false
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun rememberImageFromBase64(base64: String): ImageBitmap {
    val bitmap = remember(base64) {
        try {
            val imageBytes = Base64.decode(base64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                ?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }

    return bitmap ?: createBitmap(1, 1).asImageBitmap()
}

private fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

private fun bitmapToBase64(bitmap: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
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