package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import LoadingTransitionScreen
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.nav.Routes
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.CreateLigaDialog
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.JoinLigaDialog
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.NavbarView
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.HomeLogedViewModel
import java.text.SimpleDateFormat
import java.util.*

/** Funciones para formatear la fecha y hora */
fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("Europe/Madrid")
    return sdf.format(Date(timestamp * 1000))
}
fun splitDateTime(timestamp: Long): Pair<String, String> {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("Europe/Madrid")
    val parts = sdf.format(Date(timestamp * 1000)).split(" ")
    return Pair(parts.getOrNull(0) ?: "", parts.getOrNull(1) ?: "")
}

/** Vista principal para el HomeLoged */
@Composable
fun HomeLogedView(
    navController: NavController,
    homeLogedViewModel: HomeLogedViewModel,
) {
    var openCreateLigaDialog by remember { mutableStateOf(false) }
    var openJoinLigaDialog by remember { mutableStateOf(false) }
    val createLigaResult by homeLogedViewModel.createLigaResult.observeAsState()
    val errorMessage by homeLogedViewModel.errorMessage.observeAsState("")
    val joinLigaResult by homeLogedViewModel.joinLigaResult.observeAsState()
    val userLeagues by homeLogedViewModel.userLeagues.observeAsState(emptyList())
    // Observa el estado de carga
    val isLoading by homeLogedViewModel.isLoading.observeAsState(initial = true)
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    LaunchedEffect(key1 = joinLigaResult) {
        joinLigaResult?.getContentIfNotHandled()?.let {
            openJoinLigaDialog = false
            Toast.makeText(context, "Te has unido correctamente a la liga", Toast.LENGTH_SHORT).show()
            homeLogedViewModel.fetchUserLeagues()
        }
    }

    LaunchedEffect(key1 = createLigaResult) {
        createLigaResult?.getContentIfNotHandled()?.let {
            openCreateLigaDialog = false
            Toast.makeText(context, "Liga creada correctamente", Toast.LENGTH_SHORT).show()
            homeLogedViewModel.fetchUserLeagues()
        }
    }
    val errorEvent by homeLogedViewModel.errorMessage.observeAsState()

    LaunchedEffect(key1 = errorEvent) {
        errorEvent?.getContentIfNotHandled()?.let { error: String ->
            Toast.makeText(context, error as CharSequence, Toast.LENGTH_SHORT).show()
        }
    }



    LaunchedEffect(Unit) {
        homeLogedViewModel.fetchUserLeagues()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 56.dp)
        ) {
            /** CABECERA */
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
                    Text(
                        text = "FantasyDraft",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.fantasydraft),
                        contentDescription = "Logo FantasyDraft",
                        modifier = Modifier.size(70.dp)
                    )
                }
            }

            /** SUBTÍTULO */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
                    .padding(vertical = 12.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "¡Crea una liga con tus amigos!",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp))

            /** Contenido con Loading: si isLoading es true se muestra la animación, de lo contrario se muestra la lista */
            LoadingTransitionScreen(isLoading = isLoading) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    if (userLeagues.isEmpty()) {
                        item {
                            Text(
                                "No estás en ninguna liga aún",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    item {
                        SectionHeader(
                            title = "MIS LIGAS",
                            buttonContent = {
                                Row {
                                    FilledTonalButton(
                                        onClick = { openJoinLigaDialog = true },
                                        shape = RoundedCornerShape(50.dp),
                                        colors = ButtonDefaults.filledTonalButtonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = Color.White
                                        ),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text("UNIRSE", style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp))
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    FilledTonalButton(
                                        onClick = { openCreateLigaDialog = true },
                                        shape = RoundedCornerShape(50.dp),
                                        colors = ButtonDefaults.filledTonalButtonColors(
                                            containerColor = MaterialTheme.colorScheme.secondary,
                                            contentColor = Color.White
                                        ),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text("CREAR", style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp))
                                    }
                                }
                            }
                        )
                    }

                    itemsIndexed(userLeagues) { index, liga ->
                        LeagueRow(
                            name = liga.name,
                            puntos = liga.puntos_totales,
                            leagueCode = liga.code,
                            onClick = { navController.navigate(Routes.LigaView.createRoute(liga.code)) },
                            onOptionsClick = {
                                // Acción para menú de opciones.
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    item { Spacer(modifier = Modifier.height(12.dp)) }

                    homeLogedViewModel.jornadaData.value?.let { jornada ->
                        item {
                            SectionHeader(title = "PARTIDOS – JORNADA ${jornada.jornada}")
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        items(jornada.fixtures) { fixture ->
                            val teams = fixture.name.split(" vs ")
                            val team1 = teams.getOrNull(0) ?: "Equipo 1"
                            val team2 = teams.getOrNull(1) ?: "Equipo 2"
                            MatchRow(
                                team1 = team1,
                                team2 = team2,
                                timestamp = fixture.starting_at_timestamp,
                                localTeamImage = fixture.local_team_image ?: "",
                                visitantTeamImage = fixture.visitant_team_image ?: ""
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        /** NAVBAR */
        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            NavbarView(
                navController = navController,
                onProfileClick = { /* Acción perfil */ },
                onHomeClick = {},
                onNotificationsClick = { /* Acción notificaciones */ },
                onSettingsClick = { navController.navigate(Routes.Settings.route) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        /** Diálogos con estilo refinado */
        if (openCreateLigaDialog) {
            CreateLigaDialog(
                onDismiss = { openCreateLigaDialog = false },
                onCreateLiga = { leagueName -> homeLogedViewModel.createLiga(leagueName) },
            )
        }
        if (openJoinLigaDialog) {
            JoinLigaDialog(
                onDismiss = { openJoinLigaDialog = false },
                onJoinLiga = { leagueCode -> homeLogedViewModel.joinLiga(leagueCode) },
            )
        }
    }
}

/** Encabezado de sección con título y contenido opcional de botón */
@Composable
fun SectionHeader(
    title: String,
    buttonContent: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        buttonContent?.let { it() }
    }
}

@Composable
fun LeagueRow(
    name: String,
    puntos: String,
    leagueCode: String,
    onClick: () -> Unit,
    onOptionsClick: () -> Unit
) {
    // Variables placeholder: reemplaza estas con los datos reales de tu lógica
    val draftCompleted = false  // false -> no completado ("❌"), true -> completado ("✅")
    val userCount = 10          // Ejemplo: 10 usuarios en la liga

    // Convertir los puntos a entero para evitar decimales (si es posible)
    val ptsInt = puntos.toDoubleOrNull()?.toInt() ?: puntos

    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)) {
            // Contenedor para la imagen con márgenes laterales y verticales
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 4.dp)
                    .fillMaxHeight()
                    .width(80.dp)
            ) {
                AsyncImage(
                    model = "${RetrofitClient.BASE_URL}api/v1/liga/image/$leagueCode",
                    contentDescription = "Imagen Liga",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)),
                    placeholder = painterResource(id = R.drawable.fantasydraft),
                    error = painterResource(id = R.drawable.fantasydraft)
                )
            }
            // Contenido a la derecha, con padding para alinear el título y la fila inferior
            Column(
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 8.dp)
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Fila superior: Nombre de la liga y botón de opciones
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onOptionsClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Opciones",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                // Fila inferior: "Draft", usuarios y puntos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Campo Usuarios
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "👥",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "$userCount",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    // Campo Puntos, con fuente más grande
                    Text(
                        text = "$ptsInt pts",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}



/** Tarjeta para "Partidos" (Fixtures); se mantiene el estilo compacto */
@Composable
fun MatchRow(
    team1: String,
    team2: String,
    timestamp: Long,
    localTeamImage: String,
    visitantTeamImage: String
) {
    val (datePart, timePart) = splitDateTime(timestamp)
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = team1,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                AsyncImage(
                    model = localTeamImage,
                    contentDescription = "Escudo Equipo Local",
                    modifier = Modifier.size(32.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = datePart,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = timePart,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = team2,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                AsyncImage(
                    model = visitantTeamImage,
                    contentDescription = "Escudo Equipo Visitante",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}