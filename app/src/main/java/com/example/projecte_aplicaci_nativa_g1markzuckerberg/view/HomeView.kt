package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.proyecte_aplicaci_nativa_g1markzuckerberg.viewmodel.HomeViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.nav.Routes

// Ajusta estos valores a tu paleta de colores y tipografías reales.
private val BluePrimary @Composable
get() = colorScheme.primary

private val GraySecondary @Composable
get() = colorScheme.surfaceVariant

private val OnBluePrimary @Composable
get() = colorScheme.onPrimary

private val OnGraySecondary @Composable
get() = colorScheme.onSurfaceVariant

@Composable
fun HomeView(
    navController: NavController,
    viewModel: HomeViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        // CABECERA: Título más alto
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp) // Aumenta la altura según tu wireframe
                .background(BluePrimary), // Fondo color primario
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Crea tu liga con tus amigos!",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = OnBluePrimary,
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

        // BOTONES INFERIORES: uno encima del otro, más grandes
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    //homeViewModel.onCrearCuenta()
                    navController.navigate(Routes.Register.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp), // Botón más grande
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
            ) {
                Text(
                    text = "CREAR CUENTA",
                    color = OnBluePrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Button(
                onClick = {
                    //homeViewModel.onIniciarSesion()
                    navController.navigate(Routes.Login.route)
                },
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
