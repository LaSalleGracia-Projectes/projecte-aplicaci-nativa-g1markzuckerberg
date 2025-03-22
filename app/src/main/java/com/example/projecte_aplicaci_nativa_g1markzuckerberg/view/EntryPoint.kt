import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.nav.Routes
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.LoginViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.RegisterEmailViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.RegisterViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.view.*
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.HomeLogedViewModel
import com.example.proyecte_aplicaci_nativa_g1markzuckerberg.viewmodel.HomeViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun EntryPoint(
    navigationController: NavHostController,
    homeViewModel: HomeViewModel = viewModel(),
    registerViewModel: RegisterViewModel = viewModel(),
    loginViewModel: LoginViewModel = viewModel(),
    registerEmailViewModel: RegisterEmailViewModel = viewModel(),
    homeLogedViewModel: HomeLogedViewModel = viewModel()
) {
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
            LoginView(
                navController = navigationController,
                viewModel = loginViewModel
            )
        }
        composable(Routes.LoginMobile.route) {
            RegisterEmailView(
                navController = navigationController,
                viewModel = registerEmailViewModel
            )
        }
        composable(Routes.HomeLoged.route) {
            HomeLogedView(
                navController = navigationController,
                viewModel = homeLogedViewModel)
        }
    }
}
