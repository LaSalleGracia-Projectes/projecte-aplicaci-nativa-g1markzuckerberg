package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.Player
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.PlayerOption
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.PositionOptions
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.UIPlayer
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.nav.Routes
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.NavbarView
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.DraftViewModel

@Composable
fun DraftScreen(
    viewModel: DraftViewModel,
    navController: NavController,
    innerPadding: PaddingValues,
) {
    val tempDraftResponse by viewModel.tempDraft.observeAsState()
    val playerOptions: List<List<PlayerOption>> = tempDraftResponse?.playerOptions ?: emptyList()
    val selectedPlayers = remember { mutableStateMapOf<String, PlayerOption?>() }

    // Alturas fijas para el header y el botón
    val headerHeight = 110.dp
    val buttonHeight = 34.dp
    // Función para convertir las selecciones actuales en una lista de PositionOptions
    fun buildPositionOptions(): List<List<Any?>> {
        // Define las claves en el mismo orden que aparecen en el layout
        val keys = listOf(
            "Delantero_0", "Delantero_1", "Delantero_2",
            "Mediocentro_0", "Mediocentro_1", "Mediocentro_2",
            "Defensa_0", "Defensa_1", "Defensa_2", "Defensa_3",
            "Portero_0"
        )
        val options = playerOptions.mapIndexed { index, group ->
            val key = keys.getOrElse(index) { "grupo_$index" }
            val chosenIndex = selectedPlayers[key]?.let { selected ->
                group.take(4).indexOfFirst { it.id == selected.id }
            }
            listOf(group[0], group[1], group[2], group[3], chosenIndex)
        }
        Log.d("DraftScreen", "buildPositionOptions: $options")
        return options
    }




    // Función para llamar al update en el servidor usando el ViewModel
    fun updateDraftOnServer() {
        Log.d("DraftScreen", "Current liga id: ${viewModel.currentLigaId}")
        viewModel.updateDraft(viewModel.currentLigaId, buildPositionOptions()) {
            Log.d("DraftScreen", "Draft actualizado en el servidor")
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        // HEADER: contiene el título, el botón volver y el botón guardar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                )
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
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
                    text = "DRAFT",
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

        // CONTENIDO: ocupa todo el espacio restante (entre header y navbar)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // La imagen de fondo se estira para ocupar toda la altura disponible
            Image(
                painter = painterResource(id = R.drawable.futbol_pitch_background),
                contentDescription = "Campo de fútbol",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { scaleX = 1.25f }
            )
            // Se ubica el layout de jugadores sobre la imagen
            DraftLayout(
                formation = viewModel.selectedFormation.value,
                playerOptions = playerOptions,
                selectedPlayers = selectedPlayers,
                onPlayerSelectedWithUpdate = { key, chosenPlayer ->
                    selectedPlayers[key] = chosenPlayer
                    updateDraftOnServer()
                },
                updateDraftOnServer = { updateDraftOnServer() } // 👈 Aquí la pasas
            )
        }
    }
}

@Composable
fun getPlayerCardDimensions(): Pair<Dp, Dp> {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    // Se resta el padding horizontal de 12.dp a cada lado (24.dp en total)
    val availableWidth = screenWidth - 24.dp
    // Se asume que el máximo de cartas en una fila es 4 y hay 3 espacios de 8.dp entre ellas
    val cardWidth = (availableWidth - (3 * 8.dp)) / 4
    // Manteniendo la relación de aspecto 80:122
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
    updateDraftOnServer: () -> Unit // 👈 Añadir esto
) {
    // Muestra el número total de grupos recibidos
    Log.d("DraftLayout", "Total de grupos de jugadores: ${playerOptions.size}")

    val rows: List<Pair<String, Int>> = when (formation) {
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
        rows.forEach { (positionName, count) ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(count) { index ->
                    val key = "${positionName}_$index"
                    // Verifica si groupIndex se encuentra dentro del tamaño de playerOptions
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
                            // Actualiza la selección en el mapa local y llama al callback de nivel superior
                            onPlayerSelectedWithUpdate(key, chosenPlayer)
                        },
                        updateDraftInServer = { updateDraftOnServer() } // Aquí se pasa la función de update
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
    updateDraftInServer: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        PlayerSelectionDialog(
            players = candidates,
            onDismiss = { showDialog = false },
            onPlayerSelected = { selected ->
                onPlayerSelected(selected)
                showDialog = false
                updateDraftInServer()
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
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(300)
            ) + fadeIn(tween(300)),
            exit = slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = tween(200)
            ) + fadeOut(tween(200))
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

                    // Se muestran los jugadores usando las dimensiones calculadas
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
        1 -> Brush.verticalGradient(listOf(Color(0xFFB0B0B0), Color(0xFFE0E0E0))) // Gris metálico
        2 -> Brush.verticalGradient(listOf(Color(0xFF4CAF50), Color(0xFFA5D6A7))) // Verde metálico
        3 -> Brush.verticalGradient(listOf(Color(0xFF2196F3), Color(0xFF90CAF9))) // Azul metálico
        4 -> Brush.verticalGradient(listOf(Color(0xFF9C27B0), Color(0xFFE1BEE7))) // Lila metálico
        5 -> Brush.verticalGradient(listOf(Color(0xFFFFD700), Color(0xFFFFF59D))) // Dorado metálico
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
                // Imagen con fondo blanco y margen visible
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f) // No ocupa todo el ancho
                        .height(70.dp)
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = player.imagePath),
                        contentDescription = player.displayName,
                        modifier = Modifier
                            .fillMaxSize(),
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



