package com.example.casinoapp.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.casinoapp.R

@Composable
fun AnimatedNumberDisplayRoulette(
    fondocoins: Int,
    experience: Int,
    modifier: Modifier = Modifier,
    title: String = "¡HAS GUANYAT!",
    showExperience: Boolean = true,
    titleColor: Color = Color.White,
    titleShadowColor: Color = Color.Yellow,
    resultNumber: Int? = null,
    colorResult: String? = null,
    evenOddResult: String? = null
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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor,
                shadow = Shadow(
                    color = titleShadowColor,
                    offset = Offset(2f, 2f),
                    blurRadius = 8f
                )
            )
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Fondocoins
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = animatedFondocoins.toString(),
                    style = TextStyle(
                        fontSize = 60.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        color = Color.Yellow, // Fallback to solid color
                        shadow = Shadow(
                            color = Color.Yellow,
                            offset = Offset(2f, 2f),
                            blurRadius = 8f
                        )
                    )
                )
                Spacer(modifier = Modifier.width(5.dp))
                Image(
                    painter = painterResource(id = R.drawable.fondocoin),
                    contentDescription = "Fondocoin",
                    modifier = Modifier.size(80.dp)
                )
            }

            if (showExperience) {
                Spacer(modifier = Modifier.width(15.dp))
                // Experiencia
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = animatedExperience.toString(),
                        style = TextStyle(
                            fontSize = 60.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            color = Color.Yellow, // Fallback to solid color
                            shadow = Shadow(
                                color = Color(0xFF00FF00),
                                offset = Offset(2f, 2f),
                                blurRadius = 8f
                            )
                        )
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Level",
                        tint = Color.Yellow,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }
        }

        // Result details text
        if (resultNumber != null && colorResult != null && evenOddResult != null) {
            Text(
                text = "Número: $resultNumber ($colorResult, $evenOddResult)",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.White,
                    shadow = Shadow(
                        color = titleShadowColor,
                        offset = Offset(2f, 2f),
                        blurRadius = 8f
                    )
                ),
            )
        }
    }
}