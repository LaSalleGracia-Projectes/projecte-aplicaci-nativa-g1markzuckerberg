package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.nav.Routes
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.NavbarView
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.HomeLogedViewModel
import java.text.SimpleDateFormat
import java.util.*
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.CreateLigaDialog
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.JoinLigaDialog

private val BluePrimary @Composable get() = MaterialTheme.colorScheme.primary

// Función de ayuda para formatear el timestamp a fecha/hora
fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("Europe/Madrid")
    val date = Date(timestamp * 1000) // convertir segundos a milisegundos
    return sdf.format(date)
}
fun splitDateTime(timestamp: Long): Pair<String, String> {
    // Por ejemplo, "16/03/2025 15:30" se separa en ("16/03/2025", "15:30")
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("Europe/Madrid")
    val date = Date(timestamp * 1000)
    val fullDateTime = sdf.format(date)
    val parts = fullDateTime.split(" ")
    val datePart = parts.getOrNull(0) ?: ""
    val timePart = parts.getOrNull(1) ?: ""
    return datePart to timePart
}


@Composable
fun HomeLogedView(
    navController: NavController,
    homeLogedViewModel: HomeLogedViewModel, // Renombrado para evitar conflicto
) {
    var openCreateLigaDialog by remember { mutableStateOf(false) }
    // Observa los resultados y errores del ViewModel de crear liga
    val createLigaResult by homeLogedViewModel.createLigaResult.observeAsState()
    val errorMessage by homeLogedViewModel.errorMessage.observeAsState("")
    // Obtén el contexto para mostrar Toast
    val context = LocalContext.current
    var openJoinLigaDialog by remember { mutableStateOf(false) }
    val joinLigaResult by homeLogedViewModel.joinLigaResult.observeAsState()
    val userLeagues by homeLogedViewModel.userLeagues.observeAsState(emptyList())


    LaunchedEffect(key1 = joinLigaResult, key2 = errorMessage) {
        if (joinLigaResult != null) {
            openJoinLigaDialog = false
            Toast.makeText(context, "Te has unido correctamente a la liga", Toast.LENGTH_SHORT).show()
        } else if (errorMessage.isNotEmpty()) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    // Efecto para cerrar el diálogo y mostrar mensaje según el resultado
    LaunchedEffect(key1 = createLigaResult, key2 = errorMessage) {
        if (createLigaResult != null) {
            openCreateLigaDialog = false
            Toast.makeText(context, "Liga creada correctamente", Toast.LENGTH_SHORT).show()
        } else if (errorMessage.isNotEmpty()) {
            // Puedes decidir si deseas cerrar el modal en caso de error o no.
            // Aquí mostramos un Toast con el error:
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
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
        // Contenido general con espacio entre cabecera y navbar
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 56.dp) // Espacio para la navbar
        ) {
            // CABECERA
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "FantasyDraft",
                        fontSize = 28.sp,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.weight(1f),
                        maxLines = 1
                    )
                    // Logo de la app (si usas URL, AsyncImage; si es un recurso local, puedes usar painterResource)
                    Image(
                        painter = painterResource(id = R.drawable.fantasydraft),
                        contentDescription = "Logo FantasyDraft",
                        modifier = Modifier.size(80.dp)
                    )
                }
            }

            // SUBTÍTULO
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondary)
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "Crea una liga con tus amigos!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Línea divisoria
            HorizontalDivider()

            // ZONA SCROLLEABLE: TABLAS (Mis ligas + Próximos partidos)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp) // margen interior para "Mis ligas" y sus botones
            ) {
                if (userLeagues.isEmpty()) {
                    item {
                        Text("No estás en ninguna liga aún", modifier = Modifier.padding(16.dp))
                    }
                }

                // Sección 1: Tabla "Mis ligas"
                item {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mis ligas",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        // Botones: "Buscar liga" y "Crear liga"
                        Row {
                            OutlinedButton(
                                onClick = {
                                    // TODO: Lógica para buscar liga
                                    openJoinLigaDialog = true

                                },
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text("Unirse a Liga")
                            }
                            OutlinedButton(
                                onClick = {
                                    openCreateLigaDialog = true
                                    println("Crear Liga button clicked")
                                },
                            ) {
                                Text("Crear Liga")
                            }
                        }
                    }
                }

                // Filas de ligas (ejemplo con 5 filas)
                items(userLeagues) { liga ->
                    LeagueRow(
                        name = liga.name,
                        puntos = liga.puntos_totales,
                        onClick = {
                            // TODO: Navegar a detalles de liga
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Sección 2: Próximos partidos (Fixtures)
                homeLogedViewModel.jornadaData.value?.let { jornada ->
                    item {
                        Text(
                            text = "Jornada ${jornada.jornada}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(jornada.fixtures) { fixture ->
                        // Separa los nombres de los equipos
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
                    }
                }
            }
        }

        // NAVBAR FIJA ABAJO
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            NavbarView(
                navController = navController,
                onProfileClick = { /* TODO */ },
                onHomeClick = { navController.navigate(Routes.HomeLoged.route) },
                onNotificationsClick = { /* TODO */ },
                onSettingsClick = { navController.navigate(Routes.Settings.route) }
            )
        }
        // Bloque para mostrar el modal de crear liga cuando openCreateLigaDialog es true
        if (openCreateLigaDialog) {
            CreateLigaDialog(
                onDismiss = { openCreateLigaDialog = false },
                onCreateLiga = { leagueName ->
                    homeLogedViewModel.createLiga(leagueName)
                },
            )
        }
        // Al final del Box, agrega el modal:
        if (openJoinLigaDialog) {
            JoinLigaDialog(
                onDismiss = { openJoinLigaDialog = false },
                onJoinLiga = { leagueCode ->
                    homeLogedViewModel.joinLiga(leagueCode)
                },
            )
        }
    }
}

// --------------------------
// COMPOSABLES REUTILIZABLES
// --------------------------

// Fila para "Mis ligas": (Icono Liga) Nombre Liga | XX Puntos | XX (icono personas)
@Composable
fun LeagueRow(
    name: String,
    puntos: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.fantasydraft),
                contentDescription = "Liga Icon",
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )

            VerticalDivider()

            Text(
                text = "$puntos Pts",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            // Puedes añadir más info si quieres, como número de usuarios
        }
    }
}


// Fila para partidos: ahora se organiza en dos filas verticales
@Composable
fun MatchRow(
    team1: String,
    team2: String,
    timestamp: Long,
    localTeamImage: String,
    visitantTeamImage: String
) {
    // Separamos la fecha y la hora en dos strings
    val (datePart, timePart) = splitDateTime(timestamp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)         // Damos más altura
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Columna izquierda: nombre arriba, escudo abajo
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = team1,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            AsyncImage(
                model = localTeamImage,
                contentDescription = "Imagen equipo local",
                modifier = Modifier.size(28.dp)
            )
        }

        // Columna central: fecha arriba, hora abajo
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = datePart,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
            Text(
                text = timePart,
                fontSize = 14.sp,
                fontWeight = FontWeight.Light
            )
        }

        // Columna derecha: nombre arriba, escudo abajo
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = team2,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            AsyncImage(
                model = visitantTeamImage,
                contentDescription = "Imagen equipo visitante",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}


// Pequeña barra vertical entre columnas
@Composable
fun VerticalDivider(
    color: Color = Color.Gray,
    thickness: Dp = 1.dp
) {
    Box(
        modifier = Modifier
            .width(thickness)
            .fillMaxHeight()
            .background(color)
    )
}