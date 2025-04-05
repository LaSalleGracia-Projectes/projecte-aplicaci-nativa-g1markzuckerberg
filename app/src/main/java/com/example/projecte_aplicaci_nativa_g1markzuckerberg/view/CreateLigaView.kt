// CreateLigaView.kt
package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.CreateLigaViewModel
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
