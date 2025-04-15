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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.Player
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.PlayerOption
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.UIPlayer
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.nav.Routes
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.NavbarView
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.DraftViewModel

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun DraftScreen(
    viewModel: DraftViewModel,
    navController: NavController
) {
    // Obtenemos el draft desde el viewModel.
    val tempDraftResponse by viewModel.tempDraft.observeAsState()
    Log.d("DraftScreen", "TempDraftResponse en vista: $tempDraftResponse")

    val playerOptions: List<List<PlayerOption>> = tempDraftResponse?.playerOptions ?: emptyList()
    Log.d("DraftScreen", "Lista de grupos en DraftScreen: ${playerOptions.size}")

    val selectedPlayers = remember { mutableStateMapOf<String, PlayerOption?>() }


    // Directamente obtenemos la lista de opciones (ya convertida a List<List<PlayerOption>>)

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo: imagen de campo de fútbol.
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 130.dp, bottom = 72.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.futbol_pitch_background),
                contentDescription = "Campo de fútbol",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(9f / 16f)
                    .graphicsLayer { scaleX = 1.25f } // Aumenta el ancho en un 25%
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 120.dp, end = 16.dp)
                .align(Alignment.TopEnd)
        ) {
            FilledTonalButton(
                onClick = { viewModel.saveDraft(selectedPlayers) {
                    // Por ejemplo, navegamos a la pantalla de Home al guardar correctamente.
                    navController.navigate(Routes.HomeLoged.route)
                }
                },
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                modifier = Modifier.height(34.dp)
            ) {
                Text(
                    "Guardar",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp
                    )
                )
            }
        }

        // Contenido principal (por ejemplo, la lista de posiciones y sus candidatos)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 130.dp, bottom = 72.dp)
        ) {
            DraftLayout(
                formation = viewModel.selectedFormation.value,
                playerOptions = playerOptions,
                selectedPlayers = selectedPlayers
            )
        }

        // HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
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
                Spacer(modifier = Modifier.size(45.dp))
            }
        }

        // Navbar inferior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            NavbarView(
                navController = navController,
                onProfileClick = { /* Acción para perfil */ },
                onHomeClick = { navController.navigate(Routes.HomeLoged.route) },
                onNotificationsClick = { /* Acción para notificaciones */ },
                onSettingsClick = { navController.navigate(Routes.Settings.route) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


@SuppressLint("RememberReturnType")
@Composable
fun DraftLayout(formation: String, playerOptions: List<List<PlayerOption>>, selectedPlayers: MutableMap<String, PlayerOption?>) {
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
        modifier = Modifier.fillMaxSize().padding(vertical = 24.dp),
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
                            selectedPlayers[key] = chosenPlayer
                        }
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
    onPlayerSelected: (PlayerOption) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        PlayerSelectionDialog(
            players = candidates,
            onDismiss = { showDialog = false },
            onPlayerSelected = { selected ->
                onPlayerSelected(selected)
                showDialog = false
            }
        )
    }

    Box(
        modifier = Modifier
            .width(80.dp)
            .height(122.dp) // Igual al tamaño por defecto de CompactPlayerCard
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
                    width = 80.dp,
                    height = 122.dp,
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
                                        width = 100.dp,
                                        height = 122.dp,
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



