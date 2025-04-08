package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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

@Composable
fun LigaView(
    navController: NavController,
    ligaCode: String,
    ligaViewModel: LigaViewModel
) {
    val ligaData by ligaViewModel.ligaData.observeAsState()
    val createdJornada = ligaData?.liga?.created_jornada ?: 30
    val selectedJornada by ligaViewModel.selectedJornada.observeAsState(createdJornada)
    val currentJornada by ligaViewModel.currentJornada.observeAsState(createdJornada)
    val showCodeDialog by ligaViewModel.showCodeDialog.observeAsState(false)

    LaunchedEffect(key1 = selectedJornada) {
        val jornadaParam = if (selectedJornada == 0) null else selectedJornada
        ligaViewModel.fetchLigaInfo(ligaCode, jornadaParam)
    }

    ligaData?.let { data ->
        val imageUrl = "${RetrofitClient.BASE_URL}api/v1/liga/image/${data.liga.id}"

        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // CABECERA con gradiente y elementos redondeados
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        )
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Volver",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(imageUrl)
                                    .placeholder(R.drawable.fantasydraft) // imagen por defecto
                                    .error(R.drawable.fantasydraft)       // imagen en caso de error
                                    .build()
                            )
                            Image(
                                painter = painter,
                                contentDescription = "Icono de la liga",
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .shadow(4.dp, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = data.liga.name.uppercase(),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.2.sp
                                ),
                                color = MaterialTheme.colorScheme.onPrimary,
                                maxLines = 1,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        IconButton(
                            onClick = { ligaViewModel.toggleShowCodeDialog() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Compartir código",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }

                // FILA con Dropdown para jornadas y botón "Crear Draft"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    JornadaDropdown(
                        createdJornada = data.liga.created_jornada,
                        currentJornada = currentJornada,
                        selected = selectedJornada,
                        onSelected = { jornada ->
                            ligaViewModel.setSelectedJornada(jornada)
                        }
                    )
                    Button(
                        onClick = { /* TODO: Implementar Crear Draft */ },
                        modifier = Modifier
                            .height(42.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = "Crear Draft",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // RANKING DE USUARIOS: listado estilizado en Cards
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    itemsIndexed(data.users) { index, user ->
                        val rowColor = if (index % 2 == 0)
                            MaterialTheme.colorScheme.background
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable { /* Acción al pulsar la fila, por ejemplo ver perfil */ }
                        ) {
                            Row(
                                modifier = Modifier
                                    .background(rowColor)
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.width(30.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Image(
                                    painter = painterResource(id = R.drawable.fantasydraft),
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

            // NAVBAR fija en la parte inferior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
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

            // Diálogo emergente para mostrar el código de la liga
            if (showCodeDialog) {
                AlertDialog(
                    onDismissRequest = { ligaViewModel.toggleShowCodeDialog() },
                    confirmButton = {
                        TextButton(onClick = { ligaViewModel.toggleShowCodeDialog() }) {
                            Text("Cerrar")
                        }
                    },
                    title = {
                        Text(
                            "Código de la liga",
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    text = { Text(data.liga.code, style = MaterialTheme.typography.bodyMedium) }
                )
            }
        }
    }
}

/** Dropdown personalizado para seleccionar la jornada */
@Composable
fun JornadaDropdown(
    createdJornada: Int,
    currentJornada: Int,
    selected: Int,
    onSelected: (Int) -> Unit
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
                style = MaterialTheme.typography.bodyMedium
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Mostrar opciones"
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
