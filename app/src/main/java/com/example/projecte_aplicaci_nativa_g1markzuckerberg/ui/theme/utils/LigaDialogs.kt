// LigaDialogs.kt
package com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
        title = { Text(text = "Crear Liga") },
        text = {
            OutlinedTextField(
                value = leagueName,
                onValueChange = { leagueName = it },
                label = { Text("Nombre de la liga") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onCreateLiga(leagueName) }
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        }
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
        title = { Text("Unirse a Liga") },
        text = {
            Column {
                OutlinedTextField(
                    value = ligaCode,
                    onValueChange = { ligaCode = it },
                    label = { Text("Código de la liga") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onJoinLiga(ligaCode) }) {
                Text("Unirse")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
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

