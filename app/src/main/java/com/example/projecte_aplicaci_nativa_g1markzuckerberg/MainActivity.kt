package com.example.projecte_aplicaci_nativa_g1markzuckerberg

import EntryPoint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.Projecteaplicacinativag1markzuckerbergTheme
import androidx.core.view.WindowCompat
import android.view.WindowInsets
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.repository.AuthRepository
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.utils.TokenManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
// Inicializamos el TokenManager
        val tokenManager = TokenManager(applicationContext)
        // Creamos el repositorio de autenticación usando el authService de RetrofitClient
        RetrofitClient.authRepository = AuthRepository(RetrofitClient.authService, tokenManager)

        // Hacer que el contenido ocupe toda la pantalla
        //WindowCompat.setDecorFitsSystemWindows(window, false)

        // Ocultar barra de estado
        //window.insetsController?.hide(WindowInsets.Type.statusBars())

        setContent {
            Projecteaplicacinativag1markzuckerbergTheme(
                darkTheme = false,
                dynamicColor = false
            ) {
                val navController = rememberNavController()
                EntryPoint(navigationController = navController)
            }
        }
    }
}