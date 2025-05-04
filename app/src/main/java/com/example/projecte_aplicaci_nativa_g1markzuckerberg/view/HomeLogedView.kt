package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import androidx.compose.ui.res.stringResource
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.LoadingTransitionScreen
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.LigaConPuntos
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.nav.Routes
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.CustomAlertDialog
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.CustomAlertDialogSingleButton
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.JoinLigaDialog
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.LeagueCodeDialog
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.LigaDialog
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
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    var openCreateLigaDialog by remember { mutableStateOf(false) }
    var openJoinLigaDialog by remember { mutableStateOf(false) }
    var selectedLeagueCode by remember { mutableStateOf<String?>(null) }

    // Estados para el CustomAlertDialog
    var showCustomAlert by remember { mutableStateOf(false) }
    var alertTitle by remember { mutableStateOf("") }
    var alertMessage by remember { mutableStateOf("") }
    var alertOnConfirm by remember { mutableStateOf({}) }
    var editingLiga by remember { mutableStateOf<LigaConPuntos?>(null) }

    val createLigaResult by homeLogedViewModel.createLigaResult.observeAsState()
    val joinLigaResult by homeLogedViewModel.joinLigaResult.observeAsState()
    val userLeagues by homeLogedViewModel.userLeagues.observeAsState(emptyList())
    val isLoading by homeLogedViewModel.isLoading.observeAsState(initial = true)
    val userEmail by homeLogedViewModel.userEmail.observeAsState(initial = "")
    val lastImageUpdateTs by homeLogedViewModel.lastImageUpdateTs.observeAsState(initial = 0L)
    val context = LocalContext.current
    val updateLigaSuccess by homeLogedViewModel.updateLigaSuccess.observeAsState()
    val errorEvent       by homeLogedViewModel.errorMessage.observeAsState()

    BackHandler {}

    // Al recibir el resultado de unirse a una liga
    val joinedTitle = stringResource(R.string.joined_league_title)
    val joinedMsg   = stringResource(R.string.joined_league_message)
    val errorTitle  = stringResource(R.string.error_title)

    LaunchedEffect(key1 = joinLigaResult) {
        joinLigaResult?.getContentIfNotHandled()?.let {
            alertTitle = joinedTitle
            alertMessage = joinedMsg
            alertOnConfirm = { showCustomAlert = false }
            showCustomAlert = true
            homeLogedViewModel.fetchUserLeagues()
        }
    }

// Para mostrar errores en un modal
    LaunchedEffect(key1 = errorEvent) {
        errorEvent?.getContentIfNotHandled()?.let { error: String ->
            alertTitle = errorTitle
            alertMessage = error
            alertOnConfirm = { showCustomAlert = false }
            showCustomAlert = true
        }
    }

    editingLiga?.let { liga ->
        LigaDialog(
            title = stringResource(R.string.edit_league_title),
            initialName = liga.name,
            onDismiss = { editingLiga = null },
            onConfirm = { newName, imgUri ->
                editingLiga = null
                // Actualizar nombre si cambió
                if (newName.isNotBlank() && newName != liga.name) {
                    homeLogedViewModel.updateLigaName(liga.id.toString(), newName)
                }
                // Subir imagen si se seleccionó
                imgUri?.let {
                    homeLogedViewModel.updateLigaWithImage(liga.id.toString(), it, context)
                }
            }
        )
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
                .padding(contentPadding)
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
                        color = Color.White,
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
                    text = stringResource(R.string.create_subtitle),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp))

            /** Contenido con Loading */
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
                                stringResource(R.string.no_leagues),
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    item {
                        SectionHeader(
                            title = stringResource(R.string.my_leagues),
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
                                        Text(
                                            stringResource(R.string.join_league),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 12.sp
                                            )
                                        )
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
                                        Text(
                                            stringResource(R.string.create_league),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 12.sp
                                            )
                                        )
                                    }
                                }
                            }
                        )
                    }

                    itemsIndexed(userLeagues) { _, liga ->
                        val editTitle = stringResource(R.string.edit_league_title)
                        val onlyCaptainMsg = stringResource(R.string.only_captain_can_edit)
                        val leaveTitle = stringResource(R.string.leave_league)
                        val confirmLeaveMsg = stringResource(R.string.confirm_leave_league)

                        LeagueRow(
                            name = liga.name,
                            puntos = liga.puntos_totales,
                            leagueId = liga.id.toString(),
                            totalUsers = liga.total_users,
                            onClick = { navController.navigate(Routes.LigaView.createRoute(liga.code)) },
                            onShareLiga = {
                                selectedLeagueCode = liga.code
                            },
                            onEditLiga = {
                                if (userEmail != liga.created_by) {
                                    alertTitle = editTitle
                                    alertMessage = onlyCaptainMsg
                                    alertOnConfirm = { showCustomAlert = false }
                                    showCustomAlert = true
                                } else {
                                    editingLiga = liga
                                }
                            },
                            onLeaveLiga = {
                                alertTitle = leaveTitle
                                alertMessage = confirmLeaveMsg
                                alertOnConfirm = {
                                    homeLogedViewModel.leaveLiga(liga.id.toString())
                                    showCustomAlert = false
                                }
                                showCustomAlert = true
                            },
                            lastImageUpdateTs = lastImageUpdateTs.toString(),
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }


                    item { Spacer(modifier = Modifier.height(12.dp)) }

                    homeLogedViewModel.jornadaData.value?.let { jornada ->
                        item {
                            SectionHeader(title = stringResource(R.string.matchday_prefix_param, jornada.jornada))
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

        /** Diálogos: al enviar, se cierra el diálogo correspondiente antes de llamar a la acción */
        if (openCreateLigaDialog) {
            LigaDialog(
                title     = stringResource(R.string.create_league_title),
                onDismiss = { openCreateLigaDialog = false },
                onConfirm = { name, imgUri ->
                    openCreateLigaDialog = false
                    homeLogedViewModel.createLiga(
                        name     = name.trim(),
                        imageUri = imgUri,
                        context  = context
                    )
                }
            )
        }
        if (openJoinLigaDialog) {
            JoinLigaDialog(
                onDismiss = { openJoinLigaDialog = false },
                onJoinLiga = { leagueCode ->
                    openJoinLigaDialog = false
                    homeLogedViewModel.joinLiga(leagueCode)
                },
            )
        }
        selectedLeagueCode?.let { code ->
            LeagueCodeDialog(
                leagueCode = code,
                onDismiss = { selectedLeagueCode = null }
            )
        }
    }

    // Mostrar nuestro modal de alerta personalizado
    if (showCustomAlert) {
        if (alertTitle == stringResource(R.string.leave_league)) {
            CustomAlertDialog(
                title = alertTitle,
                message = alertMessage,
                onDismiss = { showCustomAlert = false },
                onConfirm = alertOnConfirm
            )
        } else {
            // Para los demás mensajes se usa el de un solo botón
            CustomAlertDialogSingleButton(
                title = alertTitle,
                message = alertMessage,
                onAccept = {
                    showCustomAlert = false
                    alertOnConfirm()
                }
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
    leagueId: String,
    totalUsers: String,
    onClick: () -> Unit,
    onShareLiga: () -> Unit,
    onEditLiga: () -> Unit,
    onLeaveLiga: () -> Unit,
    lastImageUpdateTs: String
) {
    var expanded by remember { mutableStateOf(false) }
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
        Box {
            Row(modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)) {
                val ctx   = LocalContext.current
                val token = RetrofitClient.authRepository.getToken().orEmpty()
                val leagueImageRequest = ImageRequest.Builder(ctx)
                    .data("${RetrofitClient.BASE_URL}api/v1/liga/image/$leagueId?ts=$lastImageUpdateTs")
                    .addHeader("Authorization", "Bearer $token")
                    .placeholder(R.drawable.fantasydraft)
                    .error(R.drawable.fantasydraft)
                    .crossfade(true)
                    .build()

                Box(
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 12.dp)
                        .size(54.dp) // Tamaño total del contenedor circular
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model             = leagueImageRequest,
                        contentDescription= "Icono de la liga $name",
                        modifier          = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(2.dp),
                        contentScale      = ContentScale.Crop
                    )
                }


                Column(
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                        .fillMaxHeight()
                        .weight(1f),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
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

                        // Menú desplegable con opciones
                        Box {
                            IconButton(
                                onClick = { expanded = true },
                                modifier = Modifier
                                    .size(36.dp)
                                    .padding(0.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = stringResource(R.string.options_menu),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier
                                    .wrapContentSize()
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                    .clip(RoundedCornerShape(8.dp))
                                    .padding(0.dp)
                            ) {
                                // Compartir liga
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.share_league)) },
                                    onClick = {
                                        expanded = false
                                        onShareLiga()
                                    },
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.edit_league)) },
                                    onClick = {
                                        expanded = false
                                        onEditLiga()
                                    },
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                                )
                                DropdownMenuItem(
                                    modifier = Modifier.background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFFFF5252), Color(0xFFB71C1C))
                                        )
                                    ),
                                    text = { Text(stringResource(R.string.leave_league), color = Color.White) },
                                    onClick = {
                                        expanded = false
                                        onLeaveLiga()
                                    },
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("👥", style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = totalUsers,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "$ptsInt pts",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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