package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.GradientHeader
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.SettingsViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.factory.SettingsViewModelFactory

/* ---------- ENTRY (llamada desde NavHost) ---------- */
@Composable
fun SettingsScreen(navController: NavController,
                   viewModel: SettingsViewModel
) {
    SettingsView(navController, viewModel)
}

/* ---------- UI PRINCIPAL ---------- */
@Composable
fun SettingsView(
    navController: NavController,
    viewModel: SettingsViewModel
) {
    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()
    val scrollState = rememberScrollState()
    val isDark by viewModel.isDarkTheme.observeAsState(initial = false)

    /* Para diálogos de “Lorem ipsum” */
    var dialogTitle by remember { mutableStateOf<String?>(null) }

    dialogTitle?.let { title ->
        AlertDialog(
            onDismissRequest = { dialogTitle = null },
            title = { Text(title) },
            text  = { Text("Lorem ipsum dolor sit amet, consectetur adipiscing elit…") },
            confirmButton = {
                TextButton(onClick = { dialogTitle = null }) { Text("Cerrar") }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .verticalScroll(scrollState)
    ) {
        /* Header degradado */
        GradientHeader(title = "Ajustes")

        Spacer(Modifier.height(16.dp))

        /* Tarjetas */
        SettingsCard("Creadores del proyecto") { dialogTitle = it }
        SettingsCard("Contacto") { dialogTitle = it }
        DarkModeCard(isDark) { viewModel.toggleTheme() }
        SettingsCard("Política de privacidad") { dialogTitle = it }
        SettingsCard("Conoce nuestra API") { dialogTitle = it }

        /* Espaciador y botón inferior */
        Spacer(Modifier.height(24.dp))

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
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) { Text("Cerrar sesión") }

        if (isLoading) {
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
        }

        errorMessage?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        Spacer(Modifier.height(24.dp))
    }
}

/* ---------- COMPONENTES REUTILIZABLES ---------- */
@Composable
fun SettingsCard(
    title: String,
    onClick: (String) -> Unit          // devolvemos el título para el diálogo
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick(title) },
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface
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
        color = MaterialTheme.colorScheme.surface
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
