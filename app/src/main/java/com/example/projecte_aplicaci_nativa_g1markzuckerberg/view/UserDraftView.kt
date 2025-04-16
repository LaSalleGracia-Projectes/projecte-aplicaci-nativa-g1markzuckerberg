package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.nav.Routes
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.NavbarView
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.Tab
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.UserDraftViewModel
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.zIndex
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.ResultDialogData
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.CustomAlertDialog
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.CustomAlertDialogSingleButton
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.TrainerCard
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.UserImage

// Define la data class fuera de la función composable

@Composable
fun UserDraftView(
    navController: NavController,
    userDraftViewModel: UserDraftViewModel,
    leagueId: String,
    userId: String,
    userName: String,
    userPhotoUrl: String
) {
    val decodedUserPhotoUrl = Uri.decode(userPhotoUrl)

    LaunchedEffect(key1 = leagueId, key2 = userId) {
        userDraftViewModel.fetchUserInfo(leagueId, userId)
    }

    val leagueUserResponse by userDraftViewModel.leagueUserResponse.observeAsState()
    val selectedTab by userDraftViewModel.selectedTab.observeAsState(initial = Tab.USER)

    // Estados para el DropdownMenu y para posicionar el ancla
    var dropDownExpanded by remember { mutableStateOf(false) }
    var anchorOffset by remember { mutableStateOf(Offset.Zero) }
    var anchorSize by remember { mutableStateOf(IntSize.Zero) }

    // Estados para el diálogo de confirmación (dos botones)
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var confirmationAction by remember { mutableStateOf("") }

    // Estado para el diálogo informativo (una acción)
    var resultDialogData by remember { mutableStateOf<ResultDialogData?>(null) }

    val density = LocalDensity.current

    Box(modifier = Modifier.fillMaxSize()) {
        // HEADER (sin cambios)
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
                .padding(horizontal = 20.dp)
                .align(Alignment.TopStart),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
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
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                    UserImage(
                        url = decodedUserPhotoUrl,
                        modifier = Modifier
                            .size(45.dp)
                            .clip(CircleShape)
                    )
                }
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

        // BODY CON LAZY COLUMN
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 130.dp, start = 16.dp, end = 16.dp)
                .align(Alignment.TopStart),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            if (selectedTab == Tab.USER) {
                if (leagueUserResponse != null) {
                    item {
                        // Usamos un Box para agrupar la TrainerCard con un "anchor"
                        Box(modifier = Modifier.wrapContentSize()) {
                            // La tarjeta de usuario
                            TrainerCard(
                                imageUrl = "${RetrofitClient.BASE_URL.trimEnd('/')}${leagueUserResponse!!.user.imageUrl}",
                                name = leagueUserResponse!!.user.username,
                                birthDate = leagueUserResponse!!.user.birthDate,
                                isCaptain = leagueUserResponse!!.user.is_capitan,
                                puntosTotales = leagueUserResponse!!.user.puntos_totales,
                                onInfoClick = { dropDownExpanded = true }
                            )
                            // El "anchor" invisible en la esquina superior derecha,
                            // que servirá para posicionar el DropdownMenu
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(40.dp)
                                    .zIndex(1f) // Asegura que esté encima
                                    .clickable { dropDownExpanded = true }
                                    .onGloballyPositioned { coordinates ->
                                        // Se guarda la posición y tamaño del botón
                                        anchorOffset = coordinates.localToRoot(Offset.Zero)
                                        anchorSize = coordinates.size
                                    }
                            )
                        }
                    }
                } else {
                    item {
                        Text("Cargando datos del usuario...")
                    }
                }
            } else {
                item { Text("Contenido de Draft (pendiente)") }
            }
        }

        // DropdownMenu anclado justo debajo del botón
        DropdownMenu(
            expanded = dropDownExpanded,
            onDismissRequest = { dropDownExpanded = false },
            // Calculamos el offset para que se abra justo debajo del anchor
            offset = with(density) {
                DpOffset(
                    x = anchorOffset.x.toDp(),
                    y = (anchorOffset.y + anchorSize.height).toDp()
                )
            },
            modifier = Modifier
                .wrapContentSize()
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
        ) {
            // Ítem "Expulsar" con fondo gradiente rojo
            DropdownMenuItem(
                modifier = Modifier.background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFFFF5252), Color(0xFFB71C1C))
                    )
                ),
                text = { Text("Expulsar", color = Color.White) },
                onClick = {
                    dropDownExpanded = false
                    confirmationAction = "expulsar"
                    showConfirmationDialog = true
                },
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
            )
            // Ítem "Hacer Capitán"
            DropdownMenuItem(
                text = { Text("Hacer Capitán") },
                onClick = {
                    dropDownExpanded = false
                    confirmationAction = "captain"
                    showConfirmationDialog = true
                },
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        // Diálogo de confirmación (con dos botones) usando CustomAlertDialog
        if (showConfirmationDialog) {
            val title = if (confirmationAction == "expulsar")
                "Confirmar Expulsión" else "Confirmar Cambio de Capitán"
            val message = if (confirmationAction == "expulsar")
                "¿Estás seguro que deseas expulsar a este usuario?"
            else "¿Estás seguro que deseas hacer capitán a este usuario?"

            CustomAlertDialog(
                title = title,
                message = message,
                onDismiss = { showConfirmationDialog = false },
                onConfirm = {
                    showConfirmationDialog = false
                    when (confirmationAction) {
                        "expulsar" -> {
                            userDraftViewModel.kickUser(leagueId, userId) { success, msg ->
                                resultDialogData = ResultDialogData(
                                    title = if (success) "Éxito" else "Error",
                                    message = msg
                                )
                            }
                        }
                        "captain" -> {
                            userDraftViewModel.makeCaptain(leagueId, userId) { success, msg ->
                                resultDialogData = ResultDialogData(
                                    title = if (success) "Éxito" else "Error",
                                    message = msg
                                )
                            }
                        }
                    }
                }
            )
        }

        // Diálogo informativo (con un solo botón) usando CustomAlertDialogSingleButton
        resultDialogData?.let { data ->
            CustomAlertDialogSingleButton(
                title = data.title,
                message = data.message,
                onAccept = { resultDialogData = null }
            )
        }

        // NAVBAR
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
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

@Composable
fun RowScope.TabButton(
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


