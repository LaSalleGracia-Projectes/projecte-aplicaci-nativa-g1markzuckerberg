package com.example.projecte_aplicaci_nativa_g1markzuckerberg.nav
import android.net.Uri

sealed class Routes(val route: String) {
    object Home : Routes("home")
    // Puedes agregar más rutas, por ejemplo:
    object Register : Routes("register")
    object Login : Routes("login")
    object LoginMobile : Routes("register_email") // Nueva ruta añadida
    object HomeLoged : Routes("home_loged") // Nueva ruta añadida
    object Settings: Routes("settings")
    object LigaView : Routes("liga_view/{ligaCode}") {
        fun createRoute(ligaCode: String) = "liga_view/$ligaCode"
    }
    object UserDraftView : Routes("userdraft/{leagueId}/{userId}/{userName}/{userPhotoUrl}/{createdJornada}/{currentJornada}") {
        fun createRoute(
            leagueId: String,
            userId: String,
            userName: String,
            userPhotoUrl: String,
            createdJornada: Int,
            currentJornada: Int
        ) = "userdraft/$leagueId/$userId/${Uri.encode(userName)}/${Uri.encode(userPhotoUrl)}/$createdJornada/$currentJornada"
    }
    object DraftScreen : Routes("draft_screen") {
        fun createRoute() = "draft_screen"
    }

}
