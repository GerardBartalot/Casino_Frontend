package com.example.casinoapp.screen.games

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.casinoapp.R
import com.example.casinoapp.entity.GameSession
import com.example.casinoapp.ui.components.AnimatedNumberDisplay
import com.example.casinoapp.ui.components.ExperienceProgressBar
import com.example.casinoapp.ui.components.GameRuleSection
import com.example.casinoapp.ui.components.GameRulesDialog
import com.example.casinoapp.ui.components.WinDisplay
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
    var currentBet by remember { mutableIntStateOf(0) }
    var localFondocoins by remember { mutableIntStateOf(0) }
    var localExperience by remember { mutableIntStateOf(0) }
    val isWin = remember { mutableStateOf(false) }
    var roundsPlayed by remember { mutableIntStateOf(0) }
    var fondocoinsSpent by remember { mutableIntStateOf(0) }
    var fondocoinsEarned by remember { mutableIntStateOf(0) }
    var experienceEarned by remember { mutableIntStateOf(0) }
    var gameBalance by remember { mutableIntStateOf(0) }
    var fondoCoinsEarnedDisplay by remember { mutableIntStateOf(0) }
    var experienceEarnedDisplay by remember { mutableIntStateOf(0) }
    var experienceEarnedInCurrentRound by remember { mutableIntStateOf(0) }
    var totalExperienceEarned by remember { mutableIntStateOf(0) }
    var buttonsLocked by remember { mutableStateOf(false) }
    var showRulesDialog by remember { mutableStateOf(false) }
    var showAddFundsMessage by remember { mutableStateOf(false) }
    val goldenGradient = listOf(Color(0xFFFFD700),Color(0xFFFFA500))
    var showNotEnoughFundsMessage by remember { mutableStateOf(false) }

    fun saveGameSession() {
        loggedInUser?.let { user ->
            val slotMachineGame = gameViewModel.games.value.find {
                it.gameName.equals("Escurabutxaques", ignoreCase = true)
            }

            slotMachineGame?.let { game ->
                val gameSession = GameSession(
                    user = user,
                    game = game,
                    rounds = roundsPlayed,
                    experienceEarned = totalExperienceEarned,
                    fondocoinsSpent = fondocoinsSpent,
                    fondocoinsEarned = fondocoinsEarned
                )
                remoteViewModel.saveGameSession(gameSession) { result ->
                    Log.d("Escurabutxaques", "Game session save result: $result")
                }
            } ?: run {
                Log.e("Escurabutxaques", "No se pudo encontrar el juego 'Escurabutxaques'")
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

    val casinoGreenGradient = listOf(
        Color(0xFF4CAF50),
        Color(0xFF2E7D32)
    )
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A1A1A),
            Color(0xFF2D2D2D),
            Color(0xFF1A1A1A)
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
                            text = "Escurabutxaques",
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
                                fondocoins = gameBalance,
                                experience = totalExperienceEarned,
                                modifier = Modifier
                                    .height(60.dp)
                                    .width(200.dp)
                                    .zIndex(1f)
                                    .offset(y = (-35).dp)
                                    .align(Alignment.TopCenter)
                                    .padding(bottom = 8.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .height(140.dp)
                                    .fillMaxWidth()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0xFF0D47A1),
                                                Color(0xFF1A3E72),
                                                Color(0xFF2A5C8C)
                                            ),
                                            startX = 0f,
                                            endX = Float.POSITIVE_INFINITY
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .border(
                                        width = 3.dp,
                                        color = Color.Yellow,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ){
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
                        }

                        if (showAddFundsMessage) {
                            Spacer(modifier = Modifier.height(25.dp))
                            LaunchedEffect(Unit) {
                                delay(3000)
                                showAddFundsMessage = false
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Afegeix fondocoins a la màquina escurabutxaques per jugar",
                                    color = Color.Red,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        if (showNotEnoughFundsMessage) {
                            Spacer(modifier = Modifier.height(25.dp))
                            LaunchedEffect(Unit) {
                                delay(3000)
                                showNotEnoughFundsMessage = false
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No tens fondocoins suficients en el teu balanç",
                                    color = Color.Red,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                    )
                            }
                        }

                        if (fondoCoinsEarnedDisplay > 0 || experienceEarnedDisplay > 0) {
                            Spacer(modifier = Modifier.height(25.dp))
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
                            BetButton(
                                value = 10,
                                enabled = localFondocoins >= 10,
                                isLocked = buttonsLocked,
                                onClick = {
                                    currentBet = 10
                                    fondoCoinsEarnedDisplay = 0
                                    experienceEarnedDisplay = 0
                                    experienceEarnedInCurrentRound = 0

                                    if (gameBalance >= currentBet) {
                                        gameBalance -= currentBet
                                        completedAnimations = 0
                                        isAnimating = true
                                        startAnimation = true
                                        targetSymbols = List(3) { symbols.random() }
                                    } else {
                                        showAddFundsMessage = true
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            BetButton(
                                value = 20,
                                enabled = localFondocoins >= 20,
                                isLocked = buttonsLocked,
                                onClick = {
                                    currentBet = 20
                                    fondoCoinsEarnedDisplay = 0
                                    experienceEarnedDisplay = 0
                                    experienceEarnedInCurrentRound = 0

                                    if (gameBalance >= currentBet) {
                                        gameBalance -= currentBet
                                        completedAnimations = 0
                                        isAnimating = true
                                        startAnimation = true
                                        targetSymbols = List(3) { symbols.random() }
                                    } else {
                                        showAddFundsMessage = true
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            BetButton(
                                value = 50,
                                enabled = localFondocoins >= 50,
                                isLocked = buttonsLocked,
                                onClick = {
                                    currentBet = 50
                                    fondoCoinsEarnedDisplay = 0
                                    experienceEarnedDisplay = 0
                                    experienceEarnedInCurrentRound = 0

                                    if (gameBalance >= currentBet) {
                                        gameBalance -= currentBet
                                        completedAnimations = 0
                                        isAnimating = true
                                        startAnimation = true
                                        targetSymbols = List(3) { symbols.random() }
                                    } else {
                                        showAddFundsMessage = true
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(30.dp))

                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(120.dp, 80.dp)
                                    .background(
                                        brush = if (!buttonsLocked) {
                                            Brush.verticalGradient(casinoGreenGradient)
                                        } else {
                                            Brush.verticalGradient(listOf(Color(0xFF616161), Color(0xFF424242)))
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        width = 2.dp,
                                        brush = if (!buttonsLocked) {
                                            Brush.verticalGradient(listOf(Color.Yellow, Color.White))
                                        } else {
                                            Brush.verticalGradient(listOf(Color(0xFF9E9E9E), Color(0xFF757575)))
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .graphicsLayer {
                                        alpha = if (!buttonsLocked) 1f else 0.5f
                                    }
                                    .clickable(
                                        enabled = !buttonsLocked,
                                        onClick = {
                                            if (localFondocoins >= 100) {
                                                localFondocoins -= 100
                                                gameBalance += 100
                                                userId.toIntOrNull()?.let { id ->
                                                    gameViewModel.updateUserFondoCoins(id, localFondocoins)
                                                }
                                            } else {
                                                showNotEnoughFundsMessage = true
                                            }
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "AFEGEIX 100",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(modifier = Modifier.width(20.dp))

                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(
                                        brush = if (!buttonsLocked) {
                                            Brush.verticalGradient(goldenGradient)
                                        } else {
                                            Brush.verticalGradient(listOf(Color(0xFF616161), Color(0xFF424242)))
                                        },
                                        shape = CircleShape
                                    )
                                    .border(
                                        width = 2.dp,
                                        brush = if (!buttonsLocked) {
                                            Brush.verticalGradient(listOf(Color.Yellow, Color.White))
                                        } else {
                                            Brush.verticalGradient(listOf(Color(0xFF9E9E9E), Color(0xFF757575)))
                                        },
                                        shape = CircleShape
                                    )
                                    .graphicsLayer {
                                        alpha = if (!buttonsLocked) 1f else 0.5f
                                    }
                                    .clickable(
                                        enabled = !buttonsLocked,
                                        onClick = {
                                            currentBet = gameBalance
                                            fondoCoinsEarnedDisplay = 0
                                            experienceEarnedDisplay = 0
                                            experienceEarnedInCurrentRound = 0

                                            if (gameBalance > 0) {
                                                gameBalance -= currentBet
                                                completedAnimations = 0
                                                isAnimating = true
                                                startAnimation = true
                                                targetSymbols = List(3) { symbols.random() }
                                            } else {
                                                showAddFundsMessage = true
                                            }
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "APOSTA MÀXIMA",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(modifier = Modifier.width(20.dp))

                            Box(
                                modifier = Modifier
                                    .size(120.dp, 80.dp)
                                    .background(
                                        brush = if (!buttonsLocked) {
                                            Brush.verticalGradient(casinoGreenGradient)
                                        } else {
                                            Brush.verticalGradient(
                                                listOf(
                                                    Color(0xFF616161),
                                                    Color(0xFF424242)
                                                )
                                            )
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        width = 2.dp,
                                        brush = if (!buttonsLocked) {
                                            Brush.verticalGradient(listOf(Color.Yellow, Color.White))
                                        } else {
                                            Brush.verticalGradient(
                                                listOf(
                                                    Color(0xFF9E9E9E),
                                                    Color(0xFF757575)
                                                )
                                            )
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .graphicsLayer {
                                        alpha = if (!buttonsLocked) 1f else 0.5f
                                    }
                                    .clickable(
                                        enabled = !buttonsLocked,
                                        onClick = {
                                            localFondocoins += gameBalance
                                            userId.toIntOrNull()?.let { id ->
                                                gameViewModel.updateUserFondoCoins(id, localFondocoins)
                                            }
                                            gameBalance = 0
                                            fondoCoinsEarnedDisplay = 0
                                            experienceEarnedDisplay = 0
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "RETIRAR",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }

                            if (showRulesDialog) {
                                GameRulesDialog(
                                    gameName = "ESCURABUTXAQUES",
                                    rules = listOf(
                                        GameRuleSection(
                                            title = "💎 APOSTES",
                                            titleColor = Color(0xFF1E88E5),
                                            items = listOf(
                                                "Tirades de 10, 20 o 50 fondocoins",
                                                "El premi es calcula sobre la base de la teva aposta",
                                                "Has d'afegir fondocoins a la màquina escurabutxaques per poder jugar",
                                                "L'opció APOSTA MÀXIMA et permet apostar tots els teus fondocoins"
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
                                                "3 símbols iguals: +100 XP",
                                                "2 símbols iguals: +50 XP",
                                                "Tots diferents: +0 XP"
                                            )
                                        ),
                                        GameRuleSection(
                                            title = "ℹ️ IMPORTANT",
                                            titleColor = Color(0xFFBA68C8),
                                            items = listOf(
                                                "Les ganències s'acumulen fins seleccionar RETIRAR",
                                                "L'experiència es suma automàticament",
                                            )
                                        ),
                                        GameRuleSection(
                                            title = "⚠️ ATENCIÓ",
                                            titleColor = Color(0xFFE57373),
                                            items = listOf(
                                                "Si abans de sortir no selecciones RETIRAR perdràs les teves ganències"
                                            )
                                        )
                                    ),
                                    showDialog = showRulesDialog,
                                    onDismiss = { showRulesDialog = false }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(70.dp))
                    }
                }
            }
        }
    }

    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            buttonsLocked = true
        } else if (startAnimation) {
            val winMultiplier = calculateWinMultiplier(targetSymbols)
            val winAmount = currentBet * winMultiplier

            roundsPlayed++
            fondocoinsSpent += currentBet
            fondocoinsEarned += winAmount
            gameBalance += winAmount
            fondoCoinsEarnedDisplay += winAmount

            val currentRoundXp = calculateSlotExperience(winMultiplier)
            experienceEarnedInCurrentRound = currentRoundXp
            experienceEarnedDisplay = currentRoundXp

            if (currentRoundXp > 0) {
                totalExperienceEarned += currentRoundXp
                localExperience += totalExperienceEarned
                userId.toIntOrNull()?.let { id ->
                    gameViewModel.updateUserExperience(id, localExperience)
                }
            }

            if (winMultiplier > 0) {
                isWin.value = true
                delay(5000)
                isWin.value = false
            }

            buttonsLocked = false
            startAnimation = false
            currentBet = 0
            isAnimating = false
            completedAnimations = 0
        }
    }
}

fun calculateSlotExperience(winMultiplier: Int): Int {
    val experienceToAdd = when (winMultiplier) {
        10 -> 100
        2 -> 50
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
fun BetButton(
    value: Int,
    enabled: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonColors = if (enabled && !isLocked) {
        Brush.verticalGradient(listOf(Color(0xFF1E88E5), Color(0xFF0D47A1)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFF616161), Color(0xFF424242)))
    }

    val borderColors = if (enabled && !isLocked) {
        Brush.verticalGradient(listOf(Color.Yellow, Color.White))
    } else {
        Brush.verticalGradient(listOf(Color(0xFF9E9E9E), Color(0xFF757575)))
    }

    Box(
        modifier = modifier
            .size(90.dp, 90.dp)
            .background(buttonColors, RoundedCornerShape(8.dp))
            .graphicsLayer {
                alpha = if (enabled && !isLocked) 1f else 0.5f
            }
            .border(
                width = 2.dp,
                brush = borderColors,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = enabled && !isLocked, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = value.toString(),
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
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