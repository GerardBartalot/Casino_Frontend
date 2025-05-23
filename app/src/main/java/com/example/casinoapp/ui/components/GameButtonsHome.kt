package com.example.casinoapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.casinoapp.R

@Composable
fun GameButtonsHome(
    imageRes: Int,
    enabled: Boolean,
    onClick: () -> Unit,
    requiredLevel: Int = 0,
    isBeta: Boolean = false,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(105.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled) { onClick() }
            .border(
                width = 2.dp,
                color = if (isBeta) Color(0xFFFFA500) else Color.White,
                shape = RoundedCornerShape(10.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (enabled && !isBeta) 1f else 0.4f),
            contentScale = ContentScale.Crop
        )

        if (!enabled || isBeta) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (isBeta) Color(0x80FFA500).copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.4f)
                    )
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                if (!enabled) {
                    Image(
                        painter = painterResource(id = R.drawable.lock),
                        contentDescription = "Locked",
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Nivell $requiredLevel requerit",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
                if (isBeta) {
                    Text(
                        text = "Properament...",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}