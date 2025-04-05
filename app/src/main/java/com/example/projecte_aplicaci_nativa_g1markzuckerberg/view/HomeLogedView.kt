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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    viewModel: HomeLogedViewModel
) {
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
                    .height(140.dp)
                    .background(BluePrimary),
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
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                        maxLines = 1
                    )
                    // Logo de la app (si usas URL, AsyncImage; si es un recurso local, puedes usar painterResource)
                    Image(
                        painter = painterResource(id = R.drawable.fantasydraft),
                        contentDescription = "Logo FantasyDraft",
                        modifier = Modifier.size(108.dp)
                    )
                }
            }

            // SUBTÍTULO
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEFEFEF)) // gris claro suave
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "Crea una liga con tus amigos!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
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
                                },
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text("Buscar Liga")
                            }
                            OutlinedButton(
                                onClick = {
                                    // TODO: Lógica para crear liga
                                }
                            ) {
                                Text("Crear Liga")
                            }
                        }
                    }
                }

                // Filas de ligas (ejemplo con 5 filas)
                items(5) {
                    LeagueRow(
                        onClick = {
                            // TODO: Navegar a la vista de la liga
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Sección 2: Próximos partidos (Fixtures)
                viewModel.jornadaData.value?.let { jornada ->
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
    }
}

// --------------------------
// COMPOSABLES REUTILIZABLES
// --------------------------

// Fila para "Mis ligas": (Icono Liga) Nombre Liga | XX Puntos | XX (icono personas)
@Composable
fun LeagueRow(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEFEFEF))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono Liga (Placeholder)
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.fantasydraft), // TODO: Reemplazar por icono de liga
                contentDescription = "Liga Icon",
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Nombre de la liga
            Text(
                text = "Nombre Liga",
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )

            VerticalDivider()

            // Puntos
            Text(
                text = "XX Puntos",
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            VerticalDivider()

            // Cantidad de usuarios
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "XX",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(4.dp))
                // Icono personas
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.fantasydraft), // TODO: Reemplazar por icono de usuarios
                    contentDescription = "Users Icon",
                    modifier = Modifier.size(20.dp)
                )
            }
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
