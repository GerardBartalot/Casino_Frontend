package com.example.casinoapp.ui.components

import android.R
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import java.io.ByteArrayOutputStream
import java.io.IOException

@Composable
fun ImagePickerDialog(
    hasCurrentImage: Boolean,
    onDismiss: () -> Unit,
    onSelectFromGallery: () -> Unit,
    onDeleteCurrent: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.Black,
        title = {
            Text(
                text = "Foto de perfil",
                color = Color.White)
                },
        text = {
            Column {
                Button(
                    onClick = onSelectFromGallery,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFD700),
                        contentColor = Color.Black
                    )
                ) {
                    Text("Abrir galería")
                }

                if (hasCurrentImage) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onDeleteCurrent,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color(0xFFFF5252),
                            containerColor = Color(0xFFFFD700)
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription = "Eliminar foto",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Eliminar foto actual")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFFFFD700)
                )
            ) {
                Text("Aplicar")
            }
        }
    )
}