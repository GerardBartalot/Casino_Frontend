package com.example.casinoapp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExperienceProgressBar(
    currentXp: Int,
    modifier: Modifier = Modifier
) {
    val level = currentXp / 1000 + 1
    val progress by animateFloatAsState(
        targetValue = (currentXp % 1000) / 1000f,
        animationSpec = tween(durationMillis = 1000)
    )
    val barGradient = listOf(
            Color(0xFFFFD700),
            Color(0xFFFFA500),
            Color(0xFFFF8C00)
    )

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(start = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.DarkGray.copy(alpha = 0.5f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(18.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.DarkGray.copy(alpha = 0.5f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(barGradient)
                            )
                    )

                    Text(
                        text = "Nivell $level",
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Level",
                tint = Color.Yellow,
                modifier = Modifier
                    .size(40.dp)
                    .offset(x = (-16).dp)
                    .align(Alignment.CenterStart)
            )
        }
    }
}