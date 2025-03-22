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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hacer que el contenido ocupe toda la pantalla
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Ocultar barra de estado
        window.insetsController?.hide(WindowInsets.Type.statusBars())

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