package com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

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
                        onValueChange = {
                            val filtered = it.replace(" ", "")
                            ligaCode = filtered.take(30)
                        },
                        label = { Text("Código de la liga") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        maxLines = 1
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
    // Se habilita solo si se ha escrito algo
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
                        onValueChange = {
                            // Para el correo se eliminan espacios y se limita a 30 caracteres
                            val filtered = it.replace(" ", "")
                            email = filtered.take(30)
                        },
                        label = { Text("Correo") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Correo"
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        maxLines = 1
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
@Composable
fun CustomAlertDialog(
    title: String,
    message: String,
    confirmButtonText: String = "Aceptar",
    cancelButtonText: String = "Cancelar",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column {
                /* ---------- ENCABEZADO ---------- */
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text  = title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                /* ---------- CONTENIDO ---------- */
                Column(modifier = Modifier.padding(16.dp)) {

                    /* ← aquí convertimos **texto** en negrita */
                    Text(
                        text = message.toAnnotatedStringWithBold(),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(cancelButtonText, color = MaterialTheme.colorScheme.error)
                        }
                        Spacer(Modifier.width(8.dp))
                        TextButton(onClick = onConfirm) {
                            Text(confirmButtonText, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

/* ---------- helper ext. function ---------- */
private fun String.toAnnotatedStringWithBold(): AnnotatedString {
    val boldRegex = Regex("\\*\\*(.*?)\\*\\*")
    return buildAnnotatedString {
        var currentIndex = 0
        boldRegex.findAll(this@toAnnotatedStringWithBold).forEach { match ->
            // texto normal hasta la coincidencia
            append(this@toAnnotatedStringWithBold.substring(currentIndex, match.range.first))
            // texto en negrita
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(match.groupValues[1])
            }
            currentIndex = match.range.last + 1
        }
        // cola de texto normal
        if (currentIndex < this@toAnnotatedStringWithBold.length) {
            append(this@toAnnotatedStringWithBold.substring(currentIndex))
        }
    }
}


@Composable
fun CustomAlertDialogSingleButton(
    title: String,
    message: String,
    confirmButtonText: String = "Aceptar",
    onAccept: () -> Unit,
) {
    Dialog(onDismissRequest = onAccept) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column {
                // Encabezado con gradiente, estilo similar a los otros diálogos
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
                        text = title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                // Contenido del diálogo
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Se muestra sólo el botón Aceptar, centrado
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(onClick = onAccept) {
                            Text(
                                text = confirmButtonText,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LigaDialog(
    title: String,
    initialName: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String, Uri?) -> Unit
) {
    var leagueName by remember { mutableStateOf(initialName) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para elegir imagen
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column {
                // Encabezado con gradiente y título dinámico
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
                        text = title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    // Campo de nombre
                    OutlinedTextField(
                        value = leagueName,
                        onValueChange = { leagueName = it },
                        label = { Text("Nombre de la liga") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón para seleccionar/cambiar imagen
                    Button(
                        onClick = { launcher.launch("image/*") },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (selectedImageUri == null)
                                "Seleccionar imagen"
                            else
                                "Cambiar imagen"
                        )
                    }

                    // Indicador de imagen elegida
                    selectedImageUri?.let {
                        Text(
                            text = "Imagen seleccionada ✅",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botones de acción
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancelar", color = MaterialTheme.colorScheme.error)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = { onConfirm(leagueName.trim(), selectedImageUri) },
                            enabled = leagueName.isNotBlank()
                        ) {
                            Text(
                                text = if (initialName.isEmpty()) "Crear" else "Guardar",
                                color = if (leagueName.isNotBlank())
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
fun ContactFormDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var message by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape     = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier  = Modifier
                .fillMaxWidth(0.9f)    // solo anchura
            // .wrapContentHeight() // opcional, por defecto el Card se adapta al contenido
        ) {
            Column {
                // ─── HEADER ───
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text  = "Contacto",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                // ─── DESCRIPCIÓN ───
                Text(
                    text = "¿Tienes dudas o sugerencias? Escríbenos y te responderemos lo antes posible.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    textAlign = TextAlign.Center
                )

                // ─── CAMPO DE TEXTO ───
                OutlinedTextField(
                    value         = message,
                    onValueChange = { message = it },
                    label         = { Text("Tu mensaje") },
                    placeholder   = { Text("Escribe tu consulta aquí…") },
                    modifier      = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp)
                        .padding(horizontal = 16.dp),
                    shape      = RoundedCornerShape(12.dp),
                    singleLine = false,
                    maxLines   = 6
                )

                // ─── BOTONES ───
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = MaterialTheme.colorScheme.error)
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(
                        onClick = {
                            onSubmit(message.trim())
                            onDismiss()
                        },
                        enabled = message.isNotBlank()
                    ) {
                        Text(
                            text  = "Enviar",
                            color = if (message.isNotBlank())
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