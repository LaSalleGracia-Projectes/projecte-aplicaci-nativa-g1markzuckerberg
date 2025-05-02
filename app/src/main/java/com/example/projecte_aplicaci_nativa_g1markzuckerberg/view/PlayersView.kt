package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.PlayerModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.nav.Routes
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.LoadingTransitionScreen
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayersView(navController: NavController) {
    val vm: PlayersViewModel = viewModel(factory = remember { PlayersViewModelFactory(RetrofitClient.playerRepository) })
    val teamVm: TeamViewModel  = viewModel(factory = remember { TeamViewModelFactory(RetrofitClient.teamRepository) })

    val state      = vm.uiState
    val listState  = rememberLazyListState()
    val onPrimary  = MaterialTheme.colorScheme.onPrimary

    var showTeamPopup by remember { mutableStateOf(false) }
    var targetScroll by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    LaunchedEffect(state.pointsOrder, targetScroll) {
        targetScroll?.let { (i, off) ->
            listState.scrollToItem(i, off)
            targetScroll = null
        }
    }

    Box(Modifier.fillMaxSize()) {
        /* ─────────── HEADER + FILTROS + LISTA ─────────── */
        Column(Modifier.fillMaxSize()) {
            /* header */
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Jugadores",
                    style  = MaterialTheme.typography.titleLarge.copy(fontSize = 26.sp, fontWeight = FontWeight.ExtraBold),
                    color  = onPrimary,
                    textAlign = TextAlign.Center
                )
            }

            /* filtros */
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = { showTeamPopup = true }) {
                    Text(state.selectedTeam ?: "Equipo", fontSize = 14.sp)
                    Icon(Icons.Filled.KeyboardArrowDown, null)
                }

                OutlinedTextField(
                    value = state.searchText,
                    onValueChange = vm::onSearch,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Buscar…") },
                    singleLine = true
                )

                IconButton(onClick = {
                    val i = listState.firstVisibleItemIndex
                    val off = listState.firstVisibleItemScrollOffset
                    vm.toggleOrder(); targetScroll = i to off
                }) {
                    Icon(
                        if (state.pointsOrder == "up") Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Ordenar puntos"
                    )
                }
            }

            /* listado */
            LoadingTransitionScreen(isLoading = state.isLoading) {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.filtered, key = { it.id }) { player ->
                        PlayerCard(player) {
                            navController.navigate(Routes.PlayerDetail.createRoute(player.id.toString()))
                        }
                    }
                }
            }
        }

        /* ─────────── POP‑UP EQUIPOS ─────────── */
        if (showTeamPopup) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { showTeamPopup = false }
            ) {
                Card(
                    Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(0.85f)
                        .fillMaxHeight(0.6f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    when {
                        teamVm.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }

                        teamVm.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(teamVm.error!!, color = MaterialTheme.colorScheme.error)
                        }

                        else -> LazyColumn {
                            item {
                                Text(
                                    "Seleccionar equipo",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                            item {
                                ListItem(
                                    leadingContent  = { Icon(Icons.Outlined.Star, null, tint = Color.Transparent) },
                                    headlineContent = { Text("Todos") },
                                    modifier = Modifier.clickable {
                                        vm.onTeamSelected(null); showTeamPopup = false
                                    }
                                )
                            }
                            items(teamVm.teams, key = { it.id }) { team ->
                                ListItem(
                                    leadingContent = {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(team.imagePath)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = team.name,
                                            modifier = Modifier.size(32.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                    },
                                    headlineContent = { Text(team.name) },
                                    modifier = Modifier.clickable {
                                        vm.onTeamSelected(team.name)
                                        showTeamPopup = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        /* ─────────── BOTÓN FILTRO ESTRELLAS ─────────── */
        IconButton(
            onClick = vm::toggleStarFilter,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .size(64.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        ) {
            if (state.starFilter == 0) {
                Icon(Icons.Filled.Close, "Sin filtro", tint = Color.Red, modifier = Modifier.size(30.dp))
            } else {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    val top    = minOf(2, state.starFilter)
                    val bottom = state.starFilter - top
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        repeat(top) { Icon(Icons.Filled.Star, null, tint = Color.Yellow, modifier = Modifier.size(24.dp)) }
                    }
                    if (bottom > 0) {
                        Spacer(Modifier.height(2.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            repeat(bottom) { Icon(Icons.Filled.Star, null, tint = Color.Yellow, modifier = Modifier.size(16.dp)) }
                        }
                    }
                }
            }
        }
    }
}

/* ──────────── CARD JUGADOR ──────────── */
@Composable
private fun PlayerCard(p: PlayerModel, onClick: () -> Unit) {
    val gradient = when (p.estrellas ?: 0) {
        1 -> Brush.verticalGradient(listOf(Color(0xFFB0B0B0), Color(0xFFE0E0E0)))
        2 -> Brush.verticalGradient(listOf(Color(0xFF4CAF50), Color(0xFFA5D6A7)))
        3 -> Brush.verticalGradient(listOf(Color(0xFF2196F3), Color(0xFF90CAF9)))
        4 -> Brush.verticalGradient(listOf(Color(0xFF9C27B0), Color(0xFFE1BEE7)))
        5 -> Brush.verticalGradient(listOf(Color(0xFFFFD700), Color(0xFFFFF59D)))
        else -> Brush.verticalGradient(listOf(Color.LightGray, Color.White))
    }

    Card(
        onClick = onClick,
        shape   = RoundedCornerShape(16.dp),
        colors  = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(Modifier.background(gradient)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(p.imagePath)
                        .crossfade(true)
                        .transformations(CircleCropTransformation())
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(p.displayName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(p.teamName ?: "--", style = MaterialTheme.typography.bodySmall)
                }
                Text("${p.puntosTotales} pts", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
