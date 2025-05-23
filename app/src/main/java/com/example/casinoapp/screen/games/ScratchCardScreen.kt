package com.example.casinoapp.screen.games

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.casinoapp.R
import com.example.casinoapp.entity.GameSession
import com.example.casinoapp.ui.components.AnimatedNumberDisplay
import com.example.casinoapp.ui.components.ExperienceProgressBar
import com.example.casinoapp.ui.components.GameRuleSection
import com.example.casinoapp.ui.components.GameRulesDialog
import com.example.casinoapp.ui.components.LevelUpPopup
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
    val localFondocoins by gameViewModel.fondocoins.collectAsState()
    val localExperience by gameViewModel.experience.collectAsState()
    var roundsPlayed by remember { mutableIntStateOf(0) }
    var fondocoinsSpent by remember { mutableIntStateOf(0) }
    var fondocoinsEarned by remember { mutableIntStateOf(0) }
    var experienceEarned by remember { mutableIntStateOf(0) }
    var experienceWon by remember { mutableIntStateOf(0) }
    var fondocoinsWon by remember { mutableIntStateOf(0) }
    var revealedSymbol by remember { mutableStateOf<String?>(null) }
    var hasWon by remember { mutableStateOf(false) }
    var showRulesDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF4A148C),
            Color(0xFF7B1FA2),
            Color(0xFF12005E)
        ),
        startY = 0f,
        endY = 1000f
    )

    val games by gameViewModel.games.collectAsState()
    LaunchedEffect(vmExperience) {
        gameViewModel.checkLevelUp(vmExperience)
    }

    if (gameViewModel.showLevelUpPopup) {
        LevelUpPopup(
            currentLevel = gameViewModel.currentPopupLevel,
            allGames = games,
            onDismiss = { gameViewModel.dismissLevelUpPopup() }
        )
    }

    fun saveGameSession() {
        loggedInUser?.let { user ->
            val slotMachineGame = gameViewModel.games.value.find {
                it.gameName.equals("Rasca i Guanya", ignoreCase = true)
            }

            slotMachineGame?.let { game ->
                val gameSession = GameSession(
                    user = user,
                    game = game,
                    rounds = roundsPlayed,
                    experienceEarned = experienceEarned,
                    fondocoinsSpent = fondocoinsSpent,
                    fondocoinsEarned = fondocoinsEarned
                )
                remoteViewModel.saveGameSession(gameSession) { result ->
                    Log.d("Rasca i Guanya", "Game session save result: $result")
                }
            } ?: run {
                Log.e("Rasca i Guanya", "No se pudo encontrar el juego 'Rasca i Guanya'")
            }
        }
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
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Ingressa una aposta vàlida.")
                }
                return
            }

            if (!isGameActive) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("El joc no està actiu.")
                }
                return
            }

            if (bet <= 0) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("L'aposta ha de ser major a zero.")
                }
                return
            }

            if (bet > localFondocoins) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Fons insuficients.")
                }
                return
            }

            if (selectedSymbol == null) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Selecciona un símbol.")
                }
                return
            }

            if (revealedIndex != null) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Ja has revelat una posició.")
                }
                return
            }

            if (index !in generatedSymbols.indices) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Posició invàlida.")
                }
                return
            }

            if (!gameViewModel.placeBet(bet)) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("No s'ha pogut processar la aposta.")
                }
                return
            }

            revealedIndex = index
            revealedSymbol = generatedSymbols[index]
            hasWon = revealedSymbol == selectedSymbol

            roundsPlayed++
            fondocoinsSpent += bet

            if (hasWon) {
                fondocoinsWon = bet * 5
                experienceWon = 100

                fondocoinsEarned += fondocoinsWon
                experienceEarned += experienceWon

                userId.toIntOrNull()?.let { id ->
                    gameViewModel.updateUserFondoCoins(id, vmFondocoins + fondocoinsWon)
                    gameViewModel.updateUserExperience(id, vmExperience + experienceWon)
                }
            } else {
                userId.toIntOrNull()?.let { id ->
                    gameViewModel.updateUserFondoCoins(id, vmFondocoins - bet)
                }
            }

            isGameActive = false
        } catch (e: Exception) {
            Log.e("ScratchCardScreen", "Error en playGame: ${e.message}")
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Error del joc. Intenta-ho novament.")
            }
        }
    }

    fun resetGame() {
        betAmount = ""
        selectedSymbol = null
        generatedSymbols = List(9) { symbols.random() }
        revealedIndex = null
        resultMessage = ""
        isGameActive = true
        experienceWon = 0
        coroutineScope.launch {
            snackbarHostState.showSnackbar("Joc reiniciat. Pots jugar de nou!")
        }
    }

    Scaffold(
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
        },
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
                            text = "Rasca i Guanya",
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
                                if (roundsPlayed > 0) {
                                    saveGameSession()
                                }
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
                actions = {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(end = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = {showRulesDialog = true},
                            modifier = Modifier
                                .size(30.dp)
                                .border(2.dp, Color.Green, CircleShape)
                                .clip(CircleShape),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.White,
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("?", style = TextStyle(fontSize = 16.sp, color = Color.Green))
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
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Fondoscoins
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        PixelDisplay(localFondocoins)
                    }

                    // Barra de experiencia
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        ExperienceProgressBar(
                            currentXp = localExperience,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        )
                    }
                }

                TextField(
                    value = betAmount,
                    onValueChange = { betAmount = it },
                    placeholder = { Text("Aposta", color = Color.Gray) },
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

                Text("Selecciona un símbol:", color = Color.White)

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

                if (resultMessage.isNotEmpty() && revealedIndex == null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = resultMessage,
                        color = Color.Red,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (revealedIndex != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        if (hasWon) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                AnimatedNumberDisplay(
                                    fondocoins = fondocoinsWon,
                                    experience = experienceWon,
                                )
                            }
                        } else {
                            Text(
                                text = "Has perdut! Símbol guanyador: $revealedSymbol",
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(onClick = { resetGame() }) {
                            Text("Tornar a jugar")
                        }
                    }

                    GameRulesDialog(
                        gameName = "RASCA I GUANYA",
                        rules = listOf(
                            GameRuleSection(
                                title = "💎 COM JUGAR",
                                titleColor = Color(0xFF1E88E5),
                                items = listOf(
                                    "Ingressa la quantitat per apostar",
                                    "Selecciona el símbol que vols trobar",
                                    "Endevina la casella on es troba"
                                )
                            ),
                            GameRuleSection(
                                title = "💰 PREMIS",
                                titleColor = Color(0xFFFFA000),
                                items = listOf(
                                    "Si encertas la casella: Aposta x5",
                                    "Si no encertas la casella: Sense premi"
                                )
                            ),
                            GameRuleSection(
                                title = "🌟 EXPERIÈNCIA",
                                titleColor = Color(0xFF4CAF50),
                                items = listOf(
                                    "Guanyar una partida: +100 XP",
                                    "Perdre una partida: +0 XP"
                                )
                            ),
                            GameRuleSection(
                                title = "ℹ️ IMPORTANT",
                                titleColor = Color(0xFFBA68C8),
                                items = listOf(
                                    "Les ganàncies es sumen automàticament",
                                    "L'experiència s'acumula inmediatament"
                                )
                            ),
                            GameRuleSection(
                                title = "⚠️ ATENCIÓ",
                                titleColor = Color(0xFFE57373),
                                items = listOf(
                                    "Només pots escollir una casella",
                                    "Has de seleccionar un símbol per jugar",
                                    "Aposta mínima d'un fondocoin"
                                )
                            )
                        ),
                        showDialog = showRulesDialog,
                        onDismiss = { showRulesDialog = false }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}