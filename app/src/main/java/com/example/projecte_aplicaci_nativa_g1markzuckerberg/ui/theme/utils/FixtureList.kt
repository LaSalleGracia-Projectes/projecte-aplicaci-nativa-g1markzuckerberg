package com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.Fixture
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.FixturesViewModel

@Composable
fun FixtureList(viewModel: FixturesViewModel) {
    val fixtures = viewModel.fixturesState.value
    val error = viewModel.errorMessage.value

    if (error.isNotEmpty()) {
        Text(
            text = "Error: $error",
            color = Color.Red,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    } else if (fixtures.isEmpty()) {
        // Muestra la rueda de carga mientras se obtienen datos
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(fixtures) { fixture ->
                FixtureRow(fixture = fixture)
            }
        }
    }
}


@Composable
fun FixtureRow(fixture: Fixture) {
    // Supongamos que tienes extensiones definidas para obtener los equipos y formatear la fecha:
    // fixture.getTeams() retorna un Pair<String, String>
    // fixture.getFormattedDateTime() retorna una String con la fecha/hora formateada

    val (team1, team2) = fixture.getTeams()
    val dateTime = fixture.getFormattedDateTime()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cada Text ocupa el 33% del ancho y se centra
        Text(
            text = team1,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Text(
            text = dateTime,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Text(
            text = team2,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
    }
}
