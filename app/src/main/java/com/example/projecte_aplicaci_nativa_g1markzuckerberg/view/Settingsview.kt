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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.nav.Routes
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.repository.AuthRepository
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.repository.ContactRepository
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.ContactFormDialog
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.GradientHeader
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.TokenManager
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.SettingsViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.factory.SettingsViewModelFactory

/* ---------- ENTRY (llamada desde NavHost) ---------- */
/* ---------- ENTRY (llamada desde NavHost) ---------- */
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel:    SettingsViewModel        // ← ahora lo recibes
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

    /* diálogo lorem… */
    var dialogTitle by remember { mutableStateOf<String?>(null) }
    dialogTitle?.let { /* tu AlertDialog… */ }

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
            item { ExpandableSettingsCard("Creadores del proyecto") }
            item {
                SettingsCard("Contacto") {
                    showContactDialog = true
                }
            }
            item { DarkModeCard(isDark) { viewModel.toggleTheme() } }
            item { ExpandableSettingsCard("Política de privacidad") }
            item { ExpandableSettingsCard("Conoce nuestra API") }

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
                        contentColor   = MaterialTheme.colorScheme.onPrimary
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

            /* margen inferior */
            item { Spacer(Modifier.height(24.dp)) }
        }


        /* 2️⃣  HEADER FIJO -------------------------------------- */
        Box(                                       // caja contenedora
            modifier = Modifier
                .height(headerHeight)
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            GradientHeader(title = "Ajustes")      // tu función original
        }
    }
    if (showContactDialog) {
        ContactFormDialog(
            onDismiss = { showContactDialog = false },
            onSubmit  = { mensaje ->
                // aquí llamas al ViewModel / Repositorio que envía el form:
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
        contactResult?.let { res ->
            viewModel.clearContactResult()
            if (res.isSuccess) {
                // mostrar CustomAlertDialogSingleButton de éxito
            } else {
                // mostrar error
            }
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
                // 🔥 cambiamos a surfaceVariant (más oscuro que surface)
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
        // 🔥 también aquí
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
    body: String = loremIpsum,
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
        // 🔥 y aquí
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


private const val loremIpsum =
    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer non dui ut est gravida luctus."
