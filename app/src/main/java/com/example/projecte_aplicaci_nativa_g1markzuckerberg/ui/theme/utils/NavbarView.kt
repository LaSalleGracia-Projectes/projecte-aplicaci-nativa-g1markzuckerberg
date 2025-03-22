package com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R

@Composable
fun NavBarItem(
    iconResId: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = contentDescription,
            modifier = Modifier.size(28.dp)
        )
    }
}
@Composable
fun NavbarView(
    onProfileClick: () -> Unit,
    onHomeClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color(0xFFD9D9D9)), // gris claro como en tu imagen
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        NavBarItem(R.drawable.ic_profile, "Perfil", onProfileClick)

        Divider(
            modifier = Modifier
                .height(24.dp)
                .width(1.dp),
            color = Color.Black
        )

        NavBarItem(R.drawable.ic_home, "Inicio", onHomeClick)

        Divider(
            modifier = Modifier
                .height(24.dp)
                .width(1.dp),
            color = Color.Black
        )

        NavBarItem(R.drawable.ic_notifications, "Notificaciones", onNotificationsClick)

        Divider(
            modifier = Modifier
                .height(24.dp)
                .width(1.dp),
            color = Color.Black
        )

        NavBarItem(R.drawable.ic_settings, "Ajustes", onSettingsClick)
    }
}
