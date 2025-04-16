package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.UserImage

@Composable
fun PlayerOptionGroupCard(
    positionOptions: List<Any>, // Asumimos que los primeros 4 elementos son jugadores (puedes castear a Player)
    onOptionSelected: (selectedIndex: Int) -> Unit
) {
    // Por simplicidad, mostramos 4 opciones en fila.
    // Debes definir cómo luce cada opción.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Recorremos las primeras 4 posiciones
        for (i in 0 until 4) {
            // Puedes comprobar que el elemento sea de tipo Player y extraer su imagen y nombre.
            // Aquí se muestra un ejemplo simplificado con un Text.
            val player = positionOptions[i]
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clickable { onOptionSelected(i) }
            ) {
                // Aquí, reemplaza con la visualización del jugador. Por ejemplo, si tienes un Player:
                // UserImage(url = player.imageUrl, modifier = Modifier.fillMaxSize())
                Text(text = "Op $i", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
