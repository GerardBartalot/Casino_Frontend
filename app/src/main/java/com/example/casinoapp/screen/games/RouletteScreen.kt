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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
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
    val rotationAngle = remember { Animatable(0f) }
    var betAmount by remember { mutableStateOf(TextFieldValue("")) }
    var selectedNumbers by remember { mutableStateOf<List<Int>>(emptyList()) }
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
        val redNumbers = listOf(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36)
        val blackNumbers = listOf(2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35)
        val evenNumbers = (1..36).filter { it % 2 == 0 }
        val oddNumbers = (1..36).filter { it % 2 != 0 }

        val win = when (lastBetType) {
            "Número" -> selectedNumbers.contains(number)
            "Color" -> (selectedColor == "Rojo" && number in redNumbers) || (selectedColor == "Negro" && number in blackNumbers)
            "ParImpar" -> (selectedEven == true && number in evenNumbers) || (selectedEven == false && number in oddNumbers)
            else -> false
        }

        return when (lastBetType) {
            "Número" -> if (win) betValue * 35 else 0
            "Color", "ParImpar" -> if (win) betValue * 2 else 0
            else -> 0
        }
    }

    fun spinRoulette() {
        if (!isSpinning) {
            var betValue = betAmount.text.toIntOrNull() ?: 0

            if (betValue <= 0 || !gameViewModel.placeBet(betValue) ||
                (selectedNumbers.isEmpty() && selectedColor == null && selectedEven == null)) {
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

                // Calcular el número ganador
                val finalPosition = (rotationAngle.value % 360) / degreesPerNumber
                val winningNumber = (finalPosition.toInt() + 1) % 37
                resultNumber = winningNumber
                isSpinning = false

                // Resto de la lógica de cálculo de premios...
                val winnings = checkBetResult(winningNumber, betValue)
                val hasWon = winnings > 0
                experienceToAdd = if (hasWon) 50 else 0

                roundsPlayed++
                fondocoinsSpent += betValue

                if (hasWon) {
                    localFondocoins += winnings
                    localExperience += experienceToAdd
                    fondocoinsEarned += winnings
                    experienceEarned += experienceToAdd
                }

                resultMessage = if (winnings > 0) {
                    isWin.value = true
                    "¡Ganaste! Número: $winningNumber (+$winnings)"
                } else {
                    "¡Perdiste! Número: $winningNumber (-$betValue)"
                }

                if (winnings > 0) {
                    delay(5000)
                    isWin.value = false
                }
            }
        }
    }

    val isBetValid = remember {
        derivedStateOf {
            betAmount.text.toIntOrNull()?.let { it > 0 } == true &&
                    (selectedNumbers.isNotEmpty() || selectedColor != null || selectedEven != null)
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
                    // Fondoscoins
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        PixelDisplay(vmFondocoins)
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

                Spacer(modifier = Modifier.height(20.dp))

                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(250.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.roulette),
                        contentDescription = "Ruleta",
                        modifier = Modifier
                            .size(250.dp)
                            .graphicsLayer(rotationZ = rotationAngle.value % 360)
                    )
                }

                Spacer(modifier = Modifier.height(if (resultNumber == null) 20.dp else 20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedCard(
                        modifier = Modifier
                            .width(180.dp)
                            .height(50.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White)
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                BasicTextField(
                                    value = betAmount.text,
                                    onValueChange = { betAmount = TextFieldValue(it) },
                                    textStyle = TextStyle(
                                        fontSize = 18.sp,
                                        color = Color.Black,
                                        textAlign = TextAlign.Center
                                    ),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    decorationBox = { innerTextField ->
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            if (betAmount.text.isEmpty()) {
                                                Text(
                                                    text = "Apuesta",
                                                    fontSize = 18.sp,
                                                    color = Color.Gray,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                            innerTextField()
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD700),
                            disabledContainerColor = Color(0x80FFD700)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(50.dp),
                        onClick = {
                            lastBetType = when {
                                selectedNumbers.isNotEmpty() -> "Número"
                                selectedColor != null -> "Color"
                                selectedEven != null -> "ParImpar"
                                else -> null
                            }
                            spinRoulette()
                        },
                        enabled = isBetValid.value && !isSpinning
                    ) {
                        Text("GIRA!")
                    }
                }

                Spacer(modifier = Modifier.height(5.dp))

                resultNumber?.let { number ->
                    val redNumbers =
                        listOf(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36)
                    val blackNumbers =
                        listOf(2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35)
                    val evenNumbers = (1..36).filter { it % 2 == 0 }
                    val oddNumbers = (1..36).filter { it % 2 != 0 }
                    val selectedZero by remember { mutableStateOf(false) }

                    val colorWinner = when {
                        number in redNumbers -> "Rojo"
                        number in blackNumbers -> "Negro"
                        else -> "Verde"
                    }

                    val evenOddWinner = when {
                        number in evenNumbers -> "Par"
                        number in oddNumbers -> "Impar"
                        else -> "Ninguno"
                    }

                    val isWin = when (lastBetType) {
                        "Número" -> selectedNumbers.contains(number) || (selectedZero && number == 0)
                        "Color" -> (selectedColor == "Rojo" && number in redNumbers) || (selectedColor == "Negro" && number in blackNumbers)
                        "ParImpar" -> (selectedEven == true && number in evenNumbers) || (selectedEven == false && number in oddNumbers)
                        else -> false
                    }

                    val resultText = when (lastBetType) {
                        "Número" -> if (isWin) "¡Ganaste! Número: $number (+50 XP)" else "¡Perdiste! Número: $number"
                        "Color" -> if (isWin) "¡Ganaste! Color: $colorWinner (+50 XP)" else "¡Perdiste! Color: $colorWinner"
                        "ParImpar" -> if (isWin) "¡Ganaste! Evento: $evenOddWinner (+50 XP)" else "¡Perdiste! Evento: $evenOddWinner"
                        else -> if (isWin) "¡Ganaste! (+50 XP)" else "¡Perdiste!"
                    }

                    Text(
                        text = resultText,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(if (resultNumber == null) 18.dp else 40.dp))

                var selectedZero by remember { mutableStateOf(false) }

                BetSelection(
                    selectedNumbers = selectedNumbers,
                    onNumberSelected = { numbers ->
                        if (numbers.isNotEmpty()) {
                            selectedColor = null
                            selectedEven = null
                            selectedZero = false
                        }
                        selectedNumbers = numbers
                    },
                    selectedColor = selectedColor,
                    selectedZero = selectedZero,
                    onColorSelected = { color, zero ->
                        selectedNumbers = emptyList()
                        selectedEven = null
                        selectedColor = color
                        selectedZero = zero
                    },
                    selectedEven = selectedEven,
                    onEvenOddSelected = { even ->
                        selectedNumbers = emptyList()
                        selectedColor = null
                        selectedZero = false
                        selectedEven = if (selectedEven == even) null else even
                    }
                )

                if (showRulesDialog) {
                    GameRulesDialog(
                        gameName = "RULETA",
                        rules = listOf(
                            GameRuleSection(
                                title = "💎 APOSTES",
                                titleColor = Color(0xFF1E88E5),
                                items = listOf(
                                    "Tirades de 10, 20 o 50 fondocoins",
                                    "El premi es calcula sobre la base de la teva aposta"
                                )
                            ),
                            GameRuleSection(
                                title = "💰 PREMIS",
                                titleColor = Color(0xFFFFA000),
                                items = listOf(
                                    "3 símbols iguals: Aposta × 10",
                                    "2 símbols iguals: Aposta × 2",
                                    "Tots diferents: Sense premi"
                                )
                            ),
                            GameRuleSection(
                                title = "🌟 EXPERIÈNCIA",
                                titleColor = Color(0xFF4CAF50),
                                items = listOf(
                                    "3 símbols iguals: +15 XP",
                                    "2 símbols iguals: +5 XP",
                                    "Tots diferents: +0 XP"
                                )
                            ),
                            GameRuleSection(
                                title = "ℹ️ IMPORTANT",
                                titleColor = Color(0xFFBA68C8),
                                items = listOf(
                                    "Las ganancias se acumulan hasta hacer CASH OUT",
                                    "La experiencia se suma automáticamente",
                                )
                            ),
                            GameRuleSection(
                                title = "⚠️ ATENCIÓ",
                                titleColor = Color(0xFFE57373),
                                items = listOf(
                                    "Si antes de salir no haces CASH OUT perderás tus ganancias"
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

@Composable
fun BetSelection(
    selectedNumbers: List<Int>,
    onNumberSelected: (List<Int>) -> Unit,
    selectedColor: String?,
    selectedZero: Boolean,
    onColorSelected: (String?, Boolean) -> Unit,
    selectedEven: Boolean?,
    onEvenOddSelected: (Boolean) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        NumberTable(selectedNumbers, onNumberSelected)
        Spacer(modifier = Modifier.height(10.dp))
        ColorTable(selectedColor, selectedZero, onColorSelected)
        Spacer(modifier = Modifier.height(10.dp))
        EvenOddTable(selectedEven, onEvenOddSelected)
    }
}

@Composable
fun NumberTable(selectedNumbers: List<Int>, onNumberSelected: (List<Int>) -> Unit) {
    val tableNumbers = listOf(
        listOf(3, 6, 9, 12, 15, 18, 21, 24, 27),
        listOf(30, 33, 36, 2, 5, 8, 11, 14, 17),
        listOf(20, 23, 26, 29, 32, 35, 1, 4, 7),
        listOf(10, 13, 16, 19, 22, 25, 28, 31, 34)
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        tableNumbers.forEach { row ->
            Row {
                row.forEach { number ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                when {
                                    selectedNumbers.contains(number) -> Color.Yellow
                                    number == 0 -> Color.Green
                                    listOf(3, 9, 12, 18, 21, 27, 30, 36, 5, 14, 23, 32, 1,
                                        7, 16, 19, 25, 34).contains(number) -> Color.Red
                                    else -> Color.Black
                                }
                            )
                            .clickable {
                                val newSelection = if (selectedNumbers.contains(number)) {
                                    selectedNumbers - number
                                } else {
                                    selectedNumbers + number
                                }
                                onNumberSelected(newSelection)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = number.toString(),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ColorTable(selectedColor: String?, selectedZero: Boolean, onColorSelected: (String?, Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(47.dp)
                .background(if (selectedColor == "Rojo") Color.Yellow else Color.Red)
                .clickable { onColorSelected(if (selectedColor == "Rojo") null else "Rojo", false) },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Color Vermell", color = Color.White)
        }

        Spacer(modifier = Modifier.width(3.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(47.dp)
                .background(if (selectedZero) Color.Yellow else Color.Green)
                .clickable { onColorSelected(null, !selectedZero) },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "0", color = Color.White)
        }

        Spacer(modifier = Modifier.width(3.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(47.dp)
                .background(if (selectedColor == "Negro") Color.Yellow else Color.Black)
                .clickable { onColorSelected(if (selectedColor == "Negro") null else "Negro", false) },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Color Negre", color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))

    }
}

@Composable
fun EvenOddTable(selectedEven: Boolean?, onEvenOddSelected: (Boolean) -> Unit) {

    Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(47.dp)
                .background(if (selectedEven == true) Color.Yellow else Color(0xFF555555))
                .clickable { onEvenOddSelected(true) },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Número Parell", color = Color.White)
        }

        Spacer(modifier = Modifier.width(2.5.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(47.dp)
                .background(if (selectedEven == false) Color.Yellow else Color(0xFF555555))
                .clickable { onEvenOddSelected(false) },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Número Imparell", color = Color.White)
        }
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