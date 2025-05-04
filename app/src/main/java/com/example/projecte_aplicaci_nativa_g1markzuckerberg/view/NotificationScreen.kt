package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.Notifications
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.LocalAppDarkTheme
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.LoadingTransitionScreen
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.NotificationViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.NotificationsUiState
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

private enum class Type { DRAFT_OPENING, CREATED, JOINED, EXPELLED, SELF_EXPELLED, UNKNOWN }
private data class MsgParts(val type: Type, val user: String?, val leagueOrDate: String?)

private fun parseSpanishMessage(raw: String): MsgParts {
    val lower = raw.lowercase(Locale.ROOT)
    return when {
        lower.startsWith("ya se abre el plazo") && raw.contains("hasta el") -> {
            val fecha = raw.substringAfter("hasta el").trim()
            MsgParts(Type.DRAFT_OPENING, null, fecha)
        }
        lower.startsWith("has creado la liga ") && lower.contains(" correctamente") -> {
            val liga = raw.substringAfter("Has creado la liga ").substringBefore(" correctamente").trim()
            MsgParts(Type.CREATED, null, liga)
        }
        lower.startsWith("te has unido a la liga ") && lower.contains(" con éxito") -> {
            val liga = raw.substringAfter("Te has unido a la liga ").substringBefore(" con éxito").trim()
            MsgParts(Type.JOINED, null, liga)
        }
        lower.startsWith("has expulsado a ") && lower.contains(" de la liga ") -> {
            val afterA = raw.substringAfter("Has expulsado a ")
            val user = afterA.substringBefore(" de la liga ").trim()
            val liga = afterA.substringAfter(" de la liga ").trim()
            MsgParts(Type.EXPELLED, user, liga)
        }
        lower.startsWith("has sido expulsado de la liga ") -> {
            val liga = raw.substringAfter("Has sido expulsado de la liga ").trim()
            MsgParts(Type.SELF_EXPELLED, null, liga)
        }
        else -> MsgParts(Type.UNKNOWN, null, null)
    }
}

@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: NotificationViewModel = viewModel()
) {
    BackHandler { /* no back */ }

    val token by produceState(initialValue = RetrofitClient.authRepository.getToken()) {
        while (value.isNullOrEmpty()) {
            delay(150)
            value = RetrofitClient.authRepository.getToken()
        }
    }
    LaunchedEffect(token) { viewModel.forceReloadIfTokenExists() }

    val uiState by viewModel.uiState.collectAsState()
    val darkTheme = LocalAppDarkTheme.current

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            Modifier
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
                text = stringResource(R.string.notifications_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                color = if (darkTheme) Color.White else MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )
        }

        LoadingTransitionScreen(isLoading = uiState is NotificationsUiState.Loading) {
            when (uiState) {
                is NotificationsUiState.Error -> {
                    val msg = (uiState as NotificationsUiState.Error).msg
                    Box(
                        Modifier.fillMaxSize().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(msg, color = MaterialTheme.colorScheme.error)
                    }
                }
                is NotificationsUiState.Success -> {
                    val list = (uiState as NotificationsUiState.Success).data
                    if (list.isEmpty()) {
                        Box(
                            Modifier.fillMaxSize().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(stringResource(R.string.no_notifications))
                        }
                    } else {
                        LazyColumn(
                            Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(list, key = { it.id }) { NotificationItem(it) }
                        }
                    }
                }
                else -> Unit
            }
        }
    }
}

@Composable
private fun NotificationItem(notif: Notifications) {
    val dateText = remember(notif.created_at) {
        runCatching {
            val inp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val out = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            out.format(inp.parse(notif.created_at)!!)
        }.getOrNull() ?: notif.created_at
    }

    val colorDraft = Color(0xFFFFAA00)
    val colorExpel = Color(0xFFD32F2F)
    val colorOk = Color(0xFF388E3C)
    val colorVar = Color(0xFF42A5F5)
    val colorDate = MaterialTheme.colorScheme.onSurfaceVariant

    val parts = remember(notif.mensaje) { parseSpanishMessage(notif.mensaje) }
    val locale = Locale.getDefault().language

    val styled = buildAnnotatedString {
        when (parts.type) {
            Type.DRAFT_OPENING -> {
                val texto = stringResource(R.string.notification_draft_opening, parts.leagueOrDate ?: "")
                append(texto)
                addStyle(SpanStyle(color = colorDraft, fontWeight = FontWeight.Bold), 0, length)
            }
            Type.CREATED -> when (locale) {
                "es" -> {
                    append("Has ")
                    withStyle(SpanStyle(color = colorOk, fontWeight = FontWeight.Bold)) { append("creado") }
                    append(" la liga ")
                    withStyle(SpanStyle(color = colorVar)) { append(parts.leagueOrDate ?: "") }
                    append(" correctamente")
                }
                "ca" -> {
                    append("Has ")
                    withStyle(SpanStyle(color = colorOk, fontWeight = FontWeight.Bold)) { append("creat") }
                    append(" la lliga ")
                    withStyle(SpanStyle(color = colorVar)) { append(parts.leagueOrDate ?: "") }
                    append(" correctament")
                }
                else -> {
                    append("You ")
                    withStyle(SpanStyle(color = colorOk, fontWeight = FontWeight.Bold)) { append("created") }
                    append(" the league ")
                    withStyle(SpanStyle(color = colorVar)) { append(parts.leagueOrDate ?: "") }
                    append(" successfully")
                }
            }
            Type.JOINED -> when (locale) {
                "es" -> {
                    append("Te has ")
                    withStyle(SpanStyle(color = colorOk, fontWeight = FontWeight.Bold)) { append("unido") }
                    append(" a la liga ")
                    withStyle(SpanStyle(color = colorVar)) { append(parts.leagueOrDate ?: "") }
                    append(" con éxito")
                }
                "ca" -> {
                    append("T'has ")
                    withStyle(SpanStyle(color = colorOk, fontWeight = FontWeight.Bold)) { append("unit") }
                    append(" a la lliga ")
                    withStyle(SpanStyle(color = colorVar)) { append(parts.leagueOrDate ?: "") }
                    append(" amb èxit")
                }
                else -> {
                    append("You ")
                    withStyle(SpanStyle(color = colorOk, fontWeight = FontWeight.Bold)) { append("joined") }
                    append(" the league ")
                    withStyle(SpanStyle(color = colorVar)) { append(parts.leagueOrDate ?: "") }
                    append(" successfully")
                }
            }
            Type.EXPELLED -> when (locale) {
                "es" -> {
                    append("Has ")
                    withStyle(SpanStyle(color = colorExpel, fontWeight = FontWeight.Bold)) { append("expulsado") }
                    append(" a ")
                    withStyle(SpanStyle(color = colorVar)) { append(parts.user ?: "") }
                    append(" de la liga ")
                    withStyle(SpanStyle(color = colorVar)) { append(parts.leagueOrDate ?: "") }
                }
                "ca" -> {
                    append("Has ")
                    withStyle(SpanStyle(color = colorExpel, fontWeight = FontWeight.Bold)) { append("expulsat") }
                    append(" a ")
                    withStyle(SpanStyle(color = colorVar)) { append(parts.user ?: "") }
                    append(" de la lliga ")
                    withStyle(SpanStyle(color = colorVar)) { append(parts.leagueOrDate ?: "") }
                }
                else -> {
                    append("You ")
                    withStyle(SpanStyle(color = colorExpel, fontWeight = FontWeight.Bold)) { append("expelled") }
                    append(" ")
                    withStyle(SpanStyle(color = colorVar)) { append(parts.user ?: "") }
                    append(" from the league ")
                    withStyle(SpanStyle(color = colorVar)) { append(parts.leagueOrDate ?: "") }
                }
            }
            Type.SELF_EXPELLED -> when (locale) {
                "es" -> {
                    append("Has sido ")
                    withStyle(SpanStyle(color = colorExpel, fontWeight = FontWeight.Bold)) { append("expulsado") }
                    append(" de la liga ")
                    withStyle(SpanStyle(color = colorVar)) { append(parts.leagueOrDate ?: "") }
                }
                "ca" -> {
                    append("Has estat ")
                    withStyle(SpanStyle(color = colorExpel, fontWeight = FontWeight.Bold)) { append("expulsat") }
                    append(" de la lliga ")
                    withStyle(SpanStyle(color = colorVar)) { append(parts.leagueOrDate ?: "") }
                }
                else -> {
                    append("You have been ")
                    withStyle(SpanStyle(color = colorExpel, fontWeight = FontWeight.Bold)) { append("expelled") }
                    append(" from the league ")
                    withStyle(SpanStyle(color = colorVar)) { append(parts.leagueOrDate ?: "") }
                }
            }
            Type.UNKNOWN -> append(notif.mensaje)
        }
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                Icons.Default.Notifications,
                contentDescription = null,
                tint = if (parts.type == Type.DRAFT_OPENING) colorDraft else Color(0xFFFFAA00),
                modifier = Modifier.size(32.dp).padding(end = 12.dp)
            )
            Column(Modifier.weight(1f)) {
                Text(text = styled, style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(6.dp))
                Text(text = dateText, style = MaterialTheme.typography.bodySmall, color = colorDate)
            }
        }
    }
}
