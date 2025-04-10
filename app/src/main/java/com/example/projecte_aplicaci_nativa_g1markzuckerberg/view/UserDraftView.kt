package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.nav.Routes
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.NavbarView
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.Tab
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.UserDraftViewModel
import androidx.compose.ui.text.font.FontWeight
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.UserImage

@Composable
fun UserDraftView(
    navController: NavController,
    userDraftViewModel: UserDraftViewModel,
    userId: String,
    userName: String,
    userPhotoUrl: String
) {
    // Decodifica la URL recibida
    val decodedUserPhotoUrl = Uri.decode(userPhotoUrl)

    // Observa la pestaña seleccionada, etc.
    val selectedTab by userDraftViewModel.selectedTab.observeAsState(initial = Tab.USER)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // HEADER con switch y foto del usuario
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Fila superior: botón, nombre del usuario y foto
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.3.sp
                            ),
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        // Aquí usamos nuestro composable UserImage en lugar de AsyncImage
                        UserImage(
                            url = decodedUserPhotoUrl,
                            modifier = Modifier
                                .size(45.dp)
                                .clip(CircleShape)
                        )
                    }
                    // Menú switch para cambiar pestañas
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        TabButton(
                            text = "Usuario",
                            isSelected = selectedTab == Tab.USER,
                            onClick = { userDraftViewModel.setSelectedTab(Tab.USER) }
                        )
                        TabButton(
                            text = "Draft",
                            isSelected = selectedTab == Tab.DRAFT,
                            onClick = { userDraftViewModel.setSelectedTab(Tab.DRAFT) }
                        )
                    }
                }
            }
            // CONTENIDO PRINCIPAL (según pestaña)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (selectedTab == Tab.USER) {
                    Text("Contenido de Usuario (pendiente)")
                } else {
                    Text("Contenido de Draft (pendiente)")
                }
            }
        }
        // Navbar inferior
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            NavbarView(
                navController = navController,
                onProfileClick = { /* Acción perfil */ },
                onHomeClick = { navController.navigate(Routes.HomeLoged.route) },
                onNotificationsClick = { /* Acción notificaciones */ },
                onSettingsClick = { navController.navigate(Routes.Settings.route) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


// Como antes, definimos TabButton como extensión de RowScope para usar weight()
@Composable
fun androidx.compose.foundation.layout.RowScope.TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(MaterialTheme.colorScheme.onPrimary)
            )
        } else {
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}
