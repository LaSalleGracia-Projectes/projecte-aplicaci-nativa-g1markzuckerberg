package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.NavbarView
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.HomeLogedViewModel

private val BluePrimary @Composable get() = MaterialTheme.colorScheme.primary

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

            // ***** ZONA SCROLLEABLE: TABLAS (Mis ligas + Próximos partidos) *****
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp) // margen interior para "Mis ligas" y sus botones
            ) {
                // =============================
                // Sección 1: Tabla "Mis ligas"
                // =============================
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

                // Filas de ligas (ejemplo con 5 filas)
                repeat(5) {
                    LeagueRow(
                        onClick = {
                            // TODO: Navegar a la vista de la liga
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // =============================
                // Sección 2: Próximos partidos
                // =============================
                Text(
                    text = "Jornada X",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Para ocupar todo el ancho, ponemos esta sección sin el padding horizontal extra.
                Column(modifier = Modifier.fillMaxWidth()) {
                    // 10 partidos
                    repeat(10) {
                        MatchRow()
                    }

                    // Botón "Ver más" centrado
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        OutlinedButton(onClick = {
                            // TODO: Mostrar más partidos
                        }) {
                            Text("Ver más")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(80.dp)) // margen al final para no tapar contenido con la navbar
            }
        }

        // NAVBAR FIJA ABAJO
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            NavbarView(
                onProfileClick = { /* TODO */ },
                onHomeClick = { /* TODO */ },
                onNotificationsClick = { /* TODO */ },
                onSettingsClick = { /* TODO */ }
            )
        }
    }
}

/* --------------------------
   COMPOSABLES REUTILIZABLES
   -------------------------- */

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
            Image(
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
                Image(
                    painter = painterResource(id = R.drawable.fantasydraft), // TODO: Reemplazar por icono de usuarios
                    contentDescription = "Users Icon",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// Fila para partidos: se adapta para ocupar todo el ancho, igual que LeagueRow
@Composable
fun MatchRow() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEFEFEF))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Grupo izquierdo: Equipo 1 (texto + imagen)
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Equipo 1",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(4.dp))
                Image(
                    painter = painterResource(id = R.drawable.fantasydraft), // TODO: Reemplazar por el ícono de Equipo 1
                    contentDescription = "Equipo 1 Icon",
                    modifier = Modifier.size(24.dp)
                )
            }

            // Grupo central: Fecha/Hora
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Fecha/Hora",
                    fontSize = 14.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }

            // Grupo derecho: Equipo 2 (imagen + texto)
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fantasydraft), // TODO: Reemplazar por el ícono de Equipo 2
                    contentDescription = "Equipo 2 Icon",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Equipo 2",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
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