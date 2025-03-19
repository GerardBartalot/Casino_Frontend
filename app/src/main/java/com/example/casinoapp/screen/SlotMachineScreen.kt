package com.example.casinoapp.screen

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlin.math.roundToLong
import kotlin.random.Random
import com.example.casinoapp.R

@Composable
fun SlotMachineScreen(
    navController: NavController
) {
    val symbols = listOf("bar", "diamond", "heart", "seven", "watermelon", "horseshoe")
    var targetSymbols by remember { mutableStateOf(List(3) { symbols.random() }) }
    var isAnimating by remember { mutableStateOf(false) }
    var startAnimation by remember { mutableStateOf(false) }
    var completedAnimations by remember { mutableStateOf(0) }

    // Puntuación inicial de 100 para pruebas
    var score by remember { mutableIntStateOf(10) }

    Box(modifier = Modifier.fillMaxSize()) {

        // Flecha para regresar al Home
        IconButton(
            onClick = { navController.navigate("homeScreen") }, modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(40.dp),
                tint = Color.Black
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Puntuación: $score",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
                modifier = Modifier.padding(16.dp)
            )

            // Frame image
            Box(
                modifier = Modifier
                    .size(320.dp, 160.dp)
            ) {
                // Fondo del marco
                Image(
                    painter = painterResource(id = R.drawable.frame),
                    contentDescription = "Frame",
                    modifier = Modifier.fillMaxSize()
                )

                // Slots dentro del marco
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
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

            // Botón para girar
            Button(
                onClick = {
                    // Solo permitir jugar si el usuario tiene 10 fondopoints
                    if (!isAnimating && score >= 10) {
                        isAnimating = true
                        startAnimation = true
                        // Restar 10 fondopoints por tirada
                        score -= 10
                        targetSymbols = List(3) { symbols.random() }
                    }
                },
                // Deshabilitar el botón si el usuario no tiene fondopoints
                enabled = score >= 10
            ) {
                Text("JUEGA (Cuesta 10 fondopoints)")
            }
        }
    }

    LaunchedEffect(isAnimating) {
        if (!isAnimating && startAnimation) {
            score += calculateScore(targetSymbols)
            startAnimation = false
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
            modifier = Modifier.size(64.dp)
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

private fun calculateScore(symbols: List<String>): Int {
    val distinctSymbols = symbols.distinct()
    return when {
        distinctSymbols.size == 1 -> 100
        distinctSymbols.size == 2 -> 50
        else -> 0
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
        else -> R.drawable.bar // Default case
    }
}

@Preview
@Composable
fun SlotMachinePreview() {
    SlotMachineScreen(
        navController = TODO()
    )
}