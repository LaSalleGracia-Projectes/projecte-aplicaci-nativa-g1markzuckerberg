package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.PlayerDetailViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.PlayerDetailViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
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
        // ─── HEADER ─────────────────────────────────────────
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
                Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = onPrimary
                    )
                }
                Text(
                    text = player?.displayName ?: "Jugador",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = onPrimary,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        // ─── BODY ───────────────────────────────────────────
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
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Foto + datos básicos
                    val imgUrl = player.imagePath
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imgUrl)
                            .crossfade(true)
                            .transformations(CircleCropTransformation())
                            .listener(onError = { _, r ->
                                Log.e("DETAIL_VIEW", "Error cargando avatar: ${r.throwable.message}")
                            })
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(100.dp)
                    )
                    Text("⭐ ${player.estrellas ?: 0}", fontSize = 14.sp)
                    Text(
                        "Puntos totales: ${player.puntosTotales ?: 0}",
                        fontWeight = FontWeight.SemiBold
                    )

                    // Detalles
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DetailLine("Posición", player.positionId.toString())
                            DetailLine("Equipo ID", player.teamId.toString())
                            DetailLine("Player ID", player.id.toString())
                        }
                    }

                    // ─── GRÁFICO ───────────────────────────────────
                    // Asegúrate de que tu BASE_URL termina sin slash
                    val chartUrl = "${RetrofitClient.BASE_URL}/api/v1/grafico/${player.id}?theme=light"
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column {
                            Text(
                                "Evolución de puntos",
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(16.dp)
                            )
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(chartUrl)
                                        .crossfade(true)
                                        .listener(onError = { _, r ->
                                            Log.e("DETAIL_VIEW", "Error cargando gráfico: ${r.throwable.message}")
                                        })
                                        .build(),
                                    contentDescription = "Gráfico puntos",
                                    modifier = Modifier
                                        .height(240.dp)
                                        .padding(16.dp),
                                    contentScale = ContentScale.FillWidth
                                )
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
        Text(label, fontWeight = FontWeight.Medium)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}