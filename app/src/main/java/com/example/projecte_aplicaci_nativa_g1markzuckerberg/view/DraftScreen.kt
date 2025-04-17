package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.BeyondBoundsLayout
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.PlayerOption
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.TempPlantillaResponse
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.nav.Routes
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.DraftViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Función para obtener las claves (ej. "Delantero_0", "Delantero_1", …)
fun getPositionKeys(formation: String): List<String> {
    val formationRows = when (formation) {
        "4-3-3" -> listOf("Delantero" to 3, "Mediocentro" to 3, "Defensa" to 4, "Portero" to 1)
        "4-4-2" -> listOf("Delantero" to 2, "Mediocampista" to 4, "Defensa" to 4, "Portero" to 1)
        "3-4-3" -> listOf("Delantero" to 3, "Mediocampista" to 4, "Defensa" to 3, "Portero" to 1)
        else -> emptyList()
    }
    return formationRows.flatMap { (positionName, count) ->
        (0 until count).map { index -> "${positionName}_$index" }
    }
}

// Función para parsear el String JSON de playerOptions a una lista de listas
fun parsePlayerOptions(raw: String): List<List<Any?>> {
    return try {
        Gson().fromJson(raw, object : TypeToken<List<List<Any?>>>() {}.type)
    } catch (e: Exception) {
        Log.e("DraftScreen", "Error al parsear playerOptions: ${e.message}")
        emptyList()
    }
}

// Inicializa el mapa de jugadores seleccionados usando la versión parseada
fun initializeSelectedPlayersFromDraft(
    draft: TempPlantillaResponse,
    formation: String,
    parsedOptions: List<List<Any?>>
): MutableMap<String, PlayerOption?> {
    val selected = mutableMapOf<String, PlayerOption?>()
    val keys = getPositionKeys(formation)
    parsedOptions.forEachIndexed { index, group ->
        // Se asume que el quinto elemento es el índice seleccionado (puede ser null)
        val chosenIndex = if (group.size >= 5 && group[4] is Number) {
            (group[4] as Number).toInt()
        } else null
        val selectedPlayer = if (chosenIndex != null && chosenIndex in 0..3) {
            val item = group[chosenIndex]
            if (item is Map<*, *>) {
                Gson().fromJson(Gson().toJson(item), PlayerOption::class.java)
            } else if (item is PlayerOption) {
                item
            } else null
        } else null
        val key = keys.getOrElse(index) { "grupo_$index" }
        selected[key] = selectedPlayer
    }
    return selected
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun DraftScreen(
    viewModel: DraftViewModel,
    navController: NavController,
    innerPadding: PaddingValues,   // viene del Scaffold
) {
    /* -------- datos y estado (idéntico a tu versión) -------- */
    val tempDraftResponse by viewModel.tempDraft.observeAsState()

    val parsedPlayerOptions by remember(tempDraftResponse) {
        derivedStateOf { tempDraftResponse?.playerOptions?.let(::parsePlayerOptions) ?: emptyList() }
    }

    val playerOptionsForDisplay: List<List<PlayerOption>> = remember(parsedPlayerOptions) {
        parsedPlayerOptions.mapNotNull { group ->
            if (group.size >= 4) {
                group.take(4).mapNotNull { item ->
                    when (item) {
                        is Map<*, *>    -> Gson().fromJson(Gson().toJson(item), PlayerOption::class.java)
                        is PlayerOption -> item
                        else            -> null
                    }
                }
            } else null
        }
    }

    val selectedPlayers = remember { mutableStateMapOf<String, PlayerOption?>() }
    LaunchedEffect(tempDraftResponse, parsedPlayerOptions) {
        if (parsedPlayerOptions.isNotEmpty() && tempDraftResponse != null) {
            selectedPlayers.clear()
            selectedPlayers.putAll(
                initializeSelectedPlayersFromDraft(
                    tempDraftResponse!!,
                    viewModel.selectedFormation.value,
                    parsedPlayerOptions
                )
            )
        }
    }

    fun buildPositionOptions(): List<List<Any?>> {
        val keys = getPositionKeys(viewModel.selectedFormation.value)
        return parsedPlayerOptions.mapIndexed { index, group ->
            val key = keys.getOrElse(index) { "grupo_$index" }
            val players = group.take(4).mapNotNull { item ->
                when (item) {
                    is Map<*, *>    -> Gson().fromJson(Gson().toJson(item), PlayerOption::class.java)
                    is PlayerOption -> item
                    else            -> null
                }
            }
            val chosenIndex = selectedPlayers[key]?.let { sel ->
                players.indexOfFirst { it.id == sel.id }
            }
            listOf(players[0], players[1], players[2], players[3], chosenIndex)
        }
    }

    fun updateDraftOnServer() =
        viewModel.updateDraft(viewModel.currentLigaId, buildPositionOptions()) { }

    /* ----------  constantes UI ---------- */
    val headerHeight = 110.dp
    val buttonHeight = 34.dp
    val bottomBarPadding = innerPadding.calculateBottomPadding()

    /* ==========  LAY OUT  ========== */
    Box(Modifier.fillMaxSize()) {

        /* ---- FONDO del campo (solo entre header y bottom bar) ---- */
        Image(
            painter = painterResource(R.drawable.futbol_pitch_background),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,   // deforma para rellenar
            modifier = Modifier
                .fillMaxSize()
                .padding(                       // respeta header y nav bar
                    top    = headerHeight,
                )
                .graphicsLayer {                // “ensanchar” sin mover verticalmente
                    scaleX = 1.25f              // 25 % más ancho
                }
                .clipToBounds()                 // que no se salga del área
                .zIndex(-1f)
        )

        /* ---- CONTENIDO (header + cartas) ---- */
        Column(
            modifier = Modifier
                .fillMaxSize()
                // solo respetamos top/left/right del Scaffold,
                // NO el bottom, para que no quede franja blanca
                .padding(
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end   = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                    top   = innerPadding.calculateTopPadding()
                )
        ) {

            /* ----------  HEADER  ---------- */
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(headerHeight)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text(
                        "DRAFT",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.3.sp
                        ),
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    FilledTonalButton(
                        onClick = {
                            viewModel.saveDraft(selectedPlayers) {
                                navController.navigate(Routes.HomeLoged.route)
                            }
                        },
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.height(buttonHeight)
                    ) {
                        Text(
                            "Guardar",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp)
                        )
                    }
                }
            }

            /* ----------  CAMPO & CARTAS  ---------- */
            Box(
                modifier = Modifier
                    .weight(1f)     // ocupa todo el alto debajo del header
                    .fillMaxWidth()
            ) {
                DraftLayout(
                    formation   = viewModel.selectedFormation.value,
                    playerOptions       = playerOptionsForDisplay,
                    selectedPlayers     = selectedPlayers,
                    onPlayerSelectedWithUpdate = { key, player ->
                        selectedPlayers[key] = player
                        updateDraftOnServer()
                    },
                    updateDraftOnServer = { updateDraftOnServer() }
                )
            }
        }
    }
}




@Composable
fun getPlayerCardDimensions(): Pair<Dp, Dp> {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val availableWidth = screenWidth - 24.dp
    val cardWidth = (availableWidth - (3 * 8.dp)) / 4
    val cardHeight = cardWidth * 122f / 80f
    return Pair(cardWidth, cardHeight)
}

@SuppressLint("RememberReturnType")
@Composable
fun DraftLayout(
    formation: String,
    playerOptions: List<List<PlayerOption>>,
    selectedPlayers: MutableMap<String, PlayerOption?>,
    onPlayerSelectedWithUpdate: (key: String, chosenPlayer: PlayerOption) -> Unit,
    updateDraftOnServer: () -> Unit
) {
    val keys = getPositionKeys(formation)
    val formationRows = when (formation) {
        "4-3-3" -> listOf("Delantero" to 3, "Mediocentro" to 3, "Defensa" to 4, "Portero" to 1)
        "4-4-2" -> listOf("Delantero" to 2, "Mediocampista" to 4, "Defensa" to 4, "Portero" to 1)
        "3-4-3" -> listOf("Delantero" to 3, "Mediocampista" to 4, "Defensa" to 3, "Portero" to 1)
        else -> emptyList()
    }
    var groupIndex = 0
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        formationRows.forEach { (positionName, count) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(count) { index ->
                    val key = "${positionName}_$index"
                    val candidates = if (groupIndex < playerOptions.size) {
                        playerOptions[groupIndex]
                    } else {
                        emptyList()
                    }
                    Log.d("DraftLayout", "Posición: $key, grupo: $groupIndex, candidatos: ${candidates.size}")
                    PositionCard(
                        positionName = "$positionName ${index + 1}",
                        selectedPlayer = selectedPlayers[key],
                        candidates = candidates,
                        onPlayerSelected = { chosenPlayer ->
                            onPlayerSelectedWithUpdate(key, chosenPlayer)
                        },
                        updateDraftOnServer = { updateDraftOnServer() }
                    )
                    groupIndex++
                }
            }
        }
    }
}

@Composable
fun PositionCard(
    positionName: String,
    selectedPlayer: PlayerOption?,
    candidates: List<PlayerOption>,
    onPlayerSelected: (PlayerOption) -> Unit,
    updateDraftOnServer: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        PlayerSelectionDialog(
            players = candidates,
            onDismiss = { showDialog = false },
            onPlayerSelected = { selected ->
                onPlayerSelected(selected)
                showDialog = false
                updateDraftOnServer()
            }
        )
    }
    val (cardWidth, cardHeight) = getPlayerCardDimensions()
    Box(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight)
            .clickable { showDialog = true },
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = selectedPlayer,
            transitionSpec = {
                (slideInHorizontally(tween(300)) + fadeIn()) togetherWith
                        (slideOutHorizontally(tween(300)) + fadeOut())
            },
            label = "SwapPlayerAnimation"
        ) { player ->
            if (player == null) {
                Text(
                    text = positionName,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                    textAlign = TextAlign.Center
                )
            } else {
                CompactPlayerCard(
                    player = player,
                    width = cardWidth,
                    height = cardHeight,
                    onClick = { showDialog = true }
                )
            }
        }
    }
}

@Composable
fun PlayerSelectionDialog(
    players: List<PlayerOption?>,
    onDismiss: () -> Unit,
    onPlayerSelected: (PlayerOption) -> Unit
) {
    val validPlayers = players.filterNotNull()
    val blueGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0D47A1), Color(0xFF1976D2))
    )
    val (cardWidth, cardHeight) = getPlayerCardDimensions()
    Dialog(onDismissRequest = onDismiss) {
        AnimatedVisibility(
            visible = true,
            enter = slideInHorizontally(tween(300)) + fadeIn(tween(300)),
            exit = slideOutHorizontally(tween(300)) + fadeOut(tween(200))
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(blueGradient)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Elige un jugador",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    val rows = validPlayers.chunked(2)
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        rows.forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                row.forEach { player ->
                                    CompactPlayerCard(
                                        player = player,
                                        width = cardWidth,
                                        height = cardHeight,
                                        onClick = { onPlayerSelected(player) }
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StyledPlayerCard(
    player: PlayerOption,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cardWidth: Dp = 140.dp,
    cardHeight: Dp = 200.dp
) {
    val gradientBackground = when (player.estrellas) {
        1 -> Brush.verticalGradient(listOf(Color(0xFFB0B0B0), Color(0xFFE0E0E0)))
        2 -> Brush.verticalGradient(listOf(Color(0xFF4CAF50), Color(0xFFA5D6A7)))
        3 -> Brush.verticalGradient(listOf(Color(0xFF2196F3), Color(0xFF90CAF9)))
        4 -> Brush.verticalGradient(listOf(Color(0xFF9C27B0), Color(0xFFE1BEE7)))
        5 -> Brush.verticalGradient(listOf(Color(0xFFFFD700), Color(0xFFFFF59D)))
        else -> Brush.verticalGradient(listOf(Color.LightGray, Color.White))
    }
    Card(
        modifier = modifier
            .width(cardWidth)
            .height(cardHeight)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(modifier = Modifier.background(gradientBackground)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(80.dp)
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = player.imagePath),
                        contentDescription = player.displayName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = player.displayName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${player.puntos_totales} pts",
                    fontSize = 10.sp,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .wrapContentWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(player.estrellas) {
                        Text(text = "⭐", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CompactPlayerCard(
    player: PlayerOption,
    modifier: Modifier = Modifier,
    width: Dp = 100.dp,
    height: Dp = 122.dp,
    onClick: () -> Unit
) {
    val gradientBackground = when (player.estrellas) {
        1 -> Brush.verticalGradient(listOf(Color(0xFFB0B0B0), Color(0xFFE0E0E0)))
        2 -> Brush.verticalGradient(listOf(Color(0xFF4CAF50), Color(0xFFA5D6A7)))
        3 -> Brush.verticalGradient(listOf(Color(0xFF2196F3), Color(0xFF90CAF9)))
        4 -> Brush.verticalGradient(listOf(Color(0xFF9C27B0), Color(0xFFE1BEE7)))
        5 -> Brush.verticalGradient(listOf(Color(0xFFFFD700), Color(0xFFFFF59D)))
        else -> Brush.verticalGradient(listOf(Color.LightGray, Color.White))
    }
    Card(
        modifier = modifier
            .width(width)
            .height(height)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(2.dp, Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(modifier = Modifier.background(gradientBackground)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(70.dp)
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = player.imagePath),
                        contentDescription = player.displayName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = player.displayName,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${player.puntos_totales} pts",
                    fontSize = 9.sp,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .wrapContentWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(player.estrellas) {
                        Text(text = "⭐", fontSize = 10.sp)
                    }
                }
            }
        }
    }
}
