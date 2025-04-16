import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
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
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.view.DraftScreen
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.LoginViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.RegisterEmailViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.RegisterViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.view.HomeView
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.view.HomeLogedView
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.view.LoginScreen
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.view.RegisterScreen
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.view.RegisterView
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.view.SettingsScreen
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.view.LigaView
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.view.UserDraftView
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.DraftViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.HomeLogedViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.LigaViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.UserDraftViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.factory.LoginViewModelFactory
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.factory.RegisterEmailViewModelFactory
import com.example.proyecte_aplicaci_nativa_g1markzuckerberg.viewmodel.HomeViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun EntryPoint(
    navigationController: NavHostController,
    homeViewModel: HomeViewModel = viewModel(),
    registerViewModel: RegisterViewModel = viewModel(),
    loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(RetrofitClient.authRepository)
    ),
    registerEmailViewModel: RegisterEmailViewModel = viewModel(
        factory = RegisterEmailViewModelFactory(RetrofitClient.authRepository)
    ),
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
        Routes.DraftScreen.route
    )
    // Averigua si la ruta actual está en la lista
    val currentRoute = navBackStackEntry?.destination?.route
    val showNavBar = currentRoute in routesConNavbar

    Scaffold(
        bottomBar = {
            if (showNavBar) {
                // La navbar se mostrará solo en las rutas indicadas.
                // Ajusta los callbacks según corresponda
                NavbarView(
                    navController = navigationController,
                    onProfileClick = { /* Acción para perfil */ },
                    onHomeClick = { navigationController.navigate(Routes.HomeLoged.route) },
                    onNotificationsClick = { /* Acción para notificaciones */ },
                    onSettingsClick = { navigationController.navigate(Routes.Settings.route) }
                )
            }
        }
    ) { innerPadding ->
    NavHost(
        navController = navigationController,
        startDestination = Routes.Home.route,
        modifier = Modifier.padding(innerPadding), // ✅ Aquí usas innerPadding
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
                viewModel = homeViewModel
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
                navArgument("userPhotoUrl") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val leagueId = backStackEntry.arguments?.getString("leagueId") ?: ""
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val userName = backStackEntry.arguments?.getString("userName") ?: ""
            val userPhotoUrl = backStackEntry.arguments?.getString("userPhotoUrl") ?: ""
            UserDraftView(
                navController = navigationController,
                userDraftViewModel = UserDraftViewModel(), // O viewModel() si usas Hilt u otro inyector
                leagueId = leagueId,
                userId = userId,
                userName = userName,
                userPhotoUrl = userPhotoUrl
            )
        }
        composable(Routes.DraftScreen.route) {
            // Obtén el ViewModel usando el scope actual de la navegación
            DraftScreen(
                navController = navigationController,
                viewModel = draftViewModel,
                innerPadding = innerPadding
            )
        }


    }
}
    }
