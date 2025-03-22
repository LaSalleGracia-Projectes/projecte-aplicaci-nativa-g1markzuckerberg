package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
                .padding(bottom = 56.dp) // espacio para la navbar
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

            HorizontalDivider()

            // Aquí puedes poner más contenido si necesitas
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
