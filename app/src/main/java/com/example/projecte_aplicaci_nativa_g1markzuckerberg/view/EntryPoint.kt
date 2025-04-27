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
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.RegisterViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.DraftViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.HomeLogedViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.LigaViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.UserDraftViewModel

@Composable
fun EntryPoint(
    navigationController: NavHostController,
    registerViewModel: RegisterViewModel = viewModel(),
    homeLogedViewModel: HomeLogedViewModel = viewModel(
        factory = HomeLogedViewModelFactory(RetrofitClient.authRepository)
    ),
    draftViewModel: DraftViewModel = viewModel()

) {
    // Comprobación del token al arrancar la app
    LaunchedEffect(Unit) {
        val token = RetrofitClient.authRepository.getToken()
        if (!token.isNullOrEmpty()) {
            navigationController.navigate(Routes.HomeLoged.route) {
                popUpTo(Routes.Home.route) { inclusive = true }
            }
        }
    }
    val navBackStackEntry by navigationController.currentBackStackEntryAsState()
    // Define las rutas en las que deseas mostrar la navbar
    val routesConNavbar = listOf(
        Routes.HomeLoged.route,
        Routes.Settings.route,
        Routes.LigaView.route,
        Routes.UserDraftView.route,
        Routes.DraftScreen.route,
        Routes.PlayersList.route,
        Routes.PlayerDetail.route
    )
    // Averigua si la ruta actual está en la lista
    val currentRoute = navBackStackEntry?.destination?.route
    val showNavBar = currentRoute in routesConNavbar

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .consumeWindowInsets(WindowInsets.systemBars), // 👈 esto previene "salto de layout"
        contentWindowInsets = WindowInsets.systemBars, // 👈 importante
        bottomBar = {
            if (showNavBar) {
                // La navbar se mostrará solo en las rutas indicadas.
                // Ajusta los callbacks según corresponda
                NavbarView(
                    navController = navigationController,
                    onProfileClick = { /* Acción para perfil */ },
                    onHomeClick = { navigationController.navigate(Routes.HomeLoged.route) },
                    onNotificationsClick = { /* Acción para notificaciones */ },
                    onPlayersClick       = { navigationController.navigate(Routes.PlayersList.createRoute()) },
                    onSettingsClick = { navigationController.navigate(Routes.Settings.route) }
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
        enterTransition = {
            // Entrada: combina fadeIn y un pequeño zoom "in"
            fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            // Salida: combina fadeOut y un zoom "out"
            fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            // Al volver atrás, se puede invertir el efecto
            fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300))
        }
    ) {
        composable(Routes.Home.route) {
            HomeView(
                navController = navigationController,
            )
        }
        composable(Routes.Register.route) {
            RegisterView(
                navController = navigationController,
                viewModel = registerViewModel
            )
        }
        composable(Routes.Login.route) {
            LoginScreen(navController = navigationController)
        }
        composable(Routes.LoginMobile.route) {
            RegisterScreen(navController = navigationController)
        }
        composable(Routes.HomeLoged.route) {
            HomeLogedView(
                navController = navigationController,
                homeLogedViewModel = homeLogedViewModel
            )
        }
        composable(Routes.Settings.route) {
            SettingsScreen(navController = navigationController)
        }
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
        composable(
            Routes.UserDraftView.route,
            arguments = listOf(
                navArgument("leagueId") { type = NavType.StringType },
                navArgument("userId") { type = NavType.StringType },
                navArgument("userName") { type = NavType.StringType },
                navArgument("userPhotoUrl") { type = NavType.StringType },
                navArgument("createdJornada")   { type = NavType.IntType    },
                navArgument("currentJornada")   { type = NavType.IntType    }
            )
        ) { backStackEntry ->
            val leagueId = backStackEntry.arguments?.getString("leagueId") ?: ""
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val userName = backStackEntry.arguments?.getString("userName") ?: ""
            val userPhotoUrl = backStackEntry.arguments?.getString("userPhotoUrl") ?: ""
            val createdJornada   = backStackEntry.arguments!!.getInt   ("createdJornada")
            val currentJornada   = backStackEntry.arguments!!.getInt   ("currentJornada")

            UserDraftView(
                navController      = navigationController,
                userDraftViewModel = UserDraftViewModel(),
                leagueId           = leagueId,
                userId             = userId,
                userName           = userName,
                userPhotoUrl       = userPhotoUrl,
                createdJornada     = createdJornada,
                currentJornada     = currentJornada
            )
        }
        composable(Routes.DraftScreen.route) {
            DraftScreen(
                navController = navigationController,
                viewModel = draftViewModel,
                innerPadding = innerPadding
            )
        }
        composable(Routes.PlayersList.route) {
            PlayersView(navController = navigationController)
        }
        composable(
            route = Routes.PlayerDetail.route,
            arguments = listOf(navArgument("playerId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val playerId = backStackEntry.arguments!!.getString("playerId")!!
            PlayerDetailView(
                navController = navigationController,
                playerId = playerId
            )
        }
    }
}
}
