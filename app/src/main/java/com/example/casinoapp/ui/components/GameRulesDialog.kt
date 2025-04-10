package com.example.casinoapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameRulesDialog(
    gameName: String,
    rules: List<GameRuleSection>,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = Color.Black,
            title = {
                Text(
                    gameName,
                    color = Color.Yellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(modifier = modifier) {
                    rules.forEach { section ->
                        Text(
                            section.title,
                            color = section.titleColor,
                            fontWeight = FontWeight.Bold
                        )
                        section.items.forEach { item ->
                            Text("   - $item", color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Yellow,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.width(120.dp)
                ) {
                    Text(
                        "D'ACORD",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        )
    }
}

data class GameRuleSection(
    val title: String,
    val titleColor: Color,
    val items: List<String>
)