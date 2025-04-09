package com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun CreateLigaDialog(
    onDismiss: () -> Unit,
    onCreateLiga: (String) -> Unit
) {
    var leagueName by remember { mutableStateOf("") }
    // Solo permite enviar si se ha escrito algo
    val isSubmitEnabled = leagueName.isNotBlank()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column {
                // Header con gradiente azul (usando el primary y secondary de la app)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Crear Liga",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                // Contenido
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = leagueName,
                        onValueChange = { leagueName = it },
                        label = { Text("Nombre de la liga") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Botones: Cancelar y Crear
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancelar", color = MaterialTheme.colorScheme.error)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                if (isSubmitEnabled) onCreateLiga(leagueName)
                            }
                        ) {
                            Text(
                                text = "Crear",
                                color = if (isSubmitEnabled)
                                    MaterialTheme.colorScheme.primary
                                else
                                    Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun JoinLigaDialog(
    onDismiss: () -> Unit,
    onJoinLiga: (String) -> Unit
) {
    var ligaCode by remember { mutableStateOf("") }
    val isSubmitEnabled = ligaCode.isNotBlank()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column {
                // Header con gradiente
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Unirse a Liga",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                // Contenido
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = ligaCode,
                        onValueChange = { ligaCode = it },
                        label = { Text("Código de la liga") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancelar", color = MaterialTheme.colorScheme.error)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = { if (isSubmitEnabled) onJoinLiga(ligaCode) }
                        ) {
                            Text(
                                text = "Unirse",
                                color = if (isSubmitEnabled)
                                    MaterialTheme.colorScheme.primary
                                else
                                    Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ForgotPasswordDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    // Habilitado solo si se ha escrito algo
    val isSubmitEnabled = email.isNotBlank()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column {
                // Header con gradiente azul
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Restablecer contraseña",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                // Contenido
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Correo"
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ingrese su correo y recibirá un enlace para cambiar la contraseña.",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancelar", color = MaterialTheme.colorScheme.error)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = { if (isSubmitEnabled) onSubmit(email) }
                        ) {
                            Text(
                                text = "Enviar",
                                color = if (isSubmitEnabled)
                                    MaterialTheme.colorScheme.primary
                                else
                                    Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LeagueCodeDialog(
    leagueCode: String,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column {
                // Header con gradiente azul
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Código de la Liga",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
                    )
                }
                // Contenido del diálogo
                Column(modifier = Modifier.padding(16.dp)) {
                    // Texto del código de la liga con estilo mejorado
                    Text(
                        text = leagueCode,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { clipboardManager.setText(AnnotatedString(leagueCode)) }
                        ) {
                            Text(
                                text = "Copiar",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = onDismiss) {
                            Text(
                                text = "Cerrar",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}