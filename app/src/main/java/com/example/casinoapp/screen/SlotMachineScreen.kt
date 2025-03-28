package com.example.casinoapp.screen

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.casinoapp.R
import com.example.casinoapp.viewModel.GameViewModel
import com.example.casinoapp.viewModel.RemoteViewModel
import kotlinx.coroutines.delay
import kotlin.math.roundToLong
import kotlin.random.Random

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
    var userId by remember { mutableStateOf("") }
    var scoreMessage by remember { mutableStateOf("") }
    var currentBet by remember { mutableIntStateOf(0) }

    // Lottie Composition for the coin rain animation
    val lottieComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.lluvia_monedas)
    )

    val isWin = remember { mutableStateOf(false) }

    LaunchedEffect(loggedInUser) {
        loggedInUser?.userId?.let {
            userId = it.toString()
            gameViewModel.setUserId(it.toInt())
            gameViewModel.getUserFondoCoins(it.toInt())
        }
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
                            gameViewModel.updateUserFondoCoins(id, vmFondocoins)
                            navController.popBackStack()
                        }

                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver atrás",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text("Fondo Coins: $vmFondocoins", style = MaterialTheme.typography.bodyLarge)
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
                                if (!isAnimating && gameViewModel.placeBet(10)) {
                                    scoreMessage = ""
                                    isAnimating = true
                                    startAnimation = true
                                    targetSymbols = List(3) { symbols.random() }
                                }
                            },
                            enabled = vmFondocoins >= 10 && !isAnimating,
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
                                if (!isAnimating && gameViewModel.placeBet(20)) {
                                    scoreMessage = ""
                                    isAnimating = true
                                    startAnimation = true
                                    targetSymbols = List(3) { symbols.random() }
                                }
                            },
                            enabled = vmFondocoins >= 20 && !isAnimating,
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
                                if (!isAnimating && gameViewModel.placeBet(50)) {
                                    scoreMessage = ""
                                    isAnimating = true
                                    startAnimation = true
                                    targetSymbols = List(3) { symbols.random() }
                                }
                            },
                            enabled = vmFondocoins >= 50 && !isAnimating,
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
                    composition = lottieComposition,
                    modifier = Modifier.fillMaxSize().zIndex(1f),
                    iterations = LottieConstants.IterateForever,
                    speed = 0.5f
                )
            }
        }
    }

    LaunchedEffect(isAnimating) {
        if (!isAnimating && startAnimation) {
            val winMultiplier = calculateWinMultiplier(targetSymbols)
            val winAmount = currentBet * winMultiplier
            val netWin = winAmount - currentBet

            gameViewModel.addWinnings(winAmount)

            scoreMessage = when (winMultiplier) {
                10 -> "¡Gran premio! Ganaste ${netWin * 10} fondocoins!"
                2 -> "¡Ganaste ${netWin * 2} fondocoins!"
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
