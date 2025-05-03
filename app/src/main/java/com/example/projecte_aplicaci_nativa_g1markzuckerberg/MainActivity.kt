@file:Suppress("DEPRECATION")

package com.example.projecte_aplicaci_nativa_g1markzuckerberg

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.view.EntryPoint
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
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.TokenManager
import android.app.Activity
import androidx.activity.viewModels
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.repository.ContactRepository
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.SettingsViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.factory.SettingsViewModelFactory
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        
        // Inicializamos el TokenManager
        val tokenManager = TokenManager(applicationContext)
        // Creamos el repositorio de autenticación usando el authService de RetrofitClient
        RetrofitClient.authRepository =
            AuthRepository(RetrofitClient.authService, tokenManager)

        // Contact repo  ──────────────────────────────
        val contactRepository = ContactRepository(RetrofitClient.contactService)

        val settingsVM: SettingsViewModel by viewModels {
            SettingsViewModelFactory(RetrofitClient.authRepository, contactRepository)
        }
        // Hacer que el contenido ocupe toda la pantalla
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            HideStatusBar()
            val isDark by settingsVM.isDarkTheme.observeAsState(initial = false)

            Projecteaplicacinativag1markzuckerbergTheme(
                darkTheme = isDark,
                dynamicColor = false
            ) {
                val navController = rememberNavController()
                EntryPoint(navigationController = navController,
                    settingsVM = settingsVM,)
            }
        }
    }
}
@Composable
fun HideStatusBar() {
    val view = LocalView.current
    SideEffect {
        (view.context as? Activity)?.window ?: return@SideEffect
        ViewCompat.getWindowInsetsController(view)?.let { controller ->
            controller.hide(WindowInsetsCompat.Type.statusBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}