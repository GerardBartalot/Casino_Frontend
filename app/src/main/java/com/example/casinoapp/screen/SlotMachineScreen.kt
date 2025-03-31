package com.example.casinoapp.screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.casinoapp.R
import com.example.casinoapp.entity.GameSession
import com.example.casinoapp.viewModel.GameViewModel
import com.example.casinoapp.viewModel.RemoteViewModel
import kotlinx.coroutines.delay
import kotlin.math.roundToLong
import kotlin.random.Random
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.draw.clip

@Composable
fun SlotMachineScreen(
    navController: NavController,
    remoteViewModel: RemoteViewModel,
    gameViewModel: GameViewModel,
) {
    val symbols = listOf("bar", "diamond", "heart", "seven", "watermelon", "horseshoe")
    var targetSymbols by remember { mutableStateOf(List(3) { symbols.random() }) }
    var isAnimating by remember { mutableStateOf(false) }
    var startAnimation by remember { mutableStateOf(false) }
    var completedAnimations by remember { mutableIntStateOf(0) }
    val loggedInUser by remoteViewModel.loggedInUser.collectAsState()
    val vmFondocoins by gameViewModel.fondocoins.collectAsState()
    val vmExperience by gameViewModel.experience.collectAsState()
    var userId by remember { mutableStateOf("") }
    var scoreMessage by remember { mutableStateOf("") }
    var currentBet by remember { mutableIntStateOf(0) }
    var localFondocoins by remember { mutableIntStateOf(0) }
    var localExperience by remember { mutableIntStateOf(0) }
    val isWin = remember { mutableStateOf(false) }
    val diamondXP by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.diamond_xp))
    var roundsPlayed by remember { mutableIntStateOf(0) }
    var fondocoinsSpent by remember { mutableIntStateOf(0) }
    var fondocoinsEarned by remember { mutableIntStateOf(0) }
    var experienceEarned by remember { mutableIntStateOf(0) }

    fun saveGameSession() {
        loggedInUser?.let { user ->
            val gameSession = GameSession(
                user = user,
                gameName = "Slot Machine",
                rounds = roundsPlayed,
                experienceEarned = experienceEarned,
                fondocoinsSpent = fondocoinsSpent,
                fondocoinsEarned = fondocoinsEarned
            )
            remoteViewModel.saveGameSession(gameSession) { result ->
                Log.d("SlotMachineScreen", "Game session save result: $result")
            }
        }
    }

    // Lottie Composition for the coin rain animation
    val coinsRain by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.lluvia_monedas)
    )

    LaunchedEffect(Unit) {
        loggedInUser?.userId?.toInt()?.let {
            userId = it.toString()
            gameViewModel.getUserFondoCoins(it)
            gameViewModel.getUserExperience(it)
        }
    }

    LaunchedEffect(vmFondocoins, vmExperience) {
        localFondocoins = vmFondocoins
        localExperience = vmExperience
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF228B22))
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        userId.toIntOrNull()?.let { id ->
                            gameViewModel.updateUserFondoCoins(id, localFondocoins)
                            gameViewModel.updateUserExperience(id, localExperience)
                        }
                        saveGameSession()
                        navController.popBackStack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver atrás",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Fondocoins
                        Image(
                            painter = painterResource(id = R.drawable.fondocoin),
                            contentDescription = "Fondocoin",
                            modifier = Modifier.size(70.dp)
                        )
                        Text(
                            "$localFondocoins",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 30.sp),
                            modifier = Modifier.padding(start = 4.dp, end = 16.dp)
                        )

                        // Experiencia
                        LottieAnimation(
                            composition = diamondXP,
                            iterations = LottieConstants.IterateForever,
                            modifier = Modifier.size(70.dp)
                        )
                        Text(
                            "$localExperience",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 30.sp),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Text(
                        text = scoreMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )

                    // Frame image
                    Box(
                        modifier = Modifier
                            .size(340.dp, 180.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.frame),
                            contentDescription = "Frame",
                            modifier = Modifier.fillMaxSize()
                        )

                        // Slots dentro del marco
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(30.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            targetSymbols.forEach { target ->
                                SlotMachine(
                                    targetSymbol = target,
                                    textStyle = MaterialTheme.typography.headlineMedium,
                                    startAnimation = startAnimation,
                                    onAnimationComplete = {
                                        completedAnimations++
                                        if (completedAnimations == 3) {
                                            isAnimating = false
                                            completedAnimations = 0
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                currentBet = 10
                                if (gameViewModel.placeBet(10)) {
                                    localFondocoins = vmFondocoins
                                    scoreMessage = ""
                                    isAnimating = true
                                    startAnimation = true
                                    targetSymbols = List(3) { symbols.random() }
                                }
                            },
                            enabled = localFondocoins >= 10 && !isAnimating,
                            modifier = Modifier.size(80.dp, 50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text("10")
                        }

                        Button(
                            onClick = {
                                currentBet = 20
                                if (gameViewModel.placeBet(20)) {
                                    localFondocoins = vmFondocoins
                                    scoreMessage = ""
                                    isAnimating = true
                                    startAnimation = true
                                    targetSymbols = List(3) { symbols.random() }
                                }
                            },
                            enabled = localFondocoins >= 20 && !isAnimating,
                            modifier = Modifier.size(80.dp, 50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text("20")
                        }

                        Button(
                            onClick = {
                                currentBet = 50
                                if (gameViewModel.placeBet(50)) {
                                    localFondocoins = vmFondocoins
                                    scoreMessage = ""
                                    isAnimating = true
                                    startAnimation = true
                                    targetSymbols = List(3) { symbols.random() }
                                }
                            },
                            enabled = localFondocoins >= 50 && !isAnimating,
                            modifier = Modifier.size(80.dp, 50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text("50")
                        }
                    }
                }
            }
        }
        if (isWin.value) {
            Box(modifier = Modifier
                .fillMaxSize()
                .zIndex(1f)
            ) {
                LottieAnimation(
                    composition = coinsRain,
                    modifier = Modifier.fillMaxSize().zIndex(1f),
                    iterations = LottieConstants.IterateForever,
                    speed = 0.5f
                )
            }
        }

        //PopUp
        var showRulesDialog by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(20.dp)
                .padding(top = 50.dp)
                .zIndex(1f)
        ) {
            Button(
                onClick = {showRulesDialog = true},
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White,
                ),
                        contentPadding = PaddingValues(0.dp)
            ) {
                Text("?", style = TextStyle(fontSize = 20.sp, color = Color.White))
            }
        }

        if (showRulesDialog) {
            AlertDialog(
                onDismissRequest = { showRulesDialog = false },
                containerColor = Color.Black,
                title = {
                    Text(
                        "Cómo jugar",
                        color = Color.Yellow
                    )
                },
                text = {
                    Column {
                        Text("1. Apuesta mínima: 10 fondocoins", color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("2. Premios:", color = Color.White)
                        Text("   - 3 símbolos iguales: x10", color = Color.White)
                        Text("   - 2 símbolos iguales: x2", color = Color.White)
                        Text("   - 0 símbolos iguales: sin premio", color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("3. Cada victoria otorga experiencia:", color = Color.White)
                        Text("   - 3 símbolos: 15 XP", color = Color.White)
                        Text("   - 2 símbolos: 5 XP", color = Color.White)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showRulesDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Yellow,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Ok")
                    }
                }
            )
        }

    }

    LaunchedEffect(isAnimating) {
        if (!isAnimating && startAnimation) {
            val winMultiplier = calculateWinMultiplier(targetSymbols)
            val winAmount = currentBet * winMultiplier
            val netWin = winAmount - currentBet

            localFondocoins = vmFondocoins
            localExperience = vmExperience

            roundsPlayed++
            fondocoinsSpent += currentBet
            fondocoinsEarned += winAmount
            experienceEarned += calculateSlotExperience(winMultiplier)

            scoreMessage = when (winMultiplier) {
                10 -> "¡Gran premio! Ganaste ${netWin * 10} fondocoins y 15 de experiencia!"
                2 -> "¡Ganaste ${netWin * 2} fondocoins y 5 de experiencia!"
                else -> "Sin premio"
            }

            if (winMultiplier > 0) {
                isWin.value = true
                delay(5000)
                isWin.value = false
            }
            startAnimation = false
            currentBet = 0
        }
    }
}

fun calculateSlotExperience(winMultiplier: Int): Int {
    val experienceToAdd = when (winMultiplier) {
        10 -> 15
        2 -> 5
        else -> 0
    }
    return experienceToAdd
}

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SlotMachine(
    targetSymbol: String,
    textStyle: TextStyle,
    startAnimation: Boolean,
    delay: Long = (Random.nextFloat() * 300).roundToLong(),
    onAnimationComplete: () -> Unit
) {
    var animationDuration by remember { mutableLongStateOf(100L) }
    var targetOffset by remember { mutableIntStateOf(Random.nextInt(15, 30)) }
    var currentSymbol by remember { mutableStateOf(getSourceSymbol(targetSymbol, targetOffset)) }

    LaunchedEffect(startAnimation) {
        if (startAnimation) {
            targetOffset = Random.nextInt(15, 30)
            animationDuration = 100L
            currentSymbol = getSourceSymbol(targetSymbol, targetOffset)
            delay(delay)
            while (targetOffset > 0) {
                targetOffset--
                animationDuration += ((10 - targetOffset) * 10L).coerceAtLeast(0L)
                currentSymbol = getNextSymbol(currentSymbol)
                delay(animationDuration)
            }
            onAnimationComplete()
        }
    }

    AnimatedContent(
        targetState = currentSymbol,
        transitionSpec = {
            slideInVertically(
                animationSpec = tween(animationDuration.toInt()),
                initialOffsetY = { -it },
            ).togetherWith(
                slideOutVertically(
                    animationSpec = tween(animationDuration.toInt()),
                    targetOffsetY = { it }
                )
            )
        }
    ) {
        Image(
            painter = painterResource(id = getSymbolImageResource(currentSymbol)),
            contentDescription = currentSymbol,
            modifier = Modifier.size(75.dp)
        )
    }
}

private fun getSourceSymbol(targetSymbol: String, targetOffset: Int): String {
    val symbols = listOf("bar", "diamond", "heart", "seven", "watermelon", "horseshoe")
    val index = (symbols.indexOf(targetSymbol) - targetOffset) % symbols.size
    return if (index < 0) symbols[symbols.size + index] else symbols[index]
}

private fun getNextSymbol(currentSymbol: String): String {
    val symbols = listOf("bar", "diamond", "heart", "seven", "watermelon", "horseshoe")
    val index = (symbols.indexOf(currentSymbol) + 1) % symbols.size
    return symbols[index]
}

private fun calculateWinMultiplier(symbols: List<String>): Int {
    val distinctSymbols = symbols.distinct()
    return when {
        distinctSymbols.size == 1 -> 10 // 3 matching symbols
        distinctSymbols.size == 2 -> 2  // 2 matching symbols
        else -> 0                       // No win
    }
}

private fun getSymbolImageResource(symbol: String): Int {
    return when (symbol) {
        "bar" -> R.drawable.bar
        "diamond" -> R.drawable.diamond
        "heart" -> R.drawable.heart
        "seven" -> R.drawable.seven
        "watermelon" -> R.drawable.watermelon
        "horseshoe" -> R.drawable.horseshoe
        else -> R.drawable.bar
    }
}

@Preview(showBackground = true)
@Composable
fun SlotMachineScreenPreview() {
    SlotMachineScreen(
        navController = rememberNavController(),
        remoteViewModel = RemoteViewModel(),
        gameViewModel = GameViewModel()
    )
}
