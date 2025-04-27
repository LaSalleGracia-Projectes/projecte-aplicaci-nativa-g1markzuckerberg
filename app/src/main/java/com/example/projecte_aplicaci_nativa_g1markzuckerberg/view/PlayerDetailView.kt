package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun PlayerDetailView(
    navController: NavHostController,
    playerId: String
) {

    Column(modifier = Modifier
        .padding(WindowInsets.systemBars.asPaddingValues())// evita solapamiento con status/navigation bars

    ) {
    //val viewModel: PlayerDetailViewModel = viewModel(factory = PlayerDetailViewModel.factory(playerId))
    //val player by viewModel.player.collectAsState()
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
    }
}


