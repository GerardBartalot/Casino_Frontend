package com.example.casinoapp.screen.games

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
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
import kotlin.math.roundToLong
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
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
    var roundsPlayed by remember { mutableIntStateOf(0) }
    var fondocoinsSpent by remember { mutableIntStateOf(0) }
    var fondocoinsEarned by remember { mutableIntStateOf(0) }
    var experienceEarned by remember { mutableIntStateOf(0) }
    var showFondocoinsWon by remember { mutableIntStateOf(0) }
    var fondoCoinsEarnedDisplay by remember { mutableIntStateOf(0) }
    var experienceEarnedDisplay by remember { mutableIntStateOf(0) }

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

    val casinoBlueGradient = listOf(
        Color(0xFF1E88E5),
        Color(0xFF0D47A1)
    )

    val casinoGreenGradient = listOf(
        Color(0xFF4CAF50),
        Color(0xFF2E7D32)
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
                            text = "Tragaperras",
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
                    containerColor = Color(0xFF0D0D0D),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1A1A))
        ) {
            Image(
                painter = painterResource(id = R.drawable.blur_background),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.4f),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top,
                    ) {

                        Spacer(modifier = Modifier.height(20.dp))

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

                        Spacer(modifier = Modifier.height(50.dp))

                        // Frame image
                        Box(
                            modifier = Modifier
                                .size(340.dp, 180.dp)
                        ) {
                            WinDisplay(
                                fondocoins = showFondocoinsWon,
                                experience = experienceEarned,
                                modifier = Modifier
                                    .height(60.dp)
                                    .width(200.dp)
                                    .zIndex(1f)
                                    .offset(y = (-20).dp)
                                    .align(Alignment.TopCenter)
                                    .padding(bottom = 8.dp)
                            )

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

                        Spacer(modifier = Modifier.height(25.dp))

                        if (fondoCoinsEarnedDisplay > 0 || experienceEarnedDisplay > 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                AnimatedNumberDisplay(
                                    fondocoins = fondoCoinsEarnedDisplay,
                                    experience = experienceEarnedDisplay,
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp, 60.dp)
                                    .background(
                                        brush = Brush.verticalGradient(casinoBlueGradient),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        width = 2.dp,
                                        brush = Brush.verticalGradient(listOf(Color.Yellow, Color.White)),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable(
                                        enabled = localFondocoins >= 10 && !isAnimating,
                                        onClick = {
                                            currentBet = 10
                                            fondoCoinsEarnedDisplay = 0
                                            experienceEarnedDisplay = 0
                                            if (gameViewModel.placeBet(10)) {
                                                localFondocoins -= 10
                                                scoreMessage = ""
                                                isAnimating = true
                                                startAnimation = true
                                                targetSymbols = List(3) { symbols.random() }
                                            }
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "10",
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Box(
                                modifier = Modifier
                                    .size(80.dp, 60.dp)
                                    .background(
                                        brush = Brush.verticalGradient(casinoBlueGradient),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        width = 2.dp,
                                        brush = Brush.verticalGradient(listOf(Color.Yellow, Color.White)),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable(
                                        enabled = localFondocoins >= 20 && !isAnimating,
                                        onClick = {
                                            currentBet = 20
                                            fondoCoinsEarnedDisplay = 0
                                            experienceEarnedDisplay = 0
                                            if (gameViewModel.placeBet(20)) {
                                                localFondocoins -= 20
                                                scoreMessage = ""
                                                isAnimating = true
                                                startAnimation = true
                                                targetSymbols = List(3) { symbols.random() }
                                            }
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "20",
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Box(
                                modifier = Modifier
                                    .size(80.dp, 60.dp)
                                    .background(
                                        brush = Brush.verticalGradient(casinoBlueGradient),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        width = 2.dp,
                                        brush = Brush.verticalGradient(listOf(Color.Yellow, Color.White)),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable(
                                        enabled = localFondocoins >= 50 && !isAnimating,
                                        onClick = {
                                            currentBet = 50
                                            fondoCoinsEarnedDisplay = 0
                                            experienceEarnedDisplay = 0
                                            if (gameViewModel.placeBet(50)) {
                                                localFondocoins -= 50
                                                scoreMessage = ""
                                                isAnimating = true
                                                startAnimation = true
                                                targetSymbols = List(3) { symbols.random() }
                                            }
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "50",
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(30.dp))

                        Box(
                            modifier = Modifier
                                .size(130.dp, 60.dp)
                                .background(
                                    brush = Brush.verticalGradient(casinoGreenGradient),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = 2.dp,
                                    brush = Brush.verticalGradient(listOf(Color.Yellow, Color.White)),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable(
                                    enabled = !isAnimating,
                                    onClick = {
                                        saveGameSession()
                                        localFondocoins += showFondocoinsWon
                                        userId.toIntOrNull()?.let { id ->
                                            gameViewModel.updateUserFondoCoins(id, localFondocoins)
                                        }
                                        showFondocoinsWon = 0
                                        fondoCoinsEarnedDisplay = 0
                                        experienceEarnedDisplay = 0
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "CASH OUT",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }

                        Spacer(modifier = Modifier.height(70.dp))
                    }
                }
            }
        }
    }

    LaunchedEffect(isAnimating) {
        if (!isAnimating && startAnimation) {
            val winMultiplier = calculateWinMultiplier(targetSymbols)
            val winAmount = currentBet * winMultiplier

            roundsPlayed++
            fondocoinsSpent += currentBet
            fondocoinsEarned += winAmount
            showFondocoinsWon += winAmount
            fondoCoinsEarnedDisplay += winAmount

            experienceEarned += calculateSlotExperience(winMultiplier)
            localExperience += experienceEarned
            experienceEarnedDisplay += experienceEarned

            if (winMultiplier > 0) {
                isWin.value = true
                delay(5000)
                isWin.value = false
            }
            startAnimation = false
            currentBet = 0
            isAnimating = false
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PixelDisplay(
    value: Int,
    modifier: Modifier = Modifier
) {
    val digits = value.toString().padStart(5, '0').toCharArray()
    val pixelFont = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        letterSpacing = 1.sp
    )

    Box(
        modifier = modifier
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.fondocoin),
                contentDescription = "Fondocoin",
                modifier = Modifier.size(50.dp)
            )
            digits.forEach { digit ->
                AnimatedContent(
                    targetState = digit,
                    transitionSpec = {
                        slideInVertically { height -> height } + fadeIn() togetherWith
                                slideOutVertically { height -> -height } + fadeOut()
                    },
                    label = "digitAnimation"
                ) { targetDigit ->
                    Text(
                        text = targetDigit.toString(),
                        style = pixelFont,
                        color = Color(0xFF00FF00),
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedNumberDisplay(
    fondocoins: Int,
    experience: Int,
    modifier: Modifier = Modifier
) {
    val animatedFondocoins by animateIntAsState(
        targetValue = fondocoins,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "fondocoinsAnimation"
    )

    val animatedExperience by animateIntAsState(
        targetValue = experience,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "experienceAnimation"
    )

    val sparkleColors = listOf(
        Color.Yellow,
        Color(0xFFFFA500),
        Color(0xFFFFD700),
        Color(0xFFFFFF00),
        Color.Yellow
    )

    val infiniteTransition = rememberInfiniteTransition()
    val sparkleProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable<Float>(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkleEffect"
    )

    val sparkleBrush = Brush.linearGradient(
        colors = sparkleColors,
        start = Offset(0f, 0f),
        end = Offset(sparkleProgress * 1000, sparkleProgress * 1000)
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        // Fondocoins
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = animatedFondocoins.toString(),
                style = TextStyle(
                    fontSize = 60.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    brush = sparkleBrush, // Aplicamos el efecto de brillo
                    shadow = Shadow(
                        color = Color.Yellow,
                        offset = Offset(2f, 2f),
                        blurRadius = 8f
                    )
                )
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "FC",
                style = TextStyle(
                    fontSize = 40.sp,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.White,
                ),
                modifier = Modifier.offset(y = 2.dp)
            )
        }
        Spacer(modifier = Modifier.width(15.dp))
        // Experiencia
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = animatedExperience.toString(),
                style = TextStyle(
                    fontSize = 60.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    brush = sparkleBrush,
                    shadow = Shadow(
                        color = Color(0xFF00FF00),
                        offset = Offset(2f, 2f),
                        blurRadius = 8f
                    )
                )
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "XP",
                style = TextStyle(
                    fontSize = 40.sp,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.White,
                ),
                modifier = Modifier.offset(y = 2.dp)
            )
        }
    }
}

@Composable
fun WinDisplay(
    fondocoins: Int,
    experience: Int,
    modifier: Modifier = Modifier
) {
    val animatedFondocoins by animateIntAsState(
        targetValue = fondocoins,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "fondocoinsAnimation"
    )

    val animatedExperience by animateIntAsState(
        targetValue = experience,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "experienceAnimation"
    )

    Box(
        modifier = modifier
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF4A148C), Color(0xFF1A237E)),
                    startX = 0f,
                    endX = Float.POSITIVE_INFINITY
                ),
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                width = 2.dp,
                color = Color.White,
                shape = RoundedCornerShape(10.dp)
            )
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(10.dp),
                clip = false,
                ambientColor = Color.Yellow,
                spotColor = Color.Yellow
            )
            .background(Color(0xFF222222), RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Fondocoins
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = animatedFondocoins.toString(),
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        color = Color.White,
                        shadow = Shadow(
                            color = Color.Yellow,
                            offset = Offset(2f, 2f),
                            blurRadius = 8f
                        )
                    )
                )
                Text(
                    text = "FC",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        color = Color(0xFFFFD700), // Gold
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(1f, 1f),
                            blurRadius = 2f
                        )
                    ),
                    modifier = Modifier.offset(y = 2.dp)
                )
            }

            // Separador
            Text(
                text = "|",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 24.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            // Experiencia
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = animatedExperience.toString(),
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        color = Color.White,
                        shadow = Shadow(
                            color = Color(0xFF00FF00),
                            offset = Offset(2f, 2f),
                            blurRadius = 8f
                        )
                    )
                )
                Text(
                    text = "XP",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        color = Color(0xFF00FF00), // Green
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(1f, 1f),
                            blurRadius = 2f
                        )
                    ),
                    modifier = Modifier.offset(y = 2.dp)
                )
            }
        }
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