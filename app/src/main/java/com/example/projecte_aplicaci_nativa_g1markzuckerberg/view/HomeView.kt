package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.nav.Routes
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.LocalAppDarkTheme

// Colores secundarios y on-colors dinámicos
private val GraySecondary @Composable get() = MaterialTheme.colorScheme.surfaceVariant
private val OnBluePrimary @Composable get() = MaterialTheme.colorScheme.onPrimary
private val OnGraySecondary @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

@Composable
fun HomeView(navController: NavController) {
    // Se ignora back
    BackHandler {}
    // Detectar tema
    val isDarkTheme = LocalAppDarkTheme.current
    val backgroundColor = if (isDarkTheme) Color.Black else MaterialTheme.colorScheme.background

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // CABECERA: gradiente
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.home_title),
                color = if (isDarkTheme) Color.White else OnBluePrimary,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        }

        // IMAGEN
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.fantasydraft),
                contentDescription = stringResource(R.string.fantasy_logo_desc),
                modifier = Modifier
                    .size(600.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }

        // BOTONES
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Crear cuenta
            GradientButton(
                text = stringResource(R.string.create_account),
                onClick = { navController.navigate(Routes.Register.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
            // Iniciar sesión
            Button(
                onClick = { navController.navigate(Routes.Login.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GraySecondary)
            ) {
                Text(
                    text = stringResource(R.string.login),
                    color = OnGraySecondary,
                    style = MaterialTheme.typography.bodyLarge
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
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        )
    ),
    textColor: Color = OnBluePrimary
) {
    val isDarkTheme = LocalAppDarkTheme.current
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues()
    ) {
        Box(
            modifier = Modifier
                .background(gradient, shape = RoundedCornerShape(8.dp))
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (isDarkTheme) Color.White else textColor,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}