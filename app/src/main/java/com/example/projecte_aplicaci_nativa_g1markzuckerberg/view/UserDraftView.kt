package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.Player
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.ResultDialogData
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.CustomAlertDialog
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.CustomAlertDialogSingleButton
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.TrainerCard
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.UserImage
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.grafanaUserUrl
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.Tab
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.UserDraftViewModel
import kotlinx.coroutines.launch

@Composable
fun UserDraftView(
    navController: NavController,
    userDraftViewModel: UserDraftViewModel,
    leagueId: String,
    userId: String,
    userName: String,
    userPhotoUrl: String,
    createdJornada: Int,
    currentJornada: Int
) {
    val decodedUserPhotoUrl = Uri.decode(userPhotoUrl)

    // 1) Pager USER / DRAFT
    val selectedTab by userDraftViewModel.selectedTab.observeAsState(initial = Tab.USER)
    val pagerState = rememberPagerState(
        initialPage = if (selectedTab == Tab.USER) 0 else 1,
        pageCount   = { 2 }
    )
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        userDraftViewModel.setSelectedTab(
            if (pagerState.currentPage == 0) Tab.USER else Tab.DRAFT
        )
    }

    // 2) Traer info de usuario
    LaunchedEffect(leagueId, userId) {
        userDraftViewModel.fetchUserInfo(leagueId, userId)
    }
    val leagueUserResponse by userDraftViewModel.leagueUserResponse.observeAsState()

    // 3) Dropdowns y diálogos (igual que antes)
    var dropDownExpanded by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var confirmationAction by remember { mutableStateOf("") }
    var resultDialogData by remember { mutableStateOf<ResultDialogData?>(null) }
// variables reactivas
    val draftPlayers   by userDraftViewModel.draftPlayers.observeAsState(emptyList())
    val draftFormation by userDraftViewModel.draftFormation.observeAsState("4-3-3")
    val jornadas = remember(createdJornada, currentJornada) {
        (createdJornada..currentJornada).toList()
    }
    var selectedJornada by remember { mutableIntStateOf(currentJornada) }
// cuando cambia la jornada seleccionada → descargar plantilla
    LaunchedEffect(selectedJornada) {
        userDraftViewModel.fetchUserDraft(leagueId, userId, selectedJornada)
    }

    Box(Modifier.fillMaxSize()) {
        // ─── HEADER ─────────────────────────────────────────
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
            Column(Modifier.fillMaxSize()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalAlignment    = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
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
                            fontWeight = FontWeight.ExtraBold
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
                    TabButton("Usuario", pagerState.currentPage == 0) {
                        scope.launch { pagerState.animateScrollToPage(0) }
                    }
                    TabButton("Draft",   pagerState.currentPage == 1) {
                        scope.launch { pagerState.animateScrollToPage(1) }
                    }
                }
            }
        }

        // ─── PAGER ──────────────────────────────────────────
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 130.dp)
        ) { page ->
            if (page == 0) {
                // === USUARIO ===
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 56.dp)
                ) {
                    item {
                        leagueUserResponse?.let { resp ->
                            Box(Modifier.wrapContentSize()) {
                                TrainerCard(
                                    imageUrl = RetrofitClient.BASE_URL.trimEnd('/') + resp.user.imageUrl,
                                    name = resp.user.username,
                                    birthDate = resp.user.birthDate,
                                    isCaptain = resp.user.is_capitan,
                                    puntosTotales = resp.user.puntos_totales,
                                    onInfoClick = { dropDownExpanded = true }
                                )
                            }
                        } ?: Text("Cargando datos…")
                    }
                    item {
                        val graphUrl = remember(leagueId, userId) {
                            grafanaUserUrl(leagueId, userId)
                        }

                        // 220 dp alto; 16 dp margen superior‑inferior
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                                .horizontalScroll(rememberScrollState()),  // ← scroll lateral
                            horizontalArrangement = Arrangement.Start
                        ) {
                            AsyncImage(                       // coil‑compose
                                model = graphUrl,
                                contentDescription = "Gráfico de rendimiento",
                                contentScale = ContentScale.FillHeight,
                                modifier = Modifier
                                    .height(220.dp)           // alto fijo
                                    .clip(MaterialTheme.shapes.medium)
                            )
                        }
                    }
                }

            } else { /* ---------- PÁGINA DRAFT ---------- */

                Column(modifier = Modifier.fillMaxSize()) {

                    /* 1️⃣  LazyRow — SIEMPRE visible arriba */
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentPadding        = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(jornadas) { j ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (j == selectedJornada)
                                            MaterialTheme.colorScheme.secondary
                                        else
                                            MaterialTheme.colorScheme.primary
                                    )
                                    .clickable {
                                        selectedJornada = j
                                        // TODO: userDraftViewModel.fetchUserDraft(...)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "$j",
                                    fontSize   = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color      = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }

                    /* 2️⃣  Lo que queda de alto: campo + contenido */
                    Box(
                        modifier = Modifier
                            .weight(1f)   // ocupa TODO tras la LazyRow
                            .fillMaxWidth()
                    ) {
                        Image(
                            painter           = painterResource(R.drawable.futbol_pitch_background),
                            contentDescription = null,
                            modifier          = Modifier
                                .fillMaxSize()
                                .graphicsLayer { scaleX = 1.25f } // opcional, como en DraftScreen
                                .clipToBounds(),
                            contentScale      = ContentScale.FillBounds
                        )

                        ReadonlyDraftLayout(
                            formation = draftFormation,
                            players   = draftPlayers
                        )
                    }
                }
            }
        }

            // ─── DROPDOWN / DIÁLOGOS ───────────────────────────
        DropdownMenu(
            expanded = dropDownExpanded,
            onDismissRequest = { dropDownExpanded = false }
        ) {
            DropdownMenuItem(
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

        if (showConfirmationDialog) {
            val title = if (confirmationAction == "expulsar") "Confirmar expulsión" else "Confirmar capitán"
            val msg   = if (confirmationAction == "expulsar")
                "¿Seguro que deseas expulsar a este usuario?"
            else
                "¿Seguro que deseas hacer capitán a este usuario?"

            CustomAlertDialog(
                title   = title,
                message = msg,
                onDismiss = { showConfirmationDialog = false },
                onConfirm = {
                    showConfirmationDialog = false
                    if (confirmationAction == "expulsar")
                        userDraftViewModel.kickUser(leagueId, userId) { ok, m ->
                            resultDialogData = ResultDialogData(if (ok) "Éxito" else "Error", m)
                        }
                    else
                        userDraftViewModel.makeCaptain(leagueId, userId) { ok, m ->
                            resultDialogData = ResultDialogData(if (ok) "Éxito" else "Error", m)
                        }
                }
            )
        }

        resultDialogData?.let { data ->
            CustomAlertDialogSingleButton(
                title    = data.title,
                message  = data.message,
                onAccept = { resultDialogData = null }
            )
        }
    }
}

@Composable
private fun RowScope.TabButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = text, color = MaterialTheme.colorScheme.onPrimary)
        Spacer(Modifier.height(4.dp))
        if (isSelected) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(MaterialTheme.colorScheme.onPrimary)
            )
        } else {
            Spacer(Modifier.height(2.dp))
        }
    }
}

@Composable
private fun ReadonlyDraftLayout(
    formation: String,
    players  : List<Player>,
) {
    val byPos = remember(players) {
        players.groupBy { it.positionId }   // 24‑27 según ejemplo
    }

    val rows: List<Pair<Int, Int>> = when (formation) {
        "4-3-3" -> listOf(27 to 3, 26 to 3, 25 to 4, 24 to 1)
        "4-4-2" -> listOf(27 to 2, 26 to 4, 25 to 4, 24 to 1)
        "3-4-3" -> listOf(27 to 3, 26 to 4, 25 to 3, 24 to 1)
        else    -> emptyList()
    }


    val (cardW, cardH) = getPlayerCardDimensions()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        rows.forEach { (posId, count) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                repeat(count) { idx ->
                    // tomamos el idx‑ésimo jugador de ese bloque
                    val p = byPos[posId]?.getOrNull(idx)
                    Box(
                        Modifier
                            .width(cardW)
                            .height(cardH),
                        contentAlignment = Alignment.Center
                    ) {
                        if (p != null) {
                            CompactPlayerCard(
                                player = p.toPlayerOption(),   // extensión abajo
                                width  = cardW,
                                height = cardH,
                                onClick = {}                   // 🔒 NO hace nada
                            )
                        }
                    }
                }
            }
        }
    }
}

/* helper para reusar CompactPlayerCard con tu modelo Player → PlayerOption */
private fun Player.toPlayerOption() = com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.PlayerOption(
    id             = id.toInt(),
    displayName    = displayName,
    positionId     = positionId,
    imagePath      = imagePath,
    estrellas      = estrellas,
    puntos_totales = puntos_totales.toInt()
)

