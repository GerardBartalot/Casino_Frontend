package com.example.casinoapp.screen.games

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.casinoapp.R
import com.example.casinoapp.entity.GameSession
import com.example.casinoapp.ui.components.ExperienceProgressBar
import com.example.casinoapp.ui.components.GameRuleSection
import com.example.casinoapp.ui.components.GameRulesDialog
import com.example.casinoapp.viewModel.GameViewModel
import com.example.casinoapp.viewModel.RemoteViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class Card(
    val value: String, val suit: String, val imageResId: Int
)

class Deck(
    private val context: android.content.Context
) {
    private val suits = listOf("hearts", "diamonds", "clubs", "spades")
    private val values = listOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
    private val cards = mutableListOf<Card>()

    init {
        for (suit in suits) {
            for (value in values) {
                val name = "_${value.lowercase()}${suit}"
                val resId = context.resources.getIdentifier(name, "drawable", context.packageName)
                cards.add(Card(value, suit, resId))
            }
        }
        cards.shuffle()
    }

    fun draw(): Card = cards.removeAt(0)
}

fun handValue(hand: List<Card>): Int {
    var total = 0
    var aces = 0
    for (card in hand) {
        total += when (card.value) {
            "A" -> {
                aces++
                11
            }
            "K", "Q", "J" -> 10
            else -> card.value.toInt()
        }
    }
    while (total > 21 && aces > 0) {
        total -= 10
        aces--
    }
    return total
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BlackjackScreen(
    navController: NavController,
    remoteViewModel: RemoteViewModel,
    gameViewModel: GameViewModel
) {
    val context = LocalContext.current
    val loggedInUser by remoteViewModel.loggedInUser.collectAsState()
    var userId by remember { mutableStateOf("") }
    var deck by remember { mutableStateOf(Deck(context)) }
    var playerHand by remember { mutableStateOf(listOf(deck.draw(), deck.draw())) }
    var dealerHand by remember { mutableStateOf(listOf(deck.draw(), deck.draw())) }
    var currentBet by remember { mutableIntStateOf(1000) }
    var gameEnded by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var showRulesDialog by remember { mutableStateOf(false) }
    var roundsPlayed by remember { mutableIntStateOf(0) }
    var fondocoinsSpent by remember { mutableIntStateOf(0) }
    var fondocoinsEarned by remember { mutableIntStateOf(0) }
    var experienceEarned by remember { mutableIntStateOf(0) }
    val vmFondocoins by gameViewModel.fondocoins.collectAsState()
    val vmExperience by gameViewModel.experience.collectAsState()
    var localFondocoins by remember { mutableIntStateOf(0) }
    var localExperience by remember { mutableIntStateOf(0) }
    var betInput by remember { mutableStateOf("") }

    LaunchedEffect(loggedInUser) {
        loggedInUser?.userId?.let {
            userId = it.toString()
            gameViewModel.getUserFondoCoins(it)
        }
    }

    LaunchedEffect(vmFondocoins, vmExperience) {
        localFondocoins = vmFondocoins
        localExperience = vmExperience
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

    fun reset() {
        deck = Deck(context)
        playerHand = listOf(deck.draw(), deck.draw())
        dealerHand = listOf(deck.draw(), deck.draw())
        gameEnded = false
        message = ""
    }

    fun saveGameSession() {
        loggedInUser?.let { user ->
            val slotMachineGame = gameViewModel.games.value.find {
                it.gameName.equals("Black Jack", ignoreCase = true)
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
                    Log.d("Black Jack", "Game session save result: $result")
                }
            } ?: run {
                Log.e("Black Jack", "No se pudo encontrar el juego 'Black Jack'")
            }
        }
    }

    fun dealerTurn() {
        while (handValue(dealerHand) < 17) {
            dealerHand = dealerHand + deck.draw()
        }
    }

    fun resolveGame() {
        dealerTurn()
        val playerScore = handValue(playerHand)
        val dealerScore = handValue(dealerHand)

        message = when {
            playerScore > 21 -> "Te pasaste. Perdiste."
            dealerScore > 21 -> {
                userId.toIntOrNull()?.let { id ->
                    gameViewModel.updateUserFondoCoins(id, currentBet * 2)
                }
                "El crupier se pasó. ¡Ganaste!"
            }
            playerScore > dealerScore -> {
                userId.toIntOrNull()?.let { id ->
                    gameViewModel.updateUserFondoCoins(id, currentBet * 2)
                }
                "¡Ganaste!"
            }
            playerScore == dealerScore -> {
                userId.toIntOrNull()?.let { id ->
                    gameViewModel.updateUserFondoCoins(id, currentBet)
                }
                "Empate"
            }
            else -> "Perdiste"
        }
        gameEnded = true
    }

    Scaffold (
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .height(100.dp)
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
                            text = "Black Jack",
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
                    .background(Color(0xFF3BB143))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(100.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        PixelDisplay(vmFondocoins)
                    }

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

                Spacer(Modifier.height(16.dp))

                // Crupier
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = Color(0xFFFFF176)
                    ) {
                        Text("Màquina", Modifier.padding(8.dp), fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = Color.White
                    ) {
                        Text("${handValue(dealerHand)}", Modifier.padding(8.dp))
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.Center) {
                    dealerHand.forEachIndexed { index, card ->
                        val show = index != 0 || gameEnded
                        Image(
                            painter = painterResource(
                                id = if (show) card.imageResId else R.drawable.card_back
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .size(60.dp, 90.dp)
                                .padding(2.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Spacer(Modifier.height(12.dp))

                // Apuesta
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = Color(0xFFFFF176)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Aposta:  $currentBet", fontSize = 18.sp)
                        Spacer(Modifier.width(4.dp))
                        Image(
                            painter = painterResource(id = R.drawable.fondocoin),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Agregar el campo de entrada para la apuesta
                OutlinedTextField(
                    value = betInput,
                    onValueChange = {
                        betInput = it
                    },
                    label = { Text("Introduce tu apuesta") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    isError = betInput.toIntOrNull() == null || betInput.toIntOrNull()!! > localFondocoins
                )

                Spacer(Modifier.height(12.dp))

                // Botón para confirmar la apuesta
                Button(
                    onClick = {
                        val bet = betInput.toIntOrNull()
                        if (bet != null && bet > 0 && bet <= localFondocoins) {
                            currentBet = bet
                        } else {
                            message = "Por favor, ingresa una apuesta válida."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                    modifier = Modifier.size(200.dp, 50.dp)
                ) {
                    Text("Confirmar Apuesta", fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(Modifier.height(16.dp))

                // Mano del jugador
                Row(horizontalArrangement = Arrangement.Center) {
                    playerHand.forEach { card ->
                        Image(
                            painter = painterResource(id = card.imageResId),
                            contentDescription = null,
                            modifier = Modifier
                                .size(60.dp, 90.dp)
                                .padding(2.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Acciones
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            playerHand = playerHand + deck.draw()
                            if (handValue(playerHand) > 21) resolveGame()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.size(100.dp, 50.dp)
                    ) {
                        Text("Demanar", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Button(
                        onClick = { resolveGame() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow),
                        modifier = Modifier.size(100.dp, 50.dp)
                    ) {
                        Text("Passar", fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Player info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = Color(0xFFFFF176)
                    ) {
                        Text("Jugador", Modifier.padding(8.dp), fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = Color.White
                    ) {
                        Text("${handValue(playerHand)}", Modifier.padding(8.dp))
                    }
                }

                if (gameEnded) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        message,
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Button(onClick = { reset() }) {
                        Text("Jugar una altra vegada")
                    }
                }

                if (showRulesDialog) {
                    GameRulesDialog(
                        gameName = "BLACK JACK",
                        rules = listOf(
                            GameRuleSection(
                                title = "💎 APOSTES",
                                titleColor = Color(0xFF1E88E5),
                                items = listOf(
                                    "Fiches de 1, 5, 25 y 50 fondocoins",
                                    "Pots apostar a números individuals, colors (vermell/negre) o parell/imparell",
                                    "El 0 és verd i no compta com a parell ni imparell",
                                    "Es poden fer múltiples apostes en un mateix gir"
                                )
                            ),
                            GameRuleSection(
                                title = "💰 PREMIS",
                                titleColor = Color(0xFFFFA000),
                                items = listOf(
                                    "Número individual (inclòs 0): 35:1 (aposta × 35)",
                                    "Vermell o Negre: 1:1 (aposta × 2)",
                                    "Parell o Senar: 1:1 (aposta × 2)",
                                    "Si surt 0, perds totes les apostes a color/parell/imparell"
                                )
                            ),
                            GameRuleSection(
                                title = "🌟 EXPERIÈNCIA",
                                titleColor = Color(0xFF4CAF50),
                                items = listOf(
                                    "Guanyar: +500 XP",
                                    "Perdre: +0 XP"
                                )
                            ),
                            GameRuleSection(
                                title = "ℹ️ IMPORTANT",
                                titleColor = Color(0xFFBA68C8),
                                items = listOf(
                                    "Selecciona una ficha abans de fer una aposta",
                                    "El 0 no té color i no compta com a parell ni imparell",
                                    "Les apostes es confirmen al clicar 'Afegir'"
                                )
                            )
                        ),
                        showDialog = showRulesDialog,
                        onDismiss = { showRulesDialog = false }
                    )
                }
            }
        }
    }
}