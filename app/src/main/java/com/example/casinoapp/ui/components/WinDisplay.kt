package com.example.casinoapp.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
            horizontalArrangement = Arrangement.Center
        ) {
            // Fondocoins
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
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
                Image(
                    painter = painterResource(id = R.drawable.fondocoin),
                    contentDescription = "Fondocoin",
                    modifier = Modifier.size(50.dp)
                )
            }

            // Separador
            Text(
                text = "|",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 24.sp,
                modifier = Modifier.padding(end = 4.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
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
                Spacer(modifier = Modifier.width(5.dp))
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Level",
                    tint = Color.Yellow,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    }

}