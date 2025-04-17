package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.nav.Routes
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.SettingsViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.factory.SettingsViewModelFactory

@Composable
fun SettingsScreen(navController: NavController) {
    // Usamos el factory para inyectar el AuthRepository
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(RetrofitClient.authRepository)
    )
    // Simplemente llama a SettingsView, pasándole todo lo que necesites
    SettingsView(navController = navController, viewModel = settingsViewModel)
}

@Composable
fun SettingsView(
    navController: NavController,
    viewModel: SettingsViewModel
) {
    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()


        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AjustesSeccion("Creadores del proyecto")
                AjustesSeccion("Contacto")
                AjustesSeccion("Modo oscuro (switch)")
                AjustesSeccion("Política de privacidad")
                AjustesSeccion("Conoce nuestra API")

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                errorMessage?.let { error ->
                    Text(text = error, color = MaterialTheme.colorScheme.error)
                }
            }

            // BOTÓN DE CERRAR SESIÓN
            Button(
                onClick = {
                    viewModel.logout {
                        // Limpiar token y volver a Home
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Home.route) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(text = "Cerrar sesión")
            }
        }
    }

@Composable
fun AjustesSeccion(titulo: String) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    }
}
