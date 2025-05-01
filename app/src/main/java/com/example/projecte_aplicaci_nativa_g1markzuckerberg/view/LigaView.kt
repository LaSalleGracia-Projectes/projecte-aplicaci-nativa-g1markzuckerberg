package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.LoadingTransitionScreen
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.nav.Routes
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.repository.AuthRepository
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.CustomAlertDialogSingleButton
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.LeagueCodeDialog
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.TokenManager
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.UserImage
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.DraftViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.LigaViewModel

@Composable
fun LigaView(
    navController: NavController,
    ligaCode: String,
    ligaViewModel: LigaViewModel,
    draftViewModel: DraftViewModel
) {
    val ligaData by ligaViewModel.ligaData.observeAsState()
    val createdJornada = ligaData?.liga?.created_jornada ?: 0
    val selectedJornada by ligaViewModel.selectedJornada.observeAsState(createdJornada)
    val currentJornada by ligaViewModel.currentJornada.observeAsState(createdJornada)
    val showCodeDialog by ligaViewModel.showCodeDialog.observeAsState(false)
    val isLoading by ligaViewModel.isLoading.observeAsState(initial = true)

    val context = LocalContext.current
    // Instancia tu TokenManager y AuthRepository
    val tokenManager = TokenManager(context)
    val authRepository = AuthRepository(service = RetrofitClient.authService, tokenManager = tokenManager)
    val currentUserId = authRepository.getCurrentUserId()
    val isFetching by draftViewModel.isFetchingDraft.observeAsState(false)
    val draftError    by draftViewModel.errorMessage.observeAsState("")
    var showDraftErr  by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = selectedJornada) {
        val jornadaParam = if (selectedJornada == 0) null else selectedJornada
        ligaViewModel.fetchLigaInfo(ligaCode, jornadaParam)
    }
    // Cuando el error de draft sea nuestro 500, lanzamos el diálogo
    LaunchedEffect(draftError) {
        if (draftError == "Ya tienes un draft creado en esta jornada.") {
            showDraftErr = true
        }
    }

    // Estado para mostrar el diálogo de creación de draft
    var showCreateDraftDialog by remember { mutableStateOf(false) }
    LoadingTransitionScreen(isLoading = isFetching) {
        // Pantalla de carga

    Box(modifier = Modifier.fillMaxSize()) {
        if (ligaData == null) {
            LoadingTransitionScreen(isLoading = true) {
                // Pantalla de carga
            }
        } else {
            val data = ligaData!!
            val imageUrl = "${RetrofitClient.BASE_URL}api/v1/liga/image/${data.liga.id}"
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 56.dp) // Espacio para la Navbar
            ) {
                // HEADER
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
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(imageUrl)
                                    .placeholder(R.drawable.fantasydraft)
                                    .error(R.drawable.fantasydraft)
                                    .build()
                            ),
                            contentDescription = "Icono de la liga",
                            modifier = Modifier
                                .size(45.dp)
                                .clip(CircleShape)
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
                        // Al pulsar el botón "Crear Draft"
                        Button(
                            onClick = {
                                draftViewModel.createOrFetchDraft(
                                    ligaId           = data.liga.id,
                                    onSuccess        = {
                                        navController.navigate(Routes.DraftScreen.createRoute())
                                    },
                                    onRequestFormation = {
                                        showCreateDraftDialog = true
                                    }
                                )
                            },
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
                // CONTENIDO: Ranking de usuarios
                LoadingTransitionScreen(isLoading = isLoading) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        itemsIndexed(data.users) { index, user ->
                            val isPodio = index < 3
                            val rankingText = when (index) {
                                0 -> "🥇"
                                1 -> "🥈"
                                2 -> "🥉"
                                else -> "${index + 1}"
                            }
                            val fullUserImageUrl = RetrofitClient.BASE_URL.trimEnd('/') + "/" + user.imageUrl.trimStart('/')
                            val backgroundBrush = if (isPodio) metallicBrushForRanking(index) else SolidColor(Color.White)

                            val cardModifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate(
                                        Routes.UserDraftView.createRoute(
                                            data.liga.id.toString(),
                                            user.usuario_id.toString(),
                                            user.username,
                                            fullUserImageUrl,
                                            createdJornada    = data.liga.created_jornada,
                                            currentJornada
                                        )
                                    )
                                }

                            val isCurrentUser = (currentUserId != null) && (user.usuario_id == currentUserId)

                            if (isCurrentUser) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(
                                            width = 2.dp,
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(
                                                    MaterialTheme.colorScheme.primary,
                                                    MaterialTheme.colorScheme.secondary
                                                )
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                ) {
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        elevation = if (isPodio)
                                            CardDefaults.cardElevation(defaultElevation = 8.dp)
                                        else CardDefaults.cardElevation(defaultElevation = 4.dp),
                                        modifier = cardModifier,
                                        colors = if (isPodio)
                                            CardDefaults.cardColors(containerColor = Color.Transparent)
                                        else CardDefaults.cardColors(containerColor = Color.White)
                                    ) {
                                        if (isPodio) {
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
                                                    UserImage(url = fullUserImageUrl)
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
                                                UserImage(url = fullUserImageUrl)
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
                            } else {
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = if (isPodio)
                                        CardDefaults.cardElevation(defaultElevation = 8.dp)
                                    else CardDefaults.cardElevation(defaultElevation = 4.dp),
                                    modifier = cardModifier.padding(vertical = 6.dp),
                                    colors = if (isPodio)
                                        CardDefaults.cardColors(containerColor = Color.Transparent)
                                    else CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    if (isPodio) {
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
                                                UserImage(url = fullUserImageUrl)
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
                                            UserImage(url = fullUserImageUrl)
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
            } // fin de Column

            if (showCreateDraftDialog) {
                CreateDraftDialog(
                    draftViewModel = draftViewModel,
                    onDismiss = { showCreateDraftDialog = false },
                    onConfirm = { formation ->
                        showCreateDraftDialog = false
                        // ahora usamos el método corregido:
                        draftViewModel.createAndFetchDraft(
                            formation = formation,
                            ligaId    = data.liga.id
                        ) {
                            navController.navigate(Routes.DraftScreen.createRoute())
                        }
                    }
                )
            }

        }
            if (showCodeDialog && ligaData != null) {
            LeagueCodeDialog(
                leagueCode = ligaData!!.liga.code,
                onDismiss = { ligaViewModel.toggleShowCodeDialog() }
            )
        }

        if (showDraftErr) {
            CustomAlertDialogSingleButton(
                title    = "Error",
                message  = draftError,
                onAccept = {
                    showDraftErr = false
                    draftViewModel.clearError()
                }
            )
        }
    }
}
}
@Composable
fun metallicBrushForRanking(index: Int): Brush {
    return when (index) {
        0 -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFD700),
                Color(0xFFFFE135),
                Color(0xFFFFC200)
            )
        )
        1 -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFC0C0C0),
                Color(0xFFD3D3D3),
                Color(0xFFC0C0C0)
            )
        )
        2 -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFCD7F32),
                Color(0xFFE5B169),
                Color(0xFFCD7F32)
            )
        )
        else -> SolidColor(Color.White)
    }
}

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

@Composable
fun CreateDraftDialog(
    draftViewModel: DraftViewModel,
    onDismiss: () -> Unit,
    onConfirm: (formation: String) -> Unit
) {
    val selectedFormation by draftViewModel.selectedFormation
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Crear Draft",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Vas a proceder a la creación del draft, por favor elige la alineación antes de continuar.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomFormationDropdown(
                        options = listOf("4-3-3", "4-4-2", "3-4-3"),
                        selectedOption = selectedFormation,
                        onOptionSelected = { draftViewModel.setSelectedFormation(it) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(
                                text = "Cancelar",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = { onConfirm(selectedFormation) }) {
                            Text(
                                text = "Aceptar",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomFormationDropdown(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var buttonSize by remember { mutableStateOf(Size.Zero) }
    val density = LocalDensity.current

    Box {
        Button(
            onClick = { expanded = true },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    buttonSize = coordinates.size.toSize()
                }
        ) {
            Text(
                text = selectedOption,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondary
                ),
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Seleccionar alineación",
                tint = MaterialTheme.colorScheme.onSecondary
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(with(density) { buttonSize.width.toDp() })
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
