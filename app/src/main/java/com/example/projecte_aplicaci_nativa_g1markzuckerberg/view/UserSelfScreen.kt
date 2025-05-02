package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.LigaConPuntos
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.grafanaUserUrl
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/* ─────────── ENTRY ─────────── */
@Composable
fun UserSelfScreen(
    navController: NavHostController,
    vm: UserSelfViewModel = viewModel(factory = UserSelfViewModel.Factory())
) {
    val ui   by vm.state.collectAsState()
    val edit by vm.edit.collectAsState()

    if (edit is UserEditState.Loading)
        Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }

    when (ui) {
        UserSelfUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
        is UserSelfUiState.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) {
            Text((ui as UserSelfUiState.Error).message, color = MaterialTheme.colorScheme.error)
        }
        is UserSelfUiState.Ready -> ProfileContent(vm, ui as UserSelfUiState.Ready)
    }
}

/* ─────────── MAIN CONTENT ─────────── */
@Composable
private fun ProfileContent(vm: UserSelfViewModel, st: UserSelfUiState.Ready) {
    val ctx = LocalContext.current

    var dlgName   by remember { mutableStateOf(false) }
    var dlgBirth  by remember { mutableStateOf(false) }
    var dlgPass   by remember { mutableStateOf(false) }
    var popLiga   by remember { mutableStateOf(false) }
    var dlgAvatar by remember { mutableStateOf(false) }

    val token      = remember { RetrofitClient.authRepository.getToken().orEmpty() }
    val avatarUrl  = remember(st.avatarStamp) {
        "${RetrofitClient.BASE_URL}api/v1/user/get-image?userId=${st.user.id}&ts=${st.avatarStamp}"
    }

    Column(Modifier.fillMaxSize()) {
        Header()

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /* avatar */
            AsyncImage(
                model = ImageRequest.Builder(ctx)
                    .data(avatarUrl)
                    .addHeader("Authorization", "Bearer $token")
                    .memoryCachePolicy(CachePolicy.DISABLED)
                    .diskCachePolicy(CachePolicy.DISABLED)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable { dlgAvatar = true },
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(32.dp))

            EditRow("Nombre de usuario", st.user.username)                 { dlgName  = true }
            EditRow("Fecha nacimiento", st.user.birthDate?.prettyDate() ?: "--") { dlgBirth = true }
            EditRow("Cambiar contraseña", "********")                      { dlgPass  = true }

            Spacer(Modifier.height(28.dp))

            if (st.leagues.isNotEmpty()) {
                LeagueSelector(st) { popLiga = true }
                Spacer(Modifier.height(20.dp))
                PointsGraph(st)
            } else Text("Aún no perteneces a ninguna liga.")
        }
    }

    /* dialogs / popups */
    if (dlgName)  SimpleEditDialog("Nuevo nombre", st.user.username) {
        dlgName = false; it?.let(vm::updateUsername)
    }
    if (dlgBirth) SimpleEditDialog("Fecha (YYYY‑MM‑DD)", st.user.birthDate ?: "") {
        dlgBirth = false; it?.let(vm::updateBirth)
    }
    if (dlgPass)  PassDialog { o, n, c ->
        dlgPass = false; if (n.isNotBlank()) vm.updatePassword(o, n, c)
    }
    if (popLiga)  LeaguePopup(
        st.leagues,
        onSelect = { vm.selectLeague(it); popLiga = false },
        onDismiss = { popLiga = false }
    )
    if (dlgAvatar) AvatarDialog(
        currentUrl = avatarUrl,
        onDismiss  = { dlgAvatar = false },
        onSave     = { uri -> vm.uploadImage(ctx.uriToTmpFile(uri)) }
    )
}

/* ─────────── REUSABLE UI COMPONENTS ─────────── */
@Composable
private fun Header() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Mi perfil",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold
            ),
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EditRow(label: String, value: String, onClick: () -> Unit) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape  = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.labelMedium)
                Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            }
            Icon(Icons.Filled.Edit, null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}

/* ----- avatar dialog ----- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AvatarDialog(
    currentUrl: String,
    onDismiss: () -> Unit,
    onSave: (Uri) -> Unit
) {
    var selected by remember { mutableStateOf<Uri?>(null) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        selected = it
    }

    val ctx = LocalContext.current
    val token = remember { RetrofitClient.authRepository.getToken().orEmpty() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Foto de perfil")
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (selected != null) {
                    AsyncImage(
                        model = selected,
                        contentDescription = null,
                        modifier = Modifier.size(180.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(ctx)
                            .data(currentUrl)
                            .addHeader("Authorization", "Bearer $token")
                            .diskCachePolicy(CachePolicy.DISABLED)
                            .memoryCachePolicy(CachePolicy.DISABLED)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.size(180.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(Modifier.height(16.dp))
                OutlinedButton(onClick = { picker.launch("image/*") }) {
                    Text("Elegir de galería")
                }
            }
        },
        confirmButton = {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Row(
                    Modifier.padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    TextButton(
                        enabled = selected != null,
                        onClick = { selected?.let(onSave) }
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    )
}

/* ----- ligas & gráfico ----- */
@Composable
private fun LeagueSelector(st: UserSelfUiState.Ready, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape   = RoundedCornerShape(50),
        border  = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        AsyncImage(
            model = "${RetrofitClient.BASE_URL}api/v1/liga/image/${st.selectedLeague.id}",
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.width(6.dp))
        Text(st.selectedLeague.name, fontWeight = FontWeight.SemiBold)
        Icon(Icons.Default.KeyboardArrowDown, null)
    }
}

@Composable
private fun PointsGraph(st: UserSelfUiState.Ready) {
    var loading by remember(st.selectedLeague.id) { mutableStateOf(true) }
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Box(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            AsyncImage(
                model = grafanaUserUrl(
                    st.selectedLeague.id.toString(),
                    st.user.id.toString()
                ),
                contentDescription = null,
                modifier = Modifier
                    .height(260.dp)
                    .padding(16.dp),
                contentScale = ContentScale.FillHeight,
                onSuccess = { loading = false },
                onError   = { loading = false }
            )
            if (loading) Box(Modifier.matchParentSize(), Alignment.Center) { CircularProgressIndicator() }
        }
    }
}

/* ----- pop‑up selección liga ----- */
@Composable
private fun LeaguePopup(
    leagues: List<LigaConPuntos>,
    onSelect: (LigaConPuntos) -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.4f))
            .clickable { onDismiss() }
    ) {
        Card(
            Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.85f)
                .fillMaxHeight(0.6f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box {
                LazyColumn {
                    items(leagues, key = { it.id }) { liga ->
                        ListItem(
                            leadingContent = {
                                AsyncImage(
                                    model = "${RetrofitClient.BASE_URL}api/v1/liga/image/${liga.id}",
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp),
                                    contentScale = ContentScale.Fit
                                )
                            },
                            headlineContent = { Text(liga.name) },
                            modifier = Modifier.clickable { onSelect(liga) }
                        )
                    }
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) { Icon(Icons.Filled.Close, null) }
            }
        }
    }
}

/* ----- simple dialogs ----- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SimpleEditDialog(title: String, init: String, onRes: (String?) -> Unit) {
    var txt by remember { mutableStateOf(init) }
    AlertDialog(
        onDismissRequest = { onRes(null) },
        title = { Text(title) },
        text  = { OutlinedTextField(txt, { txt = it }, singleLine = true) },
        confirmButton = { TextButton(onClick = { onRes(txt.trim()) }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = { onRes(null) }) { Text("Cancelar") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PassDialog(onSave: (String, String, String) -> Unit) {
    var o by remember { mutableStateOf("") }
    var n by remember { mutableStateOf("") }
    var c by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { onSave("", "", "") },
        title = { Text("Cambiar contraseña") },
        text = {
            Column {
                OutlinedTextField(o, { o = it }, label = { Text("Actual") }, singleLine = true)
                OutlinedTextField(n, { n = it }, label = { Text("Nueva")  }, singleLine = true)
                OutlinedTextField(c, { c = it }, label = { Text("Confirmar") }, singleLine = true)
            }
        },
        confirmButton = { TextButton(onClick = { onSave(o, n, c) }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = { onSave("", "", "") }) { Text("Cancelar") } }
    )
}

/* ----- utils ----- */
private fun String.prettyDate(): String = try {
    val inFmt  = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outFmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    outFmt.format(inFmt.parse(take(10))!!)
} catch (_: Exception) { this }

private fun android.content.Context.uriToTmpFile(uri: Uri): File {
    contentResolver.openInputStream(uri)!!.use { input ->
        val file = File(cacheDir, "tmp_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { output -> input.copyTo(output) }
        return file
    }
}
