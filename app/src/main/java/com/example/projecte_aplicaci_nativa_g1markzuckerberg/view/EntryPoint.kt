package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
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
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.service.NotificationSocketService
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.NavbarView
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.*

@Composable
fun EntryPoint(
    navigationController: NavHostController,
    registerViewModel: RegisterViewModel = viewModel(),
    homeLogedViewModel: HomeLogedViewModel = viewModel(
        factory = HomeLogedViewModelFactory(RetrofitClient.authRepository)
    ),
    draftViewModel: DraftViewModel = viewModel(),
    notificationViewModel: NotificationViewModel = viewModel()
) {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            ContextCompat.startForegroundService(
                context, Intent(context, NotificationSocketService::class.java)
            )
        }
    }

    LaunchedEffect(Unit) {
        RetrofitClient.authRepository.getToken()
            ?.takeIf { it.isNotEmpty() }
            ?.let {
                navigationController.navigate(Routes.HomeLoged.route) {
                    popUpTo(Routes.Home.route) { inclusive = true }
                }
            }
    }

    LaunchedEffect(RetrofitClient.authRepository.getToken()) {
        RetrofitClient.authRepository.getToken()
            ?.takeIf { it.isNotEmpty() }
            ?.let {
                val ok =
                    if (Build.VERSION.SDK_INT >= 33) {
                        ContextCompat.checkSelfPermission(
                            context, Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                    } else true

                if (ok) {
                    ContextCompat.startForegroundService(
                        context, Intent(context, NotificationSocketService::class.java)
                    )
                } else {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
    }

    val navBackStackEntry by navigationController.currentBackStackEntryAsState()
    val routesConNavbar = listOf(
        Routes.HomeLoged.route,
        Routes.Settings.route,
        Routes.LigaView.route,
        Routes.UserDraftView.route,
        Routes.DraftScreen.route,
        Routes.NotificationScreen.route,
        Routes.PlayersList.route,
        Routes.PlayerDetail.route,
        Routes.UserSelf.route          // ← añadido
    )
    val currentRoute = navBackStackEntry?.destination?.route
    val showNavBar = currentRoute in routesConNavbar

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .consumeWindowInsets(WindowInsets.systemBars),
        contentWindowInsets = WindowInsets.systemBars,
        bottomBar = {
            if (showNavBar) {
                NavbarView(
                    navController        = navigationController,
                    onProfileClick       = { navigationController.navigate(Routes.UserSelf.route) },
                    onHomeClick          = { navigationController.navigate(Routes.HomeLoged.route) },
                    onNotificationsClick = { navigationController.navigate(Routes.NotificationScreen.route) },
                    onPlayersClick       = { navigationController.navigate(Routes.PlayersList.route) },
                    onSettingsClick      = { navigationController.navigate(Routes.Settings.route) }
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
            enterTransition = { fadeIn(tween(300)) },
            exitTransition  = { fadeOut(tween(300)) },
            popEnterTransition = { fadeIn(tween(300)) },
            popExitTransition  = { fadeOut(tween(300)) }
        ) {

            composable(Routes.Home.route)        { HomeView(navController = navigationController) }
            composable(Routes.Register.route)    { RegisterView(navController = navigationController, viewModel = registerViewModel) }
            composable(Routes.Login.route)       { LoginScreen(navController = navigationController) }
            composable(Routes.LoginMobile.route) { RegisterScreen(navController = navigationController) }
            composable(Routes.HomeLoged.route)   { HomeLogedView(navController = navigationController, homeLogedViewModel = homeLogedViewModel) }
            composable(Routes.Settings.route)    { SettingsScreen(navController = navigationController) }

            composable(Routes.LigaView.route) { backStackEntry ->
                val ligaCode = backStackEntry.arguments?.getString("ligaCode") ?: ""
                LigaView(
                    navController  = navigationController,
                    ligaCode       = ligaCode,
                    ligaViewModel  = viewModel(),
                    draftViewModel = draftViewModel
                )
            }

            composable(
                Routes.UserDraftView.route,
                arguments = listOf(
                    navArgument("leagueId")       { type = NavType.StringType },
                    navArgument("userId")         { type = NavType.StringType },
                    navArgument("userName")       { type = NavType.StringType },
                    navArgument("userPhotoUrl")   { type = NavType.StringType },
                    navArgument("createdJornada") { type = NavType.IntType },
                    navArgument("currentJornada") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                UserDraftView(
                    navController     = navigationController,
                    userDraftViewModel= viewModel(),
                    leagueId          = backStackEntry.arguments?.getString("leagueId") ?: "",
                    userId            = backStackEntry.arguments?.getString("userId") ?: "",
                    userName          = backStackEntry.arguments?.getString("userName") ?: "",
                    userPhotoUrl      = backStackEntry.arguments?.getString("userPhotoUrl") ?: "",
                    createdJornada    = backStackEntry.arguments!!.getInt("createdJornada"),
                    currentJornada    = backStackEntry.arguments!!.getInt("currentJornada")
                )
            }

            composable(Routes.DraftScreen.route)        { DraftScreen(navController = navigationController, viewModel = draftViewModel, innerPadding = innerPadding) }
            composable(Routes.NotificationScreen.route) { NotificationScreen(navController = navigationController, viewModel = notificationViewModel) }
            composable(Routes.PlayersList.route)        { PlayersView(navController = navigationController) }
            composable(
                route = Routes.PlayerDetail.route,
                arguments = listOf(navArgument("playerId") { type = NavType.StringType })
            ) { backStackEntry ->
                PlayerDetailView(
                    navController = navigationController,
                    playerId      = backStackEntry.arguments!!.getString("playerId")!!
                )
            }

            composable(Routes.UserSelf.route) { UserSelfScreen(navController = navigationController) }
        }
    }
}
