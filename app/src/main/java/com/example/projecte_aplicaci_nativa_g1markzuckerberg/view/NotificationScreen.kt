package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view.screens

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
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.Notifications
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.LoadingTransitionScreen
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.NotificationViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.NotificationsUiState
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: NotificationViewModel = viewModel()
) {
    BackHandler {}

    val token by produceState(initialValue = RetrofitClient.authRepository.getToken()) {
        while (value.isNullOrEmpty()) {
            delay(150)
            value = RetrofitClient.authRepository.getToken()
        }
    }

    LaunchedEffect(token) { viewModel.forceReloadIfTokenExists() }

    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
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
                text = "Notificaciones",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )
        }

        LoadingTransitionScreen(isLoading = uiState is NotificationsUiState.Loading) {
            when (uiState) {
                is NotificationsUiState.Error -> {
                    val msg = (uiState as NotificationsUiState.Error).msg
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(msg, color = MaterialTheme.colorScheme.error)
                    }
                }

                is NotificationsUiState.Success -> {
                    val data = (uiState as NotificationsUiState.Success).data
                    if (data.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No hay notificaciones", fontSize = 16.sp)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(data, key = { it.id }) { NotificationItem(it) }
                        }
                    }
                }

                else -> {}
            }
        }
    }
}

@Composable
private fun NotificationItem(notif: Notifications) {
    val dateFormatted = remember(notif.created_at) {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            outputFormat.format(inputFormat.parse(notif.created_at)!!)
        } catch (e: Exception) {
            notif.created_at
        }
    }

    val expulsadoColor = Color(0xFFD32F2F)
    val unidoColor = Color(0xFF388E3C)
    val varColor = Color(0xFF42A5F5)
    val dateColor = MaterialTheme.colorScheme.onSurfaceVariant
    val normalColor = MaterialTheme.colorScheme.onSurface

    val styledMessage = buildAnnotatedString {
        val raw = notif.mensaje
        val words = raw.split(" ")
        var insideExpelledName = false
        var isExpulsion = false
        var skipNextAsVar = false

        for (i in words.indices) {
            val word = words[i]
            val lower = word.lowercase().trimEnd('.', ',')

            val style = when {
                lower in listOf("expulsado", "expulsada") -> {
                    isExpulsion = true
                    SpanStyle(color = expulsadoColor, fontWeight = FontWeight.Bold)
                }

                isExpulsion && lower == "a" -> {
                    insideExpelledName = true
                    SpanStyle(color = normalColor)
                }

                insideExpelledName && lower == "de" -> {
                    insideExpelledName = false
                    SpanStyle(color = normalColor)
                }

                insideExpelledName -> {
                    SpanStyle(color = varColor)
                }

                lower in listOf("unido", "unida", "creado", "creada") -> {
                    SpanStyle(color = unidoColor, fontWeight = FontWeight.Bold)
                }

                lower == "liga" -> {
                    skipNextAsVar = true
                    SpanStyle(color = normalColor)
                }

                skipNextAsVar -> {
                    skipNextAsVar = false
                    SpanStyle(color = varColor)
                }

                else -> SpanStyle(color = normalColor)
            }

            withStyle(style) {
                append(word)
            }

            // Reponer el espacio original (excepto después del último)
            if (i < words.lastIndex) append(" ")
        }
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                tint = Color(0xFFFFAA00),
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 12.dp)
            )

            Column(Modifier.weight(1f)) {
                Text(text = styledMessage, style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(6.dp))
                Text(text = dateFormatted, style = MaterialTheme.typography.bodySmall, color = dateColor)
            }
        }
    }
}




