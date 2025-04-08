// LigaDialogs.kt
package com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CreateLigaDialog(
    onDismiss: () -> Unit,
    onCreateLiga: (String) -> Unit
) {
    var leagueName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Crear Liga", fontSize = 18.sp) },
        text = {
            OutlinedTextField(
                value = leagueName,
                onValueChange = { leagueName = it },
                label = { Text("Nombre de la liga") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            FilledTonalButton(
                onClick = { onCreateLiga(leagueName) },
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondary,
                    contentColor = Color.White
                ),
                modifier = Modifier.height(40.dp)
            ) {
                Text("Crear", fontSize = 14.sp)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier.height(40.dp)
            ) {
                Text("Cancelar", fontSize = 14.sp)
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun JoinLigaDialog(
    onDismiss: () -> Unit,
    onJoinLiga: (String) -> Unit
) {
    var ligaCode by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Unirse a Liga", fontSize = 18.sp) },
        text = {
            OutlinedTextField(
                value = ligaCode,
                onValueChange = { ligaCode = it },
                label = { Text("Código de la liga") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            FilledTonalButton(
                onClick = { onJoinLiga(ligaCode) },
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                modifier = Modifier.height(40.dp)
            ) {
                Text("Unirse", fontSize = 14.sp)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier.height(40.dp)
            ) {
                Text("Cancelar", fontSize = 14.sp)
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
@Composable
fun ForgotPasswordDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Restablecer contraseña") },
        text = {
            Column {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = "Correo")
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ingrese su correo y recibirá un enlace para cambiar la contraseña.",
                    fontSize = 14.sp
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSubmit(email) }) {
                Text("Enviar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        shape = RoundedCornerShape(16.dp),
    )
}

