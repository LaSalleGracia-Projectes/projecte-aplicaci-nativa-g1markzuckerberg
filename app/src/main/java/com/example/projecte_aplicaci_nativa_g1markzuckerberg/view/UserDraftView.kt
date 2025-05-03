package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.OverlayLoading
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
import androidx.compose.foundation.pager.PagerState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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
import kotlin.math.roundToInt
import androidx.compose.ui.unit.Velocity
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.LocalAppDarkTheme
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.FancyLoadingAnimation

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

    // 2) Traer info de usuario
    LaunchedEffect(leagueId, userId) {
        userDraftViewModel.fetchUserInfo(leagueId, userId)
    }
    val leagueUserResponse by userDraftViewModel.leagueUserResponse.observeAsState()

    // 3) Dropdowns y diálogos (igual que antes)
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var confirmationAction by remember { mutableStateOf("") }
    var resultDialogData by remember { mutableStateOf<ResultDialogData?>(null) }
// variables reactivas
    val draftPlayers   by userDraftViewModel.draftPlayers.observeAsState(emptyList())
    val draftFormation by userDraftViewModel.draftFormation.observeAsState("4-3-3")
    val isLoadingDraft  by userDraftViewModel.isLoadingDraft.observeAsState(false)
    // 1 ─ estados NUEVOS junto a dropDownExpanded
    var boxCoords      by remember { mutableStateOf<LayoutCoordinates?>(null) }   // ← NUEVO

    val jornadas = remember(createdJornada, currentJornada) {
        (createdJornada..currentJornada).toList()
    }

    var selectedJornada by remember { mutableIntStateOf(currentJornada) }
// cuando cambia la jornada seleccionada → descargar plantilla
    LaunchedEffect(selectedJornada) {
        userDraftViewModel.fetchUserDraft(leagueId, userId, selectedJornada)
    }
    LaunchedEffect(pagerState.currentPage) {
        val nuevaPestanya = if (pagerState.currentPage == 0) Tab.USER else Tab.DRAFT
        userDraftViewModel.setSelectedTab(nuevaPestanya)

        // Si acabamos de ir a DRAFT, recargamos la plantilla de la jornada seleccionada
        if (nuevaPestanya == Tab.DRAFT) {
            userDraftViewModel.fetchUserDraft(
                leagueId = leagueId,
                userId   = userId,
                roundName = selectedJornada
            )
        }
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
                            tint          = Color.White,
                        )
                    }
                    Text(
                    text      = userName,
                    style     = MaterialTheme.typography.titleLarge,
                    color     = Color.White,
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
                UserDraftTabs(pagerState) { page ->
                    scope.launch { pagerState.animateScrollToPage(page) }
                }
            }
        }}

        // ─── PAGER ──────────────────────────────────────────
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 130.dp)
        ) { page ->
            if (page == 0) {
                val imageScroll = rememberScrollState()
                // === GRAFANA ===
                val grafanaConn = remember(imageScroll) {
                    object : NestedScrollConnection {
                        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                            val canScrollForward  = available.x < 0 && imageScroll.value < imageScroll.maxValue
                            val canScrollBackward = available.x > 0 && imageScroll.value > 0

                            return if (canScrollForward || canScrollBackward) {
                                Offset.Zero
                            } else {
                                Offset(available.x, 0f)
                            }
                        }
                        override suspend fun onPreFling(available: Velocity): Velocity {
                            val canFlingForward  = available.x < 0 && imageScroll.value < imageScroll.maxValue
                            val canFlingBackward = available.x > 0 && imageScroll.value > 0

                            return if (canFlingForward || canFlingBackward) {
                                Velocity.Zero
                            } else {
                                Velocity(available.x, 0f)
                            }
                        }
                    }
                }
                // === USUARIO ===
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 56.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(18.dp))
                        SectionHeader(title = "USUARIO", color = Color.White)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                    }
                    item {
                        leagueUserResponse?.let { resp ->
                            // 1) Este Box "envuelve" sólo al TrainerCard y al DropdownMenu
                            Box(
                                modifier = Modifier
                                    .wrapContentSize(align = Alignment.TopEnd)
                                    .onGloballyPositioned { boxCoords = it },
                                contentAlignment = Alignment.TopEnd
                            ) {
                                TrainerCard(
                                    imageUrl     = RetrofitClient.BASE_URL.trimEnd('/') + resp.user.imageUrl,
                                    name         = resp.user.username,
                                    birthDate    = resp.user.birthDate,
                                    isCaptain    = resp.user.is_capitan,
                                    puntosTotales= resp.user.puntos_totales,

                                    onExpelClick = {          // 🔴 “Expulsar”
                                        confirmationAction     = "expulsar"
                                        showConfirmationDialog = true
                                    },
                                    onCaptainClick = {        // 🟢 “Hacer Capitán”
                                        confirmationAction     = "captain"
                                        showConfirmationDialog = true
                                    }
                                )
                            }
                        } ?: Text("Cargando datos…")

                    }
                    item {
                        Spacer(modifier = Modifier.height(18.dp))
                        SectionHeader(title = "HISTÓRICO DE PUNTOS", color = Color.White)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                    }

                    item {// 1️⃣ detectamos el tema de la app
                        val appDark = LocalAppDarkTheme.current

                        // 2️⃣ limpiamos cualquier parámetro previo
                        val rawUrl  = grafanaUserUrl(leagueId, userId).substringBefore("?")
                        // 3️⃣ añadimos ?theme=dark solo si la app está en dark
                        val graphUrl = if (appDark) "$rawUrl?theme=dark" else rawUrl

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                                .nestedScroll(grafanaConn)
                                .horizontalScroll(imageScroll),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            SubcomposeAsyncImage(
                                model = graphUrl,
                                contentDescription = "Gráfico de rendimiento",
                                modifier = Modifier
                                    .height(220.dp)
                                    .clip(MaterialTheme.shapes.medium),
                                contentScale = ContentScale.FillHeight
                            ) {
                                when (painter.state) {
                                    is AsyncImagePainter.State.Loading ->
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            FancyLoadingAnimation(Modifier.size(120.dp))
                                        }

                                    else ->
                                        SubcomposeAsyncImageContent()
                                }
                            }
                        }
                    }
                }

            } else { /* ---------- PÁGINA DRAFT ---------- */
                OverlayLoading(isLoading = isLoadingDraft) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(modifier = Modifier.fillMaxSize()) {

                        val jornadaPoints =
                                draftPlayers.sumOf { it.puntos_jornada.toDouble().roundToInt() }


                            /* 1️⃣  LazyRow — SIEMPRE visible arriba */
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(jornadas) { j ->
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (j == selectedJornada)
                                                    MaterialTheme.colorScheme.secondary
                                                else
                                                    MaterialTheme.colorScheme.primary
                                            )
                                            .clickable { selectedJornada = j },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "J$j",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            if (j == selectedJornada) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = jornadaPoints.toString(),
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            /* 2️⃣  Lo que queda de alto: campo + contenido */
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clipToBounds()
                            ) {
                                Image(
                                    painter           = painterResource(R.drawable.futbol_pitch_background),
                                    contentDescription = null,
                                    modifier          = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer { scaleX = 1.25f },
                                    contentScale      = ContentScale.FillBounds
                                )
                                if (draftPlayers.isEmpty() && !isLoadingDraft) {
                                    Surface(
                                        color  = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                                        shape  = MaterialTheme.shapes.medium,
                                        tonalElevation = 6.dp,
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .padding(horizontal = 24.dp)
                                    ) {
                                        Text(
                                            text      = "El usuario \"$userName\" no envió la convocatoria.",
                                            style     = MaterialTheme.typography.bodyLarge,
                                            color     = MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Center,
                                            modifier  = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                                // Solo si hay jugadores, dibujamos su plantilla
                                if (draftPlayers.isNotEmpty()) {
                                    ReadonlyDraftLayout(
                                        formation = draftFormation,
                                        players   = draftPlayers
                                    )
                                }
                            }
                        }
                    }
                }
        }
        }

            // ─── DROPDOWN / DIÁLOGOS ───────────────────────────

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
        players.groupBy { it.positionId }
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
                                player = p.toPlayerOption(),
                                width  = cardW,
                                height = cardH,
                                onClick = {}
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
    puntos_totales = puntos_jornada.toDouble().roundToInt()
)

@Composable
fun UserDraftTabs(
    pagerState: PagerState,
    onTabSelected: (page: Int) -> Unit
) {
    val tabTitles = listOf("Usuario", "Draft")
    // colores fijos para texto e indicador
    val selectedColor   = Color.White
    val unselectedColor = Color.White.copy(alpha = 0.6f)
    val indicatorColor  = Color.White

    BoxWithConstraints {
        val fullWidth = constraints.maxWidth.toFloat()
        val tabWidth  = fullWidth / tabTitles.size
        val indicatorOffsetPx by remember {
            derivedStateOf {
                (pagerState.currentPage + pagerState.currentPageOffsetFraction) * tabWidth
            }
        }

        Column {
            Row(Modifier.fillMaxWidth()) {
                tabTitles.forEachIndexed { index, title ->
                    Box(
                        Modifier
                            .weight(1f)
                            .clickable { onTabSelected(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text  = title,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (pagerState.currentPage == index)
                                selectedColor
                            else
                                unselectedColor
                        )
                    }
                }
            }
            // Indicador siempre blanco
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(2.dp)
            ) {
                Box(
                    Modifier
                        .offset { IntOffset(indicatorOffsetPx.roundToInt(), 0) }
                        .width(with(LocalDensity.current) { tabWidth.toDp() })
                        .fillMaxHeight()
                        .background(indicatorColor)
                )
            }
        }
    }
}

