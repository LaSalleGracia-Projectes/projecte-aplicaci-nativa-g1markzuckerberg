package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.LoadingTransitionScreen
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.CustomAlertDialog
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.CustomAlertDialogSingleButton
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
        val chosenIndex = if (group.size >= 5 && group[4] is Number) {
            (group[4] as Number).toInt()
        } else null
        val selectedPlayer = if (chosenIndex != null && chosenIndex in 0..3) {
            val item = group[chosenIndex]
            when (item) {
                is Map<*, *>    -> Gson().fromJson(Gson().toJson(item), PlayerOption::class.java)
                is PlayerOption -> item
                else            -> null
            }
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
    innerPadding: PaddingValues
) {
    /* ----------------  DATA & STATE  ---------------- */
    val tempDraftResponse by viewModel.tempDraft.observeAsState()
    val isSaving by viewModel.isSavingDraft.observeAsState(false)

    val parsedPlayerOptions by remember(tempDraftResponse) {
        derivedStateOf { tempDraftResponse?.playerOptions?.let(::parsePlayerOptions) ?: emptyList() }
    }

    val playerOptionsForDisplay by remember(parsedPlayerOptions) {
        mutableStateOf(
            parsedPlayerOptions.mapNotNull { g ->
                if (g.size >= 4) g.take(4).mapNotNull { itm ->
                    when (itm) {
                        is Map<*, *>    -> Gson().fromJson(Gson().toJson(itm), PlayerOption::class.java)
                        is PlayerOption -> itm
                        else            -> null
                    }
                } else null
            }
        )
    }

    val selectedPlayers = remember { mutableStateMapOf<String, PlayerOption?>() }

    var showSaveDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }
    var showErrorDialog  by remember { mutableStateOf(false) }

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
        return parsedPlayerOptions.mapIndexed { idx, grp ->
            val key = keys.getOrElse(idx) { "grupo_$idx" }
            val players = grp.take(4).mapNotNull { itm ->
                when (itm) {
                    is Map<*, *>    -> Gson().fromJson(Gson().toJson(itm), PlayerOption::class.java)
                    is PlayerOption -> itm
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
        viewModel.updateDraft(viewModel.currentLigaId, buildPositionOptions()) {}

    /* ----------------  CONSTANTES UI  ---------------- */
    val headerHeight  = 110.dp
    val buttonHeight  = 34.dp

    LoadingTransitionScreen(isLoading = isSaving) {

        /* =================  LAYOUT  ================= */
        Box(Modifier.fillMaxSize()) {

            /* ----------  FONDO  ---------- */
            Image(
                painter = painterResource(R.drawable.futbol_pitch_background),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = headerHeight)
                    .graphicsLayer { scaleX = 1.25f }
                    .clipToBounds()
                    .zIndex(-1f)
            )

            /* ----------  CONTENIDO (header + cartas)  ---------- */
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                        end   = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                        top   = innerPadding.calculateTopPadding()
                    )
            ) {

                /* ---------  HEADER  --------- */
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
                        .padding(horizontal = 20.dp)
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.CenterStart)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }

                    Text(
                        stringResource(R.string.draft_title),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color     = MaterialTheme.colorScheme.onSecondary,
                        textAlign = TextAlign.Center,
                        modifier  = Modifier.align(Alignment.Center)
                    )

                    FilledTonalButton(
                        onClick = {
                            val plantillaCompleta = selectedPlayers.values.none { it == null }
                            if (plantillaCompleta) showSaveDialog = true else showErrorDialog = true
                        },
                        shape   = RoundedCornerShape(50.dp),
                        colors  = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor   = Color.White
                        ),
                        modifier = Modifier
                            .height(buttonHeight)
                            .align(Alignment.CenterEnd)
                    ) {
                        Text(stringResource(R.string.save), style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp))
                    }
                }

                /* ---------  CAMPO & CARTAS  --------- */
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    DraftLayout(
                        formation       = viewModel.selectedFormation.value,
                        playerOptions   = playerOptionsForDisplay,
                        selectedPlayers = selectedPlayers,
                        onPlayerSelectedWithUpdate = { k, p ->
                            selectedPlayers[k] = p; updateDraftOnServer()
                        },
                        updateDraftOnServer = { updateDraftOnServer() }
                    )
                }
            }

            /* ----------  BOTÓN INFO FLOTANTE  ---------- */
            val bottomInset = innerPadding.calculateBottomPadding()
            val bottomPad   = (bottomInset - 28.dp).coerceAtLeast(0.dp)

            IconButton(
                onClick = { showInfoDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = bottomPad)
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = stringResource(R.string.info),
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        /* -------------  DIÁLOGO CONFIRMAR GUARDAR ------------- */
        if (showSaveDialog) {
            CustomAlertDialog(
                title   = stringResource(R.string.dialog_save_title),
                message = stringResource(R.string.dialog_save_msg),
                confirmButtonText = stringResource(R.string.save),
                cancelButtonText  = stringResource(R.string.cancel),
                onDismiss = { showSaveDialog = false },
                onConfirm = {
                    showSaveDialog = false
                    viewModel.saveDraft(selectedPlayers) { navController.popBackStack() }
                }
            )
        }

        if (showErrorDialog) {
            CustomAlertDialogSingleButton(
                title   = stringResource(R.string.dialog_incomplete_title),
                message = stringResource(R.string.dialog_incomplete_msg),
                confirmButtonText = stringResource(R.string.ok),
                onAccept = { showErrorDialog = false }
            )
        }

        /* -------------  DIÁLOGO INSTRUCCIONES ------------- */
        if (showInfoDialog) {
            GuideDialog(onDismiss = { showInfoDialog = false })
        }
    }
}

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
        formationRows.forEach { (positionKey, count) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(count) { index ->
                    val key = "${positionKey}_$index"
                    val candidates = playerOptions.getOrNull(groupIndex) ?: emptyList()
                    Log.d("DraftLayout", "Posición: $key, grupo: $groupIndex, candidatos: ${candidates.size}")
                    PositionCard(
                        positionKey = positionKey,
                        positionIndex = index,
                        selectedPlayer = selectedPlayers[key],
                        candidates = candidates,
                        onPlayerSelected = { chosenPlayer ->
                            onPlayerSelectedWithUpdate(key, chosenPlayer)
                        },
                        updateDraftOnServer = updateDraftOnServer
                    )
                    groupIndex++
                }
            }
        }
    }
}

@Composable
fun PositionCard(
    positionKey: String,
    positionIndex: Int,
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
                val baseName = positionKey
                val labelText = when (baseName) {
                    "Delantero"     -> stringResource(R.string.position_forward)
                    "Mediocentro"   -> stringResource(R.string.position_midfielder)
                    "Mediocampista" -> stringResource(R.string.position_midfielder)
                    "Defensa"       -> stringResource(R.string.position_defender)
                    "Portero"       -> stringResource(R.string.position_goalkeeper)
                    else            -> baseName
                } + " ${positionIndex + 1}"
                CompactPlaceholderCard(
                    positionName = labelText,
                    width        = cardWidth,
                    height       = cardHeight,
                    onClick      = { showDialog = true }
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
                        text = stringResource(R.string.select_player),
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
fun GuideDialog(onDismiss: () -> Unit) {
    val scrollState = rememberScrollState()
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.5f)
        ) {
            Column {
                // Header de gradiente
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
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
                        text = stringResource(R.string.guide_title),
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                // Contenido scrolleable
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(scrollState)
                ) {
                    // -------- Bono y eventos --------
                    Text(
                        text = stringResource(R.string.bonus_events),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = """
• Titular → +3 puntos
• Gol → +4 puntos
• Asistencia → +3 puntos
• Tarjeta amarilla → -1 punto
• Falta grave → -3 puntos
• Sustitución → el jugador que entra obtendrá la mitad de los puntos
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // -------- Estadísticas por posición --------
                    Text(
                        text = stringResource(R.string.stats_by_pos),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Portero
                    Text(
                        text = stringResource(R.string.pos_goalkeeper),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = "- Paradas realizadas\n- Balones recuperados" +
                                "\n- Goles encajados",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Defensa
                    Text(
                        text = stringResource(R.string.pos_defender),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = "- Entradas, intercepciones y despejes\n- Bloqueos de disparos\n- Precisión en pases" +
                                "\n- Goles encajados",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Mediocentro
                    Text(
                        text = stringResource(R.string.pos_midfielder),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = "- Posesión\n- Total de pases\n- % de acierto en pases\n- Intercepciones\n- Regates\n- Centros",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Delantero
                    Text(
                        text = stringResource(R.string.pos_forward),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = "- Disparos totales y a puerta\n- Ataques y ataques peligrosos\n- Regates\n- Centros",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Aviso final
                    Text(
                        text = stringResource(R.string.no_play_zero),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        ),
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón Cerrar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(onClick = { onDismiss() }) {
                            Text(
                                text = stringResource(R.string.close),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompactPlaceholderCard(
    positionName: String,
    modifier: Modifier = Modifier,
    width: Dp,
    height: Dp,
    onClick: () -> Unit
) {
    // mismo estilo de fondo que CompactPlayerCard
    val gradientBackground = Brush.verticalGradient(listOf(Color.LightGray, Color.White))

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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // slot de imagen igual que CompactPlayerCard
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(70.dp)
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.player_placeholder),
                        contentDescription = positionName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = positionName,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(R.string.zero_points),
                    fontSize = 9.sp,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center
                )
                // no mostramos estrellas
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
                    .padding(start = 6.dp, top = 6.dp, end = 6.dp, bottom = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
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
                Spacer(modifier = Modifier.weight(1f))
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

@Composable
fun getPlayerCardDimensions(): Pair<Dp, Dp> {
    val density        = LocalDensity.current
    val screenWidthDp  = LocalConfiguration.current.screenWidthDp.dp

    val availableWidth = screenWidthDp - 24.dp                // padding lateral (12×2)
    val cardWidth      = (availableWidth - (3 * 8.dp)) / 4

    // 122:80 + margen -> redondeamos siempre hacia ARRIBA
    val cardHeight = with(density) {
        kotlin.math.ceil(cardWidth.toPx() * 122f / 80f).toDp() + 6.dp // +4 dp
    }

    return cardWidth to cardHeight
}
