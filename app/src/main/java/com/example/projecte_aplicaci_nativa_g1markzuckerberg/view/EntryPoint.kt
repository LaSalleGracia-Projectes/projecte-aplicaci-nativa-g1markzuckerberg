package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.factory.HomeLogedViewModelFactory
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.nav.Routes
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.NavbarView
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.view.screens.NotificationScreen
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.* // HomeView, LoginScreen, etc.

@Composable
fun EntryPoint(
    navigationController: NavHostController,
    registerViewModel: RegisterViewModel = viewModel(),
    homeLogedViewModel: HomeLogedViewModel = viewModel(
        factory = HomeLogedViewModelFactory(RetrofitClient.authRepository)
    ),
    draftViewModel: DraftViewModel = viewModel(),
    notificationViewModel: NotificationViewModel = viewModel() // 👈 nuevo
) {

    /* --- 1. Redirección automática si ya hay token --- */
    LaunchedEffect(Unit) {
        val token = RetrofitClient.authRepository.getToken()
        if (!token.isNullOrEmpty()) {
            navigationController.navigate(Routes.HomeLoged.route) {
                popUpTo(Routes.Home.route) { inclusive = true }
            }
        }
    }

    /* --- 2. Mostrar / ocultar navbar según ruta --- */
    val navBackStackEntry by navigationController.currentBackStackEntryAsState()
    val routesConNavbar = listOf(
        Routes.HomeLoged.route,
        Routes.Settings.route,
        Routes.LigaView.route,
        Routes.UserDraftView.route,
        Routes.DraftScreen.route,
        Routes.NotificationScreen.route      // 👈 añadida
    )
    val currentRoute = navBackStackEntry?.destination?.route
    val showNavBar = currentRoute in routesConNavbar

    /* --- 3. Scaffold + NavHost --- */
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .consumeWindowInsets(WindowInsets.systemBars),
        contentWindowInsets = WindowInsets.systemBars,
        bottomBar = {
            if (showNavBar) {
                NavbarView(
                    navController = navigationController,
                    onProfileClick = { /* Acción perfil */ },
                    onHomeClick = {
                        navigationController.navigate(Routes.HomeLoged.route)
                    },
                    onNotificationsClick = {           // 👈 Navega a notificaciones
                        navigationController.navigate(Routes.NotificationScreen.route)
                    },
                    onSettingsClick = {
                        navigationController.navigate(Routes.Settings.route)
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navigationController,
            startDestination = Routes.Home.route,
            modifier = Modifier
                .consumeWindowInsets(innerPadding)
                .padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition  = { fadeOut(animationSpec = tween(300)) },
            popEnterTransition = { fadeIn(animationSpec = tween(300)) },
            popExitTransition  = { fadeOut(animationSpec = tween(300)) }
        ) {

            /* ---------- Pantallas principales ---------- */
            composable(Routes.Home.route) {
                HomeView(navController = navigationController)
            }

            composable(Routes.Register.route) {
                RegisterView(navController = navigationController, viewModel = registerViewModel)
            }

            composable(Routes.Login.route) {
                LoginScreen(navController = navigationController)
            }

            composable(Routes.LoginMobile.route) {
                RegisterScreen(navController = navigationController)
            }

            composable(Routes.HomeLoged.route) {
                HomeLogedView(navController = navigationController, homeLogedViewModel = homeLogedViewModel)
            }

            composable(Routes.Settings.route) {
                SettingsScreen(navController = navigationController)
            }

            /* ---------- LigaView ---------- */
            composable(Routes.LigaView.route) { backStackEntry ->
                val ligaCode = backStackEntry.arguments?.getString("ligaCode") ?: ""
                val ligaViewModel: LigaViewModel = viewModel()
                LigaView(
                    navController = navigationController,
                    ligaCode = ligaCode,
                    ligaViewModel = ligaViewModel,
                    draftViewModel = draftViewModel
                )
            }

            /* ---------- UserDraftView con argumentos ---------- */
            composable(
                Routes.UserDraftView.route,
                arguments = listOf(
                    navArgument("leagueId")        { type = NavType.StringType },
                    navArgument("userId")          { type = NavType.StringType },
                    navArgument("userName")        { type = NavType.StringType },
                    navArgument("userPhotoUrl")    { type = NavType.StringType },
                    navArgument("createdJornada")  { type = NavType.IntType    },
                    navArgument("currentJornada")  { type = NavType.IntType    }
                )
            ) { backStackEntry ->
                UserDraftView(
                    navController      = navigationController,
                    userDraftViewModel = UserDraftViewModel(),
                    leagueId           = backStackEntry.arguments?.getString("leagueId") ?: "",
                    userId             = backStackEntry.arguments?.getString("userId") ?: "",
                    userName           = backStackEntry.arguments?.getString("userName") ?: "",
                    userPhotoUrl       = backStackEntry.arguments?.getString("userPhotoUrl") ?: "",
                    createdJornada     = backStackEntry.arguments!!.getInt("createdJornada"),
                    currentJornada     = backStackEntry.arguments!!.getInt("currentJornada")
                )
            }

            /* ---------- DraftScreen ---------- */
            composable(Routes.DraftScreen.route) {
                DraftScreen(
                    navController = navigationController,
                    viewModel = draftViewModel,
                    innerPadding = innerPadding
                )
            }

            /* ---------- NotificationScreen (nueva) ---------- */
            composable(Routes.NotificationScreen.route) {
                NotificationScreen(
                    navController = navigationController,
                    viewModel = notificationViewModel
                )
            }
        }
    }
}
