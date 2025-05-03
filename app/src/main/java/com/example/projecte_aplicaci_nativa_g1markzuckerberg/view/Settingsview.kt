package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.nav.Routes
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.ContactFormDialog
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.GradientHeader
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.SettingsViewModel

/* ---------- ENTRY (llamada desde NavHost) ---------- */
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel:    SettingsViewModel
) {
    SettingsView(navController, viewModel)
}


/* ---------- UI PRINCIPAL ---------- */
@Composable
fun SettingsView(
    navController: NavController,
    viewModel: SettingsViewModel
) {
    val isDark by viewModel.isDarkTheme.observeAsState(false)
    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()
    var showContactDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    /* diálogo lorem… */
    val dialogTitle by remember { mutableStateOf<String?>(null) }
    dialogTitle?.let {  }

    /** Altura del header para usarla como padding top */
    val headerHeight = 110.dp

    Box(Modifier.fillMaxSize()) {

/* 1️⃣  LISTA DESPLAZABLE -------------------------------- */
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = headerHeight)
        ) {
            /* separación inicial */
            item { Spacer(Modifier.height(16.dp)) }

            /* ─── tus tarjetas ─── */
            item {
                ExpandableSettingsCard(
                    title = "Creadores del proyecto",
                    body = """
            Este proyecto ha sido desarrollado por:
            • Albert Garrido
            • Joan Linares
        """.trimIndent()
                )
            }
            item {
                SettingsCard("Contacto") {
                    showContactDialog = true
                }
            }
            item { DarkModeCard(isDark) { viewModel.toggleTheme() } }
            item {
                SettingsCard("Política de privacidad") {
                    showPrivacyDialog = true
                }
            }
            item {
                ExpandableSettingsCard(
                    title = "Conoce nuestra API",
                    body = """
            • Toda la información de partidos, jugadores y estadísticas la obtenemos a través de la API de SportMonks.  
            • Gracias a SportMonks por permitirnos usar su servicio y facilitarnos datos actualizados en tiempo real.  
            • Su API es la base de nuestro sistema de puntuaciones, alineaciones y clasificación.
        """.trimIndent()
                )
            }

            /* hueco antes del botón */
            item { Spacer(Modifier.height(24.dp)) }

            /* Botón Cerrar sesión */
            item {
                Button(
                    onClick = {
                        viewModel.logout {
                            navController.navigate(Routes.Home.route) {
                                popUpTo(Routes.Home.route) { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = if (isDark) Color.White else MaterialTheme.colorScheme.onPrimary
                    )
                ) { Text("Cerrar sesión") }
            }

            /* Loading */
            if (isLoading) {
                item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
            }

            /* Error */
            errorMessage?.let { msg ->
                item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) { Text(msg, color = MaterialTheme.colorScheme.error) }
                }
            }
            if (showPrivacyDialog) {
                item {
                    PrivacyPolicyDialog(onDismiss = { showPrivacyDialog = false })
                }
            }

            /* margen inferior */
            item { Spacer(Modifier.height(24.dp)) }
        }


        /* 2️⃣  HEADER FIJO -------------------------------------- */
        Box(
            modifier = Modifier
                .height(headerHeight)
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            GradientHeader(title = "Ajustes")
        }
    }
    if (showContactDialog) {
        ContactFormDialog(
            onDismiss = { showContactDialog = false },
            onSubmit  = {
                // viewModel.sendContactForm(mensaje)
            }
        )
    }
    if (showContactDialog) {
        ContactFormDialog(
            onDismiss = { showContactDialog = false },
            onSubmit  = { msg ->
                viewModel.sendContactForm(msg)
                showContactDialog = false
            }
        )
    }

    /* observar resultado */
    val contactResult by viewModel.contactResult.observeAsState()
    LaunchedEffect(contactResult) {
        contactResult?.let {
            viewModel.clearContactResult()
        }
    }

}

        /* ---------- COMPONENTES REUTILIZABLES ---------- */
        @Composable
        fun SettingsCard(
            title: String,
            onClick: (String) -> Unit
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onClick(title) },
                shape = RoundedCornerShape(18.dp),
                tonalElevation = 2.dp,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(20.dp)
                )
            }
        }

@Composable
fun DarkModeCard(
    isDark: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Modo oscuro", style = MaterialTheme.typography.bodyLarge)
            Switch(checked = isDark, onCheckedChange = { onToggle() })
        }
    }
}

@Composable
fun ExpandableSettingsCard(
    title: String,
    body: String,
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(
                indication = null,
                interactionSource = interactionSource
            ) { expanded = !expanded }
            .animateContentSize(),
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text  = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = if (expanded)
                        Icons.Default.KeyboardArrowUp
                    else
                        Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            if (expanded) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text  = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun PrivacyPolicyDialog(onDismiss: () -> Unit) {
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.5f)
        ) {
            Column {
                // Header de gradiente
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
                        text = "Política de privacidad",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                // Contenido scrolleable
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(scrollState)
                ) {
                    // -------- 1. Información que recopilamos --------
                    Text(
                        text = "1. Información que recopilamos",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Recopilamos los datos que nos proporcionas al registrarte (nombre de usuario, correo electrónico) y la información de uso (alineaciones enviadas, puntuaciones obtenidas) para ofrecerte una experiencia personalizada.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )
                    Spacer(Modifier.height(16.dp))

                    // -------- 2. Cómo usamos tus datos --------
                    Text(
                        text = "2. Cómo usamos tus datos",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "• Procesamos tu información para gestionar tu cuenta y mostrarte tu historial de rendimiento.\n" +
                                "• No compartimos ni vendemos tus datos a terceros.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )
                    Spacer(Modifier.height(16.dp))

                    // -------- 3. Seguridad --------
                    Text(
                        text = "3. Seguridad",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Tus datos se almacenan en servidores seguros con cifrado TLS. Solo personal autorizado tiene acceso a la base de datos.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )
                    Spacer(Modifier.height(16.dp))

                    // -------- 4. Derechos del usuario --------
                    Text(
                        text = "4. Derechos del usuario",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Tienes derecho a acceder, rectificar o eliminar tus datos en cualquier momento. Para ello, ponte en contacto con nosotros a través del formulario de contacto.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )
                    Spacer(Modifier.height(16.dp))

                    // -------- 5. Cambios en esta política --------
                    Text(
                        text = "5. Cambios en esta política",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Podemos actualizar esta política ocasionalmente. Te informaremos de cualquier cambio significativo antes de que entre en vigor.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )
                    Spacer(Modifier.height(24.dp))

                    // Botón Cerrar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(
                                text = "Cerrar",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}