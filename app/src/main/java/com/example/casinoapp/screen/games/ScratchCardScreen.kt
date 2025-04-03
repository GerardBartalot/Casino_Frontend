package com.example.casinoapp.screen.games

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.casinoapp.R
import com.example.casinoapp.entity.GameSession
import com.example.casinoapp.screen.ExperienceProgressBar
import com.example.casinoapp.screen.formatWithSeparator
import com.example.casinoapp.viewModel.GameViewModel
import com.example.casinoapp.viewModel.RemoteViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun ScratchCardScreen(
    navController: NavController,
    remoteViewModel: RemoteViewModel,
    gameViewModel: GameViewModel,
) {
    val symbols = listOf("🍒", "💎", "🍋", "💰", "🍀")
    val loggedInUser by remoteViewModel.loggedInUser.collectAsState()
    var selectedSymbol by remember { mutableStateOf<String?>(null) }
    var revealedIndex by remember { mutableStateOf<Int?>(null) }
    var generatedSymbols by remember { mutableStateOf(List(9) { symbols.random() }) }
    var betAmount by remember { mutableStateOf("") }
    var resultMessage by remember { mutableStateOf("") }
    var isGameActive by remember { mutableStateOf(true) }
    var userId by remember { mutableStateOf("") }
    val vmExperience by gameViewModel.experience.collectAsState()
    val vmFondocoins by gameViewModel.fondocoins.collectAsState()
    var localFondocoins by remember { mutableIntStateOf(0) }
    var localExperience by remember { mutableIntStateOf(0) }
    var roundsPlayed by remember { mutableIntStateOf(0) }
    var fondocoinsSpent by remember { mutableIntStateOf(0) }
    var fondocoinsEarned by remember { mutableIntStateOf(0) }
    var experienceEarned by remember { mutableIntStateOf(0) }

    fun saveGameSession() {
        loggedInUser?.let { user ->
            val gameSession = GameSession(
                user = user,
                gameName = "Scratch Card",
                rounds = roundsPlayed,
                experienceEarned = experienceEarned,
                fondocoinsSpent = fondocoinsSpent,
                fondocoinsEarned = fondocoinsEarned
            )
            remoteViewModel.saveGameSession(gameSession) { result ->
                Log.d("ScratchCardScreen", "Game session save result: $result")
            }
        }
    }

    val lottieComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.lluvia_monedas)
    )

    val hasWon by derivedStateOf {
        resultMessage.contains("¡Ganaste")
    }

    LaunchedEffect(vmFondocoins, vmExperience) {
        localFondocoins = vmFondocoins
        localExperience = vmExperience
    }

    LaunchedEffect(loggedInUser) {
        loggedInUser?.userId?.let {
            userId = it.toString()
            gameViewModel.getUserFondoCoins(it.toInt())
            gameViewModel.getUserExperience(it.toInt())
        }
    }

    fun generateSymbols(simbolo: String): List<String> {
        val otros = symbols.filter { it != simbolo }
        val otrosAleatorios = if (otros.size >= 8) otros.shuffled().take(8)
        else List(8) { otros.random() }
        return (otrosAleatorios + simbolo).shuffled()
    }

    fun playGame(index: Int) {
        try {
            val bet = betAmount.toIntOrNull() ?: run {
                resultMessage = "Ingresa una apuesta válida."
                return
            }

            if (!isGameActive) {
                resultMessage = "El juego no está activo."
                return
            }

            if (bet <= 0) {
                resultMessage = "La apuesta debe ser mayor a cero."
                return
            }

            if (bet > localFondocoins) {
                resultMessage = "Fondos insuficientes."
                return
            }

            if (selectedSymbol == null) {
                resultMessage = "Selecciona un símbolo primero."
                return
            }

            if (revealedIndex != null) {
                resultMessage = "Ya has revelado una posición."
                return
            }

            if (index !in generatedSymbols.indices) {
                resultMessage = "Posición inválida."
                return
            }

            if (!gameViewModel.placeBet(bet)) {
                resultMessage = "No se pudo procesar la apuesta."
                return
            }

            revealedIndex = index
            val revealedSymbol = generatedSymbols[index]
            val hasWon = revealedSymbol == selectedSymbol
            val winnings = if (hasWon) bet * 5 else 0
            val experienceToAdd = if (hasWon) 100 else 0

            localFondocoins = localFondocoins - bet + winnings
            localExperience += experienceToAdd

            roundsPlayed++
            fondocoinsSpent += bet
            if (hasWon) {
                fondocoinsEarned += winnings
                experienceEarned += experienceToAdd
            }

            resultMessage = if (revealedSymbol == selectedSymbol) {
                "¡Ganaste $winnings fondocoins! +${experienceToAdd} XP. Símbolo ganador: $revealedSymbol"
            } else {
                "¡Perdiste! Símbolo ganador: $revealedSymbol"
            }

            isGameActive = false
        } catch (e: Exception) {
            Log.e("ScratchCardScreen", "Error en playGame: ${e.message}")
            resultMessage = "Error en el juego. Intenta nuevamente."
        }
    }

    fun resetGame() {
        betAmount = ""
        selectedSymbol = null
        generatedSymbols = List(9) { symbols.random() }
        revealedIndex = null
        resultMessage = ""
        isGameActive = true
    }

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF4A148C),
            Color(0xFF7B1FA2),
            Color(0xFF12005E)
        ),
        startY = 0f,
        endY = 1000f
    )

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                modifier = Modifier.height(100.dp),
                title = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "Rasca y Gana",
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
                            onClick = {
                                navController.navigate("loadingScreen") {
                                    popUpTo("slotMachineScreen") { inclusive = true }
                                }

                                userId.toIntOrNull()?.let { id ->
                                    gameViewModel.updateUserFondoCoins(id, localFondocoins)
                                    gameViewModel.updateUserExperience(id, localExperience)
                                }
                                saveGameSession()

                                CoroutineScope(Dispatchers.Main).launch {
                                    delay(1500)
                                    navController.navigate("homeScreen") {
                                        popUpTo("loadingScreen") { inclusive = true }
                                    }
                                }
                            }
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
                    containerColor = Color(0xFF12005E),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Box (
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
        ) {
            Image(
                painter = painterResource(id = R.drawable.subtle_texture2),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.08f),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Spacer(modifier = Modifier.height(100.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Fondoscoins
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.fondocoin),
                            contentDescription = "Fondocoin",
                            modifier = Modifier.size(50.dp)
                        )
                        Text(
                            vmFondocoins.formatWithSeparator(),
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 22.sp),
                            modifier = Modifier.padding(start = 8.dp),
                            color = Color.White
                        )
                    }

                    // Barra de experiencia
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        ExperienceProgressBar(
                            currentXp = vmExperience,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        )
                    }
                }

                TextField(
                    value = betAmount,
                    onValueChange = { betAmount = it },
                    placeholder = { Text("Apuesta", color = Color.Gray) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        cursorColor = Color.Gray,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.Gray,
                        unfocusedTextColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .width(180.dp)
                        .height(56.dp)
                )

                Text("Selecciona un símbolo", color = Color.White)

                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    symbols.forEach { symbol ->
                        val selected = symbol == selectedSymbol
                        Button(
                            onClick = {
                                selectedSymbol = symbol
                                if (revealedIndex == null) {
                                    generatedSymbols = generateSymbols(symbol)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selected) Color.Yellow else Color.Gray
                            ),
                            shape = CircleShape,
                            modifier = Modifier
                                .padding(4.dp)
                                .size(50.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(symbol, fontSize = 24.sp)
                            }
                        }
                    }
                }

                Column {
                    for (row in 0..2) {
                        Row {
                            for (col in 0..2) {
                                val index = row * 3 + col
                                val revealed = revealedIndex == index
                                Box(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .size(70.dp)
                                        .background(Color(0xFFFFD700), RoundedCornerShape(10.dp))
                                        .clickable(enabled = isGameActive && revealedIndex == null) {
                                            playGame(index)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (revealed) generatedSymbols[index] else "❓",
                                        fontSize = 26.sp
                                    )
                                }
                            }
                        }
                    }
                }

                if (resultMessage.isNotEmpty()) {
                    Text(resultMessage, color = Color.White, fontSize = 18.sp)
                    Button(onClick = { resetGame() }) {
                        Text("Volver a jugar")
                    }
                }
            }
            if (hasWon) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f)
                ) {
                    LottieAnimation(
                        composition = lottieComposition,
                        modifier = Modifier.fillMaxSize().zIndex(1f),
                        iterations = 1,
                        speed = 0.5f
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScratchCardScreenPreview() {
    ScratchCardScreen(
        navController = rememberNavController(),
        remoteViewModel = RemoteViewModel(),
        gameViewModel = GameViewModel()
    )
}