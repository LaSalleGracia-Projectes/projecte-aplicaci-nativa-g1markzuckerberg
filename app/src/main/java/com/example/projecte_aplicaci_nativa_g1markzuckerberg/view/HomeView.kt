package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.nav.Routes
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color




private val GraySecondary @Composable
get() = colorScheme.surfaceVariant

private val OnBluePrimary @Composable
get() = colorScheme.onPrimary

private val OnGraySecondary @Composable
get() = colorScheme.onSurfaceVariant

@Composable
fun HomeView(
    navController: NavController,
) {
    BackHandler {}
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        // CABECERA: Título con gradiente (igual que en HomeLogedView)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            colorScheme.primary,
                            colorScheme.secondary
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Crea tu liga con tus amigos!",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )
        }

        // ZONA CENTRAL: Imagen
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.fantasydraft),
                contentDescription = "Fantasy Draft Logo",
                modifier = Modifier
                    .size(600.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }

        // BOTONES INFERIORES: Botones de Crear Cuenta e Iniciar Sesión
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Botón con gradiente para Crear Cuenta
            GradientButton(
                text = "CREAR CUENTA",
                color = Color.White,
                onClick = {
                    navController.navigate(Routes.Register.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
            // Botón para Iniciar Sesión, con fondo gris
            Button(
                onClick = { navController.navigate(Routes.Login.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GraySecondary)
            ) {
                Text(
                    text = "INICIAR SESIÓN",
                    color = OnGraySecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradient: Brush = Brush.horizontalGradient(
        colors = listOf(
            colorScheme.primary,
            colorScheme.secondary
        )
    ),
    textColor: Color = colorScheme.onPrimary,
    color: Color
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), // Fondo transparente para mostrar el gradiente
        contentPadding = PaddingValues() // Quitamos el padding interno para usar el Box que lo gestione
    ) {
        Box(
            modifier = Modifier
                .background(gradient, shape = RoundedCornerShape(8.dp))
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
