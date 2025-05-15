package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.OverlayLoading
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.grafanaUserUrl
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.Tab
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.UserDraftViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import androidx.compose.ui.unit.Velocity
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.nav.Routes
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.LocalAppDarkTheme
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.FancyLoadingAnimation
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeParseException

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserDraftView(
    navController: NavController,
    userDraftViewModel: UserDraftViewModel,
    leagueId: String,
    userId: String,
    userName: String,
    userPhotoUrl: String,
    createdJornada: Int,
    currentJornada: Int,
    currentEndingAt: String? = null // <-- NUEVO (la fecha de fin de jornada actual, formato ISO)
) {
    val decodedUserPhotoUrl = Uri.decode(userPhotoUrl)

    // --- NUEVO: calculo de si ya se pasó la fecha de fin de la jornada actual ---
    val hemosPasadoFin = remember(currentEndingAt) {
        currentEndingAt?.let { endingAt ->
            try {
                if (endingAt.length == 10) {
                    // solo fecha yyyy-MM-dd
                    val fechaFin = LocalDate.parse(endingAt)
                    val hoy = LocalDate.now()
                    hoy.isAfter(fechaFin) || hoy.isEqual(fechaFin)
                } else {
                    // fecha y hora ISO-8601
                    val fechaFin = OffsetDateTime.parse(endingAt)
                    val ahora = OffsetDateTime.now(ZoneId.systemDefault())
                    ahora.isAfter(fechaFin) || ahora.isEqual(fechaFin)
                }
            } catch (e: Exception) {
                false
            }
        } ?: false
    }

    // Ahora mostramos la jornada actual + 1 SOLO si hemos pasado la fecha de fin
    val lastJornadaToShow = remember(currentJornada, hemosPasadoFin) {
        if (hemosPasadoFin) currentJornada + 1 else currentJornada
    }

    // La lista de jornadas disponibles
    val jornadas = remember(createdJornada, currentJornada) {
        val maxJornada = currentJornada + 1      // añadimos la siguiente
        (createdJornada..maxJornada).toList()
    }

    // --- selectedJornada empieza por defecto en la última visible ---
    var selectedJornada by remember { mutableIntStateOf(lastJornadaToShow) }

    // 1) Pager USER / DRAFT
    val selectedTab by userDraftViewModel.selectedTab.observeAsState(initial = Tab.USER)
    val pagerState = rememberPagerState(
        initialPage = if (selectedTab == Tab.USER) 0 else 1,
        pageCount = { 2 }
    )
    val scope = rememberCoroutineScope()

    // 2) Traer info de usuario
    LaunchedEffect(leagueId, userId) {
        userDraftViewModel.fetchUserInfo(leagueId, userId)
    }
    val leagueUserResponse by userDraftViewModel.leagueUserResponse.observeAsState()

    // 3) Dropdowns y diálogos
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var confirmationAction by remember { mutableStateOf("") }
    var resultDialogData by remember { mutableStateOf<ResultDialogData?>(null) }

    // variables reactivas
    val draftPlayers by userDraftViewModel.draftPlayers.observeAsState(emptyList())
    val draftFormation by userDraftViewModel.draftFormation.observeAsState("4-3-3")
    val isLoadingDraft by userDraftViewModel.isLoadingDraft.observeAsState(false)
    var boxCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }

    // --- DESCARGAR DRAFT AL CAMBIAR JORNADA ---
    LaunchedEffect(selectedJornada) {
        userDraftViewModel.fetchUserDraft(leagueId, userId, selectedJornada)
    }
    LaunchedEffect(pagerState.currentPage) {
        val nuevaPestanya = if (pagerState.currentPage == 0) Tab.USER else Tab.DRAFT
        userDraftViewModel.setSelectedTab(nuevaPestanya)
        if (nuevaPestanya == Tab.DRAFT) {
            userDraftViewModel.fetchUserDraft(
                leagueId = leagueId,
                userId = userId,
                roundName = selectedJornada
            )
        }
    }

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
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(Modifier.fillMaxSize()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    val ctx = LocalContext.current
                    val token = RetrofitClient.authRepository.getToken().orEmpty()
                    val avatarRequest = ImageRequest.Builder(ctx)
                        .data(decodedUserPhotoUrl)
                        .addHeader("Authorization", "Bearer $token")
                        .placeholder(R.drawable.fantasydraft)
                        .error(R.drawable.fantasydraft)
                        .crossfade(true)
                        .build()
                    AsyncImage(
                        model = avatarRequest,
                        contentDescription = "User avatar",
                        modifier = Modifier
                            .size(45.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            .clickable { },
                        contentScale = ContentScale.Crop
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
            }
        }

        // PAGER
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 130.dp)
        ) { page ->
            if (page == 0) {
                val isDarkApp = LocalAppDarkTheme.current
                val baseGrafanaUrl = remember(leagueId, userId) {
                    grafanaUserUrl(leagueId, userId).substringBefore("?")
                }
                val grafanaUrl = remember(baseGrafanaUrl, isDarkApp) {
                    "$baseGrafanaUrl?theme=${if (isDarkApp) "dark" else "light"}"
                }
                val imageScroll = rememberScrollState()
                val grafanaConn = remember(imageScroll) {
                    object : NestedScrollConnection {
                        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                            val canScrollForward = available.x < 0 && imageScroll.value < imageScroll.maxValue
                            val canScrollBackward = available.x > 0 && imageScroll.value > 0
                            return if (canScrollForward || canScrollBackward) {
                                Offset.Zero
                            } else {
                                Offset(available.x, 0f)
                            }
                        }
                        override suspend fun onPreFling(available: Velocity): Velocity {
                            val canFlingForward = available.x < 0 && imageScroll.value < imageScroll.maxValue
                            val canFlingBackward = available.x > 0 && imageScroll.value > 0
                            return if (canFlingForward || canFlingBackward) {
                                Velocity.Zero
                            } else {
                                Velocity(available.x, 0f)
                            }
                        }
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 56.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(18.dp))
                        SectionHeader(title = stringResource(R.string.user_section))
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                    }
                    item {
                        leagueUserResponse?.let { resp ->
                            Box(
                                modifier = Modifier
                                    .wrapContentSize(align = Alignment.TopEnd)
                                    .onGloballyPositioned { boxCoords = it },
                                contentAlignment = Alignment.TopEnd
                            ) {
                                TrainerCard(
                                    imageUrl = RetrofitClient.BASE_URL.trimEnd('/') + resp.user.imageUrl,
                                    name = resp.user.username,
                                    birthDate = resp.user.birthDate,
                                    isCaptain = resp.user.is_capitan,
                                    puntosTotales = resp.user.puntos_totales,
                                    onExpelClick = {
                                        confirmationAction = "expulsar"
                                        showConfirmationDialog = true
                                    },
                                    onCaptainClick = {
                                        confirmationAction = "captain"
                                        showConfirmationDialog = true
                                    }
                                )
                            }
                        } ?: Text(stringResource(R.string.loading_data))
                    }
                    item {
                        Spacer(modifier = Modifier.height(18.dp))
                        SectionHeader(title = stringResource(R.string.historic_section))
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                    }
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            val isTablet = LocalConfiguration.current.smallestScreenWidthDp >= 600
                            Box(
                                modifier = if (isTablet)
                                    Modifier.fillMaxWidth()
                                else
                                    Modifier
                                        .fillMaxWidth()
                                        .height(220.dp)
                                        .horizontalScroll(imageScroll)
                                        .nestedScroll(grafanaConn),
                            ) {
                                var loading by remember { mutableStateOf(true) }
                                SubcomposeAsyncImage(
                                    model = grafanaUrl,
                                    contentDescription = stringResource(R.string.performance_chart_desc),
                                    modifier = if (isTablet)
                                        Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(16f / 9f)
                                            .clip(MaterialTheme.shapes.medium)
                                    else
                                        Modifier
                                            .fillMaxHeight()
                                            .clip(MaterialTheme.shapes.medium),
                                    contentScale = if (isTablet) ContentScale.FillWidth
                                    else ContentScale.FillHeight,
                                ) {
                                    val painter = painter
                                    when (painter.state) {
                                        is AsyncImagePainter.State.Success -> {
                                            loading = false
                                            SubcomposeAsyncImageContent()
                                        }
                                        is AsyncImagePainter.State.Error -> {
                                            loading = false
                                            // opcional: un placeholder o mensaje de error
                                        }
                                        else -> {
                                            SubcomposeAsyncImageContent()
                                        }
                                    }
                                }
                                if (loading) {
                                    Box(
                                        Modifier.matchParentSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        FancyLoadingAnimation(modifier = Modifier.size(120.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            } else { // ----------- PÁGINA DRAFT -----------
                OverlayLoading(isLoading = isLoadingDraft) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            val jornadaPoints = draftPlayers.sumOf { it.puntos_jornada.toDouble().roundToInt() }
                            // --- LazyRow de jornadas ---
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
                                                color = MaterialTheme.colorScheme.onSecondary
                                            )
                                            if (j == selectedJornada) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = jornadaPoints.toString(),
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.onSecondary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clipToBounds()
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.futbol_pitch_background),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer { scaleX = 1.25f },
                                    contentScale = ContentScale.FillBounds
                                )
                                if (draftPlayers.isEmpty() && !isLoadingDraft) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                                        shape = MaterialTheme.shapes.medium,
                                        tonalElevation = 6.dp,
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .padding(horizontal = 24.dp)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.no_draft, userName),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                                if (draftPlayers.isNotEmpty()) {
                                    ReadonlyDraftLayout(
                                        navController = navController,
                                        formation = draftFormation,
                                        players = draftPlayers
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- DROPDOWN / DIÁLOGOS ---
        if (showConfirmationDialog) {
            val titleText = if (confirmationAction == "expulsar")
                stringResource(R.string.confirm_expel_title)
            else
                stringResource(R.string.confirm_captain_title)

            val msgText = if (confirmationAction == "expulsar")
                stringResource(R.string.confirm_expel_msg)
            else
                stringResource(R.string.confirm_captain_msg)

            val successText = stringResource(R.string.success)
            val errorText = stringResource(R.string.error)

            CustomAlertDialog(
                title = titleText,
                message = msgText,
                onDismiss = { showConfirmationDialog = false },
                onConfirm = {
                    showConfirmationDialog = false
                    if (confirmationAction == "expulsar") {
                        userDraftViewModel.kickUser(leagueId, userId) { ok, m ->
                            resultDialogData = ResultDialogData(
                                title = if (ok) successText else errorText,
                                message = m
                            )
                        }
                    } else {
                        userDraftViewModel.makeCaptain(leagueId, userId) { ok, m ->
                            resultDialogData = ResultDialogData(
                                title = if (ok) successText else errorText,
                                message = m
                            )
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
    navController: NavController,

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
                                onClick = {
                                    navController.navigate(
                                        Routes.PlayerDetail.createRoute(p.id.toString())
                                    )
                                }
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
    val tabTitles = listOf(stringResource(R.string.user_tab), stringResource(R.string.draft_tab))

    BoxWithConstraints {
        val fullWidth = constraints.maxWidth.toFloat()
        val tabWidth  = fullWidth / tabTitles.size
        // Offset en px: página actual + fracción de desplazamiento
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
                            text = title,
                            color = if (pagerState.currentPage == index)
                                MaterialTheme.colorScheme.onSecondary
                            else
                                MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
            // Indicador
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
                        .background(MaterialTheme.colorScheme.onSecondary)
                )
            }
        }
    }
}
