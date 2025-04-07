package com.example.projecte_aplicaci_nativa_g1markzuckerberg

import EntryPoint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.Projecteaplicacinativag1markzuckerbergTheme
import androidx.core.view.WindowCompat
import androidx.annotation.RequiresApi
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.repository.AuthRepository
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.utils.TokenManager
import android.app.Activity
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
// Inicializamos el TokenManager
        val tokenManager = TokenManager(applicationContext)
        // Creamos el repositorio de autenticación usando el authService de RetrofitClient
        RetrofitClient.authRepository = AuthRepository(RetrofitClient.authService, tokenManager)

        // Hacer que el contenido ocupe toda la pantalla
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            HideStatusBar()

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
@Composable
fun HideStatusBar() {
    val view = LocalView.current
    SideEffect {
        val window = (view.context as? Activity)?.window ?: return@SideEffect
        ViewCompat.getWindowInsetsController(view)?.let { controller ->
            controller.hide(WindowInsetsCompat.Type.statusBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}