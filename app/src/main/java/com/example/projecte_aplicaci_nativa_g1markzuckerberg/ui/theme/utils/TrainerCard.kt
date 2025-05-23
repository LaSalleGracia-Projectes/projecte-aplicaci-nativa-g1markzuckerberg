package com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient

@Composable
fun TrainerCard(
    imageUrl: String,
    name: String,
    birthDate: String?,
    isCaptain: Boolean,
    puntosTotales: String,
    onExpelClick: () -> Unit = {},
    onCaptainClick: () -> Unit = {},
) {
    var menuExpanded by remember { mutableStateOf(false) }
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Imagen a la izquierda
                val ctx   = LocalContext.current
                val token = RetrofitClient.authRepository.getToken().orEmpty()
                val req   = ImageRequest.Builder(ctx)
                    .data(imageUrl)
                    .addHeader("Authorization", "Bearer $token")
                    .placeholder(R.drawable.fantasydraft)
                    .error(R.drawable.fantasydraft)
                    .crossfade(true)
                    .build()

                AsyncImage(
                    model             = req,
                    contentDescription= "Foto de $name",
                    modifier          = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentScale      = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                // Datos centrales: nombre, fecha y estado de capitán
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val displayDate = birthDate
                        ?.substringBefore("T")
                        ?: stringResource(R.string.no_birthdate)

                    Text(
                        text = displayDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    if (isCaptain) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.captain_label),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                // Botón de información a la derecha
                // ─── Icono + menú ─────────────────────────────────────────
                Box(
                    modifier = Modifier.wrapContentSize(Alignment.TopEnd)   // ancla = botón
                ) {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = stringResource(R.string.more_info),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    DropdownMenu(
                        expanded         = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        offset           = DpOffset(0.dp, 4.dp)              // 4 dp de holgura
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.expel), color = Color.White) },
                            modifier = Modifier.background(
                                Brush.horizontalGradient(listOf(Color(0xFFFF5252), Color(0xFFB71C1C)))
                            ),
                            onClick = {
                                menuExpanded = false
                                onExpelClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.make_captain)) },
                            onClick = {
                                menuExpanded = false
                                onCaptainClick()
                            }
                        )
                    }
                }

            }
            Spacer(modifier = Modifier.height(16.dp))
            // Fila para mostrar los PTS (sin decimales) alineados a la derecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                // Se elimina la parte decimal usando substringBefore('.')
                Text(
                    text = stringResource(R.string.points_prefix, puntosTotales.substringBefore('.')),
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
