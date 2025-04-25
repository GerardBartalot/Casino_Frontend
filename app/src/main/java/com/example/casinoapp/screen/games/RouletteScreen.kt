package com.example.casinoapp.screen.games

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.casinoapp.R
import com.example.casinoapp.entity.GameSession
import com.example.casinoapp.ui.components.AnimatedNumberDisplayRoulette
import com.example.casinoapp.ui.components.ExperienceProgressBar
import com.example.casinoapp.ui.components.GameRuleSection
import com.example.casinoapp.ui.components.GameRulesDialog
import com.example.casinoapp.viewModel.GameViewModel
import com.example.casinoapp.viewModel.RemoteViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val redNumbers = listOf(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36)
val blackNumbers = listOf(2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouletteScreen(
    navController: NavController,
    gameViewModel: GameViewModel,
    remoteViewModel: RemoteViewModel,
)
{
    var isSpinning by remember { mutableStateOf(false) }
    var resultNumber by remember { mutableStateOf<Int?>(null) }
    var selectedZero by remember { mutableStateOf(false) }
    val rotationAngle = remember { Animatable(0f) }
    var selectedChipValue by remember { mutableIntStateOf(0) }
    var totalBet by remember { mutableIntStateOf(0) }
    var selectedNumbers by remember { mutableStateOf<List<Int>>(emptyList()) }
    var selectedNumbersCurrentBet by remember { mutableStateOf<List<Int>>(emptyList()) }
    var selectedColor by remember { mutableStateOf<String?>(null) }
    var selectedEven by remember { mutableStateOf<Boolean?>(null) }
    var lastBetType by remember { mutableStateOf<String?>(null) }
    var resultMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val loggedInUser by remoteViewModel.loggedInUser.collectAsState()
    val vmFondocoins by gameViewModel.fondocoins.collectAsState()
    val vmExperience by gameViewModel.experience.collectAsState()
    var localFondocoins by remember { mutableIntStateOf(0) }
    var localExperience by remember { mutableIntStateOf(0) }
    var userId by remember { mutableStateOf("") }
    val isWin = remember { mutableStateOf(false) }
    var experienceToAdd by remember { mutableIntStateOf(0) }
    var roundsPlayed by remember { mutableIntStateOf(0) }
    var fondocoinsSpent by remember { mutableIntStateOf(0) }
    var fondocoinsEarned by remember { mutableIntStateOf(0) }
    var experienceEarned by remember { mutableIntStateOf(0) }
    var showRulesDialog by remember { mutableStateOf(false) }

    fun saveGameSession() {
        loggedInUser?.let { user ->
            val gameSession = GameSession(
                user = user,
                gameName = "Roulette",
                rounds = roundsPlayed,
                experienceEarned = experienceEarned,
                fondocoinsSpent = fondocoinsSpent,
                fondocoinsEarned = fondocoinsEarned
            )
            remoteViewModel.saveGameSession(gameSession) { result ->
                Log.d("RouletteScreen", "Game session save result: $result")
            }
        }
    }

    LaunchedEffect(Unit) {
        loggedInUser?.userId?.let {
            userId = it.toString()
            gameViewModel.getUserFondoCoins(it.toInt())
            gameViewModel.getUserExperience(it.toInt())
        }
    }

    LaunchedEffect(vmFondocoins, vmExperience) {
        localFondocoins = vmFondocoins
        localExperience = vmExperience
    }

    fun checkBetResult(number: Int, betValue: Int): Int {
        val evenNumbers = (1..36).filter { it % 2 == 0 }
        val oddNumbers = (1..36).filter { it % 2 != 0 }

        val selectionCount = selectedNumbers.size +
                (if (selectedZero) 1 else 0) +
                (if (selectedColor != null) 1 else 0) +
                (if (selectedEven != null) 1 else 0)

        val betPerSelection = if (selectionCount > 0) betValue / selectionCount else 0

        val win = when (lastBetType) {
            "Número" -> selectedNumbers.contains(number) || (selectedZero && number == 0)
            "Color" -> (selectedColor == "Rojo" && number in redNumbers) || (selectedColor == "Negro" && number in blackNumbers)
            "ParImpar" -> (selectedEven == true && number in evenNumbers) || (selectedEven == false && number in oddNumbers)
            else -> false
        }

        return when (lastBetType) {
            "Número" -> if (win) betPerSelection * 35 else 0
            "Color", "ParImpar" -> if (win) betPerSelection * 2 else 0
            else -> 0
        }
    }

    fun spinRoulette() {
        if (!isSpinning) {
            val betValue = totalBet

            // Verificar si el usuario tiene fondos suficientes
            if (localFondocoins < betValue) {
                resultMessage = "No tienes suficientes fondocoins."
                return
            }

            if (betValue <= 0 || !gameViewModel.placeBet(betValue) ||
                (selectedNumbers.isEmpty() && selectedColor == null && selectedEven == null && !selectedZero)) {
                resultMessage = "Apuesta inválida."
                return
            }

            isSpinning = true
            isWin.value = false
            resultNumber = null
            resultMessage = ""

            coroutineScope.launch {
                rotationAngle.snapTo(0f)

                val fullRotations = 6
                val randomPosition = (0..36).random()
                val degreesPerNumber = 360f / 37f
                val targetRotation = 360f * fullRotations + randomPosition * degreesPerNumber

                rotationAngle.animateTo(
                    targetValue = targetRotation,
                    animationSpec = tween(durationMillis = 4000, easing = LinearOutSlowInEasing)
                )

                val finalPosition = (rotationAngle.value % 360) / degreesPerNumber
                val winningNumber = (finalPosition.toInt() + 1) % 37
                resultNumber = winningNumber
                isSpinning = false

                val winnings = checkBetResult(winningNumber, betValue)
                val hasWon = winnings > 0
                experienceToAdd = if (hasWon) 500 else 0

                roundsPlayed++
                fondocoinsSpent += betValue

                if (hasWon) {
                    localFondocoins += winnings
                    localExperience += experienceToAdd
                    fondocoinsEarned += winnings
                    experienceEarned += experienceToAdd
                }

                // Generar el mensaje de resultado detallado
                val redNumbers = listOf(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36)
                val blackNumbers = listOf(2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35)

                val colorResult = when {
                    winningNumber in redNumbers -> "Rojo"
                    winningNumber in blackNumbers -> "Negro"
                    else -> "Verde (0)"
                }

                val evenOddResult = when {
                    winningNumber == 0 -> "0"
                    winningNumber % 2 == 0 -> "Par"
                    else -> "Impar"
                }

                resultMessage = if (hasWon) {
                    isWin.value = true
                    "¡Has guanyat! Número: $winningNumber ($colorResult, $evenOddResult)\n" +
                            "Guanys: +${winnings} fondocoins (+500 XP)"
                } else {
                    "¡Has perdut! Número: $winningNumber ($colorResult, $evenOddResult)\n" +
                            "Pèrdues: -${betValue} fondocoins"
                }

                // Esperar para mostrar el mensaje y luego reiniciar
                delay(3000)

                // Reiniciar todos los valores de apuesta
                totalBet = 0
                selectedChipValue = 0
                selectedNumbers = emptyList()
                selectedNumbersCurrentBet = emptyList()
                selectedColor = null
                selectedEven = null
                selectedZero = false
                resultNumber = null
                resultMessage = ""
                isWin.value = false
            }
        }
    }

    val isBetValid = remember {
        derivedStateOf {
            totalBet > 0 && (selectedNumbers.isNotEmpty() || selectedColor != null || selectedEven != null || selectedZero)
        }
    }

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1E8449),
            Color(0xFF1E8449),
            Color(0xFF0D420D)
        ),
        startY = 0f,
        endY = 1000f
    )

    Scaffold(
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
                            text = "Ruleta",
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
                            onClick = { showRulesDialog = true },
                            modifier = Modifier
                                .size(30.dp)
                                .border(2.dp, Color.Green, CircleShape)
                                .clip(CircleShape),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.White
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
        ) {
            Image(
                painter = painterResource(id = R.drawable.subtle_texture1),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.4f),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
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

                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.roulette),
                        contentDescription = "Ruleta",
                        modifier = Modifier
                            .size(250.dp)
                            .graphicsLayer(rotationZ = rotationAngle.value % 360)
                    )
                }

                if (resultMessage.isEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(3f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(3f),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Chip(
                                        value = 1,
                                        selected = selectedChipValue == 1,
                                        onSelected = { selectedChipValue = 1 },
                                        drawableId = R.drawable.fichas_poker1,
                                        modifier = Modifier.size(50.dp),
                                        availableFunds = localFondocoins
                                    )
                                    Chip(
                                        value = 5,
                                        selected = selectedChipValue == 5,
                                        onSelected = { selectedChipValue = 5 },
                                        drawableId = R.drawable.fichas_poker5,
                                        modifier = Modifier.size(50.dp),
                                        availableFunds = localFondocoins
                                    )
                                    Chip(
                                        value = 25,
                                        selected = selectedChipValue == 25,
                                        onSelected = { selectedChipValue = 25 },
                                        drawableId = R.drawable.fichas_poker25,
                                        modifier = Modifier.size(50.dp),
                                        availableFunds = localFondocoins
                                    )
                                    Chip(
                                        value = 50,
                                        selected = selectedChipValue == 50,
                                        onSelected = { selectedChipValue = 50 },
                                        drawableId = R.drawable.fichas_poker50,
                                        modifier = Modifier.size(50.dp),
                                        availableFunds = localFondocoins
                                    )
                                }

                                Button(
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFFD700),
                                        disabledContainerColor = Color(0x80FFD700)
                                    ),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 8.dp)
                                        .height(50.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    onClick = {
                                        lastBetType = when {
                                            selectedNumbers.isNotEmpty() -> "Número"
                                            selectedColor != null -> "Color"
                                            selectedEven != null -> "ParImpar"
                                            selectedZero -> "Número"
                                            else -> null
                                        }
                                        spinRoulette()
                                    },
                                    enabled = isBetValid.value && !isSpinning
                                ) {
                                    Text("Girar", fontSize = 16.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(15.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        if (selectedChipValue > 0) {
                                            val selectionCount = selectedNumbersCurrentBet.size +
                                                    (if (selectedZero) 1 else 0) +
                                                    (if (selectedColor != null) 1 else 0) +
                                                    (if (selectedEven != null) 1 else 0)
                                            if (selectionCount > 0) {
                                                totalBet += selectedChipValue * selectionCount
                                                selectedNumbers =
                                                    selectedNumbers.plus(selectedNumbersCurrentBet)
                                                selectedNumbersCurrentBet = emptyList()
                                                selectedChipValue = 0
                                            }
                                        }
                                    },
                                    shape = RoundedCornerShape(4.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF4CAF50)
                                    ),
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier
                                        .width(100.dp)
                                        .height(40.dp)
                                ) {
                                    Text("Afegir", fontSize = 16.sp)
                                }

                                Box(
                                    modifier = Modifier
                                        .height(40.dp)
                                        .width(120.dp)
                                        .background(
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(
                                                    Color(0xFF4A148C),
                                                    Color(0xFF1A237E)
                                                ),
                                                startX = 0f,
                                                endX = Float.POSITIVE_INFINITY
                                            ),
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .border(
                                            width = 2.dp,
                                            color = Color.White,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .shadow(
                                            elevation = 8.dp,
                                            shape = RoundedCornerShape(4.dp),
                                            clip = false,
                                            ambientColor = Color.Yellow,
                                            spotColor = Color.Yellow
                                        )
                                        .background(Color(0xFF222222), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "$totalBet",
                                        style = TextStyle(
                                            fontSize = 20.sp,
                                            fontFamily = FontFamily.SansSerif,
                                            color = Color.White,
                                            shadow = Shadow(
                                                color = Color.Yellow,
                                                offset = Offset(2f, 2f),
                                                blurRadius = 8f
                                            )
                                        )
                                    )
                                }

                                Button(
                                    onClick = {
                                        totalBet = 0
                                        selectedChipValue = 0
                                        selectedNumbers = emptyList()
                                        selectedNumbersCurrentBet = emptyList()
                                        selectedColor = null
                                        selectedEven = null
                                        selectedZero = false
                                    },
                                    shape = RoundedCornerShape(4.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFF44336)
                                    ),
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier
                                        .width(100.dp)
                                        .height(40.dp)
                                ) {
                                    Text("Reiniciar", fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }

                if (resultNumber != null) {
                    val winAmount = checkBetResult(resultNumber!!, totalBet)
                    val isWin = winAmount > 0

                    AnimatedNumberDisplayRoulette(
                        fondocoins = if (isWin) winAmount else -totalBet,
                        experience = if (isWin) 500 else 0,
                        title = if (isWin) "¡HAS GUANYAT!" else "¡HAS PERDUT!",
                        titleColor = if (isWin) Color.Green else Color.Red,
                        titleShadowColor = if (isWin) Color(0xFF00FF00) else Color(0xFFFF0000),
                        resultNumber = resultNumber,
                        colorResult = when {
                            resultNumber!! in redNumbers -> "Rojo"
                            resultNumber!! in blackNumbers -> "Negro"
                            else -> "Verde (0)"
                        },
                        evenOddResult = when {
                            resultNumber!! == 0 -> "0"
                            resultNumber!! % 2 == 0 -> "Par"
                            else -> "Impar"
                        },
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }

                BetSelection(
                    selectedNumbersCurrentBet = selectedNumbersCurrentBet,
                    onNumberSelected = { numbers ->
                        if (numbers.isNotEmpty()) {
                            selectedColor = null
                            selectedEven = null
                            selectedZero = false
                        }
                        selectedNumbersCurrentBet = numbers
                    },
                    selectedColor = selectedColor,
                    selectedZero = selectedZero,
                    onColorSelected = { color, zero ->
                        selectedNumbersCurrentBet = emptyList()
                        selectedEven = null
                        selectedColor = color
                        selectedZero = zero
                    },
                    selectedEven = selectedEven,
                    onEvenOddSelected = { even ->
                        selectedNumbersCurrentBet = emptyList()
                        selectedColor = null
                        selectedZero = false
                        selectedEven = if (selectedEven == even) null else even
                    },
                    selectedChipValue = selectedChipValue,
                    selectedNumbers = selectedNumbers,
                    localFondocoins = localFondocoins
                )

                if (showRulesDialog) {
                    GameRulesDialog(
                        gameName = "RULETA",
                        rules = listOf(
                            GameRuleSection(
                                title = "💎 APOSTES",
                                titleColor = Color(0xFF1E88E5),
                                items = listOf(
                                    "Fiches de 1, 15, 25 y 50 fondocoins",
                                    "El premi es calcula sobre la base de la teva aposta",
                                    "Pots seleccionar una o varies caselles per ficha apostada",
                                    "Es poden fer diverses apostes amb diferents fiches"
                                )
                            ),
                            GameRuleSection(
                                title = "💰 PREMIS",
                                titleColor = Color(0xFFFFA000),
                                items = listOf(
                                    "Si encertes la casella, guanyaràs el valor apostat en la mateixa",
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
                                    "Abans de seleccionar una casella, has de seleccionar una ficha"
                                )
                            ),
                        ),
                        showDialog = showRulesDialog,
                        onDismiss = { showRulesDialog = false }
                    )
                }
            }
        }
    }
}

@Composable
fun BetSelection(
    selectedNumbersCurrentBet: List<Int>,
    onNumberSelected: (List<Int>) -> Unit,
    selectedColor: String?,
    selectedZero: Boolean,
    onColorSelected: (String?, Boolean) -> Unit,
    selectedEven: Boolean?,
    onEvenOddSelected: (Boolean) -> Unit,
    selectedChipValue: Int,
    selectedNumbers: List<Int>,
    localFondocoins: Int
) {
    NumberTable(
        selectedNumbersCurrentBet = selectedNumbersCurrentBet,
        onNumberSelected = onNumberSelected,
        selectedZero = selectedZero,
        selectedColor = selectedColor,
        onColorSelected = onColorSelected,
        selectedEven = selectedEven,
        onEvenOddSelected = onEvenOddSelected,
        selectedChipValue = selectedChipValue,
        selectedNumbers = selectedNumbers,
        localFondocoins = localFondocoins
    )
}

@Composable
fun NumberTable(
    selectedNumbersCurrentBet: List<Int>,
    selectedNumbers: List<Int>,
    onNumberSelected: (List<Int>) -> Unit,
    selectedZero: Boolean,
    selectedColor: String?,
    onColorSelected: (String?, Boolean) -> Unit,
    selectedEven: Boolean?,
    onEvenOddSelected: (Boolean) -> Unit,
    selectedChipValue: Int,
    localFondocoins: Int
) {
    val redNumbers = listOf(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF2E2E2E))
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (selectedEven == true) Color(0xFFFFD700) else Color(0xFF555555))
                    .clickable(enabled = selectedChipValue > 0) { onEvenOddSelected(true) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Parell",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Box(
                modifier = Modifier
                    .width(50.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (selectedZero) Color(0xFFFFD700) else Color(0xFF2196F3))
                    .clickable(enabled = selectedChipValue > 0) { onColorSelected(null, !selectedZero) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "0",
                    color = Color.White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (selectedEven == false) Color(0xFFFFD700) else Color(0xFF555555))
                    .clickable(enabled = selectedChipValue > 0) { onEvenOddSelected(false) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Imparell",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        val numbers = (1..36).toList()
        val rows = numbers.chunked(9)

        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { number ->
                    Box(
                        modifier = Modifier
                            .size(35.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when {
                                    selectedNumbersCurrentBet.contains(number) || selectedNumbers.contains(number) -> Color(0xFFFFD700)
                                    number in redNumbers -> Color.Red
                                    else -> Color.Black
                                }
                            )
                            .clickable(enabled = selectedChipValue > 0 && localFondocoins >= selectedChipValue) {
                                val newSelection = if (selectedNumbersCurrentBet.contains(number)) {
                                    selectedNumbersCurrentBet - number
                                } else {
                                    selectedNumbersCurrentBet + number
                                }
                                onNumberSelected(newSelection)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = number.toString(),
                            color = Color.White,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (selectedColor == "Rojo") Color(0xFFFFD700) else Color.Red)
                    .clickable(enabled = selectedChipValue > 0) {
                        onColorSelected(if (selectedColor == "Rojo") null else "Rojo", false)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Vermell",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (selectedColor == "Negro") Color(0xFFFFD700) else Color.Black)
                    .clickable(enabled = selectedChipValue > 0) {
                        onColorSelected(if (selectedColor == "Negro") null else "Negro", false)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Negre",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun Chip(
    value: Int,
    selected: Boolean,
    onSelected: (Int) -> Unit,
    drawableId: Int,
    modifier: Modifier = Modifier,
    availableFunds: Int
) {
    val enabled = value <= availableFunds
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable { onSelected(value) }
            .border(
                width = if (selected) 3.dp else 0.dp,
                color = if (selected) Color.Yellow else Color.Transparent,
                shape = CircleShape
            )
            .alpha(if (enabled) 1f else 0.5f)
    ) {
        Image(
            painter = painterResource(id = drawableId),
            contentDescription = "Ficha de $value",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RouletteScreenPreview() {
    RouletteScreen(
        navController = rememberNavController(),
        remoteViewModel = RemoteViewModel(),
        gameViewModel = GameViewModel()
    )
}