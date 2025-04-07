import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.factory.HomeLogedViewModelFactory
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.nav.Routes
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.LoginViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.RegisterEmailViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.RegisterViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.view.*
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.HomeLogedViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.LigaViewModel
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
    )) {
    // Ejemplo: comprobación del token al arrancar la app
    LaunchedEffect(Unit) {
        val token = RetrofitClient.authRepository.getToken()
        if (!token.isNullOrEmpty() /*&& isTokenValid(token)*/) {
            navigationController.navigate(Routes.HomeLoged.route) {
                popUpTo(Routes.Home.route) { inclusive = true }
            }
        }
    }
    NavHost(
        navController = navigationController,
        startDestination = Routes.Home.route,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(durationMillis = 300)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(durationMillis = 300)
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(durationMillis = 300)
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(durationMillis = 300)
            )
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
            RegisterScreen(
                navController = navigationController)
        }
        composable(Routes.HomeLoged.route) {
            HomeLogedView(
                navController = navigationController,
                homeLogedViewModel = homeLogedViewModel)
        }
        composable(Routes.Settings.route) {
            SettingsScreen(
                navController = navigationController
            )
        }
        composable(Routes.LigaView.route) { backStackEntry ->
            val ligaCode = backStackEntry.arguments?.getString("ligaCode") ?: ""
            val ligaViewModel: LigaViewModel = viewModel()
            LigaView(navController = navigationController, ligaCode = ligaCode, ligaViewModel = ligaViewModel)
        }

    }
}
