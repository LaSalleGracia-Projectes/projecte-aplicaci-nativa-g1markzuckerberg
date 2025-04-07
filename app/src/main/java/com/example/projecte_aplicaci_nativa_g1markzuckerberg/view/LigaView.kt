package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // HEADER: Botón atrás a la izquierda, botón compartir a la derecha y en el centro un Row con imagen y título
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.fantasydraft),
                            contentDescription = "Icono de liga",
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = data.liga.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onPrimary,
                            maxLines = 1
                        )
                    }
                    IconButton(
                        onClick = { ligaViewModel.toggleShowCodeDialog() },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Compartir código",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                // FILA para el dropdown de jornadas y botón "Crear Draft" con fondo secundario
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    JornadaDropdown(
                        createdJornada = data.liga.created_jornada,
                        currentJornada = currentJornada,
                        selected = selectedJornada,
                        onSelected = { jornada ->
                            ligaViewModel.setSelectedJornada(jornada)
                        }
                    )
                    Button(onClick = { /* TODO: Implementar Crear Draft */ }) {
                        Text("Crear Draft")
                    }
                }

                // RANKING DE USUARIOS (LazyColumn)
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(data.users) { index, user ->
                        // Alternar color de fondo: color actual y uno un poco más oscuro (surfaceVariant)
                        val rowColor = if (index % 2 == 0)
                            MaterialTheme.colorScheme.background
                        else
                            MaterialTheme.colorScheme.surfaceVariant

                        // Separador superior
                        Divider(color = MaterialTheme.colorScheme.outline)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(rowColor)
                                .clickable { /* TODO: Acción al pulsar la fila, por ejemplo ver perfil */ }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Posición en la liga
                            Text(
                                text = "${index + 1}",
                                modifier = Modifier.width(30.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            // Imagen por defecto entre el número y el nombre
                            Image(
                                painter = painterResource(id = R.drawable.fantasydraft),
                                contentDescription = "Imagen por defecto",
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            // Nombre de usuario
                            Text(
                                text = user.username,
                                modifier = Modifier.weight(1f)
                            )
                            // Puntos
                            val puntos = if (selectedJornada == 0) user.puntos_acumulados else user.puntos_jornada
                            Text(
                                text = "$puntos pts",
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                        // Separador inferior
                        Divider(color = MaterialTheme.colorScheme.outline)
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
                    onProfileClick = { /* TODO: Acción para perfil */ },
                    onHomeClick = { navController.navigate(Routes.HomeLoged.route) },
                    onNotificationsClick = { /* TODO: Acción para notificaciones */ },
                    onSettingsClick = { navController.navigate(Routes.Settings.route) }
                )
            }

            // Modal para mostrar el código de la liga
            if (showCodeDialog) {
                AlertDialog(
                    onDismissRequest = { ligaViewModel.toggleShowCodeDialog() },
                    confirmButton = {
                        TextButton(onClick = { ligaViewModel.toggleShowCodeDialog() }) {
                            Text("Cerrar")
                        }
                    },
                    title = { Text("Código de la liga") },
                    text = { Text(data.liga.code) }
                )
            }
        }
    }
}

@Composable
fun JornadaDropdown(
    createdJornada: Int,
    currentJornada: Int,
    selected: Int,
    onSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        // Botón que muestra la opción seleccionada con icono de dropdown
        Button(onClick = { expanded = true }) {
            Text(text = if (selected == 0) "Total" else "J$selected")
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Mostrar opciones"
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            // Opciones desde la jornada de creación hasta la actual
            for (j in createdJornada..currentJornada) {
                DropdownMenuItem(
                    text = { Text("J$j") },
                    onClick = {
                        onSelected(j)
                        expanded = false
                    }
                )
            }
            // Opción "Total"
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