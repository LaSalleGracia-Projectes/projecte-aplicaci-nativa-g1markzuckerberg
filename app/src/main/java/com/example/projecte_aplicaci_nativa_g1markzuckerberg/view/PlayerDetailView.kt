package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
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
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.grafanaPlayerUrl
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.PlayerDetailViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.PlayerDetailViewModelFactory

@Composable
fun PlayerDetailView(navController: NavController, playerId: String) {
    val repo = remember { RetrofitClient.playerRepository }
    val vm: PlayerDetailViewModel = viewModel(
        factory = remember { PlayerDetailViewModelFactory(playerId.toInt(), repo) }
    )
    val player = vm.player
    val isLoading = vm.isLoading
    val error = vm.errorMessage
    val onPrimary = MaterialTheme.colorScheme.onPrimary

    Column(Modifier.fillMaxSize()) {
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
                )
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = onPrimary
                    )
                }
                Text(
                    player?.displayName ?: "Jugador",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = onPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(48.dp))
            }
        }

        Box(Modifier.fillMaxSize()) {
            when {
                isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }
                error != null -> Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
                player != null -> Column(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Spacer(Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(player.teamImage)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Logo equipo",
                            modifier = Modifier.size(56.dp),
                            contentScale = ContentScale.Fit
                        )
                        Text(
                            text = player.teamName ?: "--",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    val avatarBg = when (player.estrellas ?: 0) {
                        1 -> Brush.verticalGradient(listOf(Color(0xFFB0B0B0), Color(0xFFE0E0E0)))
                        2 -> Brush.verticalGradient(listOf(Color(0xFF4CAF50), Color(0xFFA5D6A7)))
                        3 -> Brush.verticalGradient(listOf(Color(0xFF2196F3), Color(0xFF90CAF9)))
                        4 -> Brush.verticalGradient(listOf(Color(0xFF9C27B0), Color(0xFFE1BEE7)))
                        5 -> Brush.verticalGradient(listOf(Color(0xFFFFD700), Color(0xFFFFF59D)))
                        else -> Brush.verticalGradient(listOf(Color.LightGray, Color.White))
                    }

                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .align(Alignment.CenterHorizontally)
                            .background(avatarBg, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(player.imagePath)
                                .crossfade(true)
                                .transformations(CircleCropTransformation())
                                .build(),
                            contentDescription = "Jugador",
                            modifier = Modifier.size(116.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(player.estrellas ?: 0) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Text(
                        "${player.puntosTotales} pts",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            DetailLine("Posición", mapPosition(player.positionId))
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        var chartLoading by remember { mutableStateOf(true) }
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(grafanaPlayerUrl(player.id))
                                    .crossfade(true)
                                    .listener(
                                        onSuccess = { _, _ -> chartLoading = false },
                                        onError = { _, _ -> chartLoading = false }
                                    )
                                    .build(),
                                contentDescription = "Gráfico de evolución de puntos",
                                modifier = Modifier
                                    .height(260.dp)
                                    .padding(16.dp),
                                contentScale = ContentScale.FillHeight
                            )
                            if (chartLoading) {
                                Box(
                                    Modifier
                                        .matchParentSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
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
private fun DetailLine(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Medium, fontSize = 16.sp)
        Text(value, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    }
}

private fun mapPosition(positionId: Int): String = when (positionId) {
    27 -> "Delantero"
    26 -> "Mediocentro"
    25 -> "Defensa"
    24 -> "Portero"
    else -> "Posición $positionId"
}
