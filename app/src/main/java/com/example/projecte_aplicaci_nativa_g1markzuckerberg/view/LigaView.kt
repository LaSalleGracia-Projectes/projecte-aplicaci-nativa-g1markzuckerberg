package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import LoadingTransitionScreen
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.nav.Routes
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.NavbarView
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.LigaViewModel
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.LeagueCodeDialog

@Composable
fun LigaView(
    navController: NavController,
    ligaCode: String,
    ligaViewModel: LigaViewModel
) {
    val ligaData by ligaViewModel.ligaData.observeAsState()
    val createdJornada = ligaData?.liga?.created_jornada ?: 0
    val selectedJornada by ligaViewModel.selectedJornada.observeAsState(createdJornada)
    val currentJornada by ligaViewModel.currentJornada.observeAsState(createdJornada)
    val showCodeDialog by ligaViewModel.showCodeDialog.observeAsState(false)
    val isLoading by ligaViewModel.isLoading.observeAsState(initial = true)

    LaunchedEffect(key1 = selectedJornada) {
        val jornadaParam = if (selectedJornada == 0) null else selectedJornada
        ligaViewModel.fetchLigaInfo(ligaCode, jornadaParam)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LoadingTransitionScreen(isLoading = ligaData == null) {
            val data = ligaData!!
            val imageUrl = "${RetrofitClient.BASE_URL}api/v1/liga/image/${data.liga.id}"
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 56.dp) // Padding inferior igual a la altura de la Navbar
            ) {
                // HEADER (igual que en HomeView)
                // HEADER modificado en LigaView
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
                        // Botón de volver
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
                        // Título centrado (con weight para ocupar el espacio y quedar centrado)
                        Text(
                            text = data.liga.name.uppercase(),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.3.sp
                            ),
                            color = MaterialTheme.colorScheme.onPrimary,
                            maxLines = 1,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        // Imagen de la liga
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(imageUrl)
                                    .placeholder(R.drawable.fantasydraft)
                                    .error(R.drawable.fantasydraft)
                                    .build()
                            ),
                            contentDescription = "Icono de la liga",
                            modifier = Modifier.size(45.dp)
                        )
                    }
                }

                // SECCIÓN DE BOTONES
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary,
                    tonalElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            JornadaDropdown(
                                createdJornada = data.liga.created_jornada,
                                currentJornada = currentJornada,
                                selected = selectedJornada,
                                onSelected = { jornada ->
                                    ligaViewModel.setSelectedJornada(jornada)
                                },
                                textColor = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = { /* TODO: Implementar Crear Draft */ },
                            modifier = Modifier.height(42.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text(
                                text = "Crear Draft",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            )
                        }
                    }
                }

                LoadingTransitionScreen(isLoading = isLoading) {
                    // RANKING DE USUARIOS (LazyColumn para scroll)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        itemsIndexed(data.users) { index, user ->
                            // Verifica si está entre los 3 primeros
                            val isPodio = index < 3
                            val rankingText = when (index) {
                                0 -> "🥇"
                                1 -> "🥈"
                                2 -> "🥉"
                                else -> "${index + 1}"
                            }
                            // Para los tres primeros, se define un Brush con efecto metálico
                            val backgroundBrush = if (isPodio) metallicBrushForRanking(index) else SolidColor(Color.White)

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                elevation = if (isPodio)
                                    CardDefaults.cardElevation(defaultElevation = 8.dp)
                                else CardDefaults.cardElevation(defaultElevation = 4.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clickable { /* Acción al pulsar la fila */ },
                                // Usamos fondo transparente y luego lo manejamos en la Box interna
                                colors = if (isPodio)
                                    CardDefaults.cardColors(containerColor = Color.Transparent)
                                else CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                if (isPodio) {
                                    // Para el podio, aplicamos el degradado y un borde para simular brillo
                                    Box(
                                        modifier = Modifier
                                            .background(brush = backgroundBrush, shape = RoundedCornerShape(12.dp))
                                            .border(width = 2.dp, brush = backgroundBrush, shape = RoundedCornerShape(12.dp))
                                            .padding(horizontal = 16.dp, vertical = 12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = rankingText,
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 20.sp
                                                ),
                                                modifier = Modifier.width(30.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            // Cargar la imagen del usuario usando el campo imageUrl
                                            Image(
                                                painter = rememberAsyncImagePainter(
                                                    model = "${RetrofitClient.BASE_URL}${user.imageUrl}",
                                                    placeholder = painterResource(id = R.drawable.fantasydraft),
                                                    error = painterResource(id = R.drawable.fantasydraft)
                                                ),
                                                contentDescription = "Imagen de usuario",
                                                modifier = Modifier
                                                    .size(48.dp)
                                                    .clip(CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = user.username,
                                                style = MaterialTheme.typography.bodyLarge,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            val puntos = if (selectedJornada == 0) user.puntos_acumulados else user.puntos_jornada
                                            Text(
                                                text = "$puntos pts",
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                        }
                                    }
                                } else {
                                    // Diseño de carta para el resto de usuarios
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = rankingText,
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier.width(30.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        // Cargar la imagen del usuario usando el campo imageUrl
                                        Image(
                                            painter = rememberAsyncImagePainter(
                                                model = "${RetrofitClient.BASE_URL}${user.imageUrl}",
                                                placeholder = painterResource(id = R.drawable.fantasydraft),
                                                error = painterResource(id = R.drawable.fantasydraft)
                                            ),
                                            contentDescription = "Imagen de usuario",
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = user.username,
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        val puntos = if (selectedJornada == 0) user.puntos_acumulados else user.puntos_jornada
                                        Text(
                                            text = "$puntos pts",
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                }
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

        if (showCodeDialog && ligaData != null) {
            LeagueCodeDialog(
                leagueCode = ligaData!!.liga.code,
                onDismiss = { ligaViewModel.toggleShowCodeDialog() }
            )
        }
        }
    }

@Composable
fun metallicBrushForRanking(index: Int): Brush {
    return when (index) {
        0 -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFD700), // Oro brillante
                Color(0xFFFFE135), // Punto intermedio más claro
                Color(0xFFFFC200)  // Tono dorado profundo
            )
        )
        1 -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFC0C0C0), // Plateado inicial
                Color(0xFFD3D3D3), // Punto intermedio
                Color(0xFFC0C0C0)  // Plateado
            )
        )
        2 -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFCD7F32), // Bronce intenso
                Color(0xFFE5B169), // Bronce más claro
                Color(0xFFCD7F32)  // Bronce intenso
            )
        )
        else -> SolidColor(Color.White)
    }
}





/** Dropdown personalizado para seleccionar la jornada */
@Composable
fun JornadaDropdown(
    createdJornada: Int,
    currentJornada: Int,
    selected: Int,
    onSelected: (Int) -> Unit,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Button(
            onClick = { expanded = true },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.height(42.dp)
        ) {
            Text(
                text = if (selected == 0) "Total" else "J$selected",
                style = MaterialTheme.typography.bodyMedium.copy(color = textColor)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Mostrar opciones",
                tint = textColor
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            for (j in createdJornada..currentJornada) {
                DropdownMenuItem(
                    text = { Text("J$j") },
                    onClick = {
                        onSelected(j)
                        expanded = false
                    }
                )
            }
            DropdownMenuItem(
                text = { Text("Total") },
                onClick = {
                    onSelected(0)
                    expanded = false
                }
            )
        }
    }
}