// PlayersScreen.kt
package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun PlayersView(
    navController: NavController,
) {
            // ─── HEADER ───────────────────────────────
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
                    modifier            = Modifier.fillMaxSize(),
                    verticalAlignment   = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick  = { navController.popBackStack() },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector     = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint            = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text(
                        text      = "Jugadores",
                        style     = MaterialTheme.typography.titleLarge.copy(
                            fontSize    = 20.sp,
                            fontWeight  = FontWeight.ExtraBold,
                            letterSpacing = 0.3.sp
                        ),
                        color     = MaterialTheme.colorScheme.onPrimary,
                        maxLines  = 1,
                        modifier  = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // ─── AQUÍ VA EL CONTENIDO DE TU NUEVA VISTA ───
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Aquí pondrás la lista de jugadores…")
            }
        }