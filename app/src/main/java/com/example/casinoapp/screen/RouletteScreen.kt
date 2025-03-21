package com.example.casinoapp.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fondo.casinoapp.R
import kotlinx.coroutines.launch

@Composable
fun RouletteScreen(onBackPressed: () -> Unit) {
    var isSpinning by remember { mutableStateOf(false) }
    var resultNumber by remember { mutableStateOf<Int?>(null) }
    val fondoCoins by remember { mutableStateOf(0) }
    val rotationAngle = remember { Animatable(0f) }

    var selectedNumbers by remember { mutableStateOf<List<Int>>(emptyList()) }
    var selectedColor by remember { mutableStateOf<String?>(null) }
    var selectedEven by remember { mutableStateOf<Boolean?>(null) }
    var lastBetType by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    fun spinRoulette() {
        if (!isSpinning) {
            isSpinning = true
            resultNumber = null

            coroutineScope.launch {
                val targetRotation = (360 * 10) + (0..36).random() * (360f / 37)
                rotationAngle.animateTo(
                    targetValue = targetRotation,
                    animationSpec = tween(durationMillis = 4000, easing = LinearOutSlowInEasing)
                )

                val finalPosition = (rotationAngle.value % 360) / (360f / 37)
                val winningNumber = (finalPosition.toInt() + 1) % 37
                resultNumber = winningNumber
                isSpinning = false
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBar(onBackPressed, fondoCoins)

        Spacer(modifier = Modifier.height(30.dp))

        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(250.dp)) {
            Image(
                painter = painterResource(id = R.drawable.roulette),
                contentDescription = "Ruleta",
                modifier = Modifier
                    .size(250.dp)
                    .graphicsLayer(rotationZ = rotationAngle.value % 360)
            )
        }

        Spacer(modifier = Modifier.height(if (resultNumber == null) 40.dp else 20.dp))

        Button(onClick = {
            lastBetType = when {
                selectedNumbers.isNotEmpty() -> "Número"
                selectedColor != null -> "Color"
                selectedEven != null -> "ParImpar"
                else -> null
            }
            spinRoulette()
        }) {
            Text("GIRA!")
        }

        Spacer(modifier = Modifier.height(20.dp))

        resultNumber?.let { number ->
            val redNumbers = listOf(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36)
            val blackNumbers = listOf(2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35)
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
                "Número" -> if (isWin) "¡Ganaste! Número ganador: $number" else "¡Perdiste! Número ganador: $number"
                "Color" -> if (isWin) "¡Ganaste! Color ganador: $colorWinner" else "¡Perdiste! Color ganador: $colorWinner"
                "ParImpar" -> if (isWin) "¡Ganaste! Evento ganador: $evenOddWinner" else "¡Perdiste! Evento ganador: $evenOddWinner"
                else -> "¡Perdiste! Número ganador: $number"
            }

            Text(
                text = resultText,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(if (resultNumber == null) 20.dp else 30.dp))

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
            Text(text = "Color Rojo", color = Color.White)
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
            Text(text = "Color Negro", color = Color.White)
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
            Text(text = "Número Par", color = Color.White)
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
            Text(text = "Número Impar", color = Color.White)
        }
    }
}

@Composable
fun TopBar(onBackPressed: () -> Unit, fondoCoins: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = onBackPressed) { Text("Volver") }
        Text("Fondo Coins: $fondoCoins", style = MaterialTheme.typography.bodyLarge)
    }
}

enum class BetType { NUMBER, COLOR, EVEN_ODD }

@Preview(showBackground = true)
@Composable
fun RouletteScreenPreview() {
    RouletteScreen(onBackPressed = {})
}