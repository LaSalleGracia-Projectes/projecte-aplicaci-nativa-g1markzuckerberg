package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import kotlinx.coroutines.launch

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

    // Initialize pager with 2 pages, starting on current selectedTab
    val selectedTab by userDraftViewModel.selectedTab.observeAsState(initial = Tab.USER)
    val pagerState = rememberPagerState(
        initialPage = if (selectedTab == Tab.USER) 0 else 1,
        pageCount   = { 2 }
    )
    val scope = rememberCoroutineScope()

    // Sync pager -> viewModel when page changes
    LaunchedEffect(pagerState.currentPage) {
        userDraftViewModel.setSelectedTab(
            if (pagerState.currentPage == 0) Tab.USER else Tab.DRAFT
        )
    }

    // Fetch user info once
    LaunchedEffect(leagueId, userId) {
        userDraftViewModel.fetchUserInfo(leagueId, userId)
    }
    val leagueUserResponse by userDraftViewModel.leagueUserResponse.observeAsState()

    // Dropdown & dialogs state
    var dropDownExpanded by remember { mutableStateOf(false) }
    var anchorOffset by remember { mutableStateOf(Offset.Zero) }
    var anchorSize   by remember { mutableStateOf(IntSize.Zero) }

    var showConfirmationDialog by remember { mutableStateOf(false) }
    var confirmationAction     by remember { mutableStateOf("") }
    var resultDialogData       by remember { mutableStateOf<ResultDialogData?>(null) }

    val density = LocalDensity.current

    Box(Modifier.fillMaxSize()) {
        // HEADER
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
            Column(Modifier.fillMaxSize()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalAlignment   = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector   = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint          = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text(
                        text      = userName,
                        style     = MaterialTheme.typography.titleLarge.copy(
                            fontSize   = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.3.sp
                        ),
                        color     = MaterialTheme.colorScheme.onPrimary,
                        modifier  = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    UserImage(
                        url      = decodedUserPhotoUrl,
                        modifier = Modifier
                            .size(45.dp)
                            .clip(CircleShape)
                    )
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    TabButton(
                        text       = "Usuario",
                        isSelected = pagerState.currentPage == 0,
                        onClick    = { scope.launch { pagerState.animateScrollToPage(0) } }
                    )
                    TabButton(
                        text       = "Draft",
                        isSelected = pagerState.currentPage == 1,
                        onClick    = { scope.launch { pagerState.animateScrollToPage(1) } }
                    )
                }
            }
        }

        // PAGER con swipe horizontal
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 130.dp, start = 16.dp, end = 16.dp)
                .align(Alignment.TopStart)
        ) { page ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                if (page == 0) {
                    // === PÁGINA USUARIO ===
                    if (leagueUserResponse != null) {
                        item {
                            Box(Modifier.wrapContentSize()) {
                                TrainerCard(
                                    imageUrl     = RetrofitClient.BASE_URL.trimEnd('/') + leagueUserResponse!!.user.imageUrl,
                                    name         = leagueUserResponse!!.user.username,
                                    birthDate    = leagueUserResponse!!.user.birthDate,
                                    isCaptain    = leagueUserResponse!!.user.is_capitan,
                                    puntosTotales= leagueUserResponse!!.user.puntos_totales,
                                    onInfoClick  = { dropDownExpanded = true }
                                )
                                // Anchor para el dropdown
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(40.dp)
                                        .zIndex(1f)
                                        .clickable { dropDownExpanded = true }
                                        .onGloballyPositioned { coords ->
                                            anchorOffset = coords.localToRoot(Offset.Zero)
                                            anchorSize   = coords.size
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
                    // === PÁGINA DRAFT ===
                    item {
                        Text("Contenido de Draft (pendiente)")
                    }
                }
            }
        }

        // DropdownMenu anclado
        DropdownMenu(
            expanded = dropDownExpanded,
            onDismissRequest = { dropDownExpanded = false },
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
            DropdownMenuItem(
                modifier = Modifier.background(
                    Brush.horizontalGradient(listOf(Color(0xFFFF5252), Color(0xFFB71C1C)))
                ),
                text = { Text("Expulsar", color = Color.White) },
                onClick = {
                    dropDownExpanded = false
                    confirmationAction = "expulsar"
                    showConfirmationDialog = true
                }
            )
            DropdownMenuItem(
                text = { Text("Hacer Capitán") },
                onClick = {
                    dropDownExpanded = false
                    confirmationAction = "captain"
                    showConfirmationDialog = true
                }
            )
        }

        // Diálogos de confirmación e info (igual que antes)
        if (showConfirmationDialog) {
            val title   = if (confirmationAction == "expulsar") "Confirmar Expulsión" else "Confirmar Cambio de Capitán"
            val message = if (confirmationAction == "expulsar")
                "¿Estás seguro que deseas expulsar a este usuario?"
            else
                "¿Estás seguro que deseas hacer capitán a este usuario?"

            CustomAlertDialog(
                title   = title,
                message = message,
                onDismiss = { showConfirmationDialog = false },
                onConfirm = {
                    showConfirmationDialog = false
                    when (confirmationAction) {
                        "expulsar" -> userDraftViewModel.kickUser(leagueId, userId) { success, msg ->
                            resultDialogData = ResultDialogData(if (success) "Éxito" else "Error", msg)
                        }
                        "captain"  -> userDraftViewModel.makeCaptain(leagueId, userId) { success, msg ->
                            resultDialogData = ResultDialogData(if (success) "Éxito" else "Error", msg)
                        }
                    }
                }
            )
        }
        resultDialogData?.let { data ->
            CustomAlertDialogSingleButton(
                title = data.title,
                message = data.message,
                onAccept = { resultDialogData = null }
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


