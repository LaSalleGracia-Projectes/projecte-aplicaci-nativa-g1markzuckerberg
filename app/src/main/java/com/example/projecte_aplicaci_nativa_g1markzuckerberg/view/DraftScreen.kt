package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.Player
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
    // Obtenemos los grupos de jugadores ya parseados (del backend)
    val playerOptions = viewModel.getPlayerOptionsByPosition()

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo de campo de fútbol
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 110.dp, bottom = 72.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.futbol_pitch_background),
                contentDescription = "Campo de fútbol",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(9f / 16f)
                    .graphicsLayer {
                        scaleX = 1.25f // Aumenta el ancho en un 25%
                    }
            )
        }

        // Contenido de la pantalla (sobre el fondo)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 110.dp, bottom = 72.dp) // Espacio para header y navbar
        ) {
            // Llamamos a DraftLayout pasándole la formación y los grupos de jugadores
            DraftLayout(
                formation = viewModel.selectedFormation.value,
                playerOptions = playerOptions
            )
        }

        // HEADER (igual que antes)
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
fun DraftLayout(formation: String, playerOptions: List<List<Player>>) {
    // Mantenemos un mapa para almacenar las elecciones de cada posición.
    val selectedPlayers = remember { mutableStateMapOf<String, UIPlayer?>() }
    // Definimos las filas de la plantilla según la formación.
    val rows: List<Pair<String, Int>> = when (formation) {
        "4-3-3" -> listOf(
            "Delantero" to 3,
            "Mediocentro" to 3,
            "Defensa" to 4,
            "Portero" to 1
        )
        "4-4-2" -> listOf(
            "Delantero" to 2,
            "Mediocampista" to 4,
            "Defensa" to 4,
            "Portero" to 1
        )
        "3-4-3" -> listOf(
            "Delantero" to 3,
            "Mediocampista" to 4,
            "Defensa" to 3,
            "Portero" to 1
        )
        else -> emptyList()
    }
    // Se asume que el total de grupos en playerOptions es igual a la suma de posiciones en la formación.
    var groupIndex = 0
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 24.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        rows.forEach { (position, count) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(count) { index ->
                    // Construimos una key única para cada posición.
                    val key = "${position}_$index"
                    // Tomamos los candidatos del grupo correspondiente, o una lista vacía si no hay.
                    val candidates = if (groupIndex < playerOptions.size) playerOptions[groupIndex] else emptyList()
                    PositionCard(
                        positionName = "$position ${index + 1}",
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
fun PlaceholderCard(text: String) {
    Box(
        modifier = Modifier
            .width(80.dp)
            .height(100.dp)
            .background(color = Color.Gray, shape = RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 10.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PlayerCard(player: UIPlayer, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .width(80.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Usamos Coil para cargar la imagen desde la URL.
        Image(
            painter = rememberAsyncImagePainter(player.imageUrl),
            contentDescription = player.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
            contentScale = ContentScale.Crop
        )
        Text(
            text = player.name,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = Color.Black,
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}

@Composable
fun PositionCard(
    positionName: String,
    selectedPlayer: UIPlayer?,
    candidates: List<Player>,
    onPlayerSelected: (UIPlayer) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        PlayerSelectionDialog(
            players = candidates,
            onDismiss = { showDialog = false },
            onPlayerSelected = { selected ->
                onPlayerSelected(UIPlayer(name = selected.displayName, imageUrl = selected.imagePath))
                showDialog = false
            }
        )
    }

    val backgroundColor = if (selectedPlayer == null) Color.Gray else Color.White

    Box(
        modifier = Modifier
            .width(80.dp)
            .height(100.dp)
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .clickable { showDialog = true },
        contentAlignment = Alignment.Center
    ) {
        if (selectedPlayer == null) {
            // Mostrar un placeholder con el nombre de la posición.
            Text(
                text = positionName,
                color = Color.White,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        } else {
            // Mostrar la tarjeta del jugador seleccionado.
            PlayerCard(player = selectedPlayer)
        }
    }
}

@Composable
fun PlayerSelectionDialog(
    players: List<Player>,
    onDismiss: () -> Unit,
    onPlayerSelected: (Player) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .size(width = 300.dp, height = 200.dp)
                .background(Color.White, RoundedCornerShape(16.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                players.forEach { player ->
                    PlayerCard(
                        player = UIPlayer(name = player.displayName, imageUrl = player.imagePath),
                        modifier = Modifier.clickable { onPlayerSelected(player) }
                    )
                }
            }
        }
    }
}
