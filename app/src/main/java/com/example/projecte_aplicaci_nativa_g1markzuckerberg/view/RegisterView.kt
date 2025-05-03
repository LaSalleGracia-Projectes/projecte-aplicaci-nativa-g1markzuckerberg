package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.RegisterViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.nav.Routes
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.LocalAppDarkTheme
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.GradientHeader
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

// Color azul principal (ajustar según la guía de estilos)

@Composable
fun RegisterView(
    navController: NavController,
    viewModel: RegisterViewModel
) {
    // 1️⃣ Detectamos el modo
    val isDarkTheme = LocalAppDarkTheme.current

    @DrawableRes
    val emailIconRes = if (isDarkTheme) {
        R.drawable.ic_email_dark
    } else {
        R.drawable.ic_email
    }
    // 2️⃣ Creamos un color reutilizable para texto/contenido
    val contentColor = if (isDarkTheme) Color.White else Color.Black
    val textColor = if (isDarkTheme) Color.LightGray else Color.Black

    // 3️⃣ Colores de los botones (contenedor + contenido)
    val buttonColors = ButtonDefaults.outlinedButtonColors(
        containerColor = if (isDarkTheme) Color.DarkGray else Color.White,
        contentColor   = contentColor
    )

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) {
                viewModel.handleGoogleToken(idToken) {
                    navController.navigate(Routes.HomeLoged.route)
                }
            }
        } catch (e: ApiException) {
            Log.e("GoogleSignIn", "Error: ${e.message}")
        }
    }

    LaunchedEffect(Unit) {
        viewModel.initGoogle(context)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkTheme) Color.Black else Color.White)
    ) {
        // Barra superior: Usando GradientHeader
        GradientHeader(
            title = "Crear una cuenta",
            onBack = { navController.popBackStack() },
            height = 140.dp
        )

        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.Gray
        )

        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Botón: Crear cuenta con tu email
            OutlinedButton(
                onClick = {
                    navController.navigate(Routes.LoginMobile.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = buttonColors
            ) {
                Image(
                    painter = painterResource(id = emailIconRes),
                    contentDescription = "Email icon",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Crear cuenta con tu email",
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón: Continuar con Google
            OutlinedButton(
                onClick = {
                    launcher.launch(viewModel.getGoogleSignInIntent())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = buttonColors
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google logo",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Continuar con Google",
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Texto y botón para iniciar sesión
            Text(
                text = "¿Ya tienes cuenta?",
                fontSize = 14.sp,
                color = textColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    navController.navigate(Routes.Login.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = buttonColors
            ) {
                Text(
                    text = "Iniciar sesión",
                    fontSize = 16.sp
                )
            }
        }
    }
}
