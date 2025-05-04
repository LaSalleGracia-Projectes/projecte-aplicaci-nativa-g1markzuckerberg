package com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.nav.Routes

@Composable
fun NavBarItem(
    iconResId: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(36.dp)
            .clip(MaterialTheme.shapes.small)
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = contentDescription,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
fun VerticalDivider(
    color: Color = Color.White,
    thickness: Dp = 1.dp
) {
    Box(
        modifier = Modifier
            .height(24.dp)
            .width(thickness)
            .background(color)
    )
}

@Composable
fun NavbarView(
    navController: NavController,
    onProfileClick: () -> Unit,
    onHomeClick: () -> Unit = { navController.navigate(Routes.HomeLoged.route) },
    onNotificationsClick: () -> Unit,
    onPlayersClick: () -> Unit,
    onSettingsClick: () -> Unit = { navController.navigate(Routes.Settings.route) },
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    )
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NavBarItem(iconResId = R.drawable.ic_profile, contentDescription = "Perfil", onClick = onProfileClick)
            VerticalDivider()
            NavBarItem(iconResId = R.drawable.ic_home, contentDescription = "Inicio", onClick = onHomeClick)
            VerticalDivider()
            NavBarItem(iconResId = R.drawable.ic_players, contentDescription = "Jugadores", onClick = onPlayersClick)
            VerticalDivider()
            NavBarItem(iconResId = R.drawable.ic_notifications, contentDescription = "Notificaciones", onClick = onNotificationsClick)
            VerticalDivider()
            NavBarItem(iconResId = R.drawable.ic_settings, contentDescription = "Ajustes", onClick = onSettingsClick)
        }

        // 👇 este spacer evita el espacio blanco y el salto visual
        Spacer(modifier = Modifier.padding(WindowInsets.navigationBars.asPaddingValues()))
    }
}